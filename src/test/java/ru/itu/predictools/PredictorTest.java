package ru.itu.predictools;

import org.junit.Before;
import org.junit.Test;
import ru.itu.predictools.alphabet.Alphabet;
import ru.itu.predictools.registry.Entry;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class PredictorTest {
  private Predictor predictor;
  
  @Before
  public void init() {
    predictor = new Predictor(System.getProperty("user.dir") + File.separator + "config" + File.separator + "predictor.conf");
  }
  
  @Test
  public void checkPredictorFromConfigFileInstantiating() {
    assertEquals("Search distance should be equal to 1", 1, predictor.getMaxDistance());
    assertEquals("The language should be equal to 'ru'", "ru", predictor.getLanguage());
    assertThat("predictors alphabet is instance of alphabet", predictor.getAlphabet() != null);
  }
  
  @Test
  public void checkPredictorsSettersAndGetters() throws IOException {
    predictor.setMaxDistance(2);
    assertEquals("Search distance should be equal to 2", 2, predictor.getMaxDistance());
    predictor.setMaxDistance(1);
    assertEquals("Search distance should be equal to 2", 1, predictor.getMaxDistance());
    assertThat("The Predictor.getAlphabet should return Alphabet instance", Predictor.getAlphabet(predictor.getDictionary()) != null);
    assertThat("reduced alphabet is empty if there is no searches yet", predictor.getReducedAlphabet() == null);
    predictor.search("по");
    assertThat("reduced alphabet is not empty after search has been performed", predictor.getReducedAlphabet() != null);
    char[] alphabet = predictor.getReducedAlphabet().getChars();
    assertArrayEquals("alphabets should be equal", alphabet, predictor.getReducedAlphabet("по").getChars());
  }
  
  @Test
  public void checkAddingAndRemovingWords(){
    int size = predictor.getDictionary().getSearchDictionaryEntries().size();
    predictor.addWord("кaземат");
    assertEquals("New size should increase by 1", size+1, predictor.getDictionary().getSearchDictionaryEntries().size());
    predictor.addPhrase("хорошо сидим");
    assertEquals("New size should increase by 1", size+2, predictor.getDictionary().getSearchDictionaryEntries().size());
    double frequency = predictor.getWordEntry("каземат").getFrequency();
    System.out.println(frequency);
//    assertEquals("Frequency of just added user's word should be equal to 1", 1, frequency, 0.0001);
//    predictor.updateWord(new Entry("кaземат", 22D));
//    assertEquals("Frequency of user's word after update frequency value should be equal to 22", 22, frequency, 0.0001);
  }
  
}
