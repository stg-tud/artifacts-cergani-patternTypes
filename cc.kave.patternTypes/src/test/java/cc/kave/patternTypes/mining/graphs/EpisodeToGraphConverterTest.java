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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import cc.kave.commons.model.naming.Names;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.commons.model.naming.codeelements.IParameterName;
import cc.kave.commons.model.naming.types.ITypeName;
import cc.kave.patternTypes.mining.graphs.EpisodeToGraphConverter;
import cc.kave.patternTypes.model.Episode;
import cc.kave.patternTypes.model.events.Event;
import cc.kave.patternTypes.model.events.EventKind;
import cc.kave.patternTypes.model.events.Fact;

public class EpisodeToGraphConverterTest {

	private Episode episode;
	private List<Event> eventMapping;
	private EpisodeToGraphConverter sut;

	@Before
	public void setup() {
		episode = new Episode();
		episode.setFrequency(3);
		episode.setEntropy(0.5);
		episode.addStringsOfFacts("1", "2", "3", "4", "1>2", "1>3", "1>4", "2>4", "3>4");

		createMapping(eventMethodDecl("M1"), eventInvocation("M2"), eventMethodDecl("M3"), eventInvocation("M4"),
				eventMethodDecl("M5"));

		sut = new EpisodeToGraphConverter();
	}

	private void createMapping(Event... events) {
		eventMapping = Lists.newArrayList(events);
	}

	private static Event eventInvocation(String method) {
		Event e = new Event();
		e.setKind(EventKind.INVOCATION);
		e.setMethod(method(method));
		return e;
	}

	private static Event eventMethodDecl(String method) {
		Event e = new Event();
		e.setKind(EventKind.METHOD_DECLARATION);
		e.setMethod(method(method));
		return e;
	}

	private static IMethodName method(String name) {
		ITypeName declType = type("T");
		ITypeName retType = type("R");
		String methodName = String.format("[%s] [%s].%s()", retType, declType, name);
		return Names.newMethod(methodName);
	}

	private static ITypeName type(String name) {
		return Names.newType("some.namespace." + name + ", P");
	}

	@Test
	public void graphTest() {
		DirectedGraph<Fact, DefaultEdge> expected = new DefaultDirectedGraph<Fact, DefaultEdge>(DefaultEdge.class);
		expected.addVertex(new Fact("1"));
		expected.addVertex(new Fact("2"));
		expected.addVertex(new Fact("3"));
		expected.addVertex(new Fact("4"));

		expected.addEdge(new Fact("1"), new Fact("2"));
		expected.addEdge(new Fact("1"), new Fact("3"));
		expected.addEdge(new Fact("1"), new Fact("4"));
		expected.addEdge(new Fact("2"), new Fact("4"));
		expected.addEdge(new Fact("3"), new Fact("4"));

		String labels = "";

		IMethodName method1 = eventMapping.get(1).getMethod();
		String kind1 = eventMapping.get(1).getKind().toString();
		String out1 = toLabel(method1);
		IMethodName method2 = eventMapping.get(2).getMethod();
		String kind2 = eventMapping.get(2).getKind().toString();
		String out2 = toLabel(method2);
		IMethodName method3 = eventMapping.get(3).getMethod();
		String kind3 = eventMapping.get(3).getKind().toString();
		String out3 = toLabel(method3);
		IMethodName method4 = eventMapping.get(4).getMethod();
		String kind4 = eventMapping.get(4).getKind().toString();
		String out4 = toLabel(method4);

		labels += "1. " + kind1 + ": " + out1 + "\\l";
		labels += "2. " + kind2 + ": " + out2 + "\\l";
		labels += "3. " + kind3 + ": " + out3 + "\\l";
		labels += "4. " + kind4 + ": " + out4 + "\\l";

		labels += "Freq = " + episode.getFrequency() + ", bd = " + episode.getEntropy();

		Fact labelNode = new Fact(labels);
		expected.addVertex(labelNode);

		DirectedGraph<Fact, DefaultEdge> actuals = sut.convert(episode, eventMapping);

		assertEquals(expected.toString(), actuals.toString());
	}

	private String toLabel(IMethodName method) {
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
