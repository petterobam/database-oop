package oop.access.connection;

import oop.access.config.AccessConfig;
import oop.access.utils.AccessLogUtils;
import oop.access.utils.AccessUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * access连接自定义封装类
 *
 * @author 欧阳洁
 * @since 2018-05-02 13:26
 */
public class AccessBaseConnection {
    private String uri;//数据库连接
    private long createTime;// 时间戳
    private Connection connection;// 链接对象
    private boolean crypt;// 是否加密库

    public AccessBaseConnection() {

    }

    /**
     * 构造函数
     * @param uri
     * @param createTime
     * @param connection
     */
    public AccessBaseConnection(String uri, long createTime, Connection connection) {
        this.setUriAndLoderConfig(uri);
        this.createTime = createTime;
        this.connection = connection;
    }

    /**
     * 重置连接uri，并且同时会刷新连接对象
     * @param uri
     */
    public boolean resetUri(String uri){
        this.setUriAndLoderConfig(uri);
        return this.refreshConnection();
    }

    /**
     * 刷新连接对象
     * @return
     */
    public boolean refreshConnection(){
        try {
            this.createTime = AccessUtils.getNowStamp();
            if(crypt){
                this.connection = DriverManager.getConnection(uri, AccessConfig.getUserName(),AccessConfig.getPassword());
            }else {
                if (AccessBaseConnectionFactory.USE_SELF_INNER_CONFIG) {
                    this.connection = DriverManager.getConnection(uri, AccessConfig.getConnectProperties());
                } else {
                    this.connection = DriverManager.getConnection(uri);
                }
            }
            return true;
        } catch (SQLException e) {
            AccessLogUtils.error("[refreshConnection]重新建立连接对象失败！",e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置uri并且加载配置
     * @param uri
     */
    public void setUriAndLoderConfig(String uri) {
        this.uri = uri;
        if(this.uri != null && this.uri.toLowerCase().endsWith(".mny")){
            this.crypt = true;
            if(AccessBaseConnectionFactory.USE_SELF_INNER_CONFIG){
                this.uri = AccessConfig.getConnectUriWithProperties(this.uri);
            }else {
                this.uri = AccessConfig.getConnectUriWithDefaulCryptProperties(this.uri);
            }
        }else {
            if(!AccessBaseConnectionFactory.USE_SELF_INNER_CONFIG){
                this.uri = AccessConfig.getConnectUriWithDefaultProperties(this.uri);
            }
        }
    }

    public String getUri() {
        return uri;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Connection getConnection() {
        if(null == this.connection){
            this.refreshConnection();
        }
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isCrypt() {
        return crypt;
    }

    public void setCrypt(boolean crypt) {
        this.crypt = crypt;
    }
}
