/*
 * Created by dengshiwei on 2020/03/18.
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
import android.widget.Button;

import org.json.JSONObject;

/**
 * 由于设置 Background 属性，导致原生 Button 文字不居中，所以需要自定义预先置空背景
 */
public class SFButton extends Button {
    private boolean isInterceptTouchEvent;

    public SFButton(Context context, JSONObject actionJson) {
        this(context, null, -1);
        isInterceptTouchEvent = actionJson == null;
    }

    public SFButton(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SFButton(Context context, AttributeSet attrs, int defStyleAttr) {
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