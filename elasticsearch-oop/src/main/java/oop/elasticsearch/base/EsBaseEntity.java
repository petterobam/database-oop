package oop.elasticsearch.base;


import com.alibaba.fastjson.annotation.JSONField;

/**
 * ElasticSearch查询条件封装基础Bean
 *
 * @author 欧阳洁
 */
public class EsBaseEntity {

    /**
     * 索引名称
     */
    @JSONField(serialize=false)
    private String index;
    /**
     * 索引类型
     */
    @JSONField(serialize=false)
    private String type;
    /**
     * 排序条件
     */
    @JSONField(serialize=false)
    private String orderByField;
    /**
     * Get 请求参数查询 参数
     * <p>
     * 例如："id":"10" AND "zcmc":"测试"
     * (zcmc:*6* OR zcmc:*2*) AND dsld:[10 TO 18]
     */
    @JSONField(serialize=false)
    private String searchParam;
    /**
     * POST 请求体查询 请求体 Json
     * <p>
     * 例如：{"query": {"bool": {"must": [{"match": {"name": "测试"}}]}}}
     */
    @JSONField(serialize=false)
    private String searchBody;
    /**
     * 指定需要返回的字段名称（为空返回所有）
     */
    @JSONField(serialize=false)
    private String[] includes;
    /**
     * 指定不需要返回的字段名称
     */
    @JSONField(serialize=false)
    private String[] excludes;
    /**
     * 开始页数(从1开始)
     */
    @JSONField(serialize=false)
    private int current;
    /**
     * 每页条数
     */
    @JSONField(serialize=false)
    private int size = 1000;

    public String getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(String orderByField) {
        this.orderByField = orderByField;
    }

    public String getSearchParam() {
        return searchParam;
    }

    public void setSearchParam(String searchParam) {
        this.searchParam = searchParam;
    }

    public String getSearchBody() {
        return searchBody;
    }

    public void setSearchBody(String searchBody) {
        this.searchBody = searchBody;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getIncludes() {
        return includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    public String[] getExcludes() {
        return excludes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    /**
     * 获取分页起始位置
     *
     * @param current 开始页数
     * @param size    每页条数
     * @return 分页起始位置
     */
    private int offsetCurrent(int current, int size) {
        return current > 0 ? (current - 1) * size : 0;
    }

    @JSONField(serialize = false)
    public int getOffsetCurrent() {
        return offsetCurrent(this.current, this.size);
    }
}
