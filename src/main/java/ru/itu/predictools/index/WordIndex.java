package ru.itu.predictools.index;

import ru.itu.predictools.alphabet.Alphabet;
import ru.itu.predictools.registry.SearchDictionary;

@SuppressWarnings("WeakerAccess")
public abstract class WordIndex implements Index {
    protected final SearchDictionary searchDictionary;
    protected final Alphabet alphabet;

	WordIndex(SearchDictionary searchDictionary, Alphabet alphabet) { this.searchDictionary = searchDictionary; this.alphabet = alphabet; }

}
