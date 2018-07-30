package ru.itu.predictools.search;

import ru.itu.predictools.index.IndexNGram;
import ru.itu.predictools.registry.SearchResultEntry;

import java.io.IOException;
import java.util.Set;

public class NGramSearch extends Search {
    private Integer ngramN;
    private IndexNGram nGram;

    public NGramSearch(String dictionaryPath, Integer distance, Integer N, Integer resultingListLength) throws IOException {
        super(dictionaryPath, distance, resultingListLength);
        long startTime, endTime;
        ngramN = N;
//        startTime = System.currentTimeMillis();
        nGram = new IndexNGram(dictionary, ngramN);
//        endTime = System.currentTimeMillis();

//        System.out.println("N-Gram Method index creation time: " + (double) (endTime - startTime) / 1000 + " с");
//        System.out.println();
    }

    public void setN(Integer N) {
        ngramN = N;
        nGram = new IndexNGram(dictionary, ngramN);
    }

    @Override
    public Set<SearchResultEntry> run(String template, boolean prefixMode) {
//        super.run(template, prefixMode);
        //noinspection RedundantStreamOptionalCall
        return (Set<SearchResultEntry>) nGram.search(template, maxDistance, metric, prefixMode).stream()
                .sorted((f1, f2) -> Long.compare(f2.getFrequency(), f1.getFrequency()))
                .sorted((d1, d2) -> Long.compare(d1.getDistance(), d2.getDistance()))
                .limit(resultLength)
            //.map(e -> e.getWord() + " D:" + e.getDistance() + " F:" + e.getFrequency())
                //.forEach(System.out::println)
                ;
    }
}
