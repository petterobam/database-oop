package oop.test.elasticsearch.entity;

import oop.elasticsearch.annotation.EsDoc;
import oop.elasticsearch.annotation.EsFields;
import oop.elasticsearch.annotation.EsTransient;
import oop.elasticsearch.base.EsBaseEntity;

/**
 * @author 欧阳洁
 * @since 2018-05-08 18:57
 */
@EsDoc(Index = "test2",Type = "indexType2")
public class TestMappingFieldsBean extends EsBaseEntity {
    /**
     * id
     */
    @EsFields(boost = 5.0)
    private String id;
    /**
     * name
     */
    private String name;
    /**
     * article
     */
    private String article;
    /**
     * article
     */
    @EsTransient
    private String search;

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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
