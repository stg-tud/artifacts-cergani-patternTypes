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

package cc.kave.patternTypes.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import cc.kave.patternTypes.model.Threshold;

public class ThresholdsTest {

	private Threshold sut;
	
	@Before 
	public void setup() {
		sut = new Threshold();
	}
	
	@Test
	public void defaultValues() {
		assertTrue(sut.getFrequency() == 1);
		assertTrue(sut.getEntropy() == 0.0);
		assertTrue(sut.getNoGenPatterns() == 0);
		assertTrue(sut.getNoSpecPatterns() == 0);
		assertTrue(sut.getFraction() == 0.0);
	}
	
	@Test
	public void valuesCanBeIncreased() {
		sut.addGenPattern();
		sut.addSpecPattern();
		sut.addSpecPattern();
		sut.addGenPattern();
		sut.addGenPattern();
		
		assertTrue(sut.getNoGenPatterns() == 3);
		assertTrue(sut.getNoSpecPatterns() == 2);
		assertTrue(sut.getFraction() == (3.0 / 5.0));
	}
	
	@Test
	public void equality() {
		Threshold a = new Threshold();
		a.setFrequency(5);
		a.setEntropy(0.3);
		
		Threshold b = new Threshold();
		b.setFrequency(5);
		b.setEntropy(0.3);
		
		assertEquals(a, b);
		assertTrue(a.equals(b));
		assertTrue(a.getFrequency() == b.getFrequency());
		assertTrue(a.getEntropy() == b.getEntropy());
		assertTrue(a.getNoGenPatterns() == b.getNoGenPatterns());
		assertTrue(a.getNoSpecPatterns() == b.getNoSpecPatterns());
		assertTrue(a.getFraction() == b.getFraction());
	}
	
	@Test
	public void different() {
		Threshold a = new Threshold();
		a.setFrequency(7);
		a.setEntropy(0.5);
		
		Threshold b = new Threshold();
		b.setFrequency(4);
		b.setEntropy(1.0);
		
		assertNotEquals(a, b);
		assertFalse(a.equals(b));
		assertTrue(a.getFrequency() != b.getFrequency());
		assertTrue(a.getEntropy() != b.getEntropy());
		assertTrue(a.getNoGenPatterns() == b.getNoGenPatterns());
		assertTrue(a.getNoSpecPatterns() == b.getNoSpecPatterns());
		assertTrue(a.getFraction() == b.getFraction());
	}
	
	@Test
	public void different_freq() {
		Threshold a = new Threshold();
		a.setFrequency(7);
		a.setEntropy(0.5);
		
		Threshold b = new Threshold();
		b.setFrequency(4);
		b.setEntropy(0.5);
		
		assertNotEquals(a, b);
		assertFalse(a.equals(b));
		assertTrue(a.getFrequency() != b.getFrequency());
		assertTrue(a.getEntropy() == b.getEntropy());
		assertTrue(a.getNoGenPatterns() == b.getNoGenPatterns());
		assertTrue(a.getNoSpecPatterns() == b.getNoSpecPatterns());
		assertTrue(a.getFraction() == b.getFraction());
	}
	
	@Test
	public void different_entropy() {
		Threshold a = new Threshold();
		a.setFrequency(4);
		a.setEntropy(0.5);
		
		Threshold b = new Threshold();
		b.setFrequency(4);
		b.setEntropy(1.0);
		
		assertNotEquals(a, b);
		assertFalse(a.equals(b));
		assertTrue(a.getFrequency() == b.getFrequency());
		assertTrue(a.getEntropy() != b.getEntropy());
		assertTrue(a.getNoGenPatterns() == b.getNoGenPatterns());
		assertTrue(a.getNoSpecPatterns() == b.getNoSpecPatterns());
		assertTrue(a.getFraction() == b.getFraction());
	}
}
