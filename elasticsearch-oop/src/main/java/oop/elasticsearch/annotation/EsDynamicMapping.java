package oop.elasticsearch.annotation;

/**
 * 文档索引和类型名注解类
 *
 * @author 欧阳洁
 * @create 2017-09-30 11:16
 **/
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface EsDynamicMapping {
    /**
     * true/false，日期检测
     *
     * @return
     */
    public boolean date_detection() default false;

    /**
     * 日期格式定义，例如 ["MMMM/dd/yyyy"]
     *
     * @return
     */
    public String[] dynamic_date_formats() default {"MMMM/dd/yyyy"};

    /**
     * true/false，数字检测
     *
     * @return
     */
    public boolean numeric_detection() default false;
}
