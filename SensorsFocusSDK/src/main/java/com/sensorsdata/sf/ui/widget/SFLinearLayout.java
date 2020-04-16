/*
 * Created by dengshiwei on 2020/04/02.
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

package com.sensorsdata.sf.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * 由于父控件设置点击事件，子控件没设置，点击子控件会触发父控件的点击事件，所以需要自定义拦截
 */
public class SFLinearLayout extends LinearLayout {
    private boolean isInterceptTouchEvent;

    public SFLinearLayout(Context context, JSONObject actionJson) {
        this(context, null, -1);
        isInterceptTouchEvent = actionJson == null;
    }

    public SFLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SFLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setBackgroundDrawable(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isInterceptTouchEvent) {
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }
}