package ru.itu.predictools.registry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
class Registry {
  private Set<Entry> entries;
  private Set<Character> charsSet;
  private Integer maxWordLength;
  
  public Registry() {
    //make empty dictionary to put into an arbitrary combination of entries and charsSet (for storing search result and
    // reduced charsSet for instance)
    this.entries = new HashSet<>();
    this.charsSet = new HashSet<>();
    this.maxWordLength = 0;
  }
  
  public Registry(Set<Entry> entries, Set<Character> charsSet) {
    //make dictionary with an arbitrary combination of entries and charsSet (for storing search result and
    // reduced charsSet for instance)
    this.entries = entries;
    this.charsSet = charsSet;
    this.maxWordLength = entries
                             .stream()
                             .mapToInt(entry -> entry.getWord().length())
                             .max().orElseThrow(NoSuchElementException::new)
    ;
  }
  
  public Registry(String dictionaryFileName) throws IOException {
    //make dictionary from dictionary file with entries as a set of distinct words with frequencies
    // as they specified in the file
    this();
    
    BufferedReader reader = new BufferedReader(new FileReader(dictionaryFileName));
    
    String line, word;
    Long frequency;
    
    while ((line = reader.readLine()) != null) {
      String[] lineFields = line.split(" ");//todo>> customize delimiter or add several types of delimiters [\s|\,|\;|\t]
      
      word = lineFields[2];
      frequency = Long.parseLong(lineFields[1]);
      
      if (word.length() > this.maxWordLength) {
        this.maxWordLength = word.length();
      }
      
      Entry entry = new Entry(word, frequency);
      char[] wordChars = word.toCharArray();
      entries.add(entry);
      for (char i = 0; i < word.length(); i++) {
        charsSet.add(wordChars[i]);
      }
      
    }
    
    reader.close();
    
  }
  
  public Set<Character> getCharsSet() {
    return this.charsSet;
  }
  
  public Boolean setCharsSet(Set<Character> charsSet) {
    try {
      this.charsSet = charsSet;
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public Set<Entry> getEntries() {
    return this.entries;
  }
  
  public Boolean setEntries(Set<Entry> entries) {
    try {
      this.entries = entries;
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public int geMaxWordLength() {
    return this.maxWordLength;
  }
  
  public boolean addEntry(Entry entry) {
    try {
      this.entries.add(entry);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public boolean addEntry(String word, Long frequency){
    try {
      this.entries.add(new Entry(word, frequency));
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public boolean deleteEntry(Entry entry) {
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
  
  public boolean updateEntry(Entry entry){
    try{
      this.deleteEntry(entry.getWord());//delete by word field because it doesn't matter which frequency entry has had
      this.addEntry(entry);
      return true;
    }
    catch(IllegalArgumentException e){
      return false;
    }
  }
  
  public boolean updateEntry(String word, Long frequency){
    try{
      this.deleteEntry(word);//delete by word field because it doesn't matter which frequency entry has had
      this.addEntry(new Entry(word, frequency));
      return true;
    }
    catch(IllegalArgumentException e){
      return false;
    }
  }
  
}
