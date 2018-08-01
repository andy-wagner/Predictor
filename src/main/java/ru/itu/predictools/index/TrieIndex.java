package ru.itu.predictools.index;

import ru.itu.predictools.alphabet.Alphabet;
import ru.itu.predictools.registry.Dictionary;

public abstract class TrieIndex extends WordIndex{//can be useful for future use

    TrieIndex(Dictionary dictionary, Alphabet alphabet) { super(dictionary, alphabet); }

}
