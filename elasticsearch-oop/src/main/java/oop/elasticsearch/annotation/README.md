# 注解分析

## 实体类注解

### EsDoc
1. 定义 Index、Type 必填
2. 扩展定义 动态Index生成规则、动态Type生成规则

### EsMappingFile
1. 定义 value ，Mapping的json文件路径，自定义Mapping方式之一
2. 直接读取Mapping结构构建实体类对应的doc存储结构

## 实体类属性注解

### EsFieldsJson
1. 定义 value ， Mapping 中对应 每个property 的 定义 Json 字符串，自定义Mapping方式之一

### EsFields
1. 定义 type , 必填 ， 默认 string
2. 额外属性定义

    - index ，是否添加索引 ， 空则不定义，跟随elasticsearch默认值
    - analyzer ，分析器语言类型 ， 空则不定义，跟随elasticsearch默认值
    - format ， 空则不定义，跟随elasticsearch默认值
    - normalizer ， 空则不定义，跟随elasticsearch默认值
    - boost ， 空则不定义，跟随elasticsearch默认值
    - coerce ， 空则不定义，跟随elasticsearch默认值
    - copy_to ， 空则不定义，跟随elasticsearch默认值
    - doc_values ， 空则不定义，跟随elasticsearch默认值
    - dynamic ， 空则不定义，跟随elasticsearch默认值
    - enabled ， 空则不定义，跟随elasticsearch默认值
    - eager_global_ordinals ， 空则不定义，跟随elasticsearch默认值
    - fielddata ， 空则不定义，跟随elasticsearch默认值
    - ignore_above ， 空则不定义，跟随elasticsearch默认值
    - ignore_malformed ， 空则不定义，跟随elasticsearch默认值
    - index_options ， 空则不定义，跟随elasticsearch默认值
    - fields ， 空则不定义，跟随elasticsearch默认值
    - norms ， 空则不定义，跟随elasticsearch默认值
    - null_value ， 空则不定义，跟随elasticsearch默认值
    - position_increment_gap ， 空则不定义，跟随elasticsearch默认值
    - search_analyzer ， 空则不定义，跟随elasticsearch默认值
    - similarity ， 空则不定义，跟随elasticsearch默认值
    - store ， 空则不定义，跟随elasticsearch默认值
    - term_vector ， 空则不定义，跟随elasticsearch默认值
    - properties ， 对象里面的对象属性定义 ， 待考虑

3.