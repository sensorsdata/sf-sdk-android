/*
 * Created by dengshiwei on 2020/02/24.
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

package com.sensorsdata.sf.android.demo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.sf.android.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_picture).setOnClickListener(this);
        findViewById(R.id.btn_2button).setOnClickListener(this);
        findViewById(R.id.btn_picture_2button).setOnClickListener(this);
        findViewById(R.id.btn_pictureButton).setOnClickListener(this);
        findViewById(R.id.btn_text_button).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

   void trackShareSucEvent(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bb", "  cc  ");
            jsonObject.put("aa", new Date());
            jsonObject.put("dd", "");
            jsonObject.put("number", 5);
            JSONArray a = new JSONArray();
            a.put("aa");
            a.put("bb");
            jsonObject.put("ee", a);
            SensorsDataAPI.sharedInstance().track("shareSuccess", jsonObject);
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_picture:
                //PopDialogUtils.getInstance().pictureDialog(this);
                SensorsDataAPI.sharedInstance().track("$WebClick");
                break;
            case R.id.btn_2button:
                PopDialogUtils.getInstance().picture2ButtonDialog(this);
                break;
            case R.id.btn_picture_2button:
                PopDialogUtils.getInstance().picture2Button2Dialog(this);
                break;
            case R.id.btn_pictureButton:
                SensorsDataAPI.sharedInstance().track("$WebStay");
                //PopDialogUtils.getInstance().pictureButtonDialog(this);
                break;
            case R.id.btn_text_button:
                trackShareSucEvent();
                //PopDialogUtils.getInstance().textButtonDialog(this);
                break;
            default:
                break;
        }
    }
}
