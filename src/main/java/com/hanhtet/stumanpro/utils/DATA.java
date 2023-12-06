package com.hanhtet.stumanpro.utils;

public class DATA {
    public final static String SUCCESS_SOUND = "sound/success.mp3";
    public final static String WARNING_SOUND = "";
    public final static String USER_TABLE_ID = "1Z98ETn0eu6LwYJp4Xxv8UBaGwrTlJkeJ5N5OmYerqh4";
    public final static String USER_TABLE_RANGE = "lcfa_users!A2:I";
    public final static String COURSE_TABLE_ID = "113zUxWYSAafV11QAyoeozsD1YFUf4Jqt_Hfv4-vblsM";
    public final static String COURSE_TABLE_RANGE = "lcfa_courses!A2:C";
    public final static String APPLICATION_NAME = "Light English Class For All";
    public final static String TOKEN_PATH = "/.stumanpro/tokens";
    public final static String FILE_PATH = "/.stumanpro/tables";
    public static final String GOOGLE_SHEETSDIRECTORY = "/.stumanpro/sheetId";
    public static final String TOKENS_DIRECTORY_PATH;
    public static final String GOOGLE_SHEETS_DIRECTORY;
    public static final String DOWNLOAD_XLXS_FOLDER_PATH;
    static {
        TOKENS_DIRECTORY_PATH = System.getProperty("user.home") + DATA.TOKEN_PATH;
        DOWNLOAD_XLXS_FOLDER_PATH = System.getProperty("user.home") + DATA.FILE_PATH;
        GOOGLE_SHEETS_DIRECTORY = System.getProperty("user.home") + DATA.GOOGLE_SHEETSDIRECTORY;
    }
}
