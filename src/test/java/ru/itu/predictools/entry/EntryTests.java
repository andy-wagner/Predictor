package ru.itu.predictools.entry;

import org.junit.Before;
import org.junit.Test;
import ru.itu.predictools.registry.Entry;

import java.util.Objects;

import static org.junit.Assert.*;


public class EntryTests {
  private Entry entry;
  
  class EntryTestClass extends Entry {
    String word;
    Long frequency;
    
    EntryTestClass(String word, Long frequency) {
      super(word, frequency);
      this.word = word;
      this.frequency = frequency;
    }
    
  }
  
  @Before
  public void init() {
    entry = new Entry("test", 1234L);
  }
  
  @Test
  public void testEntryInstantiating() {
    entry = new Entry("test");
    assertEquals("The word should be 'test'", "test", entry.getWord());
    assertEquals("The frequency should be equal to 1", 1L, entry.getFrequency());
    entry = new Entry("test2", 282828L);
    assertEquals("The word should be 'test2'", "test2", entry.getWord());
    assertEquals("The frequency should be equal to 1", 282828L, entry.getFrequency());
  }
  
  @Test
  public void hashCodeCheck() {
    int hash = Objects.hash(entry.getWord());
    assertEquals("Hash code check failed", hash, entry.hashCode());
  }
  
  @Test
  public void checkGetAndSetFrequency() {
    assertTrue("If set is successful setFrequency should return 'true'", entry.setFrequency(1L));
    assertEquals("Frequency should be equal to 1", 1, entry.getFrequency());
    assertTrue("If set is successful setWord should return 'true'", entry.setWord("TestTest"));
    assertEquals("Frequency should be equal to 'TestTest'", "TestTest", entry.getWord());
  }
  
  @Test
  public void checkOverridenEqualsFromComparableInterface() {
    Entry entry = new Entry("word", 2L);
    EntryTestClass entryTest = new EntryTestClass("word", 3L);
    Entry entry1 = new Entry("wor", 2L);
    assertNotEquals("'entry' and 'entryTest' shouldn't be equal", entry, entryTest);
    assertNotEquals("'entry' and 'entry1' shouldn't be equal", entry, entry1);
    entryTest = new EntryTestClass("word", 2L);
    entry1 = new Entry("word", 2L);
    assertEquals("'entry' and 'entryTest' should be equal", entry, entryTest);
    assertEquals("'entry' and 'entry1' should be equal", entry, entry1);
    assertEquals("'entry' and 'entry1' should be equal", entry, entry);
  }
  
  @Test
  public void checkOverridenCompareToFromComparableInterface() {
    Entry entry = new Entry("word", 3L);
    EntryTestClass entryTest = new EntryTestClass("word", 3L);
    Entry entry1 = new Entry("wor", 4L);
    assertEquals("entry and entryTest should be equal", entry, entryTest);
    assertNotEquals("entry1 and entryTest shouldn't be equal", entry1, entryTest);
    assertNotEquals("entry1 and entry shouldn't be equal", entry, entry1);
    assertEquals("entry1 should be greater than entryTest", 1, entry1.compareTo(entryTest));
    assertEquals("entry should be lesser than entry1", -1, entry.compareTo(entry1));
    assertEquals("entry should be equal to entryTest", 0, entry.compareTo(entryTest));
  }
}
