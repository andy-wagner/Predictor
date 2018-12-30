package predictor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import predictor.alphabet.AlphabetTests;
import predictor.entry.EntryTests;
import predictor.index.IndexNGramTests;
import predictor.registry.DictionaryTests;
import predictor.registry.SearchDictionaryTests;
import predictor.search.SearchTests;

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
