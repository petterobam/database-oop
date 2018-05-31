package oop.test.access;

import oop.access.utils.AccessUtils;
import oop.test.access.entity.TestAccessSplit;
import oop.test.access.service.TestAccessSplitService;
import org.junit.Test;

public class AccessSplitTest {
    @Test
    public void test5() {
        TestAccessSplitService sqliteService = new TestAccessSplitService();//没有使用spring注入，暂时自己构建
        TestAccessSplit entity = new TestAccessSplit();
        entity.setName("test1");
        entity.setAuthor("petter");
        entity.setArticle("article1");
        entity.setCreateTime(AccessUtils.getStringDate());
        sqliteService.insert(entity);
        entity.setName("title2");
        entity.setAuthor("bob");
        entity.setArticle("article2");
        entity.setCreateTime(AccessUtils.getStringDate());
        sqliteService.insert(entity);

        TestAccessSplit queryEntity = new TestAccessSplit();
        sqliteService.query(queryEntity);
        queryEntity.setAuthor("petter");
        sqliteService.query(queryEntity);
        queryEntity.setName("test");
        sqliteService.query(queryEntity);
        queryEntity.setId(1);
        sqliteService.query(queryEntity);
    }

    @Test
    public void test6() {
        TestAccessSplitService sqliteService = new TestAccessSplitService();//没有使用spring注入，暂时自己构建
        sqliteService.getByName("test");
        sqliteService.getByNameOrId("title", 1);
    }
}
