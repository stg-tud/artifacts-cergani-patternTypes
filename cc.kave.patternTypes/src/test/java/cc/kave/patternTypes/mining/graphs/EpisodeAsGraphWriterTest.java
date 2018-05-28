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

import static cc.recommenders.assertions.Asserts.assertTrue;

import java.io.File;
import java.io.IOException;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import cc.kave.patternTypes.mining.graphs.EpisodeAsGraphWriter;
import cc.kave.patternTypes.model.events.Fact;

public class EpisodeAsGraphWriterTest {

	@Rule
	public TemporaryFolder rootFolder = new TemporaryFolder();

	private DirectedGraph<Fact, DefaultEdge> graph;
	private EpisodeAsGraphWriter sut;

	@Before
	public void setup() {
		sut = new EpisodeAsGraphWriter();

		graph = new DefaultDirectedGraph<Fact, DefaultEdge>(DefaultEdge.class);

		Fact f1 = new Fact("1");
		Fact f2 = new Fact("2");
		Fact f3 = new Fact("3");
		Fact f4 = new Fact("4");

		graph.addVertex(f1);
		graph.addVertex(f2);
		graph.addVertex(f3);
		graph.addVertex(f4);

		graph.addEdge(f1, f2);
		graph.addEdge(f1, f3);
		graph.addEdge(f1, f4);
		graph.addEdge(f2, f4);
		graph.addEdge(f3, f4);
	}

	@Test
	public void writerDirectoryTest() throws IOException {
		String filePath = getFilePath(1);
		sut.write(graph, filePath);
		File file = new File(filePath);
		assertTrue(file.getParentFile().exists());
		assertTrue(file.exists());
		file.delete();
	}

	private String getFilePath(int index) {
		String dirPath = rootFolder.getRoot().getAbsolutePath() + "/graphs/";
		new File(dirPath).mkdirs();
		String fileName = dirPath + "/graph" + index + ".dot";
		return fileName;
	}
}
