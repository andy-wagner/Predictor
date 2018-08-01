package ru.itu.predictools.search;

import ru.itu.predictools.metric.LevensteinMetric;
import ru.itu.predictools.metric.Metric;
import ru.itu.predictools.registry.Dictionary;
import ru.itu.predictools.registry.SearchResultEntry;

import java.io.IOException;
import java.util.Set;

public abstract class Search {
    @SuppressWarnings("WeakerAccess")
    protected int maxDistance, resultLength;
    protected Dictionary dictionary;
    protected Metric metric;

    Search(String dictionaryPath, Integer distance, Integer resultingListLength) throws IOException {
        this.maxDistance = distance;
        this.resultLength = resultingListLength;
//        this.dictionary = new Dictionary(new AlphabetRussian(), dictionaryPath);
        this.metric = new LevensteinMetric(dictionary.geMaxWordLength());

        System.out.println("Dictionary file contains " + dictionary.getEntries().size() + " words");

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
