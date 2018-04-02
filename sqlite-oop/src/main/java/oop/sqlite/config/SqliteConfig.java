package oop.sqlite.config;

import java.io.IOException;
import java.util.Properties;

/**
 * sqlite的一些静态配置
 */
public class SqliteConfig {
    /**
     * 程序数据库
     */
    public static final String DB_PATH = "database/sqlite.db";
    public static final String DB_PATH1 = "database/sqlite1.db";
    public static final String DB_PATH2 = "database/sqlite2.db";
    public static final String DB_PATH3 = "database/sqlite3.db";
    /**
     * 测试数据库
     */
    public static final String TEST_DB_PATH = "database/test.db";
    /**
     * 程序数据库动态生成规则
     */
    public static final int DB_TYPE_DEFAULT = 0;//不分库
    public static final int DB_TYPE_BY_MINUTE = 1;//按分钟自动分库
    public static final int DB_TYPE_BY_HOUR = 2;//按小时自动分库
    public static final int DB_TYPE_BY_DAY = 3;//按天自动分库
    public static final int DB_TYPE_BY_MOUTH = 4;//按月自动分库
    public static final int DB_TYPE_BY_YEAR = 5;//按年自动分库

    /**
     * sqlite配置文件的配置信息
     */
    private static Properties properties = new Properties();

    /**
     * 启动程序的时候读取properties配置文件信息，并永久缓存
     */
    static {
        try {
            //使用 properties 配置文件，默认在 config/sqlite.properties 目录下面，若该项目被引用，启动项目只需要在相同目录下相同配置文件覆盖即可生效
            properties.load(SqliteConfig.class.getClassLoader().getResourceAsStream("config/sqlite.properties"));
            //properties.loadFromXML(SqliteConfig.class.getClassLoader().getResourceAsStream("config/sqlite.xml"));
        } catch (IOException e) {
            e.printStackTrace();
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
     * @return
     */
    public static String getUri() {
        return properties.getProperty("sqlite.uri");
    }
    /**
     * 默认要配置的参数：用户名
     * @return
     */
    public static String getUserName() {
        return properties.getProperty("sqlite.username");
    }
    /**
     * 默认要配置的参数：密码
     * @return
     */
    public static String getPassword() {
        return properties.getProperty("sqlite.password");
    }
}
