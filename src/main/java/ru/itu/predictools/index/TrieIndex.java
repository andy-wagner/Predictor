package ru.itu.predictools.index;

import ru.itu.predictools.alphabet.Alphabet;
import ru.itu.predictools.registry.SearchDictionary;

public abstract class TrieIndex extends WordIndex{//can be useful for future use

    TrieIndex(SearchDictionary searchDictionary, Alphabet alphabet) { super(searchDictionary, alphabet); }

}
