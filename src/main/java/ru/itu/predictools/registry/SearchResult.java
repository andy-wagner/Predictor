package ru.itu.predictools.registry;

import ru.itu.predictools.Alphabet.Alphabet;

@SuppressWarnings("unused")
class SearchResult {
  private Entry[] entries;
  private Alphabet reducedAlphabet;
  
  public Alphabet getReducedAlphabet() {
    return reducedAlphabet;
  }
  
  public Boolean setReducedAlphabet(Alphabet alphabet) {
    try{
      this.reducedAlphabet = alphabet;
      return true;
    }
    catch(IllegalArgumentException e){
      return false;
    }
  }
  
  public Entry[] getEntries() {
    return entries;
  }
  
  public Boolean setEntries(Entry[] entries){
    try{
      this.entries = entries;
      return true;
    }
    catch(IllegalArgumentException e){
      return false;
    }
  }
  
}
