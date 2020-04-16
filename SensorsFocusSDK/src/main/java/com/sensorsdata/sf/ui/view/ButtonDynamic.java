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
import android.text.TextUtils;
import android.widget.Button;

import com.sensorsdata.sf.ui.widget.SFButton;

import org.json.JSONObject;

final class ButtonDynamic extends AbstractViewDynamic {

    ButtonDynamic(Context context, JSONObject uiJson) {
        super(context, uiJson);
    }

    @Override
    public Button createView(Activity activity) {
        try {
            mView = new SFButton(activity, mActionJson);
            mView.setBackgroundDrawable(null);
            return (Button) super.createView(activity);
        } catch (Exception ex) {
            // ignore
        }
        return null;
    }

    @Override
    void setViewProperty(JSONObject propertyJson) {
        try {
            mText = propertyJson.optString(UIProperty.text);
            ((Button) mView).setText(mText);
            if (!isPORTRAIT) {
                ((Button) mView).setMaxLines(1);
                ((Button) mView).setEllipsize(TextUtils.TruncateAt.END);
            }
            ((Button) mView).setTextSize(realFontSize(propertyJson.optString(UIProperty.font)));
            if (propertyJson.has(UIProperty.color)) {
                ((Button) mView).setTextColor(color(propertyJson.optJSONObject(UIProperty.color)));
            }
            ((Button) mView).setGravity(align(propertyJson.optString(UIProperty.textAlign)));
            setBackground(mContext, propertyJson);
            mView.setVisibility(visible(propertyJson.optBoolean(UIProperty.isHidden, false)));
        } catch (Exception ex) {
            // ignore
        }
    }

    @Override
    public String getType() {
        return UIProperty.type_button;
    }

    @Override
    public String getText() {
        return mText;
    }

    @Override
    public JSONObject getActionJson() {
        return this.mActionJson;
    }
}
