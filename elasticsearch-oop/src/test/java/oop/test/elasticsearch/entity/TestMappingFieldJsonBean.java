package oop.test.elasticsearch.entity;

import oop.elasticsearch.annotation.EsDoc;
import oop.elasticsearch.annotation.EsFieldsJson;
import oop.elasticsearch.base.EsBaseEntity;

/**
 * @author 欧阳洁
 * @since 2018-06-13 10:31
 */
@EsDoc(Index = "test3",Type = "indexType3")
public class TestMappingFieldJsonBean extends EsBaseEntity {
    /**
     * id
     */
    @EsFieldsJson("{'type': 'keyword','store': true,'boost':5.0}")
    private String id;
    /**
     * name
     */
    @EsFieldsJson("{'type': 'keyword','store': true}")
    private String name;
    /**
     * article
     */
    @EsFieldsJson("{'type': 'text','store': true}")
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
