package oop.elasticsearch.constant;

/**
 * 字段type
 * @author 欧阳洁
 */
public class EsFieldType {
    /*
    简单域类型
    字符串: string, text, keyword
    整数 : byte, short, integer, long, alf_float，scaled_float
    浮点数: float, double
    布尔型: boolean
    日期: date
    */
    public static final String STRING = "string";
    public static final String TEXT = "text";
    public static final String KEYWORD = "keyword";
    public static final String BYTE = "byte";
    public static final String SHORT = "short";
    public static final String INTEGER = "integer";
    public static final String LONG = "long";
    public static final String ALF_FLOAT = "alf_float";
    public static final String SCALED_FLOAT = "scaled_float";

    /*
    复杂域类型
    数组：array, 普通数据数组
    对象：object, json格式对象
    对象数组：nested, json对象数组
    */
    public static final String ARRAY = "array";
    public static final String OBJECT = "object";
    public static final String NESTED = "nested";

    /*
    其他数据类型
    经纬度点类型：geo_point
    地理形状类型：geo_shape
    IP数据类型：ip, 用于IPv4和IPv6地址
    完成数据类型：completion 提供自动完成的建议
    令牌计数数据类型：token_count 计算字符串中的令牌数量
    mapper-murmur3：murmur3 在索引时计算值的哈希值并将它们存储在索引中
    渗滤器类型：percolator, 接受来自query-dsl的查询
    join 数据类型：join, 为相同索引内的文档定义父/子关系
    */
    public static final String GEO_POINT = "geo_point";
    public static final String GEO_SHAPE = "geo_shape";
    public static final String IP = "ip";
    public static final String COMPLETION = "completion";
    public static final String TOKEN_COUNT = "token_count";
    public static final String MURMUR3 = "murmur3";
    public static final String PERCOLATOR = "percolator";
    public static final String JOIN = "join";
}
