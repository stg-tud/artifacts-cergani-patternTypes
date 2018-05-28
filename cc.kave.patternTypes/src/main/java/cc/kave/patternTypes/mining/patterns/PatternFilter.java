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

import javax.inject.Inject;

import cc.kave.patternTypes.model.Episode;
import cc.kave.patternTypes.model.EpisodeType;

public class PatternFilter {

	private PartialPatterns partials;
	private SequentialPatterns sequentials;
	private ParallelPatterns parallels;

	@Inject
	public PatternFilter(PartialPatterns partials,
			SequentialPatterns sequentials, ParallelPatterns parallels) {
		this.partials = partials;
		this.sequentials = sequentials;
		this.parallels = parallels;
	}

	public Map<Integer, Set<Episode>> filter(EpisodeType type,
			Map<Integer, Set<Episode>> episodes, int fthresh, double ethresh)
			throws Exception {

		if (type == EpisodeType.GENERAL) {
			return partials.filter(episodes, fthresh, ethresh);
		} else if (type == EpisodeType.SEQUENTIAL) {
			return sequentials.filter(episodes, fthresh);
		} else if (type == EpisodeType.PARALLEL) {
			return parallels.filter(episodes, fthresh);
		} else {
			throw new Exception("Not valid episode type!");
		}
	}
}
