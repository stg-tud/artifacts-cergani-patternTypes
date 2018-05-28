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

package cc.kave.patternTypes.evaluations.expressiveness;

import java.util.Map;
import java.util.Set;

import cc.kave.patternTypes.io.EpisodeParser;
import cc.kave.patternTypes.mining.patterns.PatternFilter;
import cc.kave.patternTypes.model.Episode;
import cc.kave.patternTypes.model.EpisodeType;
import cc.kave.patternTypes.model.events.Fact;
import cc.recommenders.io.Logger;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class PatternEvents {

	private EpisodeParser parser;
	private PatternFilter filter;

	@Inject
	public PatternEvents(EpisodeParser episodeParser,
			PatternFilter patternFilter) {
		this.parser = episodeParser;
		this.filter = patternFilter;
	}

	public void acrossTypes(int frequency, int freqThresh, double entThresh) throws Exception {
		Map<Integer, Set<Episode>> episodes = parser.parser(frequency);

		Map<Integer, Set<Episode>> sequentials = filter.filter(
				EpisodeType.SEQUENTIAL, episodes, freqThresh, entThresh);
		Map<Integer, Set<Episode>> partials = filter.filter(
				EpisodeType.GENERAL, episodes, freqThresh, entThresh);
		Map<Integer, Set<Episode>> unordered = filter.filter(
				EpisodeType.PARALLEL, episodes, freqThresh, entThresh);
		
		Set<Fact> seqFacts = getFacts(sequentials);
		Set<Fact> pocFacts = getFacts(partials);
		Set<Fact> nocFacts = getFacts(unordered);
		
		Logger.log("Number of events in sequential-order patterns: %d", seqFacts.size());
		Logger.log("Number of events in partial-order patterns: %d", pocFacts.size());
		Logger.log("Number of events in no-order patterns: %d", nocFacts.size());
		
		Logger.log("Partials cover sequential events? %s", pocFacts.containsAll(seqFacts));
		Logger.log("No-orders cover all partial events? %s", nocFacts.containsAll(pocFacts));
	}

	private Set<Fact> getFacts(Map<Integer, Set<Episode>> patterns) {
		Set<Fact> facts = Sets.newHashSet();
		for (Map.Entry<Integer, Set<Episode>> entry : patterns.entrySet()) {
			for (Episode episode : entry.getValue()) {
				for (Fact fact : episode.getEvents()) {
					facts.add(fact);
				}
			}
			break;
		}
		return facts;
	}
}
