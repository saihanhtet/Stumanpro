package com.hanhtet.stumanpro.utils;

import java.util.Map;

public class SyncManagerCustom {

    public static boolean AutoSyncData(){
        boolean isInternet = InternetConnectionChecker.isInternetAvailable();
        if (isInternet){
            try {
                Map<String, String> spreadsheetId = SheetUtils.readSpreadsheetInfoFromFile();
                SheetUtils.syncWithLocalSheet("lcfa_users",spreadsheetId.get("lcfa_users"), DATA.USER_TABLE_RANGE);
                SheetUtils.syncWithLocalSheet("lcfa_courses",spreadsheetId.get("lcfa_courses"), DATA.COURSE_TABLE_RANGE);
                return true;
            } catch (Exception e) {
                return false;
            }
        }else{
            return false;
        }
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
