package oop.access.connection;

import oop.access.config.AccessConfig;
import oop.access.thread.AccessThreadUtils;
import oop.access.utils.AccessLogUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 池回收线程
 *
 * @author 欧阳洁
 * @since 2018-05-02 13:41
 */
public class AccessConnectionPool extends AccessBaseConnectionFactory {
    private static int SLEEP = AccessConfig.getPoolThreadSleep();// 线程每次SLEEP时长
    private static boolean CHECK_RUN_ACTIVE = false;// 检查 ClearRunConnectionThread 线程是否在
    private static boolean CHECK_IDLE_ACTIVE = false;// 检查 RefreshIdleConnectionThread 线程是否在
    private static boolean CHECK_MONITOR_ACTIVE = false;// 检查 MonitorConnectionPoolThread 线程是否在
    private static int COUNT_RUN_ACTIVE = 0;// 检查 ClearRunConnectionThread 线程活跃数量
    private static int COUNT_IDLE_ACTIVE = 0;// 检查 RefreshIdleConnectionThread 线程活跃数量
    private static int COUNT_MONITOR_ACTIVE = 0;// 检查 MonitorConnectionPoolThread 线程活跃数量

    /**
     * LINK 线程池
     */
    private static final ExecutorService CONNECTION_POOL_EXETHREAD = new ThreadPoolExecutor(3, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(2000),
            AccessThreadUtils.buildJobFactory("Access 连接池 监控系统 线程池"), new ThreadPoolExecutor.AbortPolicy());

    /**
     * 获取用于Access连接池监控用的线程池服务
     *
     * @return
     */
    public static ExecutorService getExethread() {
        return CONNECTION_POOL_EXETHREAD;
    }

    /**
     * 开启连接池的线程检查，一次性
     */
    public static void checkTreadActiveStatus() {
        AccessConnectionPool.CHECK_RUN_ACTIVE = true;
        AccessConnectionPool.CHECK_IDLE_ACTIVE = true;
        AccessConnectionPool.CHECK_MONITOR_ACTIVE = true;
    }

    /**
     * 开启或关闭连接池线程
     */
    public static void switchPool(boolean on_off) {
        AccessConnectionPool.USE_CONNECT_POOL = on_off;
        if (!on_off) {
            idleConList.clear();
            runConList.clear();
        }
    }

    /**
     * 初始化连接池线程
     */
    public static void initConnectPoolThreads() {
        AccessConnectionPool.USE_CONNECT_POOL = true;
        // 添加 池监控并适时生产新连接对象的 线程
        AccessConnectionPool.addMonitorConnectionPoolThread();
        // 添加 池回收无效或久置超时的连接对象的 线程
        AccessConnectionPool.addClearRunConnectionThread();
        // 添加 池检查刷新闲置连接对象的 线程
        AccessConnectionPool.addRefreshIdleConnectionThread();
    }

    /**
     * 监控连接池中已分配的连接对象，定时收取没用的连接对象
     */
    public static void addClearRunConnectionThread() {
        CONNECTION_POOL_EXETHREAD.execute(new Runnable() {
            public void run() {
                COUNT_RUN_ACTIVE++;
                while (true) {
                    try {
                        if (AccessConnectionPool.CHECK_RUN_ACTIVE) {
                            AccessLogUtils.info("池回收无效或久置超时的连接对象的 线程运行中...当前该类线程数量：{}", COUNT_RUN_ACTIVE);
                            AccessConnectionPool.CHECK_RUN_ACTIVE = false;
                        }
                        if (!AccessConnectionPool.USE_CONNECT_POOL) {
                            AccessLogUtils.info("池回收无效或久置超时的连接对象的 线程结束...当前该类线程数量：{}", COUNT_RUN_ACTIVE - 1);
                            break;// 如果配置不使用连接池，结束线程
                        }
                        AccessThreadUtils.sleep(SLEEP);
                        checkAllRunningConnection();
                    } catch (InterruptedException e) {
                        idleConList.clear();
                        runConList.clear();
                        addClearRunConnectionThread();
                        AccessLogUtils.error("ERROR:[池回收无效或久置超时的连接对象的 线程死掉,重新添加新线程！]", e);
                        e.printStackTrace();
                        break;
                    }
                }
                COUNT_RUN_ACTIVE--;
            }
        });
    }

    /**
     * 监控连接池中闲置的连接对象，定时收取没用的连接对象
     */
    public static void addRefreshIdleConnectionThread() {
        CONNECTION_POOL_EXETHREAD.execute(new Runnable() {
            public void run() {
                COUNT_IDLE_ACTIVE++;
                while (true) {
                    try {
                        if (AccessConnectionPool.CHECK_MONITOR_ACTIVE) {
                            AccessLogUtils.info("池检查刷新闲置连接对象的 线程运行中...当前该类线程数量：{}", COUNT_IDLE_ACTIVE);
                            AccessConnectionPool.CHECK_MONITOR_ACTIVE = false;
                        }
                        if (!AccessConnectionPool.USE_CONNECT_POOL) {
                            AccessLogUtils.info("池检查刷新闲置连接对象的 线程结束...当前该类线程数量：{}", COUNT_IDLE_ACTIVE - 1);
                            break;// 如果配置不使用连接池，结束线程
                        }
                        AccessThreadUtils.sleep(SLEEP);
                        checkAllIdleConnection();
                    } catch (InterruptedException e) {
                        idleConList.clear();
                        runConList.clear();
                        addRefreshIdleConnectionThread();
                        AccessLogUtils.error("ERROR:[池检查刷新闲置连接对象的 线程死掉,重新添加新线程！]", e);
                        e.printStackTrace();
                        break;
                    }
                }
                COUNT_IDLE_ACTIVE--;
            }
        });
    }

    /**
     * 监控连接池中闲置的连接对象，定时收取没用的连接对象
     */
    public static void addMonitorConnectionPoolThread() {
        CONNECTION_POOL_EXETHREAD.execute(new Runnable() {
            public void run() {
                COUNT_MONITOR_ACTIVE++;
                while (true) {
                    try {
                        if (AccessConnectionPool.CHECK_IDLE_ACTIVE) {
                            AccessLogUtils.info("池监控并适时生产新连接对象的 线程运行中...当前该类线程数量：{}", COUNT_MONITOR_ACTIVE);
                            AccessConnectionPool.CHECK_IDLE_ACTIVE = false;
                        }
                        if (!AccessConnectionPool.USE_CONNECT_POOL) {
                            AccessLogUtils.info("池监控并适时生产新连接对象的 线程结束...当前该类线程数量：{}", COUNT_MONITOR_ACTIVE - 1);
                            break;// 如果配置不使用连接池，结束线程
                        }
                        AccessThreadUtils.sleep(SLEEP);
                        checkConnectionBox(null);
                    } catch (InterruptedException e) {
                        idleConList.clear();
                        runConList.clear();
                        addMonitorConnectionPoolThread();
                        AccessLogUtils.error("ERROR:[池监控并适时生产新连接对象的 线程死掉,重新添加新线程！]", e);
                        e.printStackTrace();
                        break;
                    }
                }
                COUNT_MONITOR_ACTIVE--;
            }
        });
    }
}
