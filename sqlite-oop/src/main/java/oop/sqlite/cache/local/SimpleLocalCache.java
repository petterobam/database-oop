package oop.sqlite.cache.local;

import oop.sqlite.cache.producter.ProducterOfList;
import oop.sqlite.cache.producter.ProducterOfObject;
import oop.sqlite.connection.SqliteConnectionPool;
import oop.sqlite.utils.SqliteLogUtils;
import oop.sqlite.utils.SqliteUtils;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 文件描述 时间
 *
 * @author ouyangjie
 * @Title: SimpleLocalCache
 * @date 2020/5/6 4:16 PM
 */
public class SimpleLocalCache {
    /**
     * 缓存默认失效时间(毫秒)
     */
    private static final long DEFAULT_TIMEOUT = 3600 * 1000;

    /**
     * 缓存清除动作执行间隔(秒)
     */
    private static final long TASK_TIME = 1;

    /**
     * 缓存存储的 map
     */
    private static final ConcurrentHashMap<String, CacheEntity> cacheMap = new ConcurrentHashMap<String, CacheEntity>();

    private static SimpleLocalCache cache = new SimpleLocalCache();

    public SimpleLocalCache() {
        SqliteLogUtils.info("本地简易缓存初始化...");
        SqliteConnectionPool.getExethread().submit(new TimeoutTimer());
        SqliteLogUtils.info("定时检查缓存任务启动完毕...");
        SqliteLogUtils.info("本地简易缓存初始化结束");
    }

    // 定时器线程-用于检查缓存过期
    static class TimeoutTimer implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(TASK_TIME);
                    for (String key : cacheMap.keySet()) {
                        CacheEntity entity = cacheMap.get(key);
                        long now = System.currentTimeMillis();
                        if ((now - entity.getTimeStamp()) >= entity.getExpire()) {
                            SqliteLogUtils.info("检查到 key={} 的缓存【{},{}】生命期已至",
                                    key, entity.getTimeStamp(), entity.getExpire());
                            cacheMap.remove(key);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 存储单元
     */
    static class CacheEntity {

        /**
         * 值
         */
        private Object value;

        /**
         * 过期时间(毫秒)
         */
        private long expire;

        /**
         * 创建时的时间戳
         */
        private long timeStamp;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public long getExpire() {
            return expire;
        }

        public void setExpire(long expire) {
            this.expire = expire;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }
    }

    public static boolean set(String key, Object value, long expire) {
        SqliteLogUtils.info("添加本地缓存 {}", key);
        cacheMap.put(key, setEntity(key, value, expire));
        return true;
    }

    public static boolean set(String key, Object value) {
        return set(key, value, DEFAULT_TIMEOUT);
    }

    private static CacheEntity setEntity(String key, Object value, long expire) {
        CacheEntity entity = new CacheEntity();
        entity.setValue(value);
        entity.setExpire(expire);
        entity.setTimeStamp(System.currentTimeMillis());
        SqliteLogUtils.info("设置有效时限：{}ms，当前时间：{}", expire, entity.getTimeStamp());
        return entity;
    }

    public static Object get(String key) {
        SqliteLogUtils.info("取缓存，key：{}", key);
        CacheEntity entity = cacheMap.get(key);

        if (entity == null) {
            SqliteLogUtils.info("缓存未命中，key:{}", key);
            return null;
        } else {
            Object value = entity.getValue();
            if (value == null) {
                return null;
            }
            SqliteLogUtils.info("缓存命中，key:{}", key);
            long now = System.currentTimeMillis();
            if ((now - entity.getTimeStamp()) >= entity.getExpire()) {
                SqliteLogUtils.info("检查到 key={} 的缓存【{},{}】生命期已至",
                        key, entity.getTimeStamp(), entity.getExpire());
                cacheMap.remove(key);
            }
            return value;
        }
    }

    public static <T> T getT(String key) {
        return (T) get(key);
    }

    public static void remove(String key) {
        cacheMap.remove(key);
    }

    public static void removeStartWith(String keyPrefix) {
        SqliteLogUtils.info("遍历删除有 {} 前缀的 key 的缓存...", keyPrefix);
        if (SqliteUtils.isBlank(keyPrefix)) {
            SqliteLogUtils.info("前缀未传入，终止！");
            return;
        }
        Iterator<String> keys = cacheMap.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            // 迭代器遍历可以删除
            if (null != key && key.startsWith(keyPrefix)) {
                cacheMap.remove(key);
                SqliteLogUtils.info("找到 key={} 的缓存，删除", key);
            }
        }
        SqliteLogUtils.info("遍历删除有 {} 前缀的 key 的缓存结束", keyPrefix);
    }

    /**
     * 用生产者模式提取公共代码，先看缓存，不存在通过生产者获取，再缓存
     *
     * @param cacheKey
     * @param cacheFirst
     * @param producter lambada 动态函数
     * @return <T>
     */
    public static <T> List<T> fetchDataForCache(String cacheKey, boolean cacheFirst, ProducterOfList<T> producter) {
        List<T> result = null;
        // 是否从缓存里面取
        if (cacheFirst) {
            // 数据量较大，检查缓存里面是否已经有了
            SqliteLogUtils.info("尝试从缓存获取 {} 结果", cacheKey);
            result = SimpleLocalCache.getT(cacheKey);
        }
        if (null == result) {
            result = producter.fetchList();
            SqliteLogUtils.info("将结果(共{}条)缓存...", null != result ? result.size() : 0);
            SimpleLocalCache.set(cacheKey, result);
        } else {
            SqliteLogUtils.info("缓存获取结果成功！");
        }
        return result;
    }

    /**
     * 用生产者模式提取公共代码，先看缓存再读表，并且读表缓存
     * @param cacheKey
     * @param cacheFirst
     * @param producter
     * @return <T>
     */
    public static <T> T fetchDataForCache(String cacheKey, boolean cacheFirst, ProducterOfObject<T> producter) {
        T result = null;
        // 是否从缓存里面取
        if (cacheFirst) {
            // 数据量较大，检查缓存里面是否已经有了
            SqliteLogUtils.info("尝试从缓存获取 {} 结果", cacheKey);
            result = SimpleLocalCache.getT(cacheKey);
        }
        if (null == result) {
            result = producter.fetchObject();
            SqliteLogUtils.info("将结果缓存...");
            SimpleLocalCache.set(cacheKey, result);
        } else {
            SqliteLogUtils.info("缓存获取结果成功！");
        }
        return result;
    }
}
