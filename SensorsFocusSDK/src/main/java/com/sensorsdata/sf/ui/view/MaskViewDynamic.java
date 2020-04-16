/*
 * Created by dengshiwei on 2020/03/01.
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

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.json.JSONObject;

final class MaskViewDynamic extends AbstractViewDynamic {
    private boolean mMaskCloseEnabled;
    private String mActionId;
    private JSONObject mUIJson;
    private LinearLayoutDynamic mChildDynamic;

    MaskViewDynamic(Context mContext, LinearLayoutDynamic childDynamic, JSONObject uiJson) {
        super(mContext, uiJson);
        this.mChildDynamic = childDynamic;
        this.mUIJson = uiJson;
    }

    @Override
    public FrameLayout createView(Activity activity) {
        try {
            mView = new FrameLayout(activity);
            mMaskCloseEnabled = mUIJson.optBoolean(UIProperty.maskCloseEnabled);
            mActionId = mUIJson.optString("maskActionId");
            super.createView(activity);
            return (FrameLayout) mView;
        } catch (Exception ex) {
            // ignore
        }
        return null;
    }

    @Override
    void setViewProperty(JSONObject propertyJson) {
        try {
            mView.setBackgroundColor(color(mUIJson.optJSONObject(UIProperty.maskColor)));
        } catch (Exception ex) {
            // ignore
        }
    }

    @Override
    void layoutView(JSONObject layoutJson) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mView.setLayoutParams(layoutParams);
    }

    boolean isMaskCloseEnabled() {
        return mMaskCloseEnabled;
    }

    String getActionId() {
        return mActionId;
    }

    LinearLayoutDynamic getChildDynamic() {
        return mChildDynamic;
    }
}
