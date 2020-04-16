/*
 * Created by dengshiwei on 2020/02/28.
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

package com.sensorsdata.sf.ui.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class SizeUtil {
    private static final int BASE_SIZE = 375;
    private static int[] screen_size = new int[2];

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        if (screen_size[0] == 0) {
            getDisplayMetrics(context);
        }
        return screen_size[0];
    }

    public static int getScreenHeight(Context context) {
        if (screen_size[1] == 0) {
            getDisplayMetrics(context);
        }
        return screen_size[1];
    }

    private static void getDisplayMetrics(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        screen_size[0] = displayMetrics.widthPixels;
        screen_size[1] = displayMetrics.heightPixels;
    }

    /**
     * 计算实际尺寸
     * @param context Context
     * @param sizePx 尺寸
     * @return 实际尺寸
     */
    public static int realSize(Context context, String sizePx) {
        try {
            float size = Integer.parseInt(sizePx.replace("px", ""));
            return (int) (size * getScreenWidth(context) / BASE_SIZE);
        } catch (Exception ex) {
            // ignore
        }
        return 0;
    }
}
