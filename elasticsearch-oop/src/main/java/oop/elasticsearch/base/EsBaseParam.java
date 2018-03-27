package oop.elasticsearch.base;


/**
 * ElasticSearch查询条件封装基础Bean
 *
 * @author 欧阳洁
 */
public class EsBaseParam {

    /**
     * 索引名称
     */
    private String index;

    /**
     * 索引类型
     */
    private String type;

    /**
     * 排序条件
     */
    private String orderByField;

    /**
     * 查询条件Sql
     *
     * 例如："id":"10" AND "zcmc":"测试"
     * (zcmc:*6* OR zcmc:*2*) AND dsld:[10 TO 18]
     */
    private String searchSql;

    /**
     * 指定需要返回的字段名称（为空返回所有）
     */
    private String[] includes;

    /**
     * 指定不需要返回的字段名称
     */
    private String[] excludes;

    /**
     * 开始页数(从1开始)
     */
    private int current;

    /**
     * 每页条数
     */
    private int size;

    public String getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(String orderByField) {
        this.orderByField = orderByField;
    }

    public String getSearchSql() {
        return searchSql;
    }

    public void setSearchSql(String searchSql) {
        this.searchSql = searchSql;
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

    public int getOffsetCurrent() {
        return offsetCurrent(this.current, this.size);
    }
}
