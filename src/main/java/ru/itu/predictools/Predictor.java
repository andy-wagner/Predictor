package ru.itu.predictools;

import com.sun.corba.se.impl.io.TypeMismatchException;
import ru.itu.predictools.metric.LevensteinMetric;
import ru.itu.predictools.registry.Entry;
import ru.itu.predictools.alphabet.Alphabet;
import ru.itu.predictools.registry.SearchDictionary;
import ru.itu.predictools.metric.Metric;
import ru.itu.predictools.index.IndexNGram;
import ru.itu.predictools.registry.SearchDictionaryEntry;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.Set;

public class Predictor {
  private String mainDictionaryFileName;
  private String userWordsDictionaryFileName;
  private String userPhrasesDictionaryFileName;
  private SearchDictionary dictionary;
  private IndexNGram index;
  private int n;
  private boolean prefix;
  private int maxDistance;
  private Metric metric;
  
  private Map<String, Set<Character>> specialSymbolsSet;//<NameOfCharactersSet, CharactersSet>
  
  public Predictor(String jsonConfigurationFileName) {
    
    String line, name, value;
    String[] lineFields;
    try (BufferedReader reader = new BufferedReader(new FileReader(jsonConfigurationFileName))) {
      
      while ((line = reader.readLine()) != null) {
        lineFields = line.split("=");
        if (line.length() == 0 || lineFields[0].trim().toCharArray()[0] == '#' || lineFields.length != 2) {
          continue;
        }
        name = lineFields[0].trim();
        value = lineFields[1].trim();
        switch (name) {
          case "mainDictionary":
            this.mainDictionaryFileName = System.getProperty("user.dir") + File.separator + value;
            break;
          case "userWordsDictionary":
            this.userWordsDictionaryFileName = System.getProperty("user.dir") + File.separator + value;
            break;
          case "userPhrasesDictionary":
            this.userPhrasesDictionaryFileName = System.getProperty("user.dir") + File.separator + value;
            break;
          case "prefix":
            this.prefix = Boolean.parseBoolean(value);
            break;
          case "n":
            this.n = Integer.parseInt(value);
            break;
          case "maxDistance":
            this.maxDistance = Integer.parseInt(value);
            break;
        }
      }
      
      this.dictionary = new SearchDictionary(
          this.mainDictionaryFileName, this.userWordsDictionaryFileName, this.userPhrasesDictionaryFileName);
      this.index = new IndexNGram(this.dictionary, n);
      this.metric = new LevensteinMetric(dictionary.getMaxWordLength());
    } catch (TypeMismatchException e) {
      throw new TypeMismatchException("Error: Wrong dictionary file format.");
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  private void reloadIndex() throws IOException {
    this.dictionary = new SearchDictionary(this.mainDictionaryFileName, this.userWordsDictionaryFileName, this.userPhrasesDictionaryFileName);
    this.index = new IndexNGram(this.dictionary, n);
    this.metric = new LevensteinMetric(dictionary.getMaxWordLength());
  }
  
  //PUBLIC METHODS
  public Set<SearchDictionaryEntry> search(String searchPattern) throws IOException {
    return this.search(searchPattern, this.maxDistance, this.metric, this.prefix);
  }
  
  public Set<SearchDictionaryEntry> search(String searchPattern, int maxDistance) throws IOException {
    return this.search(searchPattern, maxDistance, this.metric, this.prefix);
  }
  
  public Set<SearchDictionaryEntry> search(String searchPattern, int maxDistance, Metric metric, boolean prefix) throws IOException {
    //check if dictionary files changed, if so rebuild index
    BasicFileAttributes attributesOfMainDictionary = Files.readAttributes(Paths.get(this.mainDictionaryFileName), BasicFileAttributes.class);
    BasicFileAttributes attributesOfUserWordsDictionary = Files.readAttributes(Paths.get(this.userWordsDictionaryFileName), BasicFileAttributes.class);
    BasicFileAttributes attributesOfUserPhrasesDictionary = Files.readAttributes(Paths.get(this.userPhrasesDictionaryFileName), BasicFileAttributes.class);
    FileTime mainDictionaryLastModified = attributesOfMainDictionary.lastModifiedTime();
    FileTime userWordsDictionaryLastModified = attributesOfUserWordsDictionary.lastModifiedTime();
    FileTime userPhrasesDictionaryLastModified = attributesOfUserPhrasesDictionary.lastModifiedTime();
    FileTime[] previousLastTimesModified = this.dictionary.getDictionariesLastTimesModified();
    if (!mainDictionaryLastModified.equals(previousLastTimesModified[0])
            || !userWordsDictionaryLastModified.equals(previousLastTimesModified[1])
            || !userPhrasesDictionaryLastModified.equals(previousLastTimesModified[2])
        ) {
      this.dictionary.setDictionariesLastTimesModified(new FileTime[]{
          mainDictionaryLastModified, userWordsDictionaryLastModified, userPhrasesDictionaryLastModified}
      );
      this.reloadIndex();
    }
    return this.index.search(searchPattern, maxDistance, metric, prefix);
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
  
  public void setLanguage(String language) {//todo>> stub code here
  }
  
  public Alphabet getAlphabet() {
//        return set of all this.dictionary letters converted into alphabet object
    return this.dictionary.getAlphabet();
  }
  
  public static Alphabet getAlphabet(SearchDictionary searchDictionary) {
//        return set of all dictionary letters converted into alphabet object
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
//      saves this.dictionary.alphabet into the file
  }
  
  public void saveAlphabet(Alphabet alphabet, String alphabetFileNmae) {
//        saves alphabet into the file
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
  
  public void setAlphabet(String alphabetFileName) {//set this.dictionary.alphabet into alphabet loaded from specified file
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
  
  public int getIndexN() {
    return this.n;
  }

//  public SearchDictionary margeDictionaries(SearchDictionary dictionary1, SearchDictionary dictionary2){
//    return new SearchDictionary();
//  }
}
