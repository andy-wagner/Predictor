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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Predictor {
  private static final Logger LOGGER = LogManager.getLogger();
  private String mainDictionaryFileName;
  private String userWordsDictionaryFileName;
  private String userPhrasesDictionaryFileName;
  private SearchDictionary dictionary;
  private IndexNGram index;
  private int n;
  private boolean prefix;
  private int maxDistance;
  private Metric metric;
  private Set<SearchDictionaryEntry> lastSearchResult;
  
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
      LOGGER.error("Error: Wrong dictionary file format.");
      throw new TypeMismatchException("Error: Wrong dictionary file format.");
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }
    LOGGER.info("An instance of Predictor class has been created.");
    
  }
  
  /**
   * search search patternd with parameters as they are specified in the predictor.conf file
   *
   * @param searchPattern - pattern to search words with
   * @return
   * @throws IOException
   */
  public Set<SearchDictionaryEntry> search(String searchPattern) throws IOException {
    return this.search(searchPattern, this.maxDistance, this.metric, this.prefix);
  }
  
  public Set<SearchDictionaryEntry> search(String searchPattern, int maxDistance) throws IOException {
    return this.search(searchPattern, maxDistance, this.metric, this.prefix);
  }
  
  public Set<SearchDictionaryEntry> search(String searchPattern, int maxDistance, Metric metric, boolean prefix) throws IOException {
    //check if dictionary files changed, if this is true rebuild the index
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
      this.makeIndex();
    }
    this.lastSearchResult = this.index.search(searchPattern, maxDistance, metric, prefix);
    return this.lastSearchResult;
  }
  
  private void makeIndex() throws IOException {
    this.lastSearchResult = new HashSet<>();
    this.dictionary = new SearchDictionary(this.mainDictionaryFileName, this.userWordsDictionaryFileName, this.userPhrasesDictionaryFileName);
    this.index = new IndexNGram(this.dictionary, n);
    this.metric = new LevensteinMetric(dictionary.getMaxWordLength());
  }
  
  public void setMaxDistance(int maxDistance) {
    this.maxDistance = maxDistance;
  }
  
  public int getMaxDistance() {
    return this.maxDistance;
  }
  
  public String getLanguage() {
    return this.getAlphabet().getIsoLanguageName();
  }
  
  public Alphabet getAlphabet() {
    return this.dictionary.getAlphabet();
  }
  
  /**
   * Returns alphabet object of predictor's search dictionary
   *
   * @param searchDictionary - search dictionary
   * @return - alphabet object of search dictionary
   */
  public static Alphabet getAlphabet(SearchDictionary searchDictionary) {
    return searchDictionary.getAlphabet();
  }
  
  public SearchDictionary getDictionary() {
    return this.dictionary;
  }
  
  public Alphabet getReducedAlphabet() {
    return SearchDictionary.getAlphabet(this.lastSearchResult, this.dictionary.getIsoLanguageName());
  }
  
  public Alphabet getReducedAlphabet(String searchPattern) {
    try {
      return SearchDictionary.getAlphabet(this.search(searchPattern), this.dictionary.getIsoLanguageName());
    } catch (IllegalArgumentException e) {
      LOGGER.error(e.getMessage());
      return null;
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
  
  public SearchDictionaryEntry getWordEntry(String word){
    return this.dictionary.getEntry(word);
  }
  
  public void addWord(String word) {
    this.dictionary.getUserWordsDictionary().addEntry(word);
    this.dictionary.makeSearchDictionary();
  }
  
  public void addPhrase(String phrase) {
    this.dictionary.getUserPhrasesDictionary().addEntry(phrase);
    this.dictionary.makeSearchDictionary();
  }
  
  public void updateWord(Entry newWordEntry) {
    this.dictionary.getUserWordsDictionary().updateEntry(newWordEntry);
    this.dictionary.makeSearchDictionary();
  }
  
  public void updatePhrase(Entry newPhraseEntry) {
    this.dictionary.getUserPhrasesDictionary().updateEntry(newPhraseEntry);
    this.dictionary.makeSearchDictionary();
  }
  
  public Alphabet getSymbolsSet(Integer contentFlags) {
    try {
//        contentFlags is bit-field where each bit is flag of symbols subset assigned with addSymbolsSubset(..) function
      return null;//todo>> return sum of subsets of this.specialSymbolsSet converted to alphabet
    } catch (IllegalArgumentException e) {
      LOGGER.error(e.getMessage());
      return null;
    }
  }
  
  public void addSymbolsSubset(Set<Character> symbols, Integer flag) {
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
