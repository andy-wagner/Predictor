package ru.itu.predictools.registry;

import ru.itu.predictools.alphabet.Alphabet;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

// SearchDictionary contains words and their lexical parameters and other data in structures named Entry
@SuppressWarnings({"UnusedReturnValue", "unused", "WeakerAccess"})
public class SearchDictionary {//extends dictionary{
  
  private List<SearchDictionaryEntry> entries;//entries in the List because we will need to get an element from a specified position within the list
//todo>> it will be needed to save user dictionaries// private final String userWordsDictionaryFileName;
//todo>> it will be needed to save user dictionaries//  private final String userPhrasesDictionaryFileName;
  private Alphabet alphabet;
  private final String isoLanguageName;
  private int maxWordLength;
  private String dictionaryFileName;
  
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
    
    Dictionary mainDictionary = new Dictionary(mainDictionaryFileName);
    Dictionary userWordsDictionary = new Dictionary(userWordsDictionaryFileName);
    Dictionary userPhrasesDictionary = new Dictionary(userPhrasesDictionaryFileName);
    
    this.isoLanguageName = mainDictionary.getIsoLanguageName();//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
//todo>> it will be needed to save user dictionaries//    this.userWordsDictionaryFileName = userWordsDictionaryFileName;
//todo>> it will be needed to save user dictionaries//    this.userPhrasesDictionaryFileName = userPhrasesDictionaryFileName;
    Set<Entry> mainDictionaryEntries = mainDictionary
                                           .mergeDictionary(userWordsDictionary, false)
                                           .mergeDictionary(userPhrasesDictionary, false)
                                           .getEntries();
    Set<SearchDictionaryEntry> entriesSet = new HashSet<>();
    
    this.maxWordLength = Dictionary.getMaxWordLength(mainDictionaryEntries);
    
    entriesSet.addAll(userSpecificInformation(mainDictionaryEntries, userWordsDictionary));
    entriesSet.addAll(userSpecificInformation(mainDictionaryEntries, userPhrasesDictionary));
    entriesSet.addAll(mainDictionaryEntries.stream()
                          .filter(e -> !e.equals(userWordsDictionary.getEntry(e)))
                          .filter(e -> !e.equals(userPhrasesDictionary.getEntry(e)))
                          .map(SearchDictionaryEntry::new)
                          .collect(Collectors.toSet()))
    ;
    
    this.entries = new ArrayList<>(entriesSet);//entries need to be an ArrayList because we will need to be able to get an element from a specific position within the list (with method .get(i))
    
    if (alphabet == null) {
      this.alphabet = new Alphabet(mainDictionary.getCharsSet(), this.isoLanguageName);
    } else if (this.isoLanguageName.equals(alphabet.getIsoLanguageName())) {
      this.alphabet = alphabet;
    } else {
      throw new Error("Error: Language of the alphabet specified doesn't match with searchDictionary language");
    }
    
  }
  
  private List<SearchDictionaryEntry> userSpecificInformation(Set<Entry> mainDictionary, Dictionary userDictionary) {
    return
        mainDictionary.stream()
            .filter(e -> e.equals(userDictionary.getEntry(e)))
            .map(e -> new SearchDictionaryEntry(
                e.getWord(),
                e.getFrequency(),
                userDictionary.getEntry(e).getFrequency(),
                userDictionary.getEntry(e).getLastUseTime()
            )).collect(Collectors.toList())
        ;
  }
  
  public List<SearchDictionaryEntry> getEntries() {
    return this.entries;
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
  
  public int getMaxWordLength() {
    return this.maxWordLength;
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
  
  //todo>> the stub code here
  public void save(boolean backup) throws IOException {
    this.save(this.dictionaryFileName);
  }
  
  //todo>> the stub code here
  public void save(String dictionaryFileName) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(dictionaryFileName));
  }
  
  //todo>> the stub code here
  static public void save(String dictionaryFileName, SearchDictionary searchDictionary) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(dictionaryFileName));
  }
  
}
