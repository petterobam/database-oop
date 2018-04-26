package oop.elasticsearch.annotation;

import oop.elasticsearch.constant.EsConstant;
import oop.elasticsearch.constant.EsFieldType;

/**
 * 每个属性注解json属性字符串
 *
 * @author 欧阳洁
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface EsFields {
    /**
     * field类型
     * @return
     */
    String type() default EsFieldType.STRING;

    /**
     * 分析器类型
     * @return
     */
    String analyzer() default "";

    /**
     * 索引类型，仅用于string类型
     * @return
     */
    String index() default "";

    /**
     * 时间格式化成字符串格式
     * @return
     */
    String format() default "";

    /**
     * 额，这个是啥来着，请查看原生api文档
     * @return
     */
    String normalizer() default "";

    /**
     * 查询权重设置，默认1.0
     * @return
     */
    double boost() default 1.0;

    /**
     * 是否强制将数字型数据转化为对应类型
     * @return
     */
    boolean coerce() default false;

    /**
     * 定义的组字段名， 将多个字段的值复制到组字段中，然后可以将其作为单个字段进行查询
     * @return
     */
    String copy_to() default "";

    /**
     * 默认true，false则查询的结果集中没有该字段
     * @return
     */
    boolean doc_values() default true;

    /**
     * 默认true，多用于object类型字段
     * true：新检测到的字段被添加到映射中。（默认）
     * false：新检测的字段被忽略。这些字段不会被编入索引，因此不会被搜索到，但仍会出现在_source返回的匹配字段中。这些字段不会添加到映射中，必须明确添加新字段。
     * strict：如果检测到新字段，则抛出异常并拒绝该文档。必须将新字段明确添加到映射中。
     * @return
     */
    String dynamic() default "";

    /**
     * 默认true，用于开启关闭某些字段的索引建立，false则不做查询字段
     * @return
     */
    boolean enabled() default true;

    /**
     * 提高聚合查询速度，聚合字段可以设置为true
     * @return
     */
    boolean eager_global_ordinals() default false;

    /**
     * 聚合（分组）字段要设置为true
     * @return
     */
    boolean fielddata() default false;

    /**
     * 属性值为整数，针对字符类型数据，长度比设定值长的将不被索引和存储
     * @return
     */
    long ignore_above() default 0;

    /**
     * 定义为true的话，就算类型不对也不影响存储，默认为false
     * @return
     */
    boolean ignore_malformed() default false;

    /**
     * 被编入索引的项目定义
     * docs：只有文档编号被索引。
     * freqs：文件编号和术语频率被编入索引。
     * positions：文档编号，术语频率和术语位置（或顺序）被编入索引。
     * offsets：文档编号，术语频率，位置以及开始和结束字符偏移（将术语映射回原始字符串）进行索引。
     * @return
     */
    String index_options() default "";

    /**
     * 多领域定义，详情查看API
     * @return
     */
    String fields() default "";

    /**
     * 当传入null时候，用该属性的值替代null存储
     * @return
     */
    String null_value() default "";

    /**
     * 值为数字，定义匹配间隙，越大越模糊
     * @return
     */
    int position_increment_gap() default 0;

    /**
     *  定义搜索时的分析器，用于覆盖默认建立索引的分析器，通常不设定
     * @return
     */
    String search_analyzer() default "";

    /**
     * 定义评分算法
     * BM25：Okapi BM25算法。请参阅[]可插入相似性算法。](https://www.elastic.co/guide/en/elasticsearch/guide/master/pluggable-similarites.html)
     * classic：TF/IDF算法。请参阅Lucene的实用评分函数。
     * boolean：一种简单的布尔相似性，在不需要全文排名时使用，分数只应基于查询条件是否匹配而使用。布尔相似度给出了一个等于他们的查询提升的分数。
     * @return
     */
    String similarity() default "";

    /**
     * true/false 通常查询是对编入的索引进行查询，不必从_source字段中提取这些字段可以设置为 false。
     * @return
     */
    boolean store() default true;

    /**
     * bject、nested字段里面定义字段，一般用于List或Map这种弱类型字段定义
     * @return
     */
    String properties() default "";

    /**
     * term_vector ， 定义术语，用于特定文档搜索
     * no：没有任何术语向量被存储。（默认）
     * yes：只是在该领域的条款存储。
     * with_positions：条款和职位被存储。
     * with_offsets：条款和字符偏移被存储。
     * with_positions_offsets：术语，位置和字符偏移被存储。
     * @return
     */
    String term_vector() default "";
}
