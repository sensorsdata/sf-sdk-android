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
package com.sensorsdata.sf.core.window;


/**
 * 滑动窗口，对齐（到天、周、月），自然.
 */
public class Limit extends Window {
    // 自然天、周、月是哪天，格式是 yyyy-MM-dd
    public long before;

    //limit 为集合类型的列表，triggerCount 代表满足全局弹窗某个限制弹窗总的数量
    public int triggerCount = 0;

}
