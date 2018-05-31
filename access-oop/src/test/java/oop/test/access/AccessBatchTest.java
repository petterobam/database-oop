package oop.test.access;

import oop.access.utils.AccessLogUtils;
import oop.access.utils.AccessUtils;
import oop.test.access.entity.TestTable;
import oop.test.access.service.TestTableService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Sqlite批量操作测试
 *
 * @author 欧阳洁
 * @since 2018-05-14 16:47
 */
public class AccessBatchTest {
    @Test
    public void test() throws ClassNotFoundException {
        TestTableService sqliteService = new TestTableService();//没有使用spring注入，暂时自己构建

        sqliteService.delete("delete from t_test_table");
        AccessLogUtils.info("===数据总条数：{}",sqliteService.count(new TestTable()));

        List<TestTable> batchList = new ArrayList<TestTable>();
        TestTable entity = new TestTable();
        entity.setName("test1");
        entity.setAuthor("petter");
        entity.setArticle("article1");
        entity.setCreateTime(AccessUtils.getStringDate());
        batchList.add(entity);

        entity = new TestTable();
        entity.setName("test2");
        entity.setAuthor("petter");
        entity.setArticle("article1");
        entity.setCreateTime(AccessUtils.getStringDate());
        batchList.add(entity);

        entity = new TestTable();
        entity.setName("title4");
        entity.setAuthor("bob");
        entity.setArticle("article2");
        entity.setCreateTime(AccessUtils.getStringDate());
        batchList.add(entity);

        AccessLogUtils.info("--开始执行批量插入操作！");
        sqliteService.batchInsert(batchList);
        AccessLogUtils.info("--结束执行批量插入操作！");

        AccessLogUtils.info("===数据总条数：{}",sqliteService.count(new TestTable()));
        List<TestTable> tableList = sqliteService.query(new TestTable());

        if(AccessUtils.isNotEmpty(tableList)){
            tableList.remove(0);
        }

        for (TestTable testTable : tableList) {
            testTable.setName("Update");
        }

        AccessLogUtils.info("--开始执行批量修改操作！");
        sqliteService.batchUpdate(tableList);
        AccessLogUtils.info("--结束执行批量修改操作！");

        AccessLogUtils.info("===数据总条数：{}",sqliteService.count(new TestTable()));
        sqliteService.query(new TestTable());

        AccessLogUtils.info("--开始执行批量删除操作！");
        sqliteService.batchDelete(tableList);
        AccessLogUtils.info("--结束执行批量删除操作！");

        AccessLogUtils.info("===数据总条数：{}",sqliteService.count(new TestTable()));
    }
}
