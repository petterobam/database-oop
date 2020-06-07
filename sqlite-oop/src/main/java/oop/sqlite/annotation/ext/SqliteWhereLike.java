package oop.sqlite.annotation.ext;

/**
 * Sqlite的表字段注解类
 *
 * @author 欧阳洁
 * @create 2017-09-30 11:20
 **/
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqliteWhereLike {
    /**
     * 仅 like '%xxx'
     * @return
     */
    boolean onlyLeft() default false;

    /**
     * 仅 like 'xxx%'
     * @return
     */
    boolean onlyRight() default false;
}
