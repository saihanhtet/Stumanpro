package com.hanhtet.stumanpro.utils;

import java.io.File;

public class DATA {

  private DATA() {
    throw new IllegalStateException("DATA class");
  }

  public static final String USER_TABLE_RANGE = "lcfa_users!A2:I";
  public static final String COURSE_TABLE_RANGE = "lcfa_courses!A2:C";
  public static final String APPLICATION_NAME = "Light English Class For All";
  private static final String SOURCE_DIR = "/.stumanpro";
  public static final String TOKEN_PATH =
    SOURCE_DIR + File.separator + "tokens";
  public static final String FILE_PATH = SOURCE_DIR + File.separator + "tables";
  public static final String GOOGLE_SHEETSDIRECTORY =
    SOURCE_DIR + File.separator + "sheetId";
  public static final String TOKENS_DIRECTORY_PATH;
  public static final String GOOGLE_SHEETS_DIRECTORY;
  public static final String DOWNLOAD_XLXS_FOLDER_PATH;
  public static final String HOME = "user.home";

  static {
    TOKENS_DIRECTORY_PATH = System.getProperty(DATA.HOME) + DATA.TOKEN_PATH;
    DOWNLOAD_XLXS_FOLDER_PATH = System.getProperty(DATA.HOME) + DATA.FILE_PATH;
    GOOGLE_SHEETS_DIRECTORY =
      System.getProperty(DATA.HOME) + DATA.GOOGLE_SHEETSDIRECTORY;
  }

  // templates
  public static final String MAINWINDOW_FXML = "MainWindow.fxml";
  public static final String LOGIN_FXML = "Login.fxml";
  public static final String SPLASH_FXML = "SplashScreen.fxml";
}
