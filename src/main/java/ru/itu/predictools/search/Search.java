package ru.itu.predictools.search;

import com.sun.corba.se.impl.io.TypeMismatchException;
import ru.itu.predictools.metric.LevensteinMetric;
import ru.itu.predictools.registry.Dictionary;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.Integer.min;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class Search {
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
  private Set<SearchDictionaryEntry> lastSearchResultSet;
  private String lastSearchPattern;
  
  public Search(String jsonConfigurationFileName) {
    
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
    LOGGER.info("An instance of Search class has been created.");
    
  }
  
  /**
   * search search pattern with parameters as they are specified in the predictor.conf file
   *
   * @param searchPattern - pattern to fuzzy search among words of dictionary
   * @return - Set<SearchDictionaryEntry> - Set of SearchDictionaryEntries
   * @throws IOException - throws if can't find one of the dictionaries files with the name specified among dictionaries in predictor's configuration file
   */
  public Set<SearchDictionaryEntry> run(String searchPattern) throws IOException {
    return this.run(searchPattern, this.maxDistance, this.metric, this.prefix);
  }
  
  public Set<SearchDictionaryEntry> run(String searchPattern, int maxDistance) throws IOException {
    return this.run(searchPattern, maxDistance, this.metric, this.prefix);
  }
  
  public Set<SearchDictionaryEntry> run(String searchPattern, int maxDistance, Metric metric, boolean prefix) throws IOException {
    //check if dictionary files changed, if this is true rebuild the index
    BasicFileAttributes attributesOfMainDictionary = Files.readAttributes(Paths.get(this.mainDictionaryFileName), BasicFileAttributes.class);
    BasicFileAttributes attributesOfUserWordsDictionary = Files.readAttributes(Paths.get(this.userWordsDictionaryFileName), BasicFileAttributes.class);
    BasicFileAttributes attributesOfUserPhrasesDictionary = Files.readAttributes(Paths.get(this.userPhrasesDictionaryFileName), BasicFileAttributes.class);
    FileTime mainDictionaryLastModified = attributesOfMainDictionary.lastModifiedTime();
    FileTime userWordsDictionaryLastModified = attributesOfUserWordsDictionary.lastModifiedTime();
    FileTime userPhrasesDictionaryLastModified = attributesOfUserPhrasesDictionary.lastModifiedTime();
    FileTime[] previousLastTimesModified = this.dictionary.getDictionariesLastModifiedTimes();
    if (!mainDictionaryLastModified.equals(previousLastTimesModified[0])
            || !userWordsDictionaryLastModified.equals(previousLastTimesModified[1])
            || !userPhrasesDictionaryLastModified.equals(previousLastTimesModified[2])
        ) {
      this.dictionary.setDictionariesLastModifiedTimes(new FileTime[]{
          mainDictionaryLastModified, userWordsDictionaryLastModified, userPhrasesDictionaryLastModified}
      );
      this.makeIndex();
    }
    this.lastSearchResultSet = this.index.search(searchPattern, maxDistance, metric, prefix);
    this.lastSearchPattern = searchPattern;
    return this.lastSearchResultSet;
  }
  
  public Set<SearchDictionaryEntry> getLastSearchResultSet(){
    return this.lastSearchResultSet;
  }
  
  public String getLastSearchPattern(){
    return this.lastSearchPattern;
  }
  
  private void makeIndex() throws IOException {
    this.lastSearchResultSet = new HashSet<>();
    this.dictionary = new SearchDictionary(this.mainDictionaryFileName, this.userWordsDictionaryFileName, this.userPhrasesDictionaryFileName);
    this.index = new IndexNGram(this.dictionary, n);
    this.metric = new LevensteinMetric(dictionary.getMaxWordLength());
  }
  
  public int getIndexN() {
    return this.n;
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
  
  public Alphabet getNextSymbolAlphabet() {
    return getReducedAlphabet(1);
  }
  
  public Alphabet getNextSymbolsAlphabet(String searchPattern) {
    return getReducedAlphabet(searchPattern, 1);
  }
  
  public Alphabet getReducedAlphabet(int suffixLength) {
    try {
      if (suffixLength < 0) {
        throw new Exception("Wrong suffixLength parameter value, it should be greater or equal to 0");
      }
      Set<String> searchResultsSuffixesSet = this.lastSearchResultSet
                                                 .stream()
                                                 .map(e -> {
                                                   String word = e.getWord();
                                                   int wordLength = word.length();
                                                   int searchPatternLength = this.lastSearchPattern.length();
                                                   if (wordLength >= searchPatternLength) {
                                                     if (suffixLength > 0) {
                                                       return word.substring(searchPatternLength, min(searchPatternLength + suffixLength, wordLength));
                                                     } else {
                                                       return word.substring(searchPatternLength);
                                                     }
                                                   } else {
                                                     return null;
                                                   }
                                                 })
                                                 .filter(Objects::nonNull)
                                                 .distinct()
                                                 .collect(Collectors.toSet());
      return Alphabet.getAlphabet(searchResultsSuffixesSet, this.dictionary.getIsoLanguageName());
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      return null;
    }
  }
  
  public Alphabet getReducedAlphabet(String searchPattern, int suffixLength) {
    try {
      Set<String> search = this.run(searchPattern).stream()
                               .map(e -> {
                                 String word = e.getWord();
                                 int wordLength = word.length();
                                 int searchPatternLength = searchPattern.length();
                                 if (wordLength >= searchPatternLength) {
                                   return word.substring(wordLength, min(searchPatternLength + suffixLength, wordLength));
                                 } else {
                                   return null;
                                 }
                               })
                               .filter(Objects::nonNull)
                               .distinct()
                               .collect(Collectors.toSet());
      return Alphabet.getAlphabet(search, this.dictionary.getIsoLanguageName());
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      return null;
    }
  }
  
  public Alphabet getReducedAlphabet() {
    return SearchDictionary.getAlphabet(this.lastSearchResultSet, this.dictionary.getIsoLanguageName());
  }
  
  public Alphabet getReducedAlphabet(String searchPattern) {
    try {
      return SearchDictionary.getAlphabet(this.run(searchPattern), this.dictionary.getIsoLanguageName());
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
  
  public SearchDictionary getDictionary() {
    return this.dictionary;
  }
  
  public SearchDictionaryEntry getEntry(String wordOrPhrase) {
    return this.dictionary.getSearchDictionaryEntry(wordOrPhrase);
  }
  
  public SearchDictionaryEntry getEntry(Entry entry) {
    return this.dictionary.getSearchDictionaryEntry(entry);
  }
  
  public SearchDictionaryEntry getEntry(SearchDictionaryEntry entry) {
    return this.dictionary.getSearchDictionaryEntry(entry);
  }
  
  public boolean addWord(String word) {
    return this.addEntry(this.dictionary.getUserWordsDictionary(), word);
  }
  
  public boolean addPhrase(String phrase) {
    return this.addEntry(this.dictionary.getUserPhrasesDictionary(), phrase);
  }
  
  public boolean addEntry(Dictionary userDictionary, String word) {
    if (userDictionary.addEntry(word)) {
      return this.dictionary.addSearchDictionaryEntry(word);
    }
    return false;
  }
  
  public boolean addEntry(Dictionary userDictionary, Entry entry) {
    if (userDictionary.addEntry(entry)) {
      return this.dictionary.addSearchDictionaryEntry(entry);
    }
    return false;
  }
  
  public boolean addEntry(Dictionary userDictionary, SearchDictionaryEntry entry) {
    if (userDictionary.addEntry(entry)) {
      return this.dictionary.addSearchDictionaryEntry(entry);
    }
    return false;
  }
  
  public SearchDictionaryEntry removeWord(String word) {
    return this.removeEntry(this.dictionary.getUserWordsDictionary(), word);
  }
  
  public SearchDictionaryEntry removePhrase(String phrase) {
    return this.removeEntry(this.dictionary.getUserPhrasesDictionary(), phrase);
  }
  
  public SearchDictionaryEntry removeEntry(Dictionary userDictionary, String word) {
    if (userDictionary.removeEntry(word)) {
      return this.dictionary.removeSearchDictionaryEntry(word);
    }
    return null;
  }
  
  public SearchDictionaryEntry removeEntry(Dictionary userDictionary, Entry entry) {
    if (userDictionary.removeEntry(entry)) {
      return this.dictionary.removeSearchDictionaryEntry(entry);
    }
    return null;
  }
  
  public SearchDictionaryEntry removeEntry(Dictionary userDictionary, SearchDictionaryEntry entry) {
    if (userDictionary.removeEntry(entry)) {
      return this.dictionary.removeSearchDictionaryEntry(entry);
    }
    return null;
  }
  
  public SearchDictionaryEntry updateEntry(Dictionary userDictionary, String word, Double frequency) {
    if (userDictionary.updateEntry(word, frequency)) {
      return this.dictionary.updateSearchDictionaryEntry(word, frequency);
    }
    return null;
  }
  
  public SearchDictionaryEntry updateEntry(Dictionary userDictionary, String word, Double frequency, LocalDateTime lastUseTime) {
    return this.updateEntry(userDictionary, new Entry(word, frequency, lastUseTime));
  }
  
  public SearchDictionaryEntry updateEntry(Dictionary userDictionary, Entry entry) {
    if (userDictionary.updateEntry(entry)) {
      return this.dictionary.updateSearchDictionaryEntry(entry);
    }
    return null;
  }
  
  public SearchDictionaryEntry updateEntry(Dictionary userDictionary, String word, Double frequency, Double localFrequency) {
    if (userDictionary.updateEntry(word, frequency)) {
      return this.dictionary.updateSearchDictionaryEntry(word, frequency, localFrequency);
    }
    return null;
  }
  
  public SearchDictionaryEntry updateEntry(Dictionary userDictionary, String word, Double frequency, Double localFrequency, LocalDateTime lastUseTime) {
    return this.updateEntry(userDictionary, new SearchDictionaryEntry(word, frequency, localFrequency, lastUseTime));
  }
  
  public SearchDictionaryEntry updateEntry(Dictionary userDictionary, SearchDictionaryEntry entry) {
    if (userDictionary.updateEntry(entry.getWord(), entry.getFrequency())) {
      return this.dictionary.updateSearchDictionaryEntry(entry);
    }
    return null;
  }
  
}
