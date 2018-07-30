package ru.itu.predictools.index;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.itu.predictools.metric.Metric;
import ru.itu.predictools.registry.DictionaryEntry;

import java.util.stream.Collectors;


public class FuzzySearchTest {
    private static final String PATH = System.getProperty("user.dir") + "\\";
    private static final String DICTIONARY_FILE = "dictionary.txt";
    private static final int MAX_DISTANCE = 1;
    private static final int N_GRAM_N = 1;

    private IndexPrefixTrie trie;
    private Index nGram;
    private Metric metric;



    @Before
    public void setUp() throws Exception {
//        Dictionary dictionary = new Dictionary(new AlphabetRussian(), PATH + DICTIONARY_FILE);
//        metric = new LevensteinMetric(dictionary.maxWordLength());

        System.out.println("Dictionary file: ");
//        System.out.println(" - contains " + dictionary.getEntries().size() + " words");

        long startTime = System.currentTimeMillis();
//        trie = new IndexPrefixTrie(dictionary);
        long endTime = System.currentTimeMillis();
        System.out.println("Total entries count " + trie.getEntriesCount());
        System.out.println("Prefix trie index creation time: " + (double) (endTime - startTime) / 1000 + " с");

        startTime = System.currentTimeMillis();
//        nGram = new IndexNGram(dictionary, N_GRAM_N);
        endTime = System.currentTimeMillis();
        System.out.println("N-Gram Method index creation time: " + (double) (endTime - startTime) / 1000 + " с");
        System.out.println();
    }

    @After
    public void tearDown(){ }

    @Test
    public void checkPrefixTrie(){
        //TODO testing cycle for each combination of {dist; n-length}
        System.out.println("======PREFIX TRIE TESTING======");
        System.out.println("++++++add strings into trie++++++");

        System.out.println();
        System.out.println("\"Total distinct getDictionary entries in the beginning is " +
                trie.getDictionary().getEntries().stream().collect(Collectors.groupingBy(DictionaryEntry::getWord)).size());
        System.out.println("\"Total entries count while filling trie before adding some new words " + trie.getEntriesCount());
        trie.insertEntry(new DictionaryEntry("Что"));
        System.out.println("\"Что\" added, node count is " + trie.getNodesCount());
        trie.insertEntry(new DictionaryEntry("человечки"));
        System.out.println("\"человечки\" added, node count is " + trie.getNodesCount());
        trie.insertEntry(new DictionaryEntry("чего"));
        System.out.println("\"чего\" added, node count is " + trie.getNodesCount());
        trie.insertEntry(new DictionaryEntry("пригорюнились"));
        System.out.println("\"пригорюнились\" added, node count is " + trie.getNodesCount());
        trie.insertEntry(new DictionaryEntry("поговорим"));
        System.out.println("\"поговорим\" added, node count is " + trie.getNodesCount());
        System.out.println("\"Total entries count after adding words is " + trie.getEntriesCount());
        System.out.println();

        System.out.println("======PREFIX TRIE TESTING======");

        String[] testWords = new String[]{"вода","333","1ад","в2от"};
        long startTime = System.currentTimeMillis();
        for (String testWord: testWords) {
            System.out.println("++++++print some subtrees++++++");
            System.out.println("indexPrefixTrie.getDescendants(prefix)");
            System.out.println();
            System.out.println(" - префикс \"" + testWord + "\"");
            trie.getDescendants(testWord).stream()
                    .sorted((f1, f2) -> Long.compare(f2.getFrequency(), f1.getFrequency()))
                    .map(e -> e.getWord() + " F:" + e.getFrequency())
                    .forEach(System.out::println);
            long endTime = System.currentTimeMillis();
            long durationGetSubtree = endTime - startTime; //works well but seeks only from first symbol of getDictionary word

            System.out.println();
            System.out.println("trie.search(word, prefixSearch)");

            startTime = System.currentTimeMillis();
            System.out.println(" - префикс \"" + testWord + "\"");
            //noinspection RedundantStreamOptionalCall
            trie.search(testWord, MAX_DISTANCE, metric, true)
                    .stream()
                    .sorted((f1, f2) -> Long.compare(f2.getFrequency(), f1.getFrequency()))
                    .sorted((d1, d2) -> Integer.compare(d1.getDistance(), d2.getDistance()))
                    .map(e -> e.getWord() + " D:" + e.getDistance() + " F:" + e.getFrequency())
                    .forEach(System.out::println);// , MAX_DISTANCE, PREFIX_SEARCH
            //TODO compare performance .stream.sorted(...) with putting search result (SearchResultEntries) in SortedSet
            endTime = System.currentTimeMillis();
            long durationTrieSearchWPrefix = endTime - startTime;

            System.out.println();
            System.out.println("trie.search(word) not prefix");

            startTime = System.currentTimeMillis();
            System.out.println(" - префикс \"" + testWord + "\"");
            trie
                    .search(testWord, MAX_DISTANCE, metric)
                    .stream()
                    .sorted((f1, f2) -> Long.compare(f2.getFrequency(), f1.getFrequency()))
                    .sorted((d1, d2) -> Integer.compare(d1.getDistance(), d2.getDistance()))
                    .map(e -> e.getWord() + " D:" + e.getDistance() + " F:" + e.getFrequency())
                    .forEach(System.out::println);// , MAX_DISTANCE, PREFIX_SEARCH
            endTime = System.currentTimeMillis();
            long durationTrieSearchWOPrefix = endTime - startTime;

            System.out.println();
            System.out.println("Prefix Trie indexPrefixTrie.getDescendants(prefix) method search time: " + (double) durationGetSubtree / 1000 + " с");
            System.out.println("trie prefix search method search time: " + (double) durationTrieSearchWPrefix / 1000 + " с");
            System.out.println("trie search method search time: " + (double) durationTrieSearchWOPrefix / 1000 + " с");
            System.out.println("Prefix Trie trie.search(prefix) Method search Time: " + (double) (endTime - startTime) / 1000 + " с");
        }
    }
    //TODO Try with different inputs and different combinations of input params
    //TODO     private static final int MAX_DISTANCE = 0-3;
    //TODO     private static final int N_GRAM_N = 0-4;
    //TODO     private static final boolean PREFIX_SEARCH = true/false;


    @Test
    public void checkNGramSearch() throws InterruptedException {
//        Set<String> result = new HashSet<>();

        long startTime = System.currentTimeMillis();
        System.out.println("======N-GRAM TESTING======");
        String[] testWords = new String[]{"вода","333","1ад","в2от"};
        for (String testWord: testWords) {
            System.out.println("++++++print prefix search result++++++");
            System.out.println(" - префикс \""+testWord+"\"");
            nGram.search(testWord, MAX_DISTANCE, metric, true).stream()
                    .sorted((f1, f2) -> Long.compare(f2.getFrequency(), f1.getFrequency()))
                    .sorted((d1, d2) -> Integer.compare(d1.getDistance(), d2.getDistance()))
                    .map(e -> e.getWord() + " D:" + e.getDistance() + " F:" + e.getFrequency())
                    .forEach(System.out::println);
            long endTime = System.currentTimeMillis();
            long prefixSearchDuration = endTime - startTime;

            startTime = System.currentTimeMillis();
            System.out.println("++++++print search result++++++");
            System.out.println(" - префикс \""+testWord+"\"");
            nGram.search(testWord, MAX_DISTANCE, metric).stream()
                    .sorted((f1, f2) -> Long.compare(f2.getFrequency(), f1.getFrequency()))
                    .sorted((d1, d2) -> Integer.compare(d1.getDistance(), d2.getDistance()))
                    .map(e -> e.getWord() + " D:" + e.getDistance() + " F:" + e.getFrequency())
                    .forEach(System.out::println);// , MAX_DISTANCE, PREFIX_SEARCH
            endTime = System.currentTimeMillis();

            System.out.println();
            System.out.println("N-Gram Method Prefix Search Time: " + (double) prefixSearchDuration / 1000 + " с");
            System.out.println("N-Gram Method Search Time: " + (double) (endTime - startTime) / 1000 + " с");
            System.out.println();
        }
//        System.out.println((int) log(33));
//        System.out.println(log(33));

    }

/*
    Test
    public void initialState() {
    }
    Test
    public void checkLowerBound() {
    }
    @Test
    public void checkUpperBound() {
    }
    @Test
    public void tryMetric() {
    }
*/

}
