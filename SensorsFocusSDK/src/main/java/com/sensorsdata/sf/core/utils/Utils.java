/*
 * Created by renqingyou on 2020/03/05.
 * Copyright 2015－2020 Sensors Data Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sensorsdata.sf.core.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Utils {
    public static final String SENSORS_FOCUS_REMOTE_PLANS = "sensors_focus_remote_plans.json";
    public static final String SENSORS_FOCUS_LOCAL = "sensors_focus_local_plans.json";

    public static String getJsonFromAssets(String fileName, android.content.Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            android.content.res.AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            SFLog.printStackTrace(e);
        }
        return stringBuilder.toString();
    }

    public static void saveJsonToFile(String json, File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStreamWriter osw = new OutputStreamWriter(
                    new FileOutputStream(file), "utf-8");
            BufferedWriter writer = new BufferedWriter(osw);
            writer.write(json);
            writer.close();
            osw.close();
        } catch (Exception e) {
            SFLog.printStackTrace(e);
        }
    }

    public static void deleteFile(File file) {
        try {
            if (file != null && file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            SFLog.printStackTrace(e);
        }
    }

    public static String loadJsonFromFile(File file) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            //通过管理器打开文件并读取
            InputStream inputStream = new FileInputStream(file);
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            bf.close();
            inputStream.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            //SFLog.printStackTrace(e);
        }
        return null;
    }

    public static boolean isVersionValid(String saVersion, String requiredVersion) {
        try {
            if (saVersion.equals(requiredVersion)) {
                return true;
            } else {
                String[] saVersions = saVersion.split("\\.");
                String[] requiredVersions = requiredVersion.split("\\.");
                for (int index = 0; index < requiredVersions.length; index++) {
                    if (Integer.parseInt(saVersions[index]) > Integer.parseInt(requiredVersions[index])) {
                        return true;
                    }
                }
                return false;
            }
        } catch (Exception ex) {
            // ignore
            return false;
        }
    }
}
