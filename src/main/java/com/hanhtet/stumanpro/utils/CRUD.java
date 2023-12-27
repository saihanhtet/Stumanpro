package com.hanhtet.stumanpro.utils;

import com.hanhtet.stumanpro.entity.Course;
import com.hanhtet.stumanpro.entity.User;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

public class CRUD<T> {

  private static final Logger logger = Logger.getLogger(CRUD.class.getName());
  private static Map<String, String> SPREADSHEETGROUP = OnSheetWriter.readSpreadsheetInfoFromFile();

  public boolean createOrUpdateData(
    List<Object> entityData,
    String tableID,
    String tableRange,
    String filename
  ) {
    List<List<Object>> existingData;
    String filePath =
      DATA.DOWNLOAD_XLXS_FOLDER_PATH + File.separator + filename + ".xlsx";

    // Fetching existing data based on connectivity
    if (InternetConnectionChecker.isInternetAvailable()) {
      try {
        existingData = OnSheetWriter.readFromSheet(tableID, tableRange);
      } catch (IOException e) {
        logger.log(
          Level.SEVERE,
          "Error reading existing data from the sheet",
          e
        );
        return false;
      }
    } else {
      existingData = OffSheetWriter.readData(filePath);
    }

    // Check for existing data and determine creation or update
    if (existingData != null && doesDataExist(entityData, existingData)) {
      logger.info("Data already exists. Not adding duplicate entry.");
      return false;
    }

    // Add the new data or update existing data based on condition
    List<List<Object>> newData = new ArrayList<>();
    newData.add(entityData);

    try {
      OffSheetWriter.appendDataToFile(newData, filePath);
      if (InternetConnectionChecker.isInternetAvailable()) {
        OnSheetWriter.appendDataToSheet(newData, tableID, tableRange);
      } else {
        logger.info("No internet available to save the data on cloud!");
      }
      logger.info("Data added/updated successfully to the sheet!");
      return true;
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error appending/updating data to the sheet", e);
      return false;
    }
  }

  // Helper method to check if data already exists
  private boolean doesDataExist(
    List<Object> newData,
    List<List<Object>> existingData
  ) {
    for (List<Object> row : existingData) {
      List<Object> existingRowWithoutId = new ArrayList<>(
        row.subList(1, row.size())
      );
      if (existingRowWithoutId.equals(newData)) {
        return true;
      }
    }
    return false;
  }

  public T getById(String id) {
    return null;
  }

  public boolean update(T entity) {
    return false;
  }

  public boolean delete(T entity) throws IOException {
    if (InternetConnectionChecker.isInternetAvailable()) {
      OnSheetWriter.deleteRowById(
        getEntityId(entity),
        SPREADSHEETGROUP.get(getFileName(entity)),
        DATA.COURSE_TABLE_RANGE
      );
    }
    return OffSheetWriter.deleteDataById(
      getEntityId(entity),
      DATA.DOWNLOAD_XLXS_FOLDER_PATH +
      File.separator +
      getFileName(entity) +
      ".xlsx"
    );
  }

  public List<T> getAll() {
    return null;
  }

  private String getFileName(T entity) {
    if (entity instanceof User) {
      return ("lcfa_users");
    } else if (entity instanceof Course) {
      return ("lcfa_courses");
    }
    return "";
  }

  private String getEntityId(T entity) {
    // Logic to get the name of the entity, replace this with your entity-specific logic
    if (entity instanceof User) {
      return ((User) entity).getId();
    } else if (entity instanceof Course) {
      return ((Course) entity).getId();
    }
    return "-1";
  }
}
