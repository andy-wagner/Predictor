package ru.itu.predictools.registry;

import ru.itu.predictools.alphabet.Alphabet;

import java.io.*;
import java.util.*;

// Dictionary contains words and their lexical parameters and other data in structures named Entry
@SuppressWarnings({"UnusedReturnValue", "unused", "WeakerAccess"})
public class Dictionary {//extends registry{
  
  private final List<Entry> entries;//entries in the List because we will need to get an element from a specified position within the list
  private Alphabet alphabet;
  private final String isoLanguageName;
  private int maxWordLength;
  private String dictionaryFileName;
  
  private void loadDictionaryFromFile(){
  
  }
  
  public Dictionary(String dictionaryFileName, String isoLanguageName) throws IOException {
    this(dictionaryFileName, null, isoLanguageName);
  }
  
  public Dictionary(String dictionaryFileName, Alphabet alphabet, String isoLanguageName) throws IOException {
    this.isoLanguageName = isoLanguageName;//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
    this.dictionaryFileName = dictionaryFileName;
  
    boolean anAutoAlphabetIsNeeded = false;
    if (alphabet == null) {
      anAutoAlphabetIsNeeded = true;
    }
    else if(this.isoLanguageName.equals(alphabet.getIsoLanguageName())){
      this.alphabet = alphabet;
    }
    
    this.maxWordLength = 0;
    
    Set<Entry> entriesSet = new HashSet<>();//entries in the Set because we need to get list of unique words
    Set<Character> dictionaryCharsSet = new HashSet<>();//characters in the Set because we need to get list of unique symbols
    BufferedReader reader = new BufferedReader(new FileReader(dictionaryFileName));
    String line, word;
    while ((line = reader.readLine()) != null) {
      String[] lineFields = line.split(" ");//todo>> customize delimiter or add several types of delimiters [\s|\,|\;|\t]
      word = lineFields[2];
      if (word.length() > this.maxWordLength) {
        this.maxWordLength = word.length();
      }
      Entry entry = new Entry(word, Long.parseLong(lineFields[1]));
      char[] wordChars = word.toCharArray();
      entriesSet.add(entry);
      if(anAutoAlphabetIsNeeded){
        for(char i = 0; i<word.length(); i++){
          dictionaryCharsSet.add(wordChars[i]);
        }
      }
      
    }
  
    reader.close();
    
    this.entries = new ArrayList<>(new HashSet<>(entriesSet));//entries in the ArrayList because we will need to get an element from a specified position within the list (.get(i))
    if(anAutoAlphabetIsNeeded){
      this.alphabet = new Alphabet(dictionaryCharsSet, isoLanguageName);
    }
    
  }
  
  //todo>> the stub code
  public boolean save(boolean backup) {
    try{
      this.save(this.dictionaryFileName);
      return true;
    }
    catch(IllegalArgumentException ignored){
    }
    return false;
  }
  
  //todo>> the stub code
  public boolean save(String dictionaryFileName) {
    try{
      BufferedWriter writer = new BufferedWriter(new FileWriter(dictionaryFileName));
      return true;
    }
    catch(IllegalArgumentException|IOException e){
      return false;
    }
    
  }
  
  //todo>> the stub code
  static public boolean save(String dictionaryFileName, Dictionary dictionary) {
    try{
      BufferedWriter writer = new BufferedWriter(new FileWriter(dictionaryFileName));
      return true;
    }
    catch(IllegalArgumentException|IOException e){
      return false;
    }
  
  }
  
  public String getIsoLanguageName() {
    return this.isoLanguageName;
  }
  
  //todo>> the stub code
  public Alphabet getAlphabet() {
//    return new Alphabet(super.getCharsSet(), isoLanguageName);
    return new Alphabet("asdf","ru");
  }
  
  //todo>> the stub code
  static public Alphabet getAlphabet(String dictionaryFileName){
    return new Alphabet("abcde","ru");
  }
  
  public boolean setAlphabet(Alphabet alphabet) {
    try {
      this.alphabet = alphabet;
      return true;
    } catch (Exception e) {
      return false;
    }
  }
  
}
