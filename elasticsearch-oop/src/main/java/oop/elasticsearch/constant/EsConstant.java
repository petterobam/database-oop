package oop.elasticsearch.constant;

/**
 * ES相关的常量
 * @author 欧阳洁
 */
public class EsConstant {
    /**
     * 程序数据库动态生成规则
     */
    public static final byte INDEX_TYPE_DEFAULT = 0;//不分索引
    public static final byte INDEX_TYPE_BY_MINUTE = 1;//按分钟自动分索引
    public static final byte INDEX_TYPE_BY_HOUR = 2;//按小时自动分索引
    public static final byte INDEX_TYPE_BY_DAY = 3;//按天自动分索引
    public static final byte INDEX_TYPE_BY_MOUTH = 4;//按月自动分索引
    public static final byte INDEX_TYPE_BY_YEAR = 5;//按年自动分索引

    /**
     * 默认字段属性集JSON字符串
     */
    public static final String FIELDS_JSON_DEFAULT = "{'type':'keyword'}";
}
