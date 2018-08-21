package ru.itu.predictor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.itu.predictor.alphabet.AlphabetTests;
import ru.itu.predictor.entry.EntryTests;
import ru.itu.predictor.index.IndexNGramTests;
import ru.itu.predictor.registry.DictionaryTests;
import ru.itu.predictor.registry.SearchDictionaryTests;
import ru.itu.predictor.search.SearchTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AlphabetTests.class,
    EntryTests.class,
    DictionaryTests.class,
    SearchDictionaryTests.class,
    IndexNGramTests.class,
    SearchTests.class,
    PredictorTests.class
})
public class PredictorFullSuiteTest {

}
