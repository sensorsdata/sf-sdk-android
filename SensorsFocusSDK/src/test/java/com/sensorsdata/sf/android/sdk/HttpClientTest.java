/*
 * Created by dengshiwei on 2020/02/26.
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

package com.sensorsdata.sf.android.sdk;

import com.sensorsdata.sf.core.http.internal.HttpCall;
import com.sensorsdata.sf.core.http.internal.HttpCallBack;
import com.sensorsdata.sf.core.http.internal.HttpClient;
import com.sensorsdata.sf.core.http.internal.HttpRequest;
import com.sensorsdata.sf.core.http.internal.ResponseBody;

import org.junit.Test;

public class HttpClientTest {

    @Test
    public void httpClient() {
        HttpClient httpClient = new HttpClient.Builder().build();

        // response=200 url = http://route.showapi.com/119-42
        HttpRequest httpRequest =
                new HttpRequest.Builder().url("http://route.showapi.com/119-42")
                        .readTimeout(10_000)
                        .connectTimeout(10_000)
                        .get().build();
        HttpCall httpCall = httpClient.makeHttpCall(httpRequest);
        httpCall.setHttpCallBack(new HttpCallBack() {
            @Override
            public void onSuccess(HttpCall httpCall, ResponseBody responseBody) {
                System.out.println("request1onSuccess:code = " + responseBody.code + "\n body = " + responseBody.body());
            }

            @Override
            public void onFailure(HttpCall httpCall, ResponseBody responseBody) {
                System.out.println("request1onFailure:code = " + responseBody.code + "\n body = " + responseBody.body());
            }
        });
        httpCall.execute();

        // response = 400 url = https://www.sensorsdata.cn/blog/2020022612121221/
        HttpRequest httpRequest1 = new HttpRequest.Builder().url("http://img.showapi.com/201107/5/099368663.jpg").build();
        HttpCall httpCall1 = httpClient.makeHttpCall(httpRequest1);
        httpCall1.setHttpCallBack(new HttpCallBack() {
            @Override
            public void onSuccess(HttpCall httpCall, ResponseBody responseBody) {
                System.out.println("request2onSuccess:code = " + responseBody.code + "\n body = " + responseBody.body());
            }

            @Override
            public void onFailure(HttpCall httpCall, ResponseBody responseBody) {
                System.out.println("request2onFailure:code = " + responseBody.code + "\n body = " + responseBody.body());
            }
        });
        httpCall1.execute();

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
