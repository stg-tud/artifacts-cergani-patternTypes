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

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cc.kave.patternTypes.mining.patterns.SequentialPatterns;
import cc.kave.patternTypes.model.Episode;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SequentialPatternsTest {

	private static final int FREQUENCY = 2;
	
	private Map<Integer, Set<Episode>> episodes;
	
	private SequentialPatterns sut;
	
	@Before 
	public void setup() {
		episodes = Maps.newLinkedHashMap();
		sut = new SequentialPatterns();
	}
	
	@Test
	public void removeOneNodes() throws Exception {
		Set<Episode> oneNodes = Sets.newLinkedHashSet();

		oneNodes.add(createEpisode(3, 1.0, "1"));
		oneNodes.add(createEpisode(8, 1.0, "2"));
		episodes.put(1, oneNodes);

		Map<Integer, Set<Episode>> actuals = sut.filter(episodes, FREQUENCY);

		assertEquals(Maps.newLinkedHashMap(), actuals);
	}
	
	@Test
	public void multipleEqualEpisodes() throws Exception {

		Set<Episode> twoNodes = Sets.newLinkedHashSet();
		twoNodes.add(createEpisode(8, 1.0, "1", "2", "1>2"));
		twoNodes.add(createEpisode(8, 1.0, "1", "2", "2>1"));
		episodes.put(2, twoNodes);

		Map<Integer, Set<Episode>> actuals = sut.filter(episodes, FREQUENCY);

		assertEquals(episodes, actuals);
	}
	
	@Test
	public void threeNodes() {
		Set<Episode> threeNodes = Sets.newLinkedHashSet();
		threeNodes.add(createEpisode(3, 0.5, "1", "2", "3"));
		threeNodes.add(createEpisode(3, 0.6, "1", "2", "3", "1>2"));
		threeNodes.add(createEpisode(4, 0.7, "1", "2", "3", "2>1", "2>3"));
		threeNodes.add(createEpisode(5, 0.8, "1", "2", "3", "1>3"));
		threeNodes.add(createEpisode(4, 1.0, "1", "2", "3", "1>2", "1>3", "2>3"));
		threeNodes.add(createEpisode(4, 1.0, "1", "2", "3", "2>1", "2>3", "1>3"));
		threeNodes.add(createEpisode(6, 1.0, "1", "2", "3", "3>1", "3>2", "1>2"));
		episodes.put(3, threeNodes);
		
		Map<Integer, Set<Episode>> actuals = sut.filter(episodes, FREQUENCY);
		
		Map<Integer, Set<Episode>> expected = Maps.newLinkedHashMap();
		Set<Episode> set = Sets.newLinkedHashSet();
		set.add(createEpisode(4, 1.0, "1", "2", "3", "1>2", "1>3", "2>3"));
		set.add(createEpisode(4, 1.0, "1", "2", "3", "2>1", "2>3", "1>3"));
		set.add(createEpisode(6, 1.0, "1", "2", "3", "3>1", "3>2", "1>2"));
		expected.put(3, set);
		
		assertEquals(expected, actuals);
	}
	
	private Episode createEpisode(int freq, double bdmeas, String... strings) {
		Episode episode = new Episode();
		episode.setFrequency(freq);
		episode.setEntropy(bdmeas);
		for (String fact : strings) {
			episode.addFact(fact);
		}
		return episode;
	}
}
