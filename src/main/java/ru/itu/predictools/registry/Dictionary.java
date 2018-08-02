package ru.itu.predictools.registry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
class Dictionary {
  private Set<Entry> entries;
  private Set<Character> charsSet;
  private String isoLanguageName;//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
  private Integer maxWordLength;
  
  public Dictionary() {
    //make empty searchDictionary to put into an arbitrary combination of entries and charsSet (for storing search result and
    // reduced charsSet for instance)
    this.entries = new HashSet<>();
    this.charsSet = new HashSet<>();
    this.maxWordLength = 0;
    this.isoLanguageName = "";
  }
  
  public Dictionary(Set<Entry> entries, String isoLanguageName){
    this.entries = entries;
    this.charsSet = new HashSet<>();
    this.isoLanguageName = isoLanguageName;
    for(Entry entry: entries){
      char[] chars = entry.getWord().toCharArray();
      for(char ch: chars){
        this.charsSet.add(ch);
      }
    }
    this.maxWordLength = entries
                             .stream()
                             .mapToInt(entry -> entry.getWord().length())
                             .max().orElseThrow(NoSuchElementException::new)
    ;
  }
  
  public Dictionary(Set<Entry> entries, Set<Character> charsSet, String isoLanguageName) {
    //make searchDictionary with an arbitrary combination of entries and charsSet (for storing search result and
    // reduced charsSet for instance)
    this.entries = entries;
    this.charsSet = charsSet;
    this.isoLanguageName = isoLanguageName;
    this.maxWordLength = entries
                             .stream()
                             .mapToInt(entry -> entry.getWord().length())
                             .max().orElseThrow(NoSuchElementException::new)
    ;
  }
  
  public Dictionary(String dictionaryFileName) throws IOException {
    //make searchDictionary from searchDictionary file with entries as a set of distinct words with frequencies
    // as they specified in the file
    this();
    
    BufferedReader reader = new BufferedReader(new FileReader(dictionaryFileName));
    
    String line, word, command;
    Double frequency;
    String[] lineFields;
    
    //read header
    do {
      line = reader.readLine();
      lineFields = line.split("=");//todo>> customize delimiter or add several types of delimiters [\s|\,|\;|\t]
      command = lineFields[0].trim();
      switch (command.toLowerCase()) {
        case "lang":
          this.isoLanguageName = lineFields[1].trim();
          break;
      }
      if(lineFields[0].matches("\\d+\\.?\\d*")) {
        reader.mark(0);
        break;
      }
    } while (!lineFields[0].equals("start"));
    
    //read content
    try {
      reader.reset();
    }
    catch (IOException e){//do nothing if not marked
    
    }
    while ((line = reader.readLine()) != null) {
      lineFields = line.split(",");//todo>> customize delimiter or add several types of delimiters [\s|\,|\;|\t]
      
      word = lineFields[2];
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
    
    reader.close();
    
  }
  
  public Set<Character> getCharsSet() {
    return this.charsSet;
  }
  
  public void setCharsSet(Set<Character> charsSet) {
      this.charsSet = charsSet;
  }
  
  public Set<Entry> getEntries() {
    return this.entries;
  }
  
  public void setEntries(Set<Entry> entries) {
      this.entries = entries;
  }
  
  public String getIsoLanguageName(){
    return this.isoLanguageName;
  }
  
  public int getMaxWordLength() {
    return this.maxWordLength;
  }
  
  public void addEntry(Entry entry) {
      this.entries.add(entry);
  }
  
  public void addEntry(String word, Double frequency) {
      this.entries.add(new Entry(word, frequency));
  }
  
  public void deleteEntry(Entry entry) {
      this.entries.remove(entry);
  }
  
  public void deleteEntry(String word) {
      this.entries.removeIf(item -> Objects.equals(item.getWord(), word));
  }
  
  public void updateEntry(Entry entry) {
      this.deleteEntry(entry.getWord());//delete by word field because it doesn't matter which frequency entry has had
      this.addEntry(entry);
  }
  
  public void updateEntry(String word, Double frequency) {
      this.deleteEntry(word);//delete by word field because it doesn't matter which frequency entry has had
      this.addEntry(new Entry(word, frequency));
  }
  
}
