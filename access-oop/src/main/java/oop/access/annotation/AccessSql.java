package oop.access.annotation;

/**
 * 自定义SQL注解类
 *
 * @author 欧阳洁
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface AccessSql {
    /**
     * 自定义的Sql语句，带占位符的Sql
     * @return
     */
    String sql();
    /**
     * 占位符顺序对应的参数顺序，默认不带参数
     * @return
     */
    String[] params() default "";
}
