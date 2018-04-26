package oop.elasticsearch.annotation;

import java.lang.annotation.ElementType;

/**
 * 不做存储的字段
 * @author 欧阳洁
 */
@java.lang.annotation.Target(value = {ElementType.FIELD,ElementType.TYPE})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface EsTransient {
}
