package oop.sqlite.annotation;

/**
 * Sqlite的表字段注解类
 *
 * @author 欧阳洁
 * @create 2017-09-30 11:20
 **/
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqliteColumn {
    /**
     * 列名，默认空
     *
     * @return
     */
    String name() default "";

    /**
     * 主键默认类型，默认最大20位长度的字符串
     *
     * @return
     */
    String type() default "char(20)";

    /**
     * 是否不为空，默认否
     *
     * @return
     */
    boolean notNull() default false;
}
