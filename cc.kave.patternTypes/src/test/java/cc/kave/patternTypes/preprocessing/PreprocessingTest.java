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

package cc.kave.patternTypes.preprocessing;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import cc.kave.commons.model.naming.Names;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.patternTypes.data.ContextsParser;
import cc.kave.patternTypes.io.EventStreamIo;
import cc.kave.patternTypes.model.EventStream;
import cc.kave.patternTypes.model.events.Event;
import cc.kave.patternTypes.model.events.Events;
import cc.recommenders.datastructures.Tuple;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class PreprocessingTest {

	@Mock
	private ContextsParser ctxParser;
	@Mock
	private EventStreamIo trainingIo;

	private List<Tuple<Event, List<Event>>> stream;

	private static final int FREQUENCY = 5;

	private Preprocessing sut;

	@Before
	public void setup() throws Exception {
		initMocks(this);
		stream = Lists.newLinkedList();

		sut = new Preprocessing(ctxParser, trainingIo);

		stream.add(Tuple.newTuple(enclCtx(20),
				Lists.newArrayList(firstCtx(1), inv(2), inv(3))));
		stream.add(Tuple.newTuple(enclCtx(7), Lists.newArrayList(firstCtx(2),
				superCtx(2), inv(5), inv(20), inv(2))));

		when(ctxParser.parse(FREQUENCY)).thenReturn(stream);

		doNothing().when(trainingIo).write(any(EventStream.class), anyInt());
	}

	@Test
	public void checkAllRepos() throws Exception {
		EventStream expected = new EventStream();
		expected.addEvent(firstCtx(1));
		expected.addEvent(inv(2));
		expected.addEvent(inv(3));
		expected.increaseTimeout();
		
		expected.addEvent(firstCtx(2));
		expected.addEvent(superCtx(2));
		expected.addEvent(inv(5));
		expected.addEvent(inv(20));
		expected.addEvent(inv(2));
		expected.increaseTimeout();
		
		EventStream actuals = sut.run(FREQUENCY);

		verify(ctxParser).parse(FREQUENCY);
		verify(trainingIo).write(any(EventStream.class), anyInt());
		
		assertTrue(expected.equals(actuals));
	}
	
	private static Event inv(int i) {
		return Events.newInvocation(m(i));
	}

	private static Event firstCtx(int i) {
		return Events.newFirstContext(m(i));
	}

	private static Event superCtx(int i) {
		return Events.newSuperContext(m(i));
	}

	private static Event enclCtx(int i) {
		return Events.newElementContext(m(i));
	}

	private static IMethodName m(int i) {
		return Names.newMethod("[T,P, 1.2.3.4] [T,P, 1.2.3.4].m" + i + "()");
	}
}
