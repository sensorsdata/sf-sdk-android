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

package com.sensorsdata.sf.android.demo;

import android.content.Context;

import com.sensorsdata.sf.ui.view.DynamicViewJsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class PopDialogUtils {
    private static PopDialogUtils instance;
    private JSONArray jsonUIArray;
    private Context mContext;
    private DynamicViewJsonBuilder jsonBuilder;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mContext = context;
                    jsonUIArray = new JSONArray(uiJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void pictureDialog(Context context) {
        try {
            DynamicViewJsonBuilder jsonBuilder = new DynamicViewJsonBuilder();
            jsonBuilder.showDialogPreview(mContext,jsonUIArray.getJSONObject(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String getUiJson = "{\n" +
            "  \"properties\": {\n" +
            "    \"maskColor\": {\n" +
            "      \"r\": 0,\n" +
            "      \"g\": 0,\n" +
            "      \"b\": 0,\n" +
            "      \"a\": 0.6\n" +
            "    },\n" +
            "    \"maskCloseEnabled\": true,\n" +
            "    \"maskActionId\": \"action-mask-click\"\n" +
            "  },\n" +
            "  \"template\": {\n" +
            "    \"type\": \"column\",\n" +
            "    \"layout\": {\n" +
            "      \"align\": \"center\",\n" +
            "      \"width\": \"310px\",\n" +
            "      \"margin\": {\n" +
            "        \"top\": \"60px\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"subviews\": [\n" +
            "      {\n" +
            "        \"type\": \"image_button\",\n" +
            "        \"properties\": {\n" +
            "          \"image\": \"https://sf-static.sensorsdata.cn/product%2Fclose_icon.png\",\n" +
            "          \"localImageName\": \"close\",\n" +
            "          \"msgType\": \"close\"\n" +
            "        },\n" +
            "        \"layout\": {\n" +
            "          \"width\": \"30px\",\n" +
            "          \"height\": \"30px\",\n" +
            "          \"align\": \"right\"\n" +
            "        },\n" +
            "        \"action\": {\n" +
            "          \"IOS\": [\n" +
            "            {\n" +
            "              \"type\": \"close\",\n" +
            "              \"$sf_close_type\": \"POPUP_CLOSE_TOPRIGHT\",\n" +
            "              \"closeable\": true,\n" +
            "              \"id\": \"action-48e7afeb-1173-4786-9978-f904f6fdb166\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"ANDROID\": [\n" +
            "            {\n" +
            "              \"type\": \"close\",\n" +
            "              \"$sf_close_type\": \"POPUP_CLOSE_TOPRIGHT\",\n" +
            "              \"closeable\": true,\n" +
            "              \"id\": \"action-48e7afeb-1173-4786-9978-f904f6fdb166\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"H5\": [\n" +
            "            {\n" +
            "              \"type\": \"close\",\n" +
            "              \"$sf_close_type\": \"POPUP_CLOSE_TOPRIGHT\",\n" +
            "              \"closeable\": true,\n" +
            "              \"id\": \"action-48e7afeb-1173-4786-9978-f904f6fdb166\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"id\": \"action-48e7afeb-1173-4786-9978-f904f6fdb166\"\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"column\",\n" +
            "        \"properties\": {\n" +
            "          \"backgroundColor\": {\n" +
            "            \"r\": 255,\n" +
            "            \"g\": 255,\n" +
            "            \"b\": 255,\n" +
            "            \"a\": 1\n" +
            "          },\n" +
            "          \"cornerRadius\": \"5px\"\n" +
            "        },\n" +
            "        \"layout\": {\n" +
            "          \"align\": \"center\",\n" +
            "          \"width\": \"310px\",\n" +
            "          \"margin\": {\n" +
            "            \"top\": \"10px\"\n" +
            "          },\n" +
            "          \"padding\": {\n" +
            "            \"bottom\": \"17px\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"subviews\": [\n" +
            "          {\n" +
            "            \"type\": \"image\",\n" +
            "            \"properties\": {\n" +
            "              \"name\": \"图片\",\n" +
            "              \"image\": \"https://sf-static.sensorsdata.cn/sftest_ee67efbfabbda334bff7b24a83c3fe8f93aeb71d.jpg\"\n" +
            "            },\n" +
            "            \"layout\": {\n" +
            "              \"align\": \"center\",\n" +
            "              \"width\": \"310px\",\n" +
            "              \"height\": \"220px\"\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"label\",\n" +
            "            \"properties\": {\n" +
            "              \"msgType\": \"title\",\n" +
            "              \"name\": \"标题\",\n" +
            "              \"text\": \"发顺丰\",\n" +
            "              \"font\": \"24px\",\n" +
            "              \"color\": {\n" +
            "                \"r\": 0,\n" +
            "                \"g\": 0,\n" +
            "                \"b\": 0,\n" +
            "                \"a\": 1\n" +
            "              },\n" +
            "              \"textAlign\": \"center\"\n" +
            "            },\n" +
            "            \"layout\": {\n" +
            "              \"width\": \"290px\",\n" +
            "              \"align\": \"center\",\n" +
            "              \"margin\": {\n" +
            "                \"top\": \"16px\"\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"label\",\n" +
            "            \"properties\": {\n" +
            "              \"msgType\": \"content\",\n" +
            "              \"name\": \"内容\",\n" +
            "              \"text\": \"发送地方玩儿\",\n" +
            "              \"font\": \"14px\",\n" +
            "              \"color\": {\n" +
            "                \"r\": 74,\n" +
            "                \"g\": 74,\n" +
            "                \"b\": 74,\n" +
            "                \"a\": 1\n" +
            "              },\n" +
            "              \"lineHeight\": \"24px\",\n" +
            "              \"textAlign\": \"center\"\n" +
            "            },\n" +
            "            \"layout\": {\n" +
            "              \"width\": \"290px\",\n" +
            "              \"align\": \"center\",\n" +
            "              \"margin\": {\n" +
            "                \"top\": \"5px\"\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"column\",\n" +
            "            \"layout\": {\n" +
            "              \"width\": \"290px\",\n" +
            "              \"align\": \"center\",\n" +
            "              \"margin\": {\n" +
            "                \"top\": \"16px\"\n" +
            "              }\n" +
            "            },\n" +
            "            \"subviews\": [\n" +
            "              {\n" +
            "                \"type\": \"image_button\",\n" +
            "                \"properties\": {\n" +
            "                  \"name\": \"按钮 1\",\n" +
            "                  \"image\": \"https://sf-static.sensorsdata.cn/sftest_1b29b55c80ff737ea49e9063281e921ac5d7a24d.png\"\n" +
            "                },\n" +
            "                \"layout\": {\n" +
            "                  \"width\": \"290px\",\n" +
            "                  \"height\": \"40px\"\n" +
            "                },\n" +
            "                \"action\": {\n" +
            "                  \"IOS\": [\n" +
            "                    {\n" +
            "                      \"type\": \"openlink\",\n" +
            "                      \"closeable\": true,\n" +
            "                      \"id\": \"action-cb6a53d4-d099-4f9f-9ba9-b5702464ac48\",\n" +
            "                      \"value\": \"https://stock.tuchong.com/topic?topicId=49563#879092033604812819\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"ANDROID\": [\n" +
            "                    {\n" +
            "                      \"type\": \"openlink\",\n" +
            "                      \"closeable\": true,\n" +
            "                      \"id\": \"action-cb6a53d4-d099-4f9f-9ba9-b5702464ac48\",\n" +
            "                      \"value\": \"https://stock.tuchong.com/topic?topicId=49563#879092033604812819\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"H5\": [\n" +
            "                    {\n" +
            "                      \"type\": \"openlink\",\n" +
            "                      \"closeable\": true,\n" +
            "                      \"id\": \"action-cb6a53d4-d099-4f9f-9ba9-b5702464ac48\",\n" +
            "                      \"value\": \"https://stock.tuchong.com/topic?topicId=49563#879092033604812819\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"id\": \"action-cb6a53d4-d099-4f9f-9ba9-b5702464ac48\"\n" +
            "                }\n" +
            "              },\n" +
            "              {\n" +
            "                \"type\": \"link\",\n" +
            "                \"properties\": {\n" +
            "                  \"name\": \"按钮 2\",\n" +
            "                  \"color\": {\n" +
            "                    \"r\": 74,\n" +
            "                    \"g\": 144,\n" +
            "                    \"b\": 226,\n" +
            "                    \"a\": 1\n" +
            "                  },\n" +
            "                  \"text\": \"跳转到首页\",\n" +
            "                  \"font\": \"18px\"\n" +
            "                },\n" +
            "                \"layout\": {\n" +
            "                  \"width\": \"290px\",\n" +
            "                  \"height\": \"40px\",\n" +
            "                  \"margin\": {\n" +
            "                    \"top\": \"10px\"\n" +
            "                  }\n" +
            "                },\n" +
            "                \"action\": {\n" +
            "                  \"IOS\": [\n" +
            "                    {\n" +
            "                      \"type\": \"openlink\",\n" +
            "                      \"closeable\": true,\n" +
            "                      \"id\": \"action-90765f6e-b2bc-4db1-9852-ac2517c1bd74\",\n" +
            "                      \"value\": \"https://www.baidu.com/\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"ANDROID\": [\n" +
            "                    {\n" +
            "                      \"type\": \"openlink\",\n" +
            "                      \"closeable\": true,\n" +
            "                      \"id\": \"action-90765f6e-b2bc-4db1-9852-ac2517c1bd74\",\n" +
            "                      \"value\": \"https://www.baidu.com/\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"H5\": [\n" +
            "                    {\n" +
            "                      \"type\": \"openlink\",\n" +
            "                      \"closeable\": true,\n" +
            "                      \"id\": \"action-90765f6e-b2bc-4db1-9852-ac2517c1bd74\",\n" +
            "                      \"value\": \"https://www.baidu.com/\"\n" +
            "                    }\n" +
            "                  ],\n" +
            "                  \"id\": \"action-90765f6e-b2bc-4db1-9852-ac2517c1bd74\"\n" +
            "                }\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"image_button\",\n" +
            "        \"properties\": {\n" +
            "          \"image\": \"https://sf-static.sensorsdata.cn/product%2Fclose_icon.png\",\n" +
            "          \"isHidden\": true,\n" +
            "          \"localImageName\": \"close\",\n" +
            "          \"msgType\": \"close\"\n" +
            "        },\n" +
            "        \"layout\": {\n" +
            "          \"width\": \"30px\",\n" +
            "          \"height\": \"30px\",\n" +
            "          \"align\": \"center\",\n" +
            "          \"margin\": {\n" +
            "            \"top\": \"10px\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"action\": {\n" +
            "          \"IOS\": [\n" +
            "            {\n" +
            "              \"type\": \"close\",\n" +
            "              \"$sf_close_type\": \"POPUP_CLOSE_BOTTOM\",\n" +
            "              \"closeable\": true,\n" +
            "              \"id\": \"action-541139f0-ed72-44a8-a184-f617112d0377\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"ANDROID\": [\n" +
            "            {\n" +
            "              \"type\": \"close\",\n" +
            "              \"$sf_close_type\": \"POPUP_CLOSE_BOTTOM\",\n" +
            "              \"closeable\": true,\n" +
            "              \"id\": \"action-541139f0-ed72-44a8-a184-f617112d0377\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"H5\": [\n" +
            "            {\n" +
            "              \"type\": \"close\",\n" +
            "              \"$sf_close_type\": \"POPUP_CLOSE_BOTTOM\",\n" +
            "              \"closeable\": true,\n" +
            "              \"id\": \"action-541139f0-ed72-44a8-a184-f617112d0377\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"id\": \"action-541139f0-ed72-44a8-a184-f617112d0377\"\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";
    void pictureButtonDialog(Context context) {
        try {
            DynamicViewJsonBuilder jsonBuilder = new DynamicViewJsonBuilder();
            jsonBuilder.showDialogPreview(mContext,new JSONObject(getUiJson));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    void textButtonDialog(Context context) {
        try {
            DynamicViewJsonBuilder jsonBuilder = new DynamicViewJsonBuilder();
            jsonBuilder.showDialogPreview(mContext,jsonUIArray.getJSONObject(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void picture2ButtonDialog(Context context) {
        try {
            DynamicViewJsonBuilder jsonBuilder = new DynamicViewJsonBuilder();
            jsonBuilder.showDialogPreview(mContext,jsonUIArray.getJSONObject(3));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void picture2Button2Dialog(Context context) {
        try {
            DynamicViewJsonBuilder jsonBuilder = new DynamicViewJsonBuilder();
            jsonBuilder.showDialogPreview(mContext,jsonUIArray.getJSONObject(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private final String uiJson = "[\n" +
            "  {\n" +
            "    \"id\": \"V0101\",\n" +
            "    \"cname\": \"纯图片\",\n" +
            "    \"properties\": {\n" +
            "      \"maskColor\": {\n" +
            "        \"r\": 0,\n" +
            "        \"g\": 0,\n" +
            "        \"b\": 0,\n" +
            "        \"a\": 0.6\n" +
            "      },\n" +
            "      \"maskCloseEnabled\": true\n" +
            "    },\n" +
            "    \"template\": {\n" +
            "      \"type\": \"column\",\n" +
            "      \"layout\": {\n" +
            "        \"align\": \"center\",\n" +
            "        \"width\": \"310px\",\n" +
            "        \"margin\": {\n" +
            "          \"top\": \"16px\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"subviews\": [\n" +
            "        {\n" +
            "          \"type\": \"image_button\",\n" +
            "          \"properties\": {\n" +
            "            \"image\": \"https://s2.ax1x.com/2020/03/04/3oG9mt.png\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"width\": \"30px\",\n" +
            "            \"height\": \"30px\",\n" +
            "            \"align\": \"right\"\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"IOS\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"ANDROID\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"H5\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"image\",\n" +
            "          \"properties\": {\n" +
            "            \"image\": \"https://s2.ax1x.com/2020/03/04/3oMT1I.png\",\n" +
            "            \"cornerRadius\": \"8px\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"align\": \"center\",\n" +
            "            \"width\": \"310px\",\n" +
            "            \"height\": \"430px\",\n" +
            "            \"margin\": {\n" +
            "              \"top\": \"10px\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"image_button\",\n" +
            "          \"properties\": {\n" +
            "            \"image\": \"https://s2.ax1x.com/2020/03/04/3oG9mt.png\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"width\": \"30px\",\n" +
            "            \"height\": \"30px\",\n" +
            "            \"align\": \"center\",\n" +
            "            \"margin\": {\n" +
            "              \"top\": \"10px\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"IOS\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"ANDROID\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"H5\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": \"V0102\",\n" +
            "    \"cname\": \"一个按钮\",\n" +
            "    \"properties\": {\n" +
            "      \"closeEnabled\": true,\n" +
            "      \"closeStyle\": {\n" +
            "        \"image\": \"http://sf-static.cn-bj.ufileos.com/product%2Fclose_icon.png\",\n" +
            "        \"width\": \"32px\",\n" +
            "        \"height\": \"32px\"\n" +
            "      },\n" +
            "      \"maskColor\": {\n" +
            "        \"r\": 0,\n" +
            "        \"g\": 0,\n" +
            "        \"b\": 0,\n" +
            "        \"a\": 0.6\n" +
            "      },\n" +
            "      \"maskCloseEnabled\": false\n" +
            "    },\n" +
            "    \"template\": {\n" +
            "      \"type\": \"column\",\n" +
            "      \"layout\": {\n" +
            "        \"width\": \"310px\",\n" +
            "        \"align\": \"center\",\n" +
            "        \"margin\": {\n" +
            "          \"top\": \"20px\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"subviews\": [\n" +
            "        {\n" +
            "          \"type\": \"image_button\",\n" +
            "          \"properties\": {\n" +
            "            \"image\": \"https://s2.ax1x.com/2020/03/04/3oG9mt.png\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"width\": \"30px\",\n" +
            "            \"height\": \"30px\",\n" +
            "            \"align\": \"right\"\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"IOS\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"ANDROID\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"H5\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"column\",\n" +
            "          \"properties\": {\n" +
            "            \"backgroundColor\": {\n" +
            "              \"r\": 255,\n" +
            "              \"g\": 255,\n" +
            "              \"b\": 255,\n" +
            "              \"a\": 1\n" +
            "            },\n" +
            "            \"cornerRadius\": \"5px\",\n" +
            "            \"borderWidth\": \"8px\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"align\": \"center\",\n" +
            "            \"width\": \"310px\",\n" +
            "            \"margin\": {\n" +
            "              \"top\": \"10px\"\n" +
            "            },\n" +
            "            \"padding\": {\n" +
            "              \"bottom\": \"17px\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"subviews\": [\n" +
            "            {\n" +
            "              \"type\": \"image\",\n" +
            "              \"properties\": {\n" +
            "                \"image\": \"https://s2.ax1x.com/2020/03/04/3oMT1I.png\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"align\": \"center\",\n" +
            "                \"width\": \"310px\",\n" +
            "                \"height\": \"220px\"\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"label\",\n" +
            "              \"properties\": {\n" +
            "                \"msgType\": \"title\",\n" +
            "                \"name\": \"标题\",\n" +
            "                \"text\": \"标题文本区域\",\n" +
            "                \"font\": \"22px\",\n" +
            "                \"color\": {\n" +
            "                  \"r\": 0,\n" +
            "                  \"g\": 0,\n" +
            "                  \"b\": 0,\n" +
            "                  \"a\": 1\n" +
            "                },\n" +
            "                \"textAlign\": \"center\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"290px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"10px\"\n" +
            "                }\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"label\",\n" +
            "              \"properties\": {\n" +
            "                \"msgType\": \"content\",\n" +
            "                \"name\": \"内容\",\n" +
            "                \"text\": \"正文文本输入区域\\n正文文本输入区域\",\n" +
            "                \"font\": \"16px\",\n" +
            "                \"color\": {\n" +
            "                  \"r\": 74,\n" +
            "                  \"g\": 74,\n" +
            "                  \"b\": 74,\n" +
            "                  \"a\": 1\n" +
            "                },\n" +
            "                \"textAlign\": \"center\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"290px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"6px\"\n" +
            "                }\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"column\",\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"290px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"10px\"\n" +
            "                }\n" +
            "              },\n" +
            "              \"subviews\": [\n" +
            "                {\n" +
            "                  \"type\": \"button\",\n" +
            "                  \"properties\": {\n" +
            "                    \"text\": \"按钮文本 1\",\n" +
            "                    \"font\": \"18px\",\n" +
            "                    \"color\": {\n" +
            "                      \"r\": 255,\n" +
            "                      \"g\": 255,\n" +
            "                      \"b\": 255,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"backgroundColor\": {\n" +
            "                      \"r\": 74,\n" +
            "                      \"g\": 144,\n" +
            "                      \"b\": 226,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"borderWidth\": \"0px\",\n" +
            "                    \"cornerRadius\": \"20px\"\n" +
            "                  },\n" +
            "                  \"layout\": {\n" +
            "                    \"width\": \"290px\",\n" +
            "                    \"height\": \"40px\"\n" +
            "                  }\n" +
            "                },\n" +
            "                {\n" +
            "                  \"type\": \"button\",\n" +
            "                  \"properties\": {\n" +
            "                    \"isHidden\": true,\n" +
            "                    \"text\": \"按钮文本 2\",\n" +
            "                    \"font\": \"18px\",\n" +
            "                    \"color\": {\n" +
            "                      \"r\": 255,\n" +
            "                      \"g\": 255,\n" +
            "                      \"b\": 255,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"backgroundColor\": {\n" +
            "                      \"r\": 74,\n" +
            "                      \"g\": 144,\n" +
            "                      \"b\": 226,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"borderWidth\": \"0px\",\n" +
            "                    \"cornerRadius\": \"20px\"\n" +
            "                  },\n" +
            "                  \"layout\": {\n" +
            "                    \"width\": \"290px\",\n" +
            "                    \"height\": \"40px\",\n" +
            "                    \"margin\": {\n" +
            "                      \"top\": \"10px\"\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "              ]\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"image\",\n" +
            "          \"properties\": {\n" +
            "            \"image\": \"http://sf-static.cn-bj.ufileos.com/product%2Fclose_icon.png\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"width\": \"32px\",\n" +
            "            \"height\": \"32px\",\n" +
            "            \"align\": \"center\",\n" +
            "            \"margin\": {\n" +
            "              \"top\": \"20px\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"IOS\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"ANDROID\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"H5\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": \"V0103\",\n" +
            "    \"cname\": \"纵排按钮\",\n" +
            "    \"properties\": {\n" +
            "      \"closeEnabled\": true,\n" +
            "      \"closeStyle\": {\n" +
            "        \"image\": \"http://sf-static.cn-bj.ufileos.com/product%2Fclose_icon.png\",\n" +
            "        \"width\": \"32px\",\n" +
            "        \"height\": \"32px\"\n" +
            "      },\n" +
            "      \"maskColor\": {\n" +
            "        \"r\": 0,\n" +
            "        \"g\": 0,\n" +
            "        \"b\": 0,\n" +
            "        \"a\": 0.6\n" +
            "      },\n" +
            "      \"maskCloseEnabled\": false\n" +
            "    },\n" +
            "    \"template\": {\n" +
            "      \"type\": \"column\",\n" +
            "      \"layout\": {\n" +
            "        \"width\": \"310px\",\n" +
            "        \"align\": \"center\",\n" +
            "        \"margin\": {\n" +
            "          \"top\": \"20px\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"subviews\": [\n" +
            "        {\n" +
            "          \"type\": \"image_button\",\n" +
            "          \"properties\": {\n" +
            "            \"image\": \"https://s2.ax1x.com/2020/03/04/3oG9mt.png\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"width\": \"30px\",\n" +
            "            \"height\": \"30px\",\n" +
            "            \"align\": \"right\"\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"IOS\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"ANDROID\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"H5\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"column\",\n" +
            "          \"properties\": {\n" +
            "            \"backgroundColor\": {\n" +
            "              \"r\": 255,\n" +
            "              \"g\": 255,\n" +
            "              \"b\": 255,\n" +
            "              \"a\": 1\n" +
            "            },\n" +
            "            \"cornerRadius\": \"5px\",\n" +
            "            \"borderWidth\": \"8px\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"align\": \"center\",\n" +
            "            \"width\": \"310px\",\n" +
            "            \"margin\": {\n" +
            "              \"top\": \"10px\"\n" +
            "            },\n" +
            "            \"padding\": {\n" +
            "              \"bottom\": \"17px\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"subviews\": [\n" +
            "            {\n" +
            "              \"type\": \"image\",\n" +
            "              \"properties\": {\n" +
            "                \"image\": \"https://s2.ax1x.com/2020/03/04/3oMT1I.png\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"align\": \"center\",\n" +
            "                \"width\": \"310px\",\n" +
            "                \"height\": \"220px\"\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"label\",\n" +
            "              \"properties\": {\n" +
            "                \"msgType\": \"title\",\n" +
            "                \"name\": \"标题\",\n" +
            "                \"text\": \"标题文本区域\",\n" +
            "                \"font\": \"24px\",\n" +
            "                \"color\": {\n" +
            "                  \"r\": 0,\n" +
            "                  \"g\": 0,\n" +
            "                  \"b\": 0,\n" +
            "                  \"a\": 1\n" +
            "                },\n" +
            "                \"textAlign\": \"center\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"290px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"10px\"\n" +
            "                }\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"label\",\n" +
            "              \"properties\": {\n" +
            "                \"msgType\": \"content\",\n" +
            "                \"name\": \"内容\",\n" +
            "                \"text\": \"正文文本输入区域\\n正文文本输入区域\",\n" +
            "                \"font\": \"14px\",\n" +
            "                \"color\": {\n" +
            "                  \"r\": 74,\n" +
            "                  \"g\": 74,\n" +
            "                  \"b\": 74,\n" +
            "                  \"a\": 1\n" +
            "                },\n" +
            "                \"lineHeight\": \"24px\",\n" +
            "                \"textAlign\": \"center\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"290px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"10px\"\n" +
            "                }\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"column\",\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"290px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"10px\"\n" +
            "                }\n" +
            "              },\n" +
            "              \"subviews\": [\n" +
            "                {\n" +
            "                  \"type\": \"button\",\n" +
            "                  \"properties\": {\n" +
            "                    \"text\": \"按钮文本 1\",\n" +
            "                    \"font\": \"18px\",\n" +
            "                    \"color\": {\n" +
            "                      \"r\": 255,\n" +
            "                      \"g\": 255,\n" +
            "                      \"b\": 255,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"backgroundColor\": {\n" +
            "                      \"r\": 74,\n" +
            "                      \"g\": 144,\n" +
            "                      \"b\": 226,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"borderWidth\": \"0px\",\n" +
            "                    \"cornerRadius\": \"20px\"\n" +
            "                  },\n" +
            "                  \"layout\": {\n" +
            "                    \"width\": \"290px\",\n" +
            "                    \"height\": \"40px\"\n" +
            "                  }\n" +
            "                },\n" +
            "                {\n" +
            "                  \"type\": \"button\",\n" +
            "                  \"properties\": {\n" +
            "                    \"text\": \"按钮文本 2\",\n" +
            "                    \"font\": \"18px\",\n" +
            "                    \"color\": {\n" +
            "                      \"r\": 255,\n" +
            "                      \"g\": 255,\n" +
            "                      \"b\": 255,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"backgroundColor\": {\n" +
            "                      \"r\": 74,\n" +
            "                      \"g\": 144,\n" +
            "                      \"b\": 226,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"borderWidth\": \"0px\",\n" +
            "                    \"cornerRadius\": \"20px\"\n" +
            "                  },\n" +
            "                  \"layout\": {\n" +
            "                    \"width\": \"290px\",\n" +
            "                    \"height\": \"40px\",\n" +
            "                    \"margin\": {\n" +
            "                      \"top\": \"10px\"\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "              ]\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"image\",\n" +
            "          \"properties\": {\n" +
            "            \"image\": \"http://sf-static.cn-bj.ufileos.com/product%2Fclose_icon.png\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"width\": \"32px\",\n" +
            "            \"height\": \"32px\",\n" +
            "            \"align\": \"center\",\n" +
            "            \"margin\": {\n" +
            "              \"top\": \"20px\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"IOS\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"ANDROID\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"H5\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": \"V01031\",\n" +
            "    \"cname\": \"纵排按钮\",\n" +
            "    \"properties\": {\n" +
            "      \"maskColor\": {\n" +
            "        \"r\": 0,\n" +
            "        \"g\": 0,\n" +
            "        \"b\": 0,\n" +
            "        \"a\": 0.6\n" +
            "      },\n" +
            "      \"maskCloseEnabled\": false\n" +
            "    },\n" +
            "    \"template\": {\n" +
            "      \"type\": \"column\",\n" +
            "      \"layout\": {\n" +
            "        \"width\": \"310px\",\n" +
            "        \"align\": \"center\",\n" +
            "        \"margin\": {\n" +
            "          \"top\": \"20px\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"subviews\": [\n" +
            "        {\n" +
            "          \"type\": \"image_button\",\n" +
            "          \"properties\": {\n" +
            "            \"image\": \"https://s2.ax1x.com/2020/03/04/3oG9mt.png\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"width\": \"30px\",\n" +
            "            \"height\": \"30px\",\n" +
            "            \"align\": \"right\"\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"IOS\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"ANDROID\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"H5\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"column\",\n" +
            "          \"properties\": {\n" +
            "            \"backgroundColor\": {\n" +
            "              \"r\": 255,\n" +
            "              \"g\": 255,\n" +
            "              \"b\": 255,\n" +
            "              \"a\": 1\n" +
            "            },\n" +
            "            \"cornerRadius\": \"5px\",\n" +
            "            \"borderWidth\": \"8px\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"align\": \"center\",\n" +
            "            \"width\": \"310px\",\n" +
            "            \"margin\": {\n" +
            "              \"top\": \"10px\"\n" +
            "            },\n" +
            "            \"padding\": {\n" +
            "              \"bottom\": \"17px\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"subviews\": [\n" +
            "            {\n" +
            "              \"type\": \"image\",\n" +
            "              \"properties\": {\n" +
            "                \"image\": \"https://s2.ax1x.com/2020/03/04/3oMT1I.png\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"align\": \"center\",\n" +
            "                \"width\": \"310px\",\n" +
            "                \"height\": \"220px\"\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"label\",\n" +
            "              \"properties\": {\n" +
            "                \"msgType\": \"title\",\n" +
            "                \"name\": \"标题\",\n" +
            "                \"text\": \"标题文本区域\",\n" +
            "                \"font\": \"24px\",\n" +
            "                \"color\": {\n" +
            "                  \"r\": 0,\n" +
            "                  \"g\": 0,\n" +
            "                  \"b\": 0,\n" +
            "                  \"a\": 1\n" +
            "                },\n" +
            "                \"textAlign\": \"center\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"290px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"10px\"\n" +
            "                }\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"label\",\n" +
            "              \"properties\": {\n" +
            "                \"msgType\": \"content\",\n" +
            "                \"name\": \"内容\",\n" +
            "                \"text\": \"正文文本输入区域\\n正文文本输入区域\",\n" +
            "                \"font\": \"14px\",\n" +
            "                \"color\": {\n" +
            "                  \"r\": 74,\n" +
            "                  \"g\": 74,\n" +
            "                  \"b\": 74,\n" +
            "                  \"a\": 1\n" +
            "                },\n" +
            "                \"lineHeight\": \"24px\",\n" +
            "                \"textAlign\": \"center\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"290px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"10px\"\n" +
            "                }\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"column\",\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"290px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"10px\"\n" +
            "                }\n" +
            "              },\n" +
            "              \"subviews\": [\n" +
            "                {\n" +
            "                  \"type\": \"button\",\n" +
            "                  \"properties\": {\n" +
            "                    \"text\": \"按钮文本 1\",\n" +
            "                    \"font\": \"18px\",\n" +
            "                    \"color\": {\n" +
            "                      \"r\": 255,\n" +
            "                      \"g\": 255,\n" +
            "                      \"b\": 255,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"backgroundColor\": {\n" +
            "                      \"r\": 74,\n" +
            "                      \"g\": 144,\n" +
            "                      \"b\": 226,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"borderWidth\": \"0px\",\n" +
            "                    \"cornerRadius\": \"20px\"\n" +
            "                  },\n" +
            "                  \"layout\": {\n" +
            "                    \"width\": \"290px\",\n" +
            "                    \"height\": \"40px\"\n" +
            "                  }\n" +
            "                },\n" +
            "                {\n" +
            "                  \"type\": \"link\",\n" +
            "                  \"properties\": {\n" +
            "                    \"text\": \"链接文本 2\",\n" +
            "                    \"font\": \"18px\",\n" +
            "                    \"color\": {\n" +
            "                      \"r\": 0,\n" +
            "                      \"g\": 0,\n" +
            "                      \"b\": 255,\n" +
            "                      \"a\": 1\n" +
            "                    }\n" +
            "                  },\n" +
            "                  \"layout\": {\n" +
            "                    \"width\": \"290px\",\n" +
            "                    \"height\": \"40px\",\n" +
            "                    \"margin\": {\n" +
            "                      \"top\": \"10px\"\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "              ]\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"image\",\n" +
            "          \"properties\": {\n" +
            "            \"image\": \"http://sf-static.cn-bj.ufileos.com/product%2Fclose_icon.png\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"width\": \"32px\",\n" +
            "            \"height\": \"32px\",\n" +
            "            \"align\": \"center\",\n" +
            "            \"margin\": {\n" +
            "              \"top\": \"20px\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"IOS\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"ANDROID\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"H5\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": \"V0104\",\n" +
            "    \"cname\": \"并排按钮\",\n" +
            "    \"properties\": {\n" +
            "      \"maskColor\": {\n" +
            "        \"r\": 0,\n" +
            "        \"g\": 0,\n" +
            "        \"b\": 0,\n" +
            "        \"a\": 0.6\n" +
            "      },\n" +
            "      \"maskCloseEnabled\": false\n" +
            "    },\n" +
            "    \"template\": {\n" +
            "      \"type\": \"column\",\n" +
            "      \"layout\": {\n" +
            "        \"width\": \"310px\",\n" +
            "        \"align\": \"center\",\n" +
            "        \"margin\": {\n" +
            "          \"top\": \"20px\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"subviews\": [\n" +
            "        {\n" +
            "          \"type\": \"image_button\",\n" +
            "          \"properties\": {\n" +
            "            \"image\": \"https://s2.ax1x.com/2020/03/04/3oG9mt.png\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"width\": \"30px\",\n" +
            "            \"height\": \"30px\",\n" +
            "            \"align\": \"right\"\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"IOS\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"ANDROID\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"H5\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"column\",\n" +
            "          \"properties\": {\n" +
            "            \"backgroundColor\": {\n" +
            "              \"r\": 255,\n" +
            "              \"g\": 255,\n" +
            "              \"b\": 255,\n" +
            "              \"a\": 1\n" +
            "            },\n" +
            "            \"cornerRadius\": \"5px\",\n" +
            "            \"borderWidth\": \"8px\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"align\": \"center\",\n" +
            "            \"width\": \"310px\",\n" +
            "            \"margin\": {\n" +
            "              \"top\": \"10px\"\n" +
            "            },\n" +
            "            \"padding\": {\n" +
            "              \"bottom\": \"17px\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"subviews\": [\n" +
            "            {\n" +
            "              \"type\": \"image\",\n" +
            "              \"properties\": {\n" +
            "                \"image\": \"https://s2.ax1x.com/2020/03/04/3oMT1I.png\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"align\": \"center\",\n" +
            "                \"width\": \"310px\",\n" +
            "                \"height\": \"220px\"\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"label\",\n" +
            "              \"properties\": {\n" +
            "                \"msgType\": \"title\",\n" +
            "                \"name\": \"标题\",\n" +
            "                \"text\": \"标题文本区域\",\n" +
            "                \"font\": \"24px\",\n" +
            "                \"color\": {\n" +
            "                  \"r\": 0,\n" +
            "                  \"g\": 0,\n" +
            "                  \"b\": 0,\n" +
            "                  \"a\": 1\n" +
            "                },\n" +
            "                \"textAlign\": \"center\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"290px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"10px\"\n" +
            "                }\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"label\",\n" +
            "              \"properties\": {\n" +
            "                \"msgType\": \"content\",\n" +
            "                \"name\": \"内容\",\n" +
            "                \"text\": \"正文文本输入区域\\n正文文本输入区域\",\n" +
            "                \"font\": \"14px\",\n" +
            "                \"color\": {\n" +
            "                  \"r\": 74,\n" +
            "                  \"g\": 74,\n" +
            "                  \"b\": 74,\n" +
            "                  \"a\": 1\n" +
            "                },\n" +
            "                \"lineHeight\": \"24px\",\n" +
            "                \"textAlign\": \"center\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"290px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"10px\"\n" +
            "                }\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"row\",\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"290px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"10px\"\n" +
            "                }\n" +
            "              },\n" +
            "              \"subviews\": [\n" +
            "                {\n" +
            "                  \"type\": \"button\",\n" +
            "                  \"properties\": {\n" +
            "                    \"text\": \"按钮文本 1\",\n" +
            "                    \"font\": \"18px\",\n" +
            "                    \"color\": {\n" +
            "                      \"r\": 255,\n" +
            "                      \"g\": 255,\n" +
            "                      \"b\": 255,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"backgroundColor\": {\n" +
            "                      \"r\": 74,\n" +
            "                      \"g\": 144,\n" +
            "                      \"b\": 226,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"borderWidth\": \"0px\",\n" +
            "                    \"cornerRadius\": \"20px\"\n" +
            "                  },\n" +
            "                  \"layout\": {\n" +
            "                    \"width\": \"140px\",\n" +
            "                    \"height\": \"40px\"\n" +
            "                  }\n" +
            "                },\n" +
            "                {\n" +
            "                  \"type\": \"button\",\n" +
            "                  \"properties\": {\n" +
            "                    \"text\": \"按钮文本 2\",\n" +
            "                    \"font\": \"18px\",\n" +
            "                    \"color\": {\n" +
            "                      \"r\": 255,\n" +
            "                      \"g\": 255,\n" +
            "                      \"b\": 255,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"backgroundColor\": {\n" +
            "                      \"r\": 74,\n" +
            "                      \"g\": 144,\n" +
            "                      \"b\": 226,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"borderWidth\": \"0px\",\n" +
            "                    \"cornerRadius\": \"20px\"\n" +
            "                  },\n" +
            "                  \"layout\": {\n" +
            "                    \"width\": \"140px\",\n" +
            "                    \"height\": \"40px\",\n" +
            "                    \"margin\": {\n" +
            "                      \"left\": \"10px\"\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "              ]\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"image\",\n" +
            "          \"properties\": {\n" +
            "            \"image\": \"http://sf-static.cn-bj.ufileos.com/product%2Fclose_icon.png\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"width\": \"32px\",\n" +
            "            \"height\": \"32px\",\n" +
            "            \"align\": \"center\",\n" +
            "            \"margin\": {\n" +
            "              \"top\": \"20px\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"IOS\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"ANDROID\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"H5\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": \"V0105\",\n" +
            "    \"cname\": \"文字\",\n" +
            "    \"properties\": {\n" +
            "      \"closeEnabled\": true,\n" +
            "      \"closeStyle\": {\n" +
            "        \"image\": \"http://sf-static.cn-bj.ufileos.com/product%2Fclose_icon.png\",\n" +
            "        \"width\": \"32px\",\n" +
            "        \"height\": \"32px\"\n" +
            "      },\n" +
            "      \"maskColor\": {\n" +
            "        \"r\": 0,\n" +
            "        \"g\": 0,\n" +
            "        \"b\": 0,\n" +
            "        \"a\": 0.6\n" +
            "      },\n" +
            "      \"maskCloseEnabled\": false\n" +
            "    },\n" +
            "    \"template\": {\n" +
            "      \"type\": \"column\",\n" +
            "      \"layout\": {\n" +
            "        \"width\": \"310px\",\n" +
            "        \"align\": \"center\",\n" +
            "        \"margin\": {\n" +
            "          \"top\": \"20px\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"subviews\": [\n" +
            "        {\n" +
            "          \"type\": \"image_button\",\n" +
            "          \"properties\": {\n" +
            "            \"image\": \"https://s2.ax1x.com/2020/03/04/3oG9mt.png\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"width\": \"30px\",\n" +
            "            \"height\": \"30px\",\n" +
            "            \"align\": \"right\"\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"IOS\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"ANDROID\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"H5\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_topRight\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"column\",\n" +
            "          \"properties\": {\n" +
            "            \"backgroundColor\": {\n" +
            "              \"r\": 255,\n" +
            "              \"g\": 255,\n" +
            "              \"b\": 255,\n" +
            "              \"a\": 1\n" +
            "            },\n" +
            "            \"cornerRadius\": \"5px\",\n" +
            "            \"borderWidth\": \"8px\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"align\": \"center\",\n" +
            "            \"width\": \"310px\",\n" +
            "            \"margin\": {\n" +
            "              \"top\": \"10px\"\n" +
            "            },\n" +
            "            \"padding\": {\n" +
            "              \"bottom\": \"10px\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"subviews\": [\n" +
            "            {\n" +
            "              \"type\": \"label\",\n" +
            "              \"properties\": {\n" +
            "                \"msgType\": \"title\",\n" +
            "                \"name\": \"标题\",\n" +
            "                \"text\": \"惊爆你的眼球震惊你的神经惊爆你的眼球震惊你的神经惊爆你的眼球震惊你的神经\",\n" +
            "                \"font\": \"22px\",\n" +
            "                \"color\": {\n" +
            "                  \"r\": 0,\n" +
            "                  \"g\": 0,\n" +
            "                  \"b\": 0,\n" +
            "                  \"a\": 1\n" +
            "                },\n" +
            "                \"textAlign\": \"center\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"310px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {},\n" +
            "                \"padding\": {\n" +
            "                  \"top\": \"13px\",\n" +
            "                  \"right\": \"13px\",\n" +
            "                  \"left\": \"13px\"\n" +
            "                }\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"label\",\n" +
            "              \"properties\": {\n" +
            "                \"msgType\": \"content\",\n" +
            "                \"name\": \"内容\",\n" +
            "                \"text\": \"大促销大促销大促销大促销大促销大促销大促销大促销大促销大促销大促销大大促销大促销大促销大促销大促销大促销大促销大促销大促销大促销大促销大大促销大促销大促销大促销大促销大促销大促销大促销大促销大促销大促销大大促销大促销大促销大促销大促销大促销大促销大促销大促销大促销大促销大大促销大促销大促销大促销大促销大促\",\n" +
            "                \"font\": \"16px\",\n" +
            "                \"color\": {\n" +
            "                  \"r\": 74,\n" +
            "                  \"g\": 74,\n" +
            "                  \"b\": 74,\n" +
            "                  \"a\": 1\n" +
            "                },\n" +
            "                \"lineHeight\": \"24px\",\n" +
            "                \"textAlign\": \"center\"\n" +
            "              },\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"284px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"6px\"\n" +
            "                },\n" +
            "                \"padding\": {\n" +
            "                  \"right\": \"13px\",\n" +
            "                  \"left\": \"13px\"\n" +
            "                }\n" +
            "              }\n" +
            "            },\n" +
            "            {\n" +
            "              \"type\": \"column\",\n" +
            "              \"layout\": {\n" +
            "                \"width\": \"290px\",\n" +
            "                \"align\": \"center\",\n" +
            "                \"margin\": {\n" +
            "                  \"top\": \"10px\"\n" +
            "                }\n" +
            "              },\n" +
            "              \"subviews\": [\n" +
            "                {\n" +
            "                  \"type\": \"button\",\n" +
            "                  \"properties\": {\n" +
            "                    \"text\": \"按钮文本 1\",\n" +
            "                    \"font\": \"18px\",\n" +
            "                    \"color\": {\n" +
            "                      \"r\": 255,\n" +
            "                      \"g\": 255,\n" +
            "                      \"b\": 255,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"backgroundColor\": {\n" +
            "                      \"r\": 74,\n" +
            "                      \"g\": 144,\n" +
            "                      \"b\": 226,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"borderWidth\": \"0px\",\n" +
            "                    \"cornerRadius\": \"20px\"\n" +
            "                  },\n" +
            "                  \"layout\": {\n" +
            "                    \"width\": \"290px\",\n" +
            "                    \"height\": \"40px\"\n" +
            "                  }\n" +
            "                },\n" +
            "                {\n" +
            "                  \"type\": \"button\",\n" +
            "                  \"properties\": {\n" +
            "                    \"text\": \"按钮文本 2\",\n" +
            "                    \"font\": \"18px\",\n" +
            "                    \"color\": {\n" +
            "                      \"r\": 255,\n" +
            "                      \"g\": 255,\n" +
            "                      \"b\": 255,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"backgroundColor\": {\n" +
            "                      \"r\": 74,\n" +
            "                      \"g\": 144,\n" +
            "                      \"b\": 226,\n" +
            "                      \"a\": 1\n" +
            "                    },\n" +
            "                    \"borderWidth\": \"0px\",\n" +
            "                    \"cornerRadius\": \"20px\"\n" +
            "                  },\n" +
            "                  \"layout\": {\n" +
            "                    \"width\": \"290px\",\n" +
            "                    \"height\": \"40px\",\n" +
            "                    \"margin\": {\n" +
            "                      \"top\": \"10px\"\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "              ]\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"image\",\n" +
            "          \"properties\": {\n" +
            "            \"image\": \"http://sf-static.cn-bj.ufileos.com/product%2Fclose_icon.png\"\n" +
            "          },\n" +
            "          \"layout\": {\n" +
            "            \"width\": \"32px\",\n" +
            "            \"height\": \"32px\",\n" +
            "            \"align\": \"center\",\n" +
            "            \"margin\": {\n" +
            "              \"top\": \"20px\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"action\": {\n" +
            "            \"IOS\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"ANDROID\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"H5\": [\n" +
            "              {\n" +
            "                \"id\": \"id001\",\n" +
            "                \"type\": \"close\",\n" +
            "                \"$sf_close_type\": \"popup_close_bottom\"\n" +
            "              }\n" +
            "            ]\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }\n" +
            "]";

    String planJson = "{\n" +
            "  \"min_sdk_version_required\": \"string\",\n" +
            "  \"server_current_time\": 0,\n" +
            "  \"msg_limit_global\": {\n" +
            "    \"is_in_use\": true,\n" +
            "    \"limits\": [\n" +
            "      {\n" +
            "        \"unit\": \"DAY\",\n" +
            "        \"natural\": true,\n" +
            "        \"value\": 1,\n" +
            "        \"limit\": 1\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"popup_interval_global\": {\n" +
            "    \"value\": 1,\n" +
            "    \"unit\": \"string\",\n" +
            "    \"natural\": true\n" +
            "  },\n" +
            "  \"popup_plans\": [\n" +
            "    {\n" +
            "      \"plan_id\": 1,\n" +
            "      \"is_audience\": true,\n" +
            "      \"is_control_group\": true,\n" +
            "      \"audience_id\": 1,\n" +
            "      \"status\": \"PENDING\",\n" +
            "      \"cname\": \"string\",\n" +
            "      \"platform\": \"IOS\",\n" +
            "      \"popup_window_content\": {\n" +
            "        \"content\": \"string\"\n" +
            "      },\n" +
            "      \"absolute_priority\": 1,\n" +
            "      \"last_update_config_time\": 1,\n" +
            "      \"expire_at\": 1,\n" +
            "      \"re_enter\": {\n" +
            "        \"unit\": \"MINUTE\",\n" +
            "        \"value\": 1,\n" +
            "        \"limit\": 1\n" +
            "      },\n" +
            "      \"popup_interval\": {\n" +
            "        \"value\": 1,\n" +
            "        \"unit\": \"string\",\n" +
            "        \"natural\": true\n" +
            "      },\n" +
            "      \"page_filter\": {\n" +
            "        \"function\": \"ABS_MATCH\",\n" +
            "        \"params\": [\n" +
            "          \"string\"\n" +
            "        ]\n" +
            "      },\n" +
            "      \"pattern_popup\": {\n" +
            "        \"relation\": \"AND\",\n" +
            "        \"matcher_list\": [\n" +
            "          {\n" +
            "            \"type\": \"COUNT_TRIGGERED\",\n" +
            "            \"window\": {\n" +
            "              \"value\": 1,\n" +
            "              \"unit\": \"string\",\n" +
            "              \"natural\": true\n" +
            "            },\n" +
            "            \"measure\": \"GENERAL\",\n" +
            "            \"function\": \"EQUAL\",\n" +
            "            \"params\": [\n" +
            "              \"string\"\n" +
            "            ],\n" +
            "            \"event_name\": \"string\",\n" +
            "            \"filter\": {\n" +
            "              \"relation\": \"string\",\n" +
            "              \"conditions\": [\n" +
            "                {\n" +
            "                  \"field\": \"string\",\n" +
            "                  \"function\": \"string\",\n" +
            "                  \"params\": [\n" +
            "                    {}\n" +
            "                  ]\n" +
            "                }\n" +
            "              ]\n" +
            "            }\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"convert_window\": {\n" +
            "        \"value\": 1,\n" +
            "        \"unit\": \"string\",\n" +
            "        \"natural\": true\n" +
            "      },\n" +
            "      \"global_msg_limit_enabled\": false\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}
