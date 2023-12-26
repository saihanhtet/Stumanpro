package com.hanhtet.stumanpro.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class OffSheetWriter {

  public static void createSheet(
    List<List<Object>> data,
    String filePath,
    String sheetName
  ) {
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

    try {
      File file = new File(filePath);
      if (!file.exists()) {
        file.getParentFile().mkdirs(); // Create parent directories if they don't exist
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
          workbook.write(outputStream);
        }
        workbook.close();
        System.out.println("Excel file created successfully!");
      } else {
        System.out.println("File already exists at: " + filePath);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void appendDataToFile(
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
            lastID = (int) cellValue;
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
        newRowData.addAll(rowData);
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

  public static boolean deleteDataById(String id, String filePath) {
    try {
      FileInputStream fileInputStream = new FileInputStream(filePath);
      Workbook workbook = new XSSFWorkbook(fileInputStream);
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
        return true;
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
    return false;
  }

  public static void editDataById(
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

      fileInputStream.close();
      FileOutputStream outputStream = new FileOutputStream(filePath);
      workbook.write(outputStream);
      outputStream.flush();
      outputStream.close();
      workbook.close();
      System.out.println("Data successfully updated in the local file.");
    } catch (IOException e) {
      System.err.println(
        "Error occurred while updating data in the local file: " +
        e.getMessage()
      );
    }
  }

  public static List<List<Object>> readData(String filePath) {
    try {
      FileInputStream fileInputStream = new FileInputStream(filePath);
      Workbook workbook = new XSSFWorkbook(fileInputStream);
      Sheet sheet = workbook.getSheetAt(0);
      List<List<Object>> data = convertSheetToList(sheet);
      data.remove(0);
      workbook.close();
      fileInputStream.close();
      return data;
    } catch (Exception e) {
      System.err.println("File not found error while reading data: " + e);
    }
    return null;
  }

  public static List<List<Object>> convertSheetToList(Sheet sheet) {
    List<List<Object>> data = new ArrayList<>();
    for (Row row : sheet) {
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
          default:
            rowData.add("");
        }
      }
      data.add(rowData);
    }
    return data;
  }
}
