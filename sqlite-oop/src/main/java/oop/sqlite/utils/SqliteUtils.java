package oop.sqlite.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SqliteUtils {
    private static final String osName = System.getProperty("os.name").toLowerCase();

    /**
     * 获取class目录下单文件绝对路径
     *
     * @param path
     * @return
     */
    public static String getClassRootPath(String path) {
        path = SqliteUtils.trimToEmpty(path);
        String filePath = SqliteUtils.class.getResource("/").getPath().toString();
        if (filePath == null) return null;
        String p = "";
        if (path.startsWith("/")) {
            p = filePath + path.substring(1);
        } else {
            p = filePath + path;
        }
        return p;
    }

    /**
     * 替换字符串，非正则匹配
     *
     * @param text
     * @param searchString
     * @param replacement
     * @return
     */
    public static String replace(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    /**
     * 替换字符串，非正则匹配
     *
     * @param text
     * @param searchString
     * @param replacement
     * @param max
     * @return
     */
    public static String replace(String text, String searchString, String replacement, int max) {
        if (!isBlank(text) && !isBlank(searchString) && replacement != null && max != 0) {
            int start = 0;
            int end = text.indexOf(searchString, start);
            if (end == -1) {
                return text;
            } else {
                int replLength = searchString.length();
                int increase = replacement.length() - replLength;
                increase = increase < 0 ? 0 : increase;
                increase *= max < 0 ? 16 : (max > 64 ? 64 : max);

                StringBuffer buf;
                for (buf = new StringBuffer(text.length() + increase); end != -1; end = text.indexOf(searchString, start)) {
                    buf.append(text.substring(start, end)).append(replacement);
                    start = end + replLength;
                    --max;
                    if (max == 0) {
                        break;
                    }
                }

                buf.append(text.substring(start));
                return buf.toString();
            }
        } else {
            return text;
        }
    }

    /**
     * 去除字符串两边空格，为空就转成空字符串
     *
     * @param str
     * @return
     */
    public static String trimToEmpty(final String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * 是否为windows系统
     *
     * @return
     */
    public static boolean isWindows() {
        return osName.indexOf("windows") >= 0;
    }


    /**
     * 判断集合是否为空
     *
     * @param coll
     * @return
     */
    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * 判断集合是否不为空
     *
     * @param coll
     * @return
     */
    public static boolean isNotEmpty(Collection coll) {
        return !isEmpty(coll);
    }

    /**
     * 对象转字符串
     *
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    /**
     * 判断字符串是否相等
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    /**
     * 去除字符串两边空格
     *
     * @param str
     * @return
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 读取对象的某个注解属性值
     *
     * @param field
     * @param target
     * @param forceAccess
     * @return
     * @throws IllegalAccessException
     */
    public static Object readField(final Field field, final Object target, final boolean forceAccess) throws IllegalAccessException {
        if (field == null) {
            return null;
        }
        if (forceAccess && !field.isAccessible()) {
            field.setAccessible(true);
        } else {
            SqliteUtils.setAccessibleWorkaround(field);
        }
        return field.get(target);
    }

    static boolean setAccessibleWorkaround(final AccessibleObject o) {
        if (o == null || o.isAccessible()) {
            return false;
        }
        final Member m = (Member) o;
        if (!o.isAccessible() && Modifier.isPublic(m.getModifiers()) && isPackageAccess(m.getDeclaringClass().getModifiers())) {
            try {
                o.setAccessible(true);
                return true;
            } catch (final SecurityException e) { // NOPMD
                // ignore in favor of subsequent IllegalAccessException
            }
        }
        return false;
    }

    private static final int ACCESS_TEST = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;

    static boolean isPackageAccess(final int modifiers) {
        return (modifiers & ACCESS_TEST) == 0;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param jsonString json字符串
     * @param clazz      对象class，如果要转化为List<ObjectA> 传入ObjectA.class
     * @return Object [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]T
     */
    public static <T> T getInstance(String jsonString, Class clazz) {
        if (SqliteUtils.isBlank(jsonString)) return null;
        if ("[]".equals(SqliteUtils.trim(jsonString))) return null;
        Object obj = JSON.parse(jsonString);
        if (obj == null) {
            return null;
        }
        if (obj instanceof JSONArray) {
            JSONArray jsonarr = (JSONArray) obj;
            List list = new ArrayList();
            //如果集合不为null则是返回成功,则需要修改数据的时间
            //创建转换json的需要转换的集合类型   [{},{}]
            for (int i = 0; i < jsonarr.size(); i++) {
                list.add(jsonarr.getObject(i, clazz));
            }
            return (T)list;
        }else if(obj instanceof JSONObject){
            JSONObject jsonObject = (JSONObject) obj;
            return (T)jsonObject.toJavaObject(clazz);
        }else {
            return null;
        }
    }

    /**
     * Object转化为json字符串
     * <功能详细描述>
     *
     * @param object
     * @return String [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String getJson(Object object) {
        try {
            return SqliteJsonUtils.toJSONString(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 转换 List<Object> 为 Json字符串
     *
     * @param list
     * @return
     */
    public static String getJson(List list) {
        if (list == null) return "[]";
        return SqliteJsonUtils.toJSONString(list);
    }

    /**
     * 将日期时间对象转为 指定格式的字符串
     *
     * @param format
     * @return string
     */
    public static String nowFormatStr(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
     *
     * @param sformat yyyyMMddHHmmss
     * @return
     */
    public static String getUserDate(String sformat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(sformat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在日期时间
     *
     * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        return getUserDate("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 根据类路径名，无参的构造实例化
     *
     * @param fileClass
     * @return
     */
    public static <T> T getInstance(Class<T> fileClass) {
        T result = null;
        try {
            // 要创建的类的构造器
            //Class fileClass = Class.forName(beanClassName);

            // 调用pType为变量的getConstructor()，获得一个专属ctor
            Constructor ctor = null;
            try {
                ctor = fileClass.getConstructor();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            // 调用上述专属ctor的newInstance()
            try {
                result = (T) ctor.newInstance();
            } catch (InvocationTargetException e) {
                System.out.println("构造参数有误！");
                e.printStackTrace();
            }
        } catch (InstantiationException e) {
            System.out.println("构造实例化失败！" + e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.out.println("构造实例化内存分配失败！" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}



