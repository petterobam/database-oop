package oop.test.sqlite.service;

import oop.sqlite.base.SqliteBaseService;
import oop.test.sqlite.dao.TestSqliteSplitDao;
import oop.test.sqlite.entity.TestSqliteSplit;

import java.util.List;

/**
 * Sqlite[test_table]的service
 *
 * @author 欧阳洁
 * @create 2017-09-30 15:16
 **/
public class TestSqliteSplitService extends SqliteBaseService<TestSqliteSplit, TestSqliteSplitDao> {

    public List<TestSqliteSplit> getByName(String name) {
        TestSqliteSplit entity = new TestSqliteSplit();
        entity.setName(name);
        return this.getBaseDao().getByName(entity);
    }

    public List<TestSqliteSplit> getByNameOrId(String name, Integer id) {
        return this.getBaseDao().getByNameOrId(name, id);
    }
}
