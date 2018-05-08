package oop.test.elasticsearch;

import oop.elasticsearch.utils.EsLogUtils;
import oop.elasticsearch.utils.EsMappingHelper;
import oop.test.elasticsearch.entity.TestMappingFieldsBean;
import oop.test.elasticsearch.entity.TestMappingJsonFileBean;
import org.junit.Test;

/**
 * @author 欧阳洁
 * @since 2018-05-08 18:57
 */
public class EsMappingHelperTest {
    @Test
    public void test1() {
        try {
            EsMappingHelper esMappingHelper = new EsMappingHelper(TestMappingJsonFileBean.class);
            EsLogUtils.debug(esMappingHelper.getIndex());
            EsLogUtils.debug(esMappingHelper.getType());
            EsLogUtils.debug(esMappingHelper.getMappingStr());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void test2() {
        try {
            EsMappingHelper esMappingHelper = new EsMappingHelper(TestMappingFieldsBean.class);
            EsLogUtils.debug(esMappingHelper.getIndex());
            EsLogUtils.debug(esMappingHelper.getType());
            EsLogUtils.debug(esMappingHelper.getMappingStr());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
