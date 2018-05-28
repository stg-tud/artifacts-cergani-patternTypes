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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cc.kave.patternTypes.model.Episode;

public class SequentialPatterns {

	public Map<Integer, Set<Episode>> filter(
			Map<Integer, Set<Episode>> episodes, int frequency) {
		Map<Integer, Set<Episode>> results = Maps.newLinkedHashMap();

		for (Map.Entry<Integer, Set<Episode>> entry : episodes.entrySet()) {
			if (entry.getKey() == 1) {
				continue;
			}
			Set<Episode> filtered = Sets.newLinkedHashSet();

			for (Episode ep : entry.getValue()) {
				if ((ep.getFrequency() >= frequency)
						&& (ep.getEntropy() == 1.0)
						&& (ep.getRelations().size() == numRels(ep
								.getNumEvents()))) {
					filtered.add(ep);
				}
			}
			if (!filtered.isEmpty()) {
				results.put(entry.getKey(), filtered);
			}
		}
		return results;
	}

	private int numRels(int numEvents) {
		int numRels = 0;
		if (numEvents == 2) {
			return 1;
		} else {
			numRels = numEvents - 1 + numRels(numEvents - 1);
			return numRels;
		}
	}
}
