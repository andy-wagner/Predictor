package ru.itu.predictools;

import ru.itu.predictools.alphabet.Alphabet;
import ru.itu.predictools.metric.Metric;
import ru.itu.predictools.registry.SearchDictionaryEntry;
import ru.itu.predictools.search.Search;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Класс Predictor предназначен для осуществления предиктивного нечёткого поиска по заданным словарям - основному
 * условно-неизменному словарю построенному на корпусе национального разговорного языка, а также по пользовательским
 * словарям - словарю слов и словарю фраз. Пользовательские словари могут изменяться путем добавления, удаления и
 * редактирования существующих словарных статей.
 * Нечёткий поиск осуществляется по NGram индексу с использованием метрики "Расстояние Левенштейна".
 * О метрике можно почитать тут https://ru.wikipedia.org/wiki/Расстояние_Левенштейна
 * Класс предусматривает использование нескольких групп словарей в т.ч. на различных языках. Язык словаря не имеет
 * значения - алгоритм работает одинаково на любых языках использующих для построения слов алфавит из букв соотносимых
 * со звнуками
 */

/**
 * Predictor is a container class that includes methods for making and managing adaptive fuzzy predictive search
 * through main language national corpus dictionary and user's words and phrases dictionaries.
 * <p>
 * User's dictionaries could be changed by adding, removing and updating dictionaries' entries.
 * <p>
 * Fuzzy search is made on the n-gram index and uses Levenstein distance metric to measure words similarity
 * <a href ="https://en.wikipedia.org/wiki/Levenshtein_distance">https://en.wikipedia.org/wiki/Levenshtein_distance</a>
 * <p>
 * Predictor class makes possible using several different groups of dictionaries of a variety of languages. There is no
 * difference for the searching algorithm which language the dictionaries have if the dictionaries are the list of words
 * composed from letters that could be organized into an alphabet
 *
 * @author Boris Bronovitsky
 */
@SuppressWarnings({"WeakerAccess", "unused", "BooleanMethodIsAlwaysInverted"})
public class Predictor {
  private static final Logger LOGGER = LogManager.getLogger();
  private Map<String, Search> languageSpecificSearchEngines = new HashMap<>();
  private String activeLanguageIsoName;
  private Search selectedSearch;
  private Map<Integer, Set<Character>> specialSymbolsSet = new HashMap<>();
  
  /**
   * Construct a new Predictor object using configuration file and active language equal to current local default keyboard language
   *
   * @param configFileName - String - path to Predictor's configuration file
   */
  public Predictor(String configFileName) {//todo>>
    String line;
    String[] lineFields;
    Map<String, String> searchConfigurationFiles = new HashMap<>();
    
    try (BufferedReader reader = new BufferedReader(new FileReader(configFileName))) {
      while ((line = reader.readLine()) != null) {
        lineFields = line.split("=");
        if (line.length() == 0 || lineFields[0].trim().toCharArray()[0] == '#' || lineFields.length != 2) {
          continue;
        }
        String isoLanguageName = lineFields[0].trim();
        String searchConfigFileName = lineFields[1].trim();
        searchConfigurationFiles.put(isoLanguageName, searchConfigFileName);
      }
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }
    searchConfigurationFiles.forEach((language, fileName) -> this.languageSpecificSearchEngines.put(language, new Search(fileName)));
    
    if (!this.setLanguage(System.getProperty("user.language"))) {
      LOGGER.warn("The selected language isn't presented in the application at the moment");
      throw new RuntimeException("Error: The selected language isn't presented in the application at the moment");
    }
    
    this.selectedSearch = languageSpecificSearchEngines.get(this.activeLanguageIsoName);
    
    LOGGER.info("An instance of Predictor class has been created.");
    
  }
  
  /**
   * Construct a new Predictor object using configuration file and active language equal to language in activeLanguageIsoName parameter
   *
   * @param configFileName        - String - path to Predictor's configuration file
   * @param activeLanguageIsoName - string with ISO 639-1 alfa2 name of language https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
   */
  public Predictor(String configFileName, String activeLanguageIsoName) {
    this(configFileName);
    if (!this.setLanguage(activeLanguageIsoName)) {
      LOGGER.warn("The selected language isn't presented in the application at the moment");
      throw new RuntimeException("Error: The selected language isn't presented in the application at the moment");
    }
  }
  
  /**
   * Sets active language equal to language specified in the passed parameter
   *
   * @param isoLanguageName - String - string with ISO 639-1 alfa2 name of language https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
   * @return boolean - true if assignment was successful, otherwise returns false
   */
  public boolean setLanguage(String isoLanguageName) {
    if (this.languageSpecificSearchEngines.containsKey(isoLanguageName)) {
      this.activeLanguageIsoName = isoLanguageName;
      this.selectedSearch = languageSpecificSearchEngines.get(this.activeLanguageIsoName);
      return true;
    }
    this.activeLanguageIsoName = this.languageSpecificSearchEngines.entrySet().stream().map(Map.Entry::getKey).findAny().toString();
    this.selectedSearch = languageSpecificSearchEngines.get(this.activeLanguageIsoName);
    return false;
  }
  
  /**
   * Return name of currently selected language
   *
   * @return String - name of currently selected language
   */
  public String getLanguage() {
    return this.activeLanguageIsoName;
  }
  
  /**
   * Returns alphabet object of the dictionaries of the currently selected language
   *
   * @return Alphabet - object of the dictionaries of the currently selected language
   */
  public Alphabet getAlphabet() {
    return this.selectedSearch.getAlphabet();
  }
  
  /**
   * Returns array of chars of the dictionaries of the currently selected language
   *
   * @return char[] - array of chars of the dictionaries of the currently selected language
   */
  public char[] getAlphabetChars() {
    return this.getAlphabet().getChars();
  }
  
  /**
   * Returns reduced alphabet object of the last search result by dictionaries of currently selected language
   *
   * @return Alphabet - reduced alphabet object of the last search result by dictionaries of currently selected language
   */
  public Alphabet getReducedAlphabet() {
    return this.selectedSearch.getReducedAlphabet();
  }
  
  /**
   * Returns reduced alphabet object of the next possible symbols from the last search result by dictionaries of currently selected language
   *
   * @return Alphabet - reduced alphabet object of the next possible symbols from the last search result by dictionaries of currently selected language
   */
  public Alphabet getNextSymbolAlphabet() {
    return this.selectedSearch.getNextSymbolAlphabet();
  }
  
  /**
   * Adds word to the search dictionary and to the user words dictionary of the currently selected language
   *
   * @param word - String - word to be added
   * @return boolean - true if word was successfully added, otherwise returns false
   */
  public boolean addWord(String word) {
    return this.selectedSearch.addWord(word);
  }
  
  /**
   * Adds phrase to the search dictionary and to the user phrases dictionary of the currently selected language
   *
   * @param phrase - String - phrase to be added
   * @return boolean - true if phrase was successfully added, otherwise returns false
   */
  public boolean addPhrase(String phrase) {
    return this.selectedSearch.addWord(phrase);
  }
  
  /**
   * Finds and Returns SearchDictionaryEntry object from search dictionary of currently selected language
   *
   * @param wordOrPhrase - String - a word or phrase of an entry that should be found
   * @return SearchDictionaryEntry - object with word or phrase and statistical and other information about that word or phrase
   */
  public SearchDictionaryEntry getEntry(String wordOrPhrase) {
    return this.selectedSearch.getEntry(wordOrPhrase);
  }
  
  /**
   * Removes word from the dictionaries of the selected language
   *
   * @param word - String - word to be removed
   * @return boolean - true if word was successfully removed, otherwise returns false
   */
  public SearchDictionaryEntry removeWord(String word) {
    return this.selectedSearch.removeWord(word);
  }
  
  /**
   * Removes word from the dictionaries of the selected language
   *
   * @param phrase - String - phrase to be removed
   * @return boolean - true if phrase was successfully removed, otherwise returns false
   */
  public SearchDictionaryEntry removePhrase(String phrase) {
    return this.selectedSearch.removePhrase(phrase);
  }
  
  /**
   * Updates search dictionary entry of the word specified
   *
   * @param word           - key of the search dictionary entry
   * @param frequency      - IPM (Items Per Million) - frequency characteristic of the word
   * @param localFrequency - counter of uses of the word from user's words dictionary
   * @param lastUseTime    - last use time of the word from user's dictionary
   * @return SearchDictionaryEntry - previous version of the replaced entry object
   */
  public SearchDictionaryEntry updateWord(String word, Double frequency, Double localFrequency, LocalDateTime lastUseTime) {
    return this.selectedSearch.updateEntry(selectedSearch.getDictionary().getUserWordsDictionary(), word, frequency, localFrequency, lastUseTime);
  }
  
  /**
   * Updates search dictionary entry of the phrase specified
   *
   * @param phrase         - key of the search dictionary entry
   * @param frequency      - IPM (Items Per Million) - frequency characteristic of the phrase
   * @param localFrequency - counter of uses of the phrase from user's phrases dictionary
   * @param lastUseTime    - last use time of the phrase from user's dictionary
   * @return SearchDictionaryEntry - previous version of the replaced entry object
   */
  public SearchDictionaryEntry updatePhrase(String phrase, Double frequency, Double localFrequency, LocalDateTime lastUseTime) {
    return this.selectedSearch.updateEntry(selectedSearch.getDictionary().getUserPhrasesDictionary(), phrase, frequency, localFrequency, lastUseTime);
  }
  
  /**
   * Returns reference to the search engine built on the dictionaries for the currently selected language
   *
   * @return Search - search engine built on the dictionaries for the currently selected language
   */
  public Search getSelectedSearch() {
    return this.selectedSearch;
  }
  
  /**
   * Makes search and returns result set of search
   *
   * @param searchPattern - String - pattern similar to the target of search
   * @return Set\<SearchDictionaryEntry\> - set of SearchDictionaryEntry objects similar to the searchPattern parameter string
   * @throws IOException if path of the one of dictionaries is wrong or if one of the dictionaries files doesn't exist
   */
  public Set<SearchDictionaryEntry> search(String searchPattern) throws IOException {
    return this.selectedSearch.run(searchPattern);
  }
  
  /**
   * Makes search and returns result set of search
   *
   * @param searchPattern - String - pattern similar to the target of search
   * @param maxDistance   - Integer - maximal allowable distance from searchPattern to a word from search dictionary calculated using metric from configuration files
   * @return Set\<SearchDictionaryEntry\> - set of SearchDictionaryEntry objects similar to the searchPattern parameter string
   * @throws IOException if path of the one of dictionaries is wrong or if one of the dictionaries files doesn't exist
   */
  public Set<SearchDictionaryEntry> search(String searchPattern, int maxDistance) throws IOException {
    return this.selectedSearch.run(searchPattern, maxDistance);
  }
  
  /**
   * Makes search and returns result set of search
   *
   * @param searchPattern - String - pattern similar to the target of search
   * @param maxDistance   - Integer - maximal allowable distance from searchPattern to a word from search dictionary calculated using selected metric
   * @param metric        - LevensteinMetric object - metric to be used with this search
   * @param prefix        - boolean - whether this search should be by prefix (if true) or by whole word
   * @return Set\<SearchDictionaryEntry\> - set of SearchDictionaryEntry objects similar to the searchPattern parameter string
   * @throws IOException if path of the one of dictionaries is wrong or if one of the dictionaries files doesn't exist
   */
  public Set<SearchDictionaryEntry> search(String searchPattern, int maxDistance, Metric metric, boolean prefix) throws IOException {
    return this.selectedSearch.run(searchPattern, maxDistance, metric, prefix);
  }
  
  /**
   * The method returns a limited and rearranged list of search dictionary entries that were found with last search run.
   * A rearrangement is an sorting by virtual IPM (VIPM) where VIPM equals to the result of a multiplication of local
   * frequency (a count of uses from the user's dictionary) by the coefficient that equal to an IPM range multiplied to
   * the given buoyancy factor and divided by the sum of all words counters from user's dictionary:
   * <p>
   * VIPM = localFrequency * buoyancy * max of main dictionary frequency / Sum of all user's dictionaries frequencies
   *
   * @param limit    - the length of a resulting list
   * @param buoyancy - the buoyancy factor, this should be a float value in a range from 0 to 1
   * @return List\<SearchDictionaryEntry\> list of arranged string where words and phrases from user's dictionaries are rearranged according to their
   * buoyancy factor
   */
  public List<SearchDictionaryEntry> arrangedSearchResult(int limit, float buoyancy, boolean limitBeforeRearranging) {
    if (buoyancy < 0 || buoyancy > 1) {
      throw new RuntimeException("Error: error in Predictor.arrangeSearchResult(). Parameter 'buoyancy' should be a " +
                                     "float value in a range from 0 to 1");
    }
    
    Double rangeOfIPM = this.selectedSearch.getDictionary().getRangeOfIPM();
    Double countOfAllUserWordsUses = this.selectedSearch.getDictionary().getTotalUserWordsUses();
    
    if (limitBeforeRearranging) {
      return this.selectedSearch.getLastSearchResultSet().stream()
                 .limit(limit)
                 .map(e -> new SearchDictionaryEntry(
                     e.getWord(),
                     e.getLocalFrequency() * buoyancy * rangeOfIPM / countOfAllUserWordsUses,
                     e.getLocalFrequency(),
                     e.getLastUseTime()
                 ))
                 .collect(Collectors.toList());
    } else {
      return this.selectedSearch.getLastSearchResultSet().stream()
                 .map(e -> new SearchDictionaryEntry(
                     e.getWord(),
                     e.getLocalFrequency() * buoyancy * rangeOfIPM / countOfAllUserWordsUses,
                     e.getLocalFrequency(),
                     e.getLastUseTime()
                 ))
                 .limit(limit)
                 .collect(Collectors.toList());
    }
//    return new ArrayList<>();
  }
  
  /**
   * Adds a new symbols set to the map of special (non-alphabetic symbols) and sets next bit flag during adding process
   * Can keep up to 2^31 sets of special symbols.
   *
   * @param symbols Set\<Character\> - set of non-alphabetic symbols to be saved in the Predictor object
   * @throws RuntimeException If there is try to add 2^31+1th set to the collection
   */
  public void addSpecialSymbolsSubset(Set<Character> symbols) throws RuntimeException {//number of bit correspond to order in which charset was inserted into map
    int order = this.specialSymbolsSet.size();
    if (order >= 31) {
      throw new RuntimeException("Error: Special Symbols Sets Table Overflow.");
    }
    int flag = (int) Math.pow(2, order);
    this.specialSymbolsSet.put(flag, symbols);
  }
  
  /**
   * Gets a combined set of special symbol characters according to passed bit-field
   *
   * @param bitField - a bit-field descriptor of resulting special symbols set
   * @return Set\<Character\> - a combined set of special symbol characters according to passed bit-field
   */
  public Set<Character> getSpecialSymbolsSet(Integer bitField) {
    Set<Character> characters = new HashSet<>();
    try {
      this.specialSymbolsSet.forEach((key, charset) -> {
        if ((bitField & (1 << key)) != 0) {
          characters.addAll(charset);
        }
      });
      return characters;
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      return null;
    }
  }
  
}
