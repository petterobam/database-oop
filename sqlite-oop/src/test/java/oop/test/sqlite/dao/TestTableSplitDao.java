package oop.test.sqlite.dao;


import oop.sqlite.base.SqliteBaseDao;
import oop.test.sqlite.entity.TestTableSplit;

import java.util.List;

/**
 * Sqlite[test_table]的dao
 *
 * @author 欧阳洁
 * @create 2017-09-29 17:17
 **/
public class TestTableSplitDao extends SqliteBaseDao<TestTableSplit> {
    /**
     * 构造函数
     */
    public TestTableSplitDao() {// 必须要对应实现父类的构造方法
        super(TestTableSplit.class);// 表实体对应类
    }
}
