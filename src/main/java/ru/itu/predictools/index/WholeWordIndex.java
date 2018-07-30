package ru.itu.predictools.index;

import ru.itu.predictools.Alphabet.Alphabet;
import ru.itu.predictools.registry.Dictionary;

public abstract class WholeWordIndex extends WordIndex {//can be useful for future use

    WholeWordIndex(Dictionary dictionary, Alphabet alphabet) { super(dictionary, alphabet); }

}
