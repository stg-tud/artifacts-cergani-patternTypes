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

package cc.kave.patternTypes.eventstream;

import cc.kave.commons.model.events.completionevents.Context;
import cc.kave.commons.model.ssts.ISST;

public class StreamFilterGenerator extends StreamRepoGenerator{

	private int numbGeneratedMethods = 0;

	public void add(Context ctx) {
		ISST sst = ctx.getSST();
		if (isGenerated(sst)) {
			numbGeneratedMethods += sst.getMethods().size();
		} else {
			sst.accept(new EventStreamGenerationVisitor(), ctx.getTypeShape());
		}
	}
	
	public int getNumbGeneratedMethods() {
		return numbGeneratedMethods;
	}
	
	private boolean isGenerated(ISST sst) {
		if (sst.isPartialClass()) {
			String fileName = sst.getPartialClassIdentifier();
			if (fileName.contains(".designer")
					|| fileName.contains(".Designer")) {
				return true;
			}
		}
		return false;
	}
}
