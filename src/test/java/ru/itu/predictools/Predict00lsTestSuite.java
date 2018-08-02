package ru.itu.predictools;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.itu.predictools.alphabet.AlphabetTests;
import ru.itu.predictools.entry.EntryTests;
import ru.itu.predictools.registry.DictionaryTests;
import ru.itu.predictools.utils.IntArraysTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AlphabetTests.class,
    EntryTests.class,
    IntArraysTest.class,
    DictionaryTests.class
})
public class Predict00lsTestSuite {

}
