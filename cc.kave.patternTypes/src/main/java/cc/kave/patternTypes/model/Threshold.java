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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Threshold {

	private int frequency;
	private double entropy;

	private int noGens = 0;
	private int noSpecs = 0;

	public Threshold() {
		this.frequency = 1;
		this.entropy = 0.0;
	}
	
	public void addGenPattern() {
		this.noGens++;
	}

	public void addSpecPattern() {
		this.noSpecs++;
	}
	
	public void setFrequency(int freq) {
		this.frequency = freq;
	}
	 
	public void setEntropy(double ent) {
		this.entropy = ent;
	}

	public int getFrequency() {
		return this.frequency;
	}

	public double getEntropy() {
		return this.entropy;
	}

	public int getNoGenPatterns() {
		return this.noGens;
	}

	public int getNoSpecPatterns() {
		return this.noSpecs;
	}

	public double getFraction() {
		int numPatterns = this.noGens + this.noSpecs;
		if (numPatterns == 0) {
			return 0.0;
		} else {
			return ((1.0) * (this.noGens)) / ((1.0) * numPatterns);
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public boolean equals(Threshold threshs) {
		if (this.frequency != threshs.frequency) {
			return false;
		}
		if (this.entropy != threshs.entropy) {
			return false;
		}
		return true;
	}
}
