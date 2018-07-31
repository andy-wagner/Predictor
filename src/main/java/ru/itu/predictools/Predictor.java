package ru.itu.predictools;

import ru.itu.predictools.metric.LevensteinMetric;
import ru.itu.predictools.metric.Metric;
import ru.itu.predictools.Alphabet.Alphabet;
import ru.itu.predictools.registry.Dictionary;
import ru.itu.predictools.registry.DictionaryEntry;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class Predictor {
  protected int maxDistance, resultLength;
  protected Dictionary dictionary;
  protected Metric metric;
  private Map<String, Set<Character>> specialSymbolsSet;//<NameOfCharactersSet, CharactersSet>
  
  public Predictor(String dictionaryPath, Integer maxDistance, Integer resultListLength) throws IOException {
//        this(dictionaryPath, maxDistance, resultListLength, );
  }
  
  public Predictor(String dictionaryPath, Integer maxDistance, Integer resultListLength, String metricName) throws IOException {
    this.maxDistance = maxDistance;
    this.resultLength = resultListLength;
//        this.dictionary = new Dictionary(new AlphabetRussian(), dictionaryPath);
    this.metric = new LevensteinMetric(dictionary.getMaxWordLength());
    this.metric = metric;//get metric type by metricName

//        System.out.println("Dictionary file contains " + dictionary.getEntries().size() + " words");
  
  }
  
  //PUBLIC METHODS
  
  public boolean setResultLength(int resultLength) {
    try {
      this.resultLength = resultLength;
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public int getResultLength() {
    return this.resultLength;
  }
  
  public boolean setMaxDistance(int maxDistance) {
    try {
      this.maxDistance = maxDistance;
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public int getMaxDistance() {
    return this.maxDistance;
  }
  
  public String getLanguage() {
    return "ru";
  }
  
  public boolean setLanguage(String language) {
    try {
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public Alphabet getAlphabet() {
//        return set of all this.dictionary letters converted into Alphabet object
    return this.dictionary.getAlphabet();
  }
  
  public static Alphabet getAlphabet(Dictionary dictionary) {
//        return set of all dictionary letters converted into Alphabet object
    return dictionary.getAlphabet();
  }
  
  public Alphabet getReducedAlphabet(String pattern) {
    try {
      return null;//todo>>return index.getReducedAlphabet(pattern)
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
  
  public boolean saveAlphabet(String alphabetFileNmae) {
    try {
//      saves this.dictionary.alphabet into the file
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public boolean saveAlphabet(Alphabet alphabet, String alphabetFileNmae) {
    try {
//        saves alphabet into the file
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public Alphabet setAlphabet() {//get alphabet of selected dictionary and then set it to this.dictiomany.alphabet
    try {
      Alphabet alphabet = this.dictionary.getAlphabet();
      this.dictionary.setAlphabet(alphabet);
      return alphabet;
    } catch (IllegalArgumentException e) {
      return null;
    }
    
  }
  
  public boolean setAlphabet(String alphabetFileName) {//set this.dictionary.alphabet into alphabet loaded from specified file
    try {
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public Alphabet getSymbolsSet(Integer setContentFlags) {
    try {
//        setContentFlags is bit-field where each bit is flag of symbols subset assigned with addSymbolsSubset(..) function
      return null;//todo>> return sum of subsets of this.specialSymbolsSet converted to Alphabet
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
  
  public boolean addSymbolsSubset(Set<Character> symbols, Integer flag) {
    try {
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public boolean addDictionaryEntry(DictionaryEntry entry) {
    try {
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public boolean loadDictionary(String dictionaryFileName) {
    try {
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public boolean saveDictionary(String dictionaryFileName) {
    try {
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
//  public Dictionary margeDictionaries(Dictionary dictionary1, Dictionary dictionary2){
//    return new Dictionary();
//  }
}
