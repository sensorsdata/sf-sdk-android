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

package com.sensorsdata.sf.ui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.sensorsdata.sf.core.utils.SFLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private LruCache<String, Bitmap> mBitmapCache;
    private DiskLruCache mDiskLruCache;
    private ExecutorService executorService;
    private static ImageLoader instance;

    private ImageLoader(Context context) {
        if (instance == null) {
            try {
                int maxSize = (int) (Runtime.getRuntime().freeMemory() / 2);
                mBitmapCache = new LruCache<String, Bitmap>(maxSize) {
                    @Override
                    protected int sizeOf(String key, Bitmap value) {
                        return value.getRowBytes() * value.getHeight();
                    }
                };
                final String diskCacheDir = context.getCacheDir().getPath() + File.separator + TAG;
                File cacheFile = new File(diskCacheDir);
                if (!cacheFile.exists()) {
                    cacheFile.mkdirs();
                }
                mDiskLruCache = DiskLruCache.open(cacheFile, 1, 1, 10 * 1024 * 1024);
                executorService = Executors.newSingleThreadExecutor();
            } catch (Exception ex) {
                // ignore
            }
        }
    }

    public synchronized static ImageLoader getInstance(Context context) {
        if (instance == null) {
            instance = new ImageLoader(context);
        }
        return instance;
    }

    /**
     * 加载 Bitmap
     *
     * @param url URL 地址
     * @return Bitmap
     */
    public Bitmap loadBitmap(String url) {
        try {
            Bitmap bitmap = mBitmapCache.get(url);
            if (bitmap != null) {
                return bitmap;
            }

            bitmap = loadBitmapFromDisk(url);
            if (bitmap != null) {
                mBitmapCache.put(url, bitmap);
                return bitmap;
            }

            Future<Bitmap> bitmapFuture = executorService.submit(new LoaderTask(url));
            bitmap = bitmapFuture.get();
            if (bitmap != null) {
                mBitmapCache.put(url, bitmap);
            }
            return bitmap;
        } catch (Exception ex) {
            // ignore
        }
        return null;
    }

    private class LoaderTask implements Callable<Bitmap> {
        private String httpUrl;

        LoaderTask(String httpUrl) {
            this.httpUrl = httpUrl;
        }

        @Override
        public Bitmap call() {
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(httpUrl);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(30_000);
                httpURLConnection.setReadTimeout(30_000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                int responseCode = httpURLConnection.getResponseCode();
                SFLog.d(TAG, "ImageLoader 【 HttpUrl = " + httpUrl + " ,Code = " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                    byte[] data = readStream(inputStream);
                    saveStreamDiskLruCache(httpUrl, data);
                    return BitmapFactory.decodeByteArray(data, 0, data.length);
                }
            } catch (Exception ex) {
                SFLog.printStackTrace(ex);
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }

                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        //ignore
                    }
                }
            }
            return null;
        }
    }

    /**
     * save image
     */
    private void saveStreamDiskLruCache(String imageUrl, byte[] data) {
        try {
            String imageKey = hashKeyForDisk(imageUrl);
            DiskLruCache.Editor editor = mDiskLruCache.edit(imageKey);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                outputStream.write(data, 0, data.length);
                outputStream.flush();
                editor.commit();
            }
            mDiskLruCache.flush();
        } catch (Exception ex) {
            // ignore
        }
    }

    /**
     * load Bitmap from DiskLruCache
     */
    private Bitmap loadBitmapFromDisk(String imageUrl) {
        try {
            String imageKey = hashKeyForDisk(imageUrl);
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(imageKey);
            if (snapshot != null) {
                return BitmapFactory.decodeStream(snapshot.getInputStream(0));
            }
        } catch (Exception ex) {
            // ignore
        }
        return null;
    }

    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.flush();
        byte[] array = outStream.toByteArray();
        outStream.close();
        return array;
    }
}
