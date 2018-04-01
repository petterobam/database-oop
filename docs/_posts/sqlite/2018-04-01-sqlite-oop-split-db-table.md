---
layout: post
title: "Sqlite-OOP分库与分表"
author: "petterobam"
description: "Sqlite数据库的面向对象封装"
categories: [Sqlite, 面向对象封装, Java]
tags: [Sqlite,分库,分表]
redirect_from:
  - /2018/04/01/
---

## 分库与分表

分库，即同一个系统，不同数据写在不同数据库里面或同一张表信息根据某个规则有规律写在不同数据库里面的做法。分表，表示同一类数据根据某个特征写在表结构相同的不同表里面。实现分库与分表可以大大分流数据写的压力和部分场景下读的压力，同时如果能够运用的好的话还可以有效的分离不同业务数据，实现业务存储的解耦。

两种做法都是对数据的存储处理，但是都有适用各自的场景：

分库有如下优劣：
  - 业务间独立拆分，减少数据底层操作的干扰
  - 业务拆分后，降低每个业务线数据维护成本
  - 扩展数据库连接数，提升吞吐性能
  - 业务线解耦后，有利于各业务线组装扩展
  - 可以用于读写分离，增强响应能力
  - 不同业务线之间无法连表查询（小系统不方便）
  - 不同库的写入，事务管理困难

分表有如下优劣：
  - 大表数据拆分，同类数据聚合
  - 多表操作，并发性能提高
  - 没有表的规则控制，数据的验证和唯一性之类放到应用端
  - 事务管理不能做到原子性、一致性

## 实现思路

### 分库

1. 让表可以配置库源
  - 扩展表注解：实现表与库的关联，控制每个表对应的库

  ```java
  /**
   * 表名注解类
   *
   * @author 欧阳洁
   * @create 2017-09-30 11:16
   **/
  @java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
  @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
  public @interface SqliteTable {
      /**
       * 表名
       * @return
       */
      String name() default "";
      /**
       * 数据库文件路径
       * @return
       */
      String dbPath() default oop.sqlite.config.SqliteConfig.DB_PATH;
      /**
       * 数据库文件路径类型
       * @return
       */
      int dbType() default oop.sqlite.config.SqliteConfig.DB_TYPE_DEFAULT;
  }
  ```

  - 定义分库规则：dbPath 属性让我们在定义实体时候就可以实现手动分库，而 dbType 是为了自动分库（一般用于日志）

  ```java
  /**
   * 程序数据库动态生成规则
   */
  public static final int DB_TYPE_DEFAULT = 0;//不分库
  public static final int DB_TYPE_BY_MINUTE = 1;//按分钟自动分库
  public static final int DB_TYPE_BY_HOUR = 2;//按小时自动分库
  public static final int DB_TYPE_BY_DAY = 3;//按天自动分库
  public static final int DB_TYPE_BY_MOUTH = 4;//按月自动分库
  public static final int DB_TYPE_BY_YEAR = 5;//按年自动分库
  ```

2. 处理库源

  - 静态加载：每个服务注入的时候，对应自己加载每个表的 dbPath

  ```java
  /**
   * 构造函数
   *
   * @param targetClass
   */
  public SqliteHelper(Class<?> targetClass) {
      this.dbPath = SqliteConfig.DB_PATH;
      this.dbType = SqliteConfig.DB_TYPE_DEFAULT;
      SqliteTable sqliteTable = targetClass.getAnnotation(SqliteTable.class);
      if (null != sqliteTable) {
          this.dbPath = sqliteTable.dbPath();
          this.dbType = sqliteTable.dbType();
      }
      this.dbPath = SqliteUtils.getClassRootPath(this.dbPath);
  }
  ```

  - 动态加载：当 dbType 为动态分表情况，在执行SQL动态得到库源

  ```java
  /**
   * 数据库连接获取
   *
   * @return
   */
  private String getDBUrl() {
      StringBuffer currentDbPathSb = new StringBuffer(this.dbPath);
      switch (this.dbType) {
          case SqliteConfig.DB_TYPE_BY_MINUTE:
              currentDbPathSb.append(SqliteUtils.nowFormatStr("yyyyMMddHHmm")).append(".db");
              break;
          case SqliteConfig.DB_TYPE_BY_HOUR:
              currentDbPathSb.append(SqliteUtils.nowFormatStr("yyyyMMddHH")).append(".db");
              break;
          case SqliteConfig.DB_TYPE_BY_DAY:
              currentDbPathSb.append(SqliteUtils.nowFormatStr("yyyyMMdd")).append(".db");
              break;
          case SqliteConfig.DB_TYPE_BY_MOUTH:
              currentDbPathSb.append(SqliteUtils.nowFormatStr("yyyyMM")).append(".db");
              break;
          case SqliteConfig.DB_TYPE_BY_YEAR:
              currentDbPathSb.append(SqliteUtils.nowFormatStr("yyyy")).append(".db");
              break;
          default:
              break;
      }
      String currentDbPath = currentDbPathSb.toString();
      String JDBC_URL = null;
      if (SqliteUtils.isWindows()) {
          JDBC_URL = "jdbc:sqlite:/" + currentDbPath.toLowerCase();
      } else {
          JDBC_URL = "jdbc:sqlite:/" + currentDbPath;
      }
      return JDBC_URL;
  }
  ```

### 分表

1. 定义分表标记：分表注解，编辑考虑定义属性注解，可以和非表字段标识注解同用，决定要不要把分表标识规则存进数据库，可以对表实体类二次继承多个，写死分表字段，达到静态分表

  ```java
  /**
   * 分表属性注解
   * @author 欧阳洁
   */
  @java.lang.annotation.Target(value = {java.lang.annotation.ElementType.FIELD})
  @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
  public @interface SqliteTableSplit {
      /**
       * 后缀链接字符
       * @return
       */
      String joinStr() default "_";
  }
  ```

2. 记录分表属性：服务注入时候检测并记录分表属性（Field），在生成SQL的时候动态获取表名

  ```java
  /**
   * 根据注解获取表名
   * @param target
   * @param needCreateTable 是否需要判断并自动创建表
   * @return
   */
  public String getTableName(T target,boolean needCreateTable) {
      if (SqliteUtils.isBlank(this.tableName)) {
          this.tableName = this.getTableNameForClass(this.targetClass);
      }
      //启用分表功能后，动态获取表名，并生成建表SQL
      if (null != this.tableSplitField) {
          String fieldValue = (String) this.readField(this.tableSplitField, target);
          if (!SqliteUtils.isBlank(fieldValue)) {
              String joinStr = "_";
              SqliteTableSplit splitAnnotation = this.tableSplitField.getAnnotation(SqliteTableSplit.class);
              if (null != splitAnnotation) {
                  joinStr = splitAnnotation.joinStr();
              }
              String currentTableName = new StringBuffer(this.tableName).append(joinStr).append(fieldValue).toString();
              if(needCreateTable) {
                  String creatTableSql = this.createTableSql(currentTableName);
                  target.setNeedCreateBefSql(creatTableSql);
              }
              return currentTableName;
          }
      }
      return this.tableName;
  }
  ```

3. 插入的时候表存在判断，并在不存在时候自动建表

  ```java
  /**
   * 插入
   * @param entity
   * @return
   */
  public int insert(T entity) {
      this.sqlHelper.createInsert(entity);
      if(!SqliteUtils.isBlank(entity.getNeedCreateBefSql())){
          //插入数据之前判断是否需要建表
          this.sqliteHelper.execute(entity.getNeedCreateBefSql());
      }
      return this.sqliteHelper.insert(entity.getCurrentSql(), entity.getCurrentParam());
  }
  ```

## 单元测试

### 分库测试

```java
/**
 * 测试分库表对应实体类
 *
 * @author 欧阳洁
 * @create 2017-09-30 9:44
 **/
@SqliteTable(name = "t_test_splite_sqlite",dbPath = "database/t_test_splite_",dbType = SqliteConfig.DB_TYPE_BY_DAY)
public class TestSqliteSplit extends SqliteBaseEntity {
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
    @SqliteColumn(name = "publish_time")
    private String publishTime;

    ...
}
```

```java
@Test
public void test5() {
    TestSqliteSplitService sqliteService = new TestSqliteSplitService();//没有使用spring注入，暂时自己构建
    TestSqliteSplit entity = new TestSqliteSplit();
    entity.setName("test1");
    entity.setAuthor("petter");
    entity.setArticle("article1");
    entity.setCreateTime(SqliteUtils.getStringDate());
    sqliteService.insert(entity);
    entity.setName("title2");
    entity.setAuthor("bob");
    entity.setArticle("article2");
    entity.setCreateTime(SqliteUtils.getStringDate());
    sqliteService.insert(entity);

    TestSqliteSplit queryEntity = new TestSqliteSplit();
    sqliteService.query(queryEntity);
}
```

输出结果：

>执行非查询语句==> create table if not exists t_test_splite_sqlite(id integer  primary key autoincrement not null,name char(100)  not null,author char(20)  not null,article text ,create_time char(20)  not null)<br/>
执行非查询语句影响行数==> 0<br/>
执行非查询语句==> INSERT INTO t_test_splite_sqlite(name,author,article,create_time)values(?,?,?,?)<br/>
执行非查询语句影响行数==> 1<br/>
执行非查询语句==> INSERT INTO t_test_splite_sqlite(name,author,article,create_time)values(?,?,?,?)<br/>
执行非查询语句影响行数==> 1<br/>
执行查询语句==> SELECT * FROM t_test_splite_sqlite WHERE 1=1 <br/>
执行查询语句结果==> [{"id":1,"name":"test1","author":"petter","article":"article1","createTime":"2018-04-01 16:43:51"},{"id":2,"name":"title2","author":"bob","article":"article2","createTime":"2018-04-01 16:43:51"}]

### 分表测试

```java
/**
 * 测试分表对应实体类
 *
 * @author 欧阳洁
 * @create 2017-09-30 9:44
 **/
@SqliteTable(name = "t_test_split_table")
public class TestTableSplit extends SqliteBaseEntity {
    /**
     * 主键
     */
    @SqliteID
    private Integer id;
    /**
     * 类型，分表字段
     */
    @SqliteColumn(notNull = true)
    @SqliteTableSplit
    private String type;
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
    @SqliteColumn(name = "publish_time")
    private String publishTime;
    ...
}
```

```java
@Test
public void test2() {
    TestTableSplitService sqliteService = new TestTableSplitService();//没有使用spring注入，暂时自己构建
    TestTableSplit entity = new TestTableSplit();
    entity.setType("t1");//分表字段

    entity.setName("test1-1");
    entity.setAuthor("petter");
    entity.setArticle("article1");
    entity.setCreateTime(SqliteUtils.getStringDate());
    sqliteService.insert(entity);
    entity.setName("test1-2");
    entity.setAuthor("bob");
    entity.setArticle("article2");
    entity.setCreateTime(SqliteUtils.getStringDate());
    sqliteService.insert(entity);


    entity.setType("t2");//分表字段

    entity.setName("test2-1");
    entity.setAuthor("petter");
    entity.setArticle("article1");
    entity.setCreateTime(SqliteUtils.getStringDate());
    sqliteService.insert(entity);
    entity.setName("test2-2");
    entity.setAuthor("bob");
    entity.setArticle("article2");
    entity.setCreateTime(SqliteUtils.getStringDate());
    sqliteService.insert(entity);

    TestTableSplit queryEntity = new TestTableSplit();
    sqliteService.query(queryEntity);
    queryEntity.setType("t1");
    sqliteService.query(queryEntity);
    queryEntity.setType("t2");
    sqliteService.query(queryEntity);
    queryEntity.setType("t3");
    sqliteService.query(queryEntity);
}
```

输出结果：

>执行非查询语句==> create table if not exists t_test_split_table(id integer  primary key autoincrement not null,type char(20)  not null,name char(100)  not null,author char(20)  not null,article text ,create_time char(20)  not null)<br/>
执行非查询语句影响行数==> 0<br/>
执行非查询语句==> create table if not exists t_test_split_table_t1(id integer  primary key autoincrement not null,type char(20)  not null,name char(100)  not null,author char(20)  not null,article text ,create_time char(20)  not null)<br/>
执行非查询语句影响行数==> 0<br/>
执行非查询语句==> INSERT INTO t_test_split_table_t1(type,name,author,article,create_time)values(?,?,?,?,?)<br/>
执行非查询语句影响行数==> 1<br/>
执行非查询语句==> create table if not exists t_test_split_table_t1(id integer  primary key autoincrement not null,type char(20)  not null,name char(100)  not null,author char(20)  not null,article text ,create_time char(20)  not null)<br/>
执行非查询语句影响行数==> 0<br/>
执行非查询语句==> INSERT INTO t_test_split_table_t1(type,name,author,article,create_time)values(?,?,?,?,?)<br/>
执行非查询语句影响行数==> 1<br/>
执行非查询语句==> create table if not exists t_test_split_table_t2(id integer  primary key autoincrement not null,type char(20)  not null,name char(100)  not null,author char(20)  not null,article text ,create_time char(20)  not null)<br/>
执行非查询语句影响行数==> 0<br/>
执行非查询语句==> INSERT INTO t_test_split_table_t2(type,name,author,article,create_time)values(?,?,?,?,?)<br/>
执行非查询语句影响行数==> 1<br/>
执行非查询语句==> create table if not exists t_test_split_table_t2(id integer  primary key autoincrement not null,type char(20)  not null,name char(100)  not null,author char(20)  not null,article text ,create_time char(20)  not null)<br/>
执行非查询语句影响行数==> 0<br/>
执行非查询语句==> INSERT INTO t_test_split_table_t2(type,name,author,article,create_time)values(?,?,?,?,?)<br/>
执行非查询语句影响行数==> 1<br/>
执行查询语句==> SELECT * FROM t_test_split_table WHERE 1=1 <br/>
执行查询语句结果==> []<br/>
执行查询语句==> SELECT * FROM t_test_split_table_t1 WHERE 1=1  and type=?<br/>
执行查询语句结果==> [{"id":1,"type":"t1","name":"test1-1","author":"petter","article":"article1","createTime":"2018-04-01 16:48:17"},{"id":2,"type":"t1","name":"test1-2","author":"bob","article":"article2","createTime":"2018-04-01 16:48:17"}]<br/>
执行查询语句==> SELECT * FROM t_test_split_table_t2 WHERE 1=1  and type=?<br/>
执行查询语句结果==> [{"id":1,"type":"t2","name":"test2-1","author":"petter","article":"article1","createTime":"2018-04-01 16:48:17"},{"id":2,"type":"t2","name":"test2-2","author":"bob","article":"article2","createTime":"2018-04-01 16:48:17"}]<br/>
执行查询语句==> SELECT * FROM t_test_split_table_t3 WHERE 1=1  and type=?<br/>
org.sqlite.SQLiteException: [SQLITE_ERROR] SQL error or missing database (no such table: t_test_split_table_t3)
