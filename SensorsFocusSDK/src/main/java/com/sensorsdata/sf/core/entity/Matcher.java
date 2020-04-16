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
package com.sensorsdata.sf.core.entity;

import com.sensorsdata.sf.core.window.Window;

import java.util.List;

public class Matcher {
    public String type;

    // 现在只有自然天，是对齐的固定窗口，开始时间由特定事件决定，对齐到天，单个规则不允许重叠，自然.
    // 以后对齐不对其都支持
    // 表示 一天内某个事件最多触发几次
    public Window eventWindow;
    public String eventName;
    public Filter filter;
    public String measure;
    public String function;
    public List<String> params;

    @Override
    public String toString() {
        return "Matcher{" +
                "eventName='" + eventName + '\'' +
                ", params=" + params +
                ", eventWindow=" + eventWindow +
                ", type='" + type + '\'' +
                ", filter=" + filter +
                ", measure='" + measure + '\'' +
                ", function='" + function + '\'' +
                '}';
    }
}
