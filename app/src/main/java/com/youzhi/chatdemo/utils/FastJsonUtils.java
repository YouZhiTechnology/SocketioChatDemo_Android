package com.youzhi.chatdemo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastJsonUtils {

    /**
     * 将json字符串转化为对象集合
     *
     * @param jsonStr
     * @param cls
     * @return
     */
    public static <T> List<T> getObjectsList(String jsonStr, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            list = JSON.parseArray(jsonStr, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 把JSON文本parse为JavaBean
     *
     * @param text
     * @param clazz
     * @return
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        T t = JSON.parseObject(text, clazz);
        return t;
    }

    /**
     * 将JavaBean序列化为JSON文本
     *
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * 将JavaBean转换为JSONObject或者JSONArray。
     *
     * @param javaObject
     * @return
     */
    public static Object toJSON(Object javaObject) {
        return JSON.toJSON(javaObject);
    }

    /**
     * 将json字符串转化为字符串集合
     *
     * @param jsonStr
     * @param colName
     * @return
     */
    public static List<String> getStringList(String jsonStr, String colName) {
        List<String> list = new ArrayList<String>();
        try {
            JSONArray jsonArray = JSON.parseArray(jsonStr);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                list.add(jsonObject.getString(colName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将json转换为布尔
     *
     * @param jsonStr
     * @return
     */
    public static boolean getResult(String jsonStr) {
        String b = "";
        try {
            b = JSON.parseObject(jsonStr, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b.equals("true") ? true : false;

    }

    /**
     * 将json转换为布尔
     *
     * @param jsonStr
     * @param colName 列名
     * @return
     */
    public static boolean getJsonResult(String jsonStr, String colName) {

        String b = getStr(jsonStr, colName).trim();

        return b.equals("true") ? true : false;

    }

    /**
     * 将json转换为double
     *
     * @param jsonStr
     * @param colName 列名
     * @return 失败返回-1，成功返回对应double
     */
    public static double getJsonDouble(String jsonStr, String colName) {
        double jsonDou = -1;
        try {
            String b = getStr(jsonStr, colName).trim();
            jsonDou = Double.parseDouble(b);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonDou;

    }

    /**
     * 将1或0转换为布尔
     *
     * @param num 1,0
     * @return 1返回true，其他返回false
     */
    public static boolean parseToBoolean(int num) {
        if (num == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将布尔字符转换为布尔值
     *
     * @param b 布尔字符串
     * @return
     */
    public static boolean parseToBoolean(String b) {
        if (b.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将布尔转换为1或0
     *
     * @param b 布尔
     * @return 1返回true ，其他返回0
     */
    public static int parseToInt(boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 将Json 放入map<String,String>
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, String> getJsonMap(String jsonStr) {
        Map<String, String> StrMap = new HashMap<String, String>();
        try {
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                StrMap.put(entry.getKey(), String.valueOf(entry.getValue()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return StrMap;

    }

    /**
     * 将map转换为json
     *
     * @param map map
     * @return json
     */
    public static org.json.JSONObject postJson(Map map) {
        return new org.json.JSONObject(map);
    }

    /**
     * 将json字符串对象转换为Map<String, String>适用于一个对象的json字符串
     *
     * @param jsonStr 返回的字符串
     * @param colName 列明数组
     * @return 根据列名生成的map<列名                                                               ,                                                               值>
     */
    public static Map<String, String> getStrArray(String jsonStr,
                                                  String... colName) {
        Map<String, String> StrMap = new HashMap<String, String>();
        try {
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            for (int i = 0; i < colName.length; i++) {
                StrMap.put(colName[i], jsonObject.getString(colName[i]));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return StrMap;

    }

    /**
     * 用于（只储存一个对象）取得json字符串对应类列名的string值
     *
     * @param jsonStr
     * @param colName 列名
     * @return 列名对应的字符串值
     */
    public static String getStr(String jsonStr, String colName) {
        String str = "";
        try {
            JSONObject jsonObject = JSON.parseObject(jsonStr);

            str = jsonObject.getString(colName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;

    }

    /**
     * 用于（只储存一个对象）取得json字符串对应类列名的int值
     *
     * @param jsonStr
     * @param colName 列名
     * @return 列名对应的int值，空返回0，不是int类型，返回-1
     */
    public static int getJsonInt(String jsonStr, String colName) {
        int num = -1;
        try {
            String val = getStr(jsonStr, colName);
            if (val.equals("") || val.equals("null")) {
                val = "0";
            }
            num = Integer.parseInt(val);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    /**
     * 判断是否为json
     *
     * @param str
     * @return
     */
    public static boolean isJson(String str) {
        if (str.startsWith("{") && str.endsWith("}")) {
            return true;
        }
        return false;
    }
}
