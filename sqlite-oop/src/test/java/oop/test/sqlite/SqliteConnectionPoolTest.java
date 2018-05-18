package oop.test.sqlite;

import oop.sqlite.connection.SqliteConnectionPool;
import oop.sqlite.thread.SqliteThreadUtils;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Sqlite线程连接池测试
 *
 * @author 欧阳洁
 * @since 2018-05-03 18:27
 */
public class SqliteConnectionPoolTest {
    @Test
    public void test() throws ClassNotFoundException {
        // 初始化连接池线程
        SqliteConnectionPool.initConnectPoolThreads();
        // 检查连接池线程
        SqliteConnectionPool.checkTreadActiveStatus();
        try {
            // 取两个连接，观察十秒看线程执行打印日志
            SqliteConnectionPool.getConnection();
            SqliteConnectionPool.getConnection();
            SqliteThreadUtils.sleep(10000);
            // 取三个连接，观察十秒看线程执行打印日志
            SqliteConnectionPool.getConnection();
            SqliteConnectionPool.getConnection();
            SqliteConnectionPool.getConnection();
            SqliteThreadUtils.sleep(10000);
            // 关闭所有连接池线程，观察十秒看线程执行打印日志
            SqliteConnectionPool.switchPool(false);
            SqliteThreadUtils.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
