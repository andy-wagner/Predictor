package ru.itu.predictools;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.itu.predictools.alphabet.AlphabetTests;
import ru.itu.predictools.entry.EntryTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AlphabetTests.class,
    EntryTests.class
})
public class Predict00lsTestSuite {

}
