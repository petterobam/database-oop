package oop.test.sqlite.entity;


import oop.sqlite.annotation.SqliteColumn;
import oop.sqlite.annotation.SqliteID;
import oop.sqlite.annotation.SqliteTable;
import oop.sqlite.annotation.SqliteTableSplit;
import oop.sqlite.annotation.SqliteTransient;
import oop.sqlite.base.SqliteBaseEntity;

/**
 * 测试表对应实体类
 *
 * @author 欧阳洁
 * @create 2017-09-30 9:44
 **/
@SqliteTable(name = "t_test_split_table")
public class TestTableSplit extends SqliteBaseEntity {
    /**
     * 主键
     */
    @SqliteID
    private Integer id;
    /**
     * 类型，分表字段
     */
    @SqliteColumn(notNull = true)
    @SqliteTableSplit
    private String type;
    /**
     * 名称
     */
    @SqliteColumn(type = "char(100)", notNull = true)
    private String name;
    /**
     * 作者
     */
    @SqliteColumn(notNull = true)
    private String author;
    /**
     * 正文
     */
    @SqliteColumn(type = "text")
    private String article;
    /**
     * 创建时间
     */
    @SqliteColumn(name = "create_time",type = "char(20)", notNull = true)
    private String createTime;
    /**
     * 查询类型 （非表字段）
     */
    @SqliteTransient
    private String searchType;
    /**
     * 发布时间 （非表字段）
     * 注：这里不使用SqliteColumn主键，默认的列名为publishtime
     */
    @SqliteTransient
    @SqliteColumn(name = "publish_time")
    private String publishTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
