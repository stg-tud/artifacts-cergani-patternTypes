/**
 * Copyright 2014 Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.kave.patternTypes.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import cc.kave.patternTypes.model.PatternWithFreq;
import cc.kave.patternTypes.model.events.Fact;

public class PatternTest {

	private PatternWithFreq sut;

	@Before
	public void setup() {
		sut = new PatternWithFreq();
	}

	@Test
	public void defaultValues() {
		assertEquals(0, sut.getNumEvents());
		assertEquals(0, sut.getFrequency());
		assertEquals(Sets.newHashSet(), sut.getFacts());
	}

	@Test
	public void valuesCanBeSet() {
		sut.setFrequency(3);
		assertEquals(3, sut.getFrequency());
		sut.addFact("f");
		assertEquals(Sets.newHashSet(new Fact("f")), sut.getFacts());
		assertEquals(1, sut.getNumEvents());
	}

	@Test
	public void addMultipleFacts() {
		sut.addStringsOfFacts("f", "g");
		assertEquals(Sets.newHashSet(new Fact("f"), new Fact("g")), sut.getFacts());
		assertTrue(sut.getNumFacts() == 2);
	}

	@Test
	public void equality_default() {
		PatternWithFreq a = new PatternWithFreq();
		PatternWithFreq b = new PatternWithFreq();
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertTrue(a.equals(b));
	}

	@Test
	public void equality_reallyTheSame() {
		PatternWithFreq a = new PatternWithFreq();
		a.addStringsOfFacts("1", "2", "3", "1>2", "1>3");

		PatternWithFreq b = new PatternWithFreq();
		b.addStringsOfFacts("1", "2", "3", "1>2", "1>3");

		assertEquals(a, b);
		assertTrue(a.equals(b));
		assertEquals(a.hashCode(), b.hashCode());
		assertEquals(a.getFacts(), b.getFacts());
	}

	@Test
	public void equality_diffEvents() {
		PatternWithFreq a = new PatternWithFreq();
		a.addStringsOfFacts("1", "2", "3", "1>2", "1>3");

		PatternWithFreq b = new PatternWithFreq();
		b.addStringsOfFacts("3", "4", "3>4");

		assertNotEquals(a, b);
		assertFalse(a.equals(b));
		assertNotEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a.getFacts(), b.getFacts());
	}

	@Test
	public void equality_sameEventsDiffRelations() {
		PatternWithFreq a = new PatternWithFreq();
		a.addStringsOfFacts("1", "2", "3", "1>2", "1>3");

		PatternWithFreq b = new PatternWithFreq();
		b.addStringsOfFacts("1", "2", "3", "1>2");

		assertNotEquals(a, b);
		assertFalse(a.equals(b));
		assertNotEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a.getFacts(), b.getFacts());
	}

	@Test
	public void containFactTest() {
		sut.addStringsOfFacts("1", "2", "3", "1>2", "1>3");

		boolean actual = sut.containsFact(new Fact("2"));
		assertTrue(actual);
	}

	@Test
	public void notContainFactTest() {
		sut.addStringsOfFacts("1", "2", "3", "1>2", "1>3");

		boolean actual = sut.containsFact(new Fact("4"));
		assertFalse(actual);
	}
}