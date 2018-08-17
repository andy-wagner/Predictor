package ru.itu.predictools.registry;

import java.time.LocalDateTime;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
public class Entry implements Comparable<Entry> {
  private static final Logger LOGGER = LogManager.getLogger();
  String word;
  Double frequency;
  LocalDateTime lastUseTime;
  
  public Entry(String word) {
    this(word, 1D, null);
  }
  
  public Entry(String word, Double frequency) {
    this(word, frequency, null);
  }
  
  public Entry(String word, Double frequency, LocalDateTime lastUseTime){
    this.word = word;
    this.frequency = frequency;
    this.lastUseTime = lastUseTime;
  }
  
  public int hashCode() {
    return Objects.hash(this.word);
  }
  
  public Double getFrequency() {
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
  
  public LocalDateTime getLastUseTime(){
    return this.lastUseTime;
  }
  
  public void setLastUseTime(LocalDateTime lastUseTime) {
    this.lastUseTime = lastUseTime;
  }
  
  public boolean equals(Object o) {
    try {
      if (o == this) {
        return true;
      }
      Entry entry = (Entry) o;
      return Objects.equals(entry.getWord(), this.word)
      ;
    } catch (ClassCastException e) {
      return false;
    }
  }
  
  @Override
  public int compareTo(Entry entry) { //@NotNull
    return equals(entry) ? 0 : this.frequency > entry.getFrequency() ? 1 : -1;
  }
}
