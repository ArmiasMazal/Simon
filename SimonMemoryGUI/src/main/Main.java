package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/game-view.fxml"));
        Scene scene = new Scene(root, 500, 350);
        stage.setScene(scene);
        stage.setTitle("Simon Memory Game");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}