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
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SheetUtils {

  private static final Logger LOGGER = Logger.getLogger(DATA.APPLICATION_NAME);
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  private static final String downloadFolder = getDownloadFolderPath();

  private static final List<String> SCOPES = Arrays.asList(
    DriveScopes.DRIVE,
    SheetsScopes.SPREADSHEETS
  );

  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
  private static final Sheets sheetService = getSheetsService();
  private static final Drive driveService = getDriveService();

  public static Sheets getSheetService() {
    return sheetService;
  }

  public static void writeSpreadsheetInfoToFile(
    Map<String, String> spreadsheetInfo
  ) {
    String filePath = DATA.GOOGLE_SHEETS_DIRECTORY + "\\" + "sheetId.txt";
    File file = new File(filePath);
    if (!file.exists()) {
      file.getParentFile().mkdirs();
      try (
        FileOutputStream fileOut = new FileOutputStream(filePath);
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)
      ) {
        objectOut.writeObject(spreadsheetInfo);
        System.out.println("Spreadsheet info successfully written to file.");
      } catch (IOException e) {
        e.printStackTrace();
        System.err.println("Error occurred at writing the spreadsheet Id!");
      }
    } else {
      System.out.println("Google sheet Id are already added!");
    }
  }

  @SuppressWarnings("unchecked")
  public static Map<String, String> readSpreadsheetInfoFromFile() {
    String filePath = DATA.GOOGLE_SHEETS_DIRECTORY + "\\" + "sheetId.txt";
    Map<String, String> spreadsheetInfo = new HashMap<>();
    try (
      FileInputStream fileIn = new FileInputStream(filePath);
      ObjectInputStream objectIn = new ObjectInputStream(fileIn)
    ) {
      Object obj = objectIn.readObject();
      if (obj instanceof Map) {
        spreadsheetInfo = (Map<String, String>) obj;
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      System.err.println("Error occurred at reading the spreadsheet Id!");
    }
    return spreadsheetInfo;
  }

  // create sheet sheetService for request
  private static Sheets getSheetsService() {
    NetHttpTransport HTTP_TRANSPORT = null;
    Credential gCredential = null;
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      gCredential = getCredentials(HTTP_TRANSPORT, SCOPES);
    } catch (GeneralSecurityException | IOException e) {
      e.printStackTrace();
    }
    return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, gCredential)
      .setApplicationName(DATA.APPLICATION_NAME)
      .build();
  }

  private static Drive getDriveService() {
    NetHttpTransport HTTP_TRANSPORT = null;
    Credential gCredential = null;
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      gCredential = getCredentials(HTTP_TRANSPORT, SCOPES);
    } catch (GeneralSecurityException | IOException e) {
      e.printStackTrace();
    }
    return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, gCredential)
      .setApplicationName(DATA.APPLICATION_NAME)
      .build();
  }

  private static Credential getCredentials(
    final NetHttpTransport HTTP_TRANSPORT,
    List<String> scopes
  ) throws IOException {
    InputStream in =
      SheetUtils.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
      JSON_FACTORY,
      new InputStreamReader(in)
    );
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
      HTTP_TRANSPORT,
      JSON_FACTORY,
      clientSecrets,
      scopes
    )
      .setDataStoreFactory(
        new FileDataStoreFactory(new java.io.File(DATA.TOKENS_DIRECTORY_PATH))
      )
      .setAccessType("offline")
      .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder()
      .setPort(8888)
      .build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  public static void headerAdd(
    List<Object> headerData,
    String spreadsheetId,
    String range
  ) throws IOException {
    try {
      ValueRange body = new ValueRange()
        .setValues(Collections.singletonList(headerData));
      sheetService
        .spreadsheets()
        .values()
        .append(spreadsheetId, range, body)
        .setValueInputOption("RAW")
        .execute();
      System.out.println("Header is successfully added!");
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  public static void createExcelFile(
    List<List<Object>> data,
    String filePath,
    String sheetName
  ) throws IOException {
    Workbook workbook = new XSSFWorkbook();
    org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet(sheetName);

    int rowNum = 0;
    for (List<Object> rowData : data) {
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

    try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
      workbook.write(outputStream);
    }
    workbook.close();
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
        result.put(0, sheetName);
        result.put(1, sheetName + "!A1:" + endColumn);
        return result;
      }
    }
    return result;
  }

  public static void main(String... args) throws GeneralSecurityException {
    // List<List<Object>> newData = new ArrayList<>();
    // List<Object> newRow = new ArrayList<>();

    // newRow.add("hanhtetsan");
    // newRow.add("hanhtetsan13@gmail.com");
    // newRow.add("P@ssw0rd2006");
    // newRow.add("9400925900");
    // newRow.add("null");
    // newRow.add("home");
    // newRow.add("admin");
    // newData.add(newRow);

    // String spreadsheetId = "1Ww4VDQGcszWx_dROEC85E-qCvJ7m5-LL8s9UXS-2S_M";
    // String range = "lcfa!A2:H";

    // try {
    //     SheetUtils.appendDataToSheet(newData, spreadsheetId, range);
    // } catch (IOException | GeneralSecurityException e) {
    //     LOGGER.log(Level.SEVERE, "An error occurred while appending data to the sheet", e);
    //     System.err.println("An error occurred while appending data to the sheet.");
    // }

    // List<Object> newRow2 = new ArrayList<>();
    // newRow2.add("1");
    // newRow2.add("saihanhtet");
    // newRow2.add("sai.hanhtetsan@gmail.com");
    // newRow2.add("P@ssw0rd2006");
    // newRow2.add("9400925900");
    // newRow2.add("null");
    // newRow2.add("home");
    // newRow2.add("student");
    //updateDataInSheet("1", newRow2, spreadsheetId, range);

    //deleteRowById("3", spreadsheetId, range);
    //createGoogleSheet("new_testing","new");

    // String spreadsheetId = "1Ww4VDQGcszWx_dROEC85E-qCvJ7m5-LL8s9UXS-2S_M";
    // String range = "Sheet1!A:H";
    // try {
    //     SheetUtils.appendDataToSheet(newData, DATA.USER_TABLE_ID, DATA.USER_TABLE_RANGE);
    // } catch (IOException | GeneralSecurityException e) {
    //     LOGGER.log(Level.SEVERE, "An error occurred while appending data to the sheet", e);
    //     System.err.println("An error occurred while appending data to the sheet.");
    // }
    //downloadFile("new_testing", DATA.USER_TABLE_ID, DATA.USER_TABLE_RANGE);
    //syncWithLocalSheet("new_testing",DATA.USER_TABLE_ID, DATA.USER_TABLE_RANGE);

    List<Object> newData = new ArrayList<>();
    newData.add(1);
    newData.add("O level");
    newData.add(2993);

    //editDataInLocalFile("1", newData, DATA.DOWNLOAD_XLXS_FOLDER_PATH +"\\"+"lcfa_courses.xlsx");
    deleteRowById(
      "2",
      DATA.DOWNLOAD_XLXS_FOLDER_PATH + "\\" + "lcfa_courses.xlsx"
    );
  }

  private static String getDownloadFolderPath() {
    return FileSystems
      .getDefault()
      .getPath(System.getProperty("user.home"), "Downloads")
      .toString();
  }

  // add / delete/ update data to online sheet
  public static void appendDataToSheet(
    @org.jetbrains.annotations.NotNull List<List<Object>> newData,
    String spreadsheetId,
    String range
  ) throws IOException, GeneralSecurityException {
    List<List<Object>> existingData = readFromSheet(spreadsheetId, range);
    int lastRowIndex = (existingData != null && !existingData.isEmpty())
      ? existingData.size()
      : 0;
    int newId = (lastRowIndex > 0)
      ? Integer.parseInt(existingData.get(lastRowIndex - 1).get(0).toString()) +
      1
      : 1;

    List<Object> newRow = new ArrayList<>();
    for (List<Object> dataRow : newData) {
      newRow.add(Integer.toString(newId));
      for (Object data : dataRow) {
        newRow.add(data);
      }
    }

    String rangeForAppend =
      range.substring(0, range.indexOf('!') + 1) +
      "A" +
      (lastRowIndex + 2) +
      ":H";
    ValueRange body = new ValueRange()
      .setValues(Collections.singletonList(newRow));
    try {
      sheetService
        .spreadsheets()
        .values()
        .append(spreadsheetId, rangeForAppend, body)
        .setValueInputOption("RAW")
        .execute();
      System.out.println(
        "Data successfully appended to the sheet with ID: " + newId
      );
    } catch (IOException e) {
      LOGGER.log(
        Level.SEVERE,
        "An error occurred while appending data to the sheet",
        e
      );
      System.err.println(
        "An error occurred while appending data to the sheet."
      );
    }
  }

  public static void deleteRowById(
    String idToDelete,
    String spreadsheetId,
    String range
  ) throws IOException, GeneralSecurityException {
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
          sheetService
            .spreadsheets()
            .values()
            .update(spreadsheetId, range, body)
            .setValueInputOption("RAW")
            .execute();
          System.out.println(
            "Row with ID " +
            idToDelete +
            " successfully deleted from the sheet."
          );
        } catch (IOException e) {
          System.err.println("An error occurred while deleting the row:");
          LOGGER.log(
            Level.SEVERE,
            "An error occurred while deleting the row:",
            e
          );
        }
      } else {
        System.out.println("ID " + idToDelete + " not found in the sheet.");
      }
    } else {
      System.out.println("No data found in the sheet.");
    }
  }

  public static void updateDataInSheet(
    String idToUpdate,
    List<Object> newData,
    String spreadsheetId,
    String range
  ) throws IOException, GeneralSecurityException {
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
          sheetService
            .spreadsheets()
            .values()
            .update(spreadsheetId, range, body)
            .setValueInputOption("RAW")
            .execute();
          System.out.println(
            "Data with ID " + idToUpdate + " successfully updated in the sheet."
          );
        } catch (IOException e) {
          LOGGER.log(
            Level.SEVERE,
            "An error occurred while updating data:e sheet",
            e
          );
          System.err.println("An error occurred while updating data:");
        }
      } else {
        System.out.println("ID " + idToUpdate + " not found in the sheet.");
      }
    } else {
      System.out.println("No data found in the sheet.");
    }
  }

  // read from online full and without header
  public static List<List<Object>> readFromSheet(
    String spreadsheetId,
    String range
  ) throws IOException, GeneralSecurityException {
    ValueRange response = sheetService
      .spreadsheets()
      .values()
      .get(spreadsheetId, range)
      .execute();
    return response.getValues();
  }

  public static Map<Integer, Object> readFromSheetFull(
    String spreadsheetId,
    String range
  ) throws IOException, GeneralSecurityException {
    Map<Integer, String> return_list = adjustRange(range);
    Map<Integer, Object> result = new HashMap<>();
    ValueRange response = sheetService
      .spreadsheets()
      .values()
      .get(spreadsheetId, (String) return_list.get(1))
      .execute();
    result.put(0, return_list.get(0));
    result.put(1, response.getValues());
    return result;
  }

  @SuppressWarnings("unchecked")
  public static void downloadFile(
    String sheetName,
    String spreadsheetId,
    String range
  ) {
    try {
      Map<Integer, Object> response_data = SheetUtils.readFromSheetFull(
        spreadsheetId,
        range
      );
      Object responseData = response_data.get(1);
      List<List<Object>> sheetData;

      if (responseData instanceof List) {
        List<?> dataList = (List<?>) responseData;
        if (!dataList.isEmpty() && dataList.get(0) instanceof List) {
          sheetData = (List<List<Object>>) responseData;

          String filePath =
            DATA.DOWNLOAD_XLXS_FOLDER_PATH + "\\" + sheetName + ".xlsx";
          File file = new File(filePath);
          if (!file.exists()) {
            file.getParentFile().mkdirs(); // Create parent directories if they don't exist
            createExcelFile(sheetData, filePath, sheetName);
            System.out.println("Excel file created successfully!");
          } else {
            System.out.println("File already exists at: " + filePath);
          }
        } else {
          System.out.println("No data found in the sheet.");
        }
      } else {
        System.out.println("Retrieved data is not in the expected format");
      }
    } catch (IOException | GeneralSecurityException e) {
      LOGGER.log(
        Level.SEVERE,
        "Error occurred while downloading data from the sheet.",
        e
      );
      System.err.println(
        "Error occurred while downloading data from the sheet."
      );
    }
  }

  public static String createGoogleSheet(String sheetName) {
    Spreadsheet spreadsheet = new Spreadsheet()
      .setProperties(new SpreadsheetProperties().setTitle(sheetName)); // Set spreadsheet title

    try {
      spreadsheet =
        sheetService
          .spreadsheets()
          .create(spreadsheet)
          .setFields("spreadsheetId")
          .execute();
    } catch (IOException e) {
      return null;
    }

    String spreadsheetId = spreadsheet.getSpreadsheetId();
    System.out.println("Created new spreadsheet named: " + sheetName);

    try {
      BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest();
      batchUpdateRequest.setRequests(
        Collections.singletonList(
          new Request()
            .setUpdateSheetProperties(
              new UpdateSheetPropertiesRequest()
                .setProperties(new SheetProperties().setTitle(sheetName))
                .setFields("title")
            )
        )
      );
      sheetService
        .spreadsheets()
        .batchUpdate(spreadsheetId, batchUpdateRequest)
        .execute();
    } catch (IOException e) {
      System.err.println("Error updating sheet title: " + e.getMessage());
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
      System.err.println("Can't add data in this create file instance!");
    }
    return spreadsheetId;
  }

  @SuppressWarnings("unchecked")
  public static void syncWithGoogleSheet(String tableId, String tableRange) {
    try {
      // Fetch data from Google Sheets
      Map<Integer, Object> resultData = fetchDataFromGoogleSheet(
        tableId,
        tableRange
      );
      List<List<Object>> newData = (List<List<Object>>) resultData.get(1);
      String filePath = downloadFolder + "\\" + "new_testing.xlsx";

      List<List<Object>> localData = readLocalFile(filePath);
      if (!newData.equals(localData)) {
        updateLocalFile(newData, filePath); // Update local file with new data
        System.out.println("Local file updated successfully.");
      } else {
        System.out.println("Local file is already up-to-date.");
      }
    } catch (IOException | GeneralSecurityException e) {
      LOGGER.log(Level.SEVERE, "Error occurred while syncing", e);
    }
  }

  @SuppressWarnings("unchecked")
  public static void syncWithLocalSheet(
    String sheetName,
    String tableId,
    String tableRange
  ) {
    try {
      Map<Integer, Object> resultData = fetchDataFromGoogleSheet(
        tableId,
        tableRange
      );
      List<List<Object>> newData = (List<List<Object>>) resultData.get(1);
      String filePath =
        DATA.DOWNLOAD_XLXS_FOLDER_PATH + "\\" + sheetName + ".xlsx";

      List<List<Object>> localData = readLocalFile(filePath);
      if (!newData.equals(localData)) {
        updateGoogleSheet(localData, tableId, tableRange);
        System.out.println("Google Sheet file updated successfully.");
      } else {
        System.out.println("Google Sheet file is already up-to-date.");
      }
    } catch (IOException | GeneralSecurityException e) {
      LOGGER.log(Level.SEVERE, "Error occurred while syncing", e);
    }
  }

  private static void updateGoogleSheet(
    List<List<Object>> newData,
    String spreadsheetId,
    String range
  ) {
    try {
      ValueRange body = new ValueRange().setValues(newData);
      sheetService
        .spreadsheets()
        .values()
        .update(spreadsheetId, range, body)
        .setValueInputOption("RAW")
        .execute();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error occurred while updating Google Sheet", e);
    }
  }

  private static void updateLocalFile(
    List<List<Object>> newData,
    String filePath
  ) {
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
      LOGGER.log(Level.SEVERE, "File not found error while syncing", e);
    }
  }

  private static Map<Integer, Object> fetchDataFromGoogleSheet(
    String spreadsheetId,
    String range
  ) throws IOException, GeneralSecurityException {
    return SheetUtils.readFromSheetFull(spreadsheetId, range);
  }

  // read add update delete local file
  public static List<List<Object>> readLocalFile(String filePath) {
    try {
      FileInputStream fileInputStream = new FileInputStream(filePath);
      Workbook workbook = new XSSFWorkbook(fileInputStream);
      Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet
      List<List<Object>> data = SheetUtils.convertSheetToList(sheet);
      data.remove(0);
      workbook.close();
      fileInputStream.close();
      return data;
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "File not found error while syncing", e);
    }
    return null;
  }

  public static void appendDataToLocalFile(
    List<List<Object>> newData,
    String filePath
  ) {
    try {
      FileInputStream fileInputStream = new FileInputStream(filePath);
      Workbook workbook = new XSSFWorkbook(fileInputStream);
      Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet

      int lastRowNum = sheet.getLastRowNum();
      int lastID = 0;

      for (int i = lastRowNum; i >= 0; i--) {
        Row row = sheet.getRow(i);
        if (row != null) {
          Cell cell = row.getCell(0);
          if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            double cellValue = cell.getNumericCellValue();
            int intValue = (int) cellValue;
            lastID = intValue;
            break;
          }
        }
      }

      System.out.println("Last ID number: " + lastID);
      for (List<Object> rowData : newData) {
        Row row = sheet.createRow(++lastID);
        int colCount = 0;
        List<Object> newRowData = new ArrayList<>();
        newRowData.add(lastID);
        for (Object data : rowData) {
          newRowData.add(data);
        }
        System.out.println(newRowData);
        for (Object field : newRowData) {
          Cell cell = row.createCell(colCount++);
          if (field instanceof String) {
            cell.setCellValue((String) field);
          } else if (field instanceof Integer) {
            cell.setCellValue((Integer) field);
          } else if (field instanceof Double) {
            cell.setCellValue((Double) field);
          }
        }
      }

      FileOutputStream outputStream = new FileOutputStream(filePath);
      workbook.write(outputStream);
      workbook.close();
      outputStream.close();
      System.out.println("Data successfully appended to the local file.");
    } catch (IOException e) {
      System.err.println(
        "Error occurred while appending data to the local file: " +
        e.getMessage()
      );
    }
  }

  public static void deleteRowById(String id, String filePath) {
    try {
      FileInputStream fileInputStream = new FileInputStream(filePath);
      Workbook workbook = new XSSFWorkbook(fileInputStream);
      Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet

      int columnIndexToDelete = 0; // Assuming the identifier is in the first column
      boolean flag = false;
      for (int i = sheet.getLastRowNum(); i >= 0; i--) {
        Row row = sheet.getRow(i);
        if (row != null) {
          Cell cell = row.getCell(columnIndexToDelete);
          if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            double cellValue = cell.getNumericCellValue();
            int intValue = (int) cellValue;
            if (intValue == Integer.parseInt(id)) {
              System.out.println("Found matching cell: " + cellValue);
              sheet.removeRow(row);
              flag = true;
              break;
            }
          }
        }
      }

      fileInputStream.close();
      if (flag) {
        FileOutputStream outputStream = new FileOutputStream(filePath);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
        System.out.println(
          "Row with id " + id + " deleted from the local file."
        );
      } else {
        System.out.println(
          "Row with id " + id + " not found in the local file."
        );
      }
    } catch (IOException e) {
      System.err.println(
        "Error occurred while deleting row from the local file: " +
        e.getMessage()
      );
    }
  }

  public static void editDataInLocalFile(
    String id,
    List<Object> newData,
    String filePath
  ) {
    try {
      FileInputStream fileInputStream = new FileInputStream(filePath);
      Workbook workbook = new XSSFWorkbook(fileInputStream);
      Sheet sheet = workbook.getSheetAt(0);
      int columnIndexToUpdate = 0;
      for (Row row : sheet) {
        Cell cell = row.getCell(columnIndexToUpdate);
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
          double cellValue = cell.getNumericCellValue();
          int intValue = (int) cellValue;
          if (intValue == Integer.parseInt(id)) {
            System.out.println("Found matching cell: " + cellValue);
            int colNum = 0;
            for (Object field : newData) {
              Cell existingCell = row.getCell(colNum);
              if (existingCell == null) {
                existingCell = row.createCell(colNum);
              }
              if (field instanceof String) {
                existingCell.setCellValue((String) field);
              } else if (field instanceof Integer) {
                existingCell.setCellValue((Integer) field);
              } else if (field instanceof Double) {
                existingCell.setCellValue((Double) field);
              }
              colNum++;
            }
            break;
          }
        }
      }

      fileInputStream.close(); // Close the input stream before writing to the file

      // Update the existing FileOutputStream to overwrite the file
      FileOutputStream outputStream = new FileOutputStream(filePath);
      workbook.write(outputStream);
      outputStream.flush();
      outputStream.close();
      workbook.close(); // Close the workbook after writing the changes

      System.out.println("Data successfully updated in the local file.");
    } catch (IOException e) {
      System.err.println(
        "Error occurred while updating data in the local file: " +
        e.getMessage()
      );
    }
  }

  public static List<List<Object>> convertSheetToList(Sheet sheet) {
    List<List<Object>> data = new ArrayList<>();

    Iterator<Row> rowIterator = sheet.iterator();
    while (rowIterator.hasNext()) {
      Row row = rowIterator.next();
      Iterator<Cell> cellIterator = row.cellIterator();

      List<Object> rowData = new ArrayList<>();
      while (cellIterator.hasNext()) {
        Cell cell = cellIterator.next();
        switch (cell.getCellType()) {
          case STRING:
            rowData.add(cell.getStringCellValue());
            break;
          case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
              rowData.add(cell.getDateCellValue());
            } else {
              if (
                cell.getNumericCellValue() == (int) cell.getNumericCellValue()
              ) {
                rowData.add((int) cell.getNumericCellValue());
              } else {
                rowData.add(cell.getNumericCellValue());
              }
            }
            break;
          case BOOLEAN:
            rowData.add(cell.getBooleanCellValue());
            break;
          case BLANK:
            rowData.add(""); // For blank cells
            break;
          default:
            rowData.add(""); // For other cell types
        }
      }
      data.add(rowData);
    }
    return data;
  }
}
