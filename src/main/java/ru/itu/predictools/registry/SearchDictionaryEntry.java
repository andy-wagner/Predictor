package ru.itu.predictools.registry;

import java.time.LocalDateTime;
import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class SearchDictionaryEntry extends Entry {
  private Integer distance; //distance from this entry word to search target //TODO absolute or relative distance what kind is preferable???
  private Double localFrequency; //frequency from user's dictionaries
  
  public SearchDictionaryEntry(String word) {
    this(word, 1D, 1D, LocalDateTime.now(), 0);
  }
  
  public SearchDictionaryEntry(Entry entry) {
    this(entry.getWord(), entry.getFrequency(), 1D, entry.getLastUseTime(), 0);
  }
  
  public SearchDictionaryEntry(String word, Double globalFrequency) {
    this(word, globalFrequency, 1D, LocalDateTime.now(), 0);
  }
  
  public SearchDictionaryEntry(String word, Double globalFrequency, Double localFrequency) {
    this(word, globalFrequency, localFrequency, LocalDateTime.now(), 0);
  }
  
  public SearchDictionaryEntry(Entry entry, Integer distance) {
    this(entry.getWord(), entry.getFrequency(), 1D, entry.getLastUseTime(), 0);
  }
  
  public SearchDictionaryEntry(Entry entry, Double localFrequency) {
    this(entry.getWord(), entry.getFrequency(), localFrequency, LocalDateTime.now(), 0);
  }
  
  public SearchDictionaryEntry(Entry entry, Double localFrequency, LocalDateTime lastUseTime) {
    this(entry.getWord(), entry.getFrequency(), localFrequency, lastUseTime, 0);
  }
  
  public SearchDictionaryEntry(Entry entry, Double localFrequency, Integer distance) {
    this(entry.getWord(), entry.getFrequency(), localFrequency, entry.getLastUseTime(), distance);
  }
  
  public SearchDictionaryEntry(Entry entry, Double localFrequency, LocalDateTime lastUseTime, Integer distance) {
    this(entry.getWord(), entry.getFrequency(), localFrequency, lastUseTime, distance);
  }
  
  public SearchDictionaryEntry(String word, Double globalFrequency, Double localFrequency, LocalDateTime lastUseTime) {
    this(word, globalFrequency, localFrequency, lastUseTime, 0);
  }
  
  public SearchDictionaryEntry(String word, Double globalFrequency, Double localFrequency, LocalDateTime lastUseTime, Integer distance) {
    super(word, globalFrequency, lastUseTime);
    this.localFrequency = localFrequency;
    this.distance = distance;
  }
  
  public Double getLocalFrequency() {
    return this.localFrequency;
  }
  
  public void setLocalFrequency(Double localFrequency) {
    this.localFrequency = localFrequency;
  }
  
  public Integer getDistance() {
    return this.distance;
  }
  
  public void setDistance(Integer distance) {
    this.distance = distance;
  }
  
  @Override
  public boolean equals(Object o) {
    try {
      if (o == this) {
        return true;
      }
      SearchDictionaryEntry entry = (SearchDictionaryEntry) o;
      
      return Objects.equals(entry.getWord(), this.word)
//                 && Objects.equals(entry.getFrequency(), this.frequency)
//                 && Objects.equals(entry.getLocalFrequency(), this.localFrequency)
//                 && Objects.equals(entry.getLastUseTime(), this.lastUseTime)
          ;
    } catch (ClassCastException e) {
      return false;
    }
  }
  
//  @Override
//  public int compareTo(SearchDictionaryEntry entry) { //@NotNull
//    return this.equals(entry) ? 0 : this.frequency > entry.getFrequency() ? 1 : -1;
//  }
  
}
