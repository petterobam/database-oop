package oop.access.constant;

/**
 * Access相关常量
 * @author 欧阳洁
 */
public class AccessConstant {
    /**
     * 程序数据库
     */
    public static final String DB_PATH = "database/access.mdb";
    /**
     * 测试数据库
     */
    public static final String TEST_DB_PATH = "database/test.mdb";
    /**
     * 程序数据库动态生成规则
     */
    public static final int DB_TYPE_DEFAULT = 0;//不分库
    public static final int DB_TYPE_BY_MINUTE = 1;//按分钟自动分库
    public static final int DB_TYPE_BY_HOUR = 2;//按小时自动分库
    public static final int DB_TYPE_BY_DAY = 3;//按天自动分库
    public static final int DB_TYPE_BY_MOUTH = 4;//按月自动分库
    public static final int DB_TYPE_BY_YEAR = 5;//按年自动分库
    /**
     * 默认批量插入条数
     */
    public static final int DEFAULT_BATCH_COUNT = 100;
}
