package io.github.jadefalke2;

import io.github.jadefalke2.model.Crawler;
import picocli.CommandLine;

import static picocli.CommandLine.*;

public class WebCrawlerApplication implements Runnable {

	@Parameters(description = "The url to crawl")
	private String url;

	@Option(names = {"--depth", "-d"}, description = "The depth to crawl for", defaultValue = "3")
	private int maxDepth;

	
	private final Crawler crawler;

	public WebCrawlerApplication () {
		crawler = new Crawler();
	}

	@Override
	public void run () {
		var res = crawler.crawl(url, maxDepth);
		System.out.println(res);
	}


	public static void main (String[] args) {
		var cmd = new CommandLine(new WebCrawlerApplication());
		int exitCode = cmd.execute(args);
		System.exit(exitCode);
	}
}
