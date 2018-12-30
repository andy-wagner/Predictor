package predictor.alphabet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static org.junit.Assert.*;

public class AlphabetTests {
  private Alphabet alphabet;
  private char[] chars;
  private Hashtable<Character, Character> substitutes = new Hashtable<>();
  
  @Before
  public void init(){
    chars = new char[]{'a', 'b', 'c', 'd', 'e'};
    
    alphabet = new Alphabet(new String(chars), "en");//ok
    
    substitutes.put('Ё', 'е');
    substitutes.put('ъ', '\'');
    substitutes.put('ь', '\'');
  }
  
  @Test
  public void testAlphabetInstantiatingFromStringRepresentation() {
    alphabet = new Alphabet(new String(chars), "en");//ok
    assertArrayEquals("alphabet getChars should be equal to {'a', 'b', 'c', 'd', 'e'}", chars, alphabet.getChars());
    assertEquals("Input isoLanguageName and alphabet.getIsoLanguageName should be equal", "en", alphabet.getIsoLanguageName());
  }
  
  @Test
  public void testAlphabetInstantiatingFromSetOfCharactersRepresentation() {
    Set<Character> characters = new HashSet<>();
    for (char c : chars) {
      characters.add(c);
    }
    alphabet = new Alphabet(characters, "en");//ok
    assertArrayEquals("alphabet getChars should be equal to {'a', 'b', 'c', 'd', 'e'}", chars, alphabet.getChars());
    assertEquals("Input isoLanguageName and alphabet.getIsoLanguageName should be equal", "en", alphabet.getIsoLanguageName());
  }
  
  @Test
  public void isoLanguageNameSetAndGetCheck(){
    alphabet.setIsoLanguageName("ru");
    assertEquals("Should be equal to 'ru' after assignment with setIsoLanguageName", "ru", alphabet.getIsoLanguageName());
    alphabet.setIsoLanguageName("en");
    assertEquals("Should be equal to 'ru' after assignment with setIsoLanguageName", "en", alphabet.getIsoLanguageName());
  }
  
  @Test
  public void isAlphabetCharCheck(){
    assertTrue("isAlphabetChar should return true for symbol 'c' and alphabet 'abcde'", alphabet.isAlphabetChar('c'));
    assertFalse("isAlphabetChar should return false for symbol 'z' and alphabet 'abcde'", alphabet.isAlphabetChar('z'));
    assertTrue("isAlphabetChar should return true for symbol with index 0 and alphabet 'abcde'", alphabet.isAlphabetChar(0));
    assertTrue("isAlphabetChar should return true for symbol with index 4 and alphabet 'abcde'", alphabet.isAlphabetChar(4));
    assertFalse("isAlphabetChar should return false for symbol with index -1 and alphabet 'abcde'", alphabet.isAlphabetChar(-1));
    assertFalse("isAlphabetChar should return false for symbol with index 5 and alphabet 'abcde'", alphabet.isAlphabetChar(5));
  }
  
  @Test//(expected = RuntimeException.class)
  public void mapCharCheck(){
    assertEquals("Index of 'a' character of 'abcde' alphabet should be equal to 0", 0, alphabet.mapChar('a'));
    assertEquals("Index of 'e' character of 'abcde' alphabet should be equal to 4", 4, alphabet.mapChar('e'));
    alphabet = new Alphabet("абвгде'", "ru");
    assertEquals("Character 'Ё' should be replaced to 'е' from 'абвгде' alphabet, and its index should be equal to 5", 5, alphabet.mapChar('Ё', substitutes));
    assertEquals("Character 'ъ' should be replaced to '\'' from 'абвгде' alphabet, and its index should be equal to 6", 6, alphabet.mapChar('ъ', substitutes));
    assertEquals("Character 'ь' should be replaced to '\'' from 'абвгде' alphabet, and its index should be equal to 6", 6, alphabet.mapChar('ь', substitutes));
  }
  
  @Rule
  public ExpectedException thrownFromMapCharWithoutSubstitutions = ExpectedException.none();
  @Test//(expected = RuntimeException.class)
  public void mapCharWithoutSubstitutionsErrorCheck(){
    thrownFromMapCharWithoutSubstitutions.expect(RuntimeException.class);
    thrownFromMapCharWithoutSubstitutions.expectMessage("Error: The char 'Ё' is not from the alphabet and there is no substitutes for it.");
    alphabet.mapChar('Ё');
  }
  
  @Rule
  public ExpectedException thrownFromMapCharWithSubstitutions = ExpectedException.none();
  @Test//(expected = RuntimeException.class)
  public void mapCharWithSubstitutionsErrorCheck(){
    thrownFromMapCharWithSubstitutions.expect(RuntimeException.class);
    thrownFromMapCharWithSubstitutions.expectMessage("Error: The char 'Й' is not from the alphabet and there is no substitutes for it.");
    alphabet.mapChar('Й', substitutes);
  }
  
  @Test
  public void charsCheck(){
    alphabet = new Alphabet(new String(chars), "en");//ok
    assertArrayEquals("getChars array should be equal to 'abcde'", chars, alphabet.getChars());
  }
  
  @Test
  public void sizeCheck(){
    alphabet = new Alphabet(new String(chars), "en");//ok
    assertEquals("alphabet 'abcde' size should be equal to 5", 5, alphabet.size());
  }
}


