package oop.test.sqlite;

import oop.sqlite.entity.TestSpliteSqlite;
import oop.sqlite.entity.TestTable;
import oop.sqlite.service.TestSpliteSqliteService;
import oop.sqlite.service.TestTableService;
import oop.sqlite.utils.SqliteHelper;
import oop.sqlite.utils.SqliteUtils;
import org.junit.Test;

import java.util.List;

/**
 * sqlite测试
 *
 * @author 欧阳洁
 * @create 2017-09-29 18:10
 **/
public class SqliteTest {
    @Test
    public void test1() throws ClassNotFoundException {
        SqliteHelper.test();
    }

    @Test
    public void test2() {
        TestTableService sqliteService = new TestTableService();//没有使用spring注入，暂时自己构建
        TestTable entity = new TestTable();
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

        TestTable queryEntity = new TestTable();
        sqliteService.query(queryEntity);
        queryEntity.setAuthor("petter");
        sqliteService.query(queryEntity);
        queryEntity.setName("test");
        sqliteService.query(queryEntity);
        queryEntity.setId(1);
        sqliteService.query(queryEntity);
    }

    @Test
    public void test3() {
        TestTableService sqliteService = new TestTableService();//没有使用spring注入，暂时自己构建
        List<TestTable> list = sqliteService.getByName("test");
    }

    @Test
    public void test4() {
        TestTableService sqliteService = new TestTableService();//没有使用spring注入，暂时自己构建
        List<TestTable> list = sqliteService.getByNameOrId("title", 1);
    }

    @Test
    public void test5() {
        TestSpliteSqliteService sqliteService = new TestSpliteSqliteService();//没有使用spring注入，暂时自己构建
        TestSpliteSqlite entity = new TestSpliteSqlite();
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

        TestSpliteSqlite queryEntity = new TestSpliteSqlite();
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
        TestSpliteSqliteService sqliteService = new TestSpliteSqliteService();//没有使用spring注入，暂时自己构建
        sqliteService.getByName("test");
        sqliteService.getByNameOrId("title", 1);
    }
    @Test
    public void test7(){
        SqliteHelper sqliteHelper = new SqliteHelper("/D:/Sqlite/dbs/test.db",true);
        sqliteHelper.queryJsonResult("select * from person");
        sqliteHelper.cmdExec(".dd");
        String result = sqliteHelper.cmdExec(".tables");
        if(!SqliteUtils.isBlank(result)) {
            result = result.replaceAll("\r", " ");
            result = result.replaceAll("\n", " ");
            while (result.indexOf("  ") > 0) {
                result = result.replaceAll("  ", " ");
            }
            String[] arr = result.split(" ");
            if(null != arr){
                for (String s : arr) {
                    System.out.println(s);
                }
            }
        }
    }
    @Test
    public void test8(){
        TestTableService sqliteService = new TestTableService();//没有使用spring注入，暂时自己构建
        int count1 = sqliteService.count("select count(1) from t_test_table");
        System.out.println(count1);
        TestTable query = new TestTable();
        //query.setName("petter");
        int count = sqliteService.count(query);
        System.out.println(count);
    }
    //TODO 测试自定义注解，测试数据库函数和过程
}
