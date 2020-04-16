/*
 * Created by dengshiwei on 2020/02/27.
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

package com.sensorsdata.sf.core.http.internal;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ResponseBody {
    /**
     * 返回输出流
     */
    InputStream inputStream;
    /**
     * 错误信息
     */
    InputStream errorStream;
    /**
     * 响应码
     */
    public int code;
    /**
     * 长度
     */
    long contentLength;

    /**
     * Message 信息
     */
    String body = "";

    public String body() {
        if (inputStream != null) {
            return inputStream2String(inputStream);
        } else if (errorStream != null) {
            return inputStream2String(errorStream);
        } else {
            return body;
        }
    }

    /**
     * 获取 byte 数据流
     *
     * @return byte 数据流
     */
    public byte[] bytes() {
        if (inputStream != null) {
            return inputStream2Byte(inputStream);
        } else if (errorStream != null) {
            return inputStream2Byte(errorStream);
        } else {
            return new byte[]{};
        }
    }

    /**
     * InputStream 转换为 String
     */
    private String inputStream2String(InputStream inputStream) {
        try {
            return new String(inputStream2Byte(inputStream));
        } catch (Exception ex) {
            // ignore
            return "";
        }
    }

    /**
     * InputStream 转换为 byte 数组
     */
    private byte[] inputStream2Byte(InputStream inputStream) {
        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[8192];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            return buffer.toByteArray();
        } catch (Exception ex) {
            return new byte[]{};
        }
    }

    @Override
    public String toString() {
        return "ResponseBody{code = " +  code + "，message = " + body + "}";
    }
}
