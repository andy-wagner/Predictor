package ru.itu.predictools.registry;

import com.sun.corba.se.impl.io.TypeMismatchException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class Dictionary {
  private Set<Entry> entries;
  //todo>> need to consider the using of Map<String, Entry> instead of Set<Entry>, where field [String word] is a key, and the field [Entry entry] is an instance of data class of word characteristics such as frequency, distance, localFrequency, lastUseTime etc.
  private Set<Character> charsSet;
  private String isoLanguageName;//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes//todo>> create class DictionaryHeader and move language information into header object along with user id and other information specific to the dictionary as a whole
  private Integer maxWordLength;
  
  public Dictionary() {
    //make empty dictionary to put into an arbitrary combination of entries and charsSet (for storing search result and
    // reduced charsSet for instance)
    this.entries = new HashSet<>();
    this.charsSet = new HashSet<>();
    this.maxWordLength = 0;
    this.isoLanguageName = "";
  }
  
  public Dictionary(Set<Entry> entries, String isoLanguageName) {
    this(entries, new HashSet<>(), isoLanguageName);
    this.charsSet = Dictionary.getCharsSet(entries);
  }
  
  public Dictionary(Set<Entry> entries, Set<Character> charsSet, String isoLanguageName) {
    //make dictionary with an arbitrary combination of entries and charsSet (for storing search result and
    // reduced charsSet for instance)
    this.entries = entries;
    this.charsSet = charsSet;
    this.isoLanguageName = isoLanguageName;
    this.maxWordLength = Dictionary.getMaxWordLength(entries);
  }
  
  public Dictionary(String dictionaryFileName) throws IOException {
    //make dictionary from dictionary file with entries as a set of distinct words with frequencies
    // as they specified in the file
    this();
    
    BufferedReader reader = new BufferedReader(new FileReader(dictionaryFileName));
    
    String line, word, command;
    Double frequency;
    String[] lineFields;
    
    //read header
    do {
      if ((line = reader.readLine()) == null) {
        throw new RuntimeException("Error: Wrong dictionary file format.");
      }
      lineFields = line.split("=");
      command = lineFields[0].trim();
      switch (command.toLowerCase()) {
        case "lang":
          this.isoLanguageName = lineFields[1].trim();
          break;
      }
      if (lineFields[0].matches("\\d+\\.?\\d*")) {
        reader.mark(0);
        break;
      }
    } while (!lineFields[0].equals("start"));
    
    //read content
    try {
      reader.reset();
    } catch (IOException e) {//do nothing if not marked
    
    }
    try {
      while ((line = reader.readLine()) != null) {
        lineFields = line.split(",");
        
        word = lineFields[2].trim();
        frequency = Double.parseDouble(lineFields[1]);
        
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
    } catch (TypeMismatchException e) {
      reader.close();
      throw new TypeMismatchException("Error: Wrong dictionary file format.");
    }
    reader.close();
    
  }
  
  public static Set<Character> getCharsSet(Set<Entry> entries) {
    Set<Character> result = new HashSet<>();
    for (Entry entry : entries) {
      char[] chars = entry.getWord().toCharArray();
      for (char ch : chars) {
        result.add(ch);
      }
    }
    return result;
  }
  
  public Set<Character> getCharsSet() {
    return Dictionary.getCharsSet(this.getEntries());
  }
  
  /**
   * sets object's set of getChars from current object entries
   */
  public void setCharsSet() {
    this.charsSet = Dictionary.getCharsSet(this.entries);
  }
  
  /**
   * sets object's set of getChars from parameter passed
   *
   * @param charsSet - set of getChars to be set as an object's set of getChars
   */
  public void setCharsSet(Set<Character> charsSet) {
    this.charsSet = charsSet;
  }
  
  public Set<Entry> getEntries() {
    return this.entries;
  }
  
  public void setEntries(Set<Entry> entries) {
    this.entries = entries;
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
  static public int getMaxWordLength(Set<Entry> entries) {
    return entries
               .stream()
               .mapToInt(entry -> entry.getWord().length())
               .max().orElseThrow(NoSuchElementException::new)
        ;
  }
  
  public int getMaxWordLength() {
    return this.maxWordLength;
//    return Dictionary.getMaxWordLength(this.getEntries());
  }
  
  public boolean addEntry(String word) {
    return this.addEntry(word, 1D, LocalDateTime.now());
  }
  
  public boolean addEntry(String word, Double frequency, LocalDateTime lastUseTime) {
    return this.addEntry(new Entry(word, frequency, lastUseTime));
  }
  
  public boolean addEntry(String word, Double frequency) {
    return this.addEntry(new Entry(word, frequency, null));
  }
  
  public boolean addEntry(Entry entry) {
    try {
      Integer newWordLength = entry.getWord().length();
      if (this.maxWordLength < newWordLength) {
        this.maxWordLength = newWordLength;
      }
      return this.entries.add(entry);
    } catch (Exception e) {
      return false;
    }
  }
  
  public boolean addAllEntries(Set<Entry> entries) {
    try {
      Integer newWordsMaxLength = Dictionary.getMaxWordLength(entries);
      if (this.maxWordLength < newWordsMaxLength) {
        this.maxWordLength = newWordsMaxLength;
      }
      return this.entries.addAll(entries);
    } catch (Exception e) {
      return false;
    }
  }
  
  public boolean updateEntry(String word, Double frequency) {
    return this.updateEntry(new Entry(word, frequency));
  }
  
  public boolean updateEntry(Entry entry) {
    try {
      if (!this.addEntry(entry)) {
        this.removeEntry(entry);
        this.addEntry(entry);
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }
  
  public boolean updateAllEntries(Set<Entry> entries) {
    try {
      if (!this.addAllEntries(entries)) {
        this.removeAllEntries(entries);
        this.addAllEntries(entries);
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }
  
  public Entry getEntry(String word) {
    Optional<Entry> selected = entries.stream().filter(e -> e.getWord().equals(word)).findAny();
    return selected.orElse(null);
  }
  
  public Entry getEntry(Entry entry) {
    Optional<Entry> selected = entries.stream().filter(e -> e.getWord().equals(entry.getWord())).findAny();
    return selected.orElse(null);
  }
  
  public boolean removeEntry(Entry entry) {
    try {
      if (this.maxWordLength == entry.getWord().length()) {
        boolean result = this.entries.remove(entry);
        this.maxWordLength = Dictionary.getMaxWordLength(this.entries);
        return result;
      }
      return this.entries.remove(entry);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
  
  public boolean removeEntry(String word) {
    return this.entries.remove(new Entry(word));
  }
  
  public boolean removeAllEntries(Set<Entry> entries) {
    return this.entries.removeAll(entries);
  }
  
  public Dictionary mergeDictionary(Dictionary dictionary, boolean update) throws RuntimeException {
    if (this.isoLanguageName.equals(dictionary.getIsoLanguageName())) {
      if (update) {
        this.addAllEntries(dictionary.getEntries());
        this.updateAllEntries(dictionary.getEntries());
      } else {
        this.addAllEntries(dictionary.getEntries());
      }
    } else {
      throw new RuntimeException("Error: the languages of merged dictionaries should be the same");
    }
    return this;
  }
  
}
