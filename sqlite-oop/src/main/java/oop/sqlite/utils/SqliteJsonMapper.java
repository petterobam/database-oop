package oop.sqlite.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SqliteJsonMapper {
    //JsonMapper 工具
    private ObjectMapper mapper;
    private static SqliteJsonMapper jsonMapper;
    private static SqliteJsonMapper jsonNonDefaultMapper;
    private static SqliteJsonMapper jsonNonDefaultMapperRoot;
    private static SqliteJsonMapper josnNonEmptyMapperRoot;

    public SqliteJsonMapper() {
        this((JsonInclude.Include) null, false);
    }

    public SqliteJsonMapper(JsonInclude.Include include, boolean root) {
        this.mapper = new ObjectMapper();
        if (include != null) {
            this.mapper.setSerializationInclusion(include);
        }

        this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'"));
        if (root) {
            this.mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        }

    }

    public static SqliteJsonMapper nonEmptyMapper() {
        if (jsonMapper == null) {
            jsonMapper = new SqliteJsonMapper(JsonInclude.Include.NON_EMPTY, false);
        }

        return jsonMapper;
    }

    public static SqliteJsonMapper nonEmptyMapperRoot() {
        if (josnNonEmptyMapperRoot == null) {
            josnNonEmptyMapperRoot = new SqliteJsonMapper(JsonInclude.Include.NON_EMPTY, true);
        }

        return josnNonEmptyMapperRoot;
    }

    public static SqliteJsonMapper nonDefaultMapper() {
        if (jsonNonDefaultMapper == null) {
            jsonNonDefaultMapper = new SqliteJsonMapper(JsonInclude.Include.NON_DEFAULT, false);
        }

        return jsonNonDefaultMapper;
    }

    public static SqliteJsonMapper nonDefaultMapperRoot() {
        if (jsonNonDefaultMapperRoot == null) {
            jsonNonDefaultMapperRoot = new SqliteJsonMapper(JsonInclude.Include.NON_DEFAULT, true);
        }

        return jsonNonDefaultMapperRoot;
    }

    public String toJson(Object object) {
        try {
            return this.mapper.writeValueAsString(object);
        } catch (IOException var3) {
            System.out.println("write to json string error:" + object);
            var3.printStackTrace();
            return null;
        }
    }

    public <T> T fromJson(String jsonString, Class<T> clazz) {
        if (SqliteUtils.isBlank(jsonString)) {
            return null;
        } else {
            try {
                return this.mapper.readValue(jsonString, clazz);
            } catch (IOException var4) {
                System.out.println("parse json string error:" + jsonString);
                var4.printStackTrace();
                return null;
            }
        }
    }

    public <T> T fromJson(byte[] jsonByte, Class<T> clazz) {
        if (jsonByte != null && jsonByte.length >= 1) {
            try {
                return this.mapper.readValue(jsonByte, clazz);
            } catch (IOException var4) {
                System.out.println("parse json byte error");
                var4.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public <T> T fromJson(String jsonString, JavaType javaType) {
        if (SqliteUtils.isBlank(jsonString)) {
            return null;
        } else {
            try {
                return this.mapper.readValue(jsonString, javaType);
            } catch (IOException var4) {
                System.out.println("parse json string error:" + jsonString);
                var4.printStackTrace();
                return null;
            }
        }
    }

    public <T> T fromJson(byte[] jsonByte, JavaType javaType) {
        if (jsonByte != null && jsonByte.length >= 1) {
            try {
                return this.mapper.readValue(jsonByte, javaType);
            } catch (IOException var4) {
                System.out.println("parse json byte error");
                var4.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public JavaType contructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return this.mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    public JavaType contructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return this.mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    public void update(String jsonString, Object object) {
        try {
            this.mapper.readerForUpdating(object).readValue(jsonString);
        } catch (JsonProcessingException var4) {
            System.out.println("update json string:" + jsonString + " to object:" + object + " error.");
            var4.printStackTrace();
        } catch (IOException var5) {
            System.out.println("update json string:" + jsonString + " to object:" + object + " error.");
            var5.printStackTrace();
        }

    }

    public String toJsonP(String functionName, Object object) {
        return this.toJson(new JSONPObject(functionName, object));
    }

    public void enableEnumUseToString() {
        this.mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        this.mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    public void enableJaxbAnnotation() {
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        this.mapper.registerModule(module);
    }

    public ObjectMapper getMapper() {
        return this.mapper;
    }

    public String subMap2json(Map<String, Object> map, Collection<String> keys) {
        if (null != map && !map.isEmpty()) {
            if (null == keys || ((Collection) keys).isEmpty()) {
                keys = map.keySet();
            }

            HashMap newMap = new HashMap();
            Object value = null;
            Iterator i$ = ((Collection) keys).iterator();

            while (i$.hasNext()) {
                String key = (String) i$.next();
                value = map.get(key);
                if (value instanceof String) {
                    newMap.put(key, this._filter((String) value));
                } else {
                    newMap.put(key, value);
                }
            }

            return this.toJson(newMap);
        } else {
            return null;
        }
    }

    private String _filter(String str) {
        return str.replaceAll("\t|\r|\n", "");
    }
}
