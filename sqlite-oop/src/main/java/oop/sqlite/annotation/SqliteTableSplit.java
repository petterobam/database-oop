package oop.sqlite.annotation;

/**
 * 分表属性注解
 * @author 欧阳洁
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqliteTableSplit {
    /**
     * 后缀链接字符
     * @return
     */
    String joinStr() default "_";
}
