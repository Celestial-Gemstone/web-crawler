package io.github.jadefalke2.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Crawler {

	private final Executor threadPool;

	/**
	 * Constructs a new Crawler with a default cached thread pool
	 */
	public Crawler () {
		this.threadPool = Executors.newCachedThreadPool();
	}

	/**
	 * Crawl to the given depth from this url
	 * @param url the url to crawl
	 * @param depth the max-depth
	 * @return a set of pages obtained by crawling
	 */
	public Set<String> crawl (String url, int depth) {
		Set<String> acc = Collections.newSetFromMap(new ConcurrentHashMap<>());
		crawl(url, acc, depth, 0);
		return acc;
	}

	private void crawl (String url, Set<String> accumulator, int maxDepth, int depth) {
		accumulator.add(url);
		if (depth < maxDepth) {
			retrieveURLs(url)
				.forEach(newUrl -> threadPool.execute(() -> crawl(newUrl, accumulator, maxDepth, depth + 1)));
		}
	}

	private Set<String> retrieveURLs (String url) {
		return requestPage(url)
			.map(doc -> doc.select("a[href]"))
			.stream()
			.map(l -> l.attr("abc:href"))
			.filter(this::isValidURL)
			.collect(Collectors.toSet());
	}

	private Optional<Document> requestPage (String url) {
		try {
			return Optional.of(
				Jsoup.connect(url)
				     .followRedirects(true)
				     .ignoreHttpErrors(false)
				     .get()
			);
		} catch (IOException e) {
			return Optional.empty();
		}
	}

	private boolean isValidURL (String url) {
		return !url.isEmpty();
	}
}
