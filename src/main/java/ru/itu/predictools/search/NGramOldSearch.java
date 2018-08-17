package ru.itu.predictools.search;

import ru.itu.predictools.index.IndexNGram;
import ru.itu.predictools.registry.SearchDictionaryEntry;

import java.io.IOException;
import java.util.Set;

public class NGramOldSearch extends OldSearch {
    private Integer ngramN;
    private IndexNGram nGram;

    public NGramOldSearch(String dictionaryPath, Integer distance, Integer N, Integer resultingListLength) throws IOException {
        super(dictionaryPath, distance, resultingListLength);
        long startTime, endTime;
        ngramN = N;
//        startTime = System.currentTimeMillis();
        nGram = new IndexNGram(searchDictionary, ngramN);
//        endTime = System.currentTimeMillis();

//        System.out.println("N-Gram Method index creation time: " + (double) (endTime - startTime) / 1000 + " с");
//        System.out.println();
    }

    public void setN(Integer N) {
        ngramN = N;
        nGram = new IndexNGram(searchDictionary, ngramN);
    }

    @Override
    public Set<SearchDictionaryEntry> run(String template, boolean prefixMode) {
//        super.run(template, prefixMode);
        //noinspection RedundantStreamOptionalCall
        return (Set<SearchDictionaryEntry>) nGram.search(template, maxDistance, metric, prefixMode).stream()
                .sorted((f1, f2) -> Double.compare(f2.getFrequency(), f1.getFrequency()))
                .sorted((d1, d2) -> Double.compare(d1.getDistance(), d2.getDistance()))
                .limit(resultLength)
            //.map(e -> e.getWord() + " D:" + e.getDistance() + " F:" + e.getFrequency())
                //.forEach(System.out::println)
                ;
    }
}
