package oop.test.elasticsearch.service;

import oop.elasticsearch.annotation.EsParamSql;
import oop.elasticsearch.base.EsBaseService;
import oop.test.elasticsearch.entity.TestEsBean;

import java.util.List;

/**
 * @author 欧阳洁
 * @since 2018-06-13 10:59
 */
public class TestEsService extends EsBaseService<TestEsBean> {
    public TestEsService(){
        super(TestEsBean.class);
    }

    /**
     * 通过 name 查询列表
     * @param name
     * @return
     */
    @EsParamSql(paramSql = "name:*{}*")
    public List<TestEsBean> getListByName(String name) {
        return super.excuteParamSql(name);
    }

    /**
     * 通过 entity 映射的值作为参数查询列表
     * @param entity
     * @return
     */
    @EsParamSql(paramSql = "name:*{}* and article:*{}*", params = {"name","article"})
    public List<TestEsBean> getListByNameAndArticle(TestEsBean entity) {
        return super.excuteParamSql(entity);
    }
}
