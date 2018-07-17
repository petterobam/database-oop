package oop.test.elasticsearch;

import oop.test.elasticsearch.entity.TestEsBean;
import oop.test.elasticsearch.service.TestEsService;
import org.junit.Test;

import java.util.List;

/**
 * Created by vetech on 2018/7/17.
 */
public class EsParamSqlTest {
    @Test
    public void test1() {
        try {
            TestEsService service = new TestEsService();
            TestEsBean esBean = new TestEsBean();
            esBean.setId("qwerqwerasdfasfaddsd");
            esBean.setName("惊世毒妃1");
            esBean.setArticle("相府弱女，大婚当日，不堪受辱，魂归西天，现代特工，穿越而来，从此难掩智谋锋芒，风华绝代！昔日懦弱嫡女，一夕之间成为惊世毒妃！他是邪魅狂肆，铁血霸道，鬼神惧之的越王。大婚次日，她和他双双勿饮媚药，一夜后，是相憎相杀，还是相爱相守？");
            service.insert(esBean);
            esBean.setId("aeqwrqwerqwerwerffrw");
            esBean.setName("惊世毒2");
            esBean.setArticle("相府弱女，大婚当日，不堪受辱，魂归西天，现代特工，穿越而来，从此难掩智谋锋芒，风华绝代！昔日懦弱嫡女，一夕之间成为惊世毒妃！他是邪魅狂肆，铁血霸道，鬼神惧之的越王。大婚次日，她和他双双勿饮媚药，一夜后，是相憎相杀，还是相爱相守？");
            service.insert(esBean);
            esBean.setId("7uyeujdghdhgdfccghgfd");
            esBean.setName("惊世妃3");
            esBean.setArticle("相府弱女，大婚当日，不堪受辱，魂归西天，现代特工，穿越而来，从此难掩智谋锋芒，风华绝代！昔日懦弱嫡女，一夕之间成为惊世毒妃！他是邪魅狂肆，铁血霸道，鬼神惧之的越王。大婚次日，她和他双双勿饮媚药，一夜后，是相憎相杀，还是相爱相守？");
            service.insert(esBean);

            TestEsBean query = new TestEsBean();
            query.setName("惊世毒妃");
            query.setArticle("大婚当日");
            service.getListByNameAndArticle(query);

            service.getListByName("惊世毒妃");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
