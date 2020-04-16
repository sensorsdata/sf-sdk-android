/*
 * Created by dengshiwei on 2020/02/29.
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

package com.sensorsdata.sf.ui.widget;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;

import com.sensorsdata.sf.core.utils.SFLog;

import java.io.InputStream;

import static android.os.Build.VERSION.SDK_INT;

public class RoundedBitmapDrawableFactory {
    private static final String TAG = "RoundedBitmapDrawableFa";
    private static class DefaultRoundedBitmapDrawable extends RoundedBitmapDrawable {
        DefaultRoundedBitmapDrawable(Resources res, Bitmap bitmap,  boolean isCornerAll) {
            super(res, bitmap, isCornerAll);
        }

        @Override
        public void setMipMap(boolean mipMap) {
            if (mBitmap != null) {
                if (Build.VERSION.SDK_INT >= 18) {
                    mBitmap.setHasMipMap(mipMap);
                }
                invalidateSelf();
            }
        }

        @Override
        public boolean hasMipMap() {
            return mBitmap != null && hasMipMap(mBitmap);
        }

        @Override
        void gravityCompatApply(int gravity, int bitmapWidth, int bitmapHeight,
                                Rect bounds, Rect outRect) {
            if (SDK_INT >= 17) {
                Gravity.apply(gravity, bitmapWidth, bitmapHeight, bounds, outRect, 0);
            } else {
                Gravity.apply(gravity, bitmapWidth, bitmapHeight, bounds, outRect);
            }
        }

        public static boolean hasMipMap(Bitmap bitmap) {
            if (Build.VERSION.SDK_INT >= 18) {
                return bitmap.hasMipMap();
            }
            return false;
        }
    }

    public static RoundedBitmapDrawable create(Resources res, Bitmap bitmap, boolean isCornerAll) {
        if (Build.VERSION.SDK_INT >= 21) {
            return new RoundedBitmapDrawable21(res, bitmap,isCornerAll);
        }
        return new DefaultRoundedBitmapDrawable(res, bitmap, isCornerAll);
    }

    /*
     * Returns a new drawable, creating it by opening a given file path and decoding the bitmap.
     */
    public static RoundedBitmapDrawable create(Resources res, String filepath, boolean isCornerAll) {
        final RoundedBitmapDrawable drawable = create(res, BitmapFactory.decodeFile(filepath), isCornerAll);
        if (drawable.getBitmap() == null) {
            SFLog.d(TAG, "RoundedBitmapDrawable cannot decode " + filepath);
        }
        return drawable;
    }

    public static RoundedBitmapDrawable create(Resources res, InputStream is, boolean isCornerAll) {
        final RoundedBitmapDrawable drawable = create(res, BitmapFactory.decodeStream(is), isCornerAll);
        if (drawable.getBitmap() == null) {
            SFLog.d(TAG, "RoundedBitmapDrawable cannot decode " + is);
        }
        return drawable;
    }

    private RoundedBitmapDrawableFactory() {
    }
}
