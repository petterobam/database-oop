package oop.test.elasticsearch.service;

import oop.elasticsearch.base.EsBaseService;
import oop.test.elasticsearch.entity.TestMappingJsonFileBean;
/**
 * @author 欧阳洁
 * @since 2018-06-13 10:59
 */
public class TestMappingJsonFieldsService extends EsBaseService<TestMappingJsonFileBean> {
    public TestMappingJsonFieldsService(){
        super(TestMappingJsonFileBean.class);
    }
}
