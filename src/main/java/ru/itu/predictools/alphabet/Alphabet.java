package ru.itu.predictools.alphabet;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Alphabet {
  private String isoLanguageName;//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
  private char[] chars;//alphabet symbols
  private Map<Character, Character> substitutes;
  
  protected static final long serialVersionUID = 1L;//todo>> consider serialization
  
  public Alphabet(String alphabetString, String isoLanguageName) {
    this(alphabetString, new Hashtable<>(), isoLanguageName);
  }
  
  public Alphabet(Set<Character> symbolsSet, String isoLanguageName) {
    this(symbolsSet.stream().map(ch -> Character.toString(ch)).collect(Collectors.joining())
        , new Hashtable<>()
        , isoLanguageName);
  }
  
  public Alphabet(Set<Character> symbolsSet, Map<Character, Character> substitutes, String isoLanguageName) {
    this(symbolsSet.stream().map(ch -> Character.toString(ch)).collect(Collectors.joining())
        , substitutes
        , isoLanguageName);
  }
  
  public Alphabet(String alphabetString, Map<Character, Character> substitutes, String isoLanguageName) {
    this.chars = alphabetString.toLowerCase().toCharArray();
    this.isoLanguageName = isoLanguageName;
    this.substitutes = substitutes;
  }
  
  /**
   * Get ISO Language Name - ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
   *
   * @return - string that represent language name according to ISO 639-1
   */
  public String getIsoLanguageName() {
    return this.isoLanguageName;
  }
  
  /**
   * Set ISO Language Name - ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
   *
   * @param language - ISO name string
   */
  public void setIsoLanguageName(String language) {
    this.isoLanguageName = language;
  }
  
  public boolean isAlphabetChar(char ch) {
    return new String(chars).indexOf(ch) > -1;
  }
  
  public boolean isAlphabetChar(int index) {
    return index >= 0 && index < chars.length;
  }
  
  /**
   * If ch is not alphabet symbol then method throws an error;
   * if there is no error the method returns an index of character ch
   *
   * @param ch - char to be replaced with available in the alphabet
   * @return - returns index of character ch in the alphabet
   */
  public int mapChar(char ch) {
    return this.mapChar(ch, this.substitutes);
  }
  
  /**
   * If ch is not alphabet symbol then method changes it to alphabet symbol using substitutes map or, if there is no
   * appropriate substitute, throws an error; if there is no error the method returns an index of character ch
   *
   * @param ch          - char to be replaced with available in the alphabet
   * @param substitutes - map of substitutes <"the input character" : "the substitute alphabet character"> available for the given char
   * @return - returns index of character ch in the alphabet
   */
  public int mapChar(char ch, Map<Character, Character> substitutes) {
    if (!isAlphabetChar(ch)) {
      try {
        ch = substitutes.get(ch);
        return new String(chars).indexOf(ch);
      } catch (NullPointerException e) {
        throw new RuntimeException("Error: The char '" + ch + "' is not from the alphabet and there is no substitutes for it.");
      }
    }
    return new String(chars).indexOf(ch);
  }
  
  public char[] getChars() {
    return chars;
  }
  
  public int size() {
    return chars.length;
  }
  
}
