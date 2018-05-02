package oop.sqlite.connection;

import java.sql.Connection;

/**
 * Sqlite连接自定义封装类
 *
 * @author 欧阳洁
 * @since 2018-05-02 13:26
 */
public class SqliteBaseConnection {
    private long createTime;// 时间戳
    private Connection connection;// 链接对象

    public SqliteBaseConnection() {
        // TODO Auto-generated constructor stub
    }

    public SqliteBaseConnection(long createTime, Connection connection) {
        this.createTime = createTime;
        this.connection = connection;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
