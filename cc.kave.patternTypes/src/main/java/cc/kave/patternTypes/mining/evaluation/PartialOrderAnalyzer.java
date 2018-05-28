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

import javax.inject.Inject;

import cc.kave.patternTypes.io.EpisodeParser;
import cc.kave.patternTypes.model.Episode;
import cc.recommenders.io.Logger;

public class PartialOrderAnalyzer {

	private EpisodeParser parser;

	@Inject
	public PartialOrderAnalyzer(
			EpisodeParser parser) {
		this.parser = parser;
	}

	public void analyze(int foldNum, int frequency, double entropy) {
		Map<Integer, Set<Episode>> episodes = parser.parser(frequency);
		
		Logger.log("Number of Nodes\tPartial\tSequential\tParallel");
		for (Map.Entry<Integer, Set<Episode>> entry : episodes.entrySet()) {
			int partial = 0;
			int seq = 0;
			int parallel = 0;
			
			Set<Episode> pts = entry.getValue();
			
			for (Episode p : pts) {
				if (p.getEntropy() == 1.0) {
					seq++;
				} else if (p.getFacts().size() == p.getEvents().size()) {
					parallel++;
				} else {
					partial++;
				}
			}
			Logger.log("%d\t%d\t%d\t%d", entry.getKey(), partial, seq, parallel);
		}
	}
}
