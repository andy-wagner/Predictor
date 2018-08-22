package ru.itu.predictor.registry;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class Dictionary {
  private static final Logger LOGGER = LogManager.getLogger();
  private Set<Entry> entries;
  private Set<Character> charSet;
  private String isoLanguageName;//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
  private Integer maxWordLength;
  private String userId;
  
  public Dictionary() {
    //make empty dictionary to put into an arbitrary combination of entries and charSet (for storing search result and
    // reduced charSet for instance)
    this.entries = new HashSet<>();
    this.charSet = new HashSet<>();
    this.maxWordLength = 0;
    this.isoLanguageName = "";
    this.userId = "";
    LOGGER.debug("Empty dictionary has created");
  }
  
  public Dictionary(Set<Entry> entries, String isoLanguageName) {
    this(entries, new HashSet<>(), isoLanguageName, "");
    this.charSet = Dictionary.getCharSet(entries);
  }
  
  public Dictionary(Set<Entry> entries, String isoLanguageName, String userId) {
    this(entries, new HashSet<>(), isoLanguageName, userId);
    this.charSet = Dictionary.getCharSet(entries);
  }
  
  public Dictionary(Set<Entry> entries, Set<Character> charSet, String isoLanguageName, String userId) {
    //make dictionary with an arbitrary combination of entries and charSet (for storing search result and
    // reduced charSet for instance)
    this.entries = entries;
    this.charSet = charSet;
    this.isoLanguageName = isoLanguageName;
    this.userId = userId;
    this.maxWordLength = Dictionary.getMaxWordLength(entries);
    LOGGER.debug("Dictionary has created from an entries set.");
  }
  
  public Dictionary(String dictionaryFileName) throws IOException {
    this(dictionaryFileName, false);
  }
  
  public Dictionary(String dictionaryFileName, boolean keepLettersCase) throws IOException {
    //make dictionary from dictionary file with entries as a set of distinct words with frequencies
    // as they specified in the file
    this();//Create empty dictionary first
    LOGGER.debug("The empty dictionary has created to be filled with data from the dictionary file '{}'", dictionaryFileName);
    
    BufferedReader reader = new BufferedReader(new FileReader(dictionaryFileName));
    
    String line, word, command;
    Double frequency;
    String[] lineFields;
    
    //read header
    do {
      if ((line = reader.readLine()) == null) {
        LOGGER.error("Error: Wrong dictionary file format.");
        throw new RuntimeException("Error: Wrong dictionary file format.");
      }
      lineFields = line.split("=");
      command = lineFields[0].trim();
      switch (command.toLowerCase()) {
        case "language":
          this.isoLanguageName = lineFields[1].trim();
          break;
        case "user":
          this.userId = lineFields[1].trim();
          break;
      }
      if (lineFields[0].matches("\\d+\\.?\\d*")) {  //if the numeric value in the first field then it should be a content part of the dictionary file
        reader.mark(0);                      //so mark this position to reposition to it on next steps
        break;                                             //and break current cycle
      }
    } while (!lineFields[0].equals("start"));
    
    //read content
    try {
      reader.reset();                                      //reposition the reader if it was marked earlier
    } catch (IOException ignore) {                         //do nothing if not marked
    }
    try {
      while ((line = reader.readLine()) != null) {
        lineFields = line.split(",");
        
        word = keepLettersCase ? lineFields[2].trim() : lineFields[2].trim().toLowerCase();
        frequency = Double.parseDouble(lineFields[1]);
        
        if (word.length() > this.maxWordLength) {
          this.maxWordLength = word.length();
        }
        
        Entry entry = new Entry(word, frequency);
        char[] wordChars = word.toCharArray();
        this.entries.add(entry);
        for (char i = 0; i < word.length(); i++) {
          charSet.add(wordChars[i]);
        }
        
      }
    } catch (Exception e) {
      reader.close();
      LOGGER.error(e.getMessage());
      throw new RuntimeException(e);
    }
    reader.close();
    LOGGER.debug("Dictionary has created from a file '{}'", dictionaryFileName);
  }
  
  public static Set<Character> getCharSet(char[] chars){
    Set<Character> result = new HashSet<>();
    for (char ch : chars) {
      result.add(ch);
    }
    return result;
  }
  
  public static Set<Character> getCharSet(String string){
    Set<Character> result = new HashSet<>();
    char[] chars = string.toCharArray();
    for (char ch : chars) {
      result.add(ch);
    }
    return result;
  }
  
  public static Set<Character> getCharSet(Set<Entry> entries) {
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
    return Dictionary.getCharSet(this.getEntries());
  }
  
  /**
   * sets object's set of getChars from current object entries
   */
  public void setCharSet() {
    this.charSet = Dictionary.getCharSet(this.entries);
  }
  
  /**
   * sets object's set of getChars from parameter passed
   *
   * @param charSet - set of getChars to be set as an object's set of getChars
   */
  public void setCharSet(Set<Character> charSet) {
    this.charSet = charSet;
  }
  
  public String getIsoLanguageName() {
    return this.isoLanguageName;
  }
  
  public boolean setIsoLanguageName(String isoLanguageName) {
    try {
      if (this.isoLanguageName.equals("")) {
        this.isoLanguageName = isoLanguageName;
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }
  
  public String getUserId() {
    return this.userId;
  }
  
  public boolean setUserID(String userId) {
    try {
      if (this.userId.equals("")) {
        this.userId = userId;
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }
  
  /**
   * get length of the largest word in entries set
   *
   * @param entries - entries set to search in
   * @return - int value of length of the largest word in the entries set
   */
  static public int getMaxWordLength(Set<Entry> entries) {
    return entries.stream().mapToInt(entry -> entry.getWord().length()).max().orElse(0);
  }
  
  public int getMaxWordLength() {
    return this.getMaxWordLength(false);
  }
  
  public int getMaxWordLength(boolean recalculate) {
    if (recalculate) {
      return Dictionary.getMaxWordLength(this.getEntries());
    } else {
      LOGGER.warn("maxWordLength returned without recalculation be sure that its value is actual");
    }
    return this.maxWordLength;
  }
  
  public Entry getEntry(String word) {
    return entries.stream().filter(e -> e.getWord().equals(word)).findAny().orElse(null);
  }
  
  public Entry getEntry(Entry entry) {
    return this.getEntry(entry.getWord());
  }
  
  public Set<Entry> getEntries() {
    return this.entries;
  }
  
  public void setEntries(Set<Entry> entries) {
    this.entries = entries;
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
      LOGGER.error(e.getMessage());
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
      LOGGER.error(e.getMessage());
      return false;
    }
  }
  
  public boolean removeEntry(String word) {
    return this.removeEntry(new Entry(word));
  }
  
  public boolean removeEntry(Entry entry) {
    try {
      boolean result = this.entries.remove(entry);
      if (this.maxWordLength == entry.getWord().length()) {
        this.maxWordLength = Dictionary.getMaxWordLength(this.entries);
      }
      return result;
    } catch (IllegalArgumentException e) {
      LOGGER.error(e.getMessage());
      return false;
    }
  }
  
  public boolean removeAllEntries(Set<Entry> entries) {
    Integer removedWordsMaxLength = Dictionary.getMaxWordLength(entries);
    boolean isAnythingRemoved = this.entries.removeAll(entries);
    if (isAnythingRemoved && this.maxWordLength.equals(removedWordsMaxLength)) {
      this.maxWordLength = Dictionary.getMaxWordLength(this.entries);
    }
    return isAnythingRemoved;
  }
  
  public boolean updateEntry(String word, Double frequency) {
    Entry entry = this.getEntry(word);
    if (entry != null) {
      entry.setFrequency(frequency);
      return true;
    }
    return false;
  }
  
  public boolean updateEntry(String word, Double frequency, LocalDateTime lastUseTime) {
    Entry entry = this.getEntry(word);
    if (entry != null) {
      entry.setFrequency(frequency);
      entry.setLastUseTime(lastUseTime);
      return true;
    }
    return false;
  }
  
  public boolean updateEntry(Entry entry) {
    return this.updateEntry(entry.getWord(), entry.getFrequency(), entry.getLastUseTime());
  }
  
  public boolean updateAllEntries(Set<Entry> entries) {
    try {
      if (!this.addAllEntries(entries)) {
        this.removeAllEntries(entries);
        this.addAllEntries(entries);
      }
      return true;
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      return false;
    }
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
      LOGGER.error("Error: the languages of merged dictionaries should be the same");
      throw new RuntimeException("Error: the languages of merged dictionaries should be the same");
    }
    return this;
  }
  
  public boolean save(String fileName) throws IOException {
    String line;
    int n = 0;
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
      line = "language = " + this.isoLanguageName + "\r\n";
      writer.append(line);
      line = "user = " + this.userId + "\r\n";
      writer.append(line);
      line = "start\r\n";
      writer.append(line);
      for (Entry e : this.entries) {
        line = Integer.toString(++n) + ", " + Double.toString(e.getFrequency()) + ", " + e.getWord() + "\r\n";
        writer.append(line);
      }
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      throw new IOException(e.getMessage());
    }
    return true;
  }
  
}
