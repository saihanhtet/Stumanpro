package com.hanhtet.stumanpro.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class OffSheetWriter {

  private OffSheetWriter() {
    throw new IllegalStateException("Offline sheet writer class");
  }

  public static void createSheet(
    List<List<Object>> data,
    String filePath,
    String sheetName
  ) {
    Workbook workbook;
    org.apache.poi.ss.usermodel.Sheet sheet;
    try {
      workbook = new XSSFWorkbook();
      sheet = workbook.createSheet(sheetName);
      int rowNum = 0;
      for (List<Object> rowData : data) {
        Row row = sheet.createRow(rowNum++);
        int colNum = 0;
        for (Object field : rowData) {
          Cell cell = row.createCell(colNum++);
          setCellValue(cell, field);
        }
        File file = new File(filePath);
        if (!file.exists()) {
          file.getParentFile().mkdirs(); // Create parent directories if they don't exist
          try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
          }
          workbook.close();
          LOG.logInfo("Excel file created successfully!");
        } else {
          LOG.logInfo("File already exist at: " + filePath);
        }
      }
    } catch (Exception e) {
      LOG.logMe(Level.WARNING, "workbook create error", e);
    }
  }

  public static void appendDataToFile(
    List<List<Object>> newData,
    String filePath
  ) {
    try {
      Workbook workbook = openWorkbook(filePath);
      Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet
      int lastRowNum = sheet.getLastRowNum();
      int lastID = 0;
      for (int i = lastRowNum; i >= 0; i--) {
        Row row = sheet.getRow(i);
        if (row != null) {
          Cell cell = row.getCell(0);
          if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            double cellValue = cell.getNumericCellValue();
            lastID = (int) cellValue;
            break;
          }
        }
      }
      for (List<Object> rowData : newData) {
        Row row = sheet.createRow(++lastID);
        int colCount = 0;
        List<Object> newRowData = new ArrayList<>();
        newRowData.add(lastID);
        newRowData.addAll(rowData);
        for (Object field : newRowData) {
          Cell cell = row.createCell(colCount++);
          setCellValue(cell, field);
        }
      }
      FileOutputStream outputStream = new FileOutputStream(filePath);
      workbook.write(outputStream);
      workbook.close();
      outputStream.close();
      LOG.logInfo("Data successfully appended to the local file.");
    } catch (IOException e) {
      LOG.logMe(
        Level.WARNING,
        "Error occurred while appending data to the local file: ",
        e
      );
    }
  }

  public static boolean deleteDataById(String id, String filePath) {
    try {
      Workbook workbook = openWorkbook(filePath);
      Sheet sheet = workbook.getSheetAt(0);
      int columnIndexToDelete = 0;
      boolean flag = false;
      for (int i = sheet.getLastRowNum(); i >= 0; i--) {
        Row row = sheet.getRow(i);
        if (row != null) {
          Cell cell = row.getCell(columnIndexToDelete);
          if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            double cellValue = cell.getNumericCellValue();
            int intValue = (int) cellValue;
            if (intValue == Integer.parseInt(id)) {
              LOG.logInfo("Found matching cell: " + cellValue);
              sheet.removeRow(row);
              flag = true;
              break;
            }
          }
        }
      }
      if (flag) {
        FileOutputStream outputStream = new FileOutputStream(filePath);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
        LOG.logInfo("Row with id " + id + " deleted from the local file.");
        return true;
      } else {
        LOG.logWarn("Row with id " + id + " not found in the local file.");
      }
      workbook.close();
    } catch (Exception e) {
      LOG.logMe(
        Level.WARNING,
        "Error occurred while deleting row from the local file: ",
        e
      );
    }
    return false;
  }

  public static void editDataById(
    String id,
    List<Object> newData,
    String filePath
  ) {
    try {
      Workbook workbook = openWorkbook(filePath);
      if (workbook != null) {
        Sheet sheet = workbook.getSheetAt(0);
        updateData(sheet, id, newData);
        saveWorkbook(workbook, filePath);
        LOG.logInfo("Data successfully updated in the local file.");
      }
    } catch (Exception e) {
      LOG.logMe(Level.WARNING, "Error occurred while updating data: ", e);
    }
  }

  private static void updateData(Sheet sheet, String id, List<Object> newData) {
    int columnIndexToUpdate = 0;
    for (Row row : sheet) {
      Cell cell = row.getCell(columnIndexToUpdate);
      if (cell != null && cell.getCellType() == CellType.NUMERIC) {
        double cellValue = cell.getNumericCellValue();
        int intValue = (int) cellValue;
        if (intValue == Integer.parseInt(id)) {
          LOG.logInfo("Found matching cell: " + cellValue);
          updateRow(row, newData);
          break;
        }
      }
    }
  }

  private static void updateRow(Row row, List<Object> newData) {
    int colNum = 0;
    for (Object field : newData) {
      Cell existingCell = row.getCell(colNum);
      if (existingCell == null) {
        existingCell = row.createCell(colNum);
      }
      setCellValue(existingCell, field);
      colNum++;
    }
  }

  private static Workbook openWorkbook(String filePath) {
    try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
      return new XSSFWorkbook(fileInputStream);
    } catch (IOException e) {
      LOG.logWarn("can't open workbook: " + filePath);
    }
    return null;
  }

  private static void setCellValue(Cell cell, Object field) {
    if (field instanceof String) {
      cell.setCellValue((String) field);
    } else if (field instanceof Integer) {
      cell.setCellValue((Integer) field);
    } else if (field instanceof Double) {
      cell.setCellValue((Double) field);
    }
  }

  private static void saveWorkbook(Workbook workbook, String filePath) {
    try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
      workbook.write(outputStream);
      outputStream.flush();
    } catch (Exception e) {
      LOG.logWarn("Can't save workbook: " + filePath);
    }
  }

  public static List<List<Object>> readData(String filePath) {
    List<List<Object>> data = new ArrayList<>();

    try (Workbook workbook = openWorkbook(filePath)) {
      Sheet sheet = workbook.getSheetAt(0);
      data = convertSheetToList(sheet);
      data.remove(0);
    } catch (IOException e) {
      LOG.logWarn("File not found error while reading data: " + e);
    }

    return data;
  }

  public static List<List<Object>> convertSheetToList(Sheet sheet) {
    List<List<Object>> data = new ArrayList<>();
    for (Row row : sheet) {
      List<Object> rowData = convertRowToList(row);
      data.add(rowData);
    }
    return data;
  }

  private static List<Object> convertRowToList(Row row) {
    List<Object> rowData = new ArrayList<>();
    for (Cell cell : row) {
      Object cellValue = getCellValue(cell);
      rowData.add(cellValue);
    }
    return rowData;
  }

  private static Object getCellValue(Cell cell) {
    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          return cell.getDateCellValue();
        } else {
          double numericCellValue = cell.getNumericCellValue();
          if (numericCellValue == (int) numericCellValue) {
            return (int) numericCellValue;
          } else {
            return numericCellValue;
          }
        }
      case BOOLEAN:
        return cell.getBooleanCellValue();
      default:
        return "";
    }
  }
}
