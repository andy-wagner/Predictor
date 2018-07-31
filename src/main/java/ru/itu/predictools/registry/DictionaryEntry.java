package ru.itu.predictools.registry;


import java.util.Objects;

@SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
public class DictionaryEntry extends Entry {
  
  public DictionaryEntry(String word) {
    this(word, 1L);
  }
  
  public DictionaryEntry(String word, Long frequency) {
    super(word, frequency);
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof DictionaryEntry)) {
      return false;
    }
    DictionaryEntry entry = (DictionaryEntry) o;
    return Objects.equals(entry.getWord(), word) && Objects.equals(entry.getFrequency(), frequency);
  }
  
  @Override
  public int compareTo(Entry entry) { //@NotNull
    return this.equals(entry) ? 0 : frequency > entry.getFrequency() ? 1 : -1;
  }
}
