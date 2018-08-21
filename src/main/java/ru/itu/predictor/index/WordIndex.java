package ru.itu.predictor.index;

import ru.itu.predictor.alphabet.Alphabet;
import ru.itu.predictor.registry.SearchDictionary;

@SuppressWarnings("WeakerAccess")
public abstract class WordIndex implements Index {
    protected final SearchDictionary searchDictionary;
    protected final Alphabet alphabet;

	WordIndex(SearchDictionary searchDictionary, Alphabet alphabet) { this.searchDictionary = searchDictionary; this.alphabet = alphabet; }

}
