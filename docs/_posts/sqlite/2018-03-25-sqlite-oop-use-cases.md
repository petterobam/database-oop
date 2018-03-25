---
layout: post
title: "Sqlite-OOP子项目使用示例"
description: "Sqlite数据库的面向对象封装"
categories: [Sqlite, 面向对象封装, Java]
tags: [Sqlite,使用示例]
redirect_from:
  - /2018/03/25/
---

## 使用示例
### 定义表结构实体
```java
/**
 * 测试表对应实体类
 * @author 欧阳洁
 * @create 2017-09-30 9:44
 **/
@SqliteTable(name = "t_test_table")
public class TestTable extends SqliteBaseEntity {
    /**
     * 主键
     */
    @SqliteID
    private Integer id;
    /**
     * 名称
     */
    @SqliteColumn(type = "char(100)", notNull = true)
    private String name;
    /**
     * 作者
     */
    @SqliteColumn(notNull = true)
    private String author;
    /**
     * 正文
     */
    @SqliteColumn(type = "text")
    private String article;
    /**
     * 创建时间
     */
    @SqliteColumn(name = "create_time",type = "char(20)", notNull = true)
    private String createTime;
    /**
     * 查询类型 （非表字段）
     */
    @SqliteTransient
    private String searchType;
    /**
     * 发布时间 （非表字段）
     * 注：这里不使用SqliteColumn主键，默认的列名为publishtime
     */
    @SqliteTransient
    @SqliteColumn(name = "create_time")
    private String publishTime;

    //get、set此处省略
}
```

### 定义实体对应的Dao

```java
/**
 * Sqlite[t_test_table]的dao
 * @author 欧阳洁
 * @create 2017-09-29 17:17
 */
public class TestTableDao extends SqliteBaseDao<TestTable> {
    /**
     * 构造函数
     */
    public TestTableDao() {// 必须要对应实现父类的构造方法
        super(TestTable.class);// 表实体对应类
    }

    /**
     * 根据名称模糊查找数据
     * @param entity
     * @return
     */
    @SqliteSql(sql = "select t.create_time publish_time,t.* from this.tableName t where name like '%'||?||'%'", params = {"name"})
    public List<TestTable> getByName(TestTable entity) {
        //List<T> super.excuteQuery(T entity)，通过params上的参数顺序在entity中获取，并依次填充占位符
        return super.excuteQuery(entity);
    }

    /**
     * 根据名称模糊查找数据并包含id查找
     * @param name
     * @param id
     * @return
     */
    @SqliteSql(sql = "select * from this.tableName where name like '%'||?||'%' or id=?")
    public List<TestTable> getByNameOrId(String name, Integer id) {
        //List<T> super.excuteQuery(Object... params)，这里的参数顺序对应自定义的SQL的占位符顺序
        return super.excuteQuery(name, id);
    }
}
```

### 定义Dao对应的Service

```java
/**
 * Sqlite[t_test_table]的service
 * @author 欧阳洁
 * @create 2017-09-30 15:16
 */
@Service
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
```

## 单元测试结果
```java
//默认的方法测试，包括初始化检查表是否存在并构建、对象插入、对象查询（主键穿透查询）
/*————————————————————————————————————————<SqliteTest.java>—————————————————————————————————————*/
@Test
public void test2() {
    TestTableService sqliteService = new TestTableService();//没有使用spring注入，暂时自己构建
    TestTable entity = new TestTable();
    entity.setName("test1");
    entity.setAuthor("petter");
    entity.setArticle("article1");
    entity.setCreateTime(MyDate.getStringDate());
    sqliteService.insert(entity);
    entity.setName("title2");
    entity.setAuthor("bob");
    entity.setArticle("article2");
    entity.setCreateTime(MyDate.getStringDate());
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
```

test2()测试结果：
> 执行非查询语句==> create table if not exists t_test_table(id integer  primary key autoincrement not null,name char(100)  not null,author char(20)  not null,article text ,<font color='red'>create_time</font> char(20)  not null)<br/>
执行非查询语句影响行数==> 0<br/>
执行非查询语句==> INSERT INTO t_test_table(name,author,article,create_time)values(?,?,?,?)<br/>
执行非查询语句影响行数==> 1<br/>
执行非查询语句==> INSERT INTO t_test_table(name,author,article,create_time)values(?,?,?,?)<br/>
执行非查询语句影响行数==> 1<br/>
执行查询语句==> SELECT * FROM t_test_table WHERE 1=1<br/> 
执行查询语句结果==> [{"id":1,"name":"test1","author":"petter","article":"article1","<font color='red'>createTime</font>":"2018-02-20 22:54:32"},{"id":2,"name":"title2","author":"bob","article":"article2","<font color='red'>createTime</font>":"2018-02-20 22:54:32"}]<br/>
执行查询语句==> SELECT * FROM t_test_table WHERE 1=1  and author=?<br/>
执行查询语句结果==> [{"id":1,"name":"test1","author":"petter","article":"article1","<font color='red'>createTime</font>":"2018-02-20 22:54:32"}]<br/>
执行查询语句==> SELECT * FROM t_test_table WHERE 1=1  and name=? and author=?<br/>
执行查询语句结果==> []<br/>
执行查询语句==> SELECT * FROM t_test_table WHERE 1=1  and id=?<br/>
执行查询语句结果==> [{"id":1,"name":"test1","author":"petter","article":"article1","<font color='red'>createTime</font>":"2018-02-20 22:54:32"}]<br/>

------------------------------------------------------------------------------------------------

```java
//自定义的SQL查询测试，包含自定义SQL、结果集中额外列对应填充和查询对象属性值定位获取
/*——————————————————————————————————————<SqliteTest.java>——————————————————————————————————————*/
@Test
public void test3() {
    TestTableService sqliteService = new TestTableService();//没有使用spring注入，暂时自己构建
    List<TestTable> list = sqliteService.getByName("test");
}
```

test3()测试结果：
> 执行非查询语句==> create table if not exists t_test_table(id integer  primary key autoincrement not null,name char(100)  not null,author char(20)  not null,article text ,create_time char(20)  not null)<br/>
执行非查询语句影响行数==> 0<br/>
执行查询语句==> select t.create_time publish_time,t.* from t_test_table t where name like '%'||?||'%'<br/>
执行查询语句结果==> [{"<font color='red'>publishTime</font>":"2018-02-20 22:36:18","id":1,"name":"test1","author":"petter","article":"article1","createTime":"2018-02-20 22:36:18"}]

------------------------------------------------------------------------------------------------

```java
//自定义的SQL查询测试，这里直接撇开了实体类，可以任意的传参了，甚至调用存储过程或函数只需要一行注解就够了
/*———————————————————————————————————————<SqliteTest.java>—————————————————————————————————————*/
@Test
public void test4() {
    TestTableService sqliteService = new TestTableService();//没有使用spring注入，暂时自己构建
    List<TestTable> list = sqliteService.getByNameOrId("title", 1);
}
```

> 执行非查询语句==> create table if not exists t_test_table(id integer  primary key autoincrement not null,name char(100)  not null,author char(20)  not null,article text ,create_time char(20)  not null) <br/>
执行非查询语句影响行数==> 0<br/>
执行查询语句==> select * from t_test_table where name like '%'||?||'%' or id=?<br/>
执行查询语句结果==> [{"id":1,"name":"test1","author":"petter","article":"article1","createTime":"2018-02-20 22:36:18"},{"id":2,"name":"title2","author":"bob","article":"article2","createTime":"2018-02-20 22:36:19"}]