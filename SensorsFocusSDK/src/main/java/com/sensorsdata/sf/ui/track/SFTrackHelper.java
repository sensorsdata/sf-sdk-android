/*
 * Created by dengshiwei on 2020/03/07.
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

package com.sensorsdata.sf.ui.track;

import android.text.TextUtils;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.sf.android.sdk.BuildConfig;
import com.sensorsdata.sf.core.entity.PopupPlan;
import com.sensorsdata.sf.core.utils.SFLog;

import org.json.JSONObject;

import java.util.UUID;

public class SFTrackHelper {
    // event name
    private static final String EVENT_DISPLAY = "$PlanPopupDisplay";
    private static final String EVENT_CLICK = "$PlanPopupClick";

    private static final String PLAN_TYPE = "运营计划";
    private static final String CHANNEL_SERVICE_NAME = "SENSORS_FOCUS";
    private static final String CHANNEL_CATEGORY = "POPUP";
    private static final String PLATFORM_TAG = "ANDROID";
    private static final String MSG_TYPE = "type";
    private static final String MSG_ACTION_ID = "id";

    private static final String SF_PLAN_ID = "$sf_plan_id";
    private static final String SF_PLAN_TYPE = "$sf_plan_type";
    private static final String SF_CHANNEL_SERVICE_NAME = "$sf_channel_service_name";
    private static final String SF_CHANNEL_CATEGORY = "$sf_channel_category";
    private static final String SF_PLATFORM_TAG = "$sf_platform_tag";
    private static final String SF_AUDIENCE_ID = "$sf_audience_id";
    private static final String SF_CLOSE_TYPE = "$sf_close_type";
    private static final String SF_PLAN_STRATEGY_ID = "$sf_plan_strategy_id";
    private static final String SF_MSG_TITLE = "$sf_msg_title";
    private static final String SF_MSG_CONTENT = "$sf_msg_content";
    private static final String SF_SUCCEED = "$sf_succeed";
    private static final String SF_FAIL_REASON = "$sf_fail_reason";
    private static final String SF_MSG_ELEMENT_TYPE = "$sf_msg_element_type";
    private static final String SF_MSG_ELEMENT_CONTENT = "$sf_msg_element_content";
    private static final String SF_MSG_ELEMENT_ACTION = "$sf_msg_element_action";
    private static final String SF_MSG_IMAGE_URL = "$sf_msg_image_url";
    private static final String SF_MSG_ACTION_ID = "$sf_msg_action_id";
    private static final String SF_MSG_ID = "$sf_msg_id";
    private static final String SF_LIB_VERSION = "$sf_lib_version";

    /*
     * track popup window display
     */
    public static void trackPlanPopupDisplay(String title, String content, String imageUrl, boolean isSucceed, String failedReason, JSONObject planJson) {
        try {
            JSONObject propertyJson;
            if (planJson != null) {
                propertyJson = new JSONObject(planJson.toString());
            } else {
                propertyJson = new JSONObject();
            }
            if (!TextUtils.isEmpty(title)) {
                propertyJson.put(SF_MSG_TITLE, title);
            }
            if (!TextUtils.isEmpty(content)) {
                propertyJson.put(SF_MSG_CONTENT, content);
            }
            if (!TextUtils.isEmpty(imageUrl)) {
                propertyJson.put(SF_MSG_IMAGE_URL, imageUrl);
            }
            propertyJson.put(SF_SUCCEED, isSucceed);
            if (!TextUtils.isEmpty(failedReason)) {
                propertyJson.put(SF_FAIL_REASON, failedReason);
            }
            SensorsDataAPI.sharedInstance().track(EVENT_DISPLAY, propertyJson);
            SensorsDataAPI.sharedInstance().flushSync();
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }

    /*
     * track popup window click
     */
    public static void trackPlanPopupClick(String title, String content, String element_type,
                                           String element_content, String imageUrl, JSONObject actionJson, JSONObject planJson) {
        try {
            JSONObject propertyJson;
            if (planJson != null) {
                propertyJson = new JSONObject(planJson.toString());
            } else {
                propertyJson = new JSONObject();
            }
            if (!TextUtils.isEmpty(title)) {
                propertyJson.put(SF_MSG_TITLE, title);
            }
            if (!TextUtils.isEmpty(content)) {
                propertyJson.put(SF_MSG_CONTENT, content);
            }
            if (!TextUtils.isEmpty(element_content)) {
                propertyJson.put(SF_MSG_ELEMENT_CONTENT, element_content);
            }
            if (!TextUtils.isEmpty(imageUrl)) {
                propertyJson.put(SF_MSG_IMAGE_URL, imageUrl);
            }
            propertyJson.put(SF_MSG_ELEMENT_TYPE, element_type);
            if (actionJson != null) {
                if (actionJson.has(MSG_TYPE)) {
                    propertyJson.put(SF_MSG_ELEMENT_ACTION, actionJson.optString(MSG_TYPE));
                }
                if (actionJson.has(MSG_ACTION_ID)) {
                    propertyJson.put(SF_MSG_ACTION_ID, actionJson.optString(MSG_ACTION_ID));
                }
                if (actionJson.has(SF_CLOSE_TYPE)) {
                    propertyJson.put(SF_CLOSE_TYPE, actionJson.optString(SF_CLOSE_TYPE));
                }
            }
            SensorsDataAPI.sharedInstance().track(EVENT_CLICK, propertyJson);
            SensorsDataAPI.sharedInstance().flushSync();
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }

    public static JSONObject buildPlanProperty(PopupPlan popupPlan) {
        try {
            JSONObject planJson = new JSONObject();
            if (popupPlan != null) {
                planJson.put(SF_PLAN_ID, popupPlan.planId + "");
                if (popupPlan.audienceId != null) {
                    planJson.put(SF_AUDIENCE_ID, popupPlan.audienceId + "");
                }
                planJson.put(SF_PLAN_STRATEGY_ID, popupPlan.isControlGroup ? "-1" : "0");
            }
            String msgId = popupPlan == null || TextUtils.isEmpty(popupPlan.convertWindow.getUUID()) ? UUID.randomUUID().toString() : popupPlan.convertWindow.getUUID();
            planJson.put(SF_MSG_ID, msgId);
            planJson.put(SF_PLAN_TYPE, PLAN_TYPE);
            planJson.put(SF_CHANNEL_SERVICE_NAME, CHANNEL_SERVICE_NAME);
            planJson.put(SF_CHANNEL_CATEGORY, CHANNEL_CATEGORY);
            planJson.put(SF_PLATFORM_TAG, PLATFORM_TAG);
            planJson.put(SF_LIB_VERSION, BuildConfig.VERSION_NAME);
            return planJson;
        } catch (Exception ex) {
            // ignore
        }
        return null;
    }
}
