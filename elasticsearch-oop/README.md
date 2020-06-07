# Elasticsearch面向对象封装

负责人：[petterobam](https://github.com/petterobam)

1. [x] 配置加载封装实现-可扩展
2. [x] 集群连接封装实现-可扩展
3. [x] 基础映射从对应实体类中读取
4. [x] 丰富注解，动态建立索引和对应mapping
5. [x] 实现基本的面向对象封装-增删查改
6. [ ] 实现分组查询面向对象封装
7. [ ] 实现SQL查询转义器，转义ES查询
8. [ ] 实现加权查询、模糊查询面向对象封装
9. [ ] 实现多集群ES处理
10. [ ] 想起来再说
 
# 使用说明

由于Elasticsearch版本更新太快，本封装目前使用 5.6.3 版本的 API，请下载对应版本的 Elasticsearch

```
<!-- https://mvnrepository.com/artifact/org.elasticsearch.client/transport -->
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>transport</artifactId>
    <version>5.6.3</version>
</dependency>
```

1. [Elasticsearch-5.6.3 下载](https://www.elastic.co/downloads/past-releases/elasticsearch-5-6-3)
2. [Kibana-5.6.3 下载](https://www.elastic.co/downloads/past-releases/kibana-5-6-3)
2. [elasticsearch-analysis-ik-5.6.3 中文分词器下载](https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v5.6.3/elasticsearch-analysis-ik-5.6.3.zip)

最近好像出了 [JDBC Client 6.3.0 版本](https://www.elastic.co/guide/en/elasticsearch/reference/6.3/index.html) ，支持 SQL ，但目前没有 Maven ，后面添加切换其他版本再分支里面处理