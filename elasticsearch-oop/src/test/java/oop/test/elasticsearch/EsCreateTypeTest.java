package oop.test.elasticsearch;

import oop.test.elasticsearch.service.TestMappingFieldsService;
import oop.test.elasticsearch.service.TestMappingJsonFieldsService;
import org.junit.Test;

/**
 * 测试创建索引类
 *
 * @author 欧阳洁
 * @since 2018-06-12 18:16
 */
public class EsCreateTypeTest {
    @Test
    public void test1() {
        try {
            TestMappingFieldsService service = new TestMappingFieldsService();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void test2() {
        try {
            TestMappingJsonFieldsService service = new TestMappingJsonFieldsService();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
