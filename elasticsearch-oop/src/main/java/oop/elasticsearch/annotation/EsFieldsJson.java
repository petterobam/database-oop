package oop.elasticsearch.annotation;

import oop.elasticsearch.constant.EsConstant;

/**
 * 每个属性注解json属性字符串
 *
 * @author 欧阳洁
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface EsFieldsJson {
    /**
     * 每个字段的属性json
     *
     * @return
     */
    public String value() default EsConstant.FIELDS_JSON_DEFAULT;
}
