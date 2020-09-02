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

interface UIProperty {
    String TAG = "UIProperty";
    String id = "id";
    String msgType = "msgType";
    String scrollableY = "scrollableY";
    String properties = "properties";
    String closeEnabled = "closeEnabled";
    String closeStyle = "closeStyle";
    String image = "image";
    String localImageName = "localImageName";
    String width = "width";
    String height = "height";
    String maxHeight = "maxHeight";
    String maskColor = "maskColor";
    String maskCloseEnabled = "maskCloseEnabled";
    String template = "template";
    String type = "type";
    String layout = "layout";
    String align = "align";
    String margin = "margin";
    String padding = "padding";
    String top = "top";
    String right = "right";
    String bottom = "bottom";
    String left = "left";
    String cornerRadius = "cornerRadius";
    String backgroundColor = "backgroundColor";
    String borderWidth = "borderWidth";
    String borderColor = "borderColor";
    String text = "text";
    String font = "font";
    String textAlign = "textAlign";
    String lineHeight = "lineHeight";
    String subviews = "subviews";
    String backgroundImage = "backgroundImage";
    String color = "color";
    String a = "a";
    String r = "r";
    String g = "g";
    String b = "b";
    String action = "action";
    String isHidden = "isHidden";

    String type_row = "row";
    String type_column = "column";
    String type_image = "image";
    String type_image_button = "image_button";
    String type_label = "label";
    String type_link = "link";
    String type_button = "button";
    String type_mask = "mask";

    String sf_close_type = "$sf_close_type";

    String title_type = "title";
    String content_type = "content";

    String action_android = "ANDROID";
    String action_type_copy = "copy";
    String action_type_tel = "tel";
    String action_type_email = "email";
    String action_type_sms = "sms";
    String action_type_camera = "camera";
    String action_type_link = "openlink";
    String action_type_close = "close";
    String action_type_customize = "customize";
    String action_value = "value";
    String action_extra = "extra";
    String ui_json = "ui_json";
    String plan_id = "plan_id";
    String copied_tip = "copied_tip";
}
