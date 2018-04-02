package oop.test.elasticsearch;

import oop.elasticsearch.base.EsBaseService;
import oop.elasticsearch.utils.EsLogUtils;
import oop.test.elasticsearch.service.TestEsService;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Test;

public class ElasticsearchTest {
    @Test
    public void test1() {
        try {
            //创建客户端
            TestEsService esService = new TestEsService();
            EsLogUtils.debug("Elasticsearch connect info:" + esService.getClient().toString());
            //关闭客户端
            esService.closeClient();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
