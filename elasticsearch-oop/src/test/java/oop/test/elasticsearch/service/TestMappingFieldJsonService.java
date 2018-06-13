package oop.test.elasticsearch.service;

import oop.elasticsearch.base.EsBaseService;
import oop.test.elasticsearch.entity.TestMappingFieldJsonBean;

/**
 * @author 欧阳洁
 * @since 2018-06-13 10:59
 */
public class TestMappingFieldJsonService extends EsBaseService<TestMappingFieldJsonBean> {
    /**
     * 继承时候使用的构造函数
     */
    public TestMappingFieldJsonService() {
        super(TestMappingFieldJsonBean.class);
    }
}
