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

import android.os.SystemClock;
import android.text.TextUtils;

import com.sensorsdata.sf.core.window.ConvertWindow;
import com.sensorsdata.sf.core.window.Window;

import org.json.JSONObject;


public class PopupPlan {
    public long planId;
    /**
     * 是否受众
     */
    public boolean isAudience;

    /**
     * 是否是对照组
     */
    public boolean isControlGroup;
    public Long audienceId;
    /**
     * status 可能的取值如下，其中 SDK 只需要关心 ACTIVE、SUSPEND 其他状态 SDK 是观查不到的.
     * SDK 会关心的状态：
     * ACTIVE 运行状态
     * SUSPEND 挂起状态/暂停状态
     * SDK 无需关心的状态（同时也感知不到）：
     * PENDING 待审批状态
     * DRAFT 草稿状态，只有自动化流程有
     * CLOSED '到期状态'
     * 单次计划到达发送时间即到期/重复定时计划和触发型计划到期时间为 schedule_end
     * 这里实际上需要给单次计划受众加载缓冲时间
     * 到期状态的定义，过了到期时间，就不再有受众用户进入计划了
     * FINISHED 计划停止所有数据计算转化
     * 计划到期时候，因为计划有很多转化窗口时间配置
     * 简单的以运营计划为例子
     * 1.触发 A 未完成 B，有触发 A 未完成 B 的转化时间窗口
     * 2.用户触发了计划，可以配置延迟一段时间给用户触达，这里有延迟时间窗口
     * 3.勿扰配置，有勿扰时间段，可以配置待勿扰时间结束立刻发送，这里有勿扰等待时间窗口
     * 4.目标转化时间窗口 -- 所有目标取最大的目标时间转化窗口
     * 计划结束的定义是，计划数据指标，如目标转化人次，在计划结束之后都不在会变动了
     */
    public String status;

    public boolean isActive() {
        return TextUtils.equals(status, "ACTIVE");
    }

    public String cname;
    public JSONObject popupWindowContent;
    /**
     * 优先级
     */
    public int absolutePriority;
    /**
     * 会导致 last_update_config_time 更新的原因：
     * 触发规则更新.
     * 受众规则更新.
     * 窗体内容更新.
     * 不会导致 last_update_config_time 更新的原因：
     * 计划的显示名修改、所属的组修改等.
     */
    public long lastUpdateConfigTime;
    /**
     * 计划有效期截至时间戳
     */
    public long expireAt;

    /**
     * 是否过期
     * @return 是否过期
     */
    public boolean isExpired() {
        return SystemClock.currentThreadTimeMillis() >= expireAt;
    }

    /**
     * 计划内参与限制窗口
     * 一段时间内，重复弹窗的次数
     */
    public Window planReEntryWindow;
    /**
     * 计划内弹窗间隔窗口
     */
    public Window planIntervalWindow;
    /**
     * 指定页面视图
     */
    public JSONObject pageFilter;
    /**
     * 具体的弹窗规则
     */
    public PatternPopup patternPopup;

    /**
     * 转化窗口
     */
    public ConvertWindow convertWindow;

    /**
     * 全局触达配置开关
     */
    public boolean enableGlobalMsgLimit;

    @Override
    public String toString() {
        return "PopupPlan{" +
                "planId=" + planId +
                ", cname='" + cname + '\'' +
                ", patternPopup=" + patternPopup +
                ", reEntry=" + planReEntryWindow +
                ", convertWindow=" + convertWindow +
                ", planIntervalWindow=" + planIntervalWindow +
                ", isAudience=" + isAudience +
                ", isControlGroup=" + isControlGroup +
                ", audienceId=" + audienceId +
                ", status='" + status + '\'' +
                ", absolutePriority=" + absolutePriority +
                ", lastUpdateConfigTime=" + lastUpdateConfigTime +
                ", expireAt=" + expireAt +
                ", pageFilter=" + pageFilter +
                ", enableGlobalMsgLimit=" + enableGlobalMsgLimit +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        PopupPlan pp = (PopupPlan) obj;
        return pp.planId == planId;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(planId + "");
    }
}
