package oop.test.elasticsearch.service;

import oop.elasticsearch.base.EsBaseService;
import oop.test.elasticsearch.entity.TestMappingJsonFileBean;

public class TestMappingJsonFieldsService extends EsBaseService<TestMappingJsonFileBean> {
    public TestMappingJsonFieldsService(){
        super(TestMappingJsonFileBean.class);
    }
}
