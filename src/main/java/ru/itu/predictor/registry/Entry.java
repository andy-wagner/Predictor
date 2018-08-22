package ru.itu.predictor.registry;

import java.time.LocalDateTime;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
public class Entry implements Comparable<Entry> {
  private static final Logger LOGGER = LogManager.getLogger();
  String string;
  Double frequency;
  LocalDateTime lastUseTime;
  
  public Entry(String string) {
    this(string, 1D, null);
  }
  
  public Entry(String string, Double frequency) {
    this(string, frequency, null);
  }
  
  public Entry(String string, Double frequency, LocalDateTime lastUseTime){
    this.string = string;
    this.frequency = frequency;
    this.lastUseTime = lastUseTime;
  }
  
  public int hashCode() {
    return Objects.hash(this.string);
  }
  
  public Double getFrequency() {
    return this.frequency;
  }
  
  public void setFrequency(Double newFrequency) {
    this.frequency = newFrequency;
  }
  
  public String getString() {
    return this.string;
  }
  
  public void setString(String string) {
    this.string = string;
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
      return Objects.equals(entry.getString(), this.string)
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
