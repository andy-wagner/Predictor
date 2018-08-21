package ru.itu.predictor;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashSet;

public class PredictorTests {
  Predictor predictor;
  
  @Before
  public void init() {
    predictor = new Predictor(
        System.getProperty("user.dir") + File.separator
            + "src" + File.separator
            + "test" + File.separator
            + "resources" + File.separator
            + "config" + File.separator
            + "predictor.conf"
    );
  }
  
  @Test
  public void checkCharsetsWithBitFlags() {
    predictor.addSpecialSymbolsSubset(new HashSet<>());
  }
}
