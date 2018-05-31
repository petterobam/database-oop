package oop.test.access.service;


import oop.access.base.AccessBaseService;
import oop.test.access.dao.TestTableSplitDao;
import oop.test.access.entity.TestTableSplit;

/**
 * Sqlite[test_table]的service
 *
 * @author 欧阳洁
 * @create 2017-09-30 15:16
 **/
public class TestTableSplitService extends AccessBaseService<TestTableSplit, TestTableSplitDao> {
    public TestTableSplitService() {// 必须要对应实现父类的构造方法
        super(TestTableSplitDao.class);// 对应的Dao类
    }
}
