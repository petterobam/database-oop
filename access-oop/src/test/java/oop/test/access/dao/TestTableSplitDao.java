package oop.test.access.dao;


import oop.access.base.AccessBaseDao;
import oop.test.access.entity.TestTableSplit;

/**
 * Sqlite[test_table]的dao
 *
 * @author 欧阳洁
 * @create 2017-09-29 17:17
 **/
public class TestTableSplitDao extends AccessBaseDao<TestTableSplit> {
    /**
     * 构造函数
     */
    public TestTableSplitDao() {// 必须要对应实现父类的构造方法
        super(TestTableSplit.class);// 表实体对应类
    }
}
