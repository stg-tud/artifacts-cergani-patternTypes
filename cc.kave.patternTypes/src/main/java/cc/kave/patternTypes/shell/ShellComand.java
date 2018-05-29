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
package cc.kave.patternTypes.shell;

import static cc.recommenders.assertions.Asserts.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ShellComand {

	private File rootFolder;
	private File eventsFolder;

	@Inject
	public ShellComand(@Named("root") File rf, @Named("events") File ef) {
		assertTrue(rf.exists(), "Root folder does not exist");
		assertTrue(rf.isDirectory(), "Root is not a folder, but a file");

		assertTrue(ef.exists(), "Events folder does not exist");
		assertTrue(ef.isDirectory(), "Events is not a folder, but a file");

		this.rootFolder = rf;
		this.eventsFolder = ef;
	}

	public void execute(int frequency, double entropy, int breaker) {

		String command = "cp " + eventsFolder.getAbsolutePath();
		command += "/freq" + frequency + "/stream.txt ";
		command += rootFolder.getAbsolutePath() + "/n-graph-miner";

		runCommand(command);

		command = "cd " + rootFolder.getAbsolutePath() + "/n-graph-miner";
		runCommand(command);
		
		command = "cd " + rootFolder.getAbsolutePath() + "/n-graph-miner\nls";
		
		String output = runCommand(command);
		
		System.out.println(output);

//		command = "./n_graph_miner stream.txt " + frequency + " " + entropy
//				+ " " + breaker + " episodes.txt";
//
//		output = runCommand(command);
//
//		System.out.println(output);
	}

	private String runCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}
}
