/*
 * Created by dengshiwei on 2020/03/11.
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

package com.sensorsdata.sf.ui.listener;

import com.sensorsdata.sf.ui.view.SensorsFocusActionModel;

public interface PopupListener {
    /**
     * 弹窗加载成功
     * @param planId 计划 ID
     */
    void onPopupLoadSuccess(String planId);

    /**
     * 弹窗加载失败
     * @param planId 计划 ID
     * @param errorCode 错误码，取值范围 1000、1001
     * @param errorMessage 错误信息
     */
    void onPopupLoadFailed(String planId, int errorCode, String errorMessage);

    /**
     * 弹窗点击
     * @param planId 计划 ID
     * @param actionModel 操作指令
     */
    void onPopupClick(String planId, SensorsFocusActionModel actionModel);

    /**
     * 弹窗关闭
     * @param planId 计划 ID
     */
    void onPopupClose(String planId);
}
