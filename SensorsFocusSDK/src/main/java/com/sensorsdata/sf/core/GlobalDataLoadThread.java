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
package com.sensorsdata.sf.core;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.util.JSONUtils;
import com.sensorsdata.sf.android.sdk.BuildConfig;
import com.sensorsdata.sf.core.entity.Condition;
import com.sensorsdata.sf.core.entity.Filter;
import com.sensorsdata.sf.core.entity.GlobalData;
import com.sensorsdata.sf.core.entity.GlobalPopupLimit;
import com.sensorsdata.sf.core.entity.Matcher;
import com.sensorsdata.sf.core.entity.PatternPopup;
import com.sensorsdata.sf.core.entity.PopupPlan;
import com.sensorsdata.sf.core.http.HttpRequestHelper;
import com.sensorsdata.sf.core.utils.SFLog;
import com.sensorsdata.sf.core.utils.Utils;
import com.sensorsdata.sf.core.window.ConvertWindow;
import com.sensorsdata.sf.core.window.Limit;
import com.sensorsdata.sf.core.window.Window;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class GlobalDataLoadThread extends HandlerThread implements AppStateManager.AppStateChangedListener {
    private static final int MSG_LOAD_LOCAL_POPUP_PLANS = 0;
    private static final int MSG_LOAD_REMOTE_POPUP_PLANS = 1;
    private static final int MSG_DISTINCT_ID_CHANGED = 2;
    private static final long GLOBAL_DATA_FLUSH_INTERVAL = 10 * 60 * 1000;
    private static String TAG = "GlobalDataLoadThread";
    private Context mContext;
    private List<CallBack> callBacks = new ArrayList<>(1);
    private Handler mHandler;
    private boolean mAppInForeground = true;

    GlobalDataLoadThread(String threadName, final Context context) {
        super(threadName);
        this.mContext = context;
    }

    @Override
    public void start() {
        super.start();
        mHandler = new Handler(getLooper()) {
            private JSONObject runningGlobalObject = null;
            private GlobalData mGlobalData = null;
            private File remotePlanFile = new File(mContext.getFilesDir(), Utils.SENSORS_FOCUS_REMOTE_PLANS);
            private File localFile = new File(mContext.getFilesDir(), Utils.SENSORS_FOCUS_LOCAL);

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_LOAD_LOCAL_POPUP_PLANS) {
                    loadData(msg.what);
                    sendEmptyMessage(MSG_LOAD_REMOTE_POPUP_PLANS);
                } else if (msg.what == MSG_LOAD_REMOTE_POPUP_PLANS) {
                    if (!mAppInForeground) {
                        return;
                    }
                    loadData(msg.what);
                    //测试 5 秒更新
                    //sendEmptyMessageDelayed(MSG_LOAD_REMOTE_POPUP_PLANS, 5000);
                    //定时更新计划
                    sendEmptyMessageDelayed(MSG_LOAD_REMOTE_POPUP_PLANS, mGlobalData == null ? GLOBAL_DATA_FLUSH_INTERVAL : Math.max(mGlobalData.configPullIntervalMs, GLOBAL_DATA_FLUSH_INTERVAL));
                } else if (msg.what == MSG_DISTINCT_ID_CHANGED) {
                    if (mGlobalData != null) {
                        if (mGlobalData.popupPlans != null) {
                            mGlobalData.popupPlans.clear();
                        }
                        if (mGlobalData.mConvertPlans != null) {
                            mGlobalData.mConvertPlans.clear();
                        }
                        if (mGlobalData.eventPopupPlans != null) {
                            mGlobalData.eventPopupPlans.clear();
                        }
                    }
                    runningGlobalObject = null;
                    Utils.deleteFile(remotePlanFile);
                    Utils.deleteFile(localFile);
                    if (mContext != null) {
                        Utils.deleteFile(new File(mContext.getFilesDir(), GlobalPopupLimit.GLOBAL_POPUP_LIMIT_FILE_NAME));
                    }
                    sendEmptyMessage(MSG_LOAD_REMOTE_POPUP_PLANS);
                }
            }

            private void loadData(int fromWhereLoad) {
                try {
                    if (fromWhereLoad == MSG_LOAD_LOCAL_POPUP_PLANS) {
                        String popupPlansJson = Utils.loadJsonFromFile(remotePlanFile);
                        String runningPopupPlan = Utils.loadJsonFromFile(localFile);
                        if (runningPopupPlan != null) {
                            runningGlobalObject = new JSONObject(runningPopupPlan);
                        }
                        if (popupPlansJson != null) {
                            SFLog.d(TAG, "loadDataFromLocal:" + JSONUtils.formatJson(popupPlansJson));
                            JSON2GlobalData(new JSONObject(popupPlansJson), fromWhereLoad);
                        }

                    } else {
                        //从网络加载数据
                        String data = HttpRequestHelper.shareInstance().pullEventConfig(SensorsDataAPI.sharedInstance(mContext).getDistinctId());
                        if (TextUtils.isEmpty(data)) {
                            return;
                        }
                        //String data = Utils.getJsonFromAssets(Utils.SENSORS_FOCUS_POPUP_PLANS, mContext);
                        SFLog.d(TAG, "loadDataFromNet:" + JSONUtils.formatJson(data));
                        JSONObject jsonObject = new JSONObject(data);
                        JSON2GlobalData(jsonObject, fromWhereLoad);
                        Utils.saveJsonToFile(data, remotePlanFile);
                    }
                } catch (Exception e) {
                    SFLog.printStackTrace(e);
                }
            }

            /*private void removeDeletedPlans() {
                if (mGlobalData == null || runningGlobalObject == null) {
                    return;
                }
                Iterator<String> it = runningGlobalObject.keys();
                ArrayList<String> deleteIds = new ArrayList<>();
                while (it.hasNext()) {
                    String key = it.next();
                    if (key.contains("plan_")) {
                        try {
                            PopupPlan p = mGlobalData.getPopupPlan(Long.valueOf(key.replace("plan_", "")));
                            if (p == null) {
                                deleteIds.add(key);
                            }
                        } catch (Exception e) {
                            SFLog.printStackTrace(e);
                        }
                    }
                }
                for (String id : deleteIds) {
                    runningGlobalObject.remove(id);
                }
                String running = runningGlobalObject.toString();
                SFLog.d(TAG, "cached data:" + running);
                Utils.saveJsonToFile(running, localFile);
            }*/

            private void JSON2GlobalData(JSONObject jsonObject, int fromWhereLoad) {
                JSONObject runningPlanObject = null;
                String minSdkVersionRequired = jsonObject.optString("min_sdk_version_required");
                if (!TextUtils.isEmpty(minSdkVersionRequired) && BuildConfig.VERSION_NAME.compareTo(minSdkVersionRequired) < 0) {
                    SFLog.d(TAG, "SDK versionName:" + BuildConfig.VERSION_NAME + ", minSdkVersionRequired:" + minSdkVersionRequired);
                    if (mGlobalData != null) {
                        if (mGlobalData.popupPlans != null) {
                            mGlobalData.popupPlans.clear();
                        }
                        if (mGlobalData.mConvertPlans != null) {
                            mGlobalData.mConvertPlans.clear();
                        }
                        if (mGlobalData.eventPopupPlans != null) {
                            mGlobalData.eventPopupPlans.clear();
                        }
                    }
                    runningGlobalObject = null;
                    Utils.deleteFile(remotePlanFile);
                    Utils.deleteFile(localFile);
                    return;
                }
                JSONArray popupPlans = jsonObject.optJSONArray("popup_plans");
                if (popupPlans == null) {
                    return;
                }
                if (mGlobalData == null) {
                    mGlobalData = new GlobalData(mContext);
                }

                mGlobalData.minSdkVersion = minSdkVersionRequired;

                mGlobalData.serverCurrentTime = jsonObject.optString("server_current_time");

                //刹车时间
                mGlobalData.configPullIntervalMs = jsonObject.optLong("config_pull_interval_ms");

                //全局间隔窗口限制
                JSONObject globalInterval = jsonObject.optJSONObject("popup_interval_global");
                if (globalInterval != null) {
                    mGlobalData.globalIntervalWindow = new Window();
                    mGlobalData.globalIntervalWindow.natural = globalInterval.optBoolean("natural");
                    mGlobalData.globalIntervalWindow.value = globalInterval.optInt("value");
                    mGlobalData.globalIntervalWindow.unit = globalInterval.optString("unit");
                    if (runningGlobalObject != null) {
                        long interval_global = runningGlobalObject.optLong("interval_global");
                        if (interval_global != 0) {
                            mGlobalData.globalIntervalWindow.setStartTime(interval_global);
                        }
                    }
                }

                //全局弹窗限制
                JSONObject globalPopupLimit = jsonObject.optJSONObject("msg_limit_global");
                if (globalPopupLimit != null) {
                    mGlobalData.globalPopupLimit = new GlobalPopupLimit(mContext);
                    mGlobalData.globalPopupLimit.isInUse = globalPopupLimit.optBoolean("is_in_use");
                    mGlobalData.globalPopupLimit.limits = new ArrayList<>();
                    JSONArray limits = globalPopupLimit.optJSONArray("limits");
                    if (limits != null) {
                        for (int i = 0; i < limits.length(); i++) {
                            JSONObject object = (JSONObject) limits.opt(i);
                            String unit = object.optString("unit");
                            boolean natural = object.optBoolean("natural");
                            int value = object.optInt("value");
                            int limitCount = object.optInt("limit");
                            Limit limit = new Limit();
                            limit.unit = unit;
                            limit.value = value;
                            limit.limit = limitCount;
                            limit.natural = natural;
                            mGlobalData.globalPopupLimit.limits.add(limit);
                        }
                    }
                }

                if (mGlobalData.popupPlans == null) {
                    mGlobalData.popupPlans = new ArrayList<>();
                }

                ArrayList<Long> oldIds = new ArrayList<>();
                for (PopupPlan popupPlan : mGlobalData.popupPlans) {
                    oldIds.add(popupPlan.planId);
                }

                ArrayList<Long> newIds = new ArrayList<>();

                for (int i = 0; i < popupPlans.length(); i++) {
                    JSONObject plan = (JSONObject) popupPlans.opt(i);
                    long planId = plan.optLong("plan_id");
                    newIds.add(planId);
                    long lastUpdateConfigTime = plan.optLong("last_update_config_time");
                    PopupPlan popupPlan = mGlobalData.getPopupPlan(planId);
                    boolean update;
                    if (popupPlan == null) {
                        popupPlan = new PopupPlan();
                        update = true;
                        popupPlan.planId = planId;
                        popupPlan.lastUpdateConfigTime = lastUpdateConfigTime;
                    } else {
                        update = (lastUpdateConfigTime != popupPlan.lastUpdateConfigTime);
                    }
                    //https://doc.sensorsdata.cn/pages/viewpage.action?pageId=50733329
                    //is_audience、cname、audience_id 等变更不会影响 last_update_config_time
                    //是否受众
                    popupPlan.isAudience = plan.optBoolean("is_audience");
                    //受众 ID
                    try {
                        popupPlan.audienceId = plan.getLong("audience_id");
                    } catch (Exception e) {
                        //ignore SFLog.d(TAG, "Plan " + planId + " is no audience ID");
                    }
                    //显示名
                    popupPlan.cname = plan.optString("cname");
                    if (!update) {
                        continue;
                    }
                    if (runningGlobalObject != null) {
                        try {
                            //从网络加载时，发现 lastUpdateConfigTime 变更，要实时删除本地保存的该计划
                            if (fromWhereLoad == MSG_LOAD_REMOTE_POPUP_PLANS) {
                                runningGlobalObject.remove("plan_" + popupPlan.planId);
                            } else {
                                runningPlanObject = runningGlobalObject.optJSONObject("plan_" + popupPlan.planId);
                            }
                        } catch (Exception e) {
                            SFLog.printStackTrace(e);
                        }
                    }
                    //获取过期时间
                    popupPlan.expireAt = plan.optLong("expire_at");
                    //计划状态
                    popupPlan.status = plan.optString("status");
                    //对照组
                    popupPlan.isControlGroup = plan.optBoolean("is_control_group");

                    //优先级
                    popupPlan.absolutePriority = plan.optInt("absolute_priority");
                    //参与限制窗口
                    JSONObject reEnter = plan.optJSONObject("re_enter");
                    if (reEnter != null) {
                        popupPlan.planReEntryWindow = new Window();
                        popupPlan.planReEntryWindow.unit = reEnter.optString("unit");
                        popupPlan.planReEntryWindow.value = reEnter.optInt("value");
                        popupPlan.planReEntryWindow.limit = reEnter.optInt("limit");
                        if (runningPlanObject != null) {
                            JSONObject reEntry = runningPlanObject.optJSONObject("re_entry");
                            if (reEntry != null) {
                                popupPlan.planReEntryWindow.setStartTime(reEntry.optLong("start"));
                                popupPlan.planReEntryWindow.setCount(reEntry.optInt("count"));
                            }
                        }
                    }

                    //计划内间隔窗口
                    JSONObject interval = plan.optJSONObject("popup_interval");
                    if (interval != null) {
                        popupPlan.planIntervalWindow = new Window();
                        popupPlan.planIntervalWindow.unit = interval.optString("unit");
                        popupPlan.planIntervalWindow.value = interval.optInt("value");
                        popupPlan.planIntervalWindow.natural = interval.optBoolean("natural");
                        if (runningPlanObject != null) {
                            long popup_interval = runningPlanObject.optLong("interval");
                            if (popup_interval != 0) {
                                popupPlan.planIntervalWindow.setStartTime(popup_interval);
                            }
                        }
                    }

                    //转化窗口
                    JSONObject convertWindow = plan.optJSONObject("convert_window");
                    if (convertWindow != null) {
                        popupPlan.convertWindow = new ConvertWindow();
                        popupPlan.convertWindow.unit = convertWindow.optString("unit");
                        popupPlan.convertWindow.value = convertWindow.optInt("value");
                        popupPlan.convertWindow.natural = convertWindow.optBoolean("natural");

                        if (runningPlanObject != null) {
                            JSONObject convert = runningPlanObject.optJSONObject("convert");
                            if (convert != null) {
                                popupPlan.convertWindow.setUUID(convert.optString("uuid"));
                                popupPlan.convertWindow.setStartTime(convert.optLong("start"));
                                if (!popupPlan.convertWindow.isFinished()) {
                                    mGlobalData.addConvertPlan(popupPlan);
                                }
                            }
                        }
                    }

                    //弹窗样式
                    popupPlan.popupWindowContent = plan.optJSONObject("popup_window_content");

                    //指定限定内容
                    popupPlan.pageFilter = plan.optJSONObject("page_filter");

                    //全局触达配置开关
                    popupPlan.enableGlobalMsgLimit = plan.optBoolean("global_msg_limit_enabled");

                    //弹窗规则
                    JSONObject patternPopup = plan.optJSONObject("pattern_popup");
                    if (patternPopup != null) {
                        popupPlan.patternPopup = new PatternPopup();
                        popupPlan.patternPopup.relation = patternPopup.optString("relation");
                        popupPlan.patternPopup.matcherList = new ArrayList<>();
                        JSONArray array = patternPopup.optJSONArray("matcher_list");
                        if (array != null) {
                            for (int k = 0; k < array.length(); k++) {
                                JSONObject object1 = (JSONObject) array.opt(k);
                                Matcher matcher = new Matcher();
                                matcher.type = object1.optString("type");
                                matcher.eventWindow = new Window();
                                JSONObject window = object1.optJSONObject("window");
                                if (window != null) {
                                    matcher.eventWindow.value = window.optInt("value");
                                    matcher.eventWindow.unit = window.optString("unit");
                                    matcher.eventWindow.natural = window.optBoolean("natural");
                                    if (runningPlanObject != null) {
                                        JSONObject match = runningPlanObject.optJSONObject("matcher_" + k);
                                        if (match != null) {
                                            matcher.eventWindow.setStartTime(match.optLong("start"));
                                            matcher.eventWindow.setCount(match.optInt("count"));
                                        }
                                    }
                                }
                                matcher.measure = object1.optString("measure");
                                matcher.function = object1.optString("function");
                                JSONArray params = object1.optJSONArray("params");
                                if (params != null) {
                                    matcher.params = new ArrayList<>();
                                    for (int x = 0; x < params.length(); x++) {
                                        matcher.params.add((String) params.opt(x));
                                    }
                                }
                                matcher.eventName = object1.optString("event_name");
                                JSONObject filter = object1.optJSONObject("filter");
                                if (filter != null) {
                                    matcher.filter = new Filter();
                                    matcher.filter.relation = filter.optString("relation");
                                    JSONArray jsonArray1 = filter.optJSONArray("conditions");
                                    if (jsonArray1 != null) {
                                        matcher.filter.conditionsList = new ArrayList<>();
                                        for (int l = 0; l < jsonArray1.length(); l++) {
                                            Condition condition = new Condition();
                                            JSONObject conditionObject = (JSONObject) jsonArray1.opt(l);
                                            condition.field = conditionObject.optString("field");
                                            condition.function = conditionObject.optString("function");
                                            JSONArray jsonArray2 = conditionObject.optJSONArray("params");
                                            condition.params = new ArrayList<>();
                                            if (jsonArray2 != null) {
                                                for (int m = 0; m < jsonArray2.length(); m++) {
                                                    condition.params.add(jsonArray2.opt(m));
                                                }
                                            }
                                            matcher.filter.conditionsList.add(condition);
                                        }
                                    }
                                }
                                popupPlan.patternPopup.matcherList.add(matcher);
                                if (!mGlobalData.eventPopupPlans.containsKey(matcher.eventName)) {
                                    ArrayList<PopupPlan> popupPlanLists = new ArrayList<>();
                                    popupPlanLists.add(popupPlan);
                                    mGlobalData.eventPopupPlans.put(matcher.eventName, popupPlanLists);
                                } else {
                                    List<PopupPlan> popupPlanLists = mGlobalData.eventPopupPlans.get(matcher.eventName);
                                    if (popupPlanLists != null && !popupPlanLists.contains(popupPlan)) {
                                        popupPlanLists.add(popupPlan);
                                    }
                                }
                            }
                        }
                    }
                    mGlobalData.popupPlans.add(popupPlan);
                }

                //服务器上计划如果被删除，本地要及时删除
                if (fromWhereLoad == MSG_LOAD_REMOTE_POPUP_PLANS) {
                    newIds.retainAll(oldIds);
                    oldIds.removeAll(newIds);
                    if (!oldIds.isEmpty()) {
                        for (long planId : oldIds) {
                            PopupPlan pp = mGlobalData.getPopupPlan(planId);
                            if (pp != null) {
                                mGlobalData.removeConvertPlan(pp);
                                mGlobalData.popupPlans.remove(pp);
                                for (Map.Entry<String, List<PopupPlan>> entry : mGlobalData.eventPopupPlans.entrySet()) {
                                    List<PopupPlan> pps = entry.getValue();
                                    pps.remove(pp);
                                }
                            }
                            if (runningGlobalObject != null) {
                                runningGlobalObject.remove("plan_" + planId);
                            }
                        }
                        if (runningGlobalObject != null) {
                            String running = runningGlobalObject.toString();
                            SFLog.d(TAG, "cached data:" + running);
                            Utils.saveJsonToFile(running, localFile);
                        }
                    }
                }
                if (mGlobalData != null && mGlobalData.getCachedGlobalData() == null) {
                    mGlobalData.setCachedGlobalData(runningGlobalObject);
                }
                for (CallBack callBack : callBacks) {
                    callBack.loadSuccess(jsonObject, mGlobalData);
                }
            }
        };
    }

    GlobalDataLoadThread addCallBack(CallBack callBack) {
        if (!callBacks.contains(callBack)) {
            callBacks.add(callBack);
        }
        return this;
    }

    public void onDistinctIdChange() {
        if (mHandler == null) {
            return;
        }
        if (mHandler.hasMessages(MSG_LOAD_LOCAL_POPUP_PLANS)) {
            mHandler.removeMessages(MSG_LOAD_LOCAL_POPUP_PLANS);
        }
        if (mHandler.hasMessages(MSG_LOAD_REMOTE_POPUP_PLANS)) {
            mHandler.removeMessages(MSG_LOAD_REMOTE_POPUP_PLANS);
        }
        mHandler.sendEmptyMessage(MSG_DISTINCT_ID_CHANGED);
    }

    @Override
    public void onEnterForeground(boolean resumeFromBackground) {
        mAppInForeground = true;
        if (mHandler != null) {
            mHandler.sendEmptyMessage(resumeFromBackground ? MSG_LOAD_REMOTE_POPUP_PLANS : MSG_LOAD_LOCAL_POPUP_PLANS);
        }
    }

    @Override
    public void onEnterBackground() {
        mAppInForeground = false;
        if (mHandler != null) {
            mHandler.removeMessages(MSG_LOAD_REMOTE_POPUP_PLANS);
        }
    }

    public interface CallBack {
        void loadSuccess(JSONObject data, GlobalData globalData);
    }

}
