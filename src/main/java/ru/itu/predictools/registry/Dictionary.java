package ru.itu.predictools.registry;

import ru.itu.predictools.Alphabet.Alphabet;

import java.io.*;
import java.util.*;
import java.util.function.Function;

// Dictionary contains words and their lexical parameters and other data in structures named Entry
@SuppressWarnings({"UnusedReturnValue", "unused", "WeakerAccess"})
public class Dictionary {//extends Registry{
  private final String isoLanguageName;
  private final List<DictionaryEntry> entries;
  private Alphabet alphabet;
  private int maxWordLength;
  private Function<Dictionary, Alphabet> makeAlphabet = dictionary -> alphabet;
  private String dictionaryFileName;
  
  public Dictionary(String dictionaryFileName, String isoLanguageName) throws IOException {
    this(dictionaryFileName, null, isoLanguageName);
  }
  
  public Dictionary(String dictionaryFileName, Alphabet alphabet, String isoLanguageName) throws IOException {
    this.isoLanguageName = isoLanguageName;//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
    this.dictionaryFileName = dictionaryFileName;
    
    maxWordLength = 0;
    Set<DictionaryEntry> entriesSet = new HashSet<>();
    
    BufferedReader reader = new BufferedReader(new FileReader(dictionaryFileName));
    String line, word;
    while ((line = reader.readLine()) != null) {
      word = line.split(" ")[2];
      if (word.length() > maxWordLength) maxWordLength = word.length();
      DictionaryEntry entry = new DictionaryEntry(word, Long.parseLong(line.split(" ")[1]));
      entriesSet.add(entry);
    }
    
    this.entries = new ArrayList<>(new HashSet<>(entriesSet));
    reader.close();
/*    if (alphabet != null) {
      this.alphabet = alphabet;//todo>> need check if alphabet has the same language as the dictionary
    }
    else {//todo>> if an alphabet is null makeAlphabet from dictionary
      this.alphabet = makeAlphabet(this);
    }*/
  }
  
  public boolean save(boolean backup) {
    try{
      this.saveDictionary(this.dictionaryFileName);
      return true;
    }
    catch(IllegalArgumentException ignored){
    }
    return false;
  }
  
  public boolean save(String dictionaryFileName) {
    try{
      BufferedWriter writer = new BufferedWriter(new FileWriter(dictionaryFileName));
      return true;
    }
    catch(IllegalArgumentException|IOException e){
      return false;
    }
  
  }
  
  public String getIsoLanguageName() {
    return isoLanguageName;
  }
  
  public Alphabet getAlphabet() {
    return alphabet;
  }
  public Alphabet getAlphabet(Dictionary dictionary){
  
  }
  
  public boolean setAlphabet(Alphabet alphabet) {
    try {
      this.alphabet = alphabet;
      return true;
    } catch (Exception e) {
      return false;
    }
  }
  
  public List<DictionaryEntry> getEntries() {
    return this.entries;
  }
  
  public boolean addEntry(DictionaryEntry entry) {
    try {
      this.entries.add(entry);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public boolean deleteEntry(DictionaryEntry entry) {
    try {
      this.entries.remove(entry);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public boolean deleteEntry(String word) {
    try {
      this.entries.removeIf(item -> Objects.equals(item.getWord(), word));
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public boolean updateEntry(DictionaryEntry entry){
    try{
      this.deleteEntry(entry.getWord());
      this.addEntry(entry);
      return true;
    }
    catch(IllegalArgumentException e){
      return false;
    }
  }
  
  public int getMaxWordLength() {
    return maxWordLength;
  }
  
}
