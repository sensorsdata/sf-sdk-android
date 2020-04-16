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

import com.sensorsdata.sf.ui.listener.PopupListener;

public class SFConfigOptions {
    private static final String TAG = "SFConfig";
    private String mApiBaseUrl;
    private PopupListener mPopupListener;

    public SFConfigOptions(String apiBaseUrl) {
        this.mApiBaseUrl = apiBaseUrl;
    }

    public SFConfigOptions setPopupListener(PopupListener popupListener) {
        this.mPopupListener = popupListener;
        return this;
    }

    String getApiBaseUrl() {
        return mApiBaseUrl;
    }

    PopupListener getPopupListener() {
        return mPopupListener;
    }

    @Override
    public String toString() {
        return TAG + "{ServerUrl = " + mApiBaseUrl + "}";
    }
}
