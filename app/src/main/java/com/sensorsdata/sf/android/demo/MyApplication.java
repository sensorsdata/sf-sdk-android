/*
 * Created by dengshiwei on 2020/03/05.
 * Copyright 2015－2022 Sensors Data Inc.
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

package com.sensorsdata.sf.android.demo;

import android.app.Application;
import android.util.Log;

import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.sf.core.SFConfigOptions;
import com.sensorsdata.sf.core.SensorsFocusAPI;
import com.sensorsdata.sf.core.entity.SFCampaign;
import com.sensorsdata.sf.ui.listener.PopupListener;
import com.sensorsdata.sf.ui.listener.SensorsFocusCampaignListener;
import com.sensorsdata.sf.ui.view.SensorsFocusActionModel;

import org.json.JSONObject;


public class MyApplication extends Application {

    private static final String TAG = "SF.MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        PopDialogUtils.getInstance(this);
        initSensorsDataAPI();
        initSensorsFocusApi();
    }

    /**
     * 初始化 Sensors Analytics SDK
     */
    private void initSensorsDataAPI() {
        // 数据接收的 URL
        final String SA_SERVER_URL = "";

        //设置 SAConfigOptions，传入数据接收地址 SA_SERVER_URL
        SAConfigOptions saConfigOptions = new SAConfigOptions(SA_SERVER_URL);

        //通过 SAConfigOptions 设置神策 SDK，每个条件都非必须，开发者可根据自己实际情况设置，更多设置可参考 SAConfigOptions 类中方法注释
        saConfigOptions.setAutoTrackEventType(SensorsAnalyticsAutoTrackEventType.APP_CLICK | // 开启全埋点点击事件
                SensorsAnalyticsAutoTrackEventType.APP_START |      //开启全埋点启动事件
                SensorsAnalyticsAutoTrackEventType.APP_END |        //开启全埋点退出事件
                SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN)     //开启全埋点浏览事件
                .enableLog(true)        //开启神策调试日志，默认关闭(调试时，可开启日志)。
                .enableTrackAppCrash();     //开启 crash 采集
        //需要在主线程初始化神策 SDK
        SensorsDataAPI.startWithConfigOptions(this, saConfigOptions);
        SensorsDataAPI.sharedInstance().enableLog(true);
    }

    private void initSensorsFocusApi() {
        final String apiBaseUrl = "";
        SensorsFocusAPI.startWithConfigOptions(this, new SFConfigOptions(apiBaseUrl)
                .setCampaignListener(new SensorsFocusCampaignListener() {
                    @Override
                    public boolean campaignShouldStart(SFCampaign sfCampaign) {
                        Log.d(TAG, "onCampaignShouldStart: " + sfCampaign.toString());
                        return true;
                    }

                    @Override
                    public void onCampaignStart(SFCampaign sfCampaign) {
                        Log.d(TAG, "onCampaignStart: " + sfCampaign.toString());
                    }

                    @Override
                    public void onCampaignEnd(SFCampaign sfCampaign) {
                        Log.d(TAG, "onCampaignEnd" + sfCampaign.toString());
                    }

                    @Override
                    public void onCampaignFailed(SFCampaign sfCampaign, int errorCode, String errorMessage) {
                        Log.d(TAG, "SA.campaignFailed : errorCode：" + errorCode
                                + "，errorMessage：" + errorMessage + "sfCampaign:" +
                                sfCampaign.toString());
                    }
                })
                .setPopupListener(new PopupListener() {
                    /**
                     * 弹窗加载成功
                     */
                    @Override
                    public void onPopupLoadSuccess(String planId) {
                        Log.d(TAG, "onPopupLoadSuccess: planId" + planId);

                    }

                    @Override
                    public void onPopupLoadFailed(String planId, int errorCode, String errorMessage) {
                        Log.d(TAG, "onPopupLoadFailed: planId" + planId + "errorCode " + errorCode +
                                "errorMessage" + errorMessage);
                    }

                    /**
                     * 弹窗点击
                     */
                    @Override
                    public void onPopupClick(String planId, SensorsFocusActionModel actionModel) {
                        Log.d("SA.onPopupClick", "planId" + planId);
                        switch (actionModel) {
                            case OPEN_LINK:
                                // 自定义处理打开链接操作
                                String url = actionModel.getValue();
                                Log.d(TAG, "PopupClick: url = " + url);
                                break;
                            case COPY:
                                // 自定义处理复制操作
                                String copyText = actionModel.getValue();
                                Log.d(TAG, "PopupClick: copyText = " + copyText);
                                break;
                            case CLOSE:
                                // 处理 close
                                Log.d(TAG, "PopupClick: close");
                                break;
                            case CUSTOMIZE:
                                // 处理自定义操作
                                JSONObject customizeJson = actionModel.getExtra();
                                Log.d(TAG, "PopupClick: customize" + customizeJson.toString());
                                break;
                        }
                    }

                    /**
                     * 弹窗关闭
                     */
                    @Override
                    public void onPopupClose(String planId) {
                        Log.d(TAG, "onPopupClose:" + planId);
                    }
                }));
    }
}

