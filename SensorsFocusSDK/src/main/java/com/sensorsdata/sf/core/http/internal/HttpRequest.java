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

import com.sensorsdata.sf.core.utils.SFLog;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final String TAG = "HttpRequest";
    private final String url;
    private final String method;
    private final Map<String, String> headers;
    private final Map<String, String> requestParameters;
    private final String body;
    private final int connectTimeout;
    private final int readTimeout;
    private final boolean useCaches;

    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.body = builder.body;
        this.requestParameters = builder.requestParameters;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.useCaches = builder.useCaches;
    }

    public String url() {
        return url;
    }

    public String method() {
        return method;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public String body() {
        return body;
    }

    public Map<String, String> requestParameters() {
        return requestParameters;
    }

    public int connectTimeout() {
        return connectTimeout;
    }

    public int readTimeout() {
        return readTimeout;
    }

    boolean isUseCaches() {
        return useCaches;
    }

    @Override
    public String toString() {
        return "HttpRequest{method = " + method + ", url = " + url;
    }

    public static class Builder {
        private String url;
        private String method;
        private Map<String, String> headers;
        private String body;
        private Map<String, String> requestParameters;
        private int connectTimeout;
        private int readTimeout;
        private boolean useCaches;

        public Builder() {
            method = "GET";
            headers = new HashMap<>();
            requestParameters = new HashMap<>();
            connectTimeout = 30000;
            readTimeout = 30000;
        }

        /*
         * 设置请求 URL 地址
         */
        public Builder url(String url) {
            if (url == null) {
                throw new NullPointerException("The value url is null.");
            }
            this.url = url;
            return this;
        }

        /*
         * GET 请求类型
         */
        public Builder get() {
            this.method = "GET";
            this.body = null;
            return this;
        }

        /*
         * POST 请求类型
         */
        public Builder post(String body) {
            this.method = "POST";
            this.body = body;
            return this;
        }

        /*
         * 设置网络请求的请求头
         */
        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        /*
         * 添加 HTTP 请求参数
         */
        public Builder requestParameters(Map<String, String> requestParameters) {
            this.requestParameters = requestParameters;
            return this;
        }

        /*
         * 添加 HTTP 请求参数
         */
        public Builder appendRequestParameter(String key, String value) {
            requestParameters.put(key, value);
            return this;
        }

        public Builder connectTimeout(int connectTimeout) {
            if (connectTimeout < 0) {
                SFLog.d(TAG, "connectTimeout < 0");
                connectTimeout = 30000;
            }
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(int readTimeout) {
            if (readTimeout < 0) {
                SFLog.d(TAG, "readTimeout < 0");
                readTimeout = 30000;
            }
            this.readTimeout = readTimeout;
            return this;
        }

        /*
         * 是否启用缓存
         */
        public Builder useCaches(boolean useCaches) {
            this.useCaches = useCaches;
            return this;
        }

        /*
         * 创建 HttPRequest 对象
         */
        public HttpRequest build() {
            if (this.url == null) {
                throw new IllegalStateException("The value url is null.");
            }
            return new HttpRequest(this);
        }
    }
}
