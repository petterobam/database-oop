package oop.test.access.service;

import oop.access.base.AccessBaseService;
import oop.test.access.dao.TestAccessSplitDao;
import oop.test.access.entity.TestAccessSplit;

import java.util.List;

/**
 * Sqlite[test_table]的service
 *
 * @author 欧阳洁
 * @create 2017-09-30 15:16
 **/
public class TestAccessSplitService extends AccessBaseService<TestAccessSplit, TestAccessSplitDao> {
    public TestAccessSplitService() {// 必须要对应实现父类的构造方法
        super(TestAccessSplitDao.class);// 对应的Dao类
    }

    public List<TestAccessSplit> getByName(String name) {
        TestAccessSplit entity = new TestAccessSplit();
        entity.setName(name);
        return this.getBaseDao().getByName(entity);
    }

    public List<TestAccessSplit> getByNameOrId(String name, Integer id) {
        return this.getBaseDao().getByNameOrId(name, id);
    }
}
