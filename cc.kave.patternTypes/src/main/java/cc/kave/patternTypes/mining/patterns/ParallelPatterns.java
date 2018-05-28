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

import java.util.Map;
import java.util.Set;

import cc.kave.patternTypes.model.Episode;
import cc.kave.patternTypes.model.events.Fact;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ParallelPatterns {

	public Map<Integer, Set<Episode>> filter(
			Map<Integer, Set<Episode>> episodes, int frequency) {
		Map<Integer, Set<Episode>> results = Maps.newLinkedHashMap();

		for (Map.Entry<Integer, Set<Episode>> entry : episodes.entrySet()) {
			if (entry.getKey() == 1) {
				continue;
			}
			Map<Set<Fact>, Episode> filtered = Maps.newLinkedHashMap();

			for (Episode ep : entry.getValue()) {
				if (ep.getFrequency() >= frequency) {
					if (filtered.containsKey(ep.getEvents())) {
						Episode prev = filtered.get(ep.getEvents());
						Episode select = selectPattern(ep, prev);

						filtered.put(select.getEvents(), select);
					} else {
						filtered.put(ep.getEvents(), ep);
					}
				}
			}
			if (!filtered.isEmpty()) {
				results.put(entry.getKey(), getValues(filtered));
			}
		}
		return results;
	}

	private Set<Episode> getValues(Map<Set<Fact>, Episode> episodes) {
		Set<Episode> results = Sets.newLinkedHashSet();

		for (Map.Entry<Set<Fact>, Episode> entry : episodes.entrySet()) {
			results.add(createEpisode(entry.getValue()));
		}
		return results;
	}

	private Episode createEpisode(Episode episode) {
		Episode pattern = new Episode();

		for (Fact fact : episode.getEvents()) {
			pattern.addFact(fact);
		}
		pattern.setFrequency(episode.getFrequency());
		pattern.setEntropy(episode.getEntropy());

		return pattern;
	}

	private Episode selectPattern(Episode ep, Episode prev) {
		if (ep.getRelations().size() < prev.getRelations().size()) {
			return ep;
		}
		if (ep.getEntropy() < prev.getEntropy()) {
			return ep;
		}
		if (ep.getFrequency() > prev.getFrequency()) {
			return ep;
		}
		return prev;
	}
}
