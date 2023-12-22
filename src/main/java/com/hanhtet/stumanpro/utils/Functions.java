package com.hanhtet.stumanpro.utils;

import com.hanhtet.stumanpro.entity.Course;
import com.hanhtet.stumanpro.entity.Login;
import com.hanhtet.stumanpro.entity.User;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Functions {
    private static final Logger LOGGER = Logger.getLogger(DATA.APPLICATION_NAME);
    private static Map<String, String> SPREADSHEET_ID;
    
    public boolean RegisterUser(User user, Boolean defaultPass){
        try {
            if (defaultPass) {
                user.setPassword("LightClassForAll");
            }
            List<Object> userData = user.getAllDataAsList();
            boolean result = addData(userData, SPREADSHEET_ID.get("lcfa_users"), DATA.USER_TABLE_RANGE, "lcfa_users");
            if (result){
                System.out.println("Added USER: " + user.getName());
            }
            else{
                System.err.println("Error occurred at adding USER.");
            }
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while appending data to the sheet", e);
            System.err.println("An error occurred while appending data to the sheet.");
        }
        return false;
    }

    public boolean LoginUser(Login user){
        if (InternetConnectionChecker.isInternetAvailable()){
            try {
                List<List<Object>> existingData = OnSheetWriter.readFromSheet(SPREADSHEET_ID.get("lcfa_users"), DATA.USER_TABLE_RANGE);
                if (existingData != null && !existingData.isEmpty()) {
                    for (List<Object> row : existingData) {
                        if (row.size() >= 3) {
                            String email = row.get(3).toString();
                            String passwordFromSheet = row.get(4).toString();
                            if (email.equals(user.getEmail()) && user.getPassword().equals(passwordFromSheet)) {
                                System.out.println("Login successful for email: " + email);
                                UserSession userSession = UserSession.getInstance();
                                userSession.loginUser(
                                        row.get(0).toString(),
                                        row.get(1).toString(),
                                        row.get(2).toString(),
                                        row.get(3).toString(),
                                        row.get(4).toString(),
                                        row.get(5).toString(),
                                        row.get(6).toString(),
                                        row.get(7).toString());
                                return true;
                            }
                        }
                    }
                }
                System.out.println("Login failed. Invalid email or password.");
                return false;
            } catch (IOException | GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        } else{
            System.err.println("Internet connection is not available!");
            return false;
        }
    }

    public void LogoutUser(){
        UserSession userSession = UserSession.getInstance();
        userSession.logoutUser();
    }

    public Integer count_user(String userType){
        int count = 0;
        if (InternetConnectionChecker.isInternetAvailable()){
            try{
                List<List<Object>> existingData = OnSheetWriter.readFromSheet(SPREADSHEET_ID.get("lcfa_users"), DATA.USER_TABLE_RANGE);
                if (existingData != null && !existingData.isEmpty()) {
                    for (List<Object> row : existingData) {
                        if (row.get(8).toString().equals(userType)){
                            count++;
                        }
                    }
                }
                return count;
            } catch (IOException | GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("Internet connection is not available!");
            return 0;
        }
    }

    public boolean addCourse(Course course){
        List<Object> courseData = course.getAllDataAsList();
        boolean result = addData(courseData, SPREADSHEET_ID.get("lcfa_courses"), DATA.COURSE_TABLE_RANGE,"lcfa_courses");
        if (result){
            System.out.println("Added Course: " + course.getName());
        }
        else{
            System.err.println("Error occurred at adding course.");
        }
        return result;
    }

    public boolean addData(List<Object> newRow, String table_id, String table_range, String filename){
        List<List<Object>> existingData;
        String filePath = DATA.DOWNLOAD_XLXS_FOLDER_PATH+"\\"+filename+".xlsx";
        if (InternetConnectionChecker.isInternetAvailable()){
            try {
                existingData = OnSheetWriter.readFromSheet(table_id, table_range);
            } catch (IOException | GeneralSecurityException e) {
                LOGGER.log(Level.SEVERE, "An error occurred while fetching existing data from the sheet", e);
                System.err.println("An error occurred while fetching existing data from the sheet.");
                return false;
            }
        } else{
            existingData = OffSheetWriter.readData(filePath);
        }

        if (existingData != null && !existingData.isEmpty()) {
            if (existingData.stream().anyMatch(row -> {
                List<Object> existingRowWithoutId = new ArrayList<>(row.subList(1, row.size()));
                return existingRowWithoutId.equals(newRow);
            })) {
                System.out.println("Data already exists. Not adding duplicate entry.");
                return false;
            }
        }
        List<List<Object>> newData = new ArrayList<>();
        newData.add(newRow);
        try {
            OffSheetWriter.appendDataToFile(newData, filePath);
            if (InternetConnectionChecker.isInternetAvailable()){
                OnSheetWriter.appendDataToSheet(newData, table_id, table_range);
            }else{
                System.out.println("No internet available to save the data on cloud!");
            }
            System.out.println("Data added successfully to the sheet!");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while appending data to the sheet", e);
            System.err.println("An error occurred while appending data to the sheet.");
            return false;
        }
    }

	public List<Course> getCoursesFromSheet() {
        List<Course> courses = new ArrayList<>();
        try {
            String courseFile = System.getProperty("user.home") + DATA.FILE_PATH + "\\" + "lcfa_courses.xlsx";
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
            LOGGER.log(Level.SEVERE, "An error occurred while fetching courses from the sheet", e);
            System.err.println("An error occurred while fetching courses from the sheet.");
        }
        return courses;
    }

    public void InitializeProject(){
        //create the sheets

        boolean sheetsExist = SetupUtils.checkIfGoogleSheetsExist();
        boolean localFilesExist = SetupUtils.checkIfLocalFilesExist();
        if (InternetConnectionChecker.isInternetAvailable()) {
            if (!sheetsExist || !localFilesExist) {
                String lcfa_users = OnSheetWriter.createGoogleSheet("lcfa_users");
                String lcfa_courses = OnSheetWriter.createGoogleSheet("lcfa_courses");

                Map<Integer, String> user_adjust_range = OnSheetWriter.adjustRange(DATA.USER_TABLE_RANGE);
                Map<Integer, String> course_adjust_range = OnSheetWriter.adjustRange(DATA.COURSE_TABLE_RANGE);
                Map<String, String> spreadsheetId = new HashMap<>();

                spreadsheetId.put("lcfa_users", lcfa_users);
                spreadsheetId.put("lcfa_courses", lcfa_courses);
                OnSheetWriter.writeSpreadsheetInfoToFile(spreadsheetId);
                SPREADSHEET_ID = OnSheetWriter.readSpreadsheetInfoFromFile();

                List<Object> userHeaderData = new ArrayList<>(List.of(
                        "id", "firstname", "lastname", "email",
                        "password", "phone_no", "picture",
                        "address", "role"
                ));

                List<Object> courseHeaderData = new ArrayList<>(List.of("id", "name", "price"));

                try {
                    OnSheetWriter.headerAdd(userHeaderData, lcfa_users, user_adjust_range.get(1));
                    OnSheetWriter.headerAdd(courseHeaderData, lcfa_courses, course_adjust_range.get(1));
                } catch (IOException e) {
                    System.err.println("Error at adding header!");
                }

                OnSheetWriter.downloadFile("lcfa_users", lcfa_users, DATA.USER_TABLE_RANGE);
                OnSheetWriter.downloadFile("lcfa_courses", lcfa_courses, DATA.COURSE_TABLE_RANGE);
                SetupUtils.storeSetupCompletionFlag(true);

                User default_user = new User("admin", "admin", "admin@gmail.com", "admin2023", "09999999", "null", "home", "admin");
                RegisterUser(default_user, false);
            } else {
                System.out.println("Google Sheets or local files already exist. Skipping setup.");
                SPREADSHEET_ID = OnSheetWriter.readSpreadsheetInfoFromFile();
            }
        }else{
            System.err.println("Please check your internet connection!");
        }
    }

    public void downloadAll(){
        if (InternetConnectionChecker.isInternetAvailable()) {
            OnSheetWriter.downloadFile("lcfa_users", SPREADSHEET_ID.get("lcfa_users"), DATA.USER_TABLE_RANGE);
            OnSheetWriter.downloadFile("lcfa_courses", SPREADSHEET_ID.get("lcfa_courses"), DATA.COURSE_TABLE_RANGE);
        } else{
            System.err.println("Please check your internet connection!");
        }
    }
    
}
