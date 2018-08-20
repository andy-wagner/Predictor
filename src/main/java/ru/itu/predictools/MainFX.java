package ru.itu.predictools;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import ru.itu.predictools.alphabet.Alphabet;
import ru.itu.predictools.gui.ConfirmBox;
import ru.itu.predictools.registry.Entry;
import ru.itu.predictools.registry.SearchDictionaryEntry;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainFX extends Application {
  private static final Logger LOGGER = LogManager.getLogger();
  private Stage window;
  
  public static void main(String... args) {
    LOGGER.info("Predictor demo/test app is starting...");
    launch(args);
  }
  
  @Override
  public void start(Stage primaryStage) {
    window = primaryStage;
    window.setOnCloseRequest(e -> {
      e.consume();
      closeWindow();
    });
    
    Scene scene = new Scene(fillGridPane(), 500, 450);
    
    window.setScene(scene);
    window.setTitle(System.getProperty("user.dir") + " Предиктивный ввод демо/тест");
    window.show();
    
  }
  
  private GridPane fillGridPane() {
//    Locale currentLocale = Locale.getDefault();
//    LOGGER.info("User language is {}", System.getProperty("user.language"));
    Predictor predictor = new Predictor(System.getProperty("user.dir") + File.separator
                                            + "config" + File.separator
                                            + "predictor.conf"
    );
    LOGGER.info("Current language is {}", predictor.getLanguage());
    GridPane gridPane = new GridPane();
    gridPane.setPadding(new Insets(10, 20, 30, 20));
    gridPane.setVgap(8);
    gridPane.setHgap(20);
    
    ComboBox comboBox = new ComboBox();
    
    
    Label labelOfEditor = new Label("Начните вводить текст...");
    
    TextArea textEditor = new TextArea();
    textEditor.setMinHeight(100);
    textEditor.setWrapText(true);
    
    Label labelOfPredictiveListLength = new Label("Длина списка подсказок: ");
    Label labelOfIndexName = new Label("Индекс: " + "NGram" + predictor.getSelectedSearch().getIndexN());
    Label labelOfReducedAlphabet = new Label("Алфавит выборки: ");
    Label labelOfNextSymbolAlphabet = new Label("Алфавит след.символа: ");
    
    ListView<String> predictiveText = new ListView<>();
  
    gridPane.add(comboBox, 3, 0, 1, 1);
    GridPane.setHalignment(comboBox, HPos.RIGHT);
    gridPane.add(labelOfEditor, 0, 1, 4, 1);
    gridPane.add(textEditor, 0, 2, 4, 1);
    gridPane.add(labelOfPredictiveListLength, 0, 3, 3, 1);
    gridPane.add(labelOfIndexName, 3, 3, 1, 1);
    gridPane.add(labelOfReducedAlphabet, 0, 4, 4, 1);
    gridPane.add(labelOfNextSymbolAlphabet, 0, 5, 4, 1);
    gridPane.add(predictiveText, 0, 6, 4, 1);
  
    //noinspection unchecked
    comboBox.getItems().addAll("en", "ru");
    //noinspection unchecked
    comboBox.getSelectionModel().select(predictor.getLanguage());
  
    comboBox.setOnAction(e->{//todo>> change keyboard layout language
      System.out.println(comboBox.getSelectionModel().getSelectedItem().toString());
      predictor.setLanguage(comboBox.getSelectionModel().getSelectedItem().toString());
    });
    
    predictiveText.setOnMouseClicked(e -> {
      String[] words = textEditor.getText().split(" ");//[\\s,.{}();\\[\\]\\n]
      String selectedItem = predictiveText.getSelectionModel().getSelectedItem();
      if (selectedItem != null) {
        words[words.length - 1] = selectedItem;
      }
      textEditor.setText(Arrays.stream(words).collect(Collectors.joining(" ")) + " ");
      textEditor.requestFocus();
      textEditor.end();
    });
    textEditor.textProperty().addListener(e -> {
      predictiveText.getItems().clear();
      String[] words = textEditor.getText().split(" ");//[\\s,.{}();\\[\\]\\n]
      String[] predictiveWords = new String[0];
      try {
        predictiveWords = predictor.search(words[words.length - 1].toLowerCase()).stream()
                              .sorted(Comparator
                                          .comparingInt(SearchDictionaryEntry::getDistance)
                                          .reversed()
                                          .thenComparingDouble(SearchDictionaryEntry::getLocalFrequency)
                                          .thenComparingDouble(SearchDictionaryEntry::getFrequency)
                                          .reversed()
                              )
                              //                                       .limit(15)
                              .map(Entry::getWord).toArray(String[]::new);
      } catch (IOException e1) {
        LOGGER.error(e1.getMessage());
        e1.printStackTrace();
      }
      labelOfPredictiveListLength.setText("Длина списка подсказок: " + predictiveWords.length);
      predictiveText.getItems().addAll(predictiveWords);
      Alphabet reducedAlphabet = predictor.getSelectedSearch().getReducedAlphabet();
      Alphabet nextSymbolAlphabet = predictor.getSelectedSearch().getNextSymbolAlphabet();
      char[] chars;
      if (reducedAlphabet != null) {
        chars = reducedAlphabet.getChars();
      } else {
        chars = new char[0];
      }
      Arrays.sort(chars);
      labelOfReducedAlphabet.setText("Алфавит выборки: " + Arrays.toString(chars)
                                                               .replace("[", "")
                                                               .replace(", ", "")
                                                               .replace("]", "")
                                                               .toUpperCase());
      if (nextSymbolAlphabet != null) {
        chars = nextSymbolAlphabet.getChars();
      } else {
        chars = new char[0];
      }
      Arrays.sort(chars);
      labelOfNextSymbolAlphabet.setText("Алфавит след.символа: " + Arrays.toString(chars)
                                                                       .replace("[", "")
                                                                       .replace(", ", "")
                                                                       .replace("]", "")
                                                                       .toUpperCase());
    });
    
    return gridPane;
  }
  
  private void closeWindow() {
//    if (ConfirmBox.display("Confirm closing please", "Are you really want to close the window?")) {
    if (ConfirmBox.display("Закрыть", "Действительно хотите закрыть?")) {
//      System.out.println("Data is saved and window is closed");
      window.close();
      LOGGER.info("Predictor demo/test app is closed...");
    }
  }
}
