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
package exec.patternTypes;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import cc.kave.patternTypes.mining.evaluation.Consistency;
import cc.kave.patternTypes.mining.evaluation.Expressiveness;
import cc.kave.patternTypes.model.EpisodeType;
import cc.kave.patternTypes.shell.ShellComand;
import cc.recommenders.io.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class run_me {

	private static final String PROPERTY_NAME = "episodeFolder";
	private static final String PROPERTY_FILE = "patternTypes.properties";

	private static final int FREQUENCY = 300;
	private static final double ENTROPY = 0.001;

	private static final int FTH = 345;
	private static final double ETH = 0.72;

	private static final int METHODSIZE = 5000;

	private static Injector injector;

	public static void main(String[] args) throws Exception {
		initLogger();
		printAvailableMemory();

		String rootFolder = readPropertyFromFile(PROPERTY_NAME);
		injector = Guice.createInjector(new Module(rootFolder));

		Logger.append("\n");
		Logger.log("started: %s\n", new Date());

		// Generating the event stream from SSTs representation
		// load(Preprocessing.class).run(FREQUENCY);

		// mining episodes using the episode mining algorithm
		// load(ShellComand.class).execute(EpisodeType.GENERAL, FREQUENCY,
		// ENTROPY, METHODSIZE);

		// Running the evluations
		// load(Expressiveness.class).calculate(FREQUENCY, FTH, ETH);
		load(Consistency.class).calculate(FREQUENCY, FTH, ETH);

		Logger.log("done");
	}

	private static void initLogger() {
		Logger.setPrinting(true);
		Logger.setDebugging(false);
		Logger.setCapturing(false);
	}

	private static void printAvailableMemory() {
		long maxMem = Runtime.getRuntime().maxMemory();
		float maxMemInMb = Math.round(maxMem * 1.0d / (1024 * 1024 * 1.0f));
		Logger.log("maximum memory (-Xmx): %.0f MB", maxMemInMb);
	}

	private static String readPropertyFromFile(String propertyName) {
		try {
			Properties properties = new Properties();
			properties.load(new FileReader(PROPERTY_FILE));
			String property = properties.getProperty(propertyName);
			if (property == null) {
				throw new RuntimeException("property '" + propertyName
						+ "' not found in properties file");
			}
			Logger.log("%s: %s", propertyName, property);

			return property;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T load(Class<T> clazz) {
		return injector.getInstance(clazz);
	}

}