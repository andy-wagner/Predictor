package ru.itu.predictools.registry;

import ru.itu.predictools.alphabet.Alphabet;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

// SearchDictionary contains words and their lexical parameters and other data in structures named Entry
@SuppressWarnings({"UnusedReturnValue", "unused", "WeakerAccess"})
public class SearchDictionary {//extends dictionary{
  //entries need to be an ArrayList because we will need to be able to get an element from a specific position within the list (with method .get(i))
  private List<SearchDictionaryEntry> entries;
  //todo>> it will be needed to save user dictionaries// private final String userWordsDictionaryFileName;
  //todo>> it will be needed to save user dictionaries//  private final String userPhrasesDictionaryFileName;
  private Alphabet alphabet;
  private String isoLanguageName;
  //  private int maxWordLength;
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
    mainDictionary//todo>> need to consider optimisation current decision is too slow
        .mergeDictionary(userWordsDictionary, false)//todo>> good only as first iteration need to consider optimisation current decision is too slow
        .mergeDictionary(userPhrasesDictionary, false)//todo>> need to consider optimisation current decision is too slow
    ;
//    this.maxWordLength = mainDictionary.getMaxWordLength();
    
    Set<SearchDictionaryEntry> entriesSet = new HashSet<>();
    entriesSet.addAll(userSpecificInformation(mainDictionary.getEntries(), userWordsDictionary));//todo>> need to consider optimisation current decision is too slow
    entriesSet.addAll(userSpecificInformation(mainDictionary.getEntries(), userPhrasesDictionary));//todo>> need to consider optimisation current decision is too slow
    entriesSet.addAll(//todo>> need to consider optimisation current decision is too slow
        mainDictionary.getEntries().stream()
            .filter(e -> userWordsDictionary.getEntry(e) == null)
            .filter(e -> userPhrasesDictionary.getEntry(e) == null)
            .map(SearchDictionaryEntry::new).collect(Collectors.toSet())
    );

    this.entries = new ArrayList<>(entriesSet);
    
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
            .filter(e -> userDictionary.getEntry(e) != null)
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

//  public boolean updateEntry(SearchDictionaryEntry entry, SearchDictionaryEntry newEntry){
//    this.entries.set(this.entries.indexOf(entry), newEntry);
//  }
  
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
