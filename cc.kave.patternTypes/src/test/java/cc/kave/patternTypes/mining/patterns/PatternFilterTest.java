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

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cc.kave.patternTypes.mining.patterns.ParallelPatterns;
import cc.kave.patternTypes.mining.patterns.PartialPatterns;
import cc.kave.patternTypes.mining.patterns.PatternFilter;
import cc.kave.patternTypes.mining.patterns.SequentialPatterns;
import cc.kave.patternTypes.model.Episode;
import cc.kave.patternTypes.model.EpisodeType;

import com.google.common.collect.Maps;

public class PatternFilterTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PartialPatterns partials;
	@Mock
	private SequentialPatterns sequentials;
	@Mock
	private ParallelPatterns parallels;

	private static final int FREQTHRESH = 5;
	private static final double ENTROPY = 0.5;

	private Map<Integer, Set<Episode>> episodes;

	private PatternFilter sut;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		episodes = Maps.newLinkedHashMap();

		sut = new PatternFilter(partials, sequentials, parallels);
	}

	@Test
	public void exception() throws Exception {
		thrown.expect(Exception.class);
		thrown.expectMessage("Not valid episode type!");

		sut.filter(EpisodeType.OTHER, episodes, FREQTHRESH, ENTROPY);
	}

	@Test
	public void partials() throws Exception {
		when(partials.filter(anyMap(), anyInt(), anyDouble())).thenReturn(
				episodes);

		sut.filter(EpisodeType.GENERAL, episodes, FREQTHRESH, ENTROPY);

		verify(partials).filter(anyMap(), anyInt(), anyDouble());
	}

	@Test
	public void sequentials() throws Exception {
		when(sequentials.filter(anyMap(), anyInt())).thenReturn(episodes);

		sut.filter(EpisodeType.SEQUENTIAL, episodes, FREQTHRESH, ENTROPY);

		verify(sequentials).filter(anyMap(), anyInt());
	}

	@Test
	public void parallel() throws Exception {
		when(parallels.filter(anyMap(), anyInt())).thenReturn(episodes);

		sut.filter(EpisodeType.PARALLEL, episodes, FREQTHRESH, ENTROPY);

		verify(parallels).filter(anyMap(), anyInt());
	}
}
