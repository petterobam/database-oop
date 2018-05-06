package oop.sqlite.config;

import oop.sqlite.utils.SqliteUtils;
import org.sqlite.SQLiteConfig;

import java.io.IOException;
import java.util.Properties;

/**
 * sqlite的一些静态配置
 */
public class SqliteConfig {

    /**
     * sqlite配置文件的配置信息
     */
    private static Properties properties = new Properties();

    private static SQLiteConfig config = null;

    /**
     * 启动程序的时候读取properties配置文件信息，并永久缓存
     */
    static {
        try {
            //使用 properties 配置文件，默认在 config/sqlite.properties 目录下面，若该项目被引用，启动项目只需要在相同目录下相同配置文件覆盖即可生效
            properties.load(SqliteConfig.class.getClassLoader().getResourceAsStream("config/sqlite.properties"));
            //properties.loadFromXML(SqliteConfig.class.getClassLoader().getResourceAsStream("config/sqlite.xml"));
            //创建Sqlite API 配置对象
            config = new SQLiteConfig(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单例模式获取配置对象
     *
     * @return
     */
    public static SQLiteConfig getConfig() {
        if (null == config) {
            config = new SQLiteConfig(properties);
        }
        return config;
    }

    /**
     * 动态加载新配置
     *
     * @param classPath
     */
    public static void loadConfig(String classPath) {
        try {
            //使用 properties 配置文件，默认在 config/sqlite.properties 目录下面，若该项目被引用，启动项目只需要在相同目录下相同配置文件覆盖即可生效
            properties.load(SqliteConfig.class.getClassLoader().getResourceAsStream(classPath));
            //properties.loadFromXML(SqliteConfig.class.getClassLoader().getResourceAsStream("config/sqlite.xml"));
            //创建Sqlite API 配置对象
            config = new SQLiteConfig(properties);
        } catch (IOException e) {
            e.printStackTrace();
            properties = new Properties();
        }
    }

    /**
     * 根据key得到value的值
     */
    public static String getValue(String key) {
        return properties.getProperty(key);
    }

    /**
     * 默认要配置的参数：数据库uri
     *
     * @return
     */
    public static String getUri() {
        return properties.getProperty("sqlite.uri");
    }

    /**
     * 默认要配置的参数：用户名
     *
     * @return
     */
    public static String getUserName() {
        return properties.getProperty("sqlite.username");
    }

    /**
     * 默认要配置的参数：密码
     *
     * @return
     */
    public static String getPassword() {
        return properties.getProperty("sqlite.password");
    }

    /*********************************************连接相关配置******************************************/
    /**
     * 获取connection对象的时候是否加载对内置配置的自定义配置
     *
     * @return
     */
    public static boolean isConnectionWithCofig() {
        return Boolean.parseBoolean(properties.getProperty("sqlite.config.enable"));
    }

    /**
     * 数据库路径是否是基于classpath路径
     *
     * @return
     */
    public static boolean isPathBaseClasspath() {
        return Boolean.parseBoolean(properties.getProperty("sqlite.path.classpath"));
    }

    /***********************************连接池相关配置*********************************************/
    /**
     * 是否开启连接池
     * @return
     */
    public static boolean isConnectionPoolEnable() {
        return Boolean.parseBoolean(properties.getProperty("sqlite.connection.pool.enable"));
    }

    /**
     * 连接池最大连接对象数量
     * @return
     */
    public static int getPoolConnectionMax() {
        return SqliteUtils.parseInt(properties.getProperty("sqlite.connection.max"), 2);
    }

    /**
     * 连接池最小连接对象数量
     * @return
     */
    public static int getPoolConnectionMin() {
        return SqliteUtils.parseInt(properties.getProperty("sqlite.connection.min"), 1);
    }

    /**
     * 连接池每次新增连接对象最大数量
     * @return
     */
    public static int getPoolConnectionStep() {
        return SqliteUtils.parseInt(properties.getProperty("sqlite.connection.step"), 1);
    }

    /**
     * 连接池分配后失效清除时间
     * @return
     */
    public static int getPoolConnectionTimeout() {
        return SqliteUtils.parseInt(properties.getProperty("sqlite.connection.timeout"), 500000);
    }

    /**
     * 连接池线程轮询时长
     * @return
     */
    public static int getPoolThreadSleep() {
        return SqliteUtils.parseInt(properties.getProperty("sqlite.pool.thread.sleep"), 1000);
    }
}
