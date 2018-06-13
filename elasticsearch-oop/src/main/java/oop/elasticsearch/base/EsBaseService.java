package oop.elasticsearch.base;

import oop.elasticsearch.config.ElasticsearchConfig;
import oop.elasticsearch.utils.EsJsonUtils;
import oop.elasticsearch.utils.EsLogUtils;
import oop.elasticsearch.utils.EsMappingHelper;
import oop.elasticsearch.utils.EsUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 自定义elasticsearch连接和操作基础服务
 *
 * @author 欧阳洁
 */
public class EsBaseService<T extends EsBaseEntity> {
    /**
     * 设置默认索引分片个数
     */
    public static final int NUMBER_OF_SHARDS = 5;
    /**
     * 设置默认索引副本个数，默认为1个副本
     */
    public static final int NUMBER_OF_REPLICAS = 1;
    /**
     * 设置最大查询条数
     */
    public static final int MAX_RESULT_WINDOW = 50000000;
    /**
     * 服务
     */
    private TransportClient client;
    /**
     * 当前 Index
     */
    private String index;
    /**
     * 当前 Type
     */
    private String type;
    /**
     * 当前 Mapping
     */
    private String mapping;
    /**
     * 实体类对应的类
     */
    private Class<T> targetClass;
    /**
     * id 属性
     */
    private Field idField;

    /**
     * 继承时候使用的构造函数
     */
    public EsBaseService(Class<T> targetClass) {
        this.targetClass = targetClass;
        this.client = ElasticsearchConfig.getClient();
        EsMappingHelper mappingHelper = new EsMappingHelper(targetClass);
        this.index = mappingHelper.getIndex();
        this.type = mappingHelper.getType();
        this.mapping = mappingHelper.getMappingStr();
        idField = mappingHelper.getIdField();
        this.createIndex(this.index, this.type, mapping);
    }

    /**
     * 关闭client
     */
    public void closeClient() {
        client.close();
    }

    /**
     * 获取连接
     *
     * @return
     */
    public TransportClient getClient() {
        return client;
    }

    /**
     * 设置连接，继承这个类必须要实现
     *
     * @param client
     */
    public void setClient(TransportClient client) {
        this.client = client;
    }

    /**
     * 获取索引管理
     *
     * @return 索引管理对象
     */
    public IndicesAdminClient getIndicesAdminClient() {
        return client.admin().indices();
    }

    /**
     * @return d
     */
    public MultiSearchRequestBuilder multiSearchRequest() {
        return client.prepareMultiSearch();
    }

    /**
     * @param scrollId s
     * @return d
     */
    public SearchScrollRequestBuilder searchScrollRequest(String scrollId) {
        return client.prepareSearchScroll(scrollId);
    }

    /**
     * 通过ID查询 IndexResponse
     *
     * @param _id
     * @return
     */
    public IndexResponse insert(String _id, String source) {
        EsLogUtils.info("执行新增 {}/{}?{}", this.index, this.type, source);
        return this.indexRequest().setId(_id).setSource(source, XContentType.JSON).execute().actionGet();
    }

    /**
     * 通过ID插入 IndexResponse
     *
     * @param _id
     * @return
     */
    public boolean insert(String _id, T entity) {
        String json = EsJsonUtils.toJSONString(entity);
        IndexResponse response = this.insert(_id, json);
        if (null != response) {
            RestStatus restStatus = response.status();
            return RestStatus.CREATED.equals(restStatus) || RestStatus.OK.equals(restStatus);
        }
        return false;
    }

    /**
     * 通过ID插入 IndexResponse
     *
     * @return
     */
    public boolean insert(T entity) {
        String id = EsUtils.toString(readIdField(entity));
        if (EsUtils.isBlank(id)) {
            id = UUID.randomUUID().toString();
        }
        return this.insert(id, entity);
    }

    /**
     * @return d
     */
    public IndexRequestBuilder indexRequest() {
        return this.indexRequest(this.index, this.type);
    }

    /**
     * @param id d
     * @return d
     */
    public IndexRequestBuilder indexRequest(String id) {
        return this.indexRequest(this.index, this.type, id);
    }

    /**
     * @param index d
     * @param type  d
     * @return d
     */
    public IndexRequestBuilder indexRequest(String index, String type) {
        return client.prepareIndex(index, type);
    }

    /**
     * @param index d
     * @param type  d
     * @param id    d
     * @return d
     */
    public IndexRequestBuilder indexRequest(String index, String type, String id) {
        return client.prepareIndex(index, type, id);
    }

    /**
     * @param id d
     * @return d
     */
    public UpdateRequestBuilder updateRequest(String id) {
        return this.updateRequest(this.index, this.type, id);
    }

    /**
     * @param index d
     * @param type  d
     * @param id    d
     * @return d
     */
    public UpdateRequestBuilder updateRequest(String index, String type, String id) {
        return client.prepareUpdate(index, type, id);
    }

    /**
     * 通过ID查询 IndexResponse
     *
     * @param _id
     * @return
     */
    public UpdateResponse upsert(String _id, String source) {
        EsLogUtils.info("执行增改 {}/{}?{}", this.index, this.type, source);
        return this.updateRequest(_id).setDoc(source, XContentType.JSON).setUpsert(source, XContentType.JSON).execute().actionGet();
    }

    /**
     * 通过ID插入 IndexResponse
     *
     * @param _id
     * @return
     */
    public boolean upsert(String _id, T entity) {
        String json = EsJsonUtils.toJSONString(entity);
        UpdateResponse response = this.upsert(_id, json);
        if (null != response) {
            RestStatus restStatus = response.status();
            return RestStatus.CREATED.equals(restStatus) || RestStatus.OK.equals(restStatus);
        }
        return false;
    }

    /**
     * 通过ID插入 IndexResponse
     *
     * @return
     */
    public boolean upsert(T entity) {
        String id = EsUtils.toString(readIdField(entity));
        if (EsUtils.isBlank(id)) {
            id = UUID.randomUUID().toString();
        }
        return this.upsert(id, entity);
    }

    /**
     * @return d
     */
    public SearchRequestBuilder searchRequest() {
        return this.searchRequest(this.index, this.type);
    }

    /**
     * @param index d
     * @param type  d
     * @return d
     */
    public SearchRequestBuilder searchRequest(String index, String type) {
        return client.prepareSearch(index).setTypes(type);
    }

    /**
     * mget多条件关联查询
     *
     * @param extraIndex d
     * @param extraType  d
     * @param id         关联id
     * @return d
     */
    public MultiGetResponse searchRequestMulti(String extraIndex, String extraType, String id) {
        return this.searchRequestMulti(this.index, this.type, extraIndex, extraType, id);
    }

    /**
     * mget多条件关联查询
     *
     * @param index  d
     * @param type   d
     * @param index2 d
     * @param type2  d
     * @param id     关联id
     * @return d
     */
    public MultiGetResponse searchRequestMulti(String index, String type, String index2, String type2, String id) {
        return client.prepareMultiGet().add(index, type, id).add(index2, type2, id).get();
    }

    /**
     * @param id 需要删除对象ID
     * @return 删除
     */
    public DeleteRequestBuilder deleteRequest(String id) {
        return this.deleteRequest(this.index, this.type, id);
    }

    /**
     * @param index 索引名称
     * @param type  索引类型
     * @param id    需要删除对象ID
     * @return 删除
     */
    public DeleteRequestBuilder deleteRequest(String index, String type, String id) {
        return client.prepareDelete(index, type, id);
    }

    public boolean deleteById(String _id) {
        EsLogUtils.info("执行删除 {}/{}/{}", this.index, this.type, _id);
        DeleteResponse response = this.deleteRequest(_id).execute().actionGet();
        if (null != response) {
            RestStatus restStatus = response.status();
            return RestStatus.OK.equals(restStatus);
        }
        return false;
    }

    /**
     * Map方式写入Es
     *
     * @param id 需要插入的对象ID
     * @param o  需要插入的对象
     * @return 返回插入结果
     */
    public BulkResponse bulkInsert(String id, T o) {
        return this.bulkInsert(this.index, this.type, id, o);
    }

    /**
     * Map方式写入Es
     *
     * @param index 索引
     * @param type  索引类型
     * @param id    需要插入的对象ID
     * @param o     需要插入的对象
     * @return 返回插入结果
     */
    public BulkResponse bulkInsert(String index, String type, String id, Object o) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        bulkRequest.add(bulkInsertAdd(index, type, id, o));
        return bulkRequest.execute().actionGet();
    }

    /**
     * 返回一个Map类型值的索引对象
     *
     * @param index 索引
     * @param type  索引类型
     * @param id    需要插入的对象ID
     * @param o     需要插入的对象
     * @return 返回索引插入对象
     */
    public IndexRequestBuilder bulkInsertAdd(String index, String type, String id, Object o) {
        if (o instanceof Map) {
            return indexRequest(index, type, id).setSource(o);
        } else {
            String json = "";
            if (o instanceof String) {
                json = (String) o;
            } else {
                json = EsJsonUtils.toJSONString(o);
            }
            return indexRequest(index, type, id).setSource(json, XContentType.JSON);
        }
    }

    /**
     * @param index  d
     * @param filter d
     * @return d
     */
    public long deleteByQuery(String index, QueryBuilder filter) {
        if (filter == null) {
            return 0L;
        }
        DeleteByQueryAction action = DeleteByQueryAction.INSTANCE;
        DeleteByQueryRequestBuilder delete = action.newRequestBuilder(client);
        delete.source(index).filter(filter);
        BulkByScrollResponse response = delete.get();
        return response.getDeleted();
    }

    /**
     * @param index  index
     * @param filter filter
     * @param script ctx._source['XXX'] = 'XXX'
     * @return d
     */
    public long updateByQuery(String index, QueryBuilder filter, String script) {
        if (filter == null) {
            return 0L;
        }
        Script scr = new Script(script);
        UpdateByQueryAction action = UpdateByQueryAction.INSTANCE;
        UpdateByQueryRequestBuilder update = action.newRequestBuilder(client);
        update.source(index).filter(filter).script(scr);
        BulkByScrollResponse response = update.get();
        return response.getUpdated();
    }


    /**
     * 根据id查询
     *
     * @param id 类型
     * @return 返回查询得到的
     */
    public T searchById(String id) {
        String json = this.searchById(this.index, this.type, id);
        T result = EsJsonUtils.getObject(json, this.targetClass);
        return result;
    }

    /**
     * 根据id查询
     *
     * @param id    主键id
     * @param index 索引
     * @param type  类型
     * @return 返回查询得到的
     */
    public String searchById(String index, String type, String id) {
        String jsonString = null;
        try {
            GetResponse response = client.prepareGet()
                    .setId(id)
                    .setIndex(index)
                    .setType(type)
                    .execute()
                    .actionGet();
            jsonString = response.getSourceAsString();
        } catch (Exception e) {
            EsLogUtils.error("{},ES查询报错:索引:{};id:{};{}", type, index, id, e.getMessage());
            return jsonString;
        }
        return jsonString;
    }


    /**
     * 条件分页查询，使用sql语法
     *
     * @param searchParam
     * @return 分页对象
     */
    public EsBasePage<T> findEsBasePage(EsBaseEntity searchParam) {
        searchParam.setIndex(this.index);
        searchParam.setType(this.index);
        return this.findEsBasePage(searchParam, this.targetClass);
    }

    /**
     * 条件分页查询，使用sql语法
     *
     * @param searchParam 查询条件
     * @param clazz       返回对象类型
     * @param <E>         对象类型
     * @return 分页对象信息
     */
    public <E> EsBasePage<E> findEsBasePage(EsBaseEntity searchParam, Class<E> clazz) {
        if (searchParam == null) {
            return null;
        }
        //创建分页对象
        EsBasePage<E> page = new EsBasePage<E>(searchParam.getCurrent(), searchParam.getSize());

        //创建查询对象
        SearchRequestBuilder search = searchRequest(searchParam.getIndex(), searchParam.getType());//索引名称
        search.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);//查询方式（共4种查询方式）
        //指定需要返回或者不返回的字段名称
        FetchSourceContext sourceContext = new FetchSourceContext(true, searchParam.getIncludes(), searchParam.getExcludes());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(sourceContext);
        search.setSource(searchSourceBuilder);
        //设置查询分页条件
        search.setFrom(searchParam.getOffsetCurrent()).setSize(searchParam.getSize()).setExplain(true);
        //设置查询条件
        if (EsUtils.isNotEmpty(searchParam.getSearchParam())) {
            search.setQuery(QueryBuilders.queryStringQuery(searchParam.getSearchParam()));
        }


        final int two = 2;
        //设置排序方式
        if (EsUtils.isNotEmpty(searchParam.getOrderByField())) {
            String[] sortArr = searchParam.getOrderByField().split(",");
            for (String sortStr : sortArr) {
                String[] sort = sortStr.trim().split(" ");
                if (sort.length == two) {
                    if (EsUtils.isNotEmpty(sort[0]) && EsUtils.isNotEmpty(sort[1])) {
                        if ("desc".equalsIgnoreCase(sort[1])) {
                            search.addSort(SortBuilders.fieldSort(sort[0]).order(SortOrder.DESC).unmappedType("long"));
                            // search.addSort(sort[0], SortOrder.DESC);
                        } else if ("asc".equalsIgnoreCase(sort[1])) {
                            // search.addSort(sort[0], SortOrder.ASC);
                            search.addSort(SortBuilders.fieldSort(sort[0]).order(SortOrder.ASC).unmappedType("long"));
                        }
                    }
                }
            }
        }
        //执行查询，获取查询结果
        SearchResponse response = search.execute().actionGet();
        SearchHits hits = response.getHits();
        //获取查询数据的总条数
        page.setTotal((int) hits.getTotalHits());
        //循环解析返回的数据
        List<E> list = new ArrayList<E>();
        if (null != hits && hits.getHits().length > 0) {
            for (SearchHit hit : hits) {
                list.add(EsJsonUtils.getObject(hit.getSourceAsString(), clazz));
            }
        }
        page.setRecords(list);
        return page;
    }

    /**
     * 通过ID查找到对象
     *
     * @param _id
     * @return
     */
    public T getById(String _id) {
        SearchRequestBuilder srb = this.searchRequest();
        srb.setQuery(QueryBuilders.termQuery("_id", _id));
        try {
            SearchResponse response = srb.execute().actionGet();
            if (null == response) {
                EsLogUtils.info("查询异常，未接收到返回！");
                return null;
            }
            SearchHits searchHits = response.getHits();
            if (null != searchHits && searchHits.getTotalHits() > 0) {
                SearchHit searchHit = searchHits.getHits()[0];
                String jsonStr = searchHit.getSourceAsString();
                T result = EsJsonUtils.getObject(jsonStr, this.targetClass);
                return result;
            }
        } catch (Exception e) {
            EsLogUtils.info("查询异常！", e);
        }
        return null;
    }

    /**
     * 通过ID查找到对象
     *
     * @param entity
     * @return
     */
    public List<T> query(T entity) {
        EsLogUtils.info("执行查询 {}/{} 开始！", this.index, this.type);
        SearchRequestBuilder srb = this.searchRequest();
        srb.setSize(entity.getSize());
        srb.setFrom(entity.getOffsetCurrent());
        //设置查询条件
        if (EsUtils.isNotEmpty(entity.getSearchParam())) {
            // Get 参数查询方式
            EsLogUtils.info("Get 参数查询方式 参数为 {}", entity.getSearchParam());
            srb.setQuery(QueryBuilders.queryStringQuery(entity.getSearchParam()));
        } else {
            // 将 searchBody 转化为 QueryBuilder 对象
        }
        List<T> result = new ArrayList<T>();
        try {
            SearchResponse response = srb.execute().actionGet();
            if (null == response) {
                EsLogUtils.info("查询异常，未接收到返回！");
                return null;
            }
            SearchHits searchHits = response.getHits();
            if (null != searchHits && searchHits.getTotalHits() > 0) {
                for (SearchHit searchHit : searchHits.getHits()) {
                    String jsonStr = searchHit.getSourceAsString();
                    EsLogUtils.info("查询到： {}", jsonStr);
                    T item = EsJsonUtils.getObject(jsonStr, this.targetClass);
                    result.add(item);
                }
            }
        } catch (Exception e) {
            EsLogUtils.info("查询异常！", e);
        }
        EsLogUtils.info("执行查询 {}/{} 结束！", this.index, this.type);
        return result;
    }


    /**
     * 条件查询，使用 请求参数语法
     * 返回符合条件的全部数据使用prepareSearchScroll游标查询比设置from查询速度要快
     *
     * @param searchParam 查询条件
     * @return List对象集合信息
     */
    public List<T> findList(EsBaseEntity searchParam) {
        searchParam.setIndex(this.index);
        searchParam.setType(this.index);
        return this.findList(searchParam, this.targetClass);
    }

    /**
     * 条件查询，使用 请求参数语法
     * 返回符合条件的全部数据使用prepareSearchScroll游标查询比设置from查询速度要快
     *
     * @param searchParam 查询条件
     * @param clazz       返回对象类型
     * @param <E>         对象类型
     * @return List对象集合信息
     */
    public <E> List<E> findList(EsBaseEntity searchParam, Class<E> clazz) {
        if (searchParam == null) {
            return null;
        }
        //设置游标一次返回数据量
        final int size = 1000;
        //游标维持时间(秒)
        final int scrollSeconds = 6;
        //创建返回对象
        List<E> list = new ArrayList<E>();
        //创建查询对象
        SearchRequestBuilder search = searchRequest(searchParam.getIndex(), searchParam.getType());//索引名称
        search.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);//查询方式（共4种查询方式）
        //指定需要返回或者不返回的字段名称
        FetchSourceContext sourceContext = new FetchSourceContext(true, searchParam.getIncludes(), searchParam.getExcludes());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(sourceContext);
        search.setSource(searchSourceBuilder);
        //设置游标每次返回数据量(实际返回的数量为size*index的主分片格式)
        search.setSize(size);
        //设置这个游标维持多长时间
        search.setScroll(TimeValue.timeValueSeconds(scrollSeconds));
        //设置查询条件
        if (EsUtils.isNotEmpty(searchParam.getSearchParam())) {
            search.setQuery(QueryBuilders.queryStringQuery(searchParam.getSearchParam()));
        }

        final int two = 2;
        //设置排序方式
        if (EsUtils.isNotEmpty(searchParam.getOrderByField())) {
            String[] sortArr = searchParam.getOrderByField().split(",");
            for (String sortStr : sortArr) {
                String[] sort = sortStr.trim().split(" ");
                if (sort.length == two) {
                    if (EsUtils.isNotEmpty(sort[0]) && EsUtils.isNotEmpty(sort[1])) {
                        if ("desc".equalsIgnoreCase(sort[1])) {
                            search.addSort(sort[0], SortOrder.DESC);
                        } else if ("asc".equalsIgnoreCase(sort[1])) {
                            search.addSort(sort[0], SortOrder.ASC);
                        }
                    }
                }
            }
        }

        SearchResponse scrollResponse = null;
        //总条数
        Long count = 0L;
        //当前游标已经查询出的数量
        int sum = 0;
        do {
            if (count.intValue() == 0) {
                scrollResponse = search.execute().actionGet();
                count = scrollResponse.getHits().getTotalHits();
            } else {
                scrollResponse = client.prepareSearchScroll(scrollResponse.getScrollId())
                        .setScroll(TimeValue.timeValueSeconds(scrollSeconds))
                        .execute().actionGet();
            }
            SearchHits hits = scrollResponse.getHits();
            sum += hits.getHits().length;
            if (null != hits && hits.getHits().length > 0) {
                for (SearchHit hit : hits) {
                    String source = hit.getSourceAsString();
                    EsLogUtils.info("查询到： {}", source);
                    list.add(EsJsonUtils.getObject(source, clazz));
                }
            }
        } while (sum < count);
        return list;
    }


    /**
     * 判断索引是否存在
     *
     * @param index 索引名称
     * @return true 索引存在  false索引不存在
     */
    public synchronized boolean indexExists(String index) {
        IndicesExistsRequest request = new IndicesExistsRequest(index);
        IndicesExistsResponse response = getIndicesAdminClient().exists(request).actionGet();
        if (response != null && response.isExists()) {
            EsLogUtils.info("{} 索引存在！", index);
            return true;
        }
        EsLogUtils.info("{} 索引不存在！", index);
        return false;
    }

    /**
     * 判断指定的索引的类型是否存在
     *
     * @param indexName 索引名
     * @param indexType 索引类型
     * @return 存在：true; 不存在：false;
     */
    public boolean isExistsType(String indexName, String indexType) {
        TypesExistsRequest request = new TypesExistsRequest(new String[]{indexName}, indexType);
        TypesExistsResponse response = getIndicesAdminClient().typesExists(request).actionGet();
        if (response != null) {
            EsLogUtils.info("在 {} 索引中 {} 存在！", indexName, indexType);
            return response.isExists();
        }
        EsLogUtils.info("在 {} 索引中 {} 不存在！", indexName, indexType);
        return false;
    }

    /**
     * 创建索引
     *
     * @param index           索引 数据库
     * @param type            表
     * @param xContentBuilder mapping
     */
    public synchronized boolean createIndex(String index, String type, XContentBuilder xContentBuilder) {
        if (indexExists(index) && isExistsType(index, type)) {
            return true;
        }
        IndicesAdminClient adminClient = getIndicesAdminClient();
        CreateIndexResponse response = adminClient.prepareCreate(index)
                .setSettings(getSettings())
                .addMapping(type, xContentBuilder).execute().actionGet();
        if (null != response) {
            return response.isAcknowledged();
        }
        return false;
    }

    /**
     * 创建索引
     *
     * @param index  索引 数据库
     * @param type   表
     * @param source mapping
     */
    public synchronized boolean createIndex(String index, String type, String source) {
        if (indexExists(index) && isExistsType(index, type)) {
            return true;
        }
        IndicesAdminClient adminClient = getIndicesAdminClient();
        EsLogUtils.info("开始创建索引{}的{}类型！", index, type);
        CreateIndexResponse response = adminClient.prepareCreate(index)
                .setSettings(getSettings())
                .addMapping(type, source, XContentFactory.xContentType(source)).execute().actionGet();
        if (null != response) {
            EsLogUtils.info("创建成功！");
            return response.isAcknowledged();
        }
        EsLogUtils.info("创建失败！");
        return false;
    }

    /**
     * 读取属性的值
     *
     * @param target
     * @return
     */
    protected Object readIdField(T target) {
        if (this.idField == null) {
            return null;
        }
        try {
            return EsUtils.readField(this.idField, target, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Settings.Builder getSettings() {
        return Settings.builder()
                .put("index.number_of_shards", NUMBER_OF_SHARDS)
                .put("index.number_of_replicas", NUMBER_OF_REPLICAS)
                .put("index.max_result_window", MAX_RESULT_WINDOW);
    }
}

