package ru.itu.predictools.index;

import ru.itu.predictools.metric.Metric;
import ru.itu.predictools.alphabet.Alphabet;
import ru.itu.predictools.registry.SearchDictionary;
import ru.itu.predictools.registry.SearchDictionaryEntry;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IndexNGram extends WordIndex {
  private static final Logger LOGGER = LogManager.getLogger();
  private static final long serialVersionUID = 1L;//todo>> consider serialization
  private final int[][] ngramMap;
  private final int n;
//  private long wordsCount, nodesCount;
  
  //TODO try this algorithm for different combination of {distance, n} to performance evaluation
  public IndexNGram(SearchDictionary searchDictionary, int n) {//n - n-gram length
    super(searchDictionary, searchDictionary.getAlphabet());
    LOGGER.info("Building of n-gram index has been started...");
    if (n < 1) {
      n = 1;
    }
    this.n = n;
//    nodesCount = wordsCount = 0;
    
    //let's create ngramMap index
    Double mapLength; //mapLength - number of all n-length-words from size()-length-alphabet
    mapLength = Math.pow((double) alphabet.size(), (double) n);
    
    int[] ngramCountMap = new int[mapLength.intValue()];//array of counters for each kind of n-gram
    
    String word;
    
    LOGGER.info("Counting of n-grams has been started...");
    for (SearchDictionaryEntry entry : searchDictionary.getSearchDictionaryEntries()) {//for each entry of getDictionary
      word = entry.getWord();
//      wordsCount++;//words counter
      for (int k = 0; k < word.length() - n + 1; ++k) {
        int ngram = IndexNGram.getNGram(alphabet, word, k, n);
//        if (ngramCountMap[ngram] == 0) nodesCount++;//unique n-gram counter
        ++ngramCountMap[ngram];//current n-gram counter
      }
    }
    LOGGER.info("Counting of n-grams has been finished...");
    
    ngramMap = new int[mapLength.intValue()][];//n-gram map (n-gram index)
    
    LOGGER.info("Filling up of the n-gram map has been started...");
    for (int i = 0; i < searchDictionary.getSearchDictionaryEntries().size(); ++i) {
      word = searchDictionary.getSearchDictionaryEntries().get(i).getWord();
      for (int k = 0; k < word.length() - n + 1; ++k) {
        int ngram = IndexNGram.getNGram(alphabet, word, k, n);
        if (ngramMap[ngram] == null) ngramMap[ngram] = new int[ngramCountMap[ngram]];
        ngramMap[ngram][--ngramCountMap[ngram]] = i;
      }
    }
    LOGGER.info("Filling up of the n-gram map has been finished...");
    LOGGER.info("N-gram index has been built...");
  }
  
  private static int getNGram(Alphabet alphabet, CharSequence string, int start, int n) {
    int ngram = 0;
    for (int i = start; i < start + n; ++i)
      if (alphabet.isAlphabetChar(string.charAt(i)))//if symbol is not alphabetic then it will be ignored
        //ngram is represented as a number in a positional number system with a base equal to the length of the alphabet
        //(the calculation is performed in decimal)
        ngram = ngram * alphabet.size() + alphabet.mapChar(string.charAt(i));
    return ngram;
  }
  
  @Override
  public Set<SearchDictionaryEntry> search(String pattern) {
    return search(pattern, 0, null);
  }
  
  @Override
  public Set<SearchDictionaryEntry> search(String pattern, int distance, Metric metric) {
    return search(pattern, distance, metric, false);
  }
  
  @Override
  public Set<SearchDictionaryEntry> search(String searchPattern, int distance, Metric metric, boolean prefixSearch) {
    Set<SearchDictionaryEntry> set = new HashSet<>();
//        string=string.toUpperCase();
    
    SearchDictionaryEntry entry;
    SearchDictionaryEntry resultEntry;
    String word;
    int currentDistance;
    
    LOGGER.info("Searching of {} has been started ...", searchPattern);
    for (int i = 0; i < searchPattern.length() - n + 1; ++i) {
      int ngram = IndexNGram.getNGram(alphabet, searchPattern, i, n);
      
      int[] dictIndexes = ngramMap[ngram];
      
      if (dictIndexes != null)
        for (int k : dictIndexes) {
          entry = searchDictionary.getSearchDictionaryEntries().get(k);
          word = entry.getWord();
          if (metric != null) {
            currentDistance = metric.getDistance(word, searchPattern, distance, prefixSearch);
            if (currentDistance <= distance) {
              resultEntry = new SearchDictionaryEntry(
                  entry.getWord(),
                  entry.getFrequency(),
                  entry.getLocalFrequency(),
                  entry.getLastUseTime(),
                  currentDistance/*/word.length()*/
              );//TODO distance - absolute or relative???
              set.add(resultEntry);
            }
          } else if (searchPattern.equals(word)) {//if there is no metric selected then will search the exact coincidences
            set.add(entry);
            return set;//break the cycle, because the dictionary is a set of unique words and so there is impossible
            // to find more than one exact coincidence
          }
        }
    }
    LOGGER.info("Searching of {} has been finished ...", searchPattern);
    return set;
  }

//  @Override
//  public long getEntriesCount() {
//    return wordsCount;
//  }

//  @Override
//  public long getNodesCount() {
//    return nodesCount;
//  }
}
