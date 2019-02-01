package predictor.alphabet;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess"})
public class Alphabet {
  
  private static final Logger LOGGER = LogManager.getLogger();
  private Map<Character, Character> substitutes;
  
  public final String isoLanguageName;//ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
  public char[] chars;//alphabet symbols
  
  /**
   * A constructor of an alphabet instance, built from some set of Character objects, without substitutions
   *
   * @param symbolsSet - a set of Character objects the alphabet consists of
   * @param isoLanguageName - an ISO Language Name - ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
   */
  public Alphabet(Set<Character> symbolsSet, String isoLanguageName) {
    this(symbolsSet.stream().map(ch -> Character.toString(ch)).collect(Collectors.joining())
        , new HashMap<>()
        , isoLanguageName);
  }
  
  /**
   * A constructor of an alphabet instance, built from some set of Character objects, with substitutions table
   *
   * @param symbolsSet - a set of Character objects the alphabet consists of
   * @param substitutes - a characters substitutions map (interchangeable characters substitute by one "main" char)
   * @param isoLanguageName - an ISO Language Name - ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
   */
  public Alphabet(Set<Character> symbolsSet, Map<Character, Character> substitutes, String isoLanguageName) {
    this(symbolsSet.stream().map(ch -> Character.toString(ch)).collect(Collectors.joining())
        , substitutes
        , isoLanguageName);
  }
  
  /**
   * A constructor of an alphabet instance, built from some String value, representing characters of the alphabet,
   * without substitutions table
   *
   * @param alphabetString - a string representing characters of the alphabet instance
   * @param isoLanguageName - an ISO Language Name - ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
   */
  public Alphabet(String alphabetString, String isoLanguageName) {
    this(alphabetString, new HashMap<>(), isoLanguageName);
  }
  
  /**
   * Constructor of alphabet instance, built from String value, representing characters of the alphabet, with
   * substitutions table
   *
   * @param alphabetString - a string representing characters of the alphabet instance
   * @param substitutes - a characters substitutions map (interchangeable characters substitute by one "main" char)
   * @param isoLanguageName - an ISO Language Name - ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
   */
  public Alphabet(String alphabetString, Map<Character, Character> substitutes, String isoLanguageName) {
    this.chars = alphabetString.toLowerCase().toCharArray();
    this.isoLanguageName = isoLanguageName;
    this.substitutes = substitutes;
    LOGGER.debug("An alphabet instance has created -> {}", alphabetString.toUpperCase());
  }
  
  
  /**
   * Get alphabet for given set of strings
   *
   * @param strings - a set of strings, alphabet of which this method should return
   * @param isoLanguageName - an ISO Language Name - ISO 639-1 https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
   * @return Alphabet object instance for given set of strings of given ISO language name
   */
  public static Alphabet getAlphabet(Set<String> strings, String isoLanguageName) {
    Set<Character> characters = new HashSet<>();
    strings.forEach(str -> {
      for (char ch : str.toCharArray()) {
        characters.add(ch);
      }
    });
    return new Alphabet(characters, isoLanguageName);
  }
  
  public boolean hasChar(char ch) {
    return new String(chars).indexOf(ch) > -1;
  }
  
  public boolean hasChar(int index) {
    return index >= 0 && index < chars.length;
  }
  
  /**
   * If ch is not an alphabet symbol then method changes it to the alphabet symbol using default substitutes map
   * (assigned in the alphabet creation time)  or, if there is no appropriate substitutions, throws an error; if there
   * is no error the method returns an index of character ch
   *
   * @param ch - a char to be replaced with the char available in the alphabet's substitutions map
   * @return - index of character ch in the alphabet
   */
  public int mapChar(char ch) {
    return this.mapChar(ch, this.substitutes);
  }
  
  /**
   * If ch is not an alphabet symbol then method changes it to the alphabet symbol using substitutes map passed with the
   * second parameter of this method or, if there is no appropriate substitutions, throws an error;
   * if there is no error the method returns an index of character ch
   *
   * @param ch - a char to be replaced with the char available in the alphabet's substitutions map
   * @param substitutes - map of substitutes <"the input character" : "the substitute alphabet character"> available for the given char
   * @return - index of character ch in the alphabet
   */
  public int mapChar(char ch, Map<Character, Character> substitutes) {
    if (!hasChar(ch)) {
      try {
        ch = substitutes.get(ch);
        return new String(chars).indexOf(ch);
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
        throw new RuntimeException("Error: The char '" + ch + "' is not from the alphabet and there is no substitutes for it.");
      }
    }
    return new String(chars).indexOf(ch);
  }
  
  /**
   * Number of the alphabet characters
   *
   * @return - an Integer that represents number of letters of the alphabet instance
   */
  public Integer size() {
    return chars.length;
  }
  
}
