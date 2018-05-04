package oop.sqlite.utils;

import oop.sqlite.connection.SqliteBaseConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Sqlite连接工具类
 *
 * @author 欧阳洁
 * @since 2018-05-04 10:09
 */
public class SqliteConnectionUtils {
    /**
     * 获取默认连接
     * @return
     */
    public static Connection getConnection() throws SQLException {
        return SqliteBaseConnectionFactory.getConnection();
    }
    /**
     * 获取连接
     * @param dbPath
     * @return
     */
    public static Connection getConnection(String dbPath) throws SQLException {
        return SqliteBaseConnectionFactory.getConnection(dbPath);
    }
}
