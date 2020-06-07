package oop.access.utils;

import oop.access.connection.AccessBaseConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Access连接工具类
 *
 * @author 欧阳洁
 * @since 2018-05-04 10:09
 */
public class AccessConnectionUtils {
    /**
     * 获取默认连接
     * @return
     */
    public static Connection getConnection() throws SQLException {
        return AccessBaseConnectionFactory.getConnection();
    }
    /**
     * 获取连接
     * @param dbPath
     * @return
     */
    public static Connection getConnection(String dbPath) throws SQLException {
        return AccessBaseConnectionFactory.getConnection(dbPath);
    }
}
