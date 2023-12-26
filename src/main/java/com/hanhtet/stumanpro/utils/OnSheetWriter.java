package com.hanhtet.stumanpro.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.FileSystems;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.logging.*;

public class OnSheetWriter {
    private OnSheetWriter(){
        throw new IllegalStateException("OnSheetWriter class");
    }

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String DOWNLOADFOLDER = getDownloadFolderPath();
    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE, SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final Sheets sheetService = getSheetsService();
    private static final Drive driveService = getDriveService();
    
    private static final Logger logger = Logger.getLogger(DATA.APPLICATION_NAME);
    
    public static Sheets getSheetService(){
        return sheetService;
    }
    public static void writeSpreadsheetInfoToFile(Map<String, String> spreadsheetInfo) {
        String filePath = DATA.GOOGLE_SHEETS_DIRECTORY + File.separator + "sheetId.txt";
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try (FileOutputStream fileOut = new FileOutputStream(filePath);
                 ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                objectOut.writeObject(spreadsheetInfo);
                logger.info("Spreadsheet info successfully written to file.");
            } catch (IOException e) {
                e.printStackTrace();
                logger.warning("Error occurred at writing the spreadsheet Id!");
            }
        }else{
            logger.info("Google sheet ID are already been added!");
        }
    }
    public static Map<String, String> readSpreadsheetInfoFromFile() {
        String filePath = DATA.GOOGLE_SHEETS_DIRECTORY + File.separator + "sheetId.txt";
        Map<String, String> spreadsheetInfo = new HashMap<>();
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            Object obj = objectIn.readObject();
            if (obj instanceof Map) {
                spreadsheetInfo = (Map<String, String>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.warning("Error occurred at reading the spreadsheet Id!");
        }
        return spreadsheetInfo;
    }
    private static Sheets getSheetsService() {

        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential gCredential = getCredentials(httpTransport,SCOPES);
            return new Sheets.Builder(httpTransport, JSON_FACTORY, gCredential)
                    .setApplicationName(DATA.APPLICATION_NAME)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static Drive getDriveService() {
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential gCredential = getCredentials(httpTransport,SCOPES);
            return new Drive.Builder(httpTransport, JSON_FACTORY,gCredential)
                    .setApplicationName(DATA.APPLICATION_NAME)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static Credential getCredentials(final NetHttpTransport httpTransport, List<String> scopes)
            throws IOException {
        InputStream in = SheetUtils.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        assert in != null;
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new File(DATA.TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    public static void headerAdd(List<Object> headerData, String spreadsheetId, String range) throws IOException{
        ValueRange body = new ValueRange().setValues(Collections.singletonList(headerData));
        sheetService.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
        logger.info("Header is successfully added!");
    }
    public static Map<Integer, String> adjustRange(String range) {
        Map<Integer, String> result = new HashMap<>();
        int exclamationIndex = range.indexOf("!");
        if (exclamationIndex != -1) {
            String sheetName = range.substring(0, exclamationIndex);
            String cellRange = range.substring(exclamationIndex + 1);
            int colonIndex = cellRange.indexOf(":");
            if (colonIndex != -1) {
                String endColumn = cellRange.substring(colonIndex + 1);
                result.put(0,sheetName);
                result.put(1,sheetName + "!A1:" + endColumn);
                return result;
            }
        }
        return result;
    }
    private static String getDownloadFolderPath() {
        return FileSystems.getDefault().getPath(System.getProperty("user.home"), "Downloads").toString();
    }
    public static void appendDataToSheet(@NotNull List<List<Object>> newData, String spreadsheetId, String range) throws IOException {
        List<List<Object>> existingData = readFromSheet(spreadsheetId, range);
        int lastRowIndex = (existingData != null && !existingData.isEmpty()) ? existingData.size() : 0;
        int newId = (lastRowIndex > 0) ? Integer.parseInt(existingData.get(lastRowIndex - 1).get(0).toString()) + 1 : 1;

        List<Object> newRow = new ArrayList<>();
        for (List<Object> dataRow: newData){
            newRow.add(Integer.toString(newId));
            newRow.addAll(dataRow);
        }

        String rangeForAppend = range.substring(0, range.indexOf('!') + 1) + "A" + (lastRowIndex + 2) + ":H";
        ValueRange body = new ValueRange().setValues(Collections.singletonList(newRow));
        try {
            sheetService.spreadsheets().values()
                    .append(spreadsheetId, rangeForAppend, body)
                    .setValueInputOption("RAW")
                    .execute();
            logger.log(Level.INFO, "Data successfully appended to the sheet with ID: {0}", newId);
        } catch (IOException e) {
            logger.log(Level.WARNING, "An error occurred while appending data to the sheet.");
        }
    }

    public static void deleteRowById(String idToDelete, String spreadsheetId, String range) throws IOException {
        List<List<Object>> data = readFromSheet(spreadsheetId, range);

        if (data != null && !data.isEmpty()) {
            int columnIndexToDelete = 0;
            int rowIndexToDelete = -1;
            for (int i = 0; i < data.size(); i++) {
                List<Object> row = data.get(i);
                if (row.get(columnIndexToDelete).toString().equals(idToDelete)) {
                    rowIndexToDelete = i;
                    break;
                }
            }
            if (rowIndexToDelete != -1) {
                data.remove(rowIndexToDelete);
                ValueRange body = new ValueRange().setValues(data);
                try {
                    sheetService.spreadsheets().values()
                            .update(spreadsheetId, range, body)
                            .setValueInputOption("RAW")
                            .execute();
                    logger.log(Level.INFO, "Row with ID {0} has been successfully deleted from the sheet.", idToDelete);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "An error occurred while deleting the row!");
                }
            } else {
                logger.log(Level.INFO,"ID {0} not found in the sheet.", idToDelete);
            }
        }
    }

    public static void updateDataInSheet(String idToUpdate, List<Object> newData, String spreadsheetId, String range) throws IOException {
        List<List<Object>> data = readFromSheet(spreadsheetId, range);
        if (data != null && !data.isEmpty()) {
            int columnIndexToUpdate = 0; // Change this according to your data structure
            int rowIndexToUpdate = -1;

            for (int i = 0; i < data.size(); i++) {
                List<Object> row = data.get(i);
                if (row.get(columnIndexToUpdate).toString().equals(idToUpdate)) {
                    rowIndexToUpdate = i;
                    break;
                }
            }
            if (rowIndexToUpdate != -1) {
                data.set(rowIndexToUpdate, newData);
                ValueRange body = new ValueRange().setValues(data);
                try {
                    sheetService.spreadsheets().values()
                            .update(spreadsheetId, range, body)
                            .setValueInputOption("RAW")
                            .execute();
                    logger.log(Level.INFO, "Data with ID {0} successfully updated in the sheet.", idToUpdate);
                } catch (IOException e) {
                    logger.warning("An error occurred while updating data:");
                }
            } else {
                logger.log(Level.INFO, "ID {0} not found in the sheet.", idToUpdate);
            }
        } else {
            logger.info("No data found in the sheet.");
        }
    }

    // read from online full and without header
    public static List<List<Object>> readFromSheet(String spreadsheetId, String range) throws IOException {
        ValueRange response = sheetService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        return response.getValues();
    }

    public static Map<Integer, Object> readFromSheetFull(String spreadsheetId, String range) throws IOException {
        Map<Integer, String> returnList = adjustRange(range);
        Map<Integer, Object> result = new HashMap<>();
        ValueRange response = sheetService.spreadsheets().values()
                .get(spreadsheetId, returnList.get(1))
                .execute();
        result.put(0, returnList.get(0));
        result.put(1, response.getValues());
        return result;
    }
    public static void downloadFile(String sheetName, String spreadsheetId, String range) {
        try {
            Map<Integer, Object> responseDataOld = SheetUtils.readFromSheetFull(spreadsheetId, range);
            Object responseData = responseDataOld.get(1);
            List<List<Object>> sheetData;

            if (responseData instanceof List<?> dataList) {
                if (!dataList.isEmpty() && dataList.get(0) instanceof List) {
                    sheetData = (List<List<Object>>) responseData;

                    String filePath = DATA.DOWNLOAD_XLXS_FOLDER_PATH + File.separator + sheetName + ".xlsx";
                    File file = new File(filePath);
                    if (!file.exists()) {
                        file.getParentFile().mkdirs(); // Create parent directories if they don't exist
                        OffSheetWriter.createSheet(sheetData, filePath, sheetName);
                        logger.info("Excel file created successfully!");
                    } else {
                        logger.log(Level.SEVERE,"File already exists at: {0}", filePath);
                    }
                } else {
                    logger.info("No data found in the sheet.");
                }
            } else {
                logger.info("Retrieved data is not in the expected format");
            }
        } catch (IOException | GeneralSecurityException e) {
            logger.warning("Error occurred while downloading data from the sheet.");
        }
    }

    public static String createGoogleSheet(String sheetName) {
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties().setTitle(sheetName)); // Set spreadsheet title

        try {
            spreadsheet = sheetService.spreadsheets().create(spreadsheet)
                    .setFields("spreadsheetId")
                    .execute();
        } catch (IOException e) {
            return null;
        }

        String spreadsheetId = spreadsheet.getSpreadsheetId();
        logger.log(Level.INFO,"Created new spreadsheet named: {0}", sheetName);

        try {
            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest();
            batchUpdateRequest.setRequests(Collections.singletonList(new Request()
                    .setUpdateSheetProperties(new UpdateSheetPropertiesRequest()
                            .setProperties(new SheetProperties().setTitle(sheetName))
                            .setFields("title"))));
            sheetService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
        } catch (IOException e) {
            logger.warning("Error updating sheet title: " + e.getMessage());
        }

        // Set the permission to allow anyone with the link to edit
        String permissionId = "anyoneWithLink";
        Permission newPermission = new Permission()
                .setType("anyone")
                .setRole("writer")
                .setId(permissionId);

        try {
            driveService.permissions().create(spreadsheetId, newPermission).execute();
        } catch (IOException e) {
            logger.warning("Can't add data in this create file instance!");
        }
        return spreadsheetId;
    }

    @SuppressWarnings("unchecked")
    public static void syncWithGoogleSheet(String tableId, String tableRange) {
        try {
            // Fetch data from Google Sheets
            Map<Integer, Object> resultData = fetchDataFromGoogleSheet(tableId, tableRange);
            List<List<Object>> newData = (List<List<Object>>) resultData.get(1);
            String filePath = DOWNLOADFOLDER + File.separator +"new_testing.xlsx";

            List<List<Object>> localData = OffSheetWriter.readData(filePath);
            if (!newData.equals(localData)) {
                updateLocalFile(newData,filePath); // Update local file with new data
                logger.info("Local file updated successfully.");
            } else {
                logger.info("Local file is already up-to-date.");
            }
        } catch (IOException | GeneralSecurityException e) {
            logger.warning("Error occurred while syncing: "+e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void syncWithLocalSheet(String sheetName,String tableId, String tableRange){
        try {
            Map<Integer, Object> resultData = fetchDataFromGoogleSheet(tableId, tableRange);
            List<List<Object>> newData = (List<List<Object>>) resultData.get(1);
            String filePath = DATA.DOWNLOAD_XLXS_FOLDER_PATH + File.separator +sheetName+".xlsx";
            List<List<Object>> localData = OffSheetWriter.readData(filePath);
            if (!newData.equals(localData)) {
                updateGoogleSheet(localData, tableId, tableRange);
                logger.info("Google Sheet file updated successfully.");
            } else {
                logger.info("Google Sheet file is already up-to-date.");
            }
        } catch (IOException | GeneralSecurityException e) {
            logger.warning("Error occurred while syncing:"+e);
        }
    }

    private static void updateGoogleSheet(List<List<Object>> newData, String spreadsheetId, String range) {
        try {
            ValueRange body = new ValueRange().setValues(newData);
            sheetService.spreadsheets().values()
                    .update(spreadsheetId, range, body)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (IOException e) {
            logger.warning("Error occurred while updating Google Sheet: "+e);
        }
    }

    private static void updateLocalFile(List<List<Object>> newData, String filePath) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1"); // Create a new sheet

        int rowNum = 0;
        for (List<Object> rowData : newData) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (Object field : rowData) {
                Cell cell = row.createCell(colNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
        }

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            logger.warning("File not found error while syncing: "+e);
        }

    }

    private static Map<Integer, Object> fetchDataFromGoogleSheet(String spreadsheetId, String range)
            throws IOException, GeneralSecurityException {
        return SheetUtils.readFromSheetFull(spreadsheetId, range);
    }
}
