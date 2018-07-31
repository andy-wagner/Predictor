package ru.itu.predictools.registry;

import java.util.Objects;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Entry implements Comparable<Entry>{
  String word;
  Long frequency;
  
  Entry(String word) {
    this(word, 1L);
  }
  
  Entry(String word, Long frequency) {
    this.word = word;
    this.frequency = frequency;
  }
  
  public int hashCode() {
    return Objects.hash(word);
  }
  
  public Long getFrequency() {
    return this.frequency;
  }
  
  public boolean setFrequency(Long newFrequency) {
    try{
      this.frequency = newFrequency;
      return true;
    }
    catch(IllegalArgumentException e){
      return false;
    }
  }
  
  public String getWord() {
    return this.word;
  }
  
  public boolean setWord(String word) {
    try{
      this.word = word;
      return true;
    }
    catch(IllegalArgumentException e){
      return false;
    }
  }
  
  @Override
  public boolean equals(Object o) {
    return o == this;
  }
  
  @Override
  public int compareTo(Entry entry) { //@NotNull
    return equals(entry) ? 0 : frequency > entry.getFrequency() ? 1 : -1;
  }
}
