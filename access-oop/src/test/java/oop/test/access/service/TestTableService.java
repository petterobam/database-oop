package oop.test.access.service;

import oop.access.base.AccessBaseService;
import oop.test.access.dao.TestTableDao;
import oop.test.access.entity.TestTable;

import java.util.List;

/**
 * Sqlite[test_table]的service
 *
 * @author 欧阳洁
 * @create 2017-09-30 15:16
 **/
public class TestTableService extends AccessBaseService<TestTable, TestTableDao> {

    public List<TestTable> getByName(String name) {
        TestTable entity = new TestTable();
        entity.setName(name);
        return this.getBaseDao().getByName(entity);
    }

    public List<TestTable> getByNameOrId(String name, Integer id) {
        return this.getBaseDao().getByNameOrId(name, id);
    }
}
