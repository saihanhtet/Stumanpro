package com.hanhtet.stumanpro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import com.hanhtet.stumanpro.utils.DATA;
import com.hanhtet.stumanpro.utils.SyncManagerCustom;

public class GUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        showSplashScreen(primaryStage);
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
    private void showSplashScreen(Stage primaryStage) {
        Stage splashStage = new Stage();
        splashStage.initStyle(StageStyle.UNDECORATED);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(DATA.SPLASH_FXML));
        try {
            AnchorPane splashPane = fxmlLoader.load();
            Scene splashScene = new Scene(splashPane);
            splashStage.setScene(splashScene);
            splashStage.show();

            SplashScreenController controller = fxmlLoader.getController();
            controller.setOnInitializationComplete(() -> {
                splashStage.close();
                showMainGUI(primaryStage);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showMainGUI(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(DATA.LOGIN_FXML));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            primaryStage.setScene(scene);
            primaryStage.setTitle(DATA.APPLICATION_NAME);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}