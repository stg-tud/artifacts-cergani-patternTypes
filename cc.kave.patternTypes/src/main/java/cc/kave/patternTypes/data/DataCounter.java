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

package cc.kave.patternTypes.data;

import java.util.Set;

import cc.recommenders.io.Directory;
import cc.recommenders.io.Logger;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DataCounter {

	private Directory contextsDir;
	
	@Inject
	public DataCounter(@Named("contexts") Directory directory) {
		this.contextsDir = directory;
	}
	
	public void namespaces() {
		int namespaces = findZips(contextsDir).size();
		Logger.log("Number of namespaces: %d", namespaces);
	}
	
	private Set<String> findZips(Directory contextsDir) {
		Set<String> zips = contextsDir.findFiles(new Predicate<String>() {

			@Override
			public boolean apply(String arg0) {
				return arg0.endsWith(".zip");
			}
		});
		return zips;
	}
}
