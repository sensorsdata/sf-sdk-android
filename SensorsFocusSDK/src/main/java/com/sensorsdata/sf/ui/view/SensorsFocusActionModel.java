/*
 * Created by dengshiwei on 2020/03/23.
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

package com.sensorsdata.sf.ui.view;

import org.json.JSONObject;

public enum SensorsFocusActionModel {
    CLOSE,
    OPEN_LINK,
    COPY,
    CUSTOMIZE;

    private String value;
    private JSONObject extra;

    SensorsFocusActionModel() {
    }

    public String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }

    public JSONObject getExtra() {
        return extra;
    }

    void setExtra(JSONObject extra) {
        this.extra = extra;
    }
}
