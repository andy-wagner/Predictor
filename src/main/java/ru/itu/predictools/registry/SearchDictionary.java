package ru.itu.predictools.registry;

import ru.itu.predictools.alphabet.Alphabet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.stream.Collectors;

// SearchDictionary contains words and their lexical parameters and other data in structures named Entry
@SuppressWarnings({"UnusedReturnValue", "unused", "WeakerAccess"})
public class SearchDictionary {
  //entries need to be an ArrayList because we will need to be able to get an element from a specific position within the list (with method .get(i))
  private List<SearchDictionaryEntry> entries;
  //todo>> it will be needed to save user dictionaries// private final String userWordsDictionaryFileName;
  //todo>> it will be needed to save user dictionaries//  private final String userPhrasesDictionaryFileName;
  private Alphabet alphabet;
  private String isoLanguageName;
  Dictionary mainDictionary;
  Dictionary userWordsDictionary;
  Dictionary userPhrasesDictionary;
  FileTime mainDictionaryFileLastModifiedTime = FileTime.fromMillis(0);
  FileTime userWordsDictionaryFileLastModifiedTime = FileTime.fromMillis(0);
  FileTime userPhrasesDictionaryFileLastModifiedTime = FileTime.fromMillis(0);

//  private int maxWordLength;
  
  public SearchDictionary(
      String mainDictionaryFileName
  ) throws IOException {
    this(
        mainDictionaryFileName,
        "",
        "",
        null
    );
  }
  
  public SearchDictionary(
      String mainDictionaryFileName,
      String userWordsDictionaryFileName
  ) throws IOException {
    this(
        mainDictionaryFileName,
        userWordsDictionaryFileName,
        "",
        null
    );
  }
  
  public SearchDictionary(
      String mainDictionaryFileName,
      String userWordsDictionaryFileName,
      String userPhrasesDictionaryFileName
  ) throws IOException {
    this(
        mainDictionaryFileName,
        userWordsDictionaryFileName,
        userPhrasesDictionaryFileName,
        null
    );
  }
  
  public SearchDictionary(
      String mainDictionaryFileName,
      String userWordsDictionaryFileName,
      String userPhrasesDictionaryFileName,
      Alphabet alphabet
  ) throws IOException {
    
    this.mainDictionary = new Dictionary(mainDictionaryFileName);
    BasicFileAttributes attributes = Files.readAttributes(Paths.get(mainDictionaryFileName), BasicFileAttributes.class);
    this.mainDictionaryFileLastModifiedTime = attributes.lastModifiedTime();
    if (!userWordsDictionaryFileName.equals("")) {
      this.userWordsDictionary = new Dictionary(userWordsDictionaryFileName);
      attributes = Files.readAttributes(Paths.get(userWordsDictionaryFileName), BasicFileAttributes.class);
      this.userWordsDictionaryFileLastModifiedTime = attributes.lastModifiedTime();
    } else {
      this.userWordsDictionary = new Dictionary();
    }
    if (!userPhrasesDictionaryFileName.equals("")) {
      this.userPhrasesDictionary = new Dictionary(userPhrasesDictionaryFileName);
      attributes = Files.readAttributes(Paths.get(userPhrasesDictionaryFileName), BasicFileAttributes.class);
      this.userPhrasesDictionaryFileLastModifiedTime = attributes.lastModifiedTime();
    } else {
      this.userPhrasesDictionary = new Dictionary();
    }
    
    this.isoLanguageName = mainDictionary.getIsoLanguageName();//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes

//todo>> it will be needed to save user dictionaries//    this.userWordsDictionaryFileName = userWordsDictionaryFileName;
//todo>> it will be needed to save user dictionaries//    this.userPhrasesDictionaryFileName = userPhrasesDictionaryFileName;
    
    Set<SearchDictionaryEntry> entriesSet = new HashSet<>();
    entriesSet.addAll(userWordsDictionary.getEntries().stream()
                          .map(e -> new SearchDictionaryEntry(e.getWord(), 0D, e.getFrequency(), e.getLastUseTime()))
                          .collect(Collectors.toSet())
    );
    entriesSet.addAll(userPhrasesDictionary.getEntries().stream()
                          .map(e -> new SearchDictionaryEntry(e.getWord(), 0D, e.getFrequency(), e.getLastUseTime()))
                          .collect(Collectors.toSet())
    );
    entriesSet.addAll(mainDictionary.getEntries().stream()
                          .map(e -> new SearchDictionaryEntry(e.getWord(), e.getFrequency(), 0D))
                          .collect(Collectors.toSet())
    );
    
    this.mainDictionary//todo!!! if word doesn't exist in main dictionary then it will be added with global frequency equal to local frequency from user dictionary
        .mergeDictionary(this.userWordsDictionary, false)//todo>> good only as first iteration need to consider optimisation current decision is too slow
        .mergeDictionary(this.userPhrasesDictionary, false)//todo>> need to consider optimisation current decision is too slow
    ;
//    this.maxWordLength = mainDictionary.getMaxWordLength();
    
    entriesSet.stream()//todo>> performance issues are possible, there is need to optimise algorithm
        .filter(e -> userWordsDictionary.getEntry(e) != null || userPhrasesDictionary.getEntry(e) != null)
        .forEach(e -> e.setFrequency(mainDictionary.getEntry(e).getFrequency()))
    ;

//    entriesSet.stream()
//        .filter(e -> mainDictionary.getEntry(e) == null)
//        .forEach(e -> e.setFrequency(0D))
//    ;
    
    this.entries = new ArrayList<>(entriesSet);
    
    if (alphabet == null) {
      this.alphabet = new Alphabet(mainDictionary.getCharsSet(), this.isoLanguageName);
    } else if (this.isoLanguageName.equals(alphabet.getIsoLanguageName())) {
      this.alphabet = alphabet;
    } else {
      throw new Error("Error: Language of the alphabet specified doesn't match with searchDictionary language");
    }
    
  }
  
  public FileTime[] getDictionariesLastTimesModified(){
    return new FileTime[]{this.mainDictionaryFileLastModifiedTime, this.userWordsDictionaryFileLastModifiedTime, this.userPhrasesDictionaryFileLastModifiedTime};
  }
  
  public void setDictionariesLastTimesModified(FileTime[] lastTimesModified){
    this.mainDictionaryFileLastModifiedTime = lastTimesModified[0];
    this.userWordsDictionaryFileLastModifiedTime = lastTimesModified[1];
    this.userPhrasesDictionaryFileLastModifiedTime = lastTimesModified[2];
  }
  
  public List<SearchDictionaryEntry> getEntries() {
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
  
  static public Alphabet getAlphabet(String dictionaryFileName) throws IOException {
    Dictionary dictionary = new Dictionary(dictionaryFileName);
    return new Alphabet(dictionary.getCharsSet(), dictionary.getIsoLanguageName());
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
    return SearchDictionary.getMaxWordLength(this.entries.stream().distinct().collect(Collectors.toSet()));
  }
  
  public SearchDictionaryEntry getEntry(SearchDictionaryEntry entry) {
    return this.entries.get(this.entries.indexOf(entry));
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
  
  public Dictionary getMainDictionary() {
    return this.mainDictionary;
  }
  
  public Dictionary getUserWordsDictionary() {
    return this.userWordsDictionary;
  }
  
  public Dictionary getUserPhrasesDictionary() {
    return userPhrasesDictionary;
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
