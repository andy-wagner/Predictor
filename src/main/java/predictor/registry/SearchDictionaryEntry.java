package predictor.registry;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс SearchDictionaryEntry представляет собой хранилище для данных записи поискового словаря и методов для
 * доступа к ним
 *
 * *словарь поиска или поисковый словарь - словарь созданный во время создания объекта класса Predictor путём объединения основного словаря и пользовательских словарей - фраз и слов*словарь поиска или поисковый словарь - словарь
 * созданный во время создания объекта класса Predictor путём объединения основного словаря и пользовательских
 * словарей - фраз и слов
 */
@SuppressWarnings({/*"unused",*/ "WeakerAccess", "UnusedReturnValue"})
public class SearchDictionaryEntry extends Entry {
  private Integer distance; //distance from this entry string to search target //TODO absolute or relative distance what kind is preferable???
  private Double localFrequency; //frequency from user's dictionaries
  
  public SearchDictionaryEntry(String string) {
    this(string, 0D, 0D, null, 0);
  }
  
  public SearchDictionaryEntry(Entry entry) {
    this(entry.getString(), entry.getFrequency(), 0D, entry.getLastUseTime(), 0);
  }
  
  public SearchDictionaryEntry(String string, Double globalFrequency) {
    this(string, globalFrequency, 0D, null, 0);
  }
  
  public SearchDictionaryEntry(String string, Double globalFrequency, Double localFrequency) {
    this(string, globalFrequency, localFrequency, null, 0);
  }
  
  public SearchDictionaryEntry(Entry entry, Integer distance) {
    this(entry.getString(), entry.getFrequency(), 0D, entry.getLastUseTime(), distance);
  }
  
  public SearchDictionaryEntry(Entry entry, Double localFrequency) {
    this(entry.getString(), entry.getFrequency(), localFrequency, null, 0);
  }
  
  public SearchDictionaryEntry(Entry entry, Double localFrequency, LocalDateTime lastUseTime) {
    this(entry.getString(), entry.getFrequency(), localFrequency, lastUseTime, 0);
  }
  
  public SearchDictionaryEntry(Entry entry, Double localFrequency, Integer distance) {
    this(entry.getString(), entry.getFrequency(), localFrequency, entry.getLastUseTime(), distance);
  }
  
  public SearchDictionaryEntry(Entry entry, Double localFrequency, LocalDateTime lastUseTime, Integer distance) {
    this(entry.getString(), entry.getFrequency(), localFrequency, lastUseTime, distance);
  }
  
  public SearchDictionaryEntry(String string, Double globalFrequency, Double localFrequency, LocalDateTime lastUseTime) {
    this(string, globalFrequency, localFrequency, lastUseTime, 0);
  }
  
  public SearchDictionaryEntry(String string, Double globalFrequency, Double localFrequency, LocalDateTime lastUseTime, Integer distance) {
    super(string, globalFrequency, lastUseTime);
    this.localFrequency = localFrequency;
    this.distance = distance;
  }
  
  public Double getLocalFrequency() {
    return this.localFrequency;
  }
  
  public void setLocalFrequency(Double localFrequency) {
    this.localFrequency = localFrequency;
  }
  
  public int getDistance() {
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
      
      return Objects.equals(entry.getString(), this.string)
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
