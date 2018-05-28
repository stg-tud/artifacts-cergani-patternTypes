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
package cc.kave.patternTypes.mining.graphs;

import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.commons.model.naming.codeelements.IParameterName;
import cc.kave.patternTypes.model.Episode;
import cc.kave.patternTypes.model.events.Event;
import cc.kave.patternTypes.model.events.EventKind;
import cc.kave.patternTypes.model.events.Fact;
import cc.recommenders.datastructures.Tuple;

public class EpisodeToGraphConverter {

	public DirectedGraph<Fact, DefaultEdge> convert(Episode episode, List<Event> eventMapping) {
		DirectedGraph<Fact, DefaultEdge> graph = new DefaultDirectedGraph<Fact, DefaultEdge>(DefaultEdge.class);

		String labels = "";

		for (Fact fact : episode.getFacts()) {
			if (!fact.isRelation()) {
				graph.addVertex(fact);
			}
		}
		for (Fact fact : episode.getFacts()) {
			if (fact.isRelation()) {
				Tuple<Fact, Fact> existance = fact.getRelationFacts();
				graph.addEdge(existance.getFirst(), existance.getSecond());
			}
		}
		TopologicalOrderIterator<Fact, DefaultEdge> toi = new TopologicalOrderIterator<>(graph);
		while (toi.hasNext()) {
			Fact fact = toi.next();
			int index = fact.getFactID();
			IMethodName method = eventMapping.get(index).getMethod();
			EventKind kind = eventMapping.get(index).getKind();

			String out = toLabel(method);
			labels += fact.getFactID() + ". " + kind.toString() + ": " + out + "\\l";
		}
		labels += "Freq = " + episode.getFrequency() + ", bd = " + episode.getEntropy();
		Fact labelFact = new Fact(labels);
		graph.addVertex(labelFact);

		return graph;
	}

	public String toLabel(IMethodName method) {
		StringBuilder sb = new StringBuilder();

		sb.append(method.getDeclaringType().getFullName());
		sb.append('.');
		sb.append(method.getName());
		sb.append('(');
		boolean isFirst = true;
		for (IParameterName p : method.getParameters()) {
			if (!isFirst) {
				sb.append(", ");
			}
			isFirst = false;
			sb.append(p.getValueType().getName());
			sb.append(' ');
			sb.append(p.getName());
		}
		sb.append(')');
		sb.append(" : ");
		sb.append(method.getReturnType().getName());

		return sb.toString();
	}
}
