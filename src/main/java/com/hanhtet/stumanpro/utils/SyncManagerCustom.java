package com.hanhtet.stumanpro.utils;

import java.util.Map;

public class SyncManagerCustom {

    private SyncManagerCustom(){
        throw new IllegalStateException("Sync Manager Custom class");
    }

    public static boolean AutoSyncData(){
        boolean isInternet = InternetConnectionChecker.isInternetAvailable();
        if (isInternet){
            try {
                Map<String, String> spreadsheetId = OnSheetWriter.readSpreadsheetInfoFromFile();
                OnSheetWriter.syncWithLocalSheet("lcfa_users",spreadsheetId.get("lcfa_users"), DATA.USER_TABLE_RANGE);
                OnSheetWriter.syncWithLocalSheet("lcfa_courses",spreadsheetId.get("lcfa_courses"), DATA.COURSE_TABLE_RANGE);
            } catch (Exception e) {
                System.err.println("Error at syncing offline");
            }
        }
        return isInternet;
    }

    public static boolean SyncFromOnline(){
        boolean isInternet = InternetConnectionChecker.isInternetAvailable();
        if (isInternet){
            try{
                Functions functions = new Functions();
                functions.downloadAll();
            } catch (Exception e){
                System.err.println("Error at syncing online");
            }
        }
        return isInternet;
    }

    public static boolean startAutoSync(){
        boolean syncData = AutoSyncData();
        if (syncData){
            System.out.println("Successfully synced!");
        } else{
            System.err.println("An error occurred while syncing courses from the sheet.");
        }
        return syncData;
    }
}
