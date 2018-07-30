package ru.itu.predictools.registry;

import ru.itu.predictools.Alphabet.Alphabet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Dictionary has words and its lexical parameters and data in structures named Entry
 */
public class Dictionary {//extends Registry{
  private final String language;
  private List<DictionaryEntry> entries;
  private Alphabet alphabet;
  private int maxWordLength;
  private Function<Dictionary, Alphabet> makeAlphabet = dictionary -> alphabet;
  
  public Dictionary(String dictionaryFileName, String language) throws IOException {
    this(dictionaryFileName, null, language);
  }
  
  public Dictionary(String dictionaryFileName, Alphabet alphabet, String language) throws IOException {
    this.language = language;//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
    if(alphabet != null){
      this.alphabet = alphabet;
    }
    maxWordLength = 0;
//        this.entries = new ArrayList<>();
    Set<DictionaryEntry> entriesSet = new HashSet<>();
    
    BufferedReader reader = new BufferedReader(new FileReader(dictionaryFileName));
    String line, word;
    while ((line = reader.readLine()) != null) {
      word = line.split(" ")[2];
      if (word.length() > maxWordLength) maxWordLength = word.length();
      DictionaryEntry entry = new DictionaryEntry();
      entry.setWord(word);
      entry.setFrequency(Long.parseLong(line.split(" ")[1]));
      entriesSet.add(entry);
//            entries.add(entry);
    }
    
    this.entries = new ArrayList<>(new HashSet<>(entriesSet));
    reader.close();
  }
  
  public String getLanguage() {
    return language;
  }
  
  public List<DictionaryEntry> getEntries() {
    return this.entries;
  }
  
  public Alphabet getAlphabet() {
    return alphabet;
  }
  
  public int maxWordLength() {
    return maxWordLength;
  }
  
  public Boolean setAlphabet(Alphabet alphabet) {
    try {
      this.alphabet = alphabet;
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
