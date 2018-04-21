# Elasticsearch面向对象封装之注解分析

## 前情提要

前一段时间封装过关系型数据库，发现出奇的好用，最近想尝试对Elasticsearch做一个面向对象封装。与ES这种文档存储系统不同，多数关系型数据库类型属性单一、结果扁平。而ES存储的文档结构立体，层次通常深度大于1，还包含了索引、评分系统的的建立，因而他的文档数据收集确显得比较复杂。但是幸好ES给了一系列默认的配置，即使我们不定义任何结构也能实现普通关系型数据库那样的存储和查询功能，因为json格式的数据天生就包含了存储结构，但是要凸显ES的性能和扩展开发，需要对存储结构做一个更精细的定义及收集，特别是如果要支持自动建立索引这种功能，对Mapping的支持要详细而且完整。前篇Sqlite的封装已经说过，自定义注解是悄无声息收集存储结构数据的完美姿势，当然无注解也会有默认配置，下面我根据通用套路去定义一些注解。

## 实体类注解

### @EsDoc({...})

1. 定义 Index、Type，为空的话，实体类中元字段 ```_index```、```_type```要定义

2. 扩展定义 动态Index生成规则、动态Type生成规则

### @EsDynamicMapping({...})

1. 动态定义Mapping属性模板

2. 主要属性

  - 动态参数
    * ```date_detection```：```true/false```，日期检测
    * ```dynamic_date_formats```：日期格式定义，例如 ```["MM/dd/yyyy"]```
    * ```numeric_detection```：```true/false```，数字检测

  - 动态模板
    * ```dynamic_templates```，存json对象数组或json对象数组对应的文件路径。例如：

    ```java
    [
      {
        "named_analyzers": {
          "match_mapping_type": "string",
          "match": "*",
          "mapping": {
            "type": "text",
            "analyzer": "{name}"
          }
        }
      },
      {
        "no_doc_values": {
          "match_mapping_type":"*",
          "mapping": {
            "type": "{dynamic_type}",
            "doc_values": false
          }
        }
      }
    ]
    ```

### @EsMappingFile(...)

1. 定义 value ，Mapping的json文件路径，自定义Mapping方式之一

2. 直接读取Mapping结构构建实体类对应的doc存储结构

3. 适用于复制ES库已有的映射结构导入 ， 简化开发

## 实体类属性注解

### @EsFieldsJson(...)

1. 定义 value ， Mapping 中对应 每个property 的 定义 Json 字符串，自定义Mapping方式之一

2. 适用于专业ES开发人士，可以对每个doc节点精确定义属性，达到更好的效果

### @EsFields({...})

1. 自定义Mapping方式之一，粒度细化定义

2. 定义 type ， 默认 string

  - 简单域类型
    * 字符串: ```string```, ```text```, ```keyword```
    * 整数 : ```byte```, ```short```, ```integer```, ```long```, ```alf_float```，```scaled_float```
    * 浮点数: ```float```, ```double```
    * 布尔型: ```boolean```
    * 日期: ```date```

  - 复杂域类型
    * 数组：```array```, 普通数据数组
    * 对象：```object```, json格式对象
    * 对象数组：```nested```, json对象数组

  - 其他数据类型
    * 经纬度点类型：```geo_point```
    * 地理形状类型：```geo_shape```
    * IP数据类型：```ip```, 用于IPv4和IPv6地址
    * 完成数据类型：```completion``` 提供自动完成的建议
    * 令牌计数数据类型：```token_count``` 计算字符串中的令牌数量
    * mapper-murmur3：```murmur3``` 在索引时计算值的哈希值并将它们存储在索引中
    * 渗滤器类型：```percolator```, 接受来自```query-dsl```的查询
    * [```join``` 数据类型](https://www.elastic.co/guide/en/elasticsearch/reference/current/parent-join.html)：```join```, 为相同索引内的文档定义父/子关系

3. 额外属性定义

  - ```index``` ，定义索引类型 ， 空则不定义，默认 ```analyzed```
    * ```analyzed``` 首先分析字符串，然后索引它。换句话说，以全文索引这个域。```@字符串类型专用```
    * ```not_analyzed``` 索引这个域，所以它能够被搜索，但索引的是精确值。不会对它进行分析。```@字符串类型专用```
    * ```no``` 不索引这个域。这个域不会被搜索到。```@字符串类型专用```
    * ```true/false``` 是否编入索引

  - ```analyzer``` ，分析器语言类型 ， 空则不定义，默认 ```standard```
    * 内含的还有```whitespace``` 、 ```simple``` 、 ```english```
    * 自定义请参考API

  - ```format``` ， 一般用于日期用字符串格式存储，定义日期格式

  - ```normalizer``` ， 空则不定义，跟随elasticsearch默认值

  - ```boost``` ， 相关性查询权重设置，默认```1.0```

  - ```coerce``` ， 设置为```true```强制将数字型数据转化为对应类型，而避免出现字符串或定义为整形文档存储为浮点型

  - ```copy_to``` ，定义的组字段名， 将多个字段的值复制到组字段中，然后可以将其作为单个字段进行查询

  - ```doc_values``` ， 默认```true```，false则查询的结果集中没有该字段

  - ```dynamic``` ， 默认```true```，多用于```object```类型字段
    * ```true```：新检测到的字段被添加到映射中。（默认）
    * ```false```：新检测的字段被忽略。这些字段不会被编入索引，因此不会被搜索到，但仍会出现在```_source```返回的匹配字段中。这些字段不会添加到映射中，必须明确添加新字段。
    * ```strict```：如果检测到新字段，则抛出异常并拒绝该文档。必须将新字段明确添加到映射中。

  - ```enabled``` ， 默认```true```，用于开启关闭某些字段的索引建立，```false```则不做查询字段

  - ```eager_global_ordinals``` ，提高聚合查询速度，聚合字段可以设置为```true```

  - ```fielddata``` ， 空则不定义，跟随elasticsearch默认值

  - ```ignore_above``` ， 属性值为整数，针对字符类型数据，长度比设定值长的将不被索引和存储

  - ```ignore_malformed``` ， 定义为```true```的话，就算类型不对也不影响存储，默认为```false```

  - ```index_options``` ， 被编入索引的项目定义
    * ```docs```：只有文档编号被索引。
    * ```freqs```：文件编号和术语频率被编入索引。
    * ```positions```：文档编号，术语频率和术语位置（或顺序）被编入索引。
    * ```offsets```：文档编号，术语频率，位置以及开始和结束字符偏移（将术语映射回原始字符串）进行索引。

  - ```fields``` ， 多领域定义，详情查看API

  - ```norms``` ， 用于开启或禁用规范，用于查询评分，详情见API

  - ```null_value``` ， 当传入```null```时候，用该属性的值替代```null```存储

  - ```position_increment_gap``` ， 值为数字，定义匹配间隙，越大越模糊

  - ```search_analyzer``` ， 定义搜索时的分析器，用于覆盖默认建立索引的分析器，通常不设定

  - ```similarity``` ， 定义评分算法
    * ```BM25```：```Okapi BM25```算法。请参阅[]可插入相似性算法。](https://www.elastic.co/guide/en/elasticsearch/guide/master/pluggable-similarites.html)
    * ```classic```：```TF/IDF```算法。请参阅[Lucene的实用评分函数](https://www.elastic.co/guide/en/elasticsearch/guide/master/practical-scoring-function.html)。
    * ```boolean```：一种简单的布尔相似性，在不需要全文排名时使用，分数只应基于查询条件是否匹配而使用。布尔相似度给出了一个等于他们的查询提升的分数。

  - ```store``` ，```true/false``` 通常查询是对编入的索引进行查询，不必从```_source```字段中提取这些字段k可以设置为 ```false```。

  - ```term_vector``` ， 定义术语，用于特定文档搜索
    * ```no```：没有任何术语向量被存储。（默认）
    * ```yes```：只是在该领域的条款存储。
    * ```with_positions```：条款和职位被存储。
    * ```with_offsets```：条款和字符偏移被存储。
    * ```with_positions_offsets```：术语，位置和字符偏移被存储。

  - ```properties``` ， ```object```、```nested```字段里面定义字段，一般用于```List```或```Map```这种弱类型字段定义

4. 元字段定义的属性

  类中定义元字段，而这些元字段不属于文档本身，但是可以丰富扩展ES的功能，只有如下的元字段才可以定义的属性一般为```enabled```，启用或禁用。

  - 身份元字段编辑
    * ```_index```：文档所属的索引，可以扩展为**相同数据类型的分索引存储**，垂直分储
    * ```_uid```：由```_type```和和组成的复合字段```_id```
    * ```_type```：文档的映射类型，可以扩展为**相同数据类型的分类型存储**，水平分储
    * ```_id```：文件的ID

  - 文档源元数据字段编辑
    * ```_source```：代表文档正文的原始JSON，禁用该字段会失去很多功能，不建议，如有需要请参考API
      + ```enabled```：```true```、```false```两种
    * ```_size```：```_source```字段 的大小，由```mapper-size```插件提供

  - 索引元字段编辑
    * ```_all```：一个包罗万象的字段，索引的所有其他字段的值，默认情况下禁用，启用请定义该元字段并配置属性```enabled```为```true```。
      + ```enabled```: ```true```、```false```两种
    * ```_field_names```：文档中包含非空值的所有字段，如果要优化索引速度并且不需要```exists```查询，则可能需要禁用此字段
      + ```enabled```: ```true```、```false```两种

  - 路由元字段编辑
    * ```_routing```：将文档路由到特定分片的自定义路由值

  - 其他元字段编辑
    * ```_meta```：特定于应用的元数，自定义属性

## 服务类方法注解

### @EsQuerySql({...})

1. 对SQL语句的支持，通过SQL转化器，转化为ES查询体查询

2. 定义 ```sql```、```params```，实现自定义SQL转ES查询

### @EsQueryParams(...)

1. 定义 ```value```，Params字符串方式自定义查询，占位符用 ```#param#``` 或 ```#1#```，param对应实体类字段名或普通参数传入顺序

### @EsQueryJson(...)

1. 定义 ```value```，Json请求体方式自定义查询，占用符用 ```#param#``` 或 ```#1#```，param对应实体类字段名或普通参数传入顺序
