package oop.test.access.entity;

import oop.access.annotation.AccessColumn;
import oop.access.annotation.AccessID;
import oop.access.annotation.AccessTable;
import oop.access.annotation.AccessTransient;
import oop.access.base.AccessBaseEntity;
import oop.access.constant.AccessConstant;

/**
 * 测试分库表对应实体类
 *
 * @author 欧阳洁
 * @create 2017-09-30 9:44
 **/
@AccessTable(name = "t_test_splite_sqlite",dbPath = "database/t_test_splite_",dbType = AccessConstant.DB_TYPE_BY_DAY)
public class TestAccessSplit extends AccessBaseEntity {
    /**
     * 主键
     */
    @AccessID
    private Integer id;
    /**
     * 名称
     */
    @AccessColumn(type = "char(100)", notNull = true)
    private String name;
    /**
     * 作者
     */
    @AccessColumn(notNull = true)
    private String author;
    /**
     * 正文
     */
    @AccessColumn(type = "text")
    private String article;
    /**
     * 创建时间
     */
    @AccessColumn(name = "create_time",type = "char(20)", notNull = true)
    private String createTime;
    /**
     * 查询类型 （非表字段）
     */
    @AccessTransient
    private String searchType;
    /**
     * 发布时间 （非表字段）
     * 注：这里不使用AccessColumn主键，默认的列名为publishtime
     */
    @AccessTransient
    @AccessColumn(name = "publish_time")
    private String publishTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }
}
