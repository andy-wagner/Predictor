package predictor.registry;

import predictor.alphabet.Alphabet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// SearchDictionary contains some strings and their lexical parameters and other data in structures named SearchDictionaryEntry
@SuppressWarnings({"WeakerAccess", "unused", "FieldCanBeLocal", "UnusedReturnValue"})
public class SearchDictionary {
  private static final Logger LOGGER = LogManager.getLogger();
  //entries need to be an ArrayList because we will need to be able to get an element from a specific position within the list (with method .get(i))
  private List<SearchDictionaryEntry> entries;
  //todo>> it will be needed to save user dictionaries// private final String userWordsDictionaryFileName;
  //todo>> it will be needed to save user dictionaries//  private final String userPhrasesDictionaryFileName;
  private Alphabet alphabet;
  private String isoLanguageName;
  private String userId;
  
  private String userWordsDictionaryFileName;
  private String userPhrasesDictionaryFileName;
  
  private predictor.registry.Dictionary mainDictionary;
  private predictor.registry.Dictionary userWordsDictionary;
  private predictor.registry.Dictionary userPhrasesDictionary;
  
  private FileTime mainDictionaryFileLastModifiedTime;
  private FileTime userWordsDictionaryFileLastModifiedTime;
  private FileTime userPhrasesDictionaryFileLastModifiedTime;
  
  //statistical values
  private Integer maxStringLength;
  private Double maxIPM;
  private Double totalUserStringsUses;
  
  public SearchDictionary(predictor.registry.Dictionary mainDictionary, predictor.registry.Dictionary userWordsDictionary, predictor.registry.Dictionary userPhrasesDictionary) {
    this(mainDictionary, userWordsDictionary, userPhrasesDictionary, null);
  }
  
  public SearchDictionary(predictor.registry.Dictionary mainDictionary, predictor.registry.Dictionary userWordsDictionary, predictor.registry.Dictionary userPhrasesDictionary, Alphabet alphabet) {
    this.mainDictionary = mainDictionary;
    this.userWordsDictionary = userWordsDictionary;
    this.userPhrasesDictionary = userPhrasesDictionary;
    makeSearchDictionary(alphabet);
  }
  
  public SearchDictionary(String mainDictionaryFileName) {
    this(
        mainDictionaryFileName,
        "",
        "",
        null
    );
  }
  
  public SearchDictionary(String mainDictionaryFileName, String userWordsDictionaryFileName) {
    this(
        mainDictionaryFileName,
        userWordsDictionaryFileName,
        "",
        null
    );
  }
  
  public SearchDictionary(String mainDictionaryFileName, String userWordsDictionaryFileName, String userPhrasesDictionaryFileName) {
    this(
        mainDictionaryFileName,
        userWordsDictionaryFileName,
        userPhrasesDictionaryFileName,
        null
    );
  }
  
  public SearchDictionary(String mainDictionaryFileName, String userWordsDictionaryFileName, String userPhrasesDictionaryFileName, Alphabet alphabet) {
    //todo>> add a check of a user and language compliance between main and user's dictionaries
    LOGGER.debug("Building of the search dictionary has started with dictionaries: " +
                     "\r\n\t - main dictionary '{}'" +
                     "\r\n\t - user words dictionary '{}' " +
                     "\r\n\t - user phrases dictionary '{}'"
        , mainDictionaryFileName, userWordsDictionaryFileName, userPhrasesDictionaryFileName);
    try {
      BasicFileAttributes attributes = Files.readAttributes(Paths.get(mainDictionaryFileName), BasicFileAttributes.class);
      this.mainDictionary = new predictor.registry.Dictionary(mainDictionaryFileName);
      this.mainDictionaryFileLastModifiedTime = attributes.lastModifiedTime();
      if (!userWordsDictionaryFileName.equals("")) {
        this.userWordsDictionary = new predictor.registry.Dictionary(userWordsDictionaryFileName);
        this.userWordsDictionaryFileName = userWordsDictionaryFileName;
        attributes = Files.readAttributes(Paths.get(userWordsDictionaryFileName), BasicFileAttributes.class);
        this.userWordsDictionaryFileLastModifiedTime = attributes.lastModifiedTime();
      } else {
        LOGGER.warn("There is no user's words dictionary file name");
        this.userWordsDictionary = new predictor.registry.Dictionary();
      }
      if (!userPhrasesDictionaryFileName.equals("")) {
        this.userPhrasesDictionary = new predictor.registry.Dictionary(userPhrasesDictionaryFileName);
        this.userPhrasesDictionaryFileName = userPhrasesDictionaryFileName;
        attributes = Files.readAttributes(Paths.get(userPhrasesDictionaryFileName), BasicFileAttributes.class);
        this.userPhrasesDictionaryFileLastModifiedTime = attributes.lastModifiedTime();
      } else {
        LOGGER.warn("There is no user's phrases dictionary file name");
        this.userPhrasesDictionary = new predictor.registry.Dictionary();
      }
      
      this.isoLanguageName = mainDictionary.getIsoLanguageName();//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
      this.userId = userWordsDictionary.getUserId();
      
      this.makeSearchDictionary(alphabet);
      
      LOGGER.debug("Building of the search dictionary with dictionaries:" +
                       "\r\n\t - main dictionary '{}'" +
                       "\r\n\t - user words dictionary '{}' " +
                       "\r\n\t - user phrases dictionary '{}'" +
                       "\r\n has finished..."
          , mainDictionaryFileName, userWordsDictionaryFileName, userPhrasesDictionaryFileName);
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }
  }
  
  public void makeSearchDictionary(Alphabet alphabet) {
    boolean dictionariesAreOfTheSameLanguage = (mainDictionary.getIsoLanguageName().equals(userWordsDictionary.getIsoLanguageName()))
                                                   && (mainDictionary.getIsoLanguageName().equals(userPhrasesDictionary.getIsoLanguageName()));
    if (!dictionariesAreOfTheSameLanguage) {
      LOGGER.error("Error: main dictionary and users dictionary should be the same language to make search dictionary.");
      throw new RuntimeException("Error: main dictionary and users dictionary should be the same language to make search dictionary.");
    }
    
    this.isoLanguageName = this.mainDictionary.getIsoLanguageName();//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
    this.userId = this.userWordsDictionary.getUserId();
    
    Set<SearchDictionaryEntry> entriesSet = new HashSet<>();
    entriesSet.addAll(this.userWordsDictionary.getEntries().stream()
                          .map(e -> new SearchDictionaryEntry(e.getString(), 0D, e.getFrequency(), e.getLastUseTime()))
                          .collect(Collectors.toSet())
    );
    entriesSet.addAll(this.userPhrasesDictionary.getEntries().stream()
                          .map(e -> new SearchDictionaryEntry(e.getString(), 0D, e.getFrequency(), e.getLastUseTime()))
                          .collect(Collectors.toSet())
    );
    entriesSet.addAll(this.mainDictionary.getEntries().stream()
                          .map(e -> new SearchDictionaryEntry(e.getString(), e.getFrequency(), 0D))
                          .collect(Collectors.toSet())
    );
    
    this.mainDictionary
        .mergeDictionary(this.userWordsDictionary, false)
        .mergeDictionary(this.userPhrasesDictionary, false)
    ;
    this.maxStringLength = this.mainDictionary.getMaxStringLength();
    this.maxIPM = this.mainDictionary.getEntries().stream().map(Entry::getFrequency).max(Double::compare).orElse(0D);
    this.totalUserStringsUses = this.userWordsDictionary.getEntries().stream().mapToDouble(Entry::getFrequency).sum()
                                  + this.userPhrasesDictionary.getEntries().stream().mapToDouble(Entry::getFrequency).sum();
    
    entriesSet.stream()
        .filter(e -> userWordsDictionary.getEntry(e) != null || userPhrasesDictionary.getEntry(e) != null)
        .forEach(e -> e.setFrequency(mainDictionary.getEntry(e).getFrequency()))
    ;
    
    this.entries = new ArrayList<>(entriesSet);
    
    if (alphabet == null) {
//      this.alphabet = new Alphabet(this.getCharSet(), this.isoLanguageName);
      this.alphabet = new Alphabet(mainDictionary.getCharSet(), this.isoLanguageName);
    } else if (this.isoLanguageName.equals(alphabet.getIsoLanguageName())) {
      this.alphabet = alphabet;
    } else {
      LOGGER.error("Runtime error in SearchDictionary constructor: Language of the alphabet specified doesn't match with searchDictionary language");
      throw new RuntimeException("Error: Language of the alphabet specified doesn't match with searchDictionary language");
    }
    
  }
  
  public String getUserWordsDictionaryFileName() {
    return this.userWordsDictionaryFileName;
  }
  
  public String getUserPhrasesDictionaryFileName() {
    return this.userPhrasesDictionaryFileName;
  }
  
  public predictor.registry.Dictionary getMainDictionary() {
    return this.mainDictionary;
  }
  
  public predictor.registry.Dictionary getUserWordsDictionary() {
    return this.userWordsDictionary;
  }
  
  public predictor.registry.Dictionary getUserPhrasesDictionary() {
    return userPhrasesDictionary;
  }
  
  public Double getMaxIPM() {
    return this.maxIPM;
  }
  
  public Double getTotalUserStringsUses() {
    return this.totalUserStringsUses;
  }
  
  public FileTime[] getDictionariesLastModifiedTimes() {
    return new FileTime[]{this.mainDictionaryFileLastModifiedTime, this.userWordsDictionaryFileLastModifiedTime, this.userPhrasesDictionaryFileLastModifiedTime};
  }
  
  public void setDictionariesLastModifiedTimes(FileTime[] lastTimesModified) {
    this.mainDictionaryFileLastModifiedTime = lastTimesModified[0];
    this.userWordsDictionaryFileLastModifiedTime = lastTimesModified[1];
    this.userPhrasesDictionaryFileLastModifiedTime = lastTimesModified[2];
  }
  
  public static Set<Character> getCharSet(List<SearchDictionaryEntry> entries) {
    Set<Character> result = new HashSet<>();
    for (Entry entry : entries) {
      char[] chars = entry.getString().toCharArray();
      for (char ch : chars) {
        result.add(ch);
      }
    }
    return result;
  }
  
  public Set<Character> getCharSet() {
    return SearchDictionary.getCharSet(this.entries);
  }
  
  public static Alphabet getAlphabet(String dictionaryFileName) throws IOException {
    predictor.registry.Dictionary dictionary = new predictor.registry.Dictionary(dictionaryFileName);
    return new Alphabet(dictionary.getCharSet(), dictionary.getIsoLanguageName());
  }
  
  public static Alphabet getAlphabet(Set<SearchDictionaryEntry> entries, String isoLanguageName) {
    if (entries == null) {
      return null;
    }
    Set<Character> charSet = new HashSet<>();
    for (Entry entry : entries) {
      char[] chars = entry.getString().toCharArray();
      for (char ch : chars) {
        charSet.add(ch);
      }
    }
    return new Alphabet(charSet, isoLanguageName);
  }
  
  public Alphabet getAlphabet() {
    return this.alphabet;
  }
  
  public boolean setAlphabet(Alphabet alphabet) {
    if (alphabet.getIsoLanguageName().equals(this.isoLanguageName)) {
      this.alphabet = alphabet;
      return true;
    }
    return false;
  }
  
  public String getIsoLanguageName() {
    return this.isoLanguageName;
  }
  
  public String getUserId() {
    return this.userId;
  }
  
  /**
   * Returns length of the largest string in entries set of the dictionary.
   *
   * @param entries - entries set to search in
   * @return - int value of length of the largest string in the entries set
   */
  static public int getMaxStringLength(Set<SearchDictionaryEntry> entries) {
    return entries.stream().mapToInt(entry -> entry.getString().length()).max().orElse(0);
  }
  
  static public int getMaxStringLength(List<SearchDictionaryEntry> entries) {
    return entries.stream().mapToInt(entry -> entry.getString().length()).max().orElse(0);
  }
  
  public int getMaxStringLength() {
    return this.getMaxStringLength(false);
  }
  
  public int getMaxStringLength(boolean recalculate) {
    if (recalculate) {
      this.maxStringLength = SearchDictionary.getMaxStringLength(this.entries);
    } else {
      LOGGER.warn("maxStringLength returned without recalculation be sure that its value is actual");
    }
    return this.maxStringLength;
  }
  
  public SearchDictionaryEntry getSearchDictionaryEntry(SearchDictionaryEntry entry) {
    return this.getSearchDictionaryEntry(entry.getString());
  }
  
  public SearchDictionaryEntry getSearchDictionaryEntry(Entry entry) {
    return this.getSearchDictionaryEntry(entry.getString());
  }
  
  public SearchDictionaryEntry getSearchDictionaryEntry(String string) {
    return this.entries.stream().filter(e -> e.getString().equals(string)).findAny().orElse(null);
  }
  
  public List<SearchDictionaryEntry> getSearchDictionaryEntries() {
    return this.entries;
  }
  
  public void setSearchDictionaryEntries(List<SearchDictionaryEntry> entries) {
    this.entries = entries;
  }
  
  public boolean addSearchDictionaryEntry(predictor.registry.Dictionary userDictionary, String string) {
    return this.addSearchDictionaryEntry(userDictionary, string, 1D, 1D, null);
  }
  
  public boolean addSearchDictionaryEntry(predictor.registry.Dictionary userDictionary, String string, Double frequency) {
    return this.addSearchDictionaryEntry(userDictionary, string, frequency, frequency, null);
  }
  
  public boolean addSearchDictionaryEntry(predictor.registry.Dictionary userDictionary, Entry entry) {
    return this.addSearchDictionaryEntry(userDictionary, entry.getString(), entry.frequency, entry.frequency, null);
  }
  
  public boolean addSearchDictionaryEntry(predictor.registry.Dictionary userDictionary, String string, Double frequency, Double localFrequency, LocalDateTime lastUseTime) {
    return this.addSearchDictionaryEntry(userDictionary, new SearchDictionaryEntry(string, frequency, localFrequency, lastUseTime));
  }
  
  public boolean addSearchDictionaryEntry(Dictionary userDictionary, SearchDictionaryEntry entry) {
    try {
      String string = entry.getString();
      if (userDictionary.addEntry(string, entry.getFrequency())) {
        if (string.length() > this.maxStringLength) {
          this.maxStringLength = string.length();
        }
        return this.entries.add(entry);
      }
      return false;
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      return false;
    }
  }
  
  public boolean addAllEntries(Set<SearchDictionaryEntry> entries) {
    try {
      int newStringsMaxLength = SearchDictionary.getMaxStringLength(entries);
      if (this.maxStringLength < newStringsMaxLength) {
        this.maxStringLength = newStringsMaxLength;
      }
      return this.entries.addAll(entries);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      return false;
    }
  }
  
  public SearchDictionaryEntry removeSearchDictionaryEntry(SearchDictionaryEntry entry) {
    return this.removeSearchDictionaryEntry(entry.getString());
  }
  
  public SearchDictionaryEntry removeSearchDictionaryEntry(Entry entry) {
    return this.removeSearchDictionaryEntry(entry.getString());
  }
  
  public SearchDictionaryEntry removeSearchDictionaryEntry(String string) {
    SearchDictionaryEntry entry = this.getSearchDictionaryEntry(string);
    if (entry != null) {
      this.entries.remove(entry);
      if (this.maxStringLength == string.length()) {
        this.maxStringLength = this.getMaxStringLength(true);
      }
      return entry;
    } else {
      return null;
    }
  }
  
  public boolean removeAllSearchDictionaryEntries(List<SearchDictionaryEntry> entries) {
    Integer removedStringsMaxLength = SearchDictionary.getMaxStringLength(entries);
    boolean isAnythingRemoved = this.entries.removeAll(entries);
    if (isAnythingRemoved && this.maxStringLength.equals(removedStringsMaxLength)) {
      this.maxStringLength = SearchDictionary.getMaxStringLength(this.entries);
    }
    return isAnythingRemoved;
  }
  
  public SearchDictionaryEntry updateSearchDictionaryEntry(String string, Double frequency) {
    SearchDictionaryEntry searchDictionaryEntryToBeReplaced = this.getSearchDictionaryEntry(string);
    return this.updateSearchDictionaryEntry(
        new SearchDictionaryEntry(
            string,
            frequency,
            searchDictionaryEntryToBeReplaced.getLocalFrequency(),
            searchDictionaryEntryToBeReplaced.lastUseTime));
  }
  
  public SearchDictionaryEntry updateSearchDictionaryEntry(Entry entry) {
    SearchDictionaryEntry searchDictionaryEntry = this.getSearchDictionaryEntry(entry);
    return this.updateSearchDictionaryEntry(
        new SearchDictionaryEntry(
            entry.getString(),
            entry.getFrequency(),
            searchDictionaryEntry.getFrequency(),
            searchDictionaryEntry.lastUseTime));
  }
  
  public SearchDictionaryEntry updateSearchDictionaryEntry(String string, Double frequency, Double localFrequency) {
    SearchDictionaryEntry searchDictionaryEntry = this.getSearchDictionaryEntry(string);
    return this.updateSearchDictionaryEntry(
        new SearchDictionaryEntry(
            string,
            frequency,
            localFrequency,
            searchDictionaryEntry.lastUseTime));
  }
  
  public SearchDictionaryEntry updateSearchDictionaryEntry(String string, Double frequency, Double localFrequency, LocalDateTime lastUseTime) {
    return this.updateSearchDictionaryEntry(
        new SearchDictionaryEntry(
            string,
            frequency,
            localFrequency,
            lastUseTime));
  }
  
  public SearchDictionaryEntry updateSearchDictionaryEntry(SearchDictionaryEntry entry) {
    int index = this.entries.indexOf(this.getSearchDictionaryEntry(entry));
    if (index != -1) {
      return this.entries.set(index, entry);
    }
    return null;
  }
  
  public boolean saveUserWords() throws IOException { //todo>> add a backup flag (if flag set into true then before saving a new version of the dictionary file old one will be copied into the backup file with an autogenerated name)
    return this.userWordsDictionary.save(this.userWordsDictionaryFileName);
  }
  
  public boolean saveUserPhrases() throws IOException { //todo>> add a backup flag (if flag set into true then before saving a new version of the dictionary file old one will be copied into the backup file with an autogenerated name)
    return this.userPhrasesDictionary.save(this.userPhrasesDictionaryFileName);
  }
  
}
