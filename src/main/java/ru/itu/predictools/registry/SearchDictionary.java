package ru.itu.predictools.registry;

import ru.itu.predictools.alphabet.Alphabet;

import java.io.*;
import java.util.*;

// SearchDictionary contains words and their lexical parameters and other data in structures named Entry
@SuppressWarnings({"UnusedReturnValue", "unused", "WeakerAccess"})
public class SearchDictionary {//extends dictionary{
  
  private final List<Entry> entries;//entries in the List because we will need to get an element from a specified position within the list
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
    this.dictionaryFileName = mainDictionaryFileName;
    //entries need to be converted to the ArrayList because we will need to get an element from a specified position within the list (.get(i))
    this.entries = new ArrayList<>(new HashSet<>(mainDictionary.getEntries()));
    this.maxWordLength = mainDictionary.getMaxWordLength();
    
    if (alphabet == null) {
      this.alphabet = new Alphabet(mainDictionary.getCharsSet(), this.isoLanguageName);
    } else if (this.isoLanguageName.equals(alphabet.getIsoLanguageName())) {
      this.alphabet = alphabet;
    } else {
      throw new Error("Error: Language of the alphabet specified doesn't match with searchDictionary language");
    }
    
  }
  
  public List<Entry> getEntries(){
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
    if(alphabet.getIsoLanguageName().equals(this.isoLanguageName)){
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
  
  //todo>> the stub code
  public void save(boolean backup) throws IOException {
    this.save(this.dictionaryFileName);
  }
  
  //todo>> the stub code
  public void save(String dictionaryFileName) throws IOException {
      BufferedWriter writer = new BufferedWriter(new FileWriter(dictionaryFileName));
  }
  
  //todo>> the stub code
  static public void save(String dictionaryFileName, SearchDictionary searchDictionary) throws IOException {
      BufferedWriter writer = new BufferedWriter(new FileWriter(dictionaryFileName));
  }
  
}
