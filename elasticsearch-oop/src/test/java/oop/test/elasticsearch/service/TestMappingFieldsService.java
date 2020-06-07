package oop.test.elasticsearch.service;

import oop.elasticsearch.base.EsBaseService;
import oop.test.elasticsearch.entity.TestMappingFieldsBean;

/**
 * @author 欧阳洁
 * @since 2018-06-13 10:59
 */
public class TestMappingFieldsService extends EsBaseService<TestMappingFieldsBean> {
    public TestMappingFieldsService(){
        super(TestMappingFieldsBean.class);
    }
}
