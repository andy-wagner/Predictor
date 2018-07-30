package ru.itu.predictools.registry;

import java.util.Objects;

@SuppressWarnings("unused")
public abstract class Entry {
  String word;
  Long frequency;
  
  Entry() {
    this("", 0L);
  }
  
  Entry(String word) {
    this(word, 0L);
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
  
  public void setFrequency(Long newFrequency) {
    this.frequency = newFrequency;
  }
  
}
