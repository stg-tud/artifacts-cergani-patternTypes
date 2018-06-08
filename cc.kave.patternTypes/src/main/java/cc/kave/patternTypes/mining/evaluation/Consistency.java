/**
 * Copyright 2016 Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.kave.patternTypes.mining.evaluation;

import java.util.Map;
import java.util.Set;

import cc.kave.patternTypes.io.EpisodeParser;
import cc.kave.patternTypes.mining.patterns.PatternFilter;
import cc.kave.patternTypes.model.Episode;
import cc.kave.patternTypes.model.EpisodeType;
import cc.kave.patternTypes.model.events.Fact;
import cc.recommenders.io.Logger;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class Consistency {

	private EpisodeParser parser;
	private PatternFilter filter;

	@Inject
	public Consistency(EpisodeParser episodeParser, PatternFilter patternFilter) {
		this.parser = episodeParser;
		this.filter = patternFilter;
	}

	public void calculate(int frequency, int threshFreq, double threshEnt)
			throws Exception {
		Map<Integer, Set<Episode>> episodes = parser.parser(frequency);

		Map<Integer, Set<Episode>> partials = filter.filter(
				EpisodeType.GENERAL, episodes, threshFreq, threshEnt);
		Map<Integer, Set<Episode>> sequentials = filter.filter(
				EpisodeType.SEQUENTIAL, episodes, threshFreq, threshEnt);
		Map<Integer, Set<Episode>> unordered = filter.filter(
				EpisodeType.PARALLEL, episodes, threshFreq, threshEnt);

		Map<Set<Fact>, Integer> sets = getFactSets(unordered);

		Logger.log("consistency(partial-order) = %.4f",
				consistency(partials, sets));
		Logger.log("consistency(sequential-order) = %.4f",
				consistency(sequentials, sets));
	}

	private double consistency(Map<Integer, Set<Episode>> orders,
			Map<Set<Fact>, Integer> sets) {
		double consistency = 0.0;
		int numPatterns = 0;

		for (Map.Entry<Integer, Set<Episode>> entry : orders.entrySet()) {
			for (Episode pattern : entry.getValue()) {
				int orderFreq = pattern.getFrequency();
				int setFreq = sets.get(pattern.getEvents());
				
				consistency += (orderFreq * 1.0) / (setFreq * 1.0);
				numPatterns++;
			}
		}
		return (consistency / (numPatterns * 1.0));
	}

	private Map<Set<Fact>, Integer> getFactSets(
			Map<Integer, Set<Episode>> patterns) {
		Map<Set<Fact>, Integer> results = Maps.newLinkedHashMap();

		for (Map.Entry<Integer, Set<Episode>> entry : patterns.entrySet()) {
			for (Episode p : entry.getValue()) {
				results.put(p.getEvents(), p.getFrequency());
			}
		}
		return results;
	}
}
