package oop.test.sqlite.service;

import oop.sqlite.base.SqliteBaseService;
import oop.test.sqlite.dao.TestSpliteSqliteDao;
import oop.test.sqlite.entity.TestSpliteSqlite;

import java.util.List;

/**
 * Sqlite[test_table]的service
 *
 * @author 欧阳洁
 * @create 2017-09-30 15:16
 **/
public class TestSpliteSqliteService extends SqliteBaseService<TestSpliteSqlite, TestSpliteSqliteDao> {
    public TestSpliteSqliteService() {// 必须要对应实现父类的构造方法
        super(TestSpliteSqliteDao.class);// 对应的Dao类
    }

    public List<TestSpliteSqlite> getByName(String name) {
        TestSpliteSqlite entity = new TestSpliteSqlite();
        entity.setName(name);
        return this.getBaseDao().getByName(entity);
    }

    public List<TestSpliteSqlite> getByNameOrId(String name, Integer id) {
        return this.getBaseDao().getByNameOrId(name, id);
    }
}
