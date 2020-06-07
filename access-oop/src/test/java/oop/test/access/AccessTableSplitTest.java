package oop.test.access;

import oop.access.utils.AccessUtils;
import oop.test.access.entity.TestTableSplit;
import oop.test.access.service.TestTableSplitService;
import org.junit.Test;

public class AccessTableSplitTest {
    @Test
    public void test2() {
        TestTableSplitService sqliteService = new TestTableSplitService();//没有使用spring注入，暂时自己构建
        TestTableSplit entity = new TestTableSplit();
        entity.setType("t1");//分表字段

        entity.setName("test1-1");
        entity.setAuthor("petter");
        entity.setArticle("article1");
        entity.setCreateTime(AccessUtils.getStringDate());
        sqliteService.insert(entity);
        entity.setName("test1-2");
        entity.setAuthor("bob");
        entity.setArticle("article2");
        entity.setCreateTime(AccessUtils.getStringDate());
        sqliteService.insert(entity);


        entity.setType("t2");//分表字段

        entity.setName("test2-1");
        entity.setAuthor("petter");
        entity.setArticle("article1");
        entity.setCreateTime(AccessUtils.getStringDate());
        sqliteService.insert(entity);
        entity.setName("test2-2");
        entity.setAuthor("bob");
        entity.setArticle("article2");
        entity.setCreateTime(AccessUtils.getStringDate());
        sqliteService.insert(entity);

        TestTableSplit queryEntity = new TestTableSplit();
        sqliteService.query(queryEntity);
        queryEntity.setType("t1");
        sqliteService.query(queryEntity);
        queryEntity.setType("t2");
        sqliteService.query(queryEntity);
        queryEntity.setType("t3");
        sqliteService.query(queryEntity);
    }
}
