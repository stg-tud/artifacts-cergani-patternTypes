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

package cc.kave.patternTypes.mining.patterns;

import static cc.recommenders.assertions.Asserts.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.patternTypes.io.EpisodeParser;
import cc.kave.patternTypes.io.EventStreamIo;
import cc.kave.patternTypes.model.Episode;
import cc.kave.patternTypes.model.EpisodeType;
import cc.kave.patternTypes.model.events.Event;
import cc.kave.patternTypes.model.events.Events;
import cc.kave.patternTypes.model.events.Fact;
import cc.kave.patternTypes.postprocessor.EnclosingMethods;
import cc.recommenders.datastructures.Tuple;
import cc.recommenders.io.Logger;

import com.google.common.collect.Sets;

public class ThresholdAnalyzer {

	private EventStreamIo streamIo;

	private EpisodeParser parser;
	private PatternFilter filter;

	@Inject
	public ThresholdAnalyzer(EventStreamIo streamIo,
			EpisodeParser episodeParser, PatternFilter patternFilter) {
		this.streamIo = streamIo;
		this.parser = episodeParser;
		this.filter = patternFilter;
	}

	public void entropy(int frequency) throws Exception {
		Map<Integer, Set<Episode>> episodes = parser.parser(frequency);
		Logger.log("\tEntropy threshold analyzes!");

		Set<Integer> frequencies = getFrequencies(episodes);
		int maxFreq = getMax(frequencies);

		printData(frequency, maxFreq);
	}

	public void frequency(EpisodeType type, int frequency, double entropy)
			throws Exception {
		Map<Integer, Set<Episode>> episodes = parser.parser(frequency);
		Set<Integer> frequencies = getFrequencies(episodes);
		int maxFreq = getMax(frequencies);

		Logger.log("\tFrequency analyzes for %s-configuration:",
				type.toString());
		Logger.log("\tEntropy threshold = %.2f", entropy);
		Logger.log("\tFrequency\t#Patterns");
		for (int freq = frequency; freq < maxFreq + 1; freq++) {
			Map<Integer, Set<Episode>> patterns = filter.filter(type, episodes,
					freq, entropy);
			int numbPatterns = count(patterns);
			if (numbPatterns < 100) {
				break;
			}
			Logger.log("\t%d\t%d", freq, numbPatterns);
		}
	}

	public void generalizability(int frequency) throws Exception {
		List<Tuple<IMethodName, List<Fact>>> stream = streamIo
				.readStreamObject(frequency);
		Map<String, Set<IMethodName>> repoCtxMapper = streamIo
				.readRepoCtxs(frequency);
		Map<Integer, Set<Episode>> episodes = parser.parser(frequency);

		Logger.log("\tFrequency\tEntropy\tNumPatterns\tGeneral");
		for (int freq = 320; freq < 321; freq += 5) {
			for (double ent = 0.45; ent < 1.01; ent += 0.05) {
				double entropy = Math.round(ent * 100.0) / 100.0;
				int generals = 0;
				Map<Integer, Set<Episode>> patterns = filter.filter(
						EpisodeType.GENERAL, episodes, freq, entropy);
				for (Map.Entry<Integer, Set<Episode>> entry : patterns
						.entrySet()) {
					for (Episode pattern : entry.getValue()) {
						EnclosingMethods ctxOccs = new EnclosingMethods(true);

						for (Tuple<IMethodName, List<Fact>> tuple : stream) {
							List<Fact> method = tuple.getSecond();
							if (method.size() < 2) {
								continue;
							}
							if (method.containsAll(pattern.getEvents())) {
								Event ctx = Events.newElementContext(tuple
										.getFirst());
								ctxOccs.addMethod(pattern, method, ctx);
							}
						}
						int numOccs = ctxOccs.getOccurrences();
						assertTrue(numOccs >= pattern.getFrequency(),
								"Found insufficient number of occurences!");
						Set<IMethodName> methodOccs = ctxOccs
								.getMethodNames(numOccs);
						Set<String> repositories = Sets.newHashSet();

						for (Map.Entry<String, Set<IMethodName>> repoEntry : repoCtxMapper
								.entrySet()) {
							for (IMethodName methodName : repoEntry.getValue()) {
								if (methodOccs.contains(methodName)) {
									repositories.add(repoEntry.getKey());
									break;
								}
							}
						}
						if (repositories.size() > 1) {
							generals++;
						}
					}
				}
				int numPatterns = count(patterns);
				Logger.log("\t%d\t%.2f\t%d\t%d", freq, entropy, numPatterns,
						generals);
			}
		}
	}

	private void printData(int frequency, int maxFreq) throws Exception {
		Map<Integer, Set<Episode>> episodes = parser.parser(frequency);
		Logger.log("\tFrequency\tEntropy\t#Patterns");

		int prevValue = 0;

		for (int freq = frequency; freq < maxFreq; freq += 10) {

			for (double ent = 0.0; ent < 1.01; ent += 0.01) {
				double entropy = Math.round(ent * 100.0) / 100.0;
				Map<Integer, Set<Episode>> patterns = filter.filter(
						EpisodeType.GENERAL, episodes, freq, entropy);
				int counter = count(patterns);
				if (counter != prevValue) {
					prevValue = counter;
					Logger.log("\t%d\t%.2f\t%d", freq, ent, counter);
				}
			}
		}
	}

	private int getMax(Set<Integer> frequencies) {
		int max = Integer.MIN_VALUE;
		for (int freq : frequencies) {
			if (freq > max) {
				max = freq;
			}
		}
		return max;
	}

	private Set<Integer> getFrequencies(Map<Integer, Set<Episode>> episodes) {
		TreeSet<Integer> results = Sets.newTreeSet();

		for (Map.Entry<Integer, Set<Episode>> entry : episodes.entrySet()) {
			if (entry.getKey() == 1) {
				continue;
			}
			for (Episode ep : entry.getValue()) {
				int frequency = ep.getFrequency();
				results.add(frequency);
			}
		}
		return results;
	}

	private int count(Map<Integer, Set<Episode>> patterns) {
		int counter = 0;
		for (Map.Entry<Integer, Set<Episode>> entry : patterns.entrySet()) {
			counter += entry.getValue().size();
		}
		return counter;
	}
}
