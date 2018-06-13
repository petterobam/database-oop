package oop.test.elasticsearch;

import oop.test.elasticsearch.entity.TestEsBean;
import oop.test.elasticsearch.service.TestEsService;
import org.junit.Test;

import java.util.List;

/**
 * Es基础功能测试
 *
 * @author 欧阳洁
 * @since 2018-06-13 11:30
 */
public class EsBaseTest {
    @Test
    public void test1() {
        try {
            TestEsService service = new TestEsService();
            TestEsBean esBean = new TestEsBean();
            esBean.setId("qwerqwerasdfasfasd");
            esBean.setName("惊世毒妃1");
            esBean.setArticle("相府弱女，大婚当日，不堪受辱，魂归西天，现代特工，穿越而来，从此难掩智谋锋芒，风华绝代！昔日懦弱嫡女，一夕之间成为惊世毒妃！他是邪魅狂肆，铁血霸道，鬼神惧之的越王。大婚次日，她和他双双勿饮媚药，一夜后，是相憎相杀，还是相爱相守？");
            service.insert(esBean);
            esBean.setId("aeqwrqwerqwerwerrw");
            esBean.setName("惊世毒妃2");
            esBean.setArticle("相府弱女，大婚当日，不堪受辱，魂归西天，现代特工，穿越而来，从此难掩智谋锋芒，风华绝代！昔日懦弱嫡女，一夕之间成为惊世毒妃！他是邪魅狂肆，铁血霸道，鬼神惧之的越王。大婚次日，她和他双双勿饮媚药，一夜后，是相憎相杀，还是相爱相守？");
            service.insert(esBean);
            esBean.setId("7uyeujdghdhgdfghgfd");
            esBean.setName("惊世毒妃3");
            esBean.setArticle("相府弱女，大婚当日，不堪受辱，魂归西天，现代特工，穿越而来，从此难掩智谋锋芒，风华绝代！昔日懦弱嫡女，一夕之间成为惊世毒妃！他是邪魅狂肆，铁血霸道，鬼神惧之的越王。大婚次日，她和他双双勿饮媚药，一夜后，是相憎相杀，还是相爱相守？");
            service.insert(esBean);

            List<TestEsBean> list = service.query(new TestEsBean());

            if(null != list){
                for (TestEsBean testEsBean : list) {
                    testEsBean.setArticle("魂归西天，现代特工，穿越而来，从此难掩智谋锋芒，风华绝代！");
                    service.upsert(testEsBean);
                }
            }

            service.deleteById("7uyeujdghdhgdfghgfd");

            TestEsBean query = new TestEsBean();
            service.query(query);

            query.setSearchParam("name:*惊世毒妃*");
            service.query(query);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
