package oop.elasticsearch.base;

import oop.elasticsearch.utils.EsJsonMapper;
import oop.elasticsearch.utils.EsLogUtils;
import oop.elasticsearch.utils.EsUtils;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.ClearScrollRequestBuilder;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自定义elasticsearch连接和操作基础服务
 *
 * @author 欧阳洁
 */
public abstract class EsBaseService {
    /**
     * 获取连接
     * @return
     */
    public TransportClient getClient() {
        return client;
    }

    /**
     * 设置连接
     * @param client
     */
    public void setClient(TransportClient client) {
        this.client = client;
    }

    /**
     * 服务
     */
    private TransportClient client;


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
    public BulkRequestBuilder bulkRequest() {
        return client.prepareBulk();
    }

    /**
     * @return d
     */
    public GetRequestBuilder getRequest() {
        return client.prepareGet();
    }

    /**
     * @return d
     */
    public MultiGetRequestBuilder multiGetRequest() {
        return client.prepareMultiGet();
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
     * @return d
     */
    public ClearScrollRequestBuilder clearScrollRequest() {
        return client.prepareClearScroll();
    }

    /**
     * @return d
     */
    public IndexRequestBuilder indexRequest() {
        return client.prepareIndex();
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
     * @return d
     */
    public UpdateRequestBuilder updateRequest() {
        return client.prepareUpdate();
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
     * @return d
     */
    public SearchRequestBuilder searchRequest() {
        return client.prepareSearch();
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
     * @param index 索引名称
     * @param type  索引类型
     * @param id    需要删除对象ID
     * @return 删除
     */
    public DeleteRequestBuilder deleteRequest(String index, String type, String id) {
        return client.prepareDelete(index, type, id);
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
        BulkRequestBuilder bulkRequest = bulkRequest();
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
        Map map = null;
        if (o instanceof Map) {
            return indexRequest(index, type, id).setSource(map);
        } else {
            String json = "";
            if (o instanceof String) {
                json = (String) o;
            } else {
                json = EsJsonMapper.nonEmptyMapper().toJson(o);
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
     * @param id    主键id
     * @param index 索引
     * @param type  类型
     * @return 返回查询得到的
     */
    public String searchById(String id, String index, String type) {
        String jsonString = null;
        try {
            GetResponse response = getRequest()
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
     * @param searchParam 查询条件
     * @param clazz       返回对象类型
     * @param <E>         对象类型
     * @return 分页对象信息
     */
    public <E> EsBasePage<E> findEsBasePage(EsBaseParam searchParam, Class<E> clazz) {
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
        if (EsUtils.isNotEmpty(searchParam.getSearchSql())) {
            search.setQuery(QueryBuilders.queryStringQuery(searchParam.getSearchSql()));
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
        if(null != hits && hits.hits().length > 0){
            for (SearchHit hit : hits) {
                list.add(EsJsonMapper.nonEmptyMapper().fromJson(hit.getSourceAsString(), clazz));
            }
        }
        page.setRecords(list);
        return page;
    }


    /**
     * 条件查询，使用sql语法
     * 返回符合条件的全部数据使用prepareSearchScroll游标查询比设置from查询速度要快
     *
     * @param searchParam 查询条件
     * @param clazz       返回对象类型
     * @param <E>         对象类型
     * @return List对象集合信息
     */
    public <E> List<E> findList(EsBaseParam searchParam, Class<E> clazz) {
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
        if (EsUtils.isNotEmpty(searchParam.getSearchSql())) {
            search.setQuery(QueryBuilders.queryStringQuery(searchParam.getSearchSql()));
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
            sum += hits.hits().length;
            if(null != hits && hits.hits().length > 0){
                for (SearchHit hit : hits) {
                    list.add(EsJsonMapper.nonEmptyMapper().fromJson(hit.getSourceAsString(), clazz));
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
        if (response.isExists()) {
            return true;
        }
        return false;
    }

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
     * 创建公共日志索引
     *
     * @param index           索引 数据库
     * @param type            表
     * @param xContentBuilder mapping
     */
    public synchronized void createIndex(String index, String type, XContentBuilder xContentBuilder) {
        if (indexExists(index)) {
            return;
        }
        IndicesAdminClient adminClient = getIndicesAdminClient();
        adminClient.prepareCreate(index)
                .setSettings(getSettings())
                .addMapping(type, xContentBuilder).execute().actionGet();
    }

    public static Settings.Builder getSettings() {
        return Settings.builder()
                .put("index.number_of_shards", NUMBER_OF_SHARDS)
                .put("index.number_of_replicas", NUMBER_OF_REPLICAS)
                .put("index.max_result_window", MAX_RESULT_WINDOW);
    }
}

