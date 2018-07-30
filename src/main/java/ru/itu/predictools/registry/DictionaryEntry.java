package ru.itu.predictools.registry;


import java.util.Objects;

public class DictionaryEntry extends Entry implements Comparable<DictionaryEntry> {
  
  @SuppressWarnings("WeakerAccess")
  public DictionaryEntry() {
    this("", 0L);
  }
  
  public DictionaryEntry(String word) {
    this(word, 0L);
  }
  
  @SuppressWarnings("WeakerAccess")
  public DictionaryEntry(String word, Long frequency) {
    super();
    this.word = word;
    this.frequency = frequency;
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SearchResultEntry)) return false;
    SearchResultEntry entry = (SearchResultEntry) o;
    return Objects.equals(entry.getWord(), word) || Objects.equals(entry.getFrequency(), frequency);
  }
  
  @Override //overrides Comparable.compareTo
  public int compareTo(DictionaryEntry entry) { //@NotNull
    return equals(entry) ? 0 : frequency > entry.getFrequency() ? 1 : -1;
  }
  
  @SuppressWarnings("WeakerAccess")
  public void setWord(String word) {
    this.word = word;
  }
  
  @SuppressWarnings("WeakerAccess")
  public void setFrequency(Long frequency) {
    this.frequency = frequency;
  }
  
  public String getWord() {
    return word;
  }
  
  public Long getFrequency() {
    return frequency;
  }
  
}
