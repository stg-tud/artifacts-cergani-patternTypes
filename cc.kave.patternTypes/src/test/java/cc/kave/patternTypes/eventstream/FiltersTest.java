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

package cc.kave.patternTypes.eventstream;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cc.kave.commons.model.naming.Names;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.patternTypes.eventstream.Filters;
import cc.kave.patternTypes.model.events.Event;
import cc.kave.patternTypes.model.events.Events;
import cc.recommenders.datastructures.Tuple;

import com.google.common.collect.Lists;

public class FiltersTest {

	private List<Event> stream;
	private List<Tuple<Event, List<Event>>> expected;

	private static final int FREQUENCY = 2;

	private Filters sut;

	@Before
	public void setup() {
		stream = Lists.newLinkedList();
		expected = Lists.newLinkedList();

		sut = new Filters();
	}

	@Test
	public void testStruct() {
		stream.add(Events.newElementContext(m(11)));
		stream.add(Events.newFirstContext(m(21)));
		stream.add(Events.newSuperContext(m(30)));
		stream.add(Events.newInvocation(m(41)));
		stream.add(Events.newInvocation(m(42)));
		stream.add(Events.newInvocation(m(43)));

		stream.add(Events.newElementContext(m(30)));
		stream.add(Events.newFirstContext(m(22)));
		stream.add(Events.newSuperContext(m(32)));

		stream.add(Events.newElementContext(m(12)));

		stream.add(Events.newElementContext(m(30)));
		stream.add(Events.newInvocation(m(44)));

		List<Event> method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newSuperContext(m(30)));
		method.add(Events.newInvocation(m(41)));
		method.add(Events.newInvocation(m(42)));
		method.add(Events.newInvocation(m(43)));
		expected.add(Tuple.newTuple(Events.newElementContext(m(11)), method));

		method = Lists.newLinkedList();
		method.add(Events.newInvocation(m(44)));
		expected.add(Tuple.newTuple(Events.newElementContext(m(30)), method));

		List<Tuple<Event, List<Event>>> actual = sut.getStructStream(stream);

		assertEquals(expected, actual);
	}

	@Test
	public void testLocals() {
		List<Tuple<Event, List<Event>>> input = Lists.newLinkedList();
		List<Event> method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newSuperContext(m(30)));
		method.add(Events.newInvocation(m(41)));
		method.add(Events.newInvocation(m(30)));
		method.add(Events.newInvocation(m(43)));
		input.add(Tuple.newTuple(Events.newElementContext(m(11)), method));

		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(30)));
		method.add(Events.newInvocation(m(44)));
		input.add(Tuple.newTuple(Events.newElementContext(m(30)), method));

		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newInvocation(m(41)));
		method.add(Events.newInvocation(m(43)));
		expected.add(Tuple.newTuple(Events.newElementContext(m(11)), method));

		method = Lists.newLinkedList();
		method.add(Events.newInvocation(m(44)));
		expected.add(Tuple.newTuple(Events.newElementContext(m(30)), method));

		List<Tuple<Event, List<Event>>> actual = sut.locals(input);

		assertEquals(expected, actual);
	}

	@Test
	public void testUnknowns() {
		List<Tuple<Event, List<Event>>> input = Lists.newLinkedList();
		List<Event> method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newSuperContext(m(0)));
		method.add(Events.newInvocation(m(41)));
		method.add(Events.newInvocation(m(0)));
		method.add(Events.newInvocation(m(43)));
		input.add(Tuple.newTuple(Events.newElementContext(m(11)), method));

		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(0)));
		method.add(Events.newInvocation(m(44)));
		input.add(Tuple.newTuple(Events.newElementContext(m(0)), method));

		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newInvocation(m(41)));
		method.add(Events.newInvocation(m(43)));
		expected.add(Tuple.newTuple(Events.newElementContext(m(11)), method));

		method = Lists.newLinkedList();
		method.add(Events.newInvocation(m(44)));
		expected.add(Tuple.newTuple(Events.newElementContext(m(0)), method));

		List<Tuple<Event, List<Event>>> actual = sut.unknowns(input);

		assertEquals(expected, actual);
	}

	@Test
	public void testOverlaps() {
		List<Tuple<Event, List<Event>>> input = Lists.newLinkedList();
		List<Event> method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newSuperContext(m(31)));
		method.add(Events.newInvocation(m(41)));
		method.add(Events.newInvocation(m(42)));
		method.add(Events.newInvocation(m(43)));
		input.add(Tuple.newTuple(Events.newElementContext(m(11)), method));

		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(22)));
		method.add(Events.newInvocation(m(44)));
		input.add(Tuple.newTuple(Events.newElementContext(m(30)), method));

		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newInvocation(m(41)));
		input.add(Tuple.newTuple(Events.newElementContext(m(11)), method));

		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(22)));
		method.add(Events.newInvocation(m(44)));
		input.add(Tuple.newTuple(Events.newElementContext(m(30)), method));

		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newSuperContext(m(31)));
		method.add(Events.newInvocation(m(41)));
		method.add(Events.newInvocation(m(42)));
		method.add(Events.newInvocation(m(43)));
		expected.add(Tuple.newTuple(Events.newElementContext(m(11)), method));

		IMethodName normMethod = Names.newMethod("[T,???] [T,???].m30()");
		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(22)));
		method.add(Events.newInvocation(m(44)));
		expected.add(Tuple.newTuple(Events.newElementContext(normMethod),
				method));

		List<Tuple<Event, List<Event>>> actual = sut.overlaps(input);

		assertEquals(expected, actual);
	}

	@Test
	public void testFreqs() {
		List<Tuple<Event, List<Event>>> input = Lists.newLinkedList();
		List<Event> method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newSuperContext(m(31)));
		method.add(Events.newInvocation(m(41)));
		method.add(Events.newInvocation(m(42)));
		method.add(Events.newInvocation(m(43)));
		input.add(Tuple.newTuple(Events.newElementContext(m(11)), method));

		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(22)));
		method.add(Events.newInvocation(m(42)));
		input.add(Tuple.newTuple(Events.newElementContext(m(12)), method));

		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newInvocation(m(41)));
		input.add(Tuple.newTuple(Events.newElementContext(m(13)), method));

		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newInvocation(m(41)));
		method.add(Events.newInvocation(m(42)));
		expected.add(Tuple.newTuple(Events.newElementContext(m(11)), method));

		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newInvocation(m(41)));
		expected.add(Tuple.newTuple(Events.newElementContext(m(13)), method));

		List<Tuple<Event, List<Event>>> actual = sut.freqEvents(input,
				FREQUENCY);

		assertEquals(expected, actual);
	}

	@Test
	public void testErrMsg() {
		List<Tuple<Event, List<Event>>> input = Lists.newLinkedList();
		List<Event> method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newSuperContext(m(31)));
		method.add(Events.newInvocation(m(41)));
		method.add(Events.newInvocation(m(0)));
		method.add(Events.newInvocation(m(43)));
		input.add(Tuple.newTuple(Events.newElementContext(m(11)), method));

		method = Lists.newLinkedList();
		method.add(Events.newFirstContext(m(21)));
		method.add(Events.newSuperContext(m(31)));
		method.add(Events.newInvocation(m(41)));
		method.add(Events.newInvocation(m(0)));
		method.add(Events.newInvocation(m(43)));
		expected.add(Tuple.newTuple(Events.newElementContext(m(11)), method));

		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));

		List<Tuple<Event, List<Event>>> actual = sut.locals(input);

		assertEquals("different localness for: TypeName(?)\n",
				outContent.toString());
		assertEquals(expected, actual);
	}

	private static IMethodName m(int i) {
		if (i == 0) {
			return Names.getUnknownMethod();
		} else if (i == 10) {
			return Names
					.newMethod("[mscorlib,P, 4.0.0.0] [mscorlib,P, 4.0.0.0].m()");
		} else if (i == 20) {
			return Names.newMethod("[mscorlib,P] [mscorlib,P].m()");
		} else if (i == 30) {
			return Names.newMethod("[T,P] [T,P].m" + i + "()");
		} else {
			return Names
					.newMethod("[T,P, 1.2.3.4] [T,P, 1.2.3.4].m" + i + "()");
		}
	}
}
