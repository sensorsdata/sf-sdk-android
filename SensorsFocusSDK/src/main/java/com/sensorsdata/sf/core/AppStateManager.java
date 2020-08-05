/*
 * Created by dengshiwei on 2020/03/13.
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

package com.sensorsdata.sf.core;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.sensorsdata.sf.ui.view.DialogActivity;

import java.util.ArrayList;
import java.util.List;

public class AppStateManager implements Application.ActivityLifecycleCallbacks {
    private int mActivityCount;
    private boolean mAppInForeground;
    private boolean mResumeFromBackground;
    private boolean isActivityFinishing = false;
    private final String IGNORE_DIALOG_ACTIVITY = DialogActivity.class.getCanonicalName();
    private List<AppStateChangedListener> mAppStateChangedListener = new ArrayList<>();

    public interface AppStateChangedListener {

        void onEnterForeground(boolean resumeFromBackground);

        void onEnterBackground();
    }

    void addAppStateChangedListener(AppStateChangedListener appStateChangedListener) {
        if (appStateChangedListener != null && !mAppStateChangedListener.contains(appStateChangedListener)) {
            mAppStateChangedListener.add(appStateChangedListener);
        }
    }

    @Override
    public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
    }

    public boolean isAppInForeground() {
        return mAppInForeground;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (isDialogActivity(activity)) {
            return;
        }
        mActivityCount++;
        if (!mAppInForeground) {
            mAppInForeground = true;
            for (AppStateChangedListener appStateChangedListener : mAppStateChangedListener) {
                appStateChangedListener.onEnterForeground(mResumeFromBackground);
            }
        }
        if (!mResumeFromBackground) {
            mResumeFromBackground = true;
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (isDialogActivity(activity)) {
            return;
        }
        isActivityFinishing = false;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (isDialogActivity(activity)) {
            return;
        }

        if (activity.isFinishing()) {
            isActivityFinishing = true;
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (isDialogActivity(activity)) {
            return;
        }
        mActivityCount--;
        if (mActivityCount == 0) {
            mAppInForeground = false;
            activity.getApplicationContext().sendBroadcast(new Intent("com.sensorsdata.sf.close.DialogActivity"));
            for (AppStateChangedListener appStateChangedListener : mAppStateChangedListener) {
                appStateChangedListener.onEnterBackground();
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private boolean isDialogActivity(Activity activity) {
        return !TextUtils.isEmpty(IGNORE_DIALOG_ACTIVITY) && IGNORE_DIALOG_ACTIVITY.equals(activity.getClass().getCanonicalName());
    }

    public boolean isActivityFinishing() {
        return isActivityFinishing;
    }
}
