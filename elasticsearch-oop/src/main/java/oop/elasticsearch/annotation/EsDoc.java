package oop.elasticsearch.annotation;

import oop.elasticsearch.constant.EsConstant;

/**
 * 文档索引和类型名注解类
 *
 * @author 欧阳洁
 * @create 2017-09-30 11:16
 **/
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface EsDoc {
    /**
     * 索引名
     * @return
     */
    String Index();

    /**
     * 类型名
     * @return
     */
    String Type();

    /**
     * 索引类型（动态索引配置）
     * @return
     */
    byte IndexType() default EsConstant.INDEX_TYPE_DEFAULT;
}
