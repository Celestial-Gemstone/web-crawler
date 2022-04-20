package io.github.jadefalke2;

import picocli.CommandLine;

public class WebCrawlerApplication {

	public static void main (String[] args) {
		var cmd = new CommandLine(new WebCrawlerApplication());
		int exitCode = cmd.execute(args);
		System.exit(exitCode);
	}

}
