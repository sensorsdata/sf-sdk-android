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
package com.sensorsdata.sf.core.entity;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.sensorsdata.analytics.android.sdk.util.JSONUtils;
import com.sensorsdata.sf.core.AppStateManager;
import com.sensorsdata.sf.core.http.HttpRequestHelper;
import com.sensorsdata.sf.core.utils.SFLog;
import com.sensorsdata.sf.core.utils.Utils;
import com.sensorsdata.sf.core.window.Window;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalData implements AppStateManager.AppStateChangedListener {
    private static final String TAG = "GlobalData";
    public Context mContext;
    private Handler mConvertHandler;

    private boolean isConverting;

    public GlobalData(Context context) {
        mContext = context;
        mConvertPlans = new ArrayList<>();
        HandlerThread handlerThread = new HandlerThread("query_convert_state");
        handlerThread.start();
        mConvertHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (!mAppInForeground || !isConverting || mConvertPlans.isEmpty()) {
                    return;
                }
                Object lastUnionUUID = msg.obj;
                List<PopupPlan> arrivalEndTimePlans = new ArrayList<>();
                for (PopupPlan p : mConvertPlans) {
                    if (p.convertWindow != null && p.convertWindow.isFinished()) {
                        arrivalEndTimePlans.add(p);
                    }
                }
                mConvertPlans.removeAll(arrivalEndTimePlans);
                if (mConvertPlans.isEmpty()) {
                    return;
                }
                List<String> newUUID = new ArrayList<>();
                StringBuilder UnionUUID = new StringBuilder();
                for (int i = 0; i < mConvertPlans.size(); i++) {
                    String uuid = mConvertPlans.get(i).convertWindow.getUUID();
                    newUUID.add(uuid);
                    UnionUUID.append(uuid).append((i == (mConvertPlans.size() - 1)) ? "" : ",");
                }
                if (newUUID.isEmpty()) {
                    return;
                }
                String UnionUUIDS = UnionUUID.toString();
                boolean hasConvertSuc = requestConvert(UnionUUIDS);
                if (hasConvertSuc) {
                    commit();
                    if (mConvertPlans.isEmpty()) {
                        return;
                    }
                }
                Message message = new Message();
                if (lastUnionUUID != null) {
                    message.arg1 = msg.arg1 + 5;
                    message.arg1 = Math.min(message.arg1, 600);
                    String uuids = (String) lastUnionUUID;
                    String[] lastUUIDS = uuids.split(",");
                    for (String uuid : newUUID) {
                        boolean hasId = false;
                        for (String id : lastUUIDS) {
                            if (TextUtils.equals(uuid, id)) {
                                hasId = true;
                                break;
                            }
                        }
                        if (!hasId) {
                            message.arg1 = 5;
                            break;
                        }
                    }
                } else {
                    message.arg1 = 5;
                }
                message.what = MSG_QUERY_CONVERT_STATE;
                message.obj = UnionUUIDS;
                mConvertHandler.sendMessageDelayed(message, message.arg1 * 1000);
            }

            /**
             * 查询是否转化成功
             * http://10.42.189.228:8141/sfo/popup_displays?project=default&popup_display_uuids=aaa,bbb
             * [{"distinct_id":"1d13b63fc5df09c2","popup_display_uuid":"aaa","display_time":0,"convert_time":1},
             * {"distinct_id":"1d13b63fc5df09c2","popup_display_uuid":"bbb","display_time":0,"convert_time":1}]
             * @param UnionUUID 聚合 uuid
             * @return 是否有转化成功的
             */
            private boolean requestConvert(String UnionUUID) {
                String body = HttpRequestHelper.shareInstance().pullWindowState(UnionUUID);
                boolean hasConvertSuc = false;
                try {
                    if (!TextUtils.isEmpty(body)) {
                        SFLog.d(TAG, "convert result:" + JSONUtils.formatJson(body));
                        JSONArray jsonArray = new JSONArray(body);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jb = (JSONObject) jsonArray.get(i);
                            String UUID = jb.optString("popup_display_uuid");
                            PopupPlan p = getPlan(UUID);
                            if (p == null) {
                                continue;
                            }
                            if (jb.optLong("convert_time", 0) > 0) {
                                mConvertPlans.remove(p);
                                if (cachedGlobalData != null) {
                                    JSONObject jsonObject = cachedGlobalData.optJSONObject("plan_" + p.planId);
                                    if (jsonObject != null) {
                                        jsonObject.remove("convert");
                                    }
                                }
                                hasConvertSuc = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    //ignore SFLog.printStackTrace(e);
                }
                return hasConvertSuc;
            }

            private PopupPlan getPlan(String UUID) {
                if (TextUtils.isEmpty(UUID)) {
                    return null;
                }
                for (PopupPlan p : popupPlans) {
                    if (p.convertWindow != null && TextUtils.equals(p.convertWindow.getUUID(), UUID)) {
                        return p;
                    }
                }
                return null;
            }
        };
    }

    public String minSdkVersion;
    public String serverCurrentTime;
    /**
     * 默认 10 分钟刹车时间
     */
    public long configPullIntervalMs;

    public GlobalPopupLimit globalPopupLimit;
    /**
     * 固定，非对齐，非自然
     */
    public Window globalIntervalWindow;

    public List<PopupPlan> popupPlans;


    /**
     * {
     * "interval_global": 1584248217738,
     * "plans_2": {
     * "last_update_config_time": 1,
     * "match_0”: {
     * "start": 1584248217738,
     * "count": 5
     * },
     * "re_entry”: {
     * "start": 1584248217738,
     * "count": 3
     * },
     * "convert": 1584248217738,
     * "interval": 1584248217738
     * }
     * }
     */
    public JSONObject cachedGlobalData;

    public void setCachedGlobalData(JSONObject cachedGlobalData) {
        this.cachedGlobalData = cachedGlobalData;
    }

    public JSONObject getCachedGlobalData() {
        return cachedGlobalData;
    }

    /**
     * 一个事件会触发多个计划，同时一个事件也会匹配一个计划规则的多个 Match
     */
    public Map<String, List<PopupPlan>> eventPopupPlans = new HashMap<>();

    @Override
    public String toString() {
        return "GlobalData{" +
                "minSdkVersion='" + minSdkVersion + '\'' +
                ", serverCurrentTime='" + serverCurrentTime + '\'' +
                ", globalPopupLimit=" + globalPopupLimit +
                ", GlobalIntervalWindow=" + globalIntervalWindow +
                ", popupPlans=" + popupPlans +
                '}' + "\n";
    }

    public void commit() {
        if (cachedGlobalData == null) {
            return;
        }
        try {
            String data = cachedGlobalData.toString();
            Utils.saveJsonToFile(data, new File(mContext.getFilesDir(), Utils.SENSORS_FOCUS_LOCAL));
        } catch (Exception e) {
            SFLog.printStackTrace(e);
        }
    }

    public PopupPlan getPopupPlan(long planId) {
        if (popupPlans == null) {
            return null;
        }
        try {
            for (PopupPlan p : popupPlans) {
                if (p.planId == planId) {
                    return p;
                }
            }
        } catch (Exception e) {
            SFLog.printStackTrace(e);
        }
        return null;
    }


    public List<PopupPlan> mConvertPlans;

    public boolean isConvertSuccess(PopupPlan plan) {
        if (plan != null && plan.convertWindow != null) {
            return plan.convertWindow.isFinished() || !mConvertPlans.contains(plan);
        }
        return true;
    }

    private void startConvert() {
        if (isConverting) {
            if (mConvertHandler == null) {
                return;
            }
            if (mConvertHandler.hasMessages(MSG_QUERY_CONVERT_STATE)) {
                mConvertHandler.removeMessages(MSG_QUERY_CONVERT_STATE);
            }
            mConvertHandler.sendEmptyMessage(MSG_QUERY_CONVERT_STATE);
            return;
        }
        isConverting = true;
        mConvertHandler.sendEmptyMessage(MSG_QUERY_CONVERT_STATE);
    }

    private void stopConvert() {
        isConverting = false;
        if (mConvertHandler != null && mConvertHandler.hasMessages(MSG_QUERY_CONVERT_STATE)) {
            mConvertHandler.removeMessages(MSG_QUERY_CONVERT_STATE);
        }
    }

    /**
     * 添加需要转化的计划
     *
     * @param p 计划
     */
    public void addConvertPlan(PopupPlan p) {
        if (p != null && mConvertPlans != null && !mConvertPlans.contains(p)) {
            mConvertPlans.add(p);
            startConvert();
        }
    }

    /**
     * 移除需要转化的 plan
     *
     * @param p 计划
     */
    public void removeConvertPlan(PopupPlan p) {
        if (mConvertPlans != null && p != null) {
            mConvertPlans.remove(p);
        }
    }


    private static final int MSG_QUERY_CONVERT_STATE = 0;

    private boolean mAppInForeground = true;

    @Override
    public void onEnterForeground(boolean resumeFromBackground) {
        mAppInForeground = true;
        startConvert();
    }

    @Override
    public void onEnterBackground() {
        mAppInForeground = false;
        stopConvert();
    }
}
