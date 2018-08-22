package ru.itu.predictor.index;

import org.junit.Before;
import org.junit.Test;
import ru.itu.predictor.metric.LevensteinMetric;
import ru.itu.predictor.registry.SearchDictionary;
import ru.itu.predictor.registry.SearchDictionaryEntry;

import java.io.File;
import java.util.Comparator;
import java.util.Set;

public class IndexNGramTests{
  private IndexNGram index;
  private SearchDictionary dictionary;
//  private String MAIN_DICTIONARY_PATH, USER_WORDS_DICTIONARY_PATH, USER_PHRASES_DICTIONARY_PATH;
  
  @Before
  public void init() {
    String MAIN_DICTIONARY_PATH, USER_WORDS_DICTIONARY_PATH, USER_PHRASES_DICTIONARY_PATH;
    MAIN_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                               + "src" + File.separator
                               + "test" + File.separator
                               + "resources" + File.separator
                               + "dictionaries" + File.separator
                               + "ru-main-test(do not change this file)-utf8.dic";
    USER_WORDS_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                                     + "src" + File.separator
                                     + "test" + File.separator
                                     + "resources" + File.separator
                                     + "dictionaries" + File.separator
                                     + "user" + File.separator
                                     + "ru-user-words-test(do not change this file)-utf8.dic";
    USER_PHRASES_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                                       + "src" + File.separator
                                       + "test" + File.separator
                                       + "resources" + File.separator
                                       + "dictionaries" + File.separator
                                       + "user" + File.separator
                                       + "ru-user-phrases-test(do not change this file)-utf8.dic";
    dictionary = new SearchDictionary(
        MAIN_DICTIONARY_PATH,
        USER_WORDS_DICTIONARY_PATH,
        USER_PHRASES_DICTIONARY_PATH
    );
    index = new IndexNGram(dictionary, 2);
  }
  
  @Test
  public void checkNGramIndexInstantiating() {
    Set<SearchDictionaryEntry> entries =
        index.search(
            "хоккей "
            , 2
            , new LevensteinMetric(dictionary.getMaxStringLength())
            , true);
//    System.out.println("entries: " + index.getEntriesCount()
//                           + "; index nodes: " + index.getNodesCount()
//                           + "; result strings: " + entries.size());
    entries.stream()
        .sorted(Comparator
                    .comparingInt(SearchDictionaryEntry::getDistance)
                    .reversed()
                    .thenComparingDouble(SearchDictionaryEntry::getFrequency)
                    .thenComparingDouble(SearchDictionaryEntry::getLocalFrequency)
                    .reversed()
        )
        .map(e -> e.getString().toUpperCase()
                      + " --> расстояние:" + e.getDistance()
                      + " ч:" + e.getFrequency()
                      + " лч:" + e.getLocalFrequency()
                      + " пссл.время:" + e.getLastUseTime()
        )
        .forEach(System.out::println)
    ;
  }
}
