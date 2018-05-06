package oop.sqlite.exception;

import oop.sqlite.config.SqliteConfig;
import oop.sqlite.utils.SqliteLogUtils;
import oop.sqlite.utils.SqliteUtils;

/**
 * 连接池溢出异常
 *
 * @author 欧阳洁
 * @since 2018-05-02 16:43
 */
public final class SqliteConnectionMaxException extends Exception {
    private static final long serialVersionUID = 1L;

    private int max;

    /**
     * 构造函数
     */
    public SqliteConnectionMaxException(){
        this.max = SqliteConfig.getPoolConnectionMax();
    }

    /**
     * 构造函数
     * @param max
     */
    public SqliteConnectionMaxException(int max){
        this.max = max;
    }

    @Override
    public void printStackTrace() {
        SqliteLogUtils.error("连接池总数超过最大设置 max:{}", this.max);
        super.printStackTrace();
    }
}
