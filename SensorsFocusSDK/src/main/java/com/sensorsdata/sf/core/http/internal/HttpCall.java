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

package com.sensorsdata.sf.core.http.internal;

import android.text.TextUtils;

import com.sensorsdata.sf.core.utils.SFLog;
import com.sensorsdata.sf.core.utils.TipUtils;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

public class HttpCall implements Runnable {
    private static final String TAG = "HttpCall";
    private final HttpClient httpClient;
    private HttpRequest httpRequest;
    private HttpCallBack httpCallBack;

    HttpCall(HttpClient httpClient, HttpRequest httpRequest) {
        this.httpClient = httpClient;
        this.httpRequest = httpRequest;
    }

    /**
     * 设置 Http 网络请求回调
     *
     * @param httpCallBack 请求回调
     */
    public void setHttpCallBack(HttpCallBack httpCallBack) {
        this.httpCallBack = httpCallBack;
    }

    /**
     * 执行网络请求
     */
    public void execute() {
        this.httpClient.dispatcher().enqueue(this);
    }

    /**
     * 阻塞执行获取网络请求
     * @return 请求结果
     */
    public ResponseBody submit() {
        return this.httpClient.dispatcher().submit(new Callable<ResponseBody>() {
            @Override
            public ResponseBody call() throws Exception {
                return sendHttpRequest();
            }
        });
    }

    @Override
    public void run() {
        if (httpCallBack != null) {
            ResponseBody responseBody = sendHttpRequest();
            SFLog.d(TAG, "http result:" + responseBody.toString());
            int code = responseBody.code;
            if (code == 200) {
                httpCallBack.onSuccess(this, responseBody);
            } else {
                httpCallBack.onFailure(this, responseBody);
            }
        }
    }

    /**
     * 发送网络请求
     */
    private ResponseBody sendHttpRequest() {
        ResponseBody responseBody = new ResponseBody();
        HttpURLConnection httpURLConnection = null;
        String httpUrl = buildHttpUrl();
        try {
            URL url = new URL(httpUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            buildHttpHeaders(httpURLConnection);
            httpURLConnection.connect();
            int errorCode = httpURLConnection.getResponseCode();
            responseBody.code = errorCode;
            responseBody.contentLength = httpURLConnection.getContentLength();
            if (errorCode == 200) {
                responseBody.inputStream = httpURLConnection.getInputStream();
            } else {
                responseBody.errorStream = httpURLConnection.getErrorStream();
            }
        } catch (MalformedURLException ex) {
            SFLog.d(TAG, "Server URL = " + httpUrl + " 无效，请重新设置");
            handleException(responseBody, httpURLConnection, ex);
        } catch (Exception ex) {
            handleException(responseBody, httpURLConnection, ex);
        }
        return responseBody;
    }

    private void handleException(ResponseBody responseBody, HttpURLConnection httpURLConnection, Exception ex) {
        if (httpCallBack != null) {
            if (httpURLConnection != null) {
                responseBody.errorStream = httpURLConnection.getErrorStream();
            } else {
                responseBody.body = ex.getMessage();
            }
        }
    }

    /**
     * 拼接 URL 地址
     *
     * @return URL 地址
     */
    private String buildHttpUrl() {
        StringBuilder urlBuilder = new StringBuilder();
        if (httpRequest.url().contains("?")) {
            urlBuilder.append(httpRequest.url()).append("&");
        } else {
            urlBuilder.append(httpRequest.url()).append("?");
        }
        for (String key : httpRequest.requestParameters().keySet()) {
            urlBuilder.append(key).append("=").append(httpRequest.requestParameters().get(key)).append("&");
        }
        String url = urlBuilder.toString();
        return url.substring(0, url.length() - 1);
    }

    /**
     * 拼接 HttpURLConnection 请求头
     *
     * @param httpURLConnection HttpURLConnection
     */
    private void buildHttpHeaders(HttpURLConnection httpURLConnection) throws ProtocolException {
        for (String header : httpRequest.headers().keySet()) {
            httpURLConnection.setRequestProperty(header, httpRequest.headers().get(header));
        }
        httpURLConnection.setReadTimeout(httpRequest.readTimeout());
        httpURLConnection.setConnectTimeout(httpRequest.connectTimeout());
        httpURLConnection.setDoInput(true);
        httpURLConnection.setUseCaches(httpRequest.isUseCaches());
        if ("GET".equals(httpRequest.method())) {
            httpURLConnection.setRequestMethod("GET");
        } else if ("POST".equals(httpRequest.method())) {
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            try {
                if (!TextUtils.isEmpty(httpRequest.body())) {
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                    bufferedOutputStream.write(httpRequest.body().getBytes(Charset.forName("UTF-8")));
                    bufferedOutputStream.flush();
                    outputStream.close();
                    bufferedOutputStream.close();
                }
            } catch (Exception ex) {
                //ignore
            }
        }
    }
}
