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
package cc.kave.patternTypes.io;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipException;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.inject.name.Named;

import cc.kave.commons.model.events.completionevents.Context;
import cc.kave.commons.model.ssts.ISST;
import cc.kave.commons.model.ssts.declarations.IMethodDeclaration;
import cc.kave.commons.model.ssts.visitor.ISSTNodeVisitor;
import cc.kave.patternTypes.eventstream.ToFactsVisitor;
import cc.kave.patternTypes.model.Episode;
import cc.kave.patternTypes.model.events.Event;
import cc.kave.patternTypes.model.events.Fact;
import cc.recommenders.datastructures.Tuple;
import cc.recommenders.io.Directory;
import cc.recommenders.io.IReadingArchive;

public class ValidationContextsParser {

	private Directory rootDir;

	public ValidationContextsParser(@Named("contexts") Directory directory) {
		this.rootDir = directory;
	}

	public Set<Episode> parse(List<Event> eventsList) throws ZipException, IOException {
		Set<Episode> validationData = Sets.newHashSet();

		for (String zip : findZips()) {
			IReadingArchive ra = rootDir.getReadingArchive(zip);

			while (ra.hasNext()) {
				Context ctx = ra.getNext(Context.class);

				ISSTNodeVisitor<Set<Fact>, Void> tfv = new ToFactsVisitor(eventsList);

				ISST sst = ctx.getSST();

				for (IMethodDeclaration md : sst.getMethods()) {

					Set<Fact> facts = Sets.newHashSet();

					md.accept(tfv, facts);

					Episode qt = createEpisode(facts);
					validationData.add(qt);
				}
			}
			ra.close();
		}
		return validationData;
	}

	private Set<String> findZips() {
		Set<String> zips = rootDir.findFiles(new Predicate<String>() {

			@Override
			public boolean apply(String arg0) {
				return arg0.endsWith(".zip");
			}
		});
		return zips;
	}

	private Episode createEpisode(Set<Fact> facts) {
		Episode qts = new Episode();

		for (Fact f : facts) {
			if (!f.isRelation()) {
				qts.addFact(f);
			} else {
				Tuple<Fact, Fact> factsTuple = f.getRelationFacts();
				if (!factsTuple.getFirst().equals(factsTuple.getSecond())) {
					qts.addFact(f);
				}
			}
		}
		return qts;
	}
}