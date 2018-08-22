package ru.itu.predictor.index;

import org.junit.Before;
import org.junit.Test;
import ru.itu.predictor.metric.LevensteinMetric;
import ru.itu.predictor.registry.SearchDictionary;
import ru.itu.predictor.registry.SearchDictionaryEntry;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class IndexPrefixTrieTests {
  private IndexPrefixTrie index;
  private SearchDictionary dictionary;
//  private String MAIN_DICTIONARY_PATH, USER_WORDS_DICTIONARY_PATH, USER_PHRASES_DICTIONARY_PATH;
  
  @Before
  public void init() {
    String MAIN_DICTIONARY_PATH, USER_WORDS_DICTIONARY_PATH, USER_PHRASES_DICTIONARY_PATH;
    MAIN_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                               + "dictionaries" + File.separator
                               + "ru-main-test(do not change this file)-utf8.dic";
    USER_WORDS_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                                     + "user" + File.separator
                                     + "dictionaries" + File.separator
                                     + "ru-user-words-test(do not change this file)-utf8.dic";
    USER_PHRASES_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                                       + "user" + File.separator
                                       + "dictionaries" + File.separator
                                       + "ru-user-phrases-test(do not change this file)-utf8.dic";
    dictionary = new SearchDictionary(
        MAIN_DICTIONARY_PATH,
        USER_WORDS_DICTIONARY_PATH,
        USER_PHRASES_DICTIONARY_PATH
    );
    index = new IndexPrefixTrie(dictionary);
  }
  
  @Test
  public void checkPrefixTrieIndexInstantiating() {
    Set<SearchDictionaryEntry> entries =
        index.search(
            "хоккей"
            , 1
            , new LevensteinMetric(dictionary.getMaxStringLength())
            , false);
//    System.out.println("entries: " + index.getEntriesCount()
//                           + "; index nodes: " + index.getNodesCount()
//                           + "; result strings: " + entries.size());
    for (SearchDictionaryEntry entry : entries) {
      System.out.println(
          entry.getString()
              + " ч:" + entry.getFrequency()
              + " лч:" + entry.getLocalFrequency()
              + " пссл.время:" + entry.getLastUseTime()
      );
    }
  }
}
