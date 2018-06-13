package oop.test.elasticsearch.service;

import oop.elasticsearch.base.EsBaseService;
import oop.test.elasticsearch.entity.TestEsBean;

/**
 * @author 欧阳洁
 * @since 2018-06-13 10:59
 */
public class TestEsService extends EsBaseService<TestEsBean> {
    public TestEsService(){
        super(TestEsBean.class);
    }
}
