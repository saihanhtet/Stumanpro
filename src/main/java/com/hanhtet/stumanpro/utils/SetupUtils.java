package com.hanhtet.stumanpro.utils;

import java.io.File;
import java.io.IOException;

public class SetupUtils {
    
    private SetupUtils(){
        throw new IllegalStateException("Setup Utils class");
    }
    
    private static final String GOOGLE_SHEETS_DIRECTORY;
    private static final String LOCAL_FILES_DIRECTORY;
    private static final String SETUP_FLAG_FILE;

    static {
        SETUP_FLAG_FILE = System.getProperty("user.home") + "/.stumanpro/setup_flag.txt";
        GOOGLE_SHEETS_DIRECTORY = System.getProperty("user.home") + "/.stumanpro/sheetId";
        LOCAL_FILES_DIRECTORY = System.getProperty("user.home") + "/.stumanpro/tables";
    }

    public static boolean checkIfGoogleSheetsExist() {
        File googleSheetsDirectory = new File(GOOGLE_SHEETS_DIRECTORY);
        return googleSheetsDirectory.exists() && googleSheetsDirectory.isDirectory() && googleSheetsDirectory.listFiles().length > 0;
    }

    public static boolean checkIfLocalFilesExist() {
        File localFilesDirectory = new File(LOCAL_FILES_DIRECTORY);
        return localFilesDirectory.exists() && localFilesDirectory.isDirectory() && localFilesDirectory.listFiles().length > 0;
    }

   public static void storeSetupCompletionFlag(boolean flag) {
    File flagFile = new File(SETUP_FLAG_FILE);

    if (flag) {
        try {
            if (flagFile.createNewFile()) {
                System.out.println("Setup flag file created successfully.");
            } else {
                System.out.println("Setup flag file already exists.");
            }
        } catch (IOException e) {
            System.err.println("Error creating the setup flag file: " + e.getMessage());
        }
    } else {
        if (flagFile.exists()) {
            if (flagFile.delete()) {
                System.out.println("Setup flag file deleted successfully.");
            } else {
                System.out.println("Unable to delete the setup flag file.");
            }
        } else {
            System.out.println("Setup flag file doesn't exist.");
        }
    }
}


    
}
