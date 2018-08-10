package ru.itu.predictools;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import ru.itu.predictools.gui.ConfirmBox;
import ru.itu.predictools.registry.Entry;
import ru.itu.predictools.registry.SearchDictionaryEntry;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class MainFX extends Application {
  
  private Stage window;
  
  public static void main(String... args) {
    launch(args);
  }
  
  @Override
  public void start(Stage primaryStage) throws IOException {
    window = primaryStage;
    window.setOnCloseRequest(e -> {
      e.consume();
      closeWindow();
    });
    
    Scene scene = new Scene(fillGridPane(), 500, 350);
    
    window.setScene(scene);
    window.setTitle("Предиктивный ввод демо/тест");
    window.show();
    
  }
  
  private GridPane fillGridPane() throws IOException {
//    AtomicBoolean internalEditorTextChange = new AtomicBoolean(false);//todo>> to decide if this optimisation is needed use VisualVM
    Predictor predictor = new Predictor("predictor.conf");
    
    GridPane gridPane = new GridPane();
    gridPane.setPadding(new Insets(10, 20, 30, 20));
    gridPane.setVgap(8);
    gridPane.setHgap(20);
    
    Label labelOfEditor = new Label("Начните вводить текст...");
    
    TextArea textEditor = new TextArea();
    textEditor.setMinHeight(100);
    textEditor.setWrapText(true);
    
    Label labelOfPredictiveListLength = new Label("Длина списка подсказок: ");
    Label labelOfIndexName = new Label("Индекс: " + "NGram" + predictor.getIndexN());
    
    ListView<String> predictiveText = new ListView<>();
    
    gridPane.add(labelOfEditor,
        0, 1, 4, 1);
    gridPane.add(textEditor,
        0, 2, 4, 1);
    gridPane.add(labelOfPredictiveListLength,
        0, 3, 3, 1);
    gridPane.add(labelOfIndexName,
        3, 3, 1, 1);
    gridPane.add(predictiveText,
        0, 4, 4, 1);
    predictiveText.setOnMouseClicked(e -> {
      String[] words = textEditor.getText().split(" ");
      words[words.length - 1] = predictiveText.getSelectionModel().getSelectedItem();
//      internalEditorTextChange.set(true);//todo>> to decide if this optimisation is needed use VisualVM
      textEditor.setText(Arrays.stream(words).collect(Collectors.joining(" ")) + " ");
      textEditor.requestFocus();
      textEditor.end();
//      internalEditorTextChange.set(false);//todo>> to decide if this optimisation is needed use VisualVM
    });
    textEditor.textProperty().addListener(e -> {
//      if (internalEditorTextChange.get()) {//todo>> to decide if this optimisation is needed use VisualVM
//        return;
//      }
      predictiveText.getItems().clear();
      String[] words = textEditor.getText().split(" ");
//      String lastWord = words[words.length - 1];//todo>> to decide if this optimisation is needed use VisualVM
//      if (lastWord.length() > 1) {//todo>> to decide if this optimisation is needed use VisualVM
        String[] predictiveWords = predictor.search(words[words.length - 1]).stream()
                                       .sorted(Comparator
                                                   .comparingInt(SearchDictionaryEntry::getDistance)
                                                   .reversed()
                                                   .thenComparingDouble(SearchDictionaryEntry::getLocalFrequency)
                                                   .thenComparingDouble(SearchDictionaryEntry::getFrequency)
                                                   .reversed()
                                       )
//                                       .limit(15)
                                       .map(Entry::getWord).toArray(String[]::new);
        labelOfPredictiveListLength.setText("Длина списка подсказок: " + predictiveWords.length);
        predictiveText.getItems().addAll(predictiveWords);
//      }
    });
    
    return gridPane;
  }
  
  private void closeWindow() {
//    if (ConfirmBox.display("Confirm closing please", "Are you really want to close the window?")) {
    if (ConfirmBox.display("Закрыть", "Действительно хотите закрыть?")) {
      System.out.println("Data is saved and window is closed");
      window.close();
    }
  }
}
