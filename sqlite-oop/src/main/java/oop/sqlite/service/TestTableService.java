package oop.sqlite.service;

import oop.sqlite.base.SqliteBaseService;
import oop.sqlite.dao.TestTableDao;
import oop.sqlite.entity.TestTable;

import java.util.List;

/**
 * Sqlite[test_table]的service
 *
 * @author 欧阳洁
 * @create 2017-09-30 15:16
 **/
public class TestTableService extends SqliteBaseService<TestTable, TestTableDao> {
    public TestTableService() {// 必须要对应实现父类的构造方法
        super(TestTableDao.class);// 对应的Dao类
    }

    public List<TestTable> getByName(String name) {
        TestTable entity = new TestTable();
        entity.setName(name);
        return this.getBaseDao().getByName(entity);
    }

    public List<TestTable> getByNameOrId(String name, Integer id) {
        return this.getBaseDao().getByNameOrId(name, id);
    }
}
