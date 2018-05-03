/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package oop.sqlite.thread;

import oop.sqlite.utils.SqliteUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程相关工具类.
 * @author 欧阳洁
 */
public class SqliteThreadUtils {
    /**
     * 数字
     */
    private static final int FOUR = 4;
    /**
     * 数字
     */
    private static final int THIRD = 3;
    /**
     * 数字
     */
    private static final int SECOND = 2;
    /**
     * 空字符串
     */
    public static final String STR_EMPTY = "";


    /**
     * sleep等待, 单位为毫秒, 已捕捉并处理InterruptedException.
     *
     * @param durationMillis 毫秒
     */
    public static void sleep(long durationMillis) throws InterruptedException {
        try {
            Thread.sleep(durationMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    /**
     * sleep等待，已捕捉并处理InterruptedException.
     *
     * @param duration 时长
     * @param unit     时间单位
     */
    public static void sleep(long duration, TimeUnit unit) {
        try {
            Thread.sleep(unit.toMillis(duration));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 纯粹为了提醒下处理InterruptedException的正确方式，除非你是在写不可中断的任务.
     */
    public static void handleInterruptedException() {
        Thread.currentThread().interrupt();
    }

    /**
     * 通过StackTrace，获得调用者的类名.
     *
     * @return 类名
     */
    public static String getCallerClass() {

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        if (stacktrace.length >= FOUR) {
            StackTraceElement element = stacktrace[THIRD];
            return element.getClassName();
        } else {
            return STR_EMPTY;
        }
    }

    /**
     * 通过StackTrace，获得调用者的"类名.方法名()"
     *
     * @return 类名.方法名()
     */
    public static String getCallerMethod() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        if (stacktrace.length >= FOUR) {
            StackTraceElement element = stacktrace[THIRD];
            return element.getClassName() + '.' + element.getMethodName() + "()";
        } else {
            return STR_EMPTY;
        }
    }

    /**
     * 通过StackTrace，获得调用者的类名.
     *
     * @return 获得调用者的类名
     */
    public static String getCurrentClass() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        if (stacktrace.length >= THIRD) {
            StackTraceElement element = stacktrace[SECOND];
            return element.getClassName();
        } else {
            return STR_EMPTY;
        }
    }

    /**
     * 通过StackTrace，获得当前方法的"类名.方法名()"
     *
     * @return 类名.方法名()
     */
    public static String getCurrentMethod() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        if (stacktrace.length >= THIRD) {
            StackTraceElement element = stacktrace[SECOND];
            return element.getClassName() + '.' + element.getMethodName() + "()";
        } else {
            return STR_EMPTY;
        }
    }

    /**
     * 创建ThreadFactory，使得创建的线程有自己的名字而不是默认的"pool-x-thread-y"， 在用threaddump查看线程时特别有用。 格式如"mythread-%d"
     *
     * @param nameFormat 线程名字
     * @return 线程工厂
     */
    public static ThreadFactory buildJobFactory(final String nameFormat) {
        final ThreadFactory threadFactory = Executors.defaultThreadFactory();
        final AtomicLong count = nameFormat != null?new AtomicLong(0L):null;
        final String name = format(nameFormat, new Object[]{Long.valueOf(count.getAndIncrement())});
        return new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = threadFactory.newThread(runnable);
                if(nameFormat != null) {
                    thread.setName(name);
                }
                thread.setDaemon(true);
                //thread.setPriority(priority.intValue());
                //thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
                return thread;
            }
        };
    }

    /**
     * 格式化线程名
     * @param format
     * @param args
     * @return
     */
    private static String format(String format, Object... args) {
        return String.format(Locale.ROOT, format, args);
    }

    /**
     * 所有线程
     *
     * @return 线程集合
     */
    public static List<Thread> listThreads() {
        int tc = Thread.activeCount();
        Thread[] ts = new Thread[tc];
        Thread.enumerate(ts);
        return Arrays.asList(ts);
    }

    /**
     * 线程详细信息
     *
     * @param t 线程
     * @return 信息
     */
    public static String getStackMsg(Thread t) {
        StringBuffer sb = new StringBuffer();
        StackTraceElement[] stackArray = t.getStackTrace();
        for (int i = 0; i < stackArray.length; i++) {
            StackTraceElement element = stackArray[i];
            sb.append(element.toString() + "\n");
        }
        return sb.toString();
    }

    /**
     * 线程池信息
     *
     * @param executorService 线程对象
     * @return 信息字符串
     */
    public static String getQueueInfo(ExecutorService executorService) {
        ThreadPoolExecutor exec = (ThreadPoolExecutor) executorService;
        StringBuffer str = new StringBuffer();
        str.append("\r\n" + SqliteUtils.getStringDate() + "===========================");
        str.append("\r\nexec.getTaskCount():" + exec.getTaskCount());
        str.append("\r\nexec.getCompletedTaskCount():" + exec.getCompletedTaskCount());
        str.append("\r\nexec.getActiveCount():" + exec.getActiveCount());
        str.append("\r\nexec.getPoolSize():" + exec.getPoolSize());
        str.append("\r\nexec.getQueue():" + exec.getQueue().size());
        str.append("\r\n===========================");
        return str.toString();
    }
}
