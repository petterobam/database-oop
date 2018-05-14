package oop.sqlite.constant;

/**
 * Sqlite相关常量
 * @author 欧阳洁
 */
public class SqliteConstant {
    /**
     * 程序数据库
     */
    public static final String DB_PATH = "database/sqlite.db";
    public static final String DB_PATH1 = "database/sqlite1.db";
    public static final String DB_PATH2 = "database/sqlite2.db";
    public static final String DB_PATH3 = "database/sqlite3.db";
    /**
     * 测试数据库
     */
    public static final String TEST_DB_PATH = "database/test.db";
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
