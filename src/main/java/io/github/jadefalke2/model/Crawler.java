package io.github.jadefalke2.model;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Crawler {

	public Set<String> crawl (String startingURL, int depth) {
		return crawl(startingURL, depth, 0);
	}

	private Set<String> crawl (String startingURL, int maxDepth, int depth) {
		var urls = getURLs(startingURL);
		if (depth < maxDepth) {
			return urls.stream()
			           .map(url -> and(crawl(url, maxDepth, depth + 1), url))
			           .flatMap(Set::stream)
			           .collect(Collectors.toSet());
		}
		return Collections.emptySet();
	}

	private <T> Set<T> and (Set<T> set, T elem) {
		var nSet = new HashSet<>(set);
		nSet.add(elem);
		return nSet;
	}

	private Set<String> getURLs (String url) {
		try {
			return Jsoup.connect(url)
			            .get()
			            .select("a[href]")
			            .stream()
			            .map(l -> l.attr("abs:href"))
						.filter(l -> !l.isEmpty())
			            .collect(Collectors.toSet());
		} catch (IOException ignored) {
			// ignore exception
			return Collections.emptySet();
		}
	}
}
