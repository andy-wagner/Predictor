package ru.itu.predictor.search;

import org.junit.Before;
import org.junit.Test;
import ru.itu.predictor.registry.Entry;
import ru.itu.predictor.registry.SearchDictionaryEntry;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class SearchTests {
  private Search search;
  
  @Before
  public void init() throws IOException {
    search = new Search(System.getProperty("user.dir") + File.separator
                            + "src" + File.separator
                            + "test" + File.separator
                            + "resources" + File.separator
                            + "config" + File.separator
                            + "ru-utf8-ngram2-d1-prefix.conf");
  }
  
  @Test
  public void checkPredictorFromConfigFileInstantiating() {
    assertEquals("Search distance should be equal to 1", 1, search.getMaxDistance());
    assertEquals("The language should be equal to 'ru'", "ru", search.getLanguage());
    assertNotNull("predictors alphabet is instance of alphabet", search.getAlphabet());
  }
  
  @Test
  public void checkPredictorsSettersAndGetters() throws IOException {
    search.setMaxDistance(2);
    assertEquals("Search distance should be equal to 2", 2, search.getMaxDistance());
    search.setMaxDistance(1);
    assertEquals("Search distance should be equal to 1", 1, search.getMaxDistance());
    assertNotNull("The Search.getAlphabet should return Alphabet instance", Search.getAlphabet(search.getDictionary()));
    assertNull("reduced alphabet is empty if there is no searches yet", search.getReducedAlphabet());
    assertNull("next symbols alphabet is empty if there is no searches yet", search.getNextSymbolAlphabet());
    search.run("по");
    assertNotNull("reduced alphabet is not empty after search has performed", search.getReducedAlphabet());
    assertNotNull("next symbols alphabet is not empty search has performed", search.getNextSymbolAlphabet());
    char[] alphabet = search.getReducedAlphabet().getChars();
    assertArrayEquals("alphabets should be equal", alphabet, search.getReducedAlphabet("по").getChars());
  }
  
  @Test
  public void checkAddingAndRemovingWords() {
    int size = search.getDictionary().getSearchDictionaryEntries().size();
    
    search.addWord("кaземат");
    assertEquals("New size should be increased by 1", size + 1, search.getDictionary().getSearchDictionaryEntries().size());
    
    search.addPhrase("идите в сад");
    assertEquals("New size should be increased by 2", size + 2, search.getDictionary().getSearchDictionaryEntries().size());
    
    double frequency = search.getEntry("кaземат").getFrequency();
    assertEquals("Frequency of just added user's word should be equal to 1", 1, frequency, 0.0001);
    frequency = search.getEntry("идите в сад").getLocalFrequency();
    assertEquals("Frequency of just added user's word should be equal to 1", 1, frequency, 0.0001);
    
    frequency = search.getEntry("идите в сад").getFrequency();
    assertEquals("Frequency of just added user's word should be equal to 1", 1, frequency, 0.0001);
    frequency = search.getEntry("идите в сад").getLocalFrequency();
    assertEquals("Local frequency of just added user's word should be equal to 1", 1, frequency, 0.0001);
    
    search.updateEntry(search.getDictionary().getUserWordsDictionary(), new Entry("кaземат", 11D));
    frequency = search.getEntry("кaземат").getFrequency();
    assertEquals("Frequency of user's word after update frequency value should be equal to 11", 11, frequency, 0.0001);
    frequency = search.getEntry("кaземат").getLocalFrequency();
    assertEquals("Frequency of user's word after update frequency value should be equal to 1", 1, frequency, 0.0001);
    search.updateEntry(search.getDictionary().getUserWordsDictionary(), new SearchDictionaryEntry("кaземат", 21D, 22D));
    frequency = search.getEntry("кaземат").getFrequency();
    assertEquals("Frequency of user's word after update frequency value should be equal to 21", 21, frequency, 0.0001);
    frequency = search.getEntry("кaземат").getLocalFrequency();
    assertEquals("Frequency of user's word after update frequency value should be equal to 22", 22, frequency, 0.0001);
    
  }
  
}
