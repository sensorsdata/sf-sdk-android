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

public class HttpClient {
    private static final String TAG = "HttpClient";
    private final Dispatcher dispatcher;

    private HttpClient(Builder builder) {
        this.dispatcher = builder.dispatcher;
    }

    public HttpCall makeHttpCall(HttpRequest httpRequest) {
        return new HttpCall(this, httpRequest);
    }

    Dispatcher dispatcher() {
        return dispatcher;
    }

    public static final class Builder {
        private Dispatcher dispatcher;

        public Builder() {
            dispatcher = new Dispatcher();
        }

        public HttpClient build() {
            return new HttpClient(this);
        }
    }
}
