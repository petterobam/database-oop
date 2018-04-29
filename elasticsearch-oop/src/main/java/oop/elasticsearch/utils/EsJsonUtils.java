package oop.elasticsearch.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * fastjson封装的json工具类
 *
 * @author 欧阳洁
 */
public class EsJsonUtils {
    /**
     * 对象转字符串
     * @param object
     * @param prettyFormat 是否美化Json字符串
     * @return
     */
    public static final String toJSONString(Object object, boolean prettyFormat) {
        return JSON.toJSONString(object, prettyFormat);
    }

    /**
     * 对象转字符串
     * @param object
     * @return
     */
    public static final String toJSONString(Object object) {
        return JSON.toJSONString(object, false);
    }

    /**
     * @param <T>
     * @param json
     * @param clazz
     * @return
     */
    public static final <T> T getObject(String json, Class<T> clazz) {
        T t = null;
        try {
            t = JSON.parseObject(json, clazz);
        } catch (Exception e) {
            System.out.println("json字符串转换失败！" + json);
            e.printStackTrace();
        }
        return t;
    }

    /**
     * @param <T>
     * @param json
     * @param objKey
     * @param clazz
     * @return -> T
     */
    public static final <T> T getObject(String json, String objKey,
                                        Class<T> clazz) {
        JSONObject jsonobj = JSON.parseObject(json);
        if (jsonobj == null) {
            return null;
        }

        Object obj = jsonobj.get(objKey);
        if (obj == null) {
            return null;
        }

        if (obj instanceof JSONObject) {
            return jsonobj.getObject(objKey, clazz);
        } else {
            System.out.println("json字符串格式不对！");
        }

        return null;
    }

    /**
     * @param jsonStr json字符串
     * @param clazz   class名称
     * @return
     * @Description： json字符串转成为List
     */
    public static <T> List<T> getList(String jsonStr, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        try {
            list = JSON.parseArray(jsonStr, clazz);
        } catch (Exception e) {
            System.out.println("json字符串转List失败！" + jsonStr);
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Json转List<Map>
     * @param jsonString json字符串
     * @return
     * @Description： json字符串转换成list<Map>
     */
    public static List<Map<String, Object>> listKeyMaps(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = JSON.parseObject(jsonString, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            System.out.println("json字符串转map失败");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 转List<T>对象
     * @param json
     * @param listKey
     * @param clazz 子项类名
     * @param <T>
     * @return
     */
    public static final <T> List<T> getList(String json, String listKey,
                                            Class<T> clazz) {
        JSONObject jsonobj = JSON.parseObject(json);
        if (jsonobj == null) {
            return null;
        }
        Object obj = jsonobj.get(listKey);
        if (obj == null) {
            return null;
        }
        if (obj instanceof JSONArray) {
            JSONArray jsonarr = (JSONArray) obj;
            List<T> list = new ArrayList<T>();
            for (int i = 0; i < jsonarr.size(); i++) {
                list.add(jsonarr.getObject(i, clazz));
            }
            return list;
        }
        return null;
    }

    /**
     * @param jsonStr json字符串
     * @return
     * @Description： json字符串转换为Map
     */
    public static Map<String, Object> json2Map(String jsonStr) {
        try {
            return JSON.parseObject(jsonStr, Map.class);
        } catch (Exception e) {
            System.out.println("json字符串转换失败！" + jsonStr);
            e.printStackTrace();
        }
        return null;
    }
}
