package com.hanhtet.stumanpro.utils;

public class DATA {
    public final static String SUCCESS_SOUND = "sound/success.mp3";
    public final static String WARNING_SOUND = "";
    public final static String USER_TABLE_RANGE = "lcfa_users!A2:I";
    public final static String COURSE_TABLE_RANGE = "lcfa_courses!A2:C";
    public final static String APPLICATION_NAME = "Light English Class For All";
    private final static String SOURCE_DIR = "/.stumanpro";
    public final static String TOKEN_PATH = SOURCE_DIR+"\\"+"tokens";
    public final static String FILE_PATH = SOURCE_DIR+"\\"+"tables";
    public static final String GOOGLE_SHEETSDIRECTORY = SOURCE_DIR+"\\"+"sheetId";
    public static final String TOKENS_DIRECTORY_PATH;
    public static final String GOOGLE_SHEETS_DIRECTORY;
    public static final String DOWNLOAD_XLXS_FOLDER_PATH;
    static {
        TOKENS_DIRECTORY_PATH = System.getProperty("user.home") + DATA.TOKEN_PATH;
        DOWNLOAD_XLXS_FOLDER_PATH = System.getProperty("user.home") + DATA.FILE_PATH;
        GOOGLE_SHEETS_DIRECTORY = System.getProperty("user.home") + DATA.GOOGLE_SHEETSDIRECTORY;
    }
}
