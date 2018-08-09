package ru.itu.predictools.registry;

import org.junit.Before;
import org.junit.Test;
import ru.itu.predictools.alphabet.Alphabet;

import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;

import static org.junit.Assert.*;

public class SearchDictionaryTests {
  private SearchDictionary dictionary;
  private Alphabet alphabet;
  private Hashtable<Character, Character> substitutes = new Hashtable<>();
  private String MAIN_DICTIONARY_PATH, USER_WORDS_DICTIONARY_PATH, USER_PHRASES_DICTIONARY_PATH;
  
  @Before
  public void init() throws IOException {
    substitutes.put('ё', 'е');
    
    alphabet = new Alphabet(
        " абвгдежзийклмнопрстуфхцчшщъыьэюя"
        , substitutes
        , "ru"
    );
    MAIN_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                               + "dictionaries" + File.separator
                               + "ru-main-test(do not change this file)-utf8.dic";
    USER_WORDS_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                                     + "user" + File.separator
                                     + "dictionaries" + File.separator
                                     + "ru-user-words-test(do not change this file)-utf8.dic";
    USER_PHRASES_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                                       + "user" + File.separator
                                       + "dictionaries" + File.separator
                                       + "ru-user-phrases-test(do not change this file)-utf8.dic";
    this.dictionary = new SearchDictionary(
        MAIN_DICTIONARY_PATH,
        USER_WORDS_DICTIONARY_PATH,
        USER_PHRASES_DICTIONARY_PATH,
        alphabet
    );
  }
  
  @Test
  public void checkSearchDictionaryInstantiationWithoutAlphabet() throws IOException {
    dictionary = new SearchDictionary(
        MAIN_DICTIONARY_PATH,
        USER_WORDS_DICTIONARY_PATH,
        USER_PHRASES_DICTIONARY_PATH
    );
    assertEquals("Search dictionary entry should contain phrase 'как дела'", "ru", dictionary.getAlphabet().getIsoLanguageName());
    char[] innerAlphabetChars = dictionary.getAlphabet().getChars();
    char[] outerAlphabetChars = this.alphabet.getChars();
    Arrays.sort(innerAlphabetChars);
    Arrays.sort(outerAlphabetChars);
    assertEquals("Alphabet should be equal to dictionary.alphabet", Arrays.toString(outerAlphabetChars), Arrays.toString(innerAlphabetChars));
  }
  
  @Test
  public void checkSearchDictionaryInstantiationWithAlphabet() {
    assertEquals("Search dictionary entry should contain phrase 'как дела'", "ru", this.dictionary.getAlphabet().getIsoLanguageName());
    assertArrayEquals("Alphabet should be equal to dictionary.alphabet", alphabet.getChars(), this.dictionary.getAlphabet().getChars());
    assertEquals("Alphabet length should be equal 33", 33, this.dictionary.getAlphabet().size());
  }
  
  @Test
  public void checkSearchDictionaryEntryThatExistsInUserAndMainDictionary() {
    System.out.println(
        dictionary.getEntry("хоккей").getWord()
            + " ч:" + dictionary.getEntry("хоккей").getFrequency()
            + " лч:" + dictionary.getEntry("хоккей").getLocalFrequency()
            + " пссл.время:" + dictionary.getEntry("хоккей").getLastUseTime()
    );
    assertEquals("base frequency of the word 'хоккей' should be equal to frequency from mainDictionary",
        dictionary.getMainDictionary().getEntry("хоккей").getFrequency(),
        dictionary.getEntry("хоккей").getFrequency(),
        0.0001
    );
    assertEquals("local frequency of the word 'хоккей' should be equal to frequency from userDictionary",
        dictionary.getUserWordsDictionary().getEntry("хоккей").getFrequency(),
        dictionary.getEntry("хоккей").getLocalFrequency(),
        0.0001
    );
  }
  
  @Test
  public void checkSearchDictionaryEntryThatExistsInMainButNotInUserDictionary() {
    System.out.println(
        dictionary.getEntry("солнце").getWord()
            + " ч:" + dictionary.getEntry("солнце").getFrequency()
            + " лч:" + dictionary.getEntry("солнце").getLocalFrequency()
            + " пссл.время:" + dictionary.getEntry("солнце").getLastUseTime()
    );
    assertEquals("base frequency of the word 'солнце' should be equal to frequency from mainDictionary",
        dictionary.getMainDictionary().getEntry("солнце").getFrequency(),
        dictionary.getEntry("солнце").getFrequency(),
        0.0001
    );
    assertEquals("local frequency of the word 'солнце' should be equal to 0",
        0,
        dictionary.getEntry("солнце").getLocalFrequency(),
        0.0001
    );
  }
  
  @Test
  public void checkSearchDictionaryEntryThatExistsInUserButNotInMainDictionary() {
    System.out.println(
        dictionary.getEntry("как дела").getWord()
            + " ч:" + dictionary.getEntry("как дела").getFrequency()
            + " лч:" + dictionary.getEntry("как дела").getLocalFrequency()
            + " пссл.время:" + dictionary.getEntry("как дела").getLastUseTime()
    );
    assertEquals("base frequency of the word 'как дела' should be equal to the frequency from userDictionary",
        dictionary.getMainDictionary().getEntry("как дела").getFrequency(),
        dictionary.getEntry("как дела").getFrequency(),
        0.0001
    );
    assertEquals("local frequency of the word 'как дела' should be equal to the frequency from userDictionary",
        dictionary.getUserPhrasesDictionary().getEntry("как дела").getFrequency(),
        dictionary.getEntry("как дела").getLocalFrequency(),
        0.0001
    );
  }
  
  @Test
  public void getAlphabetFromDictionaryFileCheck() throws IOException {
    char[] innerAlphabetChars = SearchDictionary.getAlphabet(MAIN_DICTIONARY_PATH).getChars();
    alphabet = new Alphabet(
        "абвгдежзийклмнопрстуфхцчшщъыьэюя"
        , "ru"
    );
    char[] outerAlphabetChars = this.alphabet.getChars();
    Arrays.sort(innerAlphabetChars);
    Arrays.sort(outerAlphabetChars);
    assertArrayEquals("SearchDictionary.getAlphabet should be equal to dictionary.alphabet", innerAlphabetChars, outerAlphabetChars);
  }
}
