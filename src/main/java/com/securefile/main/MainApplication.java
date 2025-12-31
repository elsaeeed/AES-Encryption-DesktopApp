package com.securefile.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("AES Encryption APP");
        stage.setScene(scene);

        stage.setResizable(false);

        Image icon = new Image(getClass().getResourceAsStream("/softlock_logo.jpg"));
        stage.getIcons().add(icon);

        // shutdown executor on application close
        MainController controller = fxmlLoader.getController();
        stage.setOnCloseRequest(event -> {
            controller.shutdown();
        });

        stage.show();
    }
}
