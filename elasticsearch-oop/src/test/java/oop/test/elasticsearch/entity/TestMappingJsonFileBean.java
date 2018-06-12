package oop.test.elasticsearch.entity;

import oop.elasticsearch.annotation.EsDoc;
import oop.elasticsearch.annotation.EsMappingFile;
import oop.elasticsearch.base.EsBaseEntity;

/**
 * 测试
 *
 * @author 欧阳洁
 * @since 2018-05-08 18:51
 */
@EsDoc(Index = "test1",Type = "indexType1")
@EsMappingFile("test/mapping.json")
public class TestMappingJsonFileBean extends EsBaseEntity {
    /**
     * id
     */
    private String id;
    /**
     * name
     */
    private String name;
    /**
     * article
     */
    private String article;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
}
