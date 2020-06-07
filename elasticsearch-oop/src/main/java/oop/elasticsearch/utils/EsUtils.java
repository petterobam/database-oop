package oop.elasticsearch.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.*;

public class EsUtils {
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static String toString(Object str) {
        return str == null ? null : str.toString();
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

    /**
     * 输入流转字符串
     *
     * @param is
     * @return
     */
    public static String convertStreamToStr(InputStream is) {
        return convertStreamToStr(is,null);
    }
    /**
     * 输入流转字符串
     *
     * @param is
     * @return
     */
    public static String convertStreamToStr(InputStream is, String charset) {
        StringBuilder sb = new StringBuilder("");
        try {
            if (isBlank(charset)) {
                charset = "UTF-8";
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));

            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
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
            EsUtils.setAccessibleWorkaround(field);
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
}
