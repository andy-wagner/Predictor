package ru.itu.predictools.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class ConfirmBox {
  
  private static boolean answer;
  
  @SuppressWarnings("SameParameterValue")
  public static boolean display(String title, String message) {
    Stage window = new Stage();
    
    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle(title);
    window.setWidth(280);
    window.setHeight(150);
    
    GridPane gridPane = new GridPane();
    gridPane.setPadding(new Insets(10, 20, 10, 20));
    gridPane.setVgap(8);
    gridPane.setHgap(20);
    
    Label label = new Label(message);
    
//    Button buttonYes = new Button("Yes");
//    Button buttonNo = new Button("No");
    Button buttonYes = new Button("Да");
    Button buttonNo = new Button("Нет");
    buttonNo.setMinWidth(100);
    buttonYes.setMinWidth(100);
    buttonNo.setMinHeight(30);
    buttonYes.setMinHeight(30);
    
    buttonYes.setOnAction(e -> {
      answer = true;
      window.close();
    });
    buttonNo.setOnAction(e -> {
      answer = false;
      window.close();
    });
    
    buttonYes.requestFocus();
    buttonYes.defaultButtonProperty().bind(buttonYes.focusedProperty());
    buttonNo.setCancelButton(true);
    buttonNo.defaultButtonProperty().bind(buttonNo.focusedProperty());
    
    gridPane.add(label,     0, 1, 2, 1);
    gridPane.add(buttonYes, 0, 4, 1, 1);
    gridPane.add(buttonNo,  1, 4, 1, 1);
    
    Scene scene = new Scene(gridPane, 250, 150);
    window.setScene(scene);
    window.showAndWait();
    return answer;
  }
}
