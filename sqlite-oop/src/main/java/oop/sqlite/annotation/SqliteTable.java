package my.sqlite.annotation;


/**
 * 表名注解类
 *
 * @author 欧阳洁
 * @create 2017-09-30 11:16
 **/
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqliteTable {
    /**
     * 表名
     * @return
     */
    String name() default "";
    /**
     * 数据库文件路径
     * @return
     */
    String dbPath() default my.sqlite.config.SqliteConfig.DB_PATH;
    /**
     * 数据库文件路径
     * @return
     */
    int dbType() default my.sqlite.config.SqliteConfig.DB_TYPE_DEFAULT;
}