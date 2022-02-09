/*
 * Created by dengshiwei on 2020/03/01.
 * Copyright 2015－2022 Sensors Data Inc.
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

package com.sensorsdata.sf.android.demo;

import android.content.Context;

import com.sensorsdata.sf.core.utils.Utils;
import com.sensorsdata.sf.ui.view.DynamicViewJsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class PopDialogUtils {
    private static PopDialogUtils instance;
    private Context mContext;
    static ArrayList<String> list;

    static {
        list = new ArrayList<>();
        list.add("pic_gif.json");
        list.add("gif_background_text_button.json");
        list.add("pic.json");
        list.add("pic_button.json");
        list.add("pic_text_button.json");
        list.add("background_button.json");
        list.add("background_text_button.json");
        list.add("background_text.json");
        list.add("text_button.json");
        list.add("pic_title_content.json");
    }


    static PopDialogUtils getInstance() {

        if (instance == null) {
            instance = new PopDialogUtils(null);
        }
        return instance;
    }

    static PopDialogUtils getInstance(Context context) {
        if (instance == null) {
            instance = new PopDialogUtils(context);
        }
        return instance;
    }

    private PopDialogUtils(final Context context) {
        mContext = context;
    }

    void pictureDialog(Context context, int position) {
        try {
            DynamicViewJsonBuilder jsonBuilder = new DynamicViewJsonBuilder(context);
            String content = Utils.getJsonFromAssets(list.get(position), context);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", content);
            jsonBuilder.campaignStartDebug(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
