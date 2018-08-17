package ru.itu.predictools.entry;

import org.junit.Test;
import ru.itu.predictools.registry.SearchDictionaryEntry;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class OldSearchDictionaryEntryTests {
  
  @Test
  public void checkSearchDictionaryInstantiating() {
    SearchDictionaryEntry entry = new SearchDictionaryEntry("some word");
    assertEquals("Word should be equal to 'some word", "some word", entry.getWord());
    
    assertEquals("Frequency should be equal to 0", 0, entry.getFrequency(), 0.001);
    entry.setFrequency(111.111);
    assertEquals("Frequency should be equal to 111.111", 111.111, entry.getFrequency(), 0.001);
    
    assertEquals("Local local frequency should be equal to 0", 0, entry.getLocalFrequency(), 0.001);
    entry.setLocalFrequency(222.222);
    assertEquals("Local local frequency should be equal to 222.222", 222.222, entry.getLocalFrequency(), 0.001);
    
    assertNull("Last use time should be equal to null", entry.getLastUseTime());
    LocalDateTime now = LocalDateTime.now();
    entry.setLastUseTime(now);
    assertEquals("Last use time should be equal to " + now + " ", now, entry.getLastUseTime());
  
    assertEquals("Distance should be equal to 0", 0, entry.getDistance());
    entry.setDistance(1);
    assertEquals("Distance should be equal to 1", 1, entry.getDistance());
  
    SearchDictionaryEntry entry1 = new SearchDictionaryEntry("some wrd", 333.333);
    assertNotEquals("entry and entry1 should be considered as equal", entry, entry1);
    entry1 = new SearchDictionaryEntry("some word", 333.333, 444.444);
    assertEquals("entry and entry1 shoud be considered as equal", entry, entry1);
    entry = new SearchDictionaryEntry(entry1);
    entry.setLocalFrequency(444.444);
    assertEquals("Frequency should be equal to 333.333", 333.333, entry.getFrequency(), 0.001);
    assertEquals("Local frequency should be equal to 444.444", 444.444, entry.getLocalFrequency(), 0.001);
    entry = new SearchDictionaryEntry("word", 555.555, 666.666, now, 1);
    assertEquals("Word should be 'word'", "word", entry.getWord());
    assertEquals("Frequency should be 555.555", 555.555, entry.getFrequency(), 0.001);
    assertEquals("Local frequency should be 666.666", 666.666, entry.getLocalFrequency(), 0.001);
    assertEquals("Last use time should be "+now+" ", now, entry.getLastUseTime());
    assertEquals("Distance should be 1", 1, entry.getDistance());
  }
}
