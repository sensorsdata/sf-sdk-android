/*
 * Created by dengshiwei on 2020/02/27.
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

package com.sensorsdata.sf.core.http;

import android.net.Uri;
import android.text.TextUtils;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.sf.android.sdk.BuildConfig;
import com.sensorsdata.sf.core.http.internal.HttpCall;
import com.sensorsdata.sf.core.http.internal.HttpCallBack;
import com.sensorsdata.sf.core.http.internal.HttpClient;
import com.sensorsdata.sf.core.http.internal.HttpRequest;
import com.sensorsdata.sf.core.http.internal.ResponseBody;
import com.sensorsdata.sf.core.utils.SFLog;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHelper {
    private static final String TAG = "HttpRequestHelper";
    private static HttpRequestHelper instance;
    private HttpClient mHttpClient;
    private String mBaseUrl;
    private Map<String, String> mProjectMap = new HashMap<>();
    private static final String PLATFORM_TYPE = "ANDROID";
    /* /user_popup_configs/{distinct_id}?platform={platform} 拉取全部弹窗配置信息*/
    private static final String POP_CONFIGS_URL = "%s/sfo/user_popup_configs/%s?project=%s&platform=%s&sdk_version=%s";
    /* /popup_displays/{popup_display_uuid} */
    private static final String POP_DISPLAY_STATE_URL = "%s/sfo/popup_displays?popup_display_uuids=%s&project=%s";
    /*  /popup_windows/{popup_window_id} 获取窗体信息 */
    private static final String POP_INFO_URL = "%s/sfo/popup_windows/%s?project=%s&platform=%s&sdk_version=%s";

    private HttpRequestHelper(String baseUrl) {
        mHttpClient = new HttpClient.Builder().build();
        this.mBaseUrl = baseUrl;
    }

    public static HttpRequestHelper shareInstance(String baseUrl) {
        if (instance == null) {
            instance = new HttpRequestHelper(baseUrl);
        }
        return instance;
    }

    public static HttpRequestHelper shareInstance() {
        if (instance == null) {
            SFLog.d(TAG, "The static method getInstance(String baseUrl) should be called before calling getInstance()");
        }
        return instance;
    }

    /**
     * 拉取全部弹窗配置信息
     *
     * @param distinct_id distinct_id
     * @return 弹窗信息
     */
    public String pullEventConfig(String distinct_id) {
        try {
            String httpUrl = String.format(POP_CONFIGS_URL, mBaseUrl, distinct_id, getProject(), PLATFORM_TYPE, BuildConfig.VERSION_NAME);
            SFLog.d(TAG, "PullEventConfig url = " + httpUrl);
            HttpRequest httpRequest = new HttpRequest.Builder()
                    .url(httpUrl).build();
            HttpCall httpCall = mHttpClient.makeHttpCall(httpRequest);
            ResponseBody responseBody = httpCall.submit();
            if (responseBody != null) {
                return responseBody.body();
            }
        } catch (Exception exception) {
            //ignore
        }
        return null;
    }

    /**
     * 同步查询转换状态
     *
     * @param popup_display_uuid 弹窗 id
     * @return 状态
     */
    public String pullWindowState(String popup_display_uuid) {
        try {
            String httpUrl = String.format(POP_DISPLAY_STATE_URL, mBaseUrl, popup_display_uuid, getProject());
            SFLog.d(TAG, "PullWindowState url = " + httpUrl);
            HttpRequest httpRequest = new HttpRequest.Builder()
                    .url(httpUrl)
                    .build();
            HttpCall httpCall = mHttpClient.makeHttpCall(httpRequest);
            ResponseBody responseBody = httpCall.submit();
            if (responseBody != null) {
                return responseBody.body();
            }
        } catch (Exception ex) {
            //ignore
        }
        return null;
    }

    /**
     * 拉取窗体信息
     *
     * @param popup_window_id 弹窗 id
     * @param httpCallBack 监听回调
     */
    public void pullWindowInfo(String popup_window_id, HttpCallBack httpCallBack) {
        try {
            String httpUrl = String.format(POP_INFO_URL, mBaseUrl, popup_window_id, getProject(), PLATFORM_TYPE, BuildConfig.VERSION_NAME);
            SFLog.d(TAG, "PullWindowInfo url = " + httpUrl);
            HttpRequest httpRequest = new HttpRequest.Builder()
                    .url(httpUrl)
                    .build();
            HttpCall httpCall = mHttpClient.makeHttpCall(httpRequest);
            httpCall.setHttpCallBack(httpCallBack);
            httpCall.execute();
        } catch (Exception ex) {
            //ignore
        }
    }

    private String getProject() {
        try {
            SensorsDataAPI sensorsDataAPI = SensorsDataAPI.sharedInstance();
            Class apiClass = sensorsDataAPI.getClass();
            Method method = apiClass.getDeclaredMethod("getServerUrl");
            method.setAccessible(true);
            String serverUrl = (String) method.invoke(sensorsDataAPI);
            if (!TextUtils.isEmpty(serverUrl)) {
                String project = mProjectMap.get(serverUrl);
                if (TextUtils.isEmpty(project)) {
                    Uri uri = Uri.parse(serverUrl);
                    project = uri.getQueryParameter("project");
                    SFLog.d(TAG, "http request project = " + project);
                    if (!TextUtils.isEmpty(project)) {
                        mProjectMap.put(serverUrl, project);
                        return project;
                    }
                } else {
                    return project;
                }
            }
        } catch (Exception e) {
            SFLog.printStackTrace(e);
        }
        return "default";
    }
}
