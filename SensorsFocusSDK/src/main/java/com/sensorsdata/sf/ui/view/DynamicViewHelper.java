/*
 * Created by dengshiwei on 2020/03/28.
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
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensorsdata.sf.core.SensorsFocusAPI;
import com.sensorsdata.sf.core.utils.SFLog;
import com.sensorsdata.sf.ui.listener.PopupListener;
import com.sensorsdata.sf.ui.track.SFTrackHelper;
import com.sensorsdata.sf.ui.utils.ActionHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

class DynamicViewHelper {
    private PopupListener mPopupListener = ((SensorsFocusAPI) SensorsFocusAPI.shareInstance()).getWindowListener();
    private static HashMap<String, AbstractViewDynamic> viewDynamicHashMap = new HashMap<>();
    private String mTitle;
    private String mContent;
    private String mImageUrl;
    private String mPlanId;
    private JSONObject mJsonPlan;

    DynamicViewHelper(String planId, JSONObject jsonPlan) {
        this.mPlanId = planId;
        this.mJsonPlan = jsonPlan;
    }

    AbstractViewDynamic getViewDynamic(String uuid) {
        if (viewDynamicHashMap.containsKey(uuid)) {
            return viewDynamicHashMap.get(uuid);
        }
        return null;
    }

    void removeViewDynamic(String uuid) {
        if (viewDynamicHashMap.containsKey(uuid)) {
            viewDynamicHashMap.remove(uuid);
        }
    }

    void addViewDynamic(String uuid, AbstractViewDynamic viewDynamic) {
        viewDynamicHashMap.put(uuid, viewDynamic);
    }

    TextView handleTextView(final Activity activity, TextViewDynamic textViewDynamic) {
        try {
            final TextView textView = textViewDynamic.createView(activity);
            if (UIProperty.title_type.equals(textViewDynamic.getNameType())) {
                this.mTitle = textViewDynamic.getText();
            } else if (UIProperty.content_type.equals(textViewDynamic.getNameType())) {
                this.mContent = textViewDynamic.getText();
            }
            final JSONObject actionJson = textViewDynamic.getActionJson();
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (actionJson != null) {
                        handleAction(UIProperty.type_label, String.valueOf(textView.getText()), actionJson, activity);
                    }
                }
            });
            return textView;
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return null;
    }

    TextView handleLinkTextView(final Activity activity, LinkViewDynamic linkViewDynamic) {
        try {
            final TextView textView = linkViewDynamic.createView(activity);
            final JSONObject actionJson = linkViewDynamic.getActionJson();
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (actionJson != null) {
                        handleAction(UIProperty.type_link, String.valueOf(textView.getText()), actionJson, activity);
                    }
                }
            });

            return textView;
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return null;
    }

    Button handleButton(final Activity activity, ButtonDynamic buttonDynamic) {
        try {
            final Button button = buttonDynamic.createView(activity);
            final JSONObject actionJson = buttonDynamic.getActionJson();
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (actionJson != null) {
                            handleAction(UIProperty.type_button, String.valueOf(button.getText()), actionJson, activity);
                        }
                    }
                });

                return button;
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return null;
    }

    ImageView handleImageView(final Activity activity, ImageViewDynamic imageViewDynamic) {
        try {
            final ImageView imageView = imageViewDynamic.createView(activity);
            if (imageViewDynamic.isImageLoaded()) {
                if (UIProperty.type_image.equals(imageViewDynamic.getType())) {
                    this.mImageUrl = imageViewDynamic.getImageUrl();
                }

                final JSONObject actionJson = imageViewDynamic.getActionJson();
                if (imageView != null) {
                    if (UIProperty.type_image.equals(imageViewDynamic.getType())) {
                        this.mImageUrl = imageViewDynamic.getImageUrl();
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (actionJson != null) {
                                    handleAction(UIProperty.type_image, "", actionJson, activity);
                                }
                            }
                        });
                    } else if (UIProperty.type_image_button.equals(imageViewDynamic.getType())) {
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (actionJson != null) {
                                    handleAction(UIProperty.type_image_button, "", actionJson, activity);
                                }
                            }
                        });
                    }
                    return imageView;
                }
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return null;
    }

    FrameLayout handleMaskLayout(final Activity activity, final MaskViewDynamic maskViewDynamic) {
        try {
            FrameLayout frameLayout = maskViewDynamic.createView(activity);
            if (frameLayout != null) {
                if (maskViewDynamic.isMaskCloseEnabled()) {
                    frameLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                PopupListener internalWindowListener = ((SensorsFocusAPI) SensorsFocusAPI.shareInstance()).getInternalWindowListener();
                                if (internalWindowListener != null) {
                                    internalWindowListener.onPopupClose(String.valueOf(mPlanId));
                                }
                                JSONObject maskJson = new JSONObject();
                                maskJson.put(UIProperty.sf_close_type, "POPUP_CLOSE_MASK");
                                maskJson.put("id", maskViewDynamic.getActionId());
                                SFTrackHelper.trackPlanPopupClick(mTitle, mContent, UIProperty.type_mask, "", mImageUrl, maskJson, mJsonPlan);
                                if (mPopupListener != null) {
                                    mPopupListener.onPopupClick(mPlanId, getActionModel(maskJson));
                                    mPopupListener.onPopupClose(String.valueOf(mPlanId));
                                }
                                activity.finish();
                            } catch (JSONException e) {
                                SFLog.printStackTrace(e);
                            }
                        }
                    });
                }

                return frameLayout;
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return null;
    }

    private void handleAction(String element_type, String element_content, JSONObject actionJson, Activity activity) {
        try {
            String actionType = actionJson.optString(UIProperty.type);
            switch (actionType) {
                case UIProperty.action_type_camera:
                case UIProperty.action_type_email:
                case UIProperty.action_type_sms:
                case UIProperty.action_type_tel:
                    break;
                case UIProperty.action_type_close:
                    SFTrackHelper.trackPlanPopupClick(mTitle, mContent, element_type, element_content, mImageUrl, actionJson, mJsonPlan);
                    if (mPopupListener != null) {
                        mPopupListener.onPopupClick(mPlanId, getActionModel(actionJson));
                        mPopupListener.onPopupClose(String.valueOf(mPlanId));
                    }
                    PopupListener internalWindowListener = ((SensorsFocusAPI) SensorsFocusAPI.shareInstance()).getInternalWindowListener();
                    if (internalWindowListener != null) {
                        internalWindowListener.onPopupClose(String.valueOf(mPlanId));
                    }
                    activity.finish();
                    break;
                case UIProperty.action_type_copy:
                    SFTrackHelper.trackPlanPopupClick(mTitle, mContent, element_type, element_content, mImageUrl, actionJson, mJsonPlan);
                    ActionHelper.copyToClip(activity, actionJson.optString(UIProperty.action_value));
                    if (mPopupListener != null) {
                        mPopupListener.onPopupClick(String.valueOf(mPlanId), getActionModel(actionJson));
                    }
                    break;
                case UIProperty.action_type_link:
                case UIProperty.action_type_customize:
                    SFTrackHelper.trackPlanPopupClick(mTitle, mContent, element_type, element_content, mImageUrl, actionJson, mJsonPlan);
                    if (mPopupListener != null) {
                        mPopupListener.onPopupClick(String.valueOf(mPlanId), getActionModel(actionJson));
                        mPopupListener.onPopupClose(String.valueOf(mPlanId));
                    }
                    internalWindowListener = ((SensorsFocusAPI) SensorsFocusAPI.shareInstance()).getInternalWindowListener();
                    if (internalWindowListener != null) {
                        internalWindowListener.onPopupClose(String.valueOf(mPlanId));
                    }
                    activity.finish();
                    break;
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
    }

    private SensorsFocusActionModel getActionModel(JSONObject actionJson) {
        try {
            String type = actionJson.optString(UIProperty.type);
            String value = actionJson.optString(UIProperty.action_value);
            switch (type) {
                case UIProperty.action_type_link:
                    SensorsFocusActionModel.OPEN_LINK.setValue(value);
                    SensorsFocusActionModel.OPEN_LINK.setExtra(actionJson.optJSONObject(UIProperty.action_extra));
                    return SensorsFocusActionModel.OPEN_LINK;
                case UIProperty.action_type_copy:
                    SensorsFocusActionModel.COPY.setValue(value);
                    SensorsFocusActionModel.COPY.setExtra(actionJson.optJSONObject(UIProperty.action_extra));
                    return SensorsFocusActionModel.COPY;
                case UIProperty.action_type_close:
                    SensorsFocusActionModel.CLOSE.setValue(value);
                    SensorsFocusActionModel.CLOSE.setExtra(actionJson.optJSONObject(UIProperty.action_extra));
                    return SensorsFocusActionModel.CLOSE;
                case UIProperty.action_type_customize:
                    SensorsFocusActionModel.CUSTOMIZE.setExtra(new JSONObject(value));
                    return SensorsFocusActionModel.CUSTOMIZE;
            }
        } catch (Exception ex) {
            //
        }
        return null;
    }
}
