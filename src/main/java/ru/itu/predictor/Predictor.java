package ru.itu.predictor;

import ru.itu.predictor.alphabet.Alphabet;
import ru.itu.predictor.metric.Metric;
import ru.itu.predictor.registry.Entry;
import ru.itu.predictor.registry.SearchDictionaryEntry;
import ru.itu.predictor.search.Search;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
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
 */

/**
 * Класс Predictor предназначен для осуществления предиктивного нечёткого поиска по заданным словарям - основному
 * условно-неизменному словарю построенному на корпусе национального разговорного языка, а также по пользовательским
 * словарям - словарю слов и словарю фраз.
 * <p>
 * Пользовательские словари могут изменяться путем добавления, удаления и
 * редактирования существующих словарных статей.
 * <p>
 * Нечёткий поиск осуществляется по NGram индексу с использованием метрики "Расстояние Левенштейна".
 * О метрике можно почитать тут <a target="blank" href="https://ru.wikipedia.org/wiki/Расстояние_Левенштейна">
 * https://ru.wikipedia.org/wiki/Расстояние_Левенштейна</a>
 * <p>
 * Класс предусматривает использование нескольких групп словарей на различных языках. Язык словаря не имеет
 * значения - алгоритм работает одинаково на любых языках использующих для построения слов алфавит из букв.
 */
@SuppressWarnings({"WeakerAccess", "unused", "BooleanMethodIsAlwaysInverted"})
public class Predictor {
  private static final Logger LOGGER = LogManager.getLogger();
  private Map<String, Search> languageSpecificSearchEngines = new HashMap<>();
  private String activeLanguageIsoName;
  private Search selectedSearch;
  private Map<Integer, Set<Character>> specialSymbolsSet = new HashMap<>();
  
  /*
   * Construct a new Predictor object using configuration file and active language equal to current local default keyboard language
   *
   * @param configFileName path to Predictor's configuration file
   */
  
  /**
   * Конструктор - создаёт новый экземпляр класса Predictor используя конфигурационный файл и устанавливает в качестве
   * активного текущий язык операционной системы
   * <p>
   * активный язык - язык по словарям которого будет вестись поиск при вызове метода search экземпляра класса Predictor
   *
   * @param configFileName имя кофигурациооного файла
   */
  public Predictor(String configFileName) {//todo>>
    String line;
    String[] lineFields;
    Map<String, String> searchConfigurationFiles = new HashMap<>();
    Path path = Paths.get(configFileName);
    
    try (BufferedReader reader = new BufferedReader(new FileReader(configFileName))) {
      LOGGER.debug("An attempt to create an instance of the Predictor class from configurations described in '{}'", configFileName);
      while ((line = reader.readLine()) != null) {
        lineFields = line.split("=");
        if (line.length() == 0 || lineFields[0].trim().toCharArray()[0] == '#' || lineFields.length != 2) {
          continue;
        }
        String isoLanguageName = lineFields[0].trim();
        String searchConfigFileName = path.getParent() + File.separator + lineFields[1].trim();
        searchConfigurationFiles.put(isoLanguageName, searchConfigFileName);
        LOGGER.debug("Search Engine configuration file for language '{}' is '{}'", isoLanguageName, searchConfigFileName);
      }
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }
    try {
      searchConfigurationFiles.forEach((language, fileName) -> this.languageSpecificSearchEngines.put(language, new Search(fileName)));
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }
    
    if (!this.setLanguage(System.getProperty("user.language"))) {
      LOGGER.warn("The selected language isn't presented in the application at the moment");
      throw new RuntimeException("Error: The selected language isn't presented in the application at the moment");
    }
    
    this.selectedSearch = languageSpecificSearchEngines.get(this.activeLanguageIsoName);
    
    LOGGER.debug("An instance of Predictor class has created.");
    
  }
  
  /*
   * Construct a new Predictor object using configuration file and active language equal to language in activeLanguageIsoName parameter
   *
   * @param configFileName        path to Predictor's configuration file
   * @param activeLanguageIsoName string with ISO 639-1 alfa2 name of language https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
   */
  
  /**
   * Конструктор - создаёт новый экземпляр класса Predictor используя конфигурационный файл и устанавливает активный языя в соответствие передаваемому параметру
   * <p>
   * активный язык - язык по словарям которого будет вестись поиск при вызове метода search экземпляра класса Predictor
   *
   * @param configFileName        имя кофигурациооного файла
   * @param activeLanguageIsoName ISO имя языка который необходимо установить в качестве активного, *активный язык - язык по словарям которого будет вестись поиск при вызове метода search экземпляра класса Predictor
   */
  public Predictor(String configFileName, String activeLanguageIsoName) {
    this(configFileName);
    if (!this.setLanguage(activeLanguageIsoName)) {
      LOGGER.warn("The selected language isn't presented in the application at the moment");
      throw new RuntimeException("Error: The selected language isn't presented in the application at the moment");
    }
  }
  
  /*
   * Sets active language equal to language specified in the passed parameter
   *
   * @param isoLanguageName string with ISO 639-1 alfa2 name of language https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
   * @return true if assignment was successful, otherwise returns false
   */
  
  /**
   * Устанавливает активный язык, *активный язык - язык по словарям которого будет вестись поиск при вызове метода search
   * экземпляра класса Predictor
   *
   * @param isoLanguageName ISO имя языка который необходимо установить в качестве активного, *активный язык - язык по
   *                        словарям которого будет вестись поиск при вызове метода search экземпляра класса Predictor
   * @return true - если язык успешно установлен, false - в противном случае
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
  
  /*
   * Return name of currently selected language
   *
   * @return name of currently selected language
   */
  
  /**
   * Возвращает ISO имя активного языка, *активный язык - язык по словарям которого будет вестись поиск при вызове метода search экземпляра класса Predictor
   *
   * @return ISO имя активного языка, *активный язык - язык по словарям которого будет вестись поиск при вызове метода search экземпляра класса Predictor
   */
  public String getLanguage() {
    return this.activeLanguageIsoName;
  }
  
  /*
   * Returns alphabet object of the dictionaries of the currently selected language
   *
   * @return Alphabet object of the dictionaries of the currently selected language
   */
  
  /**
   * Возвращает объект класса Alphabet для текущего активного языка, *активный язык - язык по словарям которого будет вестись поиск при вызове метода search экземпляра класса Predictor
   *
   * @return объект класса Alphabet для текущего активного языка, *активный язык - язык по словарям которого будет вестись поиск при вызове метода search экземпляра класса Predictor
   */
  public Alphabet getAlphabet() {
    return this.selectedSearch.getAlphabet();
  }
  
  /*
   * Returns array of chars of the dictionaries of the currently selected language
   *
   * @return array of chars of the dictionaries of the currently selected language
   */
  
  /**
   * Возвращает массив char[] символов алфавита активного языка, *активный язык - язык по словарям которого будет вестись поиск при вызове метода search экземпляра класса Predictor
   *
   * @return массив char[] символов алфавита активного языка, *активный язык - язык по словарям которого будет вестись поиск при вызове метода search экземпляра класса Predictor
   */
  public char[] getAlphabetChars() {
    return this.getAlphabet().getChars();
  }
  
  /*
   * Returns reduced alphabet object of the last search result by dictionaries of currently selected language
   *
   * @return reduced alphabet object of the last search result by dictionaries of currently selected language
   */
  
  /**
   * Возвращает объект Alphabet содержащий редуцированный/усечённый алфавит, т.е. содержащий все символы содержащиеся в
   * последнем результате поискогово запроса полеченного после выполнения метода search(...) текущего объекта класса
   * Predictor
   *
   * @return объект Alphabet содержащий редуцированный/усечённый алфавит, т.е. содержащий все символы содержащиеся в
   * последнем результате поискогово запроса полеченного после выполнения метода search(...) текущего объекта класса
   * Predictor
   */
  public Alphabet getReducedAlphabet() {
    return this.selectedSearch.getReducedAlphabet();
  }
  
  /*
   * Returns reduced alphabet object of the next possible symbols from the last search result by dictionaries of currently selected language
   *
   * @return reduced alphabet object of the next possible symbols from the last search result by dictionaries of currently selected language
   */
  
  /**
   * Возвращает объект Alphabet содержащий редуцированный/усечённый алфавит, т.е. содержащий все символы которые могут
   * быть следующими в образце поиска
   *
   * @return объект Alphabet содержащий редуцированный/усечённый алфавит, т.е. содержащий все символы которые могут быть
   * следующими в образце поиска
   */
  public Alphabet getNextSymbolAlphabet() {
    return this.selectedSearch.getNextSymbolAlphabet();
  }
  
  /*
   * Adds word to the search dictionary and to the user words dictionary of the currently selected language
   *
   * @param word word to be added
   * @return true if word was successfully added, otherwise returns false
   */
  
  /**
   * Добавляет слово в словарь поиска и в пользовательский словарь слов, *словарь поиска или поисковый словарь - словарь
   * созданный во время создания объекта класса Predictor путём объединения основного словаря и пользовательских
   * словарей - фраз и слов
   *
   * @param word слово для добавления в словарь
   * @return true если дабвление состоялось, false в противном случае
   */
  public boolean addWord(String word) {
    return this.selectedSearch.addWord(word.toLowerCase());
  }
  
  /*
   * Adds phrase to the search dictionary and to the user phrases dictionary of the currently selected language
   *
   * @param phrase phrase to be added
   * @return true if phrase was successfully added, otherwise returns false
   */
  
  /**
   * Добавляет фразу в словарь поиска и в пользовательский словарь фраз, *словарь поиска или поисковый словарь - словарь
   * созданный во время создания объекта класса Predictor путём объединения основного словаря и пользовательских
   * словарей - фраз и слов
   *
   * @param phrase фраза для добавления в словарь
   * @return true если дабвление состоялось, false в противном случае
   */
  public boolean addPhrase(String phrase) {
    return this.selectedSearch.addPhrase(phrase.toLowerCase());
  }
  
  /*
   * Finds and Returns SearchDictionaryEntry object from search dictionary of currently selected language
   *
   * @param wordOrPhrase a word or phrase of an entry that should be found
   * @return SearchDictionaryEntry object with word or phrase and statistical and other information about that word or phrase
   */
  
  /**
   * Находит слово или фразу и возвращает объект класса SearchDictionaryEntry в поисковом словаре текущего языка,
   * *словарь поиска или поисковый словарь - словарь созданный во время создания объекта класса Predictor путём
   * объединения основного словаря и пользовательских словарей - фраз и слов
   *
   * @param wordOrPhrase фраза для добавления в словарь
   * @return искомый объект класса SearchDictionaryEntry в поисковом словаре текущего языка
   */
  public SearchDictionaryEntry getEntry(String wordOrPhrase) {
    return this.selectedSearch.getEntry(wordOrPhrase);
  }
  
  /*
   * Removes word from the dictionaries of the selected language
   *
   * @param word word to be removed
   * @return true if word was successfully removed, otherwise returns false
   */
  
  /**
   * Находит и удаляет запись соответствующую искомому слову (объект класса SearchDictionaryEntry) из словаря поиска и
   * пользовательского словаря слов, *словарь поиска или поисковый словарь - словарь созданный во время создания объекта
   * класса Predictor путём объединения основного словаря и пользовательских словарей - фраз и слов
   *
   * @param word искомое слово
   * @return возвращает удалённый объект класса SearchDictionaryEntry
   */
  public SearchDictionaryEntry removeWord(String word) {
    return this.selectedSearch.removeWord(word);
  }
  
  /*
   * Removes word from the dictionaries of the selected language
   *
   * @param phrase phrase to be removed
   * @return true if phrase was successfully removed, otherwise returns false
   */
  
  /**
   * Находит и удаляет запись соответствующую искомой фразе (объект класса SearchDictionaryEntry) из словаря поиска и
   * пользовательского словаря фраз, *словарь поиска или поисковый словарь - словарь созданный во время создания объекта
   * класса Predictor путём объединения основного словаря и пользовательских словарей - фраз и слов
   *
   * @param phrase искомая фраза
   * @return возвращает удалённый объект класса SearchDictionaryEntry
   */
  public SearchDictionaryEntry removePhrase(String phrase) {
    return this.selectedSearch.removePhrase(phrase);
  }
  
  /*
   * Updates search dictionary entry of the word specified
   *
   * @param word           key of the search dictionary entry
   * @param frequency      IPM (Items Per Million) - frequency characteristic of the word from the national language corpus
   * @param localFrequency counter of uses of the word from user's words dictionary
   * @param lastUseTime    last use time of the word from user's dictionary
   * @return SearchDictionaryEntry previous version of the replaced entry object
   */
  
  /**
   * Модифицирует поля записи поискового словаря и пользовательского словаря слов присваиваня новые значения свойствам
   * частота, счетчик использований, время последнего использования
   * *словарь поиска или поисковый словарь - это словарь созданный во время создания объекта класса Predictor путём
   * объединения основного словаря и пользовательских словарей - фраз и слов
   *
   * @param word           искомое слово
   * @param frequency      частота в IPM (Items per Million - количество выпадений слова на миллион слов национального корпуса
   *                       языка)
   * @param localFrequency пользовательская частота - количество использований слова пользователем
   * @param lastUseTime    время последнего использования слова
   * @return объект класса SearchDictionaryEntry эквивалентный искомой записи словаря до её модификации (старое значение
   * объекта записи словаря SearchDictionaryEntry)
   */
  public SearchDictionaryEntry updateWord(String word, Double frequency, Double localFrequency, LocalDateTime lastUseTime) {
    return this.selectedSearch.updateEntry(selectedSearch.getDictionary().getUserWordsDictionary(), word, frequency, localFrequency, lastUseTime);
  }
  
  /*
   * Updates search dictionary entry of the phrase specified
   *
   * @param phrase         key of the search dictionary entry
   * @param frequency      IPM (Items Per Million) - frequency characteristic of the phrase from the national language corpus
   * @param localFrequency counter of uses of the phrase from user's phrases dictionary
   * @param lastUseTime    last use time of the phrase from user's dictionary
   * @return SearchDictionaryEntry previous version of the replaced entry object
   */
  
  /**
   * Модифицирует поля записи поискового словаря и пользовательского словаря фраз присваиваня новые значения свойствам
   * частота, счетчик использований, время последнего использования
   * *словарь поиска или поисковый словарь - это словарь созданный во время создания объекта класса Predictor путём
   * объединения основного словаря и пользовательских словарей - фраз и слов
   *
   * @param phrase         искомая фраза
   * @param frequency      частота в IPM (Items per Million - количество выпадений фразы на миллион слов национального
   *                       корпуса языка)
   * @param localFrequency пользовательская частота - количество использований фразы пользователем
   * @param lastUseTime    время последнего использования фразы
   * @return объект класса SearchDictionaryEntry эквивалентный искомой записи словаря до её модификации (старое значение
   * объекта записи словаря SearchDictionaryEntry)
   */
  public SearchDictionaryEntry updatePhrase(String phrase, Double frequency, Double localFrequency, LocalDateTime lastUseTime) {
    return this.selectedSearch.updateEntry(selectedSearch.getDictionary().getUserPhrasesDictionary(), phrase, frequency, localFrequency, lastUseTime);
  }
  
  /*
   * Returns reference to the search engine built on the dictionaries for the currently selected language
   *
   * @return Search object - search engine built on the dictionaries for the currently selected language
   */
  
  /**
   * Возвращает объект класса Search, поисковый движок, для активного языка, созданный при создании объекта класса
   * Predictor, *активный язык - язык по словарям которого будет вестись поиск при вызове метода search экземпляра класса
   * Predictor
   *
   * @return объект класса Search - поисковый движок для активного языка, *активный язык - язык по словарям которого
   * будет вестись поиск при вызове метода search экземпляра класса Predictor
   */
  public Search getSelectedSearch() {
    return this.selectedSearch;
  }
  
  /*
   * Makes search and returns result set of search
   *
   * @param searchPattern  pattern similar to the target of search
   * @return set of SearchDictionaryEntry objects similar to the searchPattern parameter string
   * @throws IOException if path of the one of dictionaries is wrong or if one of the dictionaries files doesn't exist
   */
  
  /**
   * Осуществляет нечёткий поиск по образцу и возвращает множество записей (объектов класса SearchDictionaryEntry) из
   * поискового словаря префиксы или полные значения слов или фраз которых отстоят от поискового образца не дальше чем
   * на заданное при инициализации расстояние, измеренное с помощью заданной при инициализации метрики
   * <p>
   * *словарь поиска или поисковый словарь - словарь созданный во время создания объекта класса Predictor путём
   * объединения основного словаря и пользовательских словарей - фраз и слов
   *
   * @param searchPattern образец для поиска
   * @return Множество (Set) уникальных записей поискового словаря (SearchDictionaryEntry) соответствующиъ условиям поиска
   * @throws IOException если теряется доступ хотя бы к одному из файлов словарей по указанным при инициализации путям
   */
  public Set<SearchDictionaryEntry> search(String searchPattern) throws IOException {
    return this.selectedSearch.run(searchPattern);
  }
  
  /*
   * Makes search and returns result set of search
   *
   * @param searchPattern pattern similar to the target of search
   * @param maxDistance   maximal allowable distance from searchPattern to a word from search dictionary calculated using metric from configuration files
   * @return  set of SearchDictionaryEntry objects similar to the searchPattern parameter string
   * @throws IOException if path of the one of dictionaries is wrong or if one of the dictionaries files doesn't exist
   */
  
  /**
   * Осуществляет нечёткий поиск по образцу и возвращает множество записей (объектов класса SearchDictionaryEntry) из
   * поискового словаря префиксы или полные значения слов или фраз которых отстоят от поискового образца не дальше чем
   * на заданное в параметре maxDistance расстояние, измеренное с помощью заданной при инициализации метрики
   * <p>
   * *словарь поиска или поисковый словарь - словарь созданный во время создания объекта класса Predictor путём
   * объединения основного словаря и пользовательских словарей - фраз и слов
   *
   * @param searchPattern образец для поиска
   * @param maxDistance   максимально допустимое расстояние от поискового образца (измеренное с помощью метрики заданной
   *                      при инициализации)
   * @return Множество (Set) уникальных записей поискового словаря (SearchDictionaryEntry) соответствующиъ условиям поиска
   * @throws IOException если теряется доступ хотя бы к одному из файлов словарей по указанным при инициализации путям
   */
  public Set<SearchDictionaryEntry> search(String searchPattern, int maxDistance) throws IOException {
    return this.selectedSearch.run(searchPattern, maxDistance);
  }
  
  /*
   * Makes search and returns result set of search
   *
   * @param searchPattern - String - pattern similar to the target of search
   * @param maxDistance   - Integer - maximal allowable distance from searchPattern to a word from search dictionary calculated using selected metric
   * @param metric        - LevensteinMetric object - metric to be used with this search
   * @param prefix        - boolean - whether this search should be by prefix (if true) or by whole word
   * @return Set\<SearchDictionaryEntry\> - set of SearchDictionaryEntry objects similar to the searchPattern parameter string
   * @throws IOException if path of the one of dictionaries is wrong or if one of the dictionaries files doesn't exist
   */
  
  /**
   * Осуществляет нечёткий поиск по образцу и возвращает множество записей (объектов класса SearchDictionaryEntry) из
   * поискового словаря префиксы или полные значения слов или фраз которых отстоят от поискового образца не дальше чем
   * на заданное в параметре maxDistance расстояние, измеренное с помощью метрики заданной в параметре metric
   * <p>
   * *словарь поиска или поисковый словарь - словарь созданный во время создания объекта класса Predictor путём
   * объединения основного словаря и пользовательских словарей - фраз и слов
   *
   * @param searchPattern образец для поиска
   * @param maxDistance   максимально допустимое расстояние от поискового образца
   * @param metric        объект LevensteinMetric - объект метрики используемой во время поиска
   * @param prefix        флаг указывающий должен ли этот поиск быть поиском по целым словам (false) или поиском по
   *                      префиксам слов (true)
   * @return Множество (Set) уникальных записей поискового словаря (SearchDictionaryEntry) соответствующиъ условиям
   * поиска
   * @throws IOException если теряется доступ хотя бы к одному из файлов словарей по указанным при инициализации путям
   */
  public Set<SearchDictionaryEntry> search(String searchPattern, int maxDistance, Metric metric, boolean prefix) throws IOException {
    return this.selectedSearch.run(searchPattern, maxDistance, metric, prefix);
  }
  
  /*
   * The method returns a limited and rearranged list of search dictionary entries that were found with last search run.
   * A rearrangement is an sorting by virtual IPM (VIPM) where VIPM equals to the result of a multiplication of local
   * frequency (a count of uses from the user's dictionary) by the coefficient that equal to an IPM range multiplied to
   * the given buoyancy factor and divided by the one million:
   * <p>
   * VIPM = localFrequency * (liftFactor)buoyancy * 1 000 000 / Sum of all user's dictionaries frequencies
   *
   * @param limit      the length of a resulting list
   * @param liftFactor the buoyancy factor, this should be a float value in a range from 0 to 1
   * @return List\<SearchDictionaryEntry\> list of arranged string where words and phrases from user's dictionaries are rearranged according to their
   * buoyancy factor
   */
  
  /**
   * Метод возвращает ограниченный и пересортированный особым образом список записей словаря поиска полученный в
   * результате последнего выполнения поиска с помощью метода search(...). Пересортировка - это сортировка по т.н.
   * виртуальному IPM (Items per Million) если обозначить его VIPM (Virtual IPM) то
   * <p>
   * VIPM = localFrequency * liftFactor * 1,000,000  / sum(localFrequency) , где
   * <br> localFrequency - количество использований текущей словарной статьи (SearchDictionaryEntry) пользователем
   * <br> liftFactor - коэффициент подъёма - регулятор приоритета пользовательских словарей над основным
   * <br> sum(localFrequency) - сумма всех использований всех слов и фраз пользовательских словарей для активного языка
   * <p>
   * *словарь поиска или поисковый словарь - словарь созданный во время создания объекта класса Predictor путём
   * объединения основного словаря и пользовательских словарей фраз и слов
   * <p>
   * *активный язык - язык по словарям которого будет вестись поиск при вызове метода search экземпляра класса Predictor
   *
   * @param limit                  количество записей в результирующем наборе, если параметр равен нулю или меньше нуля
   *                               рельутирующая выборка не лимитируется, а возвращается полностью
   * @param liftFactor             коэффициент подъёма или плавучесть (buoyancy) - коэффициент позволяющий изменять приоритет/вес при
   *                               сортировке для слов и фраз пользовательских словарей относительно слов основного словаря
   * @param limitBeforeRearranging флаг указывающей цель пересортировки, если true то пересортировка производиться в
   *                               над множестов ограниченным параметром limit, в противном случае (false) сначала
   *                               производиться пересортировка, а затем результат обрезается до размера заданного
   *                               параметром limit
   * @return ограниченный и пересортированный с учётом коэффициента подъема (или плавучести) список записей поискового словаря
   */
  public String[] arrangedSearchResult(int limit, Float liftFactor, boolean limitBeforeRearranging) {
    if (liftFactor < 0 || liftFactor > 1) {
      throw new RuntimeException("Error: error in Predictor.arrangeSearchResult(). Parameter 'liftFactor' should be a " +
                                     "float value in a range from 0 to 1");
    }

//    Double rangeOfIPM = this.selectedSearch.getDictionary().getMaxIPM();
    Double countOfAllUserWordsUses = this.selectedSearch.getDictionary().getTotalUserStringsUses();
    
    if (limit > 0 && limitBeforeRearranging) {
      return this.selectedSearch.getLastSearchResultSet().stream()
                 .limit(limit)
                 .map(e -> new SearchDictionaryEntry(
                     e.getString(),
                     e.getLocalFrequency() > 0 ? e.getLocalFrequency() * liftFactor * 1000000 / countOfAllUserWordsUses : e.getFrequency(),
                     e.getLocalFrequency(),
                     e.getLastUseTime()
                 ))
                 .sorted(Comparator
                             .comparingInt(SearchDictionaryEntry::getDistance)
                             .reversed()
                             .thenComparingDouble(SearchDictionaryEntry::getLocalFrequency)
                             .thenComparingDouble(SearchDictionaryEntry::getFrequency)
                             .reversed()
                 )
                 .map(Entry::getString)
                 .toArray(String[]::new)
          ;
    } else if (limit > 0) {
      return this.selectedSearch.getLastSearchResultSet().stream()
                 .map(e -> new SearchDictionaryEntry(
                     e.getString(),
                     e.getLocalFrequency() > 0 ? e.getLocalFrequency() * liftFactor * 1000000 / countOfAllUserWordsUses : e.getFrequency(),
                     e.getLocalFrequency(),
                     e.getLastUseTime()
                 ))
                 .sorted(Comparator
                             .comparingInt(SearchDictionaryEntry::getDistance)
                             .reversed()
                             .thenComparingDouble(SearchDictionaryEntry::getLocalFrequency)
                             .thenComparingDouble(SearchDictionaryEntry::getFrequency)
                             .reversed()
                 )
                 .map(Entry::getString)
                 .limit(limit)
                 .toArray(String[]::new)
          ;
    } else {
      return this.selectedSearch.getLastSearchResultSet().stream()
                 .map(e -> new SearchDictionaryEntry(
                     e.getString(),
                     e.getLocalFrequency() > 0 ? e.getLocalFrequency() * liftFactor * 1000000 / countOfAllUserWordsUses : e.getFrequency(),
                     e.getLocalFrequency(),
                     e.getLastUseTime()
                 ))
                 .sorted(Comparator
                             .comparingInt(SearchDictionaryEntry::getDistance)
                             .reversed()
                             .thenComparingDouble(SearchDictionaryEntry::getLocalFrequency)
                             .thenComparingDouble(SearchDictionaryEntry::getFrequency)
                             .reversed()
                 )
                 .map(Entry::getString)
                 .toArray(String[]::new)
          ;
    }
  }
  
  /*
   * Adds a new symbols set to the map of special (non-alphabetic symbols) and sets next bit flag during adding process
   * Can keep up to 2^31 sets of special symbols.
   *
   * @param symbols Set\<Character\> - set of non-alphabetic symbols to be saved in the Predictor object
   * @throws RuntimeException If there is try to add 2^31+1th set to the collection
   */
  
  /**
   * Добавляет новые наборы символов в таблицу наборов символов и устанавливает в качестве ключевого поля целое (int32)
   * с поднятым битовым флагом на позиции соответствующей положению добавляемого набора в общем списке наборов, таким
   * образом таблица наборов символов может хранить до 31 набора символов. При попытке добавить 32 набор будет выдано
   * сообщение об ошибке переполнения набора.
   *
   * @param symbols - набор символов кот. необходимо добавить в таблицу наборов символов
   * @throws RuntimeException если все 31 слот таблицы наборов символов уже занянты
   */
  public void addSpecialSymbolsSubset(Set<Character> symbols) throws RuntimeException {//number of bit correspond to order in which charset was inserted into map
    int order = this.specialSymbolsSet.size();
    if (order >= 31) {
      throw new RuntimeException("Error: The table of the sets of special symbols is overflowed.");
    }
    int flag = (int) Math.pow(2, order);
    this.specialSymbolsSet.put(flag, symbols);
  }
  
  /*
   * Gets a combined set of special symbol characters according to passed bit-field
   *
   * @param bitField - a bit-field descriptor of resulting special symbols set
   * @return Set\<Character\> - a combined set of special symbol characters according to passed bit-field
   */
  
  /**
   * Возвращает сводный набор символов получаемый из таблицы наборов символов формируемой при помощи метода
   * addSpecialSymbolsSubset, выбор подможеств для объединения осуществляется по битовому полю bitField
   *
   * @param bitField - битовое поле для выбора подмножеств для формирования сводного множества символов
   * @return сводный набор символов получаемый из таблицы наборов символов
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
  
  public void saveWords() throws IOException {
    this.selectedSearch.getDictionary().saveUserWords();
  }
  
  public void savePhrases() throws IOException {
    this.selectedSearch.getDictionary().saveUserPhrases();
  }
  
}
