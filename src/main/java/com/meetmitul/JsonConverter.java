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

import static com.meetmitul.Utils.loadDataIntoMap;
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

        File f = new File(resources.get(0).getDirectory() + "/testdata");
        File[] matchingFiles = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith("xlsx");
            }
        });


        Arrays.stream(matchingFiles)
                .forEach((file) -> {
                    convertToJSON(file);
                });


    }

    private void convertToJSON(File file) {
        try {

            File directory = new File(resources.get(0).getDirectory() + "/testdatajson");
            if (!directory.exists()) {
                directory.mkdir();
            }
            Gson gson = new Gson();
            Writer writer = new FileWriter(resources.get(0).getDirectory() + "/testdatajson/" + file.getName().replaceFirst("[.][^.]+$", "") + ".json");
            gson.toJson(loadDataIntoMap(file.toString(), null), writer);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
