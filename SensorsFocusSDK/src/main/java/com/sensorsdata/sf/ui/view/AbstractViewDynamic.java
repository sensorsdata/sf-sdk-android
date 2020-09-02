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
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.sensorsdata.sf.core.utils.SFLog;
import com.sensorsdata.sf.ui.utils.SizeUtil;

import org.json.JSONArray;
import org.json.JSONObject;

abstract class AbstractViewDynamic implements IViewDynamic {
    View mView;
    Context mContext;
    int width;
    int height;
    boolean isPORTRAIT = true;
    /**
     * 控件类型
     */
    String mType;
    /**
     * 控件文本内容
     */
    String mText;

    /**
     * Action 操作
     */
    JSONObject mActionJson;
    /**
     * 布局信息
     */
    protected JSONObject mLayoutJson;
    /**
     * 控件属性信息
     */
    JSONObject mPropertyJson;

    int cornerRadius;

    AbstractViewDynamic(Context mContext, JSONObject jsonObject) {
        this.mContext = mContext;
        try {
            if (jsonObject != null) {
                mLayoutJson = jsonObject.optJSONObject(UIProperty.layout);
                mPropertyJson = jsonObject.optJSONObject(UIProperty.properties);
                mActionJson = jsonObject.optJSONObject(UIProperty.action);
            }

        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }

    @Override
    public View createView(Activity activity) {
        try {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                int rotation = windowManager.getDefaultDisplay().getRotation();
                isPORTRAIT = rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180;
            }

            layoutView(mLayoutJson);
            setViewProperty(mPropertyJson);
            handleActions(mActionJson);
            mView.setTag(this);
            return mView;
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
            return null;
        }
    }

    /**
     * 设置 View 的属性
     *
     * @param propertyJson 属性 Json
     */
    abstract void setViewProperty(JSONObject propertyJson);

    public String getType() {
        return mType;
    }

    public String getText() {
        return mText;
    }

    public JSONObject getActionJson() {
        return mActionJson;
    }

    private void handleActions(JSONObject actionJson) {
        try {
            // 触发埋点上报
            if (actionJson != null) {
                JSONArray jsonArray = actionJson.optJSONArray(UIProperty.action_android);
                if (jsonArray != null) {
                    this.mActionJson = jsonArray.getJSONObject(0);
                }
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }

    /**
     * 设置 View 的布局
     *
     * @param layoutJson 布局配置 Json
     */
    void layoutView(JSONObject layoutJson) {
        try {
            width = realSize(mContext, layoutJson.optString(UIProperty.width));
            height = realSize(mContext, layoutJson.optString(UIProperty.height));
            int align = align(layoutJson.optString(UIProperty.align));
            if (width == 0) {
                width = LinearLayout.LayoutParams.WRAP_CONTENT;
            }

            if (height == 0) {
                height = LinearLayout.LayoutParams.WRAP_CONTENT;
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            layoutParams.gravity = align;
            JSONObject jsonObject = layoutJson.optJSONObject(UIProperty.margin);
            if (jsonObject != null) {
                if (isPORTRAIT) {
                    layoutParams.setMargins(
                            realSize(mContext, jsonObject.optString(UIProperty.left)),
                            realSize(mContext, jsonObject.optString(UIProperty.top)),
                            realSize(mContext, jsonObject.optString(UIProperty.right)),
                            realSize(mContext, jsonObject.optString(UIProperty.bottom)));
                } else {
                    layoutParams.setMargins(
                            realSize(mContext, jsonObject.optString(UIProperty.left)) / 2,
                            realSize(mContext, jsonObject.optString(UIProperty.top)) / 2,
                            realSize(mContext, jsonObject.optString(UIProperty.right)) / 2,
                            realSize(mContext, jsonObject.optString(UIProperty.bottom)) / 2);
                }
            }
            mView.setLayoutParams(layoutParams);
            jsonObject = layoutJson.optJSONObject(UIProperty.padding);
            if (jsonObject != null) {
                mView.setPadding(
                        realSize(mContext, jsonObject.optString(UIProperty.left)),
                        realSize(mContext, jsonObject.optString(UIProperty.top)),
                        realSize(mContext, jsonObject.optString(UIProperty.right)),
                        realSize(mContext, jsonObject.optString(UIProperty.bottom)));
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }

    /**
     * 实际大小
     */
    int realSize(Context context, String sizePx) {
        try {
            return SizeUtil.realSize(context, sizePx);
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return 0;
    }

    /**
     * 实际大小
     */
    int realFontSize(String sizePx) {
        try {
            return Integer.parseInt(sizePx.replace("px", ""));
        } catch (Exception ex) {
            // ignore
        }
        return 15;
    }

    /**
     * 颜色值
     */
    int color(JSONObject jsonObject) {
        try {
            return Color.argb(
                    (int) Math.round(jsonObject.optDouble(UIProperty.a) * 255),
                    (int) jsonObject.optDouble(UIProperty.r),
                    (int) jsonObject.optDouble(UIProperty.g),
                    (int) jsonObject.optDouble(UIProperty.b));
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return 0;
    }

    /**
     * 设置背景
     */
    void setBackground(Context context, JSONObject jsonObject) {
        try {
            cornerRadius = realSize(context, jsonObject.optString(UIProperty.cornerRadius));
            int borderWidth = realSize(context, jsonObject.optString(UIProperty.borderWidth));
            if (cornerRadius == 0 && borderWidth == 0) {
                if (jsonObject.has(UIProperty.backgroundColor)) {
                    mView.setBackgroundColor(color(jsonObject.optJSONObject(UIProperty.backgroundColor)));
                }
            } else {
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                gradientDrawable.setCornerRadius(cornerRadius);
                if (jsonObject.has(UIProperty.backgroundColor)) {
                    gradientDrawable.setColor(color(jsonObject.optJSONObject(UIProperty.backgroundColor)));
                }
                if (jsonObject.has(UIProperty.borderColor)) {
                    gradientDrawable.setStroke(borderWidth, color(jsonObject.optJSONObject(UIProperty.borderColor)));
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mView.setBackground(gradientDrawable);
                } else {
                    mView.setBackgroundDrawable(gradientDrawable);
                }
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }

    /**
     * 获取对齐位置
     */
    int align(String align) {
        switch (align) {
            case "left":
                return Gravity.START;
            case "right":
                return Gravity.END;
            case "center":
                return Gravity.CENTER_HORIZONTAL;
            default:
                return Gravity.CENTER;
        }
    }

    int visible(boolean isHidden) {
        return isHidden ? View.GONE : View.VISIBLE;
    }
}