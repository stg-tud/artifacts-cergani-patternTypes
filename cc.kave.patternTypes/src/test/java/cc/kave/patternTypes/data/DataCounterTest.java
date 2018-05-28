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

package cc.kave.patternTypes.data;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static cc.recommenders.io.LoggerUtils.assertLogContains;
import cc.kave.patternTypes.data.DataCounter;
import cc.recommenders.io.Directory;
import cc.recommenders.io.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

public class DataCounterTest {

	@Mock
	private Directory rootDirectory;
	
	private static final String REPO0 = "Github/usr1/repo0/ws.zip";
	private static final String REPO1 = "Github/usr1/repo1/ws.zip";
	private static final String REPO2 = "Github/usr1/repo2/ws.zip";
	private static final String REPO3 = "Github/usr1/repo3/ws.zip";
	
	Set<String> repos;
	
	private DataCounter sut;
	
	@Before
	public void setup() {
		Logger.reset();
		Logger.setCapturing(true);

		MockitoAnnotations.initMocks(this);
		
		repos = Sets.newLinkedHashSet();
		repos.add(REPO0);
		repos.add(REPO1);
		repos.add(REPO2);
		repos.add(REPO3);
		
		sut = new DataCounter(rootDirectory);
		
		when(rootDirectory.findFiles(anyPredicateOf(String.class))).thenAnswer(
				new Answer<Set<String>>() {
					@Override
					public Set<String> answer(InvocationOnMock invocation)
							throws Throwable {
						return repos;
					}
				});
		Logger.setPrinting(false);
	}
	
	@After
	public void teardown() {
		Logger.reset();
	}
	
	@Test
	public void logger() {
		Logger.clearLog();
		
		sut.namespaces();
		
		verify(rootDirectory).findFiles(anyPredicateOf(String.class));
		
		assertLogContains(0, "Number of namespaces: 4");
	}
	
	private <T> Predicate<T> anyPredicateOf(Class<T> clazz) {
		@SuppressWarnings("unchecked")
		Predicate<T> p = any(Predicate.class);
		return p;
	}
}
