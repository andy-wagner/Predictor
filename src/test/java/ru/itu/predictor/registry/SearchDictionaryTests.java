package ru.itu.predictor.registry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.itu.predictor.alphabet.Alphabet;

import java.io.IOException;
import java.io.File;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Hashtable;

import static org.junit.Assert.*;

public class SearchDictionaryTests {
  private SearchDictionary dictionary;
  private Alphabet alphabet;
  private Hashtable<Character, Character> substitutes = new Hashtable<>();
  private String MAIN_DICTIONARY_PATH, USER_WORDS_DICTIONARY_PATH, USER_PHRASES_DICTIONARY_PATH, USER_PHRASES_EN_DICTIONARY_PATH;
  private Dictionary mainDictionary, userWordsDictionary, userPhrasesDictionary;
  
  @Before
  public void init() throws IOException {
    substitutes.put('ё', 'е');
    
    alphabet = new Alphabet(
        " абвгдежзийклмнопрстуфхцчшщъыьэюя"
        , substitutes
        , "ru"
    );
    
    MAIN_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                               + "src" + File.separator
                               + "test" + File.separator
                               + "resources" + File.separator
                               + "dictionaries" + File.separator
                               + "ru-main-test(do not change this file)-utf8.dic";
    USER_WORDS_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                                     + "src" + File.separator
                                     + "test" + File.separator
                                     + "resources" + File.separator
                                     + "dictionaries" + File.separator
                                     + "user" + File.separator
                                     + "ru-user-words-test(do not change this file)-utf8.dic";
    USER_PHRASES_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                                       + "src" + File.separator
                                       + "test" + File.separator
                                       + "resources" + File.separator
                                       + "dictionaries" + File.separator
                                       + "user" + File.separator
                                       + "ru-user-phrases-test(do not change this file)-utf8.dic";
    USER_PHRASES_EN_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                                          + "src" + File.separator
                                          + "test" + File.separator
                                          + "resources" + File.separator
                                          + "dictionaries" + File.separator
                                          + "user" + File.separator
                                          + "en-user-phrases-test(do not change this file)-utf8.dic";
    
    mainDictionary = new Dictionary(MAIN_DICTIONARY_PATH);
    userWordsDictionary = new Dictionary(USER_WORDS_DICTIONARY_PATH);
    userPhrasesDictionary = new Dictionary(USER_PHRASES_DICTIONARY_PATH);
    
    this.dictionary = new SearchDictionary(
        MAIN_DICTIONARY_PATH,
        USER_WORDS_DICTIONARY_PATH,
        USER_PHRASES_DICTIONARY_PATH,
        alphabet
    );
  }
  
  @Test
  public void checkSearchDictionaryInstantiatingFromDictionaries() {
    SearchDictionary dictionary = new SearchDictionary(mainDictionary, userWordsDictionary, userPhrasesDictionary);
    assertNotNull("Search dictionary created successfully from dictionaries with auto alphabet", dictionary);
    Alphabet alphabet = new Alphabet("абвгд", "ru");
    dictionary = new SearchDictionary(mainDictionary, userWordsDictionary, userPhrasesDictionary, alphabet);
    assertNotNull("Search dictionary created successfully from dictionaries with arbitrary alphabet parameter", dictionary);
  }
  
  @Rule
  public ExpectedException thrownFromMakeSearchDictionaryErrorCheck = ExpectedException.none();
  
  @Test
  public void checkDictionariesAndAlphabetShouldHaveSameLanguageError() {
    Alphabet alphabet = new Alphabet("abcd", "en");
    thrownFromMakeSearchDictionaryErrorCheck.expect(RuntimeException.class);
    thrownFromMakeSearchDictionaryErrorCheck.expectMessage("Error: Language of the alphabet specified doesn't match with searchDictionary language");
    SearchDictionary dictionary = new SearchDictionary(mainDictionary, userWordsDictionary, userPhrasesDictionary, alphabet);
    assertEquals("Dictionaries and alphabet should have same languages", "ru", dictionary.getIsoLanguageName());
  }
  
  @Test
  public void checkDictionariesShouldHaveSameLanguageError() throws IOException {
    Dictionary enUserDictionary = new Dictionary(USER_PHRASES_EN_DICTIONARY_PATH);
    thrownFromMakeSearchDictionaryErrorCheck.expect(RuntimeException.class);
    thrownFromMakeSearchDictionaryErrorCheck.expectMessage("Error: main dictionary and users dictionary should be the same language to make search dictionary.");
    SearchDictionary dictionary = new SearchDictionary(mainDictionary, userWordsDictionary, enUserDictionary);
    assertEquals("Dictionaries should have same languages", "ru", dictionary.getIsoLanguageName());
  }
  
  @Test
  public void checkSearchDictionaryInstantiationFromFilesWithoutAlphabet() {
    dictionary = new SearchDictionary(
        MAIN_DICTIONARY_PATH,
        USER_WORDS_DICTIONARY_PATH,
        USER_PHRASES_DICTIONARY_PATH
    );
    assertEquals("Search dictionary language should be equal to 'ru'", "ru", this.dictionary.getIsoLanguageName());
    assertEquals("User if should be equal to 'boris'", "boris", this.dictionary.getUserId());
    char[] innerAlphabetChars = dictionary.getAlphabet().getChars();
    char[] outerAlphabetChars = this.alphabet.getChars();
    Arrays.sort(innerAlphabetChars);
    Arrays.sort(outerAlphabetChars);
    assertEquals("Alphabet should be equal to dictionary.alphabet", Arrays.toString(outerAlphabetChars), Arrays.toString(innerAlphabetChars));
    assertEquals("Length of search dictionary should be equal to 69311", 69311, this.dictionary.getSearchDictionaryEntries().size());
    assertEquals("maxWordLength should be equal to 24", 24, this.dictionary.getMaxWordLength());
  }
  
  @Test
  public void checkSearchDictionaryInstantiationFromFilesWithAlphabet() {
    assertEquals("Search dictionary language should be equal to 'ru'", "ru", this.dictionary.getAlphabet().getIsoLanguageName());
    assertEquals("User if should be equal to 'boris'", "boris", this.dictionary.getUserId());
    assertArrayEquals("Alphabet should be equal to dictionary.alphabet", alphabet.getChars(), this.dictionary.getAlphabet().getChars());
    assertEquals("Alphabet length should be equal 33", 33, this.dictionary.getAlphabet().size());
    assertEquals("Length of search dictionary should be equal to 69311", 69311, this.dictionary.getSearchDictionaryEntries().size());
    assertEquals("maxWordLength should be equal to 24", 24, this.dictionary.getMaxWordLength());
  }
  
  @Test
  public void checkSearchDictionaryEntryThatExistsInUserAndMainDictionary() {
    assertEquals("base frequency of the word 'хоккей' should be equal to frequency from mainDictionary",
        dictionary.getMainDictionary().getEntry("хоккей").getFrequency(),
        dictionary.getSearchDictionaryEntry("хоккей").getFrequency(),
        0.0001
    );
    assertEquals("local frequency of the word 'хоккей' should be equal to frequency from userDictionary",
        dictionary.getUserWordsDictionary().getEntry("хоккей").getFrequency(),
        dictionary.getSearchDictionaryEntry("хоккей").getLocalFrequency(),
        0.0001
    );
  }
  
  @Test
  public void checkSearchDictionaryEntryThatExistsInMainButNotInUserDictionary() {
    assertEquals("base frequency of the word 'солнце' should be equal to frequency from mainDictionary",
        dictionary.getMainDictionary().getEntry("солнце").getFrequency(),
        dictionary.getSearchDictionaryEntry("солнце").getFrequency(),
        0.0001
    );
    assertEquals("local frequency of the word 'солнце' should be equal to 0",
        0,
        dictionary.getSearchDictionaryEntry("солнце").getLocalFrequency(),
        0.0001
    );
  }
  
  @Test
  public void checkSearchDictionaryEntryThatExistsInUserButNotInMainDictionary() {
    assertEquals("base frequency of the word 'как дела' should be equal to the frequency from userDictionary",
        dictionary.getMainDictionary().getEntry("как дела").getFrequency(),
        dictionary.getSearchDictionaryEntry("как дела").getFrequency(),
        0.0001
    );
    assertEquals("local frequency of the word 'как дела' should be equal to the frequency from userDictionary",
        dictionary.getUserPhrasesDictionary().getEntry("как дела").getFrequency(),
        dictionary.getSearchDictionaryEntry("как дела").getLocalFrequency(),
        0.0001
    );
  }
  
  @Test
  public void checkLastModificationTimeGetter() {
    FileTime[] fileTimes = this.dictionary.getDictionariesLastModifiedTimes();
    LocalDateTime timeOfMainDictionaryFile = LocalDateTime.ofInstant(fileTimes[0].toInstant(), ZoneOffset.UTC);
    LocalDateTime timeOfUserWordsFile = LocalDateTime.ofInstant(fileTimes[1].toInstant(), ZoneOffset.UTC);
    LocalDateTime timeOfUserPhrasesFile = LocalDateTime.ofInstant(fileTimes[2].toInstant(), ZoneOffset.UTC);
    int year = LocalDateTime.now().getYear();
    int month = LocalDateTime.now().getMonthValue();
    assertEquals("Year and month should be equal to current year and month", year, timeOfMainDictionaryFile.getYear());
    assertEquals("Year and month should be equal to current year and month", year, timeOfUserWordsFile.getYear());
    assertEquals("Year and month should be equal to current year and month", year, timeOfUserPhrasesFile.getYear());
    assertEquals("Year and month should be equal to current year and month", month, timeOfMainDictionaryFile.getMonthValue());
    assertEquals("Year and month should be equal to current year and month", month, timeOfUserWordsFile.getMonthValue());
    assertEquals("Year and month should be equal to current year and month", month, timeOfUserPhrasesFile.getMonthValue());
    this.dictionary.setDictionariesLastModifiedTimes(fileTimes);
    fileTimes = this.dictionary.getDictionariesLastModifiedTimes();
    timeOfMainDictionaryFile = LocalDateTime.ofInstant(fileTimes[0].toInstant(), ZoneOffset.UTC);
    timeOfUserWordsFile = LocalDateTime.ofInstant(fileTimes[1].toInstant(), ZoneOffset.UTC);
    timeOfUserPhrasesFile = LocalDateTime.ofInstant(fileTimes[2].toInstant(), ZoneOffset.UTC);
    year = LocalDateTime.now().getYear();
    month = LocalDateTime.now().getMonthValue();
    assertEquals("Year and month should be equal to current year and month", year, timeOfMainDictionaryFile.getYear());
    assertEquals("Year and month should be equal to current year and month", year, timeOfUserWordsFile.getYear());
    assertEquals("Year and month should be equal to current year and month", year, timeOfUserPhrasesFile.getYear());
    assertEquals("Year and month should be equal to current year and month", month, timeOfMainDictionaryFile.getMonthValue());
    assertEquals("Year and month should be equal to current year and month", month, timeOfUserWordsFile.getMonthValue());
    assertEquals("Year and month should be equal to current year and month", month, timeOfUserPhrasesFile.getMonthValue());
  }
  
  @Test
  public void checkSearchDictionaryEntryAddGetUpdateRemove() {
//    this.dictionary.addAllEntries()
    assertEquals("Size of the search dictionary before adding entry should be equal to 69311", 69311, dictionary.getSearchDictionaryEntries().size());
//    dictionary.addSearchDictionaryEntry("")
  }
  
  @Test
  public void checkDictionaryGetters() {
    Dictionary simpleDictionary = dictionary.getMainDictionary();
    assertNotNull("Main dictionary should be an instance of Dictionary class", simpleDictionary);
    simpleDictionary = dictionary.getUserWordsDictionary();
    assertNotNull("User words dictionary should be an instance of Dictionary class", simpleDictionary);
    simpleDictionary = dictionary.getUserPhrasesDictionary();
    assertNotNull("User phrases dictionary should be an instance of Dictionary class", simpleDictionary);
  }
  
  @Test
  public void checkCharSetGettersAndSetters() {
//  char[] chars = SearchDictionary.getCharSet();
    alphabet = new Alphabet(
        "абвгдежзийклмнопрстуфхцчшщъыьэюя"
        , "ru"
    );
    char[] outerAlphabetChars = this.alphabet.getChars();
//    Arrays.sort(innerAlphabetChars);
    Arrays.sort(outerAlphabetChars);
    
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
