package ru.itu.predictools.registry;

import java.util.Objects;

@SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
public class Entry implements Comparable<Entry> {
  String word;
  Double frequency;
  
  public Entry(String word) {
    this(word, 1D);
  }
  
  public Entry(String word, Double frequency) {
    this.word = word;
    this.frequency = frequency;
  }
  
  public int hashCode() {
    return Objects.hash(word);
  }
  
  public double getFrequency() {
    return this.frequency;
  }
  
  public void setFrequency(Double newFrequency) {
      this.frequency = newFrequency;
  }
  
  public String getWord() {
    return this.word;
  }
  
  public void setWord(String word) {
      this.word = word;
  }
  
  @Override
  public boolean equals(Object o) {
    try {
      if (o == this) {
        return true;
      }
      Entry entry = (Entry) o;
      return Objects.equals(entry.getWord(), this.word) && Objects.equals(entry.getFrequency(), this.frequency);
    } catch (ClassCastException e) {
      return false;
    }
  }
  
  @Override
  public int compareTo(Entry entry) { //@NotNull
    return equals(entry) ? 0 : frequency > entry.getFrequency() ? 1 : -1;
  }
}
