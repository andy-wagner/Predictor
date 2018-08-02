package ru.itu.predictools;

import ru.itu.predictools.metric.Metric;
import ru.itu.predictools.alphabet.Alphabet;
import ru.itu.predictools.registry.Entry;
import ru.itu.predictools.registry.SearchDictionary;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class Predictor {
  protected int maxDistance, resultLength;
  protected SearchDictionary searchDictionary;
  protected Metric metric;
  private Map<String, Set<Character>> specialSymbolsSet;//<NameOfCharactersSet, CharactersSet>
  
  public Predictor(String dictionaryPath, Integer maxDistance, Integer resultListLength) throws IOException {
//        this(dictionaryPath, maxDistance, resultListLength, );
  }
  
  public Predictor(String dictionaryPath, Integer maxDistance, Integer resultListLength, String metricName) throws IOException {
    this.maxDistance = maxDistance;
    this.resultLength = resultListLength;
//        this.searchDictionary = new SearchDictionary(new AlphabetRussian(), dictionaryPath);
//    this.metric = new LevensteinMetric(searchDictionary.geMaxWordLength());
    this.metric = metric;//get metric type by metricName

//        System.out.println("SearchDictionary file contains " + searchDictionary.getEntries().size() + " words");
  
  }
  
  //PUBLIC METHODS
  
  public void setResultLength(int resultLength) {
      this.resultLength = resultLength;
  }
  
  public int getResultLength() {
    return this.resultLength;
  }
  
  public void setMaxDistance(int maxDistance) {
      this.maxDistance = maxDistance;
  }
  
  public int getMaxDistance() {
    return this.maxDistance;
  }
  
  public String getLanguage() {
    return "ru";
  }
  
  public void setLanguage(String language) {//todo>> stub code
  }
  
  public Alphabet getAlphabet() {
//        return set of all this.searchDictionary letters converted into alphabet object
    return this.searchDictionary.getAlphabet();
  }
  
  public static Alphabet getAlphabet(SearchDictionary searchDictionary) {
//        return set of all searchDictionary letters converted into alphabet object
    return searchDictionary.getAlphabet();
  }
  
  public Alphabet getReducedAlphabet(String pattern) {
    try {
      return null;//todo>>return index.getReducedAlphabet(pattern)
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
  
  public void saveAlphabet(String alphabetFileNmae) {//todo>> stub code
//      saves this.searchDictionary.alphabet into the file
  }
  
  public void saveAlphabet(Alphabet alphabet, String alphabetFileNmae) {
//        saves alphabet into the file
  }
  
  public Alphabet setAlphabet() {//get alphabet of selected searchDictionary and then set it to this.dictiomany.alphabet
    try {
      Alphabet alphabet = this.searchDictionary.getAlphabet();
      this.searchDictionary.setAlphabet(alphabet);
      return alphabet;
    } catch (IllegalArgumentException e) {
      return null;
    }
    
  }
  
  public void setAlphabet(String alphabetFileName) {//set this.searchDictionary.alphabet into alphabet loaded from specified file
  }
  
  public Alphabet getSymbolsSet(Integer setContentFlags) {
    try {
//        setContentFlags is bit-field where each bit is flag of symbols subset assigned with addSymbolsSubset(..) function
      return null;//todo>> return sum of subsets of this.specialSymbolsSet converted to alphabet
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
  
  public void addSymbolsSubset(Set<Character> symbols, Integer flag) {
  }
  
  public void addDictionaryEntry(Entry entry) {
  }
  
  public void loadDictionary(String dictionaryFileName) {
  }
  
  public void saveDictionary(String dictionaryFileName) {
  }
  
//  public SearchDictionary margeDictionaries(SearchDictionary dictionary1, SearchDictionary dictionary2){
//    return new SearchDictionary();
//  }
}
