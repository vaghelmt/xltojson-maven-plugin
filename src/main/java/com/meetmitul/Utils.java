package com.meetmitul;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils {

    public static List<HashMap<String, String>> loadDataIntoMap(String filePath, String methodName) throws IncorrectColumnTypeException {
        Workbook wb = null;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            wb = new XSSFWorkbook(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        List<HashMap<String, String>> dataMap = new ArrayList<HashMap<String, String>>();


        List<String> headerList = new ArrayList<String>();

        Sheet sheet = null;

        if (methodName != null) {
            sheet = wb.getSheet(methodName);
        }

        sheet = wb.getSheetAt(0);
        if (!(sheet.getNumMergedRegions() > 0)) {
            Row headerRow = sheet.getRow(0);

            for (Cell mycell : headerRow) {
                if (!mycell.getCellType().equals(CellType.BOOLEAN) &&
                        !mycell.getCellType().equals(CellType.NUMERIC)) {
                    switch (mycell.getCellType()) {
                        case STRING:
                            headerList.add(mycell.getStringCellValue());
                            break;
                        default:
                    }
                } else {
                    throw new IncorrectColumnTypeException(String.format("%d has incorrect column type of%s .All column headers should be of type string",
                            mycell.getColumnIndex(), mycell.getCellType().toString()));
                }
            }

            for (Row myrow : sheet) {
                if (myrow.getPhysicalNumberOfCells() > 0 && myrow.getRowNum() > 0) {
                    HashMap<String, String> dataRow = new HashMap<String, String>();
                    if (myrow.getRowNum() > 0) {
                        String temp = null;
                        for (int i = 0; i < myrow.getPhysicalNumberOfCells(); i++) {
                            switch (myrow.getCell(i).getCellType()) {
                                case NUMERIC:
                                    temp = String.valueOf(myrow.getCell(i).getNumericCellValue());
                                    break;
                                case STRING:
                                    temp = myrow.getCell(i).getStringCellValue();
                                    break;
                                case BOOLEAN:
                                    temp = String.valueOf(myrow.getCell(i).getBooleanCellValue());
                                    break;
                                default:
                            }
                            if (temp != null) {
                                dataRow.put(headerList.get(i), temp);
                            }

                        }

                    }
                    dataMap.add(dataRow);
                }
            }


        }else{
            throw new IllegalExcelFormatException("There are merged cells present in the excel file" + filePath + "There" +
                    "should not be any merged cells.");
        }
        return dataMap;
    }
}




