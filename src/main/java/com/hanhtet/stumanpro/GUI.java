package com.hanhtet.stumanpro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.hanhtet.stumanpro.utils.DATA;
import com.hanhtet.stumanpro.utils.SyncManagerCustom;

public class GUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 360);
        stage.setTitle(DATA.APPLICATION_NAME);
        stage.setResizable(false);
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setMaxWidth(600);
        stage.setMaxHeight(400);
        stage.setScene(scene);
        stage.show();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Auto Saving Mode!");
                SyncManagerCustom.startAutoSync();
                System.out.println("Done saving bye!");
            } catch (Exception e) {
               System.out.println("bye bye!");
            }
        }));
    }
}
