package oop.test.elasticsearch;

import oop.elasticsearch.config.ElasticsearchConfig;
import oop.elasticsearch.utils.EsLogUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Test;

public class ElasticsearchTest {
    @Test
    public void test1() {
        try {
            TransportClient client = ElasticsearchConfig.getClient();
            //创建客户端
            EsLogUtils.debug("Elasticsearch connect info:" + client.toString());
            //关闭客户端
            client.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
