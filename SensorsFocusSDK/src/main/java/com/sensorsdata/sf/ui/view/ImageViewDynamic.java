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
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sensorsdata.sf.core.utils.SFLog;
import com.sensorsdata.sf.ui.utils.ImageLoader;
import com.sensorsdata.sf.ui.widget.RoundedBitmapDrawable;
import com.sensorsdata.sf.ui.widget.RoundedBitmapDrawableFactory;
import com.sensorsdata.sf.ui.widget.SFImageView;

import org.json.JSONObject;

final class ImageViewDynamic extends AbstractViewDynamic {
    private static final String TAG = "ImageViewDynamic";
    private boolean isImageLoaded = true;
    private String mImageUrl;
    private Bitmap bitmap;
    // 父控件中只有一个图片控件时，特指仅有图片的布局，要处理所有四角圆角，否则只处理顶部两个圆角
    private boolean mIsCornerAll;

    ImageViewDynamic(Context context, String type, int cornerRadius,boolean isCornerAll, JSONObject uiJson) {
        super(context, uiJson);
        this.mType = type;
        this.cornerRadius = cornerRadius;
        this.mIsCornerAll = isCornerAll;
        if (mPropertyJson != null) {
            this.mImageUrl = mPropertyJson.optString(UIProperty.image);
            String mLocalImage = mPropertyJson.optString(UIProperty.localImageName);
            ApplicationInfo appInfo = mContext.getApplicationInfo();
            int resourceId = mContext.getResources().getIdentifier(mLocalImage, "drawable", appInfo.packageName);
            if (resourceId == 0) {
                resourceId = mContext.getResources().getIdentifier(mLocalImage, "mipmap", appInfo.packageName);
            }

            if (resourceId == 0) {
                bitmap = ImageLoader.getInstance(context).loadBitmap(mImageUrl);
            } else {
                bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
            }

            if (bitmap == null) {
                isImageLoaded = false;
                SFLog.d(TAG, "Image load failed.");
            }
        }
    }

    @Override
    public ImageView createView(Activity activity) {
        try {
            mView = new SFImageView(activity, mActionJson);
            return (ImageView) super.createView(activity);
        } catch (Exception ex) {
            // ignore
        }
        return null;
    }

    @Override
    void setViewProperty(JSONObject propertyJson) {
        try {
            // 需要补充设置 BitMap
            setBackground(mContext, propertyJson);
            mView.setVisibility(visible(propertyJson.optBoolean(UIProperty.isHidden, false)));
        } catch (Exception ex) {
            // ignore
        }
    }

    @Override
    void setBackground(Context context, JSONObject jsonObject) {
        try {
            /*
                圆角图片的逻辑处理过于复杂，首先圆角在外部的遮罩层，图片属性中没有圆角，如果仅仅设置外部会导致图片把顶部圆角覆盖掉，所以图片也需要间接设置圆角，但是只设置顶部，底部的两角不用设置。
                另外，由于有单个图片的样式要求是四个角设置，所以得做特殊处理。
             */
            int cornerRadius = this.cornerRadius == 0 ? realSize(context, jsonObject.optString(UIProperty.cornerRadius)) : this.cornerRadius;
            Bitmap.createScaledBitmap(bitmap, width, height, true);
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap, mIsCornerAll); //创建 RoundedBitmapDrawable 对象
            roundedBitmapDrawable.setCornerRadius(cornerRadius);
            roundedBitmapDrawable.setAntiAlias(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mView.setBackground(roundedBitmapDrawable);
            }
            ((ImageView) mView).setScaleType(ImageView.ScaleType.CENTER_CROP);
        } catch (Exception ex) {
            // ignore
        }
    }

    @Override
    void layoutView(JSONObject layoutJson) {
        if (isPORTRAIT) {
            super.layoutView(layoutJson);
        } else {
            try {
                width = realSize(mContext, layoutJson.optString(UIProperty.width)) / 2;
                height = realSize(mContext, layoutJson.optString(UIProperty.height)) / 2;
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
                    layoutParams.setMargins(
                            realSize(mContext, jsonObject.optString(UIProperty.left)),
                            realSize(mContext, jsonObject.optString(UIProperty.top)),
                            realSize(mContext, jsonObject.optString(UIProperty.right)),
                            realSize(mContext, jsonObject.optString(UIProperty.bottom)));
                }
                mView.setLayoutParams(layoutParams);
            } catch (Exception ex) {
                //ignore
            }
        }
    }

    boolean isImageLoaded() {
        return isImageLoaded;
    }

    @Override
    public String getType() {
        return this.mType;
    }

    @Override
    public String getText() {
        return this.mText;
    }

    @Override
    public JSONObject getActionJson() {
        return this.mActionJson;
    }

    String getImageUrl() {
        return mImageUrl;
    }
}