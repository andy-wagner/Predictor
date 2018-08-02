package ru.itu.predictools.search;

import ru.itu.predictools.metric.LevensteinMetric;
import ru.itu.predictools.metric.Metric;
import ru.itu.predictools.registry.SearchDictionary;
import ru.itu.predictools.registry.SearchResultEntry;

import java.io.IOException;
import java.util.Set;

public abstract class Search {
    @SuppressWarnings("WeakerAccess")
    protected int maxDistance, resultLength;
    protected SearchDictionary searchDictionary;
    protected Metric metric;

    Search(String dictionaryPath, Integer distance, Integer resultingListLength) throws IOException {
        this.maxDistance = distance;
        this.resultLength = resultingListLength;
//        this.searchDictionary = new SearchDictionary(new AlphabetRussian(), dictionaryPath);
        this.metric = new LevensteinMetric(searchDictionary.getMaxWordLength());

        System.out.println("SearchDictionary file contains " + searchDictionary.getEntries().size() + " words");

    }

    public void setResultLength(int resultLength) {
        this.resultLength = resultLength;
    }

    public int getResultLength(){
        return this.resultLength;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public int getMaxDistance(){
        return this.maxDistance;
    }

    public abstract Set<SearchResultEntry> run(String template, boolean prefixMode);

}
