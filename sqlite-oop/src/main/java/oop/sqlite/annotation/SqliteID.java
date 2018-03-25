package my.sqlite.annotation;

/**
 * Sqlite的ID注解类
 *
 * @author 欧阳洁
 * @create 2017-09-30 11:20
 **/
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqliteID {
    /**
     * 主键列名，默认空
     * @return
     */
    String name() default "";
    /**
     * 主键默认类型，默认integer类型
     * @return
     */
    String type() default "integer";

    /**
     * 主键是否自增长，默认是true
     * @return
     */
    boolean autoincrement() default true;
}
