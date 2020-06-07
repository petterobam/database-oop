package oop.elasticsearch.utils;

import oop.elasticsearch.annotation.EsParamSql;

import java.lang.reflect.Method;

/**
 * Created by vetech on 2018/7/17.
 */
public class EsSqlUtils {
    /**
     * 多参数方法注解查询，获取方法上的注解对象
     *
     * @param daoMethodInfo
     * @param params
     * @return
     */
    public static EsParamSql getParamSqlAnnotation(StackTraceElement daoMethodInfo, Object... params) {
        if (null == daoMethodInfo) {//为空不做处理
            System.out.println("未获取到自定义的语句！");
            return null;
        }
        Class<?>[] classArr = null;
        if (null != params && params.length > 0) {
            classArr = new Class<?>[params.length];
            for (int i = 0; i < params.length; i++) {
                classArr[i] = params[i].getClass();
            }
        }
        Method method = getMethod(daoMethodInfo.getClassName(), daoMethodInfo.getMethodName(), classArr);
        if (null == method) {//为空不做处理
            System.out.println("未获取到自定义的语句！");
            return null;
        }
        EsParamSql paramSql = method.getAnnotation(EsParamSql.class);
        return paramSql;
    }
    /**
     * 多参数方法注解查询，获取方法上的注解对象
     *
     * @param daoMethodInfo
     * @param parameterTypes
     * @return
     */
    public static EsParamSql getParamSqlAnnotation(StackTraceElement daoMethodInfo, Class<?>... parameterTypes) {
        if (null == daoMethodInfo) {//为空不做处理
            System.out.println("未获取到自定义的语句！");
            return null;
        }
        Method method = getMethod(daoMethodInfo.getClassName(), daoMethodInfo.getMethodName(), parameterTypes);
        if (null == method) {//为空不做处理
            System.out.println("未获取到自定义的语句！");
            return null;
        }
        EsParamSql paramSql = method.getAnnotation(EsParamSql.class);
        return paramSql;
    }

    /**
     * 通过类名、方法名和参数类型定位到具体的方法
     *
     * @param className
     * @param methodName
     * @param parameterTypes
     * @return
     */
    protected static Method getMethod(String className, String methodName, Class<?>... parameterTypes) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, parameterTypes);
            return method;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
