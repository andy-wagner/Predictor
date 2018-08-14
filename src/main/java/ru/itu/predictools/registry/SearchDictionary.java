package ru.itu.predictools.registry;

import ru.itu.predictools.alphabet.Alphabet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// SearchDictionary contains words and their lexical parameters and other data in structures named Entry
//@SuppressWarnings({"UnusedReturnValue", "unused", "WeakerAccess"})
@SuppressWarnings({"WeakerAccess", "unused", "FieldCanBeLocal"})
public class SearchDictionary {
  private static final Logger LOGGER = LogManager.getLogger();
  //entries need to be an ArrayList because we will need to be able to get an element from a specific position within the list (with method .get(i))
  private List<SearchDictionaryEntry> entries;
  //todo>> it will be needed to save user dictionaries// private final String userWordsDictionaryFileName;
  //todo>> it will be needed to save user dictionaries//  private final String userPhrasesDictionaryFileName;
  private Alphabet alphabet;
  private String isoLanguageName;
  
  private String userWordsDictionaryFileName;
  private String userPhrasesDictionaryFileName;
  
  private Dictionary mainDictionary;
  private Dictionary userWordsDictionary;
  private Dictionary userPhrasesDictionary;
  
  private FileTime mainDictionaryFileLastModifiedTime = FileTime.fromMillis(0);
  private FileTime userWordsDictionaryFileLastModifiedTime = FileTime.fromMillis(0);
  private FileTime userPhrasesDictionaryFileLastModifiedTime = FileTime.fromMillis(0);
  
  private int maxWordLength;
  
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
    
    LOGGER.info("Building of the search dictionary has started...");
    this.mainDictionary = new Dictionary(mainDictionaryFileName);
    BasicFileAttributes attributes = Files.readAttributes(Paths.get(mainDictionaryFileName), BasicFileAttributes.class);
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
    
    makeSearchDictionary();
    
    if (alphabet == null) {
      this.alphabet = new Alphabet(mainDictionary.getCharsSet(), this.isoLanguageName);
    } else if (this.isoLanguageName.equals(alphabet.getIsoLanguageName())) {
      this.alphabet = alphabet;
    } else {
      LOGGER.error("Runtime error in SearchDictionary constructor: Language of the alphabet specified doesn't match with searchDictionary language");
      throw new Error("Error: Language of the alphabet specified doesn't match with searchDictionary language");
    }
    
    LOGGER.info("Building of the search dictionary has finished...");
  }
  
  public void makeSearchDictionary() {
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
  
  public FileTime[] getDictionariesLastTimesModified() {
    return new FileTime[]{this.mainDictionaryFileLastModifiedTime, this.userWordsDictionaryFileLastModifiedTime, this.userPhrasesDictionaryFileLastModifiedTime};
  }
  
  public void setDictionariesLastTimesModified(FileTime[] lastTimesModified) {
    this.mainDictionaryFileLastModifiedTime = lastTimesModified[0];
    this.userWordsDictionaryFileLastModifiedTime = lastTimesModified[1];
    this.userPhrasesDictionaryFileLastModifiedTime = lastTimesModified[2];
  }
  
  public List<SearchDictionaryEntry> getSearchDictionaryEntries() {
    return this.entries;
  }
  
  public static Set<Character> getCharsSet(Set<SearchDictionaryEntry> entries) {
    Set<Character> result = new HashSet<>();
    for (Entry entry : entries) {
      char[] chars = entry.getWord().toCharArray();
      for (char ch : chars) {
        result.add(ch);
      }
    }
    return result;
  }
  
  public static Alphabet getAlphabet(String dictionaryFileName) throws IOException {
    Dictionary dictionary = new Dictionary(dictionaryFileName);
    return new Alphabet(dictionary.getCharsSet(), dictionary.getIsoLanguageName());
  }
  
  public Alphabet getAlphabet() {
    return this.alphabet;
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
  
  /**
   * get length of the largest word in entries set
   *
   * @param entries - entries set to search in
   * @return - int value of length of the largest word in the entries set
   */
  static public int getMaxWordLength(Set<SearchDictionaryEntry> entries) {
    return entries
               .stream()
               .mapToInt(entry -> entry.getWord().length())
               .max().orElseThrow(NoSuchElementException::new)
        ;
  }
  
  public int getMaxWordLength() {
    return this.maxWordLength;
//    return SearchDictionary.getMaxWordLength(this.entries.stream().distinct().collect(Collectors.toSet()));
  }
  
  public SearchDictionaryEntry getEntry(SearchDictionaryEntry entry) {
    return this.entries.get(this.entries.indexOf(entry));
  }
  
  public SearchDictionaryEntry getEntry(Entry entry) {
    return this.entries.get(this.entries.indexOf(new SearchDictionaryEntry(entry)));
  }
  
  public SearchDictionaryEntry getEntry(String word) {
    return this.getEntry(new SearchDictionaryEntry(word));
  }
  
  public SearchDictionaryEntry updateEntry(SearchDictionaryEntry entry, SearchDictionaryEntry newEntry) {
    return this.entries.set(this.entries.indexOf(entry), newEntry);
  }
  
  /**
   * replaces the element specified by the word
   *
   * @param word - word of search dictionary entry that should be replaced
   * @return - the element previously at the specified position
   */
  public SearchDictionaryEntry updateEntry(String word, SearchDictionaryEntry newEntry) {
    return this.entries.set(this.entries.indexOf(new SearchDictionaryEntry(word)), newEntry);
  }
  
  //todo>> the stub code here
  public void save(String dictionaryFileName, boolean backup) throws IOException {
    this.save(dictionaryFileName);
  }
  
  //todo>> the stub code here
  public void save(String dictionaryFileName) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(dictionaryFileName));
  }
  
  //todo>> the stub code here
  static public void save(String dictionaryFileName, SearchDictionary searchDictionary) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(dictionaryFileName));
  }
  
  public Set<Character> getCharsSet() {
    return Dictionary.getCharsSet(this.mainDictionary.getEntries());
  }
}
