/*
 * Created by dengshiwei on 2020/03/04.
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
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sensorsdata.analytics.android.sdk.SensorsDataIgnoreTrackAppViewScreen;
import com.sensorsdata.sf.core.SensorsFocusAPI;
import com.sensorsdata.sf.core.entity.PopupPlan;
import com.sensorsdata.sf.core.utils.SFLog;
import com.sensorsdata.sf.ui.track.SFTrackHelper;

import org.json.JSONObject;

@SensorsDataIgnoreTrackAppViewScreen
public class DialogActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (getIntent().hasExtra(UIProperty.TAG)) {
                String uuid = getIntent().getStringExtra(UIProperty.TAG);
                String planId = getIntent().getStringExtra(UIProperty.plan_id);
                View dialogView = createView(uuid, planId);
                if (dialogView != null) {
                    setContentView(dialogView);
                    return;
                }
            }
        } catch (Exception e) {
            SFLog.printStackTrace(e);
        }
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DynamicViewJsonBuilder.dialogIsShowing = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        DynamicViewJsonBuilder.dialogIsShowing = false;
    }

    private View createView(String uuid, String planId) {
        try {
            JSONObject jsonPlan;
            if (TextUtils.isEmpty(planId)) {
                jsonPlan = SFTrackHelper.buildPlanProperty(null);
            } else {
                PopupPlan popupPlan = ((SensorsFocusAPI) SensorsFocusAPI.shareInstance()).getPopupPlan(Long.parseLong(planId));
                jsonPlan = SFTrackHelper.buildPlanProperty(popupPlan);
            }
            DynamicViewHelper dynamicViewHelper = new DynamicViewHelper(planId, jsonPlan);
            AbstractViewDynamic viewDynamic = dynamicViewHelper.getViewDynamic(uuid);
            dynamicViewHelper.removeViewDynamic(uuid);
            if (viewDynamic instanceof MaskViewDynamic) {
                FrameLayout frameLayout = dynamicViewHelper.handleMaskLayout(this, (MaskViewDynamic) viewDynamic);
                frameLayout.addView(traverseView(dynamicViewHelper, ((MaskViewDynamic) viewDynamic).getChildDynamic()));
                return frameLayout;
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return null;
    }

    private View traverseView(DynamicViewHelper dynamicViewHelper, AbstractViewDynamic viewDynamic) {
        try {
            View view;
            if (viewDynamic instanceof LinearLayoutDynamic) {
                LinearLayout linearLayout = (LinearLayout) viewDynamic.createView(this);
                if (linearLayout == null) {
                    return null;
                }
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 为了防止空白处触发蒙层，这里就设置空的点击
                    }
                });
                for (AbstractViewDynamic subViewDynamic : ((LinearLayoutDynamic) viewDynamic).getChildViews()) {
                    if (subViewDynamic instanceof LinearLayoutDynamic) {
                        view = traverseView(dynamicViewHelper, subViewDynamic);
                    } else {
                        view = buildView(dynamicViewHelper, subViewDynamic);
                    }

                    if (view != null) {
                        linearLayout.addView(view);
                    }
                }
                return linearLayout;
            }
        } catch (Exception ex) {
            SFLog.printStackTrace(ex);
        }
        return null;
    }

    private View buildView(DynamicViewHelper dynamicViewHelper, AbstractViewDynamic viewDynamic) {
        View view = null;
        if (viewDynamic instanceof ButtonDynamic) {
            view = dynamicViewHelper.handleButton(this, (ButtonDynamic) viewDynamic);
        } else if (viewDynamic instanceof LinkViewDynamic) {
            view = dynamicViewHelper.handleLinkTextView(this, (LinkViewDynamic) viewDynamic);
        } else if (viewDynamic instanceof TextViewDynamic) {
            view = dynamicViewHelper.handleTextView(this, (TextViewDynamic) viewDynamic);
        } else if (viewDynamic instanceof ImageViewDynamic) {
            view = dynamicViewHelper.handleImageView(this, (ImageViewDynamic) viewDynamic);
        }
        return view;
    }
}
