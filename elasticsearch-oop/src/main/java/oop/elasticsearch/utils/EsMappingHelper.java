package oop.elasticsearch.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import oop.elasticsearch.annotation.EsDoc;
import oop.elasticsearch.annotation.EsFields;
import oop.elasticsearch.annotation.EsFieldsJson;
import oop.elasticsearch.annotation.EsMappingFile;
import oop.elasticsearch.annotation.EsTransient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * es索引mapping工具类
 *
 * @author 欧阳洁
 * @since 2018-05-08 16:14
 */
public class EsMappingHelper {
    /**
     * 索引类型对应实体类
     */
    private Class<?> targetClass;
    /**
     * 索引名
     */
    private String index;
    /**
     * 索引类型名
     */
    private String type;
    /**
     * mapping 字符串
     */
    private String mappingStr;

    public void setMappingStr(String mappingStr) {
        this.mappingStr = mappingStr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * 构造函数
     *
     * @param targetClass
     */
    public EsMappingHelper(Class<?> targetClass) {
        this.targetClass = targetClass;
        readIndexAndType();
        this.mappingStr = getMappingStr();
    }

    /**
     * 获取index和type
     */
    private void readIndexAndType() {
        EsDoc esDoc = this.targetClass.getAnnotation(EsDoc.class);
        if (null != esDoc) {
            this.index = esDoc.Index();
            this.type = esDoc.Type();
        }
    }

    /**
     * 获取对应索引类型的Mapping映射Json字符串
     *
     * @return
     */
    public String getMappingStr() {
        String mappingStr = null;
        EsMappingFile mappingFile = targetClass.getAnnotation(EsMappingFile.class);
        if (null != mappingFile) {
            String pathForClasspath = mappingFile.value();
            InputStream inputStream = EsMappingHelper.class.getClassLoader().getResourceAsStream(pathForClasspath);
            mappingStr = EsUtils.convertStreamToStr(inputStream);
        } else {
            Field[] fieldArray = this.targetClass.getDeclaredFields();
            if (null == fieldArray || fieldArray.length == 0) {
                return mappingStr;
            }
            try {
                XContentBuilder mapping = XContentFactory.jsonBuilder().startObject().startObject(this.type);
                mapping.startObject("properties");
                for (Field field : fieldArray) {
                    mapping.startObject(field.getName());
                    EsTransient esTransient = field.getAnnotation(EsTransient.class);
                    if (null != esTransient) {
                        // EsTransient注解标记的属性即不存储也不做索引建立，无关字段
                        mapping.field("enabled", false).field("doc_values", false).field("store", false);
                    } else {
                        EsFieldsJson esfieldsJson = field.getAnnotation(EsFieldsJson.class);
                        EsFields esfields = field.getAnnotation(EsFields.class);
                        if (null != esfieldsJson) {
                            putFileds(mapping,esfieldsJson);
                        } else if (null != esfields) {
                            putFileds(mapping,esfields);
                        } else {
                            mapping.field("type", "keyword").field("store", true);
                        }
                    }
                    mapping.endObject();
                }
                mapping.endObject();
                mapping.endObject().endObject();
                mappingStr = mapping.string();
            } catch (IOException e) {
                return mappingStr;
            }
        }
        return mappingStr;
    }

    /**
     * 填充 EsFieldsJson 注解里面所有的Fields到对应属性里面
     * @param mapping
     * @param esFieldsJson
     * @throws IOException
     */
    private void putFileds(XContentBuilder mapping, EsFieldsJson esFieldsJson) throws IOException {
        JSONObject fields = JSON.parseObject(esFieldsJson.value());
        if (!fields.isEmpty()) {
            for (Map.Entry<String, Object> field : fields.entrySet()) {
                mapping.field(field.getKey(), field.getValue());
            }
        }
    }
    /**
     * 填充 EsFields 注解里面所有的Fields到对应属性里面
     * @param mapping
     * @param esfields
     * @throws IOException
     */
    private void putFileds(XContentBuilder mapping, EsFields esfields) throws IOException {
        //field类型
        mapping.field("type", esfields.type());

        //分析器类型
        if (!EsUtils.isBlank(esfields.analyzer())) {
            mapping.field("analyzer", esfields.analyzer());
        }

        //索引类型，仅用于string类型
        if (!EsUtils.isBlank(esfields.index())) {
            mapping.field("index", esfields.index());
        }
        //时间格式化成字符串格式
        if (!EsUtils.isBlank(esfields.format())) {
            mapping.field("format", esfields.format());
        }

        //额，这个是啥来着，请查看原生api文档
        if (!EsUtils.isBlank(esfields.normalizer())) {
            mapping.field("normalizer", esfields.normalizer());
        }

        //查询权重设置，默认1.0
        mapping.field("boost", esfields.boost());

        //是否强制将数字型数据转化为对应类型
        if (esfields.coerce()) {
            mapping.field("coerce", true);
        }

        //定义的组字段名， 将多个字段的值复制到组字段中，然后可以将其作为单个字段进行查询
        if (!EsUtils.isBlank(esfields.copy_to())) {
            mapping.field("copy_to", esfields.copy_to());
        }

        //默认true，false则查询的结果集中没有该字段
        if (!esfields.doc_values()) {
            mapping.field("doc_values", false);
        }

        /**
         * 默认true，多用于object类型字段
         * true：新检测到的字段被添加到映射中。（默认）
         * false：新检测的字段被忽略。这些字段不会被编入索引，因此不会被搜索到，但仍会出现在_source返回的匹配字段中。这些字段不会添加到映射中，必须明确添加新字段。
         * strict：如果检测到新字段，则抛出异常并拒绝该文档。必须将新字段明确添加到映射中。
         */
        if (!EsUtils.isBlank(esfields.dynamic())) {
            mapping.field("dynamic", esfields.dynamic());
        }

        //默认true，用于开启关闭某些字段的索引建立，false则不做查询字段
        if (!esfields.enabled()) {
            mapping.field("enabled", false);
        }

        //提高聚合查询速度，聚合字段可以设置为true
        if (esfields.eager_global_ordinals()) {
            mapping.field("enabled", true);
        }

        //聚合（分组）字段要设置为true
        if (esfields.fielddata()) {
            mapping.field("fielddata", true);
        }

        //属性值为整数，针对字符类型数据，长度比设定值长的将不被索引和存储
        if (esfields.ignore_above() != 0) {
            mapping.field("ignore_above", esfields.ignore_above());
        }

        //定义为true的话，就算类型不对也不影响存储，默认为false
        if (esfields.ignore_malformed()) {
            mapping.field("ignore_malformed", true);
        }

        /**
         * 被编入索引的项目定义
         * docs：只有文档编号被索引。
         * freqs：文件编号和术语频率被编入索引。
         * positions：文档编号，术语频率和术语位置（或顺序）被编入索引。
         * offsets：文档编号，术语频率，位置以及开始和结束字符偏移（将术语映射回原始字符串）进行索引。
         */
        if (!EsUtils.isBlank(esfields.index_options())) {
            mapping.field("index_options", esfields.index_options());
        }

        //多领域定义，详情查看API
        if (!EsUtils.isBlank(esfields.fields())) {
            mapping.field("fields", esfields.fields());
        }

        //当传入null时候，用该属性的值替代null存储
        if (!EsUtils.isBlank(esfields.null_value())) {
            mapping.field("null_value", esfields.null_value());
        }

        //值为数字，定义匹配间隙，越大越模糊
        if (esfields.ignore_above() != 0) {
            mapping.field("position_increment_gap", esfields.position_increment_gap());
        }

        //定义搜索时的分析器，用于覆盖默认建立索引的分析器，通常不设定
        if (!EsUtils.isBlank(esfields.search_analyzer())) {
            mapping.field("search_analyzer", esfields.search_analyzer());
        }

        /**
         * 定义评分算法
         * BM25：Okapi BM25算法。请参阅[]可插入相似性算法。](https://www.elastic.co/guide/en/elasticsearch/guide/master/pluggable-similarites.html)
         * classic：TF/IDF算法。请参阅Lucene的实用评分函数。
         * boolean：一种简单的布尔相似性，在不需要全文排名时使用，分数只应基于查询条件是否匹配而使用。布尔相似度给出了一个等于他们的查询提升的分数。
         */
        if (!EsUtils.isBlank(esfields.similarity())) {
            mapping.field("similarity", esfields.similarity());
        }

        //true/false 通常查询是对编入的索引进行查询，不必从_source字段中提取这些字段可以设置为 false。
        mapping.field("store", esfields.store());

        //bject、nested字段里面定义字段，一般用于List或Map这种弱类型字段定义
        if (!EsUtils.isBlank(esfields.properties())) {
            mapping.field("properties", esfields.properties());
        }

        /**
         * term_vector ， 定义术语，用于特定文档搜索
         * no：没有任何术语向量被存储。（默认）
         * yes：只是在该领域的条款存储。
         * with_positions：条款和职位被存储。
         * with_offsets：条款和字符偏移被存储。
         * with_positions_offsets：术语，位置和字符偏移被存储。
         */
        if (!EsUtils.isBlank(esfields.term_vector())) {
            mapping.field("term_vector", esfields.term_vector());
        }
    }
}
