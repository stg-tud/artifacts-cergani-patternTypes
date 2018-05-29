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
import java.util.Set;

import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.commons.model.naming.types.ITypeName;
import cc.kave.patternTypes.model.events.Event;
import cc.recommenders.datastructures.Tuple;
import cc.recommenders.io.Logger;

import com.google.common.collect.Sets;

public class StreamStatistics {

	private Set<ITypeName> typeDecls = Sets.newHashSet();
	private Set<ITypeName> apis = Sets.newHashSet();
	private Set<IMethodName> ctxFirst = Sets.newHashSet();
	private Set<IMethodName> ctxSuper = Sets.newHashSet();
	private Set<IMethodName> ctxElement = Sets.newHashSet();
	private Set<IMethodName> invExpr = Sets.newHashSet();

	int numApis = 0;
	int numbFirst = 0;
	int numbSuper = 0;
	int numbElement = 0;
	int numbInv = 0;
	
	public void addStats(List<Tuple<Event, List<Event>>> eventStream) {
		for (Tuple<Event, List<Event>> tuple : eventStream) {
			IMethodName decl = tuple.getFirst().getMethod();
			ctxElement.add(decl);
			numbElement++;
			typeDecls.add(decl.getDeclaringType());

			for (Event event : tuple.getSecond()) {
				IMethodName methodName = event.getMethod();
				apis.add(methodName.getDeclaringType());
				numApis++;
				
				switch (event.getKind()) {
				case FIRST_DECLARATION:
					ctxFirst.add(methodName);
					numbFirst++;
					break;
				case SUPER_DECLARATION:
					ctxSuper.add(methodName);
					numbSuper++;
					break;
				case INVOCATION:
					invExpr.add(methodName);
					numbInv++;
					break;
				default:
					System.err.println("should not happen");
				}
			}
		}
	}
	
	public void printStats() {
		Logger.log("typeDecls:\t(%d unique)", typeDecls.size());
		Logger.log("apiTypes:\t%d (%d unique)", numApis, apis.size());
		Logger.log("ctxFirst:\t%d (%d unique)", numbFirst, ctxFirst.size());
		Logger.log("ctxSuper:\t%d (%d unique)", numbSuper, ctxSuper.size());
		Logger.log("ctxElement:\t%d (%d unique)", numbElement,
				ctxElement.size());
		Logger.log("invExpr:\t%d (%d unique)", numbInv, invExpr.size());
		Logger.log("------");
		int length = numbFirst + numbSuper + numbElement + numbInv;
		Logger.log("full stream:\t%d events (excl. types)\n", length);
	}
}
