package oop.sqlite.config;

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
