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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sensorsdata.sf.core.utils.SFLog;
import com.sensorsdata.sf.ui.utils.ImageLoader;
import com.sensorsdata.sf.ui.utils.SizeUtil;
import com.sensorsdata.sf.ui.widget.SFLinearLayout;

import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.Queue;

final class LinearLayoutDynamic extends AbstractViewDynamic {
    private String mType;
    private boolean isOuterView;
    private Queue<AbstractViewDynamic> childViews;
    private JSONObject mUIJson;

    private static final String TAG = "LinearLayoutDynamic";
    private String mImageUrl;
    private Bitmap bitmap;
    private boolean isImageLoaded = true;

    LinearLayoutDynamic(Context context, boolean isOuterView, JSONObject uiJson) {
        super(context, uiJson);
        try {
            this.isOuterView = isOuterView;
            this.mUIJson = uiJson;
            childViews = new ArrayDeque<>();
            if (mPropertyJson != null) {
                cornerRadius = realSize(context, mPropertyJson.optString(UIProperty.cornerRadius));
                mImageUrl = mPropertyJson.optString(UIProperty.backgroundImage);
                if (!TextUtils.isEmpty(mImageUrl)) {
                    bitmap = ImageLoader.getInstance(context).loadBitmap(mImageUrl);
                    if (bitmap == null) {
                        isImageLoaded = false;
                        SFLog.d(TAG, mImageUrl + ",Image load failed.");
                    }
                }
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }

    String getImageUrl() {
        return mImageUrl;
    }

    boolean isImageLoaded() {
        return isImageLoaded;
    }

    @Override
    public LinearLayout createView(Activity activity) {
        try {
            mView = new SFLinearLayout(activity, mActionJson);
            mType = mUIJson.optString(UIProperty.type);
            return (LinearLayout) super.createView(activity);
        } catch (Exception ex) {
            // ignore
        }
        return null;
    }

    @Override
    void setBackground(Context context, JSONObject jsonObject) {
        super.setBackground(context, jsonObject);
        if (jsonObject.has(UIProperty.backgroundImage)) {
            String url = jsonObject.optString(UIProperty.backgroundImage);
            ImageLoader imageLoader = ImageLoader.getInstance(mContext);
            if (imageLoader == null) {
                return;
            }
            Bitmap bitmap = imageLoader.loadBitmap(url);
            if (bitmap == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mView.setBackground(new BitmapDrawable(mContext.getResources(), bitmap));
            } else {
                mView.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
            }
        }
    }

    @Override
    void setViewProperty(JSONObject propertyJson) {
        try {
            // 针对最外层的 LinearLayout 没有 property 属性的问题，需要单独判断
            if (propertyJson != null) {
                setBackground(mContext, propertyJson);
                mView.setVisibility(visible(propertyJson.optBoolean(UIProperty.isHidden, false)));
            }
        } catch (Exception ex) {
            // ignore
        }
    }

    @Override
    void layoutView(JSONObject layoutJson) {
        if (isPORTRAIT) {
            try {
                width = realSize(mContext, layoutJson.optString(UIProperty.width));
                height = realSize(mContext, layoutJson.optString(UIProperty.height));
                int align = align(layoutJson.optString(UIProperty.align));
                JSONObject jsonObject = layoutJson.optJSONObject(UIProperty.margin);
                if (width == 0) {
                    width = FrameLayout.LayoutParams.WRAP_CONTENT;
                }
                if (height == 0) {
                    height = FrameLayout.LayoutParams.WRAP_CONTENT;
                }
                if (isOuterView) {
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
                    layoutParams.gravity = Gravity.CENTER;
                    if (jsonObject != null) {
                        layoutParams.setMargins(
                                0,
                                SizeUtil.dip2px(mContext, -15),
                                0,
                                0);
                    }
                    mView.setLayoutParams(layoutParams);
                } else {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                    layoutParams.gravity = align;
                    if (jsonObject != null) {
                        layoutParams.setMargins(
                                realSize(mContext, jsonObject.optString(UIProperty.left)),
                                realSize(mContext, jsonObject.optString(UIProperty.top)),
                                realSize(mContext, jsonObject.optString(UIProperty.right)),
                                realSize(mContext, jsonObject.optString(UIProperty.bottom)));
                    }
                    mView.setLayoutParams(layoutParams);
                }

                ((LinearLayout) mView).setOrientation(orientation(mType));
                jsonObject = layoutJson.optJSONObject(UIProperty.padding);
                if (jsonObject != null) {
                    mView.setPadding(
                            realSize(mContext, jsonObject.optString(UIProperty.left)),
                            realSize(mContext, jsonObject.optString(UIProperty.top)),
                            realSize(mContext, jsonObject.optString(UIProperty.right)),
                            realSize(mContext, jsonObject.optString(UIProperty.bottom)));
                }
            } catch (Exception ex) {
                //ignore
            }
        } else {
            try {
                if (isOuterView) {
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    mView.setLayoutParams(layoutParams);
                } else {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = align(layoutJson.optString(UIProperty.align));
                    JSONObject jsonObject = layoutJson.optJSONObject(UIProperty.margin);
                    if (jsonObject != null) {
                        layoutParams.setMargins(
                                realSize(mContext, jsonObject.optString(UIProperty.left)) / 2,
                                realSize(mContext, jsonObject.optString(UIProperty.top)) / 2,
                                realSize(mContext, jsonObject.optString(UIProperty.right)) / 2,
                                realSize(mContext, jsonObject.optString(UIProperty.bottom)) / 2);
                    }
                    mView.setLayoutParams(layoutParams);
                }

                ((LinearLayout) mView).setOrientation(orientation(mType));
                JSONObject jsonObject = layoutJson.optJSONObject(UIProperty.padding);
                if (jsonObject != null) {
                    mView.setPadding(
                            realSize(mContext, jsonObject.optString(UIProperty.left)) / 2,
                            realSize(mContext, jsonObject.optString(UIProperty.top)) / 2,
                            realSize(mContext, jsonObject.optString(UIProperty.right)) / 2,
                            realSize(mContext, jsonObject.optString(UIProperty.bottom)) / 2);
                }
            } catch (Exception ex) {
                //ignore
            }
        }
    }

    private int orientation(String type) {
        if (UIProperty.type_row.equals(type)) {
            return LinearLayout.HORIZONTAL;
        } else {
            return LinearLayout.VERTICAL;
        }
    }

    Queue<AbstractViewDynamic> getChildViews() {
        return childViews;
    }

    void addChildView(AbstractViewDynamic childView) {
        this.childViews.add(childView);
    }
}
