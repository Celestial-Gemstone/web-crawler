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

	private final Executor pooledExecutor;

	public Crawler () {
		this.pooledExecutor = Executors.newCachedThreadPool();
	}

	public Set<String> crawl (String startingURL, int depth) {
		Set<String> acc = Collections.newSetFromMap(new ConcurrentHashMap<>());
		crawl(startingURL, acc, depth, 0);
		return acc;
	}

	private void crawl (String startingURL, Set<String> accumulator, int maxDepth, int depth) {
		accumulator.add(startingURL);
		if (depth < maxDepth) {
			getURLs(startingURL)
				.forEach(url -> pooledExecutor.execute(() -> crawl(url, accumulator, maxDepth, depth + 1)));
		}
	}

	private Set<String> getURLs (String url) {
		return requestPage(url)
			.map(doc -> doc.select("a[href]"))
			.stream()
			.map(l -> l.attr("abc:href"))
			.filter(this::isValid)
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

	private boolean isValid (String url) {
		return !url.isEmpty();
	}
}
