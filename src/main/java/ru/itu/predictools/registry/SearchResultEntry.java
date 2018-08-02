package ru.itu.predictools.registry;

import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class SearchResultEntry extends Entry {
  private Integer distance; //distance from this entry word to search target //TODO absolute or relative distance what kind is preferable???
  
  public SearchResultEntry(String word) {
    this(word, 1D);
  }
  
  public SearchResultEntry(Entry entry) {
    this(entry.getWord(), entry.getFrequency());
  }
  
  public SearchResultEntry(String word, Double frequency) {
    this(word, frequency, 0);
  }
  
  public SearchResultEntry(Entry entry, Integer distance) {
    this(entry.getWord(), entry.getFrequency(), distance);
  }
  
  public SearchResultEntry(String word, Double frequency, Integer distance) {
    super(word, frequency);
    this.distance = distance;
  }
  
  public Integer getDistance() {
    return this.distance;
  }
  
  public void setDistance(Integer distance) {
      this.distance = distance;
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SearchResultEntry)) {
      return false;
    }
    SearchResultEntry entry = (SearchResultEntry) o;
/*
    boolean result = Objects.equals(entry.getWord(), this.word)
                         && Objects.equals(entry.getDistance(), this.distance)
                         && Objects.equals(entry.getFrequency(), this.frequency);
    
    if (entry.getWord().equals(this.word)) {
      entry.setDistance(this.distance = Math.min(entry.getDistance(), this.distance));
    }
    
    return result;
*/
    return Objects.equals(entry.getWord(), this.word)
               && Objects.equals(entry.getDistance(), this.distance)
               && Objects.equals(entry.getFrequency(), this.frequency);
  }
  
  public int compareTo(SearchResultEntry entry) { //@NotNull
    return this.equals(entry) ?
               super.equals(entry) ? 0 : frequency > entry.getFrequency() ? 1 : -1
               :
               distance > entry.getDistance() ? 1 : -1;
  }
  
}
