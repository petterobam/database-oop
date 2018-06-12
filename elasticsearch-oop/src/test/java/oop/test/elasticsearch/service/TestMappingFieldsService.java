package oop.test.elasticsearch.service;

import oop.elasticsearch.base.EsBaseService;
import oop.test.elasticsearch.entity.TestMappingFieldsBean;

public class TestMappingFieldsService extends EsBaseService<TestMappingFieldsBean> {
    public TestMappingFieldsService(){
        super(TestMappingFieldsBean.class);
    }
}
