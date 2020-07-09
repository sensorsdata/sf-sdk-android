/*
 * Created by dengshiwei on 2020/03/10.
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

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.sensorsdata.analytics.android.sdk.BuildConfig;
import com.sensorsdata.analytics.android.sdk.SALog;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.listener.SAEventListener;
import com.sensorsdata.sf.core.entity.GlobalData;
import com.sensorsdata.sf.core.entity.PopupPlan;
import com.sensorsdata.sf.core.http.HttpRequestHelper;
import com.sensorsdata.sf.core.utils.SFLog;
import com.sensorsdata.sf.core.utils.Utils;
import com.sensorsdata.sf.ui.listener.PopupListener;
import com.sensorsdata.sf.ui.view.DynamicViewJsonBuilder;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SensorsFocusAPI implements ISensorsFocusAPI {
    private static final String TAG = "SensorsFocusAPI";
    private SFConfigOptions mSFConfigOptions;
    private Context mContext;
    private static SensorsFocusAPI instance;
    private GlobalData mGlobalData;
    private AppStateManager mAppStateManager;
    private GlobalDataLoadThread mGlobalDataLoadThread;

    SensorsFocusAPI() {
    }

    private SensorsFocusAPI(Context context, SFConfigOptions sfConfigOptions) {
        try {
            this.mContext = context.getApplicationContext();
            this.mSFConfigOptions = sfConfigOptions;
            String baseUrl = mSFConfigOptions.getApiBaseUrl();
            if (baseUrl == null) {
                SFLog.e(TAG, "The baseUrl of SFConfigOptions is null.");
                return;
            }
            if (baseUrl.trim().equals("")) {
                SFLog.e(TAG, "The baseUrl of SFConfigOptions is empty.");
                return;
            }
            HttpRequestHelper.shareInstance(baseUrl);
            if (mContext instanceof Application) {
                Application application = (Application) mContext;
                mAppStateManager = new AppStateManager();
                application.registerActivityLifecycleCallbacks(mAppStateManager);
            }
            loadConfig();
            setSAEventListener();
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }

    /**
     * SDK 初始化
     *
     * @param context Context
     * @param sfConfigOptions SFConfig
     */
    public static void startWithConfigOptions(Context context, SFConfigOptions sfConfigOptions) {
        if (context == null) {
            SFLog.e(TAG, "Context should not be null.", null);
            return;
        }
        if (sfConfigOptions == null) {
            SFLog.e(TAG, "SFConfigOptions should not be null.", null);
            return;
        }

        SFLog.syncAppState(context);
        if (!checkSAVersion()) {
            return;
        }

        if (instance == null) {
            instance = new SensorsFocusAPI(context, sfConfigOptions);
        }
    }

    public PopupListener getWindowListener() {
        try {
            return mSFConfigOptions.getPopupListener();
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return null;
    }

    PopupListener internalWindowListener;
    public void setInternalWindowListener(PopupListener popupListener){
        this.internalWindowListener = popupListener;
    }

    public PopupListener getInternalWindowListener() {
        return internalWindowListener;
    }

    /**
     * 获取 SensorsFocusAPI 实例
     *
     * @return SensorsFocusAPI 实例
     */
    public static ISensorsFocusAPI shareInstance() {
        if (instance == null) {
            SFLog.d(TAG, "You should call startWithConfigOptions(Context context, SFConfig sfConfig) first.");
            return new SensorsFocusAPIEmpty();
        }
        return instance;
    }

    private void loadConfig() {
        mGlobalDataLoadThread = new GlobalDataLoadThread("sensors_focus_popup_plans", mContext);
        mGlobalDataLoadThread.addCallBack(new GlobalDataLoadThread.CallBack() {
            @Override
            public void loadSuccess(JSONObject data, GlobalData globalData) {
                mGlobalData = globalData;
                mAppStateManager.addAppStateChangedListener(mGlobalData);
                DynamicViewJsonBuilder.preLoadImage(mContext, globalData);
            }
        }).start();
        if (mAppStateManager != null) {
            mAppStateManager.addAppStateChangedListener(mGlobalDataLoadThread);
        }
    }

    public PopupPlan getPopupPlan(long planId) {
        if (mGlobalData == null) {
            return null;
        }
        return mGlobalData.getPopupPlan(planId);
    }

    private void setSAEventListener() {
        SensorsDataAPI.sharedInstance(mContext).addEventListener(new SAEventListener() {
            @Override
            public void trackEvent(JSONObject jsonObject) {
                if (mGlobalData == null || jsonObject == null) {
                    SFLog.d(TAG, "GlobalData is null");
                    return;
                }
                try {
                    String type = jsonObject.getString("type");
                    if (!TextUtils.equals(type, "track")) {
                        return;
                    }
                    String eventName = jsonObject.getString("event");
                    if (!mGlobalData.eventPopupPlans.containsKey(eventName)) {
                        SFLog.d(TAG, "Plan json not contains " + eventName);
                        return;
                    }
                    List<PopupPlan> pps = mGlobalData.eventPopupPlans.get(eventName);
                    if (pps == null) {
                        return;
                    }
                    Collections.sort(pps, new Comparator<PopupPlan>() {
                        @Override
                        public int compare(PopupPlan o1, PopupPlan o2) {
                            int priorityDifference = o2.absolutePriority - o1.absolutePriority;
                            if (priorityDifference == 0) {
                                long idPriorityDifference = o2.planId - o1.planId;
                                if (idPriorityDifference != 0) {
                                    return idPriorityDifference > 0 ? 1 : -1;
                                }
                            }
                            return priorityDifference;
                        }
                    });
                    PlanManager.TriggerPopupPlans(mGlobalData, mContext, pps, jsonObject, mAppStateManager);

                } catch (Exception e) {
                    SFLog.printStackTrace(e);
                }
            }

            @Override
            public void login() {
                if (mGlobalDataLoadThread != null) {
                    mGlobalDataLoadThread.onDistinctIdChange();
                }
            }

            @Override
            public void logout() {
                if (mGlobalDataLoadThread != null) {
                    mGlobalDataLoadThread.onDistinctIdChange();
                }
            }

            @Override
            public void identify() {
                try {
                    if (mGlobalDataLoadThread != null && TextUtils.isEmpty(SensorsDataAPI.sharedInstance().getLoginId())) {
                        mGlobalDataLoadThread.onDistinctIdChange();
                    }
                } catch (Exception e) {
                    SALog.printStackTrace(e);
                }
            }

            @Override
            public void resetAnonymousId() {
                if (mGlobalDataLoadThread != null) {
                    mGlobalDataLoadThread.onDistinctIdChange();
                }
            }
        });
    }

    private static boolean checkSAVersion() {
        try {
            final String requiredVersion = "4.0.3";
            SensorsDataAPI sensorsDataAPI = SensorsDataAPI.sharedInstance();
            Field field = sensorsDataAPI.getClass().getDeclaredField("VERSION");
            field.setAccessible(true);
            String version = (String) field.get(sensorsDataAPI);
            String compareVersion = version;
            if (!TextUtils.isEmpty(version)) {
                if (version.contains("-")) {
                    compareVersion = compareVersion.substring(0, compareVersion.indexOf("-"));
                }
                if (!Utils.isVersionValid(compareVersion, requiredVersion)) {
                    SFLog.e(TAG, "当前神策 Android 埋点 SDK 版本 " + version + " 过低，请升级至 " + requiredVersion + " 及其以上版本后进行使用");
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
