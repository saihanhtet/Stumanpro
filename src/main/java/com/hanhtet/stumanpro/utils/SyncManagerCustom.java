package com.hanhtet.stumanpro.utils;

import java.util.Map;

public class SyncManagerCustom {

  private SyncManagerCustom() {
    throw new IllegalStateException("Sync Manager Custom class");
  }

  public static boolean autoSyncData() {
    boolean isInternet = InternetConnectionChecker.isInternetAvailable();
    if (isInternet) {
      try {
        Map<String, String> spreadsheetId = OnSheetWriter.readSpreadsheetInfoFromFile();
        OnSheetWriter.syncWithLocalSheet(
          "lcfa_users",
          spreadsheetId.get("lcfa_users"),
          DATA.USER_TABLE_RANGE
        );
        OnSheetWriter.syncWithLocalSheet(
          "lcfa_courses",
          spreadsheetId.get("lcfa_courses"),
          DATA.COURSE_TABLE_RANGE
        );
      } catch (Exception e) {
        LOG.logWarn("Error at syncing offline");
      }
    }
    return isInternet;
  }

  public static boolean syncFromOnline() {
    boolean isInternet = InternetConnectionChecker.isInternetAvailable();
    if (isInternet) {
      try {
        Functions functions = new Functions();
        functions.downloadAll();
      } catch (Exception e) {
        LOG.logWarn("Error at syncing online");
      }
    }
    return isInternet;
  }

  public static boolean startAutoSync() {
    boolean syncData = autoSyncData();
    if (syncData) {
      LOG.logInfo("Successfully synced!");
    } else {
      LOG.logWarn("An error occurred while syncing courses from the sheet.");
    }
    return syncData;
  }
}
