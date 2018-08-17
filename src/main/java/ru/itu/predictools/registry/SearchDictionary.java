package ru.itu.predictools.registry;

import ru.itu.predictools.alphabet.Alphabet;

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

// SearchDictionary contains words and their lexical parameters and other data in structures named Entry
//@SuppressWarnings({"UnusedReturnValue", "unused", "WeakerAccess"})
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
  
  private Dictionary mainDictionary;
  private Dictionary userWordsDictionary;
  private Dictionary userPhrasesDictionary;
  
  private FileTime mainDictionaryFileLastModifiedTime = FileTime.fromMillis(0);
  private FileTime userWordsDictionaryFileLastModifiedTime = FileTime.fromMillis(0);
  private FileTime userPhrasesDictionaryFileLastModifiedTime = FileTime.fromMillis(0);
  
  //statistical values
  private Integer maxWordLength;
  private Double rangeOfIPM;
  private Double totalUserWordsUses;
  
  public SearchDictionary(Dictionary mainDictionary, Dictionary userWordsDictionary, Dictionary userPhrasesDictionary) {
    this(mainDictionary, userWordsDictionary, userPhrasesDictionary, null);
  }
  
  public SearchDictionary(Dictionary mainDictionary, Dictionary userWordsDictionary, Dictionary userPhrasesDictionary, Alphabet alphabet) {
    this.mainDictionary = mainDictionary;
    this.userWordsDictionary = userWordsDictionary;
    this.userPhrasesDictionary = userPhrasesDictionary;
    
    this.isoLanguageName = mainDictionary.getIsoLanguageName();//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
    this.userId = userWordsDictionary.getUserId();
    
    makeSearchDictionary();
    
    if (alphabet == null) {
      this.alphabet = new Alphabet(this.getCharSet(), this.isoLanguageName);
      this.alphabet = new Alphabet(mainDictionary.getCharSet(), this.isoLanguageName);
    } else if (this.isoLanguageName.equals(alphabet.getIsoLanguageName())) {
      this.alphabet = alphabet;
    } else {
      LOGGER.error("Runtime error in SearchDictionary constructor: Language of the alphabet specified doesn't match with searchDictionary language");
      throw new RuntimeException("Error: Language of the alphabet specified doesn't match with searchDictionary language");
    }
  }
  
  public SearchDictionary(String mainDictionaryFileName) throws IOException {
    this(
        mainDictionaryFileName,
        "",
        "",
        null
    );
  }
  
  public SearchDictionary(String mainDictionaryFileName, String userWordsDictionaryFileName) throws IOException {
    this(
        mainDictionaryFileName,
        userWordsDictionaryFileName,
        "",
        null
    );
  }
  
  public SearchDictionary(String mainDictionaryFileName, String userWordsDictionaryFileName, String userPhrasesDictionaryFileName) throws IOException {
    this(
        mainDictionaryFileName,
        userWordsDictionaryFileName,
        userPhrasesDictionaryFileName,
        null
    );
  }
  
  public SearchDictionary(String mainDictionaryFileName, String userWordsDictionaryFileName, String userPhrasesDictionaryFileName, Alphabet alphabet) throws IOException {
    //todo>> add a check of a user and language compliance between main and user's dictionaries
    LOGGER.info("Building of the search dictionary has started...");
    BasicFileAttributes attributes = Files.readAttributes(Paths.get(mainDictionaryFileName), BasicFileAttributes.class);
    this.mainDictionary = new Dictionary(mainDictionaryFileName);
    this.mainDictionaryFileLastModifiedTime = attributes.lastModifiedTime();
    if (!userWordsDictionaryFileName.equals("")) {
      this.userWordsDictionary = new Dictionary(userWordsDictionaryFileName);
      this.userWordsDictionaryFileName = userWordsDictionaryFileName;
      attributes = Files.readAttributes(Paths.get(userWordsDictionaryFileName), BasicFileAttributes.class);
      this.userWordsDictionaryFileLastModifiedTime = attributes.lastModifiedTime();
    } else {
      LOGGER.warn("There is no user's words dictionary file name");
      this.userWordsDictionary = new Dictionary();
    }
    if (!userPhrasesDictionaryFileName.equals("")) {
      this.userPhrasesDictionary = new Dictionary(userPhrasesDictionaryFileName);
      this.userPhrasesDictionaryFileName = userPhrasesDictionaryFileName;
      attributes = Files.readAttributes(Paths.get(userPhrasesDictionaryFileName), BasicFileAttributes.class);
      this.userPhrasesDictionaryFileLastModifiedTime = attributes.lastModifiedTime();
    } else {
      LOGGER.warn("There is no user's phrases dictionary file name");
      this.userPhrasesDictionary = new Dictionary();
    }
    
    this.isoLanguageName = mainDictionary.getIsoLanguageName();//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
    this.userId = userWordsDictionary.getUserId();
    
    makeSearchDictionary();
    
    if (alphabet == null) {
      this.alphabet = new Alphabet(this.getCharSet(), this.isoLanguageName);
      this.alphabet = new Alphabet(mainDictionary.getCharSet(), this.isoLanguageName);
    } else if (this.isoLanguageName.equals(alphabet.getIsoLanguageName())) {
      this.alphabet = alphabet;
    } else {
      LOGGER.error("Runtime error in SearchDictionary constructor: Language of the alphabet specified doesn't match with searchDictionary language");
      throw new Error("Error: Language of the alphabet specified doesn't match with searchDictionary language");
    }
    
    LOGGER.info("Building of the search dictionary has finished...");
  }
  
  public void makeSearchDictionary() {
    boolean dictionariesAreOfTheSameLanguage = (this.mainDictionary.getIsoLanguageName().equals(this.userWordsDictionary.getIsoLanguageName()))
                                                   && (this.mainDictionary.getIsoLanguageName().equals(this.userPhrasesDictionary.getIsoLanguageName()));
    if (!dictionariesAreOfTheSameLanguage) {
      LOGGER.error("Error: main dictionary and users dictionary should be the same language to make search dictionary.");
      throw new RuntimeException("Error: main dictionary and users dictionary should be the same language to make search dictionary.");
    }
    
    Set<SearchDictionaryEntry> entriesSet = new HashSet<>();
    entriesSet.addAll(this.userWordsDictionary.getEntries().stream()
                          .map(e -> new SearchDictionaryEntry(e.getWord(), 0D, e.getFrequency(), e.getLastUseTime()))
                          .collect(Collectors.toSet())
    );
    entriesSet.addAll(this.userPhrasesDictionary.getEntries().stream()
                          .map(e -> new SearchDictionaryEntry(e.getWord(), 0D, e.getFrequency(), e.getLastUseTime()))
                          .collect(Collectors.toSet())
    );
    entriesSet.addAll(this.mainDictionary.getEntries().stream()
                          .map(e -> new SearchDictionaryEntry(e.getWord(), e.getFrequency(), 0D))
                          .collect(Collectors.toSet())
    );
    
    this.mainDictionary
        .mergeDictionary(this.userWordsDictionary, false)
        .mergeDictionary(this.userPhrasesDictionary, false)
    ;
    this.maxWordLength = mainDictionary.getMaxWordLength();
    this.rangeOfIPM = this.mainDictionary.getEntries().stream().map(Entry::getFrequency).max(Double::compare).orElse(0D);
    this.totalUserWordsUses = this.userWordsDictionary.getEntries().stream().mapToDouble(Entry::getFrequency).sum()
                                  + this.userPhrasesDictionary.getEntries().stream().mapToDouble(Entry::getFrequency).sum();
    
    entriesSet.stream()
        .filter(e -> userWordsDictionary.getEntry(e) != null || userPhrasesDictionary.getEntry(e) != null)
        .forEach(e -> e.setFrequency(mainDictionary.getEntry(e).getFrequency()))
    ;
    
    this.entries = new ArrayList<>(entriesSet);
    
  }
  
  public Dictionary getMainDictionary() {
    return this.mainDictionary;
  }
  
  public Dictionary getUserWordsDictionary() {
    return this.userWordsDictionary;
  }
  
  public Dictionary getUserPhrasesDictionary() {
    return userPhrasesDictionary;
  }
  
  public Double getRangeOfIPM(){
    return this.rangeOfIPM;
  }
  
  public Double getTotalUserWordsUses(){
    return this.totalUserWordsUses;
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
      char[] chars = entry.getWord().toCharArray();
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
    Dictionary dictionary = new Dictionary(dictionaryFileName);
    return new Alphabet(dictionary.getCharSet(), dictionary.getIsoLanguageName());
  }
  
  public static Alphabet getAlphabet(Set<SearchDictionaryEntry> entries, String isoLanguageName) {
    if (entries == null) {
      return null;
    }
    Set<Character> charSet = new HashSet<>();
    for (Entry entry : entries) {
      char[] chars = entry.getWord().toCharArray();
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
   * Returns length of the largest word in entries set of the dictionary.
   *
   * @param entries - entries set to search in
   * @return - int value of length of the largest word in the entries set
   */
  static public int getMaxWordLength(Set<SearchDictionaryEntry> entries) {
    return entries.stream().mapToInt(entry -> entry.getWord().length()).max().orElse(0);
  }
  
  static public int getMaxWordLength(List<SearchDictionaryEntry> entries) {
    return entries.stream().mapToInt(entry -> entry.getWord().length()).max().orElse(0);
  }
  
  public int getMaxWordLength() {
    return this.getMaxWordLength(false);
  }
  
  public int getMaxWordLength(boolean recalculate) {
    if (recalculate) {
      this.maxWordLength = SearchDictionary.getMaxWordLength(this.entries);
    } else {
      LOGGER.warn("maxWordLength returned without recalculation be sure that its value is actual");
    }
    return this.maxWordLength;
  }
  
  public SearchDictionaryEntry getSearchDictionaryEntry(SearchDictionaryEntry entry) {
    return this.getSearchDictionaryEntry(entry.getWord());
  }
  
  public SearchDictionaryEntry getSearchDictionaryEntry(Entry entry) {
    return this.getSearchDictionaryEntry(entry.getWord());
  }
  
  public SearchDictionaryEntry getSearchDictionaryEntry(String word) {
    return this.entries.stream().filter(e -> e.getWord().equals(word)).findAny().orElse(null);
  }
  
  public List<SearchDictionaryEntry> getSearchDictionaryEntries() {
    return this.entries;
  }
  
  public void setSearchDictionaryEntries(List<SearchDictionaryEntry> entries) {
    this.entries = entries;
  }
  
  public boolean addSearchDictionaryEntry(String word) {
    return this.addSearchDictionaryEntry(word, 1D, 1D, null);
  }
  
  public boolean addSearchDictionaryEntry(String word, Double frequency) {
    return this.addSearchDictionaryEntry(word, frequency, frequency, null);
  }
  
  public boolean addSearchDictionaryEntry(Entry entry) {
    return this.addSearchDictionaryEntry(entry.getWord(), entry.frequency, entry.frequency, null);
  }
  
  public boolean addSearchDictionaryEntry(String word, Double frequency, Double localFrequency, LocalDateTime lastUseTime) {
    return this.addSearchDictionaryEntry(new SearchDictionaryEntry(word, frequency, localFrequency, lastUseTime));
  }
  
  public boolean addSearchDictionaryEntry(SearchDictionaryEntry entry) {
    try {
      String word = entry.getWord();
      if (this.userWordsDictionary.addEntry(word, entry.getFrequency())) {
        if (word.length() > this.maxWordLength) {
          this.maxWordLength = word.length();
        }
      }
      return this.entries.add(entry);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      return false;
    }
  }
  
  public boolean addAllEntries(Set<SearchDictionaryEntry> entries) {
    try {
      int newWordsMaxLength = SearchDictionary.getMaxWordLength(entries);
      if (this.maxWordLength < newWordsMaxLength) {
        this.maxWordLength = newWordsMaxLength;
      }
      return this.entries.addAll(entries);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      return false;
    }
  }
  
  public SearchDictionaryEntry removeSearchDictionaryEntry(SearchDictionaryEntry entry) {
    return this.removeSearchDictionaryEntry(entry.getWord());
  }
  
  public SearchDictionaryEntry removeSearchDictionaryEntry(Entry entry) {
    return this.removeSearchDictionaryEntry(entry.getWord());
  }
  
  public SearchDictionaryEntry removeSearchDictionaryEntry(String word) {
    SearchDictionaryEntry entry = this.getSearchDictionaryEntry(word);
    if (entry != null) {
      this.entries.remove(entry);
      if (this.maxWordLength == word.length()) {
        this.maxWordLength = this.getMaxWordLength(true);
      }
      return entry;
    } else {
      return null;
    }
  }
  
  public boolean removeAllSearchDictionaryEntries(List<SearchDictionaryEntry> entries) {
    Integer removedWordsMaxLength = SearchDictionary.getMaxWordLength(entries);
    boolean isAnythingRemoved = this.entries.removeAll(entries);
    if (isAnythingRemoved && this.maxWordLength.equals(removedWordsMaxLength)) {
      this.maxWordLength = SearchDictionary.getMaxWordLength(this.entries);
    }
    return isAnythingRemoved;
  }
  
  public SearchDictionaryEntry updateSearchDictionaryEntry(String word, Double frequency) {
    SearchDictionaryEntry searchDictionaryEntryToBeReplaced = this.getSearchDictionaryEntry(word);
    return this.updateSearchDictionaryEntry(
        new SearchDictionaryEntry(
            word,
            frequency,
            searchDictionaryEntryToBeReplaced.getLocalFrequency(),
            searchDictionaryEntryToBeReplaced.lastUseTime));
  }
  
  public SearchDictionaryEntry updateSearchDictionaryEntry(Entry entry) {
    SearchDictionaryEntry searchDictionaryEntry = this.getSearchDictionaryEntry(entry);
    return this.updateSearchDictionaryEntry(
        new SearchDictionaryEntry(
            entry.getWord(),
            entry.getFrequency(),
            searchDictionaryEntry.getFrequency(),
            searchDictionaryEntry.lastUseTime));
  }
  
  public SearchDictionaryEntry updateSearchDictionaryEntry(String word, Double frequency, Double localFrequency) {
    SearchDictionaryEntry searchDictionaryEntry = this.getSearchDictionaryEntry(word);
    return this.updateSearchDictionaryEntry(
        new SearchDictionaryEntry(
            word,
            frequency,
            localFrequency,
            searchDictionaryEntry.lastUseTime));
  }
  
  public SearchDictionaryEntry updateSearchDictionaryEntry(String word, Double frequency, Double localFrequency, LocalDateTime lastUseTime) {
    return this.updateSearchDictionaryEntry(
        new SearchDictionaryEntry(
            word,
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
  
  public boolean save(String fileName) { //todo>> add a backup flag (if flag set into true then before saving a new version of the dictionary file old one will be copied into the backup file with an autogenerated name)
    String line;
    int n = 0;
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + File.separator + fileName, false))) {
      line = "en = " + this.isoLanguageName + "\r\n";
      writer.append(line);
      line = "user = " + this.userId + "\r\n";
      writer.append(line);
      line = "start\r\n";
      writer.append(line);
      for (SearchDictionaryEntry e : this.entries) {
        line = Integer.toString(++n) + "," + Double.toString(e.getFrequency()) + "," + Double.toString(e.getLocalFrequency()) + "," + e.getLastUseTime() + "," + e.getWord() + "\r\n";
        writer.append(line);
      }
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      return false;
    }
    return true;
  }
  
}
