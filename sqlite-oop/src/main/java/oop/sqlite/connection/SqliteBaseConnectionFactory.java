package oop.sqlite.connection;

import oop.sqlite.config.SqliteConfig;
import oop.sqlite.exception.SqliteConnectionMaxException;
import oop.sqlite.utils.SqliteLogUtils;
import oop.sqlite.utils.SqliteUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
    private static int CON_MIN = Integer.parseInt(SqliteConfig.getValue("sqlite.connection.min"), 1);// 初始池链接
    private static int CON_STEP = Integer.parseInt(SqliteConfig.getValue("sqlite.connection.step"), 1);// 每次最大补充线程数量
    protected static long CON_TIMEOUT = Integer.parseInt(SqliteConfig.getValue("sqlite.connection.timeout"),500000);// 超时线程回收
    protected static Vector<SqliteBaseConnection> sList = new Vector<SqliteBaseConnection>();// 链接容器
    protected static Vector<SqliteBaseConnection> sRunList = new Vector<SqliteBaseConnection>();// 已分配的链接容器
    private static boolean REFRESH_CON = false;

    static {
        init();
        new Thread(new SqliteConnectionPool()).start();
    }

    /**
     * 初始化连接池
     *
     * @throws SQLException
     */
    private static void init() {
        Connection con = null;
        Statement stmt = null;
        try {
            loadClass();
            addConnection(DEFAULT_DB_PATH,CON_MIN);
        } catch (Exception e) {
            sList.clear();
            SqliteLogUtils.error("ERROR:[池初始化失败][池容器已清空]");
            e.printStackTrace();
        } finally {
            try {
                if (con != null)
                    con.close();
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                SqliteLogUtils.error("ERROR:[池初始化失败][池容器已清空]");
                sList.clear();
                e.printStackTrace();
            }
        }
    }

    /**
     * @return 返回结果
     * @author 欧阳洁
     * @date 2018/5/2
     * @description
     */
    private static void loadClass() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
    }

    /**
     * 获取链接
     *
     * @return
     * @throws Exception
     * @throws SQLException
     * @throws SqliteConnectionMaxException
     */
    public Connection getConnection(String dbPath) throws SQLException, SqliteConnectionMaxException, SqliteConnectionMaxException {
        // 先进先出原则
        SqliteBaseConnection sPojo = null;
        checkConnection(dbPath);
        synchronized (sList) {
            checkList();
            sPojo = sList.get(0);
            sList.remove(0);
            sRunList.add(sPojo);
        }
        if (sPojo == null || sPojo.getConnection().isClosed()) {
            throw new SqliteConnectionMaxException(CON_MAX);
        }
        return sPojo.getConnection();
    }

    /**
     * 检查容器是否满足使用需求
     *
     * @throws SqliteConnectionMaxException
     * @throws SQLException
     * @author Allen
     * @date 2016年10月31日
     */
    private void checkList() throws SqliteConnectionMaxException, SQLException {
        if (sList.size() == 0) {
            // 超过最大线程数
            int num = 0;
            if ((num = CON_MAX - sRunList.size() - sList.size()) <= 0) {
                throw new SqliteConnectionMaxException();
            }
            // 剩余可增加线程数
            num = num > CON_STEP ? CON_STEP : num;
            addConnection(DEFAULT_DB_PATH,num);
        }
    }

    /**
     * @param dbPath
     * @return 返回结果
     * @author 欧阳洁
     * @date 2018/5/2
     * @description
     */
    private synchronized static void checkConnection(String dbPath) {
        try {
            if(REFRESH_CON) {
                for (SqliteBaseConnection con : sList) {
                    con.setConnection(createConnection(dbPath));
                }
                REFRESH_CON = false;
            }
        } catch (Exception e) {
            SqliteLogUtils.error("ERROR:[新日期检查更新失败][池容器已清空]");
            e.printStackTrace();
        }
    }

    /**
     * 创建connection
     * @param dbPath
     * @return
     */
    public static Connection createConnection(String dbPath) throws SQLException {
        String JDBC = getDBUrl(dbPath);
        try {
            return DriverManager.getConnection(JDBC);
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
    private static String getDBUrl(String dbPath) {
        String JDBC = "jdbc:sqlite:/" + dbPath;
        if (SqliteUtils.isWindows()) {
            dbPath = dbPath.toLowerCase();
            JDBC = "jdbc:sqlite:/" + dbPath;
        }
        return JDBC;
    }

    /**
     * 添加新的链接到容器
     *
     * @param num
     * @throws SQLException
     * @author Allen
     * @date 2016年10月31日
     */
    private static void addConnection(String dbPath, int num) throws SQLException {
        for (int i = 0; i < num; i++) {
            checkConnection(dbPath);
            sList.add(new SqliteBaseConnection(SqliteUtils.getNowStamp(), createConnection(dbPath)));
        }
    }
}
