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
import cc.recommenders.datastructures.Tuple;
import cc.recommenders.io.Logger;

import com.google.inject.Inject;

public class Expressiveness {

	private EpisodeParser parser;
	private PatternFilter filter;

	@Inject
	public Expressiveness(EpisodeParser episodeParser,
			PatternFilter patternFilter) {
		this.parser = episodeParser;
		this.filter = patternFilter;
	}

	public void calculate(int frequency, int threshFreq, double threshEnt)
			throws Exception {
		Map<Integer, Set<Episode>> episodes = parser.parser(frequency);

		Map<Integer, Set<Episode>> sequentials = filter.filter(
				EpisodeType.SEQUENTIAL, episodes, threshFreq, threshEnt);
		Map<Integer, Set<Episode>> partials = filter.filter(
				EpisodeType.GENERAL, episodes, threshFreq, threshEnt);
		Map<Integer, Set<Episode>> unordered = filter.filter(
				EpisodeType.PARALLEL, episodes, threshFreq, threshEnt);

		exactMetric(sequentials, partials, unordered);

		subsumedMetric(sequentials, partials, unordered);

		newMetric(sequentials, partials, unordered);
	}

	private void newMetric(Map<Integer, Set<Episode>> sequentials,
			Map<Integer, Set<Episode>> partials,
			Map<Integer, Set<Episode>> unordered) {

		int counter = newPatterns(partials, sequentials);
		Logger.log("new(POC, SOC) = %d", counter);
		
		counter = newPatterns(unordered, partials);
		Logger.log("new(NOC, POC) = %d", counter);
		
		counter = newPatterns(unordered, sequentials);
		Logger.log("new(NOC, SOC) = %d", counter);
		
		Logger.log("");
	}

	private int newPatterns(Map<Integer, Set<Episode>> patterns1,
			Map<Integer, Set<Episode>> patterns2) {

		int counter = 0;

		for (Map.Entry<Integer, Set<Episode>> entry : patterns1.entrySet()) {
			for (Episode partial : entry.getValue()) {
				boolean isNew = true;
				for (Episode sequential : patterns2.get(entry.getKey())) {
					if (partial.getEvents().equals(sequential.getEvents())) {
						isNew = false;
						break;
					}
				}
				if (isNew) {
					counter++;
				}
			}
		}
		return counter;
	}

	private void subsumedMetric(Map<Integer, Set<Episode>> sequentials,
			Map<Integer, Set<Episode>> partials,
			Map<Integer, Set<Episode>> unordered) {

		Tuple<Integer, Integer> tuple = subsume(partials, sequentials);
		int exact = equals(partials, sequentials);
		Logger.log("subsume(POC, SOC) = (%d;%d)",
				(tuple.getFirst() - exact), (tuple.getSecond() - exact));

		tuple = simplified(unordered, partials);
		exact = equals(unordered, partials);
		Logger.log("subsume(NOC, POC) = (%d;%d)",
				(tuple.getFirst() - exact), (tuple.getSecond() - exact));

		tuple = simplified(unordered, sequentials);
		exact = equals(unordered, sequentials);
		Logger.log("subsume(NOC, SOC) = (%d;%d)",
				(tuple.getFirst() - exact), (tuple.getSecond() - exact));

		Logger.log("");
	}

	private Tuple<Integer, Integer> simplified(
			Map<Integer, Set<Episode>> unordered,
			Map<Integer, Set<Episode>> ordered) {

		int counter1 = 0;
		int counter2 = 0;
		for (Map.Entry<Integer, Set<Episode>> entry : unordered.entrySet()) {
			for (Episode unorder : entry.getValue()) {
				boolean contains = false;
				for (Episode partial : ordered.get(entry.getKey())) {
					if (unorder.getEvents().equals(partial.getEvents())) {
						contains = true;
						counter2++;
					}
				}
				if (contains) {
					counter1++;
				}
			}
		}
		return Tuple.newTuple(counter1, counter2);
	}

	private Tuple<Integer, Integer> subsume(
			Map<Integer, Set<Episode>> partials,
			Map<Integer, Set<Episode>> sequentials) {

		int counter1 = 0;
		int counter2 = 0;

		for (Map.Entry<Integer, Set<Episode>> entry : partials.entrySet()) {
			for (Episode partial : entry.getValue()) {
				boolean contains = false;
				for (Episode sequential : sequentials.get(entry.getKey())) {
					if (repr(partial, sequential)) {
						contains = true;
						counter2++;
					}
				}
				if (contains) {
					counter1++;
				}
			}
		}
		return Tuple.newTuple(counter1, counter2);
	}

	private boolean repr(Episode partial, Episode sequential) {

		if (!partial.getEvents().equals(sequential.getEvents())) {
			return false;
		}
		Set<Fact> seqRels = sequential.getRelations();
		Set<Fact> partRels = partial.getRelations();
		for (Fact rel : seqRels) {
			Tuple<Fact, Fact> tuple = rel.getRelationFacts();
			Fact opRel = new Fact(tuple.getSecond(), tuple.getFirst());

			if (partRels.contains(opRel)) {
				return false;
			}
		}
		return true;
	}

	private void exactMetric(Map<Integer, Set<Episode>> sequentials,
			Map<Integer, Set<Episode>> partials,
			Map<Integer, Set<Episode>> unordered) {

		int counter = equals(partials, sequentials);
		Logger.log("exact(POC, SOC) = %d", counter);

		counter = equals(unordered, partials);
		Logger.log("exact(NOC, POC) = %d", counter);

		counter = equals(unordered, sequentials);
		Logger.log("exact(NOC, SOC) = %d", counter);

		Logger.log("");
	}

	private int equals(Map<Integer, Set<Episode>> pt1,
			Map<Integer, Set<Episode>> pt2) {
		int counter = 0;

		for (Map.Entry<Integer, Set<Episode>> entryPartials : pt1.entrySet()) {
			for (Episode partial : entryPartials.getValue()) {
				for (Episode sequential : pt2.get(entryPartials.getKey())) {
					if (partial.getFacts().equals(sequential.getFacts())) {
						counter++;
						break;
					}
				}
			}
		}
		return counter;
	}
}
