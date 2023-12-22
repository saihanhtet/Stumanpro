package com.hanhtet.stumanpro;
import java.net.URL;
import java.util.ResourceBundle;

import com.hanhtet.stumanpro.utils.DATA;

import com.hanhtet.stumanpro.utils.Functions;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;
public class SplashScreenController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label LogoName;

    @FXML
    private ProgressBar SplashProgress;

    private Runnable onInitializationComplete;

    public void setOnInitializationComplete(Runnable onInitializationComplete) {
        this.onInitializationComplete = onInitializationComplete;
    }

    @FXML
    void initialize() {
        assert LogoName != null : "fx:id=\"LogoName\" was not injected: check your FXML file 'SplashScreen.fxml'.";
        assert SplashProgress != null : "fx:id=\"SplashProgress\" was not injected: check your FXML file 'SplashScreen.fxml'.";

        LogoName.setText(DATA.APPLICATION_NAME);

        simulateInitialization();
    }

    private void simulateInitialization() {
        Functions functions = new Functions(); // Create an instance of your Functions class

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(SplashProgress.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(3), new KeyValue(SplashProgress.progressProperty(), 1))
        );

        timeline.setOnFinished(event -> {
            // Perform your initialization here
            functions.InitializeProject();

            // Call the method indicating initialization is complete
            if (onInitializationComplete != null) {
                onInitializationComplete.run();
            }
        });

        timeline.play();
    }
}
