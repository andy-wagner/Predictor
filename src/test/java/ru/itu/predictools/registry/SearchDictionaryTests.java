package ru.itu.predictools.registry;

import org.junit.Before;
import org.junit.Test;
import ru.itu.predictools.alphabet.Alphabet;

import java.io.IOException;
import java.util.Set;
import java.io.File;

public class SearchDictionaryTests {
  private SearchDictionary dictionary;
  private Alphabet alphabet;
  private String MAIN_DICTIONARY_PATH, USER_WORDS_DICTIONARY_PATH, USER_PHRASES_DICTIONARY_PATH;
  
  @Before
  public void init() {
    alphabet = new Alphabet("абвгдеёжзийклмнопрстуфхцчшщъыьэюя", "ru");
    MAIN_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                               + "dictionaries" + File.separator + "ru-main-v1-utf8.dic";
    USER_WORDS_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                                     + "dictionaries" + File.separator + "ru-user-words-v1-utf8.dic";
    USER_PHRASES_DICTIONARY_PATH = System.getProperty("user.dir") + File.separator
                                       + "dictionaries" + File.separator + "ru-user-phrases-v1-utf8.dic";
  }
  
  @Test
  public void checkSearchDictionaryInstantiationWithoutAlphabet() throws IOException {
    dictionary = new SearchDictionary(
        MAIN_DICTIONARY_PATH,
        USER_WORDS_DICTIONARY_PATH,
        USER_PHRASES_DICTIONARY_PATH
    );
  }
  
  @Test
  public void checkSearchDictionaryInstantiationWithAlphabet() throws IOException {
    dictionary = new SearchDictionary(
        MAIN_DICTIONARY_PATH,
        USER_WORDS_DICTIONARY_PATH,
        USER_PHRASES_DICTIONARY_PATH,
        alphabet
    );
  }
  
}
