/*
 * Created by dengshiwei on 2020/03/30.
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
import android.text.TextUtils;

import com.sensorsdata.sf.core.http.HttpRequestHelper;
import com.sensorsdata.sf.core.http.internal.HttpCall;
import com.sensorsdata.sf.core.http.internal.HttpCallBack;
import com.sensorsdata.sf.core.http.internal.ResponseBody;
import com.sensorsdata.sf.core.utils.SFLog;
import com.sensorsdata.sf.ui.view.DynamicViewJsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class PreviewUtil {
    private static final String TAG = "PreviewUtil";

    public static void showPreview(final Context context, boolean isTest, String popupId) {
        try {
            SFLog.d(TAG, "showPreview {isTest = " + isTest + " ,popupId = " + popupId + "}");
            if (isTest && !TextUtils.isEmpty(popupId)) {
                HttpRequestHelper.shareInstance().pullWindowInfo(popupId, new HttpCallBack() {
                    @Override
                    public void onSuccess(HttpCall httpCall, ResponseBody responseBody) {
                        try {
                            String windowJson = responseBody.body();
                            SFLog.d(TAG, "code = " + responseBody.code + "-- ui = " + windowJson);
                            DynamicViewJsonBuilder dynamicViewJsonBuilder = new DynamicViewJsonBuilder();
                            JSONObject jsonObject = new JSONObject(windowJson);
                            dynamicViewJsonBuilder.showDialogPreview(context, new JSONObject(jsonObject.optString("content")));
                        } catch (JSONException e) {
                            // ignore
                        }
                    }

                    @Override
                    public void onFailure(HttpCall httpCall, ResponseBody responseBody) {

                    }
                });
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }
}