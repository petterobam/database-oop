package oop.elasticsearch.annotation;

/**
 * 文档索引和类型名注解类
 *
 * @author 欧阳洁
 * @create 2017-09-30 11:16
 **/
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface EsMappingFile {
    /**
     * mapping文件路径，classpath:下，一般放置于 es.mapping包下面
     *
     * @return
     */
    public String value();
}
