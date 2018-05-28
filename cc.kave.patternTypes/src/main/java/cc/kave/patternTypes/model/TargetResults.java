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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import cc.kave.patternTypes.model.events.Fact;
import cc.recommenders.datastructures.Tuple;

public class TargetResults {

	private Episode target = new Episode();
	Map<Double, List<Tuple<Double, Double>>> results = new HashMap<Double, List<Tuple<Double, Double>>>();
	
	public Episode getTarget() {
		return this.target;
	}
	
	public void setTarget(Episode ep) {
		this.target.setFrequency(ep.getFrequency());
		
		for (Fact fact : ep.getFacts()) {
			this.target.addFact(fact);
		}
	}
	
	public Map<Double, List<Tuple<Double, Double>>> getResults() {
		return this.results;
	}
	
	public void addResult(double qs, double qp, double tp) {
		if (!this.results.containsKey(qs)) {
			this.results.put(qs, new LinkedList<Tuple<Double, Double>>());
		} 
		this.results.get(qs).add(Tuple.newTuple(qp, tp));
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	public boolean equals(TargetResults ps) {
		if (!this.target.equals(ps.target)) {
			return false;
		}
		if (this.results.size() != ps.results.size()) {
			return false;
		}
		if (!this.results.equals(ps.getResults())) {
			return false;
		}
		return true;
	}
}
