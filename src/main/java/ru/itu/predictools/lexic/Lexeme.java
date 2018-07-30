package ru.itu.predictools.lexic;

import java.util.List;

/**
 * Lexeme (lexic unit such as phrase) is a set of lexical chains (such as words) consists of links (such as fonems/letters/sounds/...)
 * @param <E> - type of sequence element
 * @param <L> - type of links (links is parts from which element of sequence is consists)
 */
public abstract class Lexeme<E, L> {
    List<E> chains;
    List<L> links;

}
