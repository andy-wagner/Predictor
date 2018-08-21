package ru.itu.predictor.registry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DictionaryTests {
  private Dictionary dictionary, dictionaryToSave;
  private Set<Entry> entries, entries2, entries3;
  private Set<Character> characters;
  private String DICTIONARY_PATH;
  
  @Before
  public void init() throws IOException {
    entries = new HashSet<>();
    entries2 = new HashSet<>();
    entries3 = new HashSet<>();
    characters = new HashSet<>();
    DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                          + "src" + File.separator
                          + "test" + File.separator
                          + "resources" + File.separator
                          + "dictionaries" + File.separator
                          + "ru-main-test(do not change this file)-utf8.dic";
    entries.add(new Entry("four"));
    entries.add(new Entry("five"));
    entries.add(new Entry("six"));
    entries.add(new Entry("seven"));
    entries.add(new Entry("eight"));
    entries2.add(new Entry("one"));
    entries2.add(new Entry("two"));
    entries2.add(new Entry("three"));
    entries2.add(new Entry("four", 11.11D));
    entries2.add(new Entry("five"));
    entries3.add(new Entry("uno"));
    entries3.add(new Entry("dos"));
    entries3.add(new Entry("tres"));
    entries3.add(new Entry("quatro"));
    entries3.add(new Entry("cinco"));
    characters.add('a');
    characters.add('b');
    characters.add('c');
    characters.add('d');
    characters.add('e');
    characters.add('f');
    characters.add('g');
    characters.add('j');
    dictionaryToSave = new Dictionary(
        System.getProperty("user.dir")+ File.separator
            + "src" + File.separator
            + "test" + File.separator
            + "resources" + File.separator
            + "dictionaries" + File.separator
            + "user" + File.separator
            + "ru-user-words-test(do not change this file)-utf8.dic"
    );
  }
  
  @Test
  public void testEmptyDictionaryInstantiating() {
    dictionary = new Dictionary();
    assertEquals("dictionary.entries.size should be equal to 0", 0, dictionary.getEntries().size());
    assertEquals("dictionary.alphabet.size should be equal to 0", 0, dictionary.getCharSet().size());
    assertEquals("dictionary.isoLanguageName should be equal to ''", "", dictionary.getIsoLanguageName());
    assertEquals("dictionary.maxWordLength should be equal to 0", 0, dictionary.getMaxWordLength());
  }
  
  @Test
  public void testDictionaryInstantiatingInitializedByEntriesSet() {
    dictionary = new Dictionary(entries, "en");
    assertEquals("charSet should be equal to [e, f, g, h, i, n, o, r, s, t, u, v, x]", "[e, f, g, h, i, n, o, r, s, t, u, v, x]", dictionary.getCharSet().toString());
    assertEquals("dictionary.entries.size should be equal to 5", 5, dictionary.getEntries().size());
    assertEquals("dictionary.alphabet.size should be equal to 13", 13, dictionary.getCharSet().size());
    assertEquals("dictionary.isoLanguageName should be equal to 'en'", "en", dictionary.getIsoLanguageName());
    assertEquals("dictionary.maxWordLength should be equal to 6", 5, dictionary.getMaxWordLength());
  }
  
  @Test
  public void testDictionaryInstantiatingByEntriesAndAlphabetSets() {
    dictionary = new Dictionary(entries, characters, "de", "none");
    assertEquals("charSet should be equal to [e, f, g, h, i, n, o, r, s, t, u, v, x]", "[e, f, g, h, i, n, o, r, s, t, u, v, x]", dictionary.getCharSet().toString());
    assertEquals("dictionary.entries.size should be equal to 5", 5, dictionary.getEntries().size());
    assertEquals("dictionary.alphabet.size should be equal to 13", 13, dictionary.getCharSet().size());
    assertEquals("dictionary.isoLanguageName should be equal to 'de'", "de", dictionary.getIsoLanguageName());
    assertEquals("dictionary.maxWordLength should be equal to 6", 5, dictionary.getMaxWordLength());
  }
  
  @Test
  public void testDictionaryInstantiatingFromFile() throws IOException {
    dictionary = new Dictionary(DICTIONARY_PATH);
    assertEquals("charSet should be equal to [р, с, т, у, ф, х, ц, ч, ш, щ, ъ, ы, ь, э, ю, я, а, б, в, г, д, е, ж, з, и, й, к, л, м, н, о, п]", "[р, с, т, у, ф, х, ц, ч, ш, щ, ъ, ы, ь, э, ю, я, а, б, в, г, д, е, ж, з, и, й, к, л, м, н, о, п]", dictionary.getCharSet().toString());
    assertEquals("dictionary.entries.size should be equal to 69307", 69307, dictionary.getEntries().size());
    assertEquals("dictionary.alphabet.size should be equal to 32", 32, dictionary.getCharSet().size());
    assertEquals("dictionary.isoLanguageName should be equal to 'ru'", "ru", dictionary.getIsoLanguageName());
    assertEquals("dictionary.maxWordLength should be equal to 24", 24, dictionary.getMaxWordLength());
  }
  
  @Test
  public void checkGetAddRemoveUpdateEntry() {
    dictionary = new Dictionary();
    Entry entry = new Entry("word2", 2D);
    
    dictionary.addEntry("word1");
    assertEquals("Size of an empty dictionary with an entry added should be equal to 1", 1, dictionary.getEntries().size());
    assertEquals("Frequency of 'word1' should be 1D", 1D, dictionary.getEntry("word1").getFrequency(), 0.0001);
    
    dictionary.addEntry(entry);
    assertEquals("Size of an empty dictionary with two entry added should be equal to 2", 2, dictionary.getEntries().size());
    assertEquals("Frequency of entry should be 2D", 2D, dictionary.getEntry(entry).getFrequency(), 0.0001);
    
    dictionary.addEntry("word3", 30.5D);
    assertEquals("Size of an empty dictionary with three entries added should be equal to 3", 3, dictionary.getEntries().size());
    assertEquals("Frequency of 'word3' should be 30.5D", 30.5D, dictionary.getEntry("word3").getFrequency(), 0.0001);
    
    
    assertEquals("Frequency of 'word1' should be equal to 1", 1D, dictionary.getEntry("word1").getFrequency(), 0.0001);
    
    dictionary.removeEntry("word1");
    assertEquals("Size of the dictionary of 3 entries with an entry removed should be equal to 2", 2, dictionary.getEntries().size());
    
    dictionary.updateEntry("word2", 22.22D);
    assertEquals("Frequency of 'word2' should be 22.22D", 22.22D, dictionary.getEntry(entry).getFrequency(), 0.0001);
    
    entry.setFrequency(4D);
    dictionary.updateEntry(entry);
    assertEquals("Frequency of entry should be equal to frequency of 'word2' and should be 4D", 4D, dictionary.getEntry("word2").getFrequency(), 0.0001);
    
    dictionary.removeEntry(entry);
    assertNull("If there is no such entry then getEntry(entry) should return null", dictionary.getEntry(entry));
    assertEquals("Size of the dictionary with one more entry deleted should be equal to 1", 1, dictionary.getEntries().size());
    
    String lastWord = dictionary.getEntries().iterator().next().getWord();
    assertEquals("Last entry word should be 'word3", "word3", lastWord);
    
    dictionary.removeEntry("word1");
    assertEquals("Size of the dictionary after an attempt of deleting an absent entry shouldn't change and should be equal to 1 as before the removeEntry operation", 1, dictionary.getEntries().size());
    
    dictionary.removeEntry("word3");
    assertEquals("Size of the dictionary after last entry deleted should be equal to 0", 0, dictionary.getEntries().size());
    
    dictionary.addAllEntries(entries);
    assertEquals("Size of the dictionary after adding set of 5 entries should be equal to 5", 5, dictionary.getEntries().size());
    
    dictionary.addAllEntries(entries);
    dictionary.removeAllEntries(entries2);
    assertEquals("Size of the dictionary after removing another one with 2 identical to the first set of 5 entries should be equal to 5-2=3", 3, dictionary.getEntries().size());
    
    dictionary.updateEntry("five", 2D);
    dictionary.removeAllEntries(entries2);
    assertEquals("Size of the dictionary after removing another set of 5 entries, where 2 of them are identical to the entries in the first set (except for the one element - frequency changed) should be equal to 5-2=3", 3, dictionary.getEntries().size());
  }
  
  @Test
  public void checkGettersAndSetters() {
    dictionary = new Dictionary();
    dictionary.setIsoLanguageName("ru");
    assertEquals("ISO Language name should be equal to 'ru'", "ru", dictionary.getIsoLanguageName());
    
  }
  
  @Test
  public void checkDictionariesMergingWithoutUpdate() {
    dictionary = new Dictionary(entries, "en");
    Dictionary dictionary2 = new Dictionary(entries2, "en");
    Dictionary dictionary3 = new Dictionary(entries3, "en");
    Double frequencyBefore = dictionary.getEntry("four").getFrequency();
    dictionary.mergeDictionary(dictionary2, false);
    assertEquals("Frequency before merge should be equal to frequency after", frequencyBefore, dictionary.getEntry("four").getFrequency(), 0.001);
    assertEquals("Length of dictionary entries set should increase by the number of elements that weren't in the origin set of entries before merging with dictionary2 (5+3=8) ",
        8, dictionary.getEntries().size());
    dictionary.mergeDictionary(dictionary3, false);
    assertEquals("Length of dictionary entries set should be equal to 13 (8+5) after merging with dictionary of 5 words new",
        13, dictionary.getEntries().size());
    dictionary.mergeDictionary(dictionary2, false).mergeDictionary(dictionary3, false);//check chain call
    assertEquals("Length of dictionary entries set should be equal to 13 (8+5) after merging with dictionary of 5 words new",
        13, dictionary.getEntries().size());
  }
  
  @Test
  public void checkDictionariesMergingWithUpdate() {
    dictionary = new Dictionary(entries, "en");
    Dictionary dictionary2 = new Dictionary(entries2, "en");
    Dictionary dictionary3 = new Dictionary(entries3, "en");
    Double frequencyBefore = dictionary.getEntry("four").getFrequency();
    dictionary.mergeDictionary(dictionary2, true);
    assertNotEquals("Frequency before merge shouldn't be equal to frequency after", frequencyBefore, dictionary.getEntry("four").getFrequency(), 0.001);
    assertEquals("Frequency before merge should be equal to frequency from dictionary2 after", dictionary2.getEntry("four").getFrequency(), dictionary.getEntry("four").getFrequency(), 0.001);
    assertEquals("Length of dictionary entries set should increase by the number of elements that weren't in the origin set of entries before merging with dictionary2 (5+3=8) ",
        8, dictionary.getEntries().size());
    dictionary.mergeDictionary(dictionary3, true);
    assertEquals("Length of dictionary entries set should be equal to 13 (8+5) after merging with dictionary of 5 words new",
        13, dictionary.getEntries().size());
    dictionary.mergeDictionary(dictionary2, true).mergeDictionary(dictionary3, true);//check chain call
    assertEquals("Length of dictionary entries set should be equal to 13 (8+5) after merging with dictionary of 5 words new",
        13, dictionary.getEntries().size());
  }
  
  private boolean compareFiles(String fileName1, String fileName2) throws IOException {
    BufferedReader reader1, reader2;
    String line1, line2;
    reader1 = new BufferedReader(new FileReader(fileName1));
    reader2 = new BufferedReader(new FileReader(fileName2));
    
    while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null) {
      if (!line1.equals(line2)) {
        return false;
      }
    }
    return true;
  }
  
  @Test
  public void checkSavingDictionaryToFile() throws IOException {
    final String USER_WORDS_PATH = System.getProperty("user.dir") + File.separator
                                       + "src" + File.separator
                                       + "test" + File.separator
                                       + "resources" + File.separator
                                       + "dictionaries" + File.separator
                                       + "user" + File.separator
                                       + "ru-user-words-test(do not change this file)-utf8.dic";
    final String TEST_WORDS_PATH = System.getProperty("user.dir") + File.separator
                                       + "src" + File.separator
                                       + "test" + File.separator
                                       + "resources" + File.separator
                                       + "dictionaries" + File.separator
                                       + "user" + File.separator
                                       + "UserWordsDictionarySavedFromTest.dic";
    final String USER_PHRASES_PATH = System.getProperty("user.dir") + File.separator
                                         + "src" + File.separator
                                         + "test" + File.separator
                                         + "resources" + File.separator
                                         + "dictionaries" + File.separator
                                         + "user" + File.separator
                                         + "ru-user-phrases-test(do not change this file)-utf8.dic";
    final String TEST_PHRASES_PATH = System.getProperty("user.dir") + File.separator
                                         + "src" + File.separator
                                         + "test" + File.separator
                                         + "resources" + File.separator
                                         + "dictionaries" + File.separator
                                         + "user" + File.separator
                                         + "UserPhrasesDictionarySavedFromTest.dic";
    
    dictionary = new Dictionary(USER_WORDS_PATH);
    dictionaryToSave.save(TEST_WORDS_PATH);
    assertTrue("Content of files should be equal", compareFiles(USER_WORDS_PATH, TEST_WORDS_PATH));
    dictionaryToSave = new Dictionary(USER_PHRASES_PATH);
    dictionaryToSave.save(TEST_PHRASES_PATH);
    assertTrue("Content of files should be equal", compareFiles(USER_PHRASES_PATH, TEST_PHRASES_PATH));
    
  }
  
  @Rule
  public ExpectedException thrownFromDictionaryMergeErrorCheck = ExpectedException.none();
  
  @Test
  public void dictionaryMergeErrorCheck() {
    dictionary = new Dictionary(entries, "en");
    Dictionary dictionary2 = new Dictionary(entries3, "es");
    thrownFromDictionaryMergeErrorCheck.expect(RuntimeException.class);
    thrownFromDictionaryMergeErrorCheck.expectMessage("Error: the languages of merged dictionaries should be the same");
    dictionary.mergeDictionary(dictionary2, true);
    dictionary.mergeDictionary(dictionary2, false);
  }
}
