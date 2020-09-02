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

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Window {

    /**
     * 秒、分钟、小时, nature 只能为 false
     */
    public static final String UNIT_SECOND = "SECOND";
    public static final String UNIT_MINUTE = "MINUTE";
    public static final String UNIT_HOUR = "HOUR";

    /**
     * 天，周，日，nature 可以为 true 和 false
     */
    public static final String UNIT_DAY = "DAY";
    public static final String UNIT_WEEK = "WEEK";
    public static final String UNIT_MONTH = "MONTH";

    public int value;
    public String unit;
    public boolean natural;

    /**
     * 对于参与计划窗口 ReEntryWindow 有值
     */
    public int limit;

    private long startTime;
    private long endTime;
    private int count;

    public void addCount() {
        count++;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public boolean isFinished() {
        return System.currentTimeMillis() > endTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        if (natural) {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
        }
        if (TextUtils.equals(UNIT_SECOND, unit)) {
            endTime = startTime + 1000L * value;
        } else if (TextUtils.equals(UNIT_MINUTE, unit)) {
            endTime = startTime + 60 * 1000L * value;
        } else if (TextUtils.equals(UNIT_HOUR, unit)) {
            endTime = startTime + 60 * 60 * 1000L * value;
        } else if (TextUtils.equals(UNIT_DAY, unit)) {
            if (!natural) {
                endTime = startTime + 24 * 60 * 60 * 1000L * value;
            } else {
                calendar.add(Calendar.DAY_OF_YEAR, value - 1);
                endTime = calendar.getTimeInMillis();
            }
        } else if (TextUtils.equals(UNIT_WEEK, unit)) {
            if (!natural) {
                endTime = startTime + 7 * 24 * 60 * 60 * 1000L * value;
            } else {
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
                calendar.add(Calendar.DAY_OF_WEEK, 1);
                //前进几周
                calendar.add(Calendar.WEEK_OF_YEAR, value - 1);
                endTime = calendar.getTimeInMillis();
            }
        } else if (TextUtils.equals(UNIT_MONTH, unit)) {
            if (!natural) {
                endTime = startTime + 30 * 24 * 60 * 60 * 1000L * value;
            } else {
                calendar.add(Calendar.MONTH, value - 1);
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                endTime = calendar.getTimeInMillis();
            }
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public int changeToSecond(int value, String unit) {
        if (TextUtils.equals(UNIT_MINUTE, unit)) {
            return changeToSecond(value * 60, UNIT_SECOND);
        } else if (TextUtils.equals(UNIT_HOUR, unit)) {
            return changeToSecond(value * 60, UNIT_MINUTE);
        } else if (TextUtils.equals(UNIT_DAY, unit)) {
            return changeToSecond(value * 24, UNIT_HOUR);
        } else if (TextUtils.equals(UNIT_WEEK, unit)) {
            return changeToSecond(value * 7, UNIT_DAY);
        } else if (TextUtils.equals(UNIT_MONTH, unit)) {
            return changeToSecond(value * 30, UNIT_DAY);
        }
        //默认返回秒
        return value;
    }


    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return "Window{" +
                "count=" + count +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                ", natural=" + natural +
                ", limit=" + limit +
                ", startTime=" + simpleDateFormat.format(startTime) +
                ", endTime=" + simpleDateFormat.format(endTime) +
                '}';
    }
}
