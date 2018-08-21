package ru.itu.predictor.entry;

import org.junit.Before;
import org.junit.Test;
import ru.itu.predictor.registry.Entry;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.Assert.*;


public class EntryTests {
  private Entry entry;
  
  class EntryTestClass extends Entry {
    String word;
    Double frequency;
    
    EntryTestClass(String word, Double frequency) {
      super(word, frequency);
      this.word = word;
      this.frequency = frequency;
    }
    
  }
  
  @Before
  public void init() {
    entry = new Entry("test", 1234D);
  }
  
  @Test
  public void testEntryInstantiating() {
    entry = new Entry("test");
    assertEquals("The word should be 'test'", "test", entry.getWord());
    assertEquals("The frequency should be equal to 1", 1D, entry.getFrequency(), 0.001);
    entry = new Entry("test2", 282828D);
    assertEquals("The word should be 'test2'", "test2", entry.getWord());
    assertEquals("The frequency should be equal to 1", 282828D, entry.getFrequency(), 0.001);
  }
  
  @Test
  public void hashCodeCheck() {
    int hash = Objects.hash(entry.getWord());
    assertEquals("Hash code check failed", hash, entry.hashCode());
  }
  
  @Test
  public void checkGetAndSetFrequency() {
    entry.setFrequency(1D);
    assertEquals("Frequency should be equal to 1", 1, entry.getFrequency(), 0.001);
    entry.setWord("TestTest");
    assertEquals("Frequency should be equal to 'TestTest'", "TestTest", entry.getWord());
    LocalDateTime time = LocalDateTime.parse("1970-10-27T20:30:00");
    entry.setLastUseTime(time);
    assertEquals("Time should be equal to 1970-10-27T20:30:00", time, entry.getLastUseTime());
  }
  
  @Test
  public void checkOverridenEqualsFromComparableInterface() {
    Entry entry = new Entry("word", 2D);
    EntryTestClass entryTest = new EntryTestClass("word", 3D);
    Entry entry1 = new Entry("wor", 2D);
    assertEquals("'entry' and 'entryTest' shouldn't be equal", entry, entryTest);
    assertNotEquals("'entry' and 'entry1' shouldn't be equal", entry, entry1);
    entryTest = new EntryTestClass("word", 2D);
    entry1 = new Entry("word", 3D);
    assertEquals("'entry' and 'entryTest' should be equal", entry, entryTest);
    assertEquals("'entry' and 'entry1' should be equal", entry, entry1);
    assertEquals("'entry' and 'entry1' should be equal", entry, entry);
  }
  
  @Test
  public void checkOverridenCompareToFromComparableInterface() {
    Entry entry = new Entry("word", 3D);
    EntryTestClass entryTest = new EntryTestClass("word", 3D);
    Entry entry1 = new Entry("wor", 4D);
    assertEquals("entry and entryTest should be equal", entry, entryTest);
    assertNotEquals("entry1 and entryTest shouldn't be equal", entry1, entryTest);
    assertNotEquals("entry1 and entry shouldn't be equal", entry, entry1);
    assertEquals("entry1 should be greater than entryTest", 1, entry1.compareTo(entryTest));
    assertEquals("entry should be lesser than entry1", -1, entry.compareTo(entry1));
    assertEquals("entry should be equal to entryTest", 0, entry.compareTo(entryTest));
  }
}
