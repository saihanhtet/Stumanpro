package com.hanhtet.stumanpro.utils;

import com.hanhtet.stumanpro.entity.Course;
import com.hanhtet.stumanpro.entity.Login;
import com.hanhtet.stumanpro.entity.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Functions {
    private static final Logger logger = Logger.getLogger(DATA.APPLICATION_NAME);
    private static Map<String, String> SPREADSHEETGROUP;
    
    public void addUserToSession(User user){
        UserSession userSession = UserSession.getInstance();
        userSession.loginUser(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getPhone_no(),
                user.getPicture(),
                user.getAddress(),
                user.getRole()
        );
    }

    public boolean loginOfflineUser(Login user, List<User> existingUsers) {
        for (User exUser : existingUsers) {
            logger.info(exUser.getEmail()+"|"+user.getEmail());
            if (exUser.getEmail().equals(user.getEmail()) && exUser.getPassword().equals(user.getPassword())) {
                logger.info("Login successful for email offline: " + user.getEmail());
                addUserToSession(exUser);
                return true;
            }
        }
        return false;
    }

    public boolean loginOnlineUser(Login user, List<List<Object>> existingData) {
        for (List<Object> row : existingData) {
            if (row.size() >= 8) { // Check if the row has enough data for a user
                User currentUser = getUserFromRow(row);
                if (currentUser.getEmail().equals(user.getEmail()) && currentUser.getPassword().equals(user.getPassword())) {
                    logger.info("Login successful for email: " + currentUser.getEmail());
                    addUserToSession(currentUser);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean registerUser(User user, Boolean defaultPass){
        try {
            if (Boolean.TRUE.equals(defaultPass)) {
                user.setPassword("LightClassForAll");
            }
            List<Object> userData = user.getAllDataAsList();
            boolean result = addData(userData, SPREADSHEETGROUP.get("lcfa_users"), DATA.USER_TABLE_RANGE, "lcfa_users");
            if (result){
                logger.info("Added USER: " + user.getName());
            }
            else{
                logger.warning("Error occurred at adding USER.");
            }
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while appending data to the sheet", e);
        }
        return false;
    }

    public boolean loginUser(Login user) {
        if (InternetConnectionChecker.isInternetAvailable()) {
            try {
                List<List<Object>> existingData = OnSheetWriter.readFromSheet(SPREADSHEETGROUP.get("lcfa_users"), DATA.USER_TABLE_RANGE);
                if (existingData != null && !existingData.isEmpty()) {
                    return loginOnlineUser(user, existingData);
                }
                return false;
            } catch (IOException e) {
                logger.log(Level.SEVERE, "An error occurred durring online login: ", e);
            }
        } else {
            logger.warning("Internet connection is not available!");
            try {
                List<User> existingUsers = getUsersFromSheet();
                if (existingUsers != null && !existingUsers.isEmpty()) {
                    return loginOfflineUser(user, existingUsers);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "An error occurred durring offline login: ", e);
            }
        }
        return false;
    }

    private User getUserFromRow(List<Object> row) {
        User user = new User(
                row.get(1).toString(),
                row.get(2).toString(),
                row.get(3).toString(),
                row.get(4).toString(),
                row.get(5).toString(),
                row.get(6).toString(),
                row.get(7).toString(),
                row.get(8).toString()
        );
        user.setId(row.get(0).toString()); // Assuming ID is at index 0
        user.setPasswordWithoutHash(row.get(4).toString());
        return user;
    }

    public void logoutUser(){
        UserSession userSession = UserSession.getInstance();
        userSession.logoutUser();
    }

    public Integer countUser(String userType) {
        int count = 0;
        try {
            if (InternetConnectionChecker.isInternetAvailable()) {
                List<List<Object>> existingData = OnSheetWriter.readFromSheet(SPREADSHEETGROUP.get("lcfa_users"), DATA.USER_TABLE_RANGE);
                if (existingData != null && !existingData.isEmpty()) {
                    for (List<Object> row : existingData) {
                        if (row.size() > 8 && row.get(8).toString().equals(userType)) {
                            count++;
                        }
                    }
                }
                return count;
            } else {
                logger.warning("Internet connection is not available!");
                return 0;
            }
        } catch (IOException e) {
            logger.warning("Error reading from the sheet: " + e.getMessage());
            return -1;
        }
    }


    public boolean addCourse(Course course){
        List<Object> courseData = course.getAllDataAsList();
        boolean result = addData(courseData, SPREADSHEETGROUP.get("lcfa_courses"), DATA.COURSE_TABLE_RANGE,"lcfa_courses");
        if (result){
            logger.info("Added Course: " + course.getName());
        }
        else{
            logger.warning("Error occurred at adding course.");
        }
        return result;
    }

    public boolean addData(List<Object> newRow, String tableID, String tableRange, String filename){
        List<List<Object>> existingData;
        String filePath = DATA.DOWNLOAD_XLXS_FOLDER_PATH + File.separator + filename+".xlsx";
        if (InternetConnectionChecker.isInternetAvailable()){
            try {
                existingData = OnSheetWriter.readFromSheet(tableID, tableRange);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "An error occurred while fetching existing data from the sheet", e);
                return false;
            }
        } else{
            existingData = OffSheetWriter.readData(filePath);
        }

        if (existingData != null && !existingData.isEmpty() &&  (existingData.stream().anyMatch(row -> {
                List<Object> existingRowWithoutId = new ArrayList<>(row.subList(1, row.size()));
                return existingRowWithoutId.equals(newRow);
            }))) {
                logger.info("Data already exists. Not adding duplicate entry.");
                return false;
            
        }
        List<List<Object>> newData = new ArrayList<>();
        newData.add(newRow);
        try {
            OffSheetWriter.appendDataToFile(newData, filePath);
            if (InternetConnectionChecker.isInternetAvailable()){
                OnSheetWriter.appendDataToSheet(newData, tableID, tableRange);
            }else{
                logger.info("No internet available to save the data on cloud!");
            }
            logger.info("Data added successfully to the sheet!");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while appending data to the sheet", e);
            return false;
        }
    }

	public List<User> getUsersFromSheet(){
        List<User> users = new ArrayList<>();
        try{
            String userFile = System.getProperty(DATA.HOME) + DATA.FILE_PATH + "\\" + "lcfa_users.xlsx";
            List<List<Object>> data = OffSheetWriter.readData(userFile);
            if (data != null && !data.isEmpty()){
                for (List<Object> row: data){
                    User user = getUserFromRow(row);
                    users.add(user);
                }
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "An error occurred while fetching users from the sheet", e);
        }
        return users;
    }
    public List<Course> getCoursesFromSheet() {
        List<Course> courses = new ArrayList<>();
        try {
            String courseFile = System.getProperty(DATA.HOME) + DATA.FILE_PATH + File.separator + "lcfa_courses.xlsx";
            List<List<Object>> data = OffSheetWriter.readData(courseFile);
            if (data != null && !data.isEmpty()) {
                for (List<Object> row : data) {
                    Course course = new Course(
                        row.get(0).toString(),
                        row.get(1).toString(),
                        row.get(2).toString()
                    );
                    courses.add(course);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while fetching courses from the sheet", e);
        }
        return courses;
    }

    public void InitializeProject(){
        //create the sheets
        boolean sheetsExist = SetupUtils.checkIfGoogleSheetsExist();
        boolean localFilesExist = SetupUtils.checkIfLocalFilesExist();
        if (InternetConnectionChecker.isInternetAvailable()) {
            if (!sheetsExist || !localFilesExist) {
                String lcfaUsers = OnSheetWriter.createGoogleSheet("lcfa_users");
                String lcfaCourses = OnSheetWriter.createGoogleSheet("lcfa_courses");

                Map<Integer, String> userAdjustRange = OnSheetWriter.adjustRange(DATA.USER_TABLE_RANGE);
                Map<Integer, String> courseAdjustRange = OnSheetWriter.adjustRange(DATA.COURSE_TABLE_RANGE);
                Map<String, String> spreadsheetId = new HashMap<>();

                spreadsheetId.put("lcfa_users", lcfaUsers);
                spreadsheetId.put("lcfa_courses", lcfaCourses);
                OnSheetWriter.writeSpreadsheetInfoToFile(spreadsheetId);
                SPREADSHEETGROUP = OnSheetWriter.readSpreadsheetInfoFromFile();

                List<Object> userHeaderData = new ArrayList<>(List.of(
                        "id", "firstname", "lastname", "email",
                        "password", "phone_no", "picture",
                        "address", "role"
                ));
                List<Object> courseHeaderData = new ArrayList<>(List.of("id", "name", "price"));
                try {
                    OnSheetWriter.headerAdd(userHeaderData, lcfaUsers, userAdjustRange.get(1));
                    OnSheetWriter.headerAdd(courseHeaderData, lcfaCourses, courseAdjustRange.get(1));
                } catch (IOException e) {
                    logger.warning("Error at adding header!");
                }
                OnSheetWriter.downloadFile("lcfa_users", lcfaUsers, DATA.USER_TABLE_RANGE);
                OnSheetWriter.downloadFile("lcfa_courses", lcfaCourses, DATA.COURSE_TABLE_RANGE);
                SetupUtils.storeSetupCompletionFlag(true);

                User defaultUser = new User("admin", "admin", "admin@gmail.com", "admin2023", "09999999", "null", "home", "admin");
                registerUser(defaultUser, false);
            } else {
                logger.info("Google Sheets or local files already exist. Skipping setup.");
                SPREADSHEETGROUP = OnSheetWriter.readSpreadsheetInfoFromFile();
            }
        }else{
            logger.warning("Please check your internet connection!");
        }
    }

    public void downloadAll(){
        if (InternetConnectionChecker.isInternetAvailable()) {
            OnSheetWriter.downloadFile("lcfa_users", SPREADSHEETGROUP.get("lcfa_users"), DATA.USER_TABLE_RANGE);
            OnSheetWriter.downloadFile("lcfa_courses", SPREADSHEETGROUP.get("lcfa_courses"), DATA.COURSE_TABLE_RANGE);
        } else{
            logger.warning("Please check your internet connection!");
        }
    }
    
}
