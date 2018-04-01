package oop.test.sqlite;

import oop.sqlite.utils.SqliteUtils;
import oop.test.sqlite.entity.TestSqliteSplit;
import oop.test.sqlite.service.TestSqliteSplitService;
import org.junit.Test;

public class SqliteSplitTest {
    @Test
    public void test5() {
        TestSqliteSplitService sqliteService = new TestSqliteSplitService();//没有使用spring注入，暂时自己构建
        TestSqliteSplit entity = new TestSqliteSplit();
        entity.setName("test1");
        entity.setAuthor("petter");
        entity.setArticle("article1");
        entity.setCreateTime(SqliteUtils.getStringDate());
        sqliteService.insert(entity);
        entity.setName("title2");
        entity.setAuthor("bob");
        entity.setArticle("article2");
        entity.setCreateTime(SqliteUtils.getStringDate());
        sqliteService.insert(entity);

        TestSqliteSplit queryEntity = new TestSqliteSplit();
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
        TestSqliteSplitService sqliteService = new TestSqliteSplitService();//没有使用spring注入，暂时自己构建
        sqliteService.getByName("test");
        sqliteService.getByNameOrId("title", 1);
    }
}
