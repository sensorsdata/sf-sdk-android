/*
 * Created by dengshiwei on 2020/02/26.
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

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class SFLog {
    private static final String REGEX = "SensorsFocus.";
    // 在 DEBUG 模式下是默认开启，RELEASE 下默认关闭
    private static boolean debug;

    public static void d(String tag, String msg) {
        if (debug) {
            info(REGEX + tag, msg, null);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (debug) {
            info(REGEX + tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (debug) {
            info(REGEX + tag, msg, null);
        }
    }

    public static void i(String tag, Throwable tr) {
        if (debug) {
            info(REGEX + tag, "", tr);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (debug) {
            info(REGEX + tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (debug) {
            e(REGEX + tag, msg, null);
        }
    }

    private static void e(String tag, String msg, Throwable tr) {
        try {
            Log.e(tag, msg, tr);
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    private static void info(String tag, String msg, Throwable tr) {
        try {
            Log.i(tag, msg, tr);
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    public static void printStackTrace(Exception e) {
        if (debug && e != null) {
            e.printStackTrace();
        }
    }

    /**
     * 同步获取当前 App 的状态
     *
     * @param context Context
     */
    public static void syncAppState(Context context) {
        if (context != null && context.getApplicationContext() != null) {
            debug = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
    }
}
