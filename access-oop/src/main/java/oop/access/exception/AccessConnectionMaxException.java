package oop.access.exception;

import oop.access.config.AccessConfig;
import oop.access.utils.AccessLogUtils;

/**
 * 连接池溢出异常
 *
 * @author 欧阳洁
 * @since 2018-05-02 16:43
 */
public final class AccessConnectionMaxException extends Exception {
    private static final long serialVersionUID = 1L;

    private int max;

    /**
     * 构造函数
     */
    public AccessConnectionMaxException(){
        this.max = AccessConfig.getPoolConnectionMax();
    }

    /**
     * 构造函数
     * @param max
     */
    public AccessConnectionMaxException(int max){
        this.max = max;
    }

    @Override
    public void printStackTrace() {
        AccessLogUtils.error("连接池总数超过最大设置 max:{}", this.max);
        super.printStackTrace();
    }
}
