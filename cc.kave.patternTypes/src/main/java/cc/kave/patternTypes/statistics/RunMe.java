package cc.kave.patternTypes.statistics;

import java.util.concurrent.ExecutionException;

public class RunMe {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// String path = "/Users/seb/some-contexts/";
		String path = "/Users/ervinacergani/Documents/EpisodeMining/dataSet/SSTs/";
		EventStreamGenerator2 gen = new EventStreamGenerator2(path);
		gen.run();
	}
}
  