package ru.itu.predictools;

import org.junit.Test;
import ru.itu.predictools.index.IndexNGram;
import ru.itu.predictools.registry.SearchDictionary;

import java.io.IOException;

public class PredictorTest {
  private IndexNGram index;
  private SearchDictionary dictionary;
  
  @Test
  public void checkPredictorFromConfigFileInstantiating() throws IOException {
    Predictor predictor = new Predictor("predictor.conf");
  }
}
