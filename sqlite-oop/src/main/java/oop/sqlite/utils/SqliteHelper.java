package oop.sqlite.utils;

import oop.sqlite.annotation.SqliteTable;
import oop.sqlite.config.SqliteConfig;
import oop.sqlite.console.SqliteConsoleBaseEntity;
import oop.sqlite.constant.SqliteConstant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础操作
 *
 * @author 欧阳洁
 * @create 2017-09-29 17:48
 **/
public class SqliteHelper {
    private String dbPath;//数据库路径
    private int dbType;//数据库类型

    /**
     * 构造函数
     *
     * @param targetClass
     */
    public SqliteHelper(Class<?> targetClass) {
        this.dbPath = SqliteUtils.isBlank(SqliteConfig.getUri()) ? SqliteConstant.DB_PATH : SqliteConfig.getUri();
        this.dbType = SqliteConstant.DB_TYPE_DEFAULT;
        SqliteTable sqliteTable = targetClass.getAnnotation(SqliteTable.class);
        if (null != sqliteTable) {
            this.dbPath = sqliteTable.dbPath();
            this.dbType = sqliteTable.dbType();
        }
        // 默认相对路径
        this.dbPath = SqliteUtils.getClassRootPath(this.dbPath);
    }

    /**
     * 构造函数
     *
     * @param dbPath
     */
    public SqliteHelper(String dbPath, boolean absolute) {
        if (SqliteUtils.isBlank(dbPath)) {
            this.dbPath = SqliteUtils.isBlank(SqliteConfig.getUri()) ? SqliteConstant.DB_PATH : SqliteConfig.getUri();
        } else {
            this.dbPath = dbPath;
        }
        this.dbType = SqliteConstant.DB_TYPE_DEFAULT;
        if (!absolute) {
            this.dbPath = SqliteUtils.getClassRootPath(this.dbPath);
        }
    }

    /**
     * 构造函数
     *
     * @param dbPath
     */
    public SqliteHelper(String dbPath) {
        if (SqliteUtils.isBlank(dbPath)) {
            this.dbPath = SqliteUtils.isBlank(SqliteConfig.getUri()) ? SqliteConstant.DB_PATH : SqliteConfig.getUri();
        }
        this.dbType = SqliteConstant.DB_TYPE_DEFAULT;
        // 默认相对路径
        this.dbPath = SqliteUtils.getClassRootPath(this.dbPath);
    }

    /**
     * 插入
     *
     * @param sql
     * @return
     */
    public int insert(String sql) {
        return this.execute(sql);
    }

    /**
     * 修改
     *
     * @param sql
     * @return
     */
    public int update(String sql) {
        return this.execute(sql);
    }

    /**
     * 删除
     *
     * @param sql
     * @return
     */
    public int delete(String sql) {
        return this.execute(sql);
    }

    /**
     * 插入，带参数
     *
     * @param sql
     * @param param
     * @return
     */
    public int insert(String sql, List<Object> param) {
        return this.execute(sql, param);
    }

    /**
     * 修改，带参数
     *
     * @param sql
     * @param param
     * @return
     */
    public int update(String sql, List<Object> param) {
        return this.execute(sql, param);
    }

    /**
     * 删除，带参数
     *
     * @param sql
     * @param param
     * @return
     */
    public int delete(String sql, List<Object> param) {
        return this.execute(sql, param);
    }

    /**
     * 创建
     *
     * @param sql
     * @return
     */
    public int create(String sql) {
        return this.execute(sql);
    }

    /**
     * 数据库连接获取
     *
     * @return
     */
    private String getDBUrl() {
        StringBuffer currentDbPathSb = new StringBuffer("jdbc:sqlite:/").append(this.dbPath);
        switch (this.dbType) {
            case SqliteConstant.DB_TYPE_BY_MINUTE:
                currentDbPathSb.append(SqliteUtils.nowFormatStr("yyyyMMddHHmm")).append(".db");
                break;
            case SqliteConstant.DB_TYPE_BY_HOUR:
                currentDbPathSb.append(SqliteUtils.nowFormatStr("yyyyMMddHH")).append(".db");
                break;
            case SqliteConstant.DB_TYPE_BY_DAY:
                currentDbPathSb.append(SqliteUtils.nowFormatStr("yyyyMMdd")).append(".db");
                break;
            case SqliteConstant.DB_TYPE_BY_MOUTH:
                currentDbPathSb.append(SqliteUtils.nowFormatStr("yyyyMM")).append(".db");
                break;
            case SqliteConstant.DB_TYPE_BY_YEAR:
                currentDbPathSb.append(SqliteUtils.nowFormatStr("yyyy")).append(".db");
                break;
            default:
                break;
        }
        String JDBC_URL = currentDbPathSb.toString();
        if (SqliteUtils.isWindows()) {
            return JDBC_URL.toLowerCase();
        }
        return JDBC_URL;
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
     * 查询语句执行，返回list格式的json字符串
     *
     * @param sql
     * @return
     */
    public String queryJsonResult(String sql) {
        return this.queryJsonResult(sql, null);
    }

    /**
     * 查询语句执行，返回list格式的json字符串
     *
     * @param sql
     * @param columnMap
     * @return
     */
    public String queryJsonResult(String sql, Map<String, String> columnMap) {
        Connection connection = null;
        try {
            // create a database connection
            String JDBC_URL = this.getDBUrl();
            connection = DriverManager.getConnection(JDBC_URL);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(300); // set timeout to 30 sec.
            System.out.println("执行查询语句==> " + sql);
            ResultSet rs = statement.executeQuery(sql);

            return this.getDataJson(rs, columnMap);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询语句执行，返回list格式的json字符串，带参数
     *
     * @param sql
     * @param param
     * @param columnMap
     * @return
     */
    public String queryJsonResult(String sql, List<Object> param, Map<String, String> columnMap) {
        Connection connection = null;
        try {
            // create a database connection
            String JDBC_URL = this.getDBUrl();
            connection = DriverManager.getConnection(JDBC_URL);
            System.out.println("执行查询语句==> " + sql);
            PreparedStatement prep = connection.prepareStatement(sql);
            prep.setQueryTimeout(300);
            if (SqliteUtils.isNotEmpty(param)) {
                int count = 1;
                for (Object o : param) {
                    prep.setObject(count++, o);
                }
            }

            ResultSet rs = prep.executeQuery();

            return this.getDataJson(rs, columnMap);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取查询条数的结果
     * @param sql
     * @return
     */
    public int queryCountResult(String sql){
        Connection connection = null;
        try {
            // create a database connection
            String JDBC_URL = this.getDBUrl();
            connection = DriverManager.getConnection(JDBC_URL);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(300); // set timeout to 30 sec.
            System.out.println("执行查询语句==> " + sql);
            ResultSet rs = statement.executeQuery(sql);

            rs.next();//第一行
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询条数语句执行，返回条数，带参数
     *
     * @param sql
     * @param param
     * @return
     */
    public int queryCountResult(String sql, List<Object> param) {
        Connection connection = null;
        try {
            // create a database connection
            String JDBC_URL = this.getDBUrl();
            connection = DriverManager.getConnection(JDBC_URL);
            System.out.println("执行查询语句==> " + sql);
            PreparedStatement prep = connection.prepareStatement(sql);
            prep.setQueryTimeout(300);
            if (SqliteUtils.isNotEmpty(param)) {
                int count = 1;
                for (Object o : param) {
                    prep.setObject(count++, o);
                }
            }

            ResultSet rs = prep.executeQuery();
            rs.next();//第一行
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询语句执行，返回list格式的json字符串
     *
     * @param sql
     * @return
     */
    public List<Map<String, Object>> query(String sql) {
        Connection connection = null;
        try {
            // create a database connection
            String JDBC_URL = this.getDBUrl();
            connection = DriverManager.getConnection(JDBC_URL);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(300); // set timeout to 30 sec.
            System.out.println("执行查询语句==> " + sql);
            ResultSet rs = statement.executeQuery(sql);

            return this.getListMap(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询语句执行，返回list格式的json字符串
     *
     * @param sql
     * @return
     */
    public SqliteConsoleBaseEntity queryForConsole(String sql) {
        Connection connection = null;
        SqliteConsoleBaseEntity consoleResult = new SqliteConsoleBaseEntity();
        try {
            // create a database connection
            String JDBC_URL = this.getDBUrl();
            connection = DriverManager.getConnection(JDBC_URL);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(300); // set timeout to 30 sec.
            System.out.println("执行查询语句==> " + sql);
            ResultSet rs = statement.executeQuery(sql);
            List<Map<String, Object>> queryResult = this.getListMap(rs);
            consoleResult.setQueryResult(queryResult);
            consoleResult.setInfactLine(queryResult.size());
        } catch (SQLException e) {
            e.printStackTrace();
            consoleResult.setHasException(true);
            consoleResult.setSqlException(e);
        } catch (Exception e) {
            e.printStackTrace();
            consoleResult.setHasException(true);
            consoleResult.setException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                consoleResult.setHasException(true);
                consoleResult.setException(e);
            }
        }
        return consoleResult;
    }

    /**
     * 查询语句执行，返回list格式的json字符串，带参数
     *
     * @param sql
     * @param param
     * @return
     */
    public List<Map<String, Object>> query(String sql, List<Object> param) {
        Connection connection = null;
        try {
            // create a database connection
            String JDBC_URL = this.getDBUrl();
            connection = DriverManager.getConnection(JDBC_URL);
            System.out.println("执行查询语句==> " + sql);
            PreparedStatement prep = connection.prepareStatement(sql);
            prep.setQueryTimeout(300);
            if (SqliteUtils.isNotEmpty(param)) {
                int count = 1;
                for (Object o : param) {
                    prep.setObject(count++, o);
                }
            }

            ResultSet rs = prep.executeQuery();

            return this.getListMap(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 非查询语句执行，带参数的
     *
     * @param sql
     * @param param
     * @return
     */
    public int execute(String sql, List<Object> param) {
        Connection connection = null;
        try {
            // create a database connection
            String JDBC_URL = this.getDBUrl();
            connection = DriverManager.getConnection(JDBC_URL);
            System.out.println("执行非查询语句==> " + sql);
            PreparedStatement prep = connection.prepareStatement(sql);
            prep.setQueryTimeout(30);
            if (SqliteUtils.isNotEmpty(param)) {
                int count = 1;
                for (Object o : param) {
                    prep.setObject(count++, o);
                }
            }
            int result = prep.executeUpdate();
            System.out.println("执行非查询语句影响行数==> " + result);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行cmd命令
     *
     * @param cmd
     */
    public String cmdExec(String cmd) {
        StringBuffer cmdConnect = new StringBuffer("sqlite3 ").append(this.dbPath).append("\n");
        cmdConnect.append(cmd).append("\n");
        Runtime rt = Runtime.getRuntime();
        InputStream isNormal = null;
        InputStream isError = null;
        Process process = null;
        ByteArrayOutputStream baos = null;
        try {
            process = rt.exec(cmdConnect.toString());
            baos = new ByteArrayOutputStream();
            int i;
            isNormal = process.getInputStream();
            if (null != isNormal) {
                while ((i = isNormal.read()) != -1) {
                    baos.write(i);
                }
            }
            isError = process.getErrorStream();
            if (null != isError) {
                while ((i = isError.read()) != -1) {
                    baos.write(i);
                }
            }
            String str = baos.toString();
            System.out.println("执行cmd命令[" + cmd + "]==> " + str);
            return str;
        } catch (IOException e) {
            e.printStackTrace();
            return "Exception";
        } finally {
            try {
                if (null != isNormal) {
                    isNormal.close();
                }
                if (null != isError) {
                    isError.close();
                }
                if (null != baos) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取数据库里面的表名
     *
     * @return
     */
    public String[] getTableNameArr() {
        String result = this.cmdExec(".tables");
        if (!SqliteUtils.isBlank(result) && !result.startsWith("Error:") && !"Exception".equals(result)) {
            result = result.replaceAll("\r", " ");
            result = result.replaceAll("\n", " ");
            while (result.indexOf("  ") > 0) {
                result = result.replaceAll("  ", " ");
            }
            String[] arr = result.split(" ");
            return arr;
        }
        return null;
    }

    /**
     * 非查询语句执行，无参数
     *
     * @param sql
     * @return
     */
    public int execute(String sql) {
        Connection connection = null;
        try {
            // create a database connection
            String JDBC_URL = this.getDBUrl();
            connection = DriverManager.getConnection(JDBC_URL);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.
            System.out.println("执行非查询语句==> " + sql);
            int result = statement.executeUpdate(sql);
            System.out.println("执行非查询语句影响行数==> " + result);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 非查询语句执行，无参数
     *
     * @param sql
     * @return
     */
    public SqliteConsoleBaseEntity executeForConsole(String sql) {
        Connection connection = null;
        SqliteConsoleBaseEntity consoleResult = new SqliteConsoleBaseEntity();
        try {
            // create a database connection
            String JDBC_URL = this.getDBUrl();
            connection = DriverManager.getConnection(JDBC_URL);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.
            System.out.println("执行非查询语句==> " + sql);
            int result = statement.executeUpdate(sql);
            System.out.println("执行非查询语句影响行数==> " + result);
            consoleResult.setInfactLine(result);
        } catch (SQLException e) {
            e.printStackTrace();
            consoleResult.setHasException(true);
            consoleResult.setSqlException(e);
        } catch (Exception e) {
            e.printStackTrace();
            consoleResult.setHasException(true);
            consoleResult.setException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                consoleResult.setHasException(true);
                consoleResult.setException(e);
            }
        }
        return consoleResult;
    }

    /**
     * cmd语句执行
     *
     * @param cmd
     * @return
     */
    public SqliteConsoleBaseEntity cmdExecForConsole(String cmd) {
        Connection connection = null;
        SqliteConsoleBaseEntity consoleResult = new SqliteConsoleBaseEntity();
        try {
            System.out.println("执行非查询语句==> " + cmd);
            String result = this.cmdExec(cmd);
            System.out.println("执行非查询语句影响行数==> " + result);
            consoleResult.setCmdResult(result);
            consoleResult.setInfactLine(0);
        } catch (Exception e) {
            e.printStackTrace();
            consoleResult.setHasException(true);
            consoleResult.setException(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                consoleResult.setHasException(true);
                consoleResult.setException(e);
            }
        }
        return consoleResult;
    }

    /**
     * 根据结果集返回数据json
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public String getDataJson(ResultSet rs, Map<String, String> columnMap) throws SQLException {
        String[] nameArr = null;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        int rows = 1;
        while (rs.next()) {
            if (rows++ == 1) {
                nameArr = getNameArr(rs);// 获取列名
            }

            Map<String, Object> one = new LinkedHashMap<String, Object>();
            for (int i = 0; i < nameArr.length; i++) {
                String nameKey = null == columnMap ? nameArr[i] : columnMap.get(nameArr[i]);
                nameKey = null == nameKey ? nameArr[i] : nameKey;
                one.put(nameKey, rs.getObject(i + 1));
            }
            result.add(one);
        }
        String dataStr = SqliteUtils.getJson(result);
        System.out.println("执行查询语句结果==> " + dataStr);
        return dataStr;
    }

    /**
     * 根据结果集返回数据json
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public String getDataJson(ResultSet rs) throws SQLException {
        String[] nameArr = null;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        int rows = 1;
        while (rs.next()) {
            if (rows++ == 1) {
                nameArr = getNameArr(rs);// 获取列名
            }

            Map<String, Object> one = new LinkedHashMap<String, Object>();
            for (int i = 0; i < nameArr.length; i++) {
                String nameKey = nameArr[i];
                one.put(nameKey, rs.getObject(i + 1));
            }
            result.add(one);
        }
        String dataStr = SqliteUtils.getJson(result);
        System.out.println("执行查询语句结果==> " + dataStr);
        return dataStr;
    }

    /**
     * 根据结果集返回数据json
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> getListMap(ResultSet rs) throws SQLException {
        String[] nameArr = null;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        int rows = 1;
        while (rs.next()) {
            if (rows++ == 1) {
                nameArr = getNameArr(rs);// 获取列名
            }

            Map<String, Object> one = new LinkedHashMap<String, Object>();
            for (int i = 0; i < nameArr.length; i++) {
                String nameKey = nameArr[i];
                one.put(nameKey, rs.getObject(i + 1));
            }
            result.add(one);
        }
        return result;
    }

    /**
     * 根据结果集返回列集合
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public String[] getNameArr(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        String[] nameArr = new String[count];
        for (int i = 0; i < count; i++) {
            nameArr[i] = rsmd.getColumnName(i + 1);
            nameArr[i] = null == nameArr[i] ? "null" : nameArr[i].toLowerCase();
        }
        return nameArr;
    }

    static {//加载驱动器
        try {
            // load the sqlite-JDBC driver using the current class loader
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*————————————————测试sqlite————————————————*/
    public static void test() {
        Connection connection = null;
        try {
            String TEST_DB_PATH = SqliteUtils.getClassRootPath(SqliteConstant.TEST_DB_PATH);
            // create a database connection
            String connectStr = getDBUrl(TEST_DB_PATH);
            connection = DriverManager.getConnection(connectStr);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.
            statement.executeUpdate("create table if not exists person (id integer, name string)");
            statement.executeUpdate("insert into person values(1, 'leo')");
            statement.executeUpdate("insert into person values(2, 'yui')");
            ResultSet rs = statement.executeQuery("select * from person");
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            String[] name = new String[count];
            for (int i = 0; i < count; i++) {
                name[i] = rsmd.getColumnName(i + 1);
            }
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

            while (rs.next()) {
                Map<String, Object> one = new LinkedHashMap<String, Object>();

                for (int i = 0; i < name.length; i++) {
                    Object value = rs.getObject(i + 1);
                    System.out.println(name[i] + ":" + value);
                    one.put(name[i], value);
                }
                result.add(one);
            }
            String dataRes = SqliteUtils.getJson(result);
            System.out.println(dataRes);

            PreparedStatement prep = connection.prepareStatement(
                    "insert into person values (?, ?)");
            prep.setObject(1, 5);
            prep.setObject(2, "asdfasdfas");
            prep.execute();
            prep = connection.prepareStatement(
                    "select * from person where id=?");
            prep.setObject(1, 5);
            rs = prep.executeQuery();
            while (rs.next()) {
                System.out.println("id = " + rs.getString("id"));
                System.out.println("name = " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /*————————————————测试sqlite————————————————*/
}
