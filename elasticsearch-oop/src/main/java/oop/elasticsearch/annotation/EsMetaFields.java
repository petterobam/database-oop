package oop.elasticsearch.annotation;

import oop.elasticsearch.constant.EsFieldType;

/**
 * 每个属性注解json属性字符串
 *
 * @author 欧阳洁
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@EsTransient
public @interface EsMetaFields {
    /**
     * 元字段名
     * @return
     */
    String name();

    /**
     * 分析器类型
     * @return
     */
    boolean enabled();
}
