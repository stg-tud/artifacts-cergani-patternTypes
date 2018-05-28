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

package cc.kave.patternTypes.statistics;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.kave.patternTypes.io.EpisodeParser;
import cc.kave.patternTypes.io.EventStreamIo;
import cc.kave.patternTypes.mining.patterns.PatternFilter;
import cc.kave.patternTypes.model.Episode;
import cc.kave.patternTypes.model.EpisodeType;
import cc.kave.patternTypes.model.events.Event;
import cc.kave.patternTypes.model.events.Fact;
import cc.recommenders.io.Logger;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class APITypes {

	private EventStreamIo streamIo;
	private EpisodeParser parser;
	private PatternFilter filter;

	@Inject
	public APITypes(EventStreamIo eventStream, EpisodeParser episodeParser, PatternFilter patternFilter) {
		this.streamIo = eventStream;
		this.parser = episodeParser;
		this.filter = patternFilter;
	}

	public void streamAPIs(int frequency) {
		List<Event> events = streamIo.readMapping(frequency);
		Set<String> libraries = Sets.newHashSet();

		for (Event event : events) {
			String libName = getLibraryName(event);
			libraries.add(libName);
		}
		Logger.log("\tLibraries analyzed in event stream");
		for (String lib : libraries) {
			Logger.log("\t%s", lib);
		}
	}

	public void patternAPI(int frequency, int freqThresh, double entThresh)
			throws Exception {
		List<Event> events = streamIo.readMapping(frequency);
		Map<Integer, Set<Episode>> episodes = parser.parser(frequency);
		Map<Integer, Set<Episode>> patterns = filter.filter(
				EpisodeType.PARALLEL, episodes, freqThresh, entThresh);
		Set<String> libraries = Sets.newHashSet();
		
		for (Map.Entry<Integer, Set<Episode>> entry : patterns.entrySet()) {
			for (Episode pattern : entry.getValue()) {
				for (Fact fact : pattern.getEvents()) {
					Event event = events.get(fact.getFactID());
					String libName = getLibraryName(event);
					libraries.add(libName);
				}
			}
		}
		Logger.log("\tLibraries for which we learn patterns");
		for (String lib : libraries) {
			Logger.log("\t%s", lib);
		}
	}
	
	private String getLibraryName(Event event) {
		String typeName = event.getMethod().getDeclaringType()
				.getFullName();
		if (typeName.contains(".")) {
			int idx = typeName.indexOf(".");
			String libName = typeName.substring(0, idx);
			return libName;
		}
		return "";
	}
}
