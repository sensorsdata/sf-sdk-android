/*
 * Created by dengshiwei on 2020/02/24.
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

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static ArrayList<String> list;

    static {
        list = new ArrayList<>();
        list.add("仅 Gif 图片");
        list.add("Gif 背景图 +文字 +按钮");
        list.add("仅图片");
        list.add("图片+按钮");
        list.add("图片+文字+按钮");
        list.add("背景图+按钮");
        list.add("背景图+文字+按钮");
        list.add("背景图+文字");
        list.add("文字+按钮");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < list.size(); i++) {
            Button button = new Button(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.bottomMargin = 25;
            button.setLayoutParams(layoutParams);
            button.setId(i);
            button.setText(list.get(i));
            linearLayout.addView(button);
            button.setOnClickListener(this);
        }
        setContentView(linearLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        try {
            PopDialogUtils.getInstance().pictureDialog(this, v.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
