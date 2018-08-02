package ru.itu.predictools.registry;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DictionaryTests {
  private Dictionary dictionary;
  private Set<Entry> entries;
  private Set<Character> characters;
  private String DICTIONARY_PATH;
  
  @Before
  public void init() {
    entries = new HashSet<>();
    characters = new HashSet<>();
    DICTIONARY_PATH = System.getProperty("user.dir") + File.separator + "ru-main-v1-utf8.dic";
    entries.add(new Entry("one", 1D));
    entries.add(new Entry("two", 1D));
    entries.add(new Entry("three", 1D));
    entries.add(new Entry("four", 1D));
    entries.add(new Entry("five", 1D));
    characters.add('a');
    characters.add('b');
    characters.add('c');
    characters.add('d');
    characters.add('e');
    characters.add('f');
    characters.add('g');
    characters.add('j');
    
  }
  
  
  @Test
  public void testEmptyDictionaryInstantiating() {
    dictionary = new Dictionary();
    assertEquals("dictionary.entries.size should be equal to 0", 0, dictionary.getEntries().size());
    assertEquals("dictionary.alphabet.size should be equal to 0", 0, dictionary.getCharsSet().size());
    assertEquals("dictionary.isoLanguageName should be equal to ''", "", dictionary.getIsoLanguageName());
    assertEquals("dictionary.maxWordLength should be equal to 0", 0, dictionary.getMaxWordLength());
  }
  
  @Test
  public void testDictionaryInstantiatingInitializedByEntriesSet() {
    dictionary = new Dictionary(entries, "en");
    assertEquals("CharsSet should be equal to [r, t, e, u, f, v, w, h, i, n, o]","[r, t, e, u, f, v, w, h, i, n, o]", dictionary.getCharsSet().toString());
    assertEquals("dictionary.entries.size should be equal to 5", 5, dictionary.getEntries().size());
    assertEquals("dictionary.alphabet.size should be equal to 11", 11, dictionary.getCharsSet().size());
    assertEquals("dictionary.isoLanguageName should be equal to 'en'", "en", dictionary.getIsoLanguageName());
    assertEquals("dictionary.maxWordLength should be equal to 6", 5, dictionary.getMaxWordLength());
  }
  
  @Test
  public void testDictionaryInstantiatingByEntriesAndAlphabetSets(){
    dictionary = new Dictionary(entries, characters, "de");
    assertEquals("CharsSet should be equal to [a, b, c, d, e, f, g, j]","[a, b, c, d, e, f, g, j]", dictionary.getCharsSet().toString());
    assertEquals("dictionary.entries.size should be equal to 5", 5, dictionary.getEntries().size());
    assertEquals("dictionary.alphabet.size should be equal to 8", 8, dictionary.getCharsSet().size());
    assertEquals("dictionary.isoLanguageName should be equal to 'de'", "de", dictionary.getIsoLanguageName());
    assertEquals("dictionary.maxWordLength should be equal to 6", 5, dictionary.getMaxWordLength());
  }
  
  @Test
  public void testDictionaryInstantiatingFromFile() throws IOException {
    dictionary = new Dictionary(DICTIONARY_PATH);
    assertEquals("CharsSet should be equal to [р, с, т, у, ф, х, ц, ч, ш, щ, ъ, ы, ь, э, ю, я,  , а, б, в, г, д, е, ж, з, и, й, к, л, м, н, о, п]","[р, с, т, у, ф, х, ц, ч, ш, щ, ъ, ы, ь, э, ю, я,  , а, б, в, г, д, е, ж, з, и, й, к, л, м, н, о, п]", dictionary.getCharsSet().toString());
    assertEquals("dictionary.entries.size should be equal to 69307", 69307, dictionary.getEntries().size());
    assertEquals("dictionary.alphabet.size should be equal to 33", 33, dictionary.getCharsSet().size());
    assertEquals("dictionary.isoLanguageName should be equal to 'ru'", "ru", dictionary.getIsoLanguageName());
    assertEquals("dictionary.maxWordLength should be equal to 25", 25, dictionary.getMaxWordLength());
  }
}
