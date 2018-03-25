package my.sqlite.config;

/**
 * sqlite的一些静态配置
 */
public class SqliteConfig {
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
    public static final int DB_TYPE_DEFAULT = 0;
    public static final int DB_TYPE_BY_MINUTE = 1;
    public static final int DB_TYPE_BY_HOUR = 2;
    public static final int DB_TYPE_BY_DAY = 3;
    public static final int DB_TYPE_BY_MOUTH = 4;
    public static final int DB_TYPE_BY_YEAR = 5;
}
