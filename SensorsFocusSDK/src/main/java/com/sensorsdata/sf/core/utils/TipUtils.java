/*
 * Created by dengshiwei on 2020/03/19.
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

public class TipUtils {
    public static final int IMAGE_LOAD_FAILED = 1000;
    public static final int JSON_ERROR = 1001;

    private static int errorCode;

    public static  void setErrorCode(int code) {
        errorCode = code;
    }

    public static int getErrorCode() {
        return errorCode;
    }

    public static String getErrorMessage() {
        switch (errorCode) {
            case IMAGE_LOAD_FAILED:
                return "图片加载失败";
            case JSON_ERROR:
                return "预览信息解析失败，请检查计划配置";
        }
        return "";
    }
}
