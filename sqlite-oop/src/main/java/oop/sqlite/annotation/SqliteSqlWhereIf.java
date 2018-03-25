package oop.sqlite.annotation;

/**
 * 自定义注解辅助，条件判断注解，用于生成动态SQL
 *
 * @author 欧阳洁
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Repeatable(SqliteSqlWhereIfs.class)
public @interface SqliteSqlWhereIf {
    /**
     * 判断条件标识ID
     *
     * @return
     */
    int testId();

    /**
     * 所属层级，默认0，最外层
     *
     * @return
     */
    int parentTestId() default 0;

    /**
     * 判断条件类型，==、>、<、>=、<=、eq、ne
     *
     * @return
     */
    String testType() default "eq";

    /**
     * 判断字段名
     *
     * @return
     */
    String testName();

    /**
     * 符合条件的值
     *
     * @return
     */
    String[] testTrueValue();

    /**
     * 符合条件对应的动态SQL
     *
     * @return
     */
    String[] testTrueSql() default "";

    /**
     * 占位符顺序对应的参数顺序，默认不带参数
     *
     * @return
     */
    String[] params() default "";
}
