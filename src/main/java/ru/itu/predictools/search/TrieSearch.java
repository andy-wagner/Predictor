package ru.itu.predictools.search;

import ru.itu.predictools.index.IndexPrefixTrie;
import ru.itu.predictools.registry.SearchDictionaryEntry;

import java.io.IOException;
import java.util.Set;

public class TrieSearch extends Search {
    private IndexPrefixTrie trie;

    public TrieSearch(String dictionaryPath, Integer distance, Integer resultingListLength) throws IOException {
        super(dictionaryPath, distance, resultingListLength);
        long startTime, endTime;
        startTime = System.currentTimeMillis();
        trie = new IndexPrefixTrie(searchDictionary);
        endTime = System.currentTimeMillis();

        System.out.println("Total entries count " + trie.getEntriesCount());
        System.out.println("Prefix trie index creation time: " + (double) (endTime - startTime) / 1000 + " с");

    }

    @Override
    public Set<SearchDictionaryEntry> run(String template, boolean prefixMode) {
//        super.run(template, prefixMode);
        //noinspection RedundantStreamOptionalCall
        return (Set<SearchDictionaryEntry>) trie.search(template, maxDistance, metric, prefixMode)
                .stream()
                .sorted((f1, f2) -> Double.compare(f2.getFrequency(), f1.getFrequency()))
                .sorted((d1, d2) -> Double.compare(d1.getDistance(), d2.getDistance()))
                .map(e -> e.getWord() + " D:" + e.getDistance() + " F:" + e.getFrequency())
                .limit(resultLength)
            ;//.forEach(System.out::println);
    }

}
