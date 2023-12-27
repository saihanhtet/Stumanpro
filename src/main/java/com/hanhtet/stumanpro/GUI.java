package com.hanhtet.stumanpro;

import com.hanhtet.stumanpro.utils.DATA;
import com.hanhtet.stumanpro.utils.LOG;
import com.hanhtet.stumanpro.utils.SyncManagerCustom;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GUI extends Application {

  @Override
  public void start(Stage primaryStage) {
    showSplashScreen(primaryStage);
    Runtime
      .getRuntime()
      .addShutdownHook(
        new Thread(() -> {
          try {
            LOG.logInfo("Auto Saving Mode!");
            SyncManagerCustom.startAutoSync();
            LOG.logInfo("Done saving bye!");
          } catch (Exception e) {
            LOG.logInfo("bye bye!");
          }
        })
      );
  }

  private void showSplashScreen(Stage primaryStage) {
    Stage splashStage = new Stage();
    splashStage.initStyle(StageStyle.UNDECORATED);

    FXMLLoader fxmlLoader = new FXMLLoader(
      getClass().getResource(DATA.SPLASH_FXML)
    );
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
      FXMLLoader fxmlLoader = new FXMLLoader(
        getClass().getResource(DATA.LOGIN_FXML)
      );
      Scene scene = new Scene(fxmlLoader.load(), 600, 400);
      primaryStage.setScene(scene);
      primaryStage.setTitle(DATA.APPLICATION_NAME);
      primaryStage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
