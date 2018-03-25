package oop.sqlite.annotation;

/**
 * 实现SqliteSqlWhereIf的重复注解
 * @author 欧阳洁
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqliteSqlWhereIfs {
    SqliteSqlWhereIf[] value();
}
