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
package cc.kave.patternTypes.sample.code;

import static cc.recommenders.assertions.Asserts.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import cc.kave.patternTypes.io.EpisodeParser;
import cc.kave.patternTypes.io.EventStreamIo;
import cc.kave.patternTypes.io.FileReader;
import cc.kave.patternTypes.mining.graphs.EpisodeAsGraphWriter;
import cc.kave.patternTypes.mining.graphs.EpisodeToGraphConverter;
import cc.kave.patternTypes.model.Episode;
import cc.kave.patternTypes.model.events.Event;
import cc.kave.patternTypes.model.events.Fact;
import cc.kave.patternTypes.postprocessor.TransClosedEpisodes;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SampleCodeMatcher {

	private File rootFolder;
	
	@Inject
	public SampleCodeMatcher(@Named("rootDir") File folder) {
		assertTrue(folder.exists(), "Contexts folder does not exist");
		assertTrue(folder.isDirectory(), "Contexts is not a folder, but a file");
		this.rootFolder = folder;
	}
	private FileReader reader = new FileReader();
	private EpisodeParser epParser = new EpisodeParser(rootFolder, reader);
	private EventStreamIo streamIo = new EventStreamIo(rootFolder);
	private EpisodeToGraphConverter graphConverter = new EpisodeToGraphConverter();
	private TransClosedEpisodes transitivity = new TransClosedEpisodes();
	private EpisodeAsGraphWriter graphWriter = new EpisodeAsGraphWriter();
	
	public void generateGraphs(int frequency) throws IOException {
		String type = "ArrayList";
		
		Map<Integer, Set<Episode>> allEpisodes = epParser.parser(frequency);
		List<Event> mapper = streamIo.readMapping(frequency);
		
		int nodeLevel = allEpisodes.size() - 1;
		int graphID = 0;
		
		for (Episode ep : allEpisodes.get(nodeLevel)) {
			Episode closedEpisode = transitivity.remTransClosure(ep);
			for (Fact fact : closedEpisode.getFacts()) {
				int factID = fact.getFactID();
				Event event = mapper.get(factID);
				if (event.getMethod().getDeclaringType().getFullName().contains(type)) {
					DirectedGraph<Fact, DefaultEdge> graph = graphConverter.convert(closedEpisode, mapper);
					String fileName = "graph" + graphID + ".txt";
					graphWriter.write(graph, fileName);
					graphID++;
					break;
				}
			}
		}
	} 
}
