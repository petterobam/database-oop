package oop.access.exception;

import oop.access.utils.AccessLogUtils;

/**
 * Access连接异常
 *
 * @author 欧阳洁
 * @since 2018-05-02 16:57
 */
public final class AccessConnectionException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * @param e
     */
    public AccessConnectionException(Exception e){
        super(e);
    }

    @Override
    public void printStackTrace() {
        AccessLogUtils.error("ERROR:[池对象创建异常]");
        super.printStackTrace();
    }
}
