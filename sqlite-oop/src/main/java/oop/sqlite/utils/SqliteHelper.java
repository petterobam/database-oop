package oop.sqlite.utils;

import oop.sqlite.annotation.SqliteTable;
import oop.sqlite.base.SqliteBaseEntity;
import oop.sqlite.config.SqliteConfig;
import oop.sqlite.console.SqliteConsoleBaseEntity;
import oop.sqlite.constant.SqliteConstant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
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
public class SqliteHelper<T extends SqliteBaseEntity> {
    /**
     * 数据库路径
     */
    private String dbPath;
    /**
     * 数据库类型
     */
    private int dbType;

    /**
     * 构造函数
     *
     * @param targetClass
     */
    public SqliteHelper(Class<T> targetClass) {
        this.dbPath = SqliteUtils.isBlank(SqliteConfig.getUri()) ? SqliteConstant.DB_PATH : SqliteConfig.getUri();
        this.dbType = SqliteConstant.DB_TYPE_DEFAULT;
        SqliteTable sqliteTable = targetClass.getAnnotation(SqliteTable.class);
        if (null != sqliteTable) {
            this.dbPath = sqliteTable.dbPath();
            this.dbType = sqliteTable.dbType();
        }
        // 默认相对路径
        if(SqliteConfig.isPathBaseClasspath()) {
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
        if(SqliteConfig.isPathBaseClasspath()) {
            this.dbPath = SqliteUtils.getClassRootPath(this.dbPath);
        }
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
     * 创建
     *
     * @param sql
     * @return
     */
    public int create(String sql) {
        return this.execute(sql);
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
     * 插入
     *
     * @param sql
     * @param param
     * @return
     */
    public int insert(String sql, List<Object> param) {
        return this.execute(sql, param);
    }

    /**
     * 批量插入
     *
     * @param sqlList
     * @return
     */
    public int batchInsertSql(List<String> sqlList) {
        return this.batchExecuteSql(sqlList);
    }

    /**
     * 批量插入
     *
     * @param sqlList
     * @param batchCount
     * @return
     */
    public int batchInsertSql(List<String> sqlList, int batchCount) {
        return this.batchExecuteSql(sqlList, batchCount);
    }

    /**
     * 批量插入，带参数
     *
     * @param sqlWithParamList
     * @return
     */
    public int batchInsert(List<T> sqlWithParamList) {
        return this.batchExecute(sqlWithParamList);
    }

    /**
     * 批量插入，带参数
     *
     * @param sqlWithParamList
     * @param batchCount
     * @return
     */
    public int batchInsert(List<T> sqlWithParamList, int batchCount) {
        return this.batchExecute(sqlWithParamList, batchCount);
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
     * 批量更新
     *
     * @param sqlList
     * @return
     */
    public int batchUpdateSql(List<String> sqlList) {
        return this.batchExecuteSql(sqlList);
    }

    /**
     * 批量更新
     *
     * @param sqlList
     * @param batchCount
     * @return
     */
    public int batchUpdateSql(List<String> sqlList, int batchCount) {
        return this.batchExecuteSql(sqlList, batchCount);
    }

    /**
     * 批量更新，带参数
     *
     * @param sqlWithParamList
     * @return
     */
    public int batchUpdate(List<T> sqlWithParamList) {
        return this.batchExecute(sqlWithParamList);
    }

    /**
     * 批量更新，带参数
     *
     * @param sqlWithParamList
     * @param batchCount
     * @return
     */
    public int batchUpdate(List<T> sqlWithParamList, int batchCount) {
        return this.batchExecute(sqlWithParamList, batchCount);
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
     * 批量删除
     *
     * @param sqlList
     * @return
     */
    public int batchDeleteSql(List<String> sqlList) {
        return this.batchExecuteSql(sqlList);
    }

    /**
     * 批量删除
     *
     * @param sqlList
     * @param batchCount
     * @return
     */
    public int batchDeleteSql(List<String> sqlList, int batchCount) {
        return this.batchExecuteSql(sqlList, batchCount);
    }

    /**
     * 批量删除，带参数
     *
     * @param sqlWithParamList
     * @return
     */
    public int batchDelete(List<T> sqlWithParamList) {
        return this.batchExecute(sqlWithParamList);
    }

    /**
     * 批量删除，带参数
     *
     * @param sqlWithParamList
     * @param batchCount
     * @return
     */
    public int batchDelete(List<T> sqlWithParamList, int batchCount) {
        return this.batchExecute(sqlWithParamList, batchCount);
    }

    /**
     * 根据Id集合批量删除
     *
     * @param idParamSql
     * @param idList
     * @param batchCount
     * @return
     */
    public int batchDeleteByIdList(String idParamSql, List<Object> idList, int batchCount) {
        return this.batchExecuteByIdList(idParamSql, idList, batchCount);
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
            connection = getConnection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(300); // set timeout to 30 sec.
            SqliteLogUtils.info("执行查询语句==> " + sql);
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
            connection = getConnection();
            SqliteLogUtils.info("执行查询语句==> " + sql);
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
     *
     * @param sql
     * @return
     */
    public int queryCountResult(String sql) {
        Connection connection = null;
        try {
            // create a database connection
            connection = getConnection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(300); // set timeout to 30 sec.
            SqliteLogUtils.info("执行查询语句==> " + sql);
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
            connection = getConnection();
            SqliteLogUtils.info("执行查询语句==> " + sql);
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
            connection = getConnection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(300); // set timeout to 30 sec.
            SqliteLogUtils.info("执行查询语句==> " + sql);
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
            connection = getConnection();
            SqliteLogUtils.info("执行查询语句==> " + sql);
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
            connection = getConnection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.
            SqliteLogUtils.info("执行非查询语句==> " + sql);
            int result = statement.executeUpdate(sql);
            SqliteLogUtils.info("执行非查询语句影响行数==> " + result);
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
            connection = getConnection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(300); // set timeout to 30 sec.
            SqliteLogUtils.info("执行查询语句==> " + sql);
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
     * cmd语句执行
     *
     * @param cmd
     * @return
     */
    public SqliteConsoleBaseEntity cmdExecForConsole(String cmd) {
        SqliteConsoleBaseEntity consoleResult = new SqliteConsoleBaseEntity();
        try {
            SqliteLogUtils.info("执行非查询语句==> " + cmd);
            String result = this.cmdExec(cmd);
            SqliteLogUtils.info("执行非查询语句影响行数==> " + result);
            consoleResult.setCmdResult(result);
            consoleResult.setInfactLine(0);
        } catch (Exception e) {
            e.printStackTrace();
            consoleResult.setHasException(true);
            consoleResult.setException(e);
        }
        return consoleResult;
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
            connection = getConnection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.
            SqliteLogUtils.info("执行非查询语句==> " + sql);
            int result = statement.executeUpdate(sql);
            SqliteLogUtils.info("执行非查询语句影响行数==> " + result);
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
            connection = getConnection();
            SqliteLogUtils.info("执行非查询语句==> " + sql);
            PreparedStatement prep = connection.prepareStatement(sql);
            prep.setQueryTimeout(30);
            if (SqliteUtils.isNotEmpty(param)) {
                int count = 1;
                for (Object o : param) {
                    prep.setObject(count++, o);
                }
            }
            int result = prep.executeUpdate();
            SqliteLogUtils.info("执行非查询语句影响行数==> " + result);
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
     * 非查询语句批量执行Sql语句
     *
     * @param sqlList
     * @return
     */
    public int batchExecuteSql(List<String> sqlList) {
        return this.batchExecuteSql(sqlList, SqliteConstant.DEFAULT_BATCH_COUNT);
    }

    /**
     * 非查询语句批量执行Sql语句
     *
     * @param sqlList
     * @param batchCount
     * @return
     */
    public int batchExecuteSql(List<String> sqlList, int batchCount) {
        Connection connection = null;
        try {
            int result = 0;
            if (SqliteUtils.isNotEmpty(sqlList)) {
                if (batchCount <= 0) {//默认批量提交粒度100条
                    batchCount = SqliteConstant.DEFAULT_BATCH_COUNT;
                }
                // create a database connection
                connection = this.getConnection();
                connection.setAutoCommit(false);//单次执行不自动提交
                Statement statement = connection.createStatement();
                for (String sql : sqlList) {
                    if (!SqliteUtils.isBlank(sql)) {
                        statement.addBatch(sql);
                        result++;
                    }
                    if (result % batchCount == 0) {
                        statement.executeBatch();
                        connection.commit();// 提交
                        if (null == connection || connection.isClosed()) {
                            //如果连接关闭了 就在创建一个 为什么要这样 原因是 connection.commit()后可能conn被关闭
                            connection = this.getConnection();
                            connection.setAutoCommit(false);
                            statement = connection.createStatement();
                        }
                    }
                }
                statement.executeBatch();
                statement.close();
                connection.commit();// 提交
                connection.setAutoCommit(true);
            }
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
     * 非查询语句批量执行Sql语句
     *
     * @param sqlWithParamList
     * @return
     */
    public int batchExecute(List<T> sqlWithParamList) {
        return this.batchExecute(sqlWithParamList, SqliteConstant.DEFAULT_BATCH_COUNT);
    }

    /**
     * 非查询语句批量执行Sql语句
     *
     * @param sqlWithParamList
     * @param batchCount
     * @return
     */
    public int batchExecute(List<T> sqlWithParamList, int batchCount) {
        Connection connection = null;
        try {
            int result = 0;
            if (SqliteUtils.isNotEmpty(sqlWithParamList)) {
                if (batchCount <= 0) {//默认批量提交粒度100条
                    batchCount = SqliteConstant.DEFAULT_BATCH_COUNT;
                }
                // create a database connection
                connection = getConnection();
                connection.setAutoCommit(false);//单次执行不自动提交
                PreparedStatement prep = null;
                String preSql = null;
                int currCount = 0;
                for (T sqlAndParam : sqlWithParamList) {
                    if (SqliteUtils.isBlank(sqlAndParam.getCurrentSql())) {
                        continue;
                    }
                    if (!SqliteUtils.equals(preSql, sqlAndParam.getCurrentSql()) || currCount % batchCount == 0) {
                        if(currCount > 0) {
                            currCount = 0;
                            prep.executeBatch();
                            connection.commit();// 提交
                            if (null == connection || connection.isClosed()) {
                                //如果连接关闭了 就在创建一个 为什么要这样 原因是 connection.commit()后可能conn被关闭
                                connection = this.getConnection();
                                connection.setAutoCommit(false);
                            }
                        }
                        prep = connection.prepareStatement(sqlAndParam.getCurrentSql());
                    }
                    if (SqliteUtils.isNotEmpty(sqlAndParam.getCurrentParam())) {
                        int count = 1;
                        for (Object o : sqlAndParam.getCurrentParam()) {
                            prep.setObject(count++, o);
                        }
                    }
                    prep.addBatch();
                    result++;
                    currCount++;
                    preSql = sqlAndParam.getCurrentSql();
                }
                prep.executeBatch();
                connection.commit();// 提交
                connection.setAutoCommit(true);
            }
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
     * 非查询语句批量执行Sql语句
     *
     * @param idParamSql
     * @param idList
     * @param batchCount
     * @return
     */
    public int batchExecuteByIdList(String idParamSql, List<Object> idList, int batchCount) {
        Connection connection = null;
        try {
            int result = 0;
            if (SqliteUtils.isNotEmpty(idList)) {
                if (batchCount <= 0) {//默认批量提交粒度100条
                    batchCount = SqliteConstant.DEFAULT_BATCH_COUNT;
                }
                // create a database connection
                connection = getConnection();
                connection.setAutoCommit(false);//单次执行不自动提交
                PreparedStatement prep = null;
                prep = connection.prepareStatement(idParamSql);
                for (Object id : idList) {
                    if (null == id) {
                        continue;
                    }
                    prep.setObject(1, id);
                    prep.addBatch();
                    result++;
                    if (result % batchCount == 0) {
                        prep.executeBatch();
                        connection.commit();// 提交
                        if (null == connection || connection.isClosed()) { //如果连接关闭了 就在创建一个 为什么要这样 原因是 connection.commit()后可能conn被关闭
                            connection = this.getConnection();
                            connection.setAutoCommit(false);
                        }
                    }
                }
                prep.executeBatch();
                prep.close();
                connection.commit();// 提交
                connection.setAutoCommit(true);
            }
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
        SqliteLogUtils.info("执行查询语句结果==> " + dataStr);
        return dataStr;
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
        SqliteLogUtils.info("执行查询语句结果==> " + dataStr);
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
            SqliteLogUtils.info("执行cmd命令[" + cmd + "]==> " + str);
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
     * 数据库连接获取
     *
     * @return
     */
    private Connection getConnection() throws SQLException {
        String currDbPath = getCurrDbPath();
        return SqliteConnectionUtils.getConnection(currDbPath);
    }

    /**
     * 获取当前数据库名（分库）
     * @return
     */
    public String getCurrDbPath(){
        if(SqliteConstant.DB_TYPE_DEFAULT == this.dbType){
            return this.dbPath;
        }
        StringBuffer currentDbPathSb = new StringBuffer(this.dbPath);
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
        String currDbPath = currentDbPathSb.toString();
        return currDbPath;
    }
}
