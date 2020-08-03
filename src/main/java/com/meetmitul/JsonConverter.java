package com.meetmitul;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.gson.Gson;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static org.apache.poi.xssf.usermodel.XSSFCell.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.apache.poi.xssf.usermodel.XSSFCell.*;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * @phase process-sources
 */
@Mojo(name = "json-converter", defaultPhase = LifecyclePhase.COMPILE)
public class JsonConverter
        extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "scope")
    String scope;

    @Parameter(defaultValue = "${project.resources}", required = true, readonly = true)
    private List<Resource> resources;

    public JsonConverter() throws IOException {
    }

    public void execute() throws MojoExecutionException, MojoFailureException {


        // your directory
        File f = new File(resources.get(0).getDirectory() + "/testdata");
        File[] matchingFiles = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith("xlsx");
            }
        });

        System.out.println(matchingFiles[0]);

        Arrays.stream(matchingFiles)
                .forEach((file) -> {
                    convertToJSON(file);
                });


    }

    private void convertToJSON(File file) {
        try {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            char c1 = (char) fis.read();
            System.out.println(c1);

            File directory = new File(resources.get(0).getDirectory() + "/testdatajson");
            if (!directory.exists()) {
                directory.mkdir();
            }
            Gson gson = new Gson();
            Writer writer = new FileWriter(resources.get(0).getDirectory() + "/testdatajson/" + file.getName().replaceFirst("[.][^.]+$", "")+ ".json");
            gson.toJson(loadDataIntoMap(file.toString(), null), writer);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<HashMap<String, String>> loadDataIntoMap(String filePath, String methodName) {
        Workbook wb = null;           //initialize Workbook null
        try {
            //reading data from a file in the form of bytes
            FileInputStream fis = new FileInputStream(filePath);
            //constructs an XSSFWorkbook object, by buffering the whole stream into the memory
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


        Row headerRow = sheet.getRow(0);

        for (Cell mycell : headerRow) {
            switch (mycell.getCellType()) {
                case NUMERIC:
                    headerList.add(String.valueOf(mycell.getNumericCellValue()));
                    break;
                case STRING:
                    headerList.add(mycell.getStringCellValue());
                    break;
                case BOOLEAN:
                    headerList.add(String.valueOf(mycell.getBooleanCellValue()));
                    break;
                default:
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
                        if(temp!=null){
                            dataRow.put(headerList.get(i), temp);
                        }

                    }

                }
                dataMap.add(dataRow);
            }
        }

        return dataMap;
    }

}
