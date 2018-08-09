package ru.itu.predictools;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.itu.predictools.alphabet.AlphabetTests;
import ru.itu.predictools.entry.EntryTests;
import ru.itu.predictools.index.IndexNGramTests;
import ru.itu.predictools.registry.DictionaryTests;
import ru.itu.predictools.registry.SearchDictionaryTests;
import ru.itu.predictools.utils.IntArraysTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AlphabetTests.class,
    EntryTests.class,
    IntArraysTest.class,
    DictionaryTests.class,
    SearchDictionaryTests.class,
    IndexNGramTests.class
})
public class Predict00lsTestSuite {

}
