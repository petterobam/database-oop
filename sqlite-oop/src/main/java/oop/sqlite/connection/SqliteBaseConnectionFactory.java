package oop.sqlite.connection;

import oop.sqlite.config.SqliteConfig;
import oop.sqlite.utils.SqliteLogUtils;
import oop.sqlite.utils.SqliteUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Sqlite连接工厂
 *
 * @author 欧阳洁
 * @since 2018-05-02 13:30
 */
public class SqliteBaseConnectionFactory {
    private static String DEFAULT_DB_PATH = SqliteConfig.getUri();//默认库
    private static int CON_MAX = SqliteUtils.parseInt(SqliteConfig.getValue("sqlite.connection.max"), 2);// 最大池链接
    private static int CON_MIN = SqliteUtils.parseInt(SqliteConfig.getValue("sqlite.connection.min"), 1);// 初始池链接
    private static int CON_STEP = SqliteUtils.parseInt(SqliteConfig.getValue("sqlite.connection.step"), 1);// 每次最大补充线程数量
    private static boolean REFRESH_CON_POOL = false;
    protected static boolean USE_CONNECT_POOL = Boolean.parseBoolean(SqliteConfig.getValue("sqlite.connection.pool"));
    protected static long CON_TIMEOUT = SqliteUtils.parseInt(SqliteConfig.getValue("sqlite.connection.timeout"), 500000);// 超时线程回收
    protected static Vector<SqliteBaseConnection> idleConList = new Vector<SqliteBaseConnection>();// 闲置连接
    protected static Vector<SqliteBaseConnection> runConList = new Vector<SqliteBaseConnection>();// 已分配的连接

    static {
        init();
        // 是否启用线程池
        if(USE_CONNECT_POOL){
            SqliteConnectionPool.initConnectPoolThreads();
        }
    }

    /**
     * 初始化连接池
     *
     * @throws SQLException
     */
    private static void init() {
        try {
            if(Boolean.parseBoolean(SqliteConfig.getValue("sqlite.path.classpath"))){
                DEFAULT_DB_PATH = SqliteUtils.getClassRootPath(DEFAULT_DB_PATH);
            }
            loadSqliteJdbcClass();//加载 org.sqlite.JDBC
            addConnection(DEFAULT_DB_PATH, CON_MIN);//默认预先建立一些连接到链接对象
        } catch (Exception e) {
            idleConList.clear();
            SqliteLogUtils.error("ERROR:[池初始化失败][池容器已清空]");
            e.printStackTrace();
        }
    }

    /**
     * 加载 org.sqlite.JDBC
     *
     * @return 返回结果
     * @author 欧阳洁
     * @date 2018/5/2
     * @description
     */
    private static void loadSqliteJdbcClass() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
    }

    /**
     * 获取链接
     *
     * @return
     * @throws Exception
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        // 先进先出原则
        SqliteBaseConnection currCon = null;
        synchronized (idleConList) {
            // 当可用连接池不为空时候
            if (SqliteUtils.isNotEmpty(idleConList)) {
                currCon = idleConList.get(0);
                idleConList.remove(0);
                addRunningConnection(currCon);
            }
            if (currCon == null || currCon.getConnection() == null || currCon.getConnection().isClosed()) {
                currCon = createBaseConnection();
                addRunningConnection(currCon);
            }
        }
        return currCon.getConnection();
    }
    /**
     * 获取链接
     *
     * @return
     * @throws Exception
     * @throws SQLException
     */
    public static Connection getConnection(String dbPath) throws SQLException {
        if(DEFAULT_DB_PATH.equals(dbPath)){
            return getConnection();
        }else {
            SqliteBaseConnection currCon = createBaseConnection(dbPath);
            addRunningConnection(currCon);
            return currCon.getConnection();
        }
    }

    /**
     * 添加已分配的连接到已分配队列
     *
     * @param running
     * @return
     */
    private static boolean addRunningConnection(SqliteBaseConnection running) {
        if (runConList.size() < CON_MAX) {
            runConList.add(running);
            return true;
        }
        SqliteLogUtils.warn("当前连接数量大于自定义的最大连接数量！");
        return false;
    }

    /**
     * 检查容器是否满足使用需求
     * @return
     */
    protected static boolean checkConnectionBox(String dbPath){
        // 如果连接池里闲置的连接没有
        int addNum = 0;
        if (idleConList.size() == 0) {
            // 超过最大线程数
            addNum = CON_MAX - runConList.size();
            if (addNum <= 0) {
                SqliteLogUtils.warn("当前使用的连接数量大于自定义的最大连接[{}]限制！", CON_MAX);
                return false;
            }
        } else if (idleConList.size() < CON_MIN) {
            addNum = CON_MAX - idleConList.size() - runConList.size();
            if (addNum <= 0) {
                SqliteLogUtils.warn("连接池中当前使用的连接数量太多，闲置连接数量小于自定义的最小[{}]闲置连接！", CON_MIN);
                return false;
            }
        }
        // 剩余可增加连接对象数，默认每次最多增加 CON_STEP 个
        addNum = addNum > CON_STEP ? CON_STEP : addNum;
        if (SqliteUtils.isBlank(dbPath)) {
            addConnection(DEFAULT_DB_PATH, addNum);
        } else {
            addConnection(dbPath, addNum);
        }
        return true;
    }

    /**
     * 检查闲置连接
     * @return 检查到并重置的无效闲置连接数量
     * @author 欧阳洁
     */
    protected synchronized static int checkAllIdleConnection() {
        int idleRefreshCount = 0;
        SqliteLogUtils.info("INFO:[可用链接数:{}]，[已用连接数:{}]",idleConList.size(),runConList.size());
        try {
            if (REFRESH_CON_POOL) {//是否刷新闲置连接池里面的连接
                for (SqliteBaseConnection con : idleConList) {
                    if (null == con.getConnection() || con.getConnection().isClosed()) {
                        con.refreshConnection();
                        idleRefreshCount++;
                    }
                }
                REFRESH_CON_POOL = false;
            }
        } catch (Exception e) {
            SqliteLogUtils.error("ERROR:[新日期检查更新失败][池容器已清空]");
            e.printStackTrace();
        }
        SqliteLogUtils.info("检测到闲置连接池中无效连接并重置的数量：{}",idleRefreshCount);
        return idleRefreshCount;
    }

    /**
     * 检查已分配的连接
     * @return 清除已分配使用中的无效连接数量
     * @author 欧阳洁
     */
    protected synchronized static int checkAllRunningConnection() {
        int runningRemoveCount = 0;
        SqliteLogUtils.info("INFO:[可用链接数:{}]，[已用连接数:{}]",idleConList.size(),runConList.size());
        SqliteBaseConnection con = null;
        for (int i = 0; i < runConList.size(); i++) {
            con = runConList.get(i);
            try {
                if(null == con || null == con.getConnection() || con.getConnection().isClosed() || SqliteUtils.getNowStamp() - con.getCreateTime() > CON_TIMEOUT){
                    runConList.remove(i--);
                    runningRemoveCount++;
                }
            } catch (SQLException e) {
                SqliteLogUtils.error("检查已分配的连接出现异常！",e);
                e.printStackTrace();
            }
        }
        SqliteLogUtils.info("定时清除已分配的废弃或超时连接，清除数量：{}",runningRemoveCount);
        return runningRemoveCount;
    }

    /**
     * 创建connection
     *
     * @return
     */
    public static SqliteBaseConnection createBaseConnection() throws SQLException {
        return createBaseConnection(DEFAULT_DB_PATH);
    }

    /**
     * 创建connection
     *
     * @param dbPath
     * @return
     */
    public static SqliteBaseConnection createBaseConnection(String dbPath) throws SQLException {
        if (SqliteUtils.isBlank(dbPath)) {
            return null;
        }
        // 获取连接字符串
        String JDBC = getJDBCStr(dbPath);
        try {
            SqliteBaseConnection result = new SqliteBaseConnection();
            if (!result.resetUri(JDBC)) {
                result.setCreateTime(SqliteUtils.getNowStamp());
                result.setConnection(DriverManager.getConnection(JDBC));
            }
            return result;
        } catch (SQLException e) {
            SqliteLogUtils.error("ERROR:[Connection对象创建异常]");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 获取固定格式的数据库路径信息
     *
     * @param dbPath
     * @return
     */
    private static String getJDBCStr(String dbPath) {
        if(null != dbPath && !dbPath.startsWith("jdbc:sqlite")) {
            String JDBC = "jdbc:sqlite:/" + dbPath;
            if (SqliteUtils.isWindows()) {
                dbPath = dbPath.toLowerCase();
                JDBC = "jdbc:sqlite:/" + dbPath;
            }
            return JDBC;
        }else {
            return dbPath;
        }
    }

    /**
     * 添加新的链接到容器
     *
     * @param dbPath 数据库的路径
     * @param num    添加的数量
     * @throws SQLException
     */
    private static void addConnection(String dbPath, int num) {
        for (int i = 0; i < num; i++) {
            // 添加前检查连接池里面所有连接对象
            checkAllIdleConnection();
            // 创建新的连接对象
            SqliteBaseConnection newSqliteConnection = null;
            try {
                newSqliteConnection = createBaseConnection(dbPath);
            } catch (SQLException e) {
                SqliteLogUtils.error("[addConnection]添加新连接异常！",e);
                e.printStackTrace();
            }
            idleConList.add(newSqliteConnection);
        }
    }
}
