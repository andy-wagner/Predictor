package ru.itu.predictools.index;

import ru.itu.predictools.metric.Metric;
import ru.itu.predictools.alphabet.Alphabet;
import ru.itu.predictools.registry.SearchDictionary;
import ru.itu.predictools.registry.SearchDictionaryEntry;

import java.util.HashSet;
import java.util.Set;

public class IndexNGram extends WordIndex {
  private static final long serialVersionUID = 1L;//todo>> consider serialization
  private final int[][] ngramMap;
  private final int n;
  private long wordsCount, nodesCount;
  
  //TODO try this algorithm for different combination of {distance, n} to performance evaluation
  public IndexNGram(SearchDictionary searchDictionary, int n) {//n - n-gram length
    super(searchDictionary, searchDictionary.getAlphabet());
    if (n < 1) {
      n = 1;
    }
    this.n = n;
    nodesCount = wordsCount = 0;
    
    //let's create ngramMap index
    Double mapLength; //mapLength - number of all n-length-words from size()-length-alphabet
    mapLength = Math.pow((double) alphabet.size(), (double) n);
    
    int[] ngramCountMap = new int[mapLength.intValue()];//array of counters for each kind of n-gram
    
    String word;
    
    for (SearchDictionaryEntry entry : searchDictionary.getEntries()) {//for each entry of getDictionary
      word = entry.getWord();
      wordsCount++;//words counter
      for (int k = 0; k < word.length() - n + 1; ++k) {
        int ngram = IndexNGram.getNGram(alphabet, word, k, n);
        if (ngramCountMap[ngram] == 0) nodesCount++;//unique n-gram counter
        ++ngramCountMap[ngram];//current n-gram counter
      }
    }
    
    ngramMap = new int[mapLength.intValue()][];//n-gram map (n-gram index)
    
    for (int i = 0; i < searchDictionary.getEntries().size(); ++i) {
      word = searchDictionary.getEntries().get(i).getWord();
      for (int k = 0; k < word.length() - n + 1; ++k) {
        int ngram = IndexNGram.getNGram(alphabet, word, k, n);
        if (ngramMap[ngram] == null) ngramMap[ngram] = new int[ngramCountMap[ngram]];
        ngramMap[ngram][--ngramCountMap[ngram]] = i;
      }
    }
    
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
  public void insertEntry(SearchDictionaryEntry entry) {//todo>> make insertEntry into ngram index (when new word added to dictionary)
/*
        String word = entry.getWord();
        for (int k = 0; k < word.length() - n + 1; ++k) {
            int ngram = getNGram(alphabet, word, k, n);
            ngramMap[ngram][ngramMap[ngram].length+1]
            ++ngramCountMap[ngram];//current n-gram counter
        }
*/
  }
  
  @Override
  public Set<SearchDictionaryEntry> search(String pattern) {
    return search(pattern, 0, null);
  }
  
  @Override
  public Set<SearchDictionaryEntry> search(String pattern, int distance, Metric metric) {
    return search(pattern, distance, metric, false);
  }//TODO need to clarify type of collection used for subtree Set||Map||Ordered...<Entry||SearchDictionaryEntry>
  
  @Override//TODO need to clarify type of collection used for subtree Set||Map||Ordered...<Entry||SearchDictionaryEntry>
  public Set<SearchDictionaryEntry> search(String searchPattern, int distance, Metric metric, boolean prefixSearch) {
    Set<SearchDictionaryEntry> set = new HashSet<>();
//        string=string.toUpperCase();
    
    SearchDictionaryEntry entry;
    SearchDictionaryEntry resultEntry;
    String word;
    int currentDistance;
    
    for (int i = 0; i < searchPattern.length() - n + 1; ++i) {
      int ngram = IndexNGram.getNGram(alphabet, searchPattern, i, n);
      
      int[] dictIndexes = ngramMap[ngram];
      
      if (dictIndexes != null)
        for (int k : dictIndexes) {
          entry = searchDictionary.getEntries().get(k);
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
    return set;
  }
  
  @Override
  public long getEntriesCount() {
    return wordsCount;
  }
  
  @Override
  public long getNodesCount() {
    return nodesCount;
  }
}
