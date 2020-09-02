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

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.sensorsdata.sf.core.utils.SFLog;
import com.sensorsdata.sf.core.utils.Utils;
import com.sensorsdata.sf.core.window.Limit;
import com.sensorsdata.sf.core.window.Window;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 全局弹窗限制（所有计划 5 天内最多弹 4 次且 1 天最多弹 2 次）
 */
public class GlobalPopupLimit {
    public boolean isInUse;
    public List<Limit> limits;
    public Context context;
    private JSONArray data;
    public static final String GLOBAL_POPUP_LIMIT_FILE_NAME = "sensors_focus_global_popup_limit";
    private long startTime;

    public GlobalPopupLimit(Context context) {
        this.context = context;
        loadData();
    }

    private void loadData() {
        try {
            File file = new File(context.getFilesDir(), GLOBAL_POPUP_LIMIT_FILE_NAME);
            String jsonContent = Utils.loadJsonFromFile(file);
            if (jsonContent != null) {
                String json = Utils.loadJsonFromFile(file);
                if (!TextUtils.isEmpty(json)) {
                    data = new JSONArray(json);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * 增加计次
     */
    public void addCount() {
        try {
            if (data == null) {
                data = new JSONArray();
            }
            data.put(startTime);
            saveData();
        } catch (Exception e) {
            SFLog.printStackTrace(e);
        }
    }

    public boolean isMatcher() {
        if (data == null) {
            return true;
        }
        boolean isMatcher = true;
        try {
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTimeInMillis(startTime);
            calendar2.add(Calendar.DAY_OF_YEAR, -90);
            long before2 = calendar2.getTimeInMillis();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startTime);
            for (Limit limit : limits) {
                limit.triggerCount = 0;
                if (limit.natural) {
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    if (TextUtils.equals(Window.UNIT_WEEK, limit.unit)) {
                        calendar.setFirstDayOfWeek(Calendar.MONDAY);
                        calendar.set(Calendar.DAY_OF_WEEK, 2);
                        //回退几周
                        calendar.add(Calendar.WEEK_OF_YEAR, -(limit.value - 1));
                    } else if (TextUtils.equals(Window.UNIT_MONTH, limit.unit)) {
                        calendar.add(Calendar.MONTH, 0);
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        calendar.add(Calendar.MONTH, -(limit.value - 1));
                    } else {
                        calendar.add(Calendar.DAY_OF_YEAR, -(limit.value - 1));
                    }
                } else {
                    calendar.add(Calendar.SECOND, -limit.changeToSecond(limit.value, limit.unit));
                }
                limit.before = calendar.getTimeInMillis();
            }

            ArrayList<Integer> deletedData = new ArrayList<>();
            //有多少天数据
            int count = data.length();

            boolean isShouldDelete = count > 90;
            for (int i = 0; i < count; i++) {
                long date = data.optLong(i);
                if (isShouldDelete && date < before2) {
                    deletedData.add(i);
                    continue;
                }
                for (Limit limit : limits) {
                    if (date >= limit.before && date <= startTime) {
                        if (++limit.triggerCount >= limit.limit) {
                            isMatcher = false;
                            break;
                        }
                    }
                }
            }

            if (deletedData.size() > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (Integer delete : deletedData) {
                    data.remove(delete);
                }
                saveData();
            }
        } catch (Exception e) {
            SFLog.printStackTrace(e);
        }
        return isMatcher;
    }

    private void saveData() {
        try {
            if (data != null) {
                File dir = context.getFilesDir();
                Utils.saveJsonToFile(data.toString(), new File(dir, GLOBAL_POPUP_LIMIT_FILE_NAME));
            }
        } catch (Exception e) {
            SFLog.printStackTrace(e);
        }
    }

    @Override
    public String toString() {
        return "GlobalPopupLimit{" +
                "limits=" + limits +
                ", data=" + data +
                '}';
    }
}
