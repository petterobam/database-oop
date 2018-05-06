package oop.sqlite.connection;

import oop.sqlite.config.SqliteConfig;
import oop.sqlite.utils.SqliteLogUtils;
import oop.sqlite.utils.SqliteUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Sqlite连接自定义封装类
 *
 * @author 欧阳洁
 * @since 2018-05-02 13:26
 */
public class SqliteBaseConnection {
    private String uri;//数据库连接
    private long createTime;// 时间戳
    private Connection connection;// 链接对象

    public SqliteBaseConnection() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 构造函数
     * @param uri
     * @param createTime
     * @param connection
     */
    public SqliteBaseConnection(String uri,long createTime, Connection connection) {
        this.uri = uri;
        this.createTime = createTime;
        this.connection = connection;
    }

    /**
     * 重置连接uri，并且同时会刷新连接对象
     * @param uri
     */
    public boolean resetUri(String uri){
        try {
            this.uri = uri;
            this.createTime = SqliteUtils.getNowStamp();
            if(SqliteBaseConnectionFactory.USE_SELF_INNER_CONFIG){
                this.connection = DriverManager.getConnection(uri, SqliteConfig.getConfig().toProperties());
            }else {
                this.connection = DriverManager.getConnection(uri);
            }
            return true;
        } catch (SQLException e) {
            SqliteLogUtils.error("[resetUri]重置连接对象失败！",e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 刷新连接对象
     * @return
     */
    public boolean refreshConnection(){
        try {
            this.createTime = SqliteUtils.getNowStamp();
            if(SqliteBaseConnectionFactory.USE_SELF_INNER_CONFIG){
                this.connection = DriverManager.getConnection(uri, SqliteConfig.getConfig().toProperties());
            }else {
                this.connection = DriverManager.getConnection(uri);
            }
            return true;
        } catch (SQLException e) {
            SqliteLogUtils.error("[refreshConnection]重新建立连接对象失败！",e);
            e.printStackTrace();
            return false;
        }
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Connection getConnection() {
        if(null == this.connection){
            try {
                if(SqliteBaseConnectionFactory.USE_SELF_INNER_CONFIG){
                    this.connection = DriverManager.getConnection(uri, SqliteConfig.getConfig().toProperties());
                }else {
                    this.connection = DriverManager.getConnection(uri);
                }
            } catch (SQLException e) {
                SqliteLogUtils.error("连接建立失败！",e);
                e.printStackTrace();
            }
        }
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
