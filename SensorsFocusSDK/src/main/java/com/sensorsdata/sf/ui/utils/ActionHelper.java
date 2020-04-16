/*
 * Created by dengshiwei on 2020/03/12.
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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.sensorsdata.sf.core.utils.SFLog;

public class ActionHelper {
    private static final String TAG = "SActionHelper";

    public static void copyToClip(Context context, String text) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(TAG, text);
            clipboardManager.setPrimaryClip(clipData);
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }
}
