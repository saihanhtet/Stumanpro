package com.hanhtet.stumanpro.utils;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hanhtet.stumanpro.entity.Course;
import com.hanhtet.stumanpro.entity.Login;
import com.hanhtet.stumanpro.entity.User;

public class Functions {
    private static final Logger LOGGER = Logger.getLogger(DATA.APPLICATION_NAME);
    
    public boolean RegisterUser(User user){
        try {
            List<Object> userData = user.getAllDataAsList();
            boolean result = addData(userData, DATA.USER_TABLE_ID, DATA.USER_TABLE_RANGE);
            if (result){
                System.out.println("Added USER: " + user.getName());
                return result;
            }
            else{
                System.err.println("Error occurred at adding USER.");
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while appending data to the sheet", e);
            System.err.println("An error occurred while appending data to the sheet.");
        }
        return false;
    }

    public boolean LoginUser(Login user){
        try {
            List<List<Object>> existingData = SheetUtils.readFromSheet(DATA.USER_TABLE_ID, DATA.USER_TABLE_RANGE);
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
    }

    public void LogoutUser(){
        UserSession userSession = UserSession.getInstance();
        userSession.logoutUser();
    }

    public Integer count_user(String userType){
        int count = 0;
        try{
            List<List<Object>> existingData = SheetUtils.readFromSheet(DATA.USER_TABLE_ID, DATA.USER_TABLE_RANGE);
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
    }

    public boolean addCourse(Course course){
        List<Object> courseData = course.getAllDataAsList();
        boolean result = addData(courseData, DATA.COURSE_TABLE_ID, DATA.COURSE_TABLE_RANGE);
        if (result){
            System.out.println("Added Course: " + course.getName());
        }
        else{
            System.err.println("Error occurred at adding course.");
        }
        return result;
    }

    public boolean addData(List<Object> newRow, String table_id, String table_range){
        List<List<Object>> existingData;
        try {
            existingData = SheetUtils.readFromSheet(table_id, table_range);
        } catch (IOException | GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while fetching existing data from the sheet", e);
            System.err.println("An error occurred while fetching existing data from the sheet.");
            return false;
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
        System.out.println(existingData +"|"+ newRow);
        try {
            SheetUtils.appendDataToSheet(newData, table_id, table_range);
            System.out.println("Data added successfully to the sheet!");
            return true;
        } catch (IOException | GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while appending data to the sheet", e);
            System.err.println("An error occurred while appending data to the sheet.");
            return false;
        }
    }

    public void playAudio(URL soundUrl) {
        if (soundUrl == null) {
            System.out.println("Sound file not found");
        } else {
            String uriString = soundUrl.toExternalForm();
            javafx.scene.media.Media sound = new javafx.scene.media.Media(uriString);
            javafx.scene.media.MediaPlayer mediaPlayer = new javafx.scene.media.MediaPlayer(sound);
            mediaPlayer.setOnReady(() -> mediaPlayer.play());
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.stop());   
            mediaPlayer.play();
        }
    }

    public void download_course(){
        SheetUtils.downloadFile("course",DATA.COURSE_TABLE_ID, DATA.COURSE_TABLE_RANGE);
    }

	public List<Course> getCoursesFromSheet() {
        List<Course> courses = new ArrayList<>();
        try {
            List<List<Object>> data = SheetUtils.readFromSheet(DATA.COURSE_TABLE_ID, DATA.COURSE_TABLE_RANGE);
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
        } catch (IOException | GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while fetching courses from the sheet", e);
            System.err.println("An error occurred while fetching courses from the sheet.");
        }
        return courses;
    }

    public boolean AutoSyncData(){
        try {
            SheetUtils.syncWithGoogleSheet(DATA.USER_TABLE_ID,DATA.USER_TABLE_RANGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while syncing courses from the sheet", e);
            System.err.println("An error occurred while syncing courses from the sheet.");
        }
        return true;
    }

    public void InitializeProject(){
        //create the sheets

        boolean sheetsExist = SetupUtils.checkIfGoogleSheetsExist();
        boolean localFilesExist = SetupUtils.checkIfLocalFilesExist();

        if (!sheetsExist || !localFilesExist) {
            String lcfa_users = SheetUtils.createGoogleSheet("lcfa_users");
            String lcfa_courses = SheetUtils.createGoogleSheet("lcfa_courses");

            Map<Integer, String> user_adjust_range = SheetUtils.adjustRange(DATA.USER_TABLE_RANGE);
            Map<Integer, String> course_adjust_range = SheetUtils.adjustRange(DATA.COURSE_TABLE_RANGE);
            Map<String, String> spreadsheetId = new HashMap<>();

            spreadsheetId.put("lcfa_users", lcfa_users);
            spreadsheetId.put("lcfa_courses", lcfa_courses);
            SheetUtils.writeSpreadsheetInfoToFile(spreadsheetId);

            List<Object> userHeaderData = new ArrayList<>(List.of(
                "id", "firstname", "lastname", "email", 
                "password", "phone_no", "picture", 
                "address", "role"
            ));

            List<Object> courseHeaderData = new ArrayList<>(List.of("id","name","price"));
            
            try {
                SheetUtils.headerAdd(userHeaderData,lcfa_users,user_adjust_range.get(1));
                SheetUtils.headerAdd(courseHeaderData,lcfa_courses,course_adjust_range.get(1));
            } catch (IOException e) {
                System.err.println("Error at adding header!");
            }

            SheetUtils.downloadFile("lcfa_users", lcfa_users, DATA.USER_TABLE_RANGE);
            SheetUtils.downloadFile("lcfa_courses", lcfa_courses, DATA.COURSE_TABLE_RANGE);
            SetupUtils.storeSetupCompletionFlag(true);

        } else{
            System.out.println("Google Sheets or local files already exist. Skipping setup.");
            Map<String, String> spreadsheetId = SheetUtils.readSpreadsheetInfoFromFile();

            System.out.println(spreadsheetId.get("lcfa_users"));
            System.out.println(spreadsheetId.get("lcfa_courses"));
        }
    }
}
