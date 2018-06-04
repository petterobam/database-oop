package oop.access.config;

import oop.access.utils.AccessUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * access的一些静态配置
 */
public class AccessConfig {

    /**
     * access配置文件的配置信息
     */
    private static Properties properties = new Properties();

    /**
     * 连接属性
     */
    private static Properties connectProperties = new Properties();

    /**
     * 启动程序的时候读取properties配置文件信息，并永久缓存
     */
    static {
        try {
            //使用 properties 配置文件，默认在 config/access.properties 目录下面，若该项目被引用，启动项目只需要在相同目录下相同配置文件覆盖即可生效
            properties.load(AccessConfig.class.getClassLoader().getResourceAsStream("config/access.properties"));
            //properties.loadFromXML(AccessConfig.class.getClassLoader().getResourceAsStream("config/access.xml"));
            loadConnectProperties(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载连接属性
     *
     * @param properties
     */
    private static void loadConnectProperties(Properties properties) {
        // 是否打开扩展功能
        String openExclusive = properties.getProperty("openExclusive");
        // 是否忽略SQL语句的大小写
        String ignoreCase = properties.getProperty("ignoreCase");
        // 是否开启内存模式
        String memory = properties.getProperty("memory");
        // 密码库链接辅助类
        String jackcessOpener = getJackcessOpener();
        if ("false".equalsIgnoreCase(openExclusive)) {
            connectProperties.put("openExclusive", "false");
        } else {
            // 默认打开
            connectProperties.put("openExclusive", "true");
        }
        if ("false".equalsIgnoreCase(ignoreCase)) {
            connectProperties.put("ignoreCase", "false");
        } else {
            // 默认忽略
            connectProperties.put("ignoreCase", "true");
        }
        connectProperties.put("memory", Boolean.parseBoolean(memory) ? "true" : "false");
        connectProperties.put("jackcessOpener", jackcessOpener);
    }

    /**
     * 单例模式获取配置对象
     *
     * @return
     */
    public static Properties getConfigProperties() {
        return properties;
    }

    /**
     * 获取连接配置对象
     *
     * @return
     */
    public static Properties getConnectProperties() {
        return connectProperties;
    }

    /**
     * 获取连接属性拼接的uri
     *
     * @param uri
     * @return
     */
    public static String getConnectUriWithProperties(String uri) {
        if (AccessUtils.isBlank(uri)) {
            return null;
        }
        if (uri.endsWith(";")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        StringBuffer sb = new StringBuffer(uri);

        for (Map.Entry<Object, Object> entry : connectProperties.entrySet()) {
            sb.append(";").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    /**
     * 获取连接属性拼接的uri
     *
     * @param uri
     * @return
     */
    public static String getConnectUriWithDefaultProperties(String uri) {
        if (AccessUtils.isBlank(uri)) {
            return null;
        }
        StringBuffer sb = new StringBuffer(uri);
        if (!uri.endsWith(";")) {
            sb.append(";");
        }
        sb.append("openExclusive=true;ignoreCase=true");
        return sb.toString();
    }

    /**
     * 获取连接属性拼接的uri，带密码的默认配置
     *
     * @param uri
     * @return
     */
    public static String getConnectUriWithDefaulCryptProperties(String uri) {
        if (AccessUtils.isBlank(uri)) {
            return null;
        }
        StringBuffer sb = new StringBuffer(uri);
        if (!uri.endsWith(";")) {
            sb.append(";");
        }
        sb.append("openExclusive=true;ignoreCase=true;jackcessOpener=").append(getJackcessOpener());
        return sb.toString();
    }

    /**
     * 动态加载新配置
     *
     * @param classPath
     */
    public static void loadConfig(String classPath) {
        try {
            //使用 properties 配置文件，默认在 config/access.properties 目录下面，若该项目被引用，启动项目只需要在相同目录下相同配置文件覆盖即可生效
            properties.load(AccessConfig.class.getClassLoader().getResourceAsStream(classPath));
            //properties.loadFromXML(AccessConfig.class.getClassLoader().getResourceAsStream("config/access.xml"));
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
        return properties.getProperty("access.uri");
    }

    /**
     * 默认要配置的参数：用户名
     *
     * @return
     */
    public static String getUserName() {
        return properties.getProperty("access.username");
    }

    /**
     * 获取密码库连接类路径
     *
     * @return
     */
    public static String getJackcessOpener() {
        String jackcessOpener = properties.getProperty("jackcessOpener");
        if (AccessUtils.isBlank(jackcessOpener)) {
            jackcessOpener = "oop.access.opener.AccessCryptCodecOpener";
        }
        return jackcessOpener;
    }

    /**
     * 默认要配置的参数：密码
     *
     * @return
     */
    public static String getPassword() {
        String password = properties.getProperty("access.password");
        if (AccessUtils.isBlank(password)) {
            password = "123456";
        }
        return password;
    }

    /*********************************************连接相关配置******************************************/
    /**
     * 获取connection对象的时候是否加载对内置配置的自定义配置
     *
     * @return
     */
    public static boolean isConnectionWithCofig() {
        return Boolean.parseBoolean(properties.getProperty("access.config.enable"));
    }

    /**
     * 数据库路径是否是基于classpath路径
     *
     * @return
     */
    public static boolean isPathBaseClasspath() {
        return Boolean.parseBoolean(properties.getProperty("access.path.classpath"));
    }

    /***********************************连接池相关配置*********************************************/
    /**
     * 是否开启连接池
     *
     * @return
     */
    public static boolean isConnectionPoolEnable() {
        return Boolean.parseBoolean(properties.getProperty("access.connection.pool.enable"));
    }

    /**
     * 连接池最大连接对象数量
     *
     * @return
     */
    public static int getPoolConnectionMax() {
        return AccessUtils.parseInt(properties.getProperty("access.connection.max"), 2);
    }

    /**
     * 连接池最小连接对象数量
     *
     * @return
     */
    public static int getPoolConnectionMin() {
        return AccessUtils.parseInt(properties.getProperty("access.connection.min"), 1);
    }

    /**
     * 连接池每次新增连接对象最大数量
     *
     * @return
     */
    public static int getPoolConnectionStep() {
        return AccessUtils.parseInt(properties.getProperty("access.connection.step"), 1);
    }

    /**
     * 连接池分配后失效清除时间
     *
     * @return
     */
    public static int getPoolConnectionTimeout() {
        return AccessUtils.parseInt(properties.getProperty("access.connection.timeout"), 500000);
    }

    /**
     * 获取Access库的版本格式
     *
     * @return
     */
    public static String getAccessFormat() {
        return properties.getProperty("access.file.format");
    }

    /**
     * 连接池线程轮询时长
     *
     * @return
     */
    public static int getPoolThreadSleep() {
        return AccessUtils.parseInt(properties.getProperty("access.pool.thread.sleep"), 1000);
    }
}
