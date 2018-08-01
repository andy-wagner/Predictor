package ru.itu.predictools.index;

import ru.itu.predictools.alphabet.Alphabet;
import ru.itu.predictools.registry.Dictionary;

public abstract class WordIndex implements Index {
    protected final Dictionary dictionary;
    protected final Alphabet alphabet;

	WordIndex(Dictionary dictionary, Alphabet alphabet) { this.dictionary = dictionary; this.alphabet = alphabet; }

}
