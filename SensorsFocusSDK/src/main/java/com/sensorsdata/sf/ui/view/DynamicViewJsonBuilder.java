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

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.sensorsdata.sf.core.SensorsFocusAPI;
import com.sensorsdata.sf.core.entity.GlobalData;
import com.sensorsdata.sf.core.entity.PopupPlan;
import com.sensorsdata.sf.core.utils.SFLog;
import com.sensorsdata.sf.core.utils.TipUtils;
import com.sensorsdata.sf.ui.listener.PopupListener;
import com.sensorsdata.sf.ui.track.SFTrackHelper;
import com.sensorsdata.sf.ui.utils.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class DynamicViewJsonBuilder {
    private static final String TAG = "DynamicViewJsonBuilder";
    private JSONObject mJsonPlan;
    private String mPlanId = "";
    private String mTitle;
    private String mContent;
    private boolean mIsControlGroup;
    // 图片加载是否成功
    private boolean mImageSucceed = true;
    private boolean mViewCreatedSucceed = true;
    static boolean dialogIsShowing = false;
    private Context mContext;
    private String mImageUrl;

    public DynamicViewJsonBuilder() {

    }

    public DynamicViewJsonBuilder(Context context, String planId) {
        try {
            this.mContext = context;
            mPlanId = planId;
        } catch (Exception ex) {
            // ignore
        }
    }

    public void showDialog() {
        try {
            long planId = 0;
            if (!TextUtils.isEmpty(mPlanId)) {
                planId = Long.parseLong(mPlanId);
            }
            PopupPlan popupPlan = ((SensorsFocusAPI) SensorsFocusAPI.shareInstance()).getPopupPlan(planId);
            mJsonPlan = SFTrackHelper.buildPlanProperty(popupPlan);
            if (popupPlan != null) {
                mIsControlGroup = popupPlan.isControlGroup;
                String jsonUI = popupPlan.popupWindowContent.optString(UIProperty.content_type);
                MaskViewDynamic maskViewDynamic = createViewFromJson(mContext, new JSONObject(jsonUI), false);
                if (!mViewCreatedSucceed || maskViewDynamic == null) {
                    return;
                }
                String uuid = UUID.randomUUID().toString();
                DynamicViewHelper dynamicViewHelper = new DynamicViewHelper(mPlanId, mJsonPlan);
                dynamicViewHelper.addViewDynamic(uuid, maskViewDynamic);
                startDialogActivity(uuid);
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }

    public void showDialogPreview(final Context context, final JSONObject jsonView) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mContext = context;
                    mJsonPlan = SFTrackHelper.buildPlanProperty(null);
                    MaskViewDynamic maskViewDynamic = createViewFromJson(mContext, jsonView, true);
                    if (!mViewCreatedSucceed || maskViewDynamic == null) {
                        Looper.prepare();
                        toastInPreview();
                        Looper.loop();
                        return;
                    }
                    String uuid = UUID.randomUUID().toString();
                    DynamicViewHelper dynamicViewHelper = new DynamicViewHelper(mPlanId, mJsonPlan);
                    dynamicViewHelper.addViewDynamic(uuid, maskViewDynamic);
                    startDialogActivity(uuid);
                }
            }).start();
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }

    private void startDialogActivity(String uuid) {
        try {
            Intent intent = new Intent(mContext, DialogActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(UIProperty.TAG, uuid);
            intent.putExtra(UIProperty.plan_id, mPlanId);
            mContext.startActivity(intent);
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }

    /*
     * pre loading images
     */
    public static void preLoadImage(Context context, GlobalData globalData) {
        try {
            SFLog.d(TAG, "preLoadImage");
            if (globalData == null) {
                return;
            }
            List<PopupPlan> planList = globalData.popupPlans;
            for (PopupPlan popupPlan : planList) {
                JSONObject viewJson = popupPlan.popupWindowContent;
                JSONObject jsonObject = viewJson.optJSONObject(UIProperty.template);
                if (jsonObject == null) {
                    continue;
                }
                JSONArray jsonArray = jsonObject.optJSONArray(UIProperty.subviews);
                if (jsonArray == null) continue;
                for (int viewIndex = 0; viewIndex < jsonArray.length(); viewIndex++) {
                    viewJson = jsonArray.getJSONObject(viewIndex);
                    if (viewJson.getString(UIProperty.type).startsWith(UIProperty.image)) {
                        jsonObject = viewJson.optJSONObject(UIProperty.properties);
                        if (jsonObject != null) {
                            String url = jsonObject.optString(UIProperty.image);
                            ImageLoader.getInstance(context).loadBitmap(url);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }

    /**
     * 创建 View 视图
     *
     * @param context Context
     * @param viewJson 布局 Json
     * @return MaskViewDynamic
     */
    private MaskViewDynamic createViewFromJson(final Context context, JSONObject viewJson, boolean isPreview) {
        try {
            if (dialogIsShowing && !isPreview) {//如果老弹窗已经展示，并且不是测试发送，新弹窗不用展示
                return null;
            }
            JSONObject propertyJson = viewJson.optJSONObject(UIProperty.properties);
            JSONObject templateJson = viewJson.optJSONObject(UIProperty.template);
            if (templateJson != null) {
                LinearLayoutDynamic linearLayoutDynamic = createSubView(context, templateJson, true);
                if (!mIsControlGroup && linearLayoutDynamic != null && mImageSucceed) {// View 创建成功，并且图片没有加载失败，并且不是对照组
                    SFTrackHelper.trackPlanPopupDisplay(this.mTitle, this.mContent, this.mImageUrl, true, "", mJsonPlan);
                    return handleMaskLayout(context, linearLayoutDynamic, propertyJson);
                }
            }

            // 标记当前 View 没有创建成功
            mViewCreatedSucceed = false;
            if (!mImageSucceed) {
                TipUtils.setErrorCode(TipUtils.IMAGE_LOAD_FAILED);
            } else {
                TipUtils.setErrorCode(TipUtils.JSON_ERROR);
            }
            // 执行到此处，说明已经加载失败，失败也要进行埋点
            if (mIsControlGroup) {
                SFTrackHelper.trackPlanPopupDisplay(this.mTitle, this.mContent, this.mImageUrl, false, "对照组", mJsonPlan);
            } else {
                SFTrackHelper.trackPlanPopupDisplay(this.mTitle, this.mContent, this.mImageUrl, false, TipUtils.getErrorMessage(), mJsonPlan);
                PopupListener popupListener = ((SensorsFocusAPI) SensorsFocusAPI.shareInstance()).getWindowListener();
                if (popupListener != null) {
                    popupListener.onPopupLoadFailed(String.valueOf(mPlanId), TipUtils.getErrorCode(), TipUtils.getErrorMessage());
                }
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return null;
    }

    private LinearLayoutDynamic createSubView(final Context context, JSONObject subViewJson, boolean isOuterView) {
        try {
            AbstractViewDynamic dynamic;
            LinearLayoutDynamic linearLayoutDynamic = new LinearLayoutDynamic(context, isOuterView, subViewJson);
            int cornerRadius = linearLayoutDynamic.cornerRadius;
            JSONArray subViewsArray = subViewJson.getJSONArray(UIProperty.subviews);
            if (subViewsArray.length() > 0) {
                JSONObject jsonObject;
                int subViews = subViewsArray.length();
                for (int index = 0; index < subViews; index++) {
                    jsonObject = subViewsArray.getJSONObject(index);
                    String type = jsonObject.optString(UIProperty.type);
                    if (isViewGroup(type)) {
                        dynamic = createSubView(context, jsonObject, false);
                    } else {
                        dynamic = buildSubView(context, type, jsonObject, cornerRadius, isOuterView);
                    }
                    if (dynamic != null) {
                        linearLayoutDynamic.addChildView(dynamic);
                    } else {
                        return null;
                    }
                }
            }
            return linearLayoutDynamic;
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return null;
    }

    /**
     * 创建子 View
     */
    private AbstractViewDynamic buildSubView(final Context context, String type, JSONObject jsonObject, int cornerRadius, boolean isOuterView) {
        switch (type) {
            case UIProperty.type_image_button:
            case UIProperty.type_image:
                // 因为由于 ImageView 在顶部会覆盖外部的圆角，所以只能让图片也圆角展示。
                return handleImageView(context, type, jsonObject, cornerRadius, isOuterView);
            case UIProperty.type_label:
                return handleTextView(context, jsonObject);
            case UIProperty.type_button:
                return new ButtonDynamic(context, jsonObject);
            case UIProperty.type_link:
                return new LinkViewDynamic(context, jsonObject);
            default:
                return null;
        }
    }

    private boolean isViewGroup(String type) {
        return UIProperty.type_row.equals(type) || UIProperty.type_column.equals(type);
    }

    private TextViewDynamic handleTextView(final Context context, JSONObject viewJson) {
        try {
            TextViewDynamic textViewDynamic = new TextViewDynamic(context, viewJson);
            if (UIProperty.title_type.equals(textViewDynamic.getNameType())) {
                this.mTitle = textViewDynamic.getText();
            } else if (UIProperty.content_type.equals(textViewDynamic.getNameType())) {
                this.mContent = textViewDynamic.getText();
            }

            return textViewDynamic;
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return null;
    }

    private ImageViewDynamic handleImageView(final Context context, String type, JSONObject viewJson, int cornerRadius, boolean isOuterView) {
        try {
            ImageViewDynamic imageViewDynamic = new ImageViewDynamic(context, type, cornerRadius, isOuterView, viewJson);
            if (UIProperty.type_image.equals(imageViewDynamic.getType())) {
                this.mImageUrl = imageViewDynamic.getImageUrl();
            }
            if (!imageViewDynamic.isImageLoaded()) {
                this.mImageSucceed = false;
                if (TextUtils.isEmpty(TipUtils.getErrorMessage())) {
                    // 如果不是网络原因，还是失败了，则是图片加载失败
                    TipUtils.setErrorCode(TipUtils.IMAGE_LOAD_FAILED);
                }
            }
            return imageViewDynamic;
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return null;
    }

    private MaskViewDynamic handleMaskLayout(final Context context, LinearLayoutDynamic layoutDynamic, final JSONObject propertyJson) {
        try {
            return new MaskViewDynamic(context, layoutDynamic, propertyJson);
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return null;
    }

    private void toastInPreview() {
        try {
            String errorMessage = TipUtils.getErrorMessage();
            if (!TextUtils.isEmpty(errorMessage)) {
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }
}