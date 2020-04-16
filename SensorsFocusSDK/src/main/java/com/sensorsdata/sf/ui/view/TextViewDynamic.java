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
import android.os.Build;
import android.text.TextUtils;
import android.widget.TextView;

import com.sensorsdata.sf.ui.widget.SFTextView;

import org.json.JSONObject;

class TextViewDynamic extends AbstractViewDynamic {
    private String mNameType;

    TextViewDynamic(Context context, JSONObject uiJson) {
        super(context, uiJson);
        if (mPropertyJson != null) {
            this.mNameType = mPropertyJson.optString(UIProperty.msgType);
            this.mText = mPropertyJson.optString(UIProperty.text);
        }
    }

    @Override
    public TextView createView(Activity activity) {
        mView = new SFTextView(activity, mActionJson);
        return (TextView) super.createView(activity);
    }

    @Override
    void setViewProperty(JSONObject propertyJson) {
        try {
            ((TextView) mView).setText(mText);
            ((TextView) mView).setTextSize(realFontSize(propertyJson.optString(UIProperty.font)));
            if (propertyJson.has(UIProperty.color)) {
                ((TextView) mView).setTextColor(color(propertyJson.optJSONObject(UIProperty.color)));
            }
            ((TextView) mView).setGravity(align(propertyJson.optString(UIProperty.textAlign)));
//            if (propertyJson.has(UIProperty.lineHeight)) {
//                ((TextView) view).setMaxHeight(realSize(mContext, propertyJson.optString(UIProperty.lineHeight)));   // LineHeight 设置
//            }
            // 设置行间距
            ((TextView) mView).setLineSpacing(0, (float) 1.1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // 设置字间距
                ((TextView) mView).setLetterSpacing((float) 0.1);
            }
            if (!isPORTRAIT) {
                int MAX_LINES_DEFAULT_LANDSCAPE = 2;
                ((TextView) mView).setMaxLines(MAX_LINES_DEFAULT_LANDSCAPE);
                ((TextView) mView).setEllipsize(TextUtils.TruncateAt.END);
            }
            setBackground(mContext, propertyJson);
            mView.setVisibility(visible(propertyJson.optBoolean(UIProperty.isHidden, false)));
        } catch (Exception ex) {
            // ignore
        }
    }

    @Override
    public String getType() {
        return UIProperty.type_label;
    }

    @Override
    public String getText() {
        return this.mText;
    }

    @Override
    public JSONObject getActionJson() {
        return this.mActionJson;
    }

    String getNameType() {
        return mNameType;
    }
}
