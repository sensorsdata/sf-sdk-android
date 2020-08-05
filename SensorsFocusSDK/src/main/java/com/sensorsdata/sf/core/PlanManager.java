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
package com.sensorsdata.sf.core;

import android.content.Context;
import android.text.TextUtils;

import com.sensorsdata.sf.core.entity.Condition;
import com.sensorsdata.sf.core.entity.GlobalData;
import com.sensorsdata.sf.core.entity.Matcher;
import com.sensorsdata.sf.core.entity.PatternPopup;
import com.sensorsdata.sf.core.entity.PopupPlan;
import com.sensorsdata.sf.core.utils.PropertyExpression;
import com.sensorsdata.sf.core.utils.SFLog;
import com.sensorsdata.sf.core.utils.TipUtils;
import com.sensorsdata.sf.ui.listener.PopupListener;
import com.sensorsdata.sf.ui.view.DynamicViewJsonBuilder;
import com.sensorsdata.sf.ui.view.SensorsFocusActionModel;

import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

class PlanManager {
    private static final String TAG = "PlanManager";

    static boolean loadFailed = false;

    static void TriggerPopupPlans(final GlobalData globalData, Context context, List<PopupPlan> pps, JSONObject jsonObject, AppStateManager appStateManager) {
        SFLog.d(TAG, jsonObject.toString());
        //标记某个弹窗被触发或显示，优先级高的弹窗计划被触发，其他计划要走优先级抑制过程
        boolean isTriggerPopupPlan = false;
        boolean isShouldSaveData = false;
        for (final PopupPlan p : pps) {
            SFLog.d(TAG, "****** Start Trigger Popup Plan ******");
            SFLog.d(TAG, p.toString());
            //检查是否受众
            // 不管是不是受众，计划配置都返回.
            // 通过is_audience 字段，区分是否 “今天”是受众.
            // 考虑各种窗口跨多天的情况，is_audience 发生变更，不应当删除本地计划状态.
            // is_audience 不改变 last_update_config_time
            // 计算规则参照详细设计.
            if (!p.isAudience) {
                SFLog.d(TAG, "You are not audience.");
                continue;
            }
            //检查计划是否启动
            if (!p.isActive()) {
                SFLog.d(TAG, "Plan is paused.");
                continue;
            }
            //检查计划是否过期
            if (p.isExpired()) {
                SFLog.d(TAG, "Plan is expired:" + p.expireAt);
                continue;
            }
            //3.检查：转化窗口 is_finished 检查，若到达 end_time，则删除窗口
            //4.检查：转化窗口 in_window 检查，若存在转化窗口，且 in_window 为真，则结束流程.特别地，转化窗口的结束，还可能由一个后台轮询检查转化完毕的线程所关闭

            if (!globalData.isConvertSuccess(p)) {
                SFLog.d(TAG, "Convert Window is not finished:" + p.convertWindow);
                continue;
            }

            // 5.检查：全局弹窗间隔窗口 is_finished 检查，若达到 end_time，则删除窗口
            // 6.检查：全局弹窗间隔窗口 in_window 检查. 若存在此窗口，且 in_window 为真，则结束流程.

            if (globalData.globalIntervalWindow != null && !globalData.globalIntervalWindow.isFinished()) {
                SFLog.d(TAG, "Global Interval Window is not finished:" + globalData.globalIntervalWindow);
                continue;
            }

            //7.检查：计划内弹窗间隔窗口 is_finished 检查，若达到 end_time，则删除窗口
            //8.检查：计划内弹窗间隔窗口 in_window 检查. 若存在此窗口，且 in_window 为真，则结束流程.

            if (p.planIntervalWindow != null && !p.planIntervalWindow.isFinished()) {
                SFLog.d(TAG, "Plan Interval Window is not finished:" + p.planIntervalWindow);
                continue;
            }

            //9.触发判断：触发窗口 is_finished 检查，对于所有存在的触发窗口，若到达 end_time，则删除它
            //10.触发判断：用触发规则，依次判断事件是否符合条件，
            //11 触发判断：检查匹配成功的规则列表 r_matched 是否具有窗口. 尚无窗口的规则为 r_no_window	触发判断
            //12 触发判断：对于 r_no_window 的规则，创建窗口，设置始末时间，计次为0，触发判断
            //13 触发判断：对于 r_matched，重新取出所有对应的窗口为 w_matched，依次做计次 + 1 触发判断
            PatternPopup pp = p.patternPopup;
            if (pp == null) {
                SFLog.d(TAG, "PatternPopup is null，it should not happen.");
                continue;
            }

            JSONObject planJSONObject = new JSONObject();
            try {
                planJSONObject = globalData.cachedGlobalData.getJSONObject("plan_" + p.planId);
            } catch (Exception e) {
                //SFLog.printStackTrace(e);
            }

            List<Matcher> matchers = pp.matcherList;
            //表示 matcher 中是否有触发次数达到阀值的
            boolean isHaveArrivalTriggerCount = false;
            for (int i = 0; i < matchers.size(); i++) {
                Matcher matcher = matchers.get(i);
                // 事件属性是否匹配规则
                if (!isMatchPattern(jsonObject, matcher)) {
                    continue;
                }
                //规则匹配成功后，触发窗口计次++，返回触发窗口计次是否达到阀值
                if (triggerTimesCount(matcher)) {
                    isHaveArrivalTriggerCount = true;
                }
                SFLog.d(TAG, "matcher index=" + i + ", arrivalTriggerCount=" + isHaveArrivalTriggerCount + ", matcher=" + matcher);
                isShouldSaveData = true;
                try {
                    JSONObject matcherJSONObject = new JSONObject();
                    matcherJSONObject.put("start", matcher.eventWindow.getStartTime());
                    matcherJSONObject.put("count", matcher.eventWindow.getCount());
                    planJSONObject.put("matcher_" + i, matcherJSONObject);
                } catch (Exception e) {
                    SFLog.printStackTrace(e);
                }
            }

            if (isShouldSaveData) {
                try {
                    if (globalData.getCachedGlobalData() == null) {
                        globalData.setCachedGlobalData(new JSONObject());
                    }
                    globalData.getCachedGlobalData().put("plan_" + p.planId, planJSONObject);
                    /*int version = globalData.getCachedGlobalData().optInt("version", 0);
                    if (version == 0) {
                        globalData.getCachedGlobalData().put("version", 1);
                    }*/
                } catch (Exception e) {
                    SFLog.printStackTrace(e);
                }
            }

            //14 触发判断：对于 w_matched，检查计次是否到达阈值，若未达到阈值，则结束流程
            if (!isHaveArrivalTriggerCount) {
                SFLog.d(TAG, "It is not arrival trigger count.");
                continue;
            }

            // 有弹窗要显示了，其他计划走优先级抑制过程
            if (isTriggerPopupPlan) {
                SFLog.d(TAG, "Other plan trigger, continue.");
                continue;
            }

            final long now = System.currentTimeMillis();

            //16 全局弹窗限制判断，不满足则结束流程。注意，全局弹窗限制，均为 “自然” 窗口。
            if (globalData.globalPopupLimit != null && globalData.globalPopupLimit.isInUse && p.enableGlobalMsgLimit) {
                globalData.globalPopupLimit.setStartTime(now);
                SFLog.d(TAG, "Global Popup Limit:" + globalData.globalPopupLimit);
                if (!globalData.globalPopupLimit.isMatcher()) {
                    SFLog.d(TAG, "Global Popup Limit, continue.");
                    continue;
                }
            }

            //17 计划内参与限制窗口判断，先检查窗口是否过期，过期删除，若存在窗口，则检查窗口条件是否满足，不满足则结束流程.
            if (p.planReEntryWindow != null && !p.planReEntryWindow.isFinished() && p.planReEntryWindow.getCount() >= p.planReEntryWindow.limit) {
                SFLog.d(TAG, "ReEntry Window=" + p.planReEntryWindow);
                continue;
            }

            //该计划要弹窗了，其他计划走优先级抑制过程
            isTriggerPopupPlan = true;

            if (appStateManager != null) {
                if (!appStateManager.isAppInForeground() || appStateManager.isActivityFinishing()) {
                    SFLog.d(TAG, "App is background, Don't show window. isFinishing = " + appStateManager.isActivityFinishing());
                    continue;
                }
            }

            String uuid = UUID.randomUUID().toString();

            //准备启动转化窗口
            if (p.convertWindow != null) {
                p.convertWindow.setUUID(uuid);
            }

            ((SensorsFocusAPI) SensorsFocusAPI.shareInstance()).setInternalWindowListener(new PopupListener() {
                @Override
                public void onPopupLoadSuccess(String planId) {
                    loadFailed = false;
                }

                @Override
                public void onPopupLoadFailed(String planId, int errorCode, String errorMessage) {
                    SFLog.d(TAG, "onPopupLoadFailed, planId=" + planId + "，errorCode=" + errorCode);
                    if (errorCode == TipUtils.ACTIVITY_IN_BACKGROUND_FINISH) {
                        loadFailed = true;
                    }
                }

                @Override
                public void onPopupClick(String planId, SensorsFocusActionModel actionModel) {

                }

                @Override
                public void onPopupClose(String planId) {
                    long currentTimeMillis = System.currentTimeMillis();
                    //开始全局间隔窗口，区间为 [now, now + 间隔时长]
                    if (globalData.globalIntervalWindow != null) {
                        globalData.globalIntervalWindow.setStartTime(currentTimeMillis);
                    }

                    //开始间隔窗口，区间为 [now, now + 间隔时长]
                    if (p.planIntervalWindow != null) {
                        p.planIntervalWindow.setStartTime(currentTimeMillis);
                    }
                }
            });

            new DynamicViewJsonBuilder(context, appStateManager, String.valueOf(p.planId)).showDialog();
            if (loadFailed) {
                loadFailed = false;
                continue;
            }
            SFLog.d(TAG, "Window will showing.");
            JSONObject reEntry = new JSONObject();
            try {
                JSONObject convert = new JSONObject();
                convert.put("start", now);
                convert.put("uuid", uuid);
                planJSONObject.put("convert", convert);
                planJSONObject.put("interval", now);
                reEntry = planJSONObject.getJSONObject("re_entry");
            } catch (Exception e) {
                //SFLog.printStackTrace(e);
            }

            //4 开始转化窗口，区间为 [now, now + 转化时长]
            if (p.convertWindow != null) {
                p.convertWindow.setStartTime(now);
                globalData.addConvertPlan(p);
            }

            //6 若不存在参与限制窗口，则开始参与限制窗口，区间为 [now, now + 参与限制时长]
            if (p.planReEntryWindow != null && p.planReEntryWindow.isFinished()) {
                p.planReEntryWindow.setStartTime(now);
                p.planReEntryWindow.setCount(0);
                try {
                    reEntry.put("start", now);
                } catch (Exception e) {
                    SFLog.printStackTrace(e);
                }
            }

            //开始全局间隔窗口，区间为 [now, now + 间隔时长]
            if (globalData.globalIntervalWindow != null) {
                globalData.globalIntervalWindow.setStartTime(now);
            }

            //开始间隔窗口，区间为 [now, now + 间隔时长]
            if (p.planIntervalWindow != null) {
                p.planIntervalWindow.setStartTime(now);
            }

            //7 计划内，参与限制窗口计次 ++
            if (p.planReEntryWindow != null) {
                p.planReEntryWindow.addCount();
                try {
                    reEntry.put("count", p.planReEntryWindow.getCount());
                    planJSONObject.put("re_entry", reEntry);
                } catch (Exception e) {
                    SFLog.printStackTrace(e);
                }
            }

            //7 全局弹窗限制窗口，计次 ++
            if (globalData.globalPopupLimit != null) {
                globalData.globalPopupLimit.setStartTime(now);
                globalData.globalPopupLimit.addCount();
            }

            // 删除本计划所有触发窗口，退出本计划
            for (int i = 0; i < matchers.size(); i++) {
                Matcher matcher = matchers.get(i);
                matcher.eventWindow.setCount(0);
                matcher.eventWindow.setStartTime(0);
                try {
                    planJSONObject.remove("matcher_" + i);
                } catch (Exception e) {
                    SFLog.printStackTrace(e);
                }
            }

            try {
                globalData.cachedGlobalData.put("interval_global", now);
            } catch (Exception e) {
                SFLog.printStackTrace(e);
            }

        }
        if (isShouldSaveData) {
            globalData.commit();
        }
    }

    /**
     * 规则匹配成功后，触发计次 ++，返回计次是否达到阀值
     *
     * @param matcher Matcher 对象
     * @return 计次是否达到阀值
     */
    private static boolean triggerTimesCount(Matcher matcher) {
        boolean isArrivalTriggerCount = false;
        if (matcher.eventWindow.isFinished()) {
            SFLog.d(TAG, "Event trigger window expired.");
            matcher.eventWindow.setStartTime(System.currentTimeMillis());
            matcher.eventWindow.setCount(1);
        } else {
            matcher.eventWindow.addCount();
        }
        int matcherCount = Integer.parseInt(matcher.params.get(0));
        //目前一期 measure 只可能是 GENERAL
        if (TextUtils.equals(matcher.measure, "GENERAL")) {
            /*if (TextUtils.equals(matcher.function, "EQUAL") && matcher.eventWindow.count == matcherCount) {
                isArrivalTriggerCount = true;
            } else if (matcher.eventWindow.count >= matcherCount) {
                isArrivalTriggerCount = true;
            }*/
            if (matcher.eventWindow.getCount() >= matcherCount) {
                isArrivalTriggerCount = true;
            }
        }
        return isArrivalTriggerCount;
    }

    /**
     * 触发事件是否匹配某个规则
     *
     * @param jsonObject 触发事件对象
     * @param matcher 规则匹配
     * @return 触发事件是否命中某个规则
     */
    private static boolean isMatchPattern(JSONObject jsonObject, Matcher matcher) {
        try {
            if (!TextUtils.equals(jsonObject.getString("event"), matcher.eventName)) {
                return false;
            }
            try {
                // 事件属性匹配成功的计数
                int count = 0;
                if (matcher.filter == null || matcher.filter.conditionsList == null || matcher.filter.conditionsList.isEmpty()) {
                    return true;
                }
                String relation = matcher.filter.relation;
                if (TextUtils.isEmpty(relation)) {
                    relation = PropertyExpression.OR;
                }
                int size = matcher.filter.conditionsList.size();
                for (int i = 0; i < size; i++) {
                    Condition condition = matcher.filter.conditionsList.get(i);
                    String field = condition.field;
                    int index = condition.field.lastIndexOf(".");
                    field = field.substring(index + 1, condition.field.length());
                    Object fieldValue = jsonObject.getJSONObject("properties").opt(field);
                    String function = condition.function;
                    List<Object> params = condition.params;
                    boolean isMatchProperty = PropertyExpression.isMatchProperty(function, fieldValue, params);
                    if (isMatchProperty) {
                        if (PropertyExpression.OR.equalsIgnoreCase(relation)) {
                            return true;
                        }
                        count++;
                    }
                }
                if (PropertyExpression.AND.equalsIgnoreCase(relation) && count == size) {
                    return true;
                }
            } catch (Exception e) {
                SFLog.printStackTrace(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
