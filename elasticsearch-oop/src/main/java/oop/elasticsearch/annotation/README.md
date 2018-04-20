# 注解分析

## 实体类注解

### EsDoc
1. 定义 Index、Type 必填
2. 扩展定义 动态Index生成规则、动态Type生成规则

### EsMappingFile
1. 定义 value ，Mapping的json文件路径，自定义Mapping方式之一
2. 直接读取Mapping结构构建实体类对应的doc存储结构
3. 适用于复制ES库已有的映射结构导入 ， 简化开发

## 实体类属性注解

### EsFieldsJson
1. 定义 value ， Mapping 中对应 每个property 的 定义 Json 字符串，自定义Mapping方式之一
2. 适用于专业ES开发人士，可以对每个doc节点精确定义属性，达到更好的效果

### EsFields

1. 自定义Mapping方式之一
2. 定义 type ， 默认 string

  - 简单域类型
    * 字符串: string
    * 整数 : byte, short, integer, long
    * 浮点数: float, double
    * 布尔型: boolean
    * 日期: date

3. 额外属性定义

  - index ，定义索引类型 ， 空则不定义，默认 analyzed
    * analyzed 首先分析字符串，然后索引它。换句话说，以全文索引这个域。
    * not_analyzed 索引这个域，所以它能够被搜索，但索引的是精确值。不会对它进行分析。
    * no 不索引这个域。这个域不会被搜索到。
  - analyzer ，分析器语言类型 ， 空则不定义，默认 standard
    * whitespace 、 simple 、 english
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
