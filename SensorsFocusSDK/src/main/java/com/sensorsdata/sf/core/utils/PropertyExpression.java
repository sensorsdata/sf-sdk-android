/*
 * Created by renqingyou on 2020/03/05.
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
package com.sensorsdata.sf.core.utils;

import android.text.TextUtils;

import org.json.JSONArray;

import java.util.List;

public class PropertyExpression {
    /**
     * https://doc.sensorsdata.cn/pages/viewpage.action?pageId=50744177
     * 操作符：equal / notEqual
     * 表示等于/不等于，对字符串、数值类型有效。如果 Params 有多个，则相当于 In 或者 Not In。例如想筛选出来自北京或者上海的用户:
     * {
     * "field": "user.$city",
     * "function": "equal",
     * "params": ["北京", "上海"]
     * }
     */
    private static final String EQUAL = "equal";

    private static final String NOT_EQUAL = "notEqual";

    /**
     * 只对布尔类型有效
     */
    private static final String IS_TRUE = "isTrue";

    private static final String IS_FALSE = "isFalse";

    /**
     * '
     * 某个属性是否有值，对字符串、数值类型有效。
     * isSet/notSet
     */
    private static final String IS_SET = "isSet";

    private static final String NOT_SET = "notSet";

    /**
     * 针对集合的操作，表示包含某个元素，例如筛选出所有喜欢苹果的用户:
     * {
     * "field": "user.FavoriteFruits",
     * "function": "in",
     * "params": ["Apple"]
     * }
     */
    private static final String INCLUDE = "in";

    private static final String NOT_INCLUDE = "notInclude";

    private static final String IS_EMPTY = "isEmpty";

    private static final String IS_NOT_EMPTY = "isNotEmpty";

    /**
     * less / greater / between：表示小于/大于/小于且大于，其中 between 是前闭后闭的区间，只对数值类型有效。例如筛选买入黄金的价格在 230 和 232 之间的所有事件:
     * {
     * "field": "event.BuyGold.GoldPrice",
     * "function": "between",
     * "params": [230, 232]
     * }
     */

    private static final String LESS = "less";

    private static final String GREATER = "greater";

    private static final String BETWEEN = "between";

    /**
     * contain / notContain
     * 包含或者不包含，表示字符串的部分匹配，只对字符串类型有效。
     */
    private static final String CONTAIN = "contain";

    private static final String NOT_CONTAIN = "notContain";

    /**
     * absoluteBetween / relativeBefore / relativeWithin
     * 针对日期类型的操作符，分别表示在一个绝对日期范围/在 N 天之前/在 N 天之内。例如想筛选所有注册时间在 3 天之内的用户:
     * {
     * "field": "user.$signup_time",
     * "function": "relativeWithin",
     * "params": [3]
     * }
     * 或者筛选所有在 2015-1-1~2015-1-10 注册的用户:
     * {
     * "field": "user.$signup_time",
     * "function": "absoluteBetween",
     * "params": ["2020-02-26 00:00:00", "2020-03-04 00:00:00"]
     * }
     */

    private static final String ABSOLUTE_BETWEEN = "absolute_between";
    private static final String ABSOLUTE1_BETWEEN = "absoluteBetween";

    public static final String AND = "AND";

    public static final String OR = "OR";

    public static boolean isMatchProperty(String function, Object property, List<Object> params) {
        try {
            if (property == null) {
                return TextUtils.equals(function, NOT_SET);
            } else if (TextUtils.equals(function, EQUAL)) {
                for (Object param : params) {
                    if (param instanceof CharSequence) {
                        if (TextUtils.equals((String) param, (String) property)) {
                            return true;
                        }
                    } else if (param instanceof Number) {
                        double doubleParam = ((Number) param).doubleValue();
                        if (doubleParam == (double) property) {
                            return true;
                        }
                    }
                }
            } else if (TextUtils.equals(function, NOT_EQUAL)) {
                for (Object param : params) {
                    if (param instanceof String) {
                        if (TextUtils.equals((String) param, (String) property)) {
                            return false;
                        }
                    } else if (param instanceof Number) {
                        double doubleParam = ((Number) param).doubleValue();
                        if (doubleParam == (double) param) {
                            return false;
                        }
                    }
                }
                return true;
            } else if (TextUtils.equals(function, IS_TRUE)) {
                if (property instanceof Boolean) {
                    return (boolean) property;
                }
            } else if (TextUtils.equals(function, IS_FALSE)) {
                if (property instanceof Boolean) {
                    return !(boolean) property;
                }
            } else if (TextUtils.equals(function, IS_SET)) {
                return true;
            } else if (TextUtils.equals(function, NOT_SET)) {
                return false;
            } else if (TextUtils.equals(function, IS_EMPTY)) {
                if (property instanceof String) {
                    if (TextUtils.isEmpty((String) property)) {
                        return true;
                    }
                } else if (property instanceof JSONArray) {
                    JSONArray propertyArray = (JSONArray) property;
                    for (int i = 0; i < propertyArray.length(); i++) {
                        String value = propertyArray.optString(i);
                        if (!TextUtils.isEmpty(value)) {
                            return false;
                        }
                    }
                    return true;
                }
            } else if (TextUtils.equals(function, IS_NOT_EMPTY)) {
                if (property instanceof String) {
                    return !TextUtils.isEmpty((String) property);
                } else if (property instanceof JSONArray) {
                    JSONArray propertyArray = (JSONArray) property;
                    for (int i = 0; i < propertyArray.length(); i++) {
                        String value = propertyArray.optString(i);
                        if (value == null) {
                            continue;
                        }
                        if (!TextUtils.isEmpty(value.trim())) {
                            return true;
                        }
                    }
                }
            } else if (TextUtils.equals(function, INCLUDE)) {
                if (property instanceof JSONArray) {
                    JSONArray value = (JSONArray) property;
                    CharSequence param = (CharSequence) params.get(0);
                    for (int i = 0; i < value.length(); i++) {
                        if (TextUtils.equals((CharSequence) value.get(i), param)) {
                            return true;
                        }
                    }
                }
            } else if (TextUtils.equals(function, NOT_INCLUDE)) {
                if (property instanceof JSONArray) {
                    JSONArray value = (JSONArray) property;
                    CharSequence param = (CharSequence) params.get(0);
                    for (int i = 0; i < value.length(); i++) {
                        if (TextUtils.equals((CharSequence) value.get(i), param)) {
                            return false;
                        }
                    }
                    return true;
                }
            } else if (TextUtils.equals(function, LESS)) {
                //规则中给的 params 是字符串列表类型
                if (property instanceof Number) {
                    double doubleProperty = ((Number) property).doubleValue();
                    String param = (String) params.get(0);
                    double doubleParam = Double.parseDouble(param);
                    return doubleProperty < doubleParam;
                }
            } else if (TextUtils.equals(function, GREATER)) {
                //规则中给的 params 是字符串列表类型
                if (property instanceof Number) {
                    double doubleProperty = ((Number) property).doubleValue();
                    String param = (String) params.get(0);
                    double doubleParam = Double.parseDouble(param);
                    return doubleProperty > doubleParam;
                }
            } else if (TextUtils.equals(function, BETWEEN)) {
                if (property instanceof Number) {
                    double doubleProperty = ((Number) property).doubleValue();
                    //规则中给的 params 是字符串列表类型
                    String param1 = (String) params.get(0);
                    String param2 = (String) params.get(1);
                    return doubleProperty > Double.parseDouble(param1) && doubleProperty < Double.parseDouble(param2);
                }
            } else if (TextUtils.equals(function, CONTAIN)) {
                if (property instanceof String) {
                    String value = (String) property;
                    for (int i = 0; i < params.size(); i++) {
                        if (value.contains((CharSequence) params.get(i))) {
                            return true;
                        }
                    }
                }
            } else if (TextUtils.equals(function, NOT_CONTAIN)) {
                if (property instanceof String) {
                    String value = (String) property;
                    for (int i = 0; i < params.size(); i++) {
                        if (value.contains((String) params.get(i))) {
                            return false;
                        }
                    }
                    return true;
                }
            } else if (TextUtils.equals(function, ABSOLUTE_BETWEEN) || TextUtils.equals(function, ABSOLUTE1_BETWEEN)) {
                if (property instanceof String) {
                    String param1 = (String) params.get(0);
                    String param2 = (String) params.get(1);
                    return param1.compareTo((String) property) <= 0 && param2.compareTo((String) property) >= 0;
                }
            } /*else if (TextUtils.equals(function, RELATIVE_BEFORE)) {
                String param1 = (String) params.get(0);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 24);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.add(Calendar.DAY_OF_YEAR, -1 * Integer.parseInt(param1));
                Date value = (Date) property;
                return value.getTime() < calendar.getTimeInMillis();
            } else if (TextUtils.equals(function, RELATIVE_WITHIN)) {
                String param1 = (String) params.get(0);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 24);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.add(Calendar.DAY_OF_YEAR, -1 * Integer.parseInt(param1));
                Date value = (Date) property;
                return value.getTime() > calendar.getTimeInMillis() && value.getTime() < System.currentTimeMillis();
            }*/
        } catch (Exception e) {
            SFLog.printStackTrace(e);
        }
        return false;
    }

}
