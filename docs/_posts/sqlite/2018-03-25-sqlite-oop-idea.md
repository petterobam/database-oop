---
layout: post
title: "Sqlite数据库面向对象封装思路"
description: "Sqlite数据库的面向对象封装"
categories: [Sqlite, 面向对象封装, Java]
tags: [Sqlite,实现思路]
redirect_from:
  - /2018/03/25/
---

众所周知，我们与数据库沟通用的是数据库的语言SQL，就像我们编程的语言是Java、C#、JavaScript一样，不同的语言是不能直接沟通的，需要一个翻译层，所以这一层就是面向数据库封装，而这一层jdk里面早就实现了（jdbc），本文个人以最接近的实际数据库的文件数据库sqlite（org.sqlite.JDBC）来做代码实验。

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
```
```xml
<!-- maven:sqlite数据库操作 -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>${sqlite.version}</version> 
</dependency>
```

```sql
#java所用的sql语言也是通用的sql预处理语言，通过占位符（?）接收各种类型的变量，可以说即安全又优美。

[oracle语法]：select * from table where 1=1 and column1=? and column2>? or column3 like '%'||?||'%' 
```

当前的很多框架已经完美的封装了数据库，mybatis、hibernate这些对数据库的语言做了非常灵活的处理，也有一套各自自己的语法。但是，个人认为xml文件还是属于配置文件，如果能实现零配置的面向对象封装就好了。面向对象封装需要达到的程度就是通过一个对象的信息可以直接自动的产生数据库相关的sql，然后将对象的变量属性值对应sql的占位符，并且在基类上实现基本的功能。

```java
public abstract class SqliteBaseDao<T extends SqliteBaseEntity> {
    public int insert(String sql){...}
    public int update(String sql){...}
    public int delete(String sql){...}
    public List<T> query(String sql){...}
    // 通过对象信息实现增删查改
    public int insert(T entity){...}
    public int update(T entity){...}
    public int delete(T entity){...}
    public int deleteById(Object id){...}
    public List<T> query(T entity){...}
    public T queryById(Object id){...}
    // 自定义SQL的实现，模仿mybatis的xml动态SQL，但是不是用xml文件的方式
    public List<T> excuteQuery(T entity) {...}
    public List<T> excuteQuery(Object... params) {...}
    public int excute(T entity) {...}
    public int excute(Object... params) {...}
}
```

### 一个类信息收集器

想要做到面向对象的数据库封装，就要把数据的最小单元和程序的最小单元关联起来，这两个单元都是独立的整体，再分割就会出现信息失真或不对称的情况。数据的最小单元就是表的单行记录，程序最小单元是实体类的单个对象，但是数据不一定有，对象需要实例化，需要运行起来才能提供信息。因此，这个最小单元要静态化，那就是数据库的表结构和实体类的信息关联起来。

表结构能提供的信息有表名、表列名、列属性（类型、是否为空、默认值），实体类能提供的信息有类名、类变量名、类变量名属性（变量类型），似乎大部分都能对应上，但是属性部分还不能对应上，比如是否为空、默认值，而且为了良好的Java编码规范，有些东西和数据库的一些信息是不能完全对应的，比如通常java的命名规范一般不允许带下划线，而是用驼峰命名，而数据库的表和字段确经常用下划线分割单词（主要由于很多数据库不区分表名和字段名的大小写，甚至有些数据库不支持表名和字段名的大写）这就非常尴尬了，所以，这种差异化的对应就需要配置。并且，为了增加扩展性，程序单元的实体类的信息往往要比数据单元的表结构信息多一些，比如可能会有非表字段的属性（占位符中非表字段的传入属性和结果集中非表字段的接收属性），

ibatis、hibernate就用到了这些，但是用的是xml文件，而且一旦配置出错还非常容易影响编译，并且不容易定位问题。mybatis+spring的xml和java注解让系统变得非常灵活，但是依然不能彻底的去掉xml文件，而且xml中如果出现一个小小的分号或空格都有可能引发问题，要是只用java注解就能解决掉的话就完美了。
    
综上，想要实现这些适配，需要几个基础的注解：表名注解、主键注解、表字段注解、非表字段标识注解、自定义SQL注解（多种）。

```java
/**
 * 表名注解类
 * @author 欧阳洁
 * @create 2017-09-30 11:16
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqliteTable {
    String name() default "";
}
```
```java
/**
 * Sqlite的ID注解类
 * @author 欧阳洁
 * @create 2017-09-30 11:20
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqliteID {
    /* 主键列名，默认空*/
    String name() default "";
    /* 主键默认类型，默认integer类型 */
    String type() default "integer";
    /* 主键是否自增长，默认是true */
    boolean autoincrement() default true;
}
```
```java
/**
 * Sqlite的表(列）字段注解类
 * @author 欧阳洁
 * @create 2017-09-30 11:20
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqliteColumn {
    /* 列名，默认空 */
    String name() default "";
    /* 主键默认类型，默认最大20位长度的字符串 */
    String type() default "char(20)";
    /* 主键是否自增长，默认是true */
    boolean notNull() default true;
}
```
```java
/**
 * 非表字段注解
 * @author 欧阳洁
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqliteTransient {
}
```
```java
/**
 * 自定义SQL注解类
 * @author 欧阳洁
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqliteSql {
    /* 自定义的Sql语句，带占位符的Sql */
    String sql();
    /* 占位符顺序对应的参数顺序，默认不带参数 */
    String[] params() default "";
}
```
在上述的这些注解中，主键注解、表字段注解、非表字段标识注解其实都可以归为表字段注解，只要稍微添加某些属性就可以实现其他两个注解的功能，但是为了使用的方便性和程序的可读性，我牺牲了顶层代码的简易性，对这三个注解都做了区分和处理。而自定义SQL注解应该类似于mybatis里面的xml标签，@SqliteSql这个注解类只是实现了基本的（select、insert、delete、update）标签，搭配SqliteBaseDao里面的基本方法就可以轻松实现简单的mapper功能，而那些复杂的if else、choose when标签则需要例外些其他的辅助标签搭配使用，比如需求最多的动态where，可以定义类似于@SqliteSqlWhereIf，这样就可以简单的实现if标签，并且通过testId和parentTestId还可以实现if标签的嵌套功能，然后利用java8的新特性@Repeatable实现重复注解，或者直接定义一个集合注解（这个看着不是特别美观）就可轻松的实现判断条件非嵌套动态生成各种sql。
```java
public abstract class SqliteBaseDao<T extends SqliteBaseEntity> {
    ...
    // 自定义SQL的实现，模仿mybatis的xml动态SQL，但是不是用xml文件的方式
    public List<T> excuteQuery(T entity) {...}  //对应select标签
    public List<T> excuteQuery(Object... params) {...}  //对应select标签
    public int excute(T entity) {...}   //对应insert、update、delete标签
    public int excute(Object... params) {...}   //对应insert、update、delete标签
}
```
```java
/**
 * 自定义SQL注解类，实现SqliteSqlWhereIf的重复注解
 * @author 欧阳洁
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqliteSqlWhereIfs {
    SqliteSqlWhereIf[] value();
}
/**
 * 自定义SQL注解类，条件判断注解，用于生成动态SQL
 * @author 欧阳洁
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Repeatable(SqliteSqlWhereIfs.class)
public @interface SqliteSqlWhereIf {
    /* 判断条件标识ID */
    int testId();
    /* 所属层级，默认0，最外层 */
    int parentTestId() default 0;
    /* 判断条件类型，==、>、<、>=、<=、eq、ne */
    String testType() default "eq";
    /* 判断字段名 */
    String testName();
    /* 符合条件的值 */
    String[] testTrueValue();
    /* 符合条件对应的动态SQL */
    String[] testTrueSql() default "";
    /* 占位符顺序对应的参数顺序，默认不带参数 */
    String[] params() default "";
}

例如：
@SqliteSql(sql = "select * from this.tableName where 1=1")
@SqliteSqlWhereIf(testId=1,testType="eq",testName="searchType",testTrueValue={"2"},testTrueSql=" and create_time>datetime('now') ")
@SqliteSqlWhereIf(testId=2,testType="ne",testName="name",testTrueValue={""},testTrueSql=" and name like '%'||?||'%' ",parentTestId=1,params={"name"})
@SqliteSqlWhereIf(testId=3,testType="eq",testName="author",testTrueValue={""},testTrueSql=" and author=? or name=? ",parentTestId=1,params={"author","author"})
public List<XXX> method1(XXX entity){ return super.excuteQuery(entity); }
```

于是，类信息收集器能做成静态的吗，不能。因为每个表的信息不同，就会对应不同的类信息，如果我们做成静态的，那么就会产生很多无意义的重复代码，我们要用对象存储他，并且是一个表用一个对象去存储。这样，我们就可以准确定义一个类信息收集器的收集内容了，如下所示：

```java
/**
 * 实体类T的信息收集器
 * Sqlite的Sql语句生成器
 * @author 欧阳洁
 * @create 2017-09-30 10:56
 */
public class SqliteSqlHelper<T extends SqliteBaseEntity> {
    private Class<T> targetClass;//实体类
    private String tableName;//表名
    private String idName;//主键名
    private Field idField;//主键变量属性
    List<Field> columnFields;//表列名对应的变量属性集合
    Map<String, String> columnMap;//表列名和字段映射map

    /**
     * 构造函数
     * @param targetClass
     */
    public SqliteSqlHelper(Class<T> targetClass) {
        this.tableName = this.getTableNameForClass(targetClass);
        this.targetClass = targetClass;
        this.columnFields = new Vector<Field>();
        this.columnMap = new HashMap<String, String>();
        this.getColumnFields();//表列名对应的变量属性相关信息，包括表列名和字段映射
    }
    ...
}
```

### 一个SQL生成器

和数据库沟通就需要使用相应的SQL语言，而通用的关系型数据库（oracle、mysql、sql server等），甚至是非关系型的数据库NOSQL（elasticsearch、mongodb等），绝大部分交互无非就是增删查改，而SQL的增删查改语言几乎是通用的。
```sql
# 建表
    create table t_test_table(
        id integer primary key autoincrement not null,
        name char(100) not null,
        author char(100) not null,
        article text,
        create_time char(20) not null
    );
# 新增
    insert into t_test_table(name,author, article,create_time)
        values ("test11","petter","article1","2017-09-29 17:01:22");
# 查询
    select * from t_test_table;
    select * from t_test_table where id=1;
# 修改
    update t_test_table
        set name = "test11_修改", article = "article1_修改", create_time = "2017-09-29 17:01:27"
            where id=1;
# 删除
    delete from t_test_table where id = 1;
```
这里多了个建表SQL，这里是为了实现自动化部署数据库用的，相当于程序驱动数据，让更多主动层面转移到程序代码这一层。当我们为我们的业务新增一个数据实体时候，程序就能自动的去生成规范的数据库，免去了繁琐的手动创建以及容易弄错的字段类型方面的细节，如果封装的好的话，迭代更新就能免去SQL脚本了。扩展一下的话，在表名注解里面添加数据库的链接属性还可以实现平滑分库的功能（这个可以，对于sqlite这种小型数据库，如果能实现分库的话，那就不再是小型数据库了，当然本文的例子还是以单数据库为例）。

而且建表的sql生成比较简单，只要单独的根据实体类的信息就能直接生成，如下：

```java
# SqliteSqlHelper.java
public class SqliteSqlHelper<T extends SqliteBaseEntity> {
    /**
     * 创建创建表的sql语句
     */
    public String createTableSql() {
        StringBuffer sql = new StringBuffer("create table if not exists ");
        sql.append(this.tableName).append("(");
        boolean useCumma = false;
        for (Field field : this.columnFields) {
            if (useCumma) {
                sql.append(",");//第一次不用逗号
            }else{
                useCumma = true;
            }
            
            String columnName = field.getName();
            String columnType = "char(20)";
            String notNull = "";
            SqliteID id = field.getAnnotation(SqliteID.class);
            if (id != null) {
                columnName = SqliteUtils.isBlank(id.name()) ? field.getName() : id.name();
                columnType = id.type();
                //主键默认不为空，如果自增长默认自增长
                notNull = id.autoincrement() ? " primary key autoincrement not null" : " primary key not null";
            } else {
                SqliteColumn column = field.getAnnotation(SqliteColumn.class);
                if (null != column) {
                    columnName = SqliteUtils.isBlank(column.name()) ? field.getName() : column.name();
                    columnType = column.type();
                    notNull = column.notNull() ? " not null" : "";
                }
            }
            sql.append(columnName.toLowerCase()).append(" ").append(columnType.toLowerCase()).append(" ").append(notNull);
        }
        sql.append(")");
        return sql.toString();
    }
    ...
}

# SqliteBaseDao.java
public abstract class SqliteBaseDao<T extends SqliteBaseEntity> {
    private String tableName;
    private Class entityClazz;
    private SqliteSqlHelper sqlHelper;

    public SqliteBaseDao(Class<T> entityClass) {
        this.sqlHelper = new SqliteSqlHelper(entityClass);
        this.tableName = this.sqlHelper.getTableName();
        this.entityClazz = entityClass;
        //调用该方法就能在使用时检查和创建表，可以通过配置信息判断是否执行，达到开关控制的效果
        this.existOrCreateTable();
    }
    /**
     * 检查表是否存在，不存在则创建
     */
    public void existOrCreateTable() {
        String sql = this.sqlHelper.createTableSql();
        SqliteHelper.execute(sql);
    }
    ...
}
```
同理，增删查改，也是通过类的信息进行sql的组装，但是却依赖调用入参实现动态sql生成。就以查询为例，默认的动态sql生成，基于传入的实体类对象里面的值，一般为空的不做处理，不为空的条件用and做连接起来形成动态的查询SQL。
```java
# SqliteSqlHelper.java
public class SqliteSqlHelper<T extends SqliteBaseEntity> {
    /**
     * 创建查询语句
     */
    public void createSelect(T target) {
        List<Object> param = new Vector<Object>();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT * FROM ").append(this.tableName);
        finishWhereOfAnd(sqlBuffer, param, target);
    
        target.setCurrentSql(sqlBuffer.toString());
        target.setCurrentParam(param);
    }
    /**
     * 补全用and连接的sql语句
     * @param sqlBuffer
     * @param param
     * @param target
     */
    private void finishWhereOfAnd(StringBuffer sqlBuffer, List<Object> param, T target) {
        sqlBuffer.append(" WHERE 1=1 ");
        Object idValue = null;
        if (null != this.idField) {
            idValue = readField(this.idField, target);
        }
        if (idValue != null) {
            sqlBuffer.append(" and ").append(this.idName).append("=?");
            param.add(idValue);
        } else {
            for (Field field : this.columnFields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    Object currentValue = readField(field, target);
                    if (null != currentValue && !SqliteUtils.equals(this.idName, field.getName())) {
                        String columnName = field.getName();
                        SqliteColumn sqliteColumn = field.getAnnotation(SqliteColumn.class);
                        if (null != sqliteColumn) {
                            columnName = SqliteUtils.isBlank(sqliteColumn.name()) ? field.getName() : sqliteColumn.name();
                        }
                        sqlBuffer.append(" and ").append(columnName.toLowerCase()).append("=?");
                        param.add(currentValue);
                    }
                }
            }
        }
    }
    ...
}
```
对于自定义SQL的获取和动态组装生成，它相对其他类的SQL就要复杂一些，因为这个注解不是在实体类中，而是在Dao类中，而且是一种用于方法的注解。想要知道如何获取这类注解的信息，就需要了解java程序的一些运行原理。

> 线程栈区的方法栈：简而言之，就是每个运行中的程序会有一个线程，而调用的方法、调用方法调用的方法、调用方法调用的方法调用的方法...他们会形成该线程的方法栈，进行中的方法都会放入这个栈中，这样我们就可以在运行时候获取到对应的方法了。

然而，我们要使得注解和编码更加简洁易懂，我们需要把这部分获取的代码放到Dao类继承的基类（SqliteBaseDao）中，通过基类获取子类的某个方法的注解信息。虽然基类获取子类方法信息与常规程序设计有较大差异，但是只有这样才能实现优美的封装。下面为自定义查询部分代码（增删改同理）：
```java
# SqliteSqlHelper.java
public class SqliteSqlHelper<T extends SqliteBaseEntity> {
    /**
     * 创建自定义查询语句
     */
    public void convertSelfSql(StackTraceElement daoMethodInfo, T target) {
        if (null == daoMethodInfo) {//为空用默认的查询语句
            createSelect(target);
            return;
        }
        Method method = getMethod(daoMethodInfo.getClassName(), daoMethodInfo.getMethodName(), target.getClass());
        if (null == method) {//为空用默认的查询语句
            createSelect(target);
            return;
        }
        SqliteSql sqliteSql = method.getAnnotation(SqliteSql.class);
        if (null != sqliteSql) {
            List<Object> param = new Vector<Object>();
            String[] paramNameArr = sqliteSql.params();
            if (null != paramNameArr && paramNameArr.length > 0) {
                Field[] fieldArray = this.targetClass.getDeclaredFields();
                for (String paramName : paramNameArr) {
                    Object value = null;
                    for (Field field : fieldArray) {
                        if (paramName.equalsIgnoreCase(field.getName())) {
                            value = readField(field, target);
                            break;
                        }
                    }
                    param.add(value);
                }

            }
            String sql = SqliteUtils.replace(sqliteSql.sql(), "this.tableName", this.tableName);
            //TODO 此处可以读取自定义SQL的辅助注解，像上面提到的SqliteSqlWhereIf注解，实现动态SQL
            target.setCurrentSql(sql);
            target.setCurrentParam(param);
        }
    }
    /**
     * 创建自定义查询语句，参数随机
     */
    public String convertSelfSql(StackTraceElement daoMethodInfo, Object... params) {
        if (null == daoMethodInfo) {//为空不做处理
            System.out.println("未获取到自定义的语句！");
            return null;
        }
        Class<?>[] classArr = null;
        if (null != params && params.length > 0) {
            classArr = new Class<?>[params.length];
            for (int i = 0; i < params.length; i++) {
                classArr[i] = params[i].getClass();
            }
        }
        Method method = getMethod(daoMethodInfo.getClassName(), daoMethodInfo.getMethodName(), classArr);
        if (null == method) {//为空不做处理
            System.out.println("未获取到自定义的语句！");
            return null;
        }
        SqliteSql sqliteSql = method.getAnnotation(SqliteSql.class);
        //TODO 此处可以读取自定义SQL的辅助注解，像上面提到的SqliteSqlWhereIf注解，实现动态SQL
        String sql = SqliteUtils.replace(sqliteSql.sql(), "this.tableName", this.tableName);
        return sql;
    }
    ...
}

# SqliteBaseDao.java
public abstract class SqliteBaseDao<T extends SqliteBaseEntity> {
    /**
     * 通过自定义注解执行查询的语句
     */
    public List<T> excuteQuery(T entity) {
        //[0]为getStackTrace方法，[1]当前的excuteQuery方法，[2]为调用excuteQuery方法的方法
        StackTraceElement parrentMethodInfo = Thread.currentThread().getStackTrace()[2];
        this.sqlHelper.convertSelfSql(parrentMethodInfo, entity);
        String jsonStr = SqliteHelper.query(entity.getCurrentSql(), entity.getCurrentParam(), this.getColumMap());
        if (jsonStr == null) return null;
        List<T> result = SqliteUtils.getInstance(jsonStr, entity.getClass());
        if (SqliteUtils.isNotEmpty(result)) {
            return result;
        } else {
            return null;
        }
    }
    /**
     * 通过自定义注解执行查询的语句
     */
    public List<T> excuteQuery(Object... params) {
        //[0]为getStackTrace方法，[1]当前的excuteQuery方法，[2]为调用excuteQuery方法的方法
        StackTraceElement parrentMethodInfo = Thread.currentThread().getStackTrace()[2];
        String sql = this.sqlHelper.convertSelfSql(parrentMethodInfo, params);
        List<Object> paramList = new Vector<Object>();
        if (null != params && params.length > 0) {
            for (Object o : params) {
                paramList.add(o);
            }
        }
        String jsonStr = SqliteHelper.query(sql, paramList, this.getColumMap());
        if (jsonStr == null) return null;
        List<T> result = SqliteUtils.getInstance(jsonStr, this.entityClazz);
        if (SqliteUtils.isNotEmpty(result)) {
            return result;
        } else {
            return null;
        }
    }
    ...    
}

# 实体DAO中使用如下
public class TestTableDao extends SqliteBaseDao<TestTable> {
    @SqliteSql(sql = "select t.create_time publish_time,t.* from this.tableName t where name like '%'||?||'%'", params = {"name"})
    public List<TestTable> getByName(TestTable entity) {
        //List<T> super.excuteQuery(T entity)，通过params上的参数顺序在entity中获取，并依次填充占位符
        return super.excuteQuery(entity);
    }
    @SqliteSql(sql = "select * from this.tableName where name like '%'||?||'%' or id=?")
    public List<TestTable> getByNameOrId(String name, Integer id) {
        //List<T> super.excuteQuery(Object... params)，这里的参数顺序对应自定义的SQL的占位符顺序
        return super.excuteQuery(name, id);
    }
    ...
}
```
另外，自定义SQL注解对于像oracle这样的存储过程和函数占比较大的数据库，调用存储过程可是无往不利，不过就不知道占位符返回集合的存储过程或函数，方不方便数据的填充组装（TODO）。


### 一个数据组装器

上面说到数据的组装，这也是比较关键的一个环节，只有这一步OK了，这个封装才算OK。总所周知，数据库表字段类型五花八门，想要很好的和java的类型对应上，确实要花费一点精力。这里为了节省时间和篇幅，个人取了个巧，将数据统一转化为JSON，然后统一通过json转对象。从代码上省去了复杂的类型对应，也摒弃了一个个取值，然后通过反射对应写值到对象属性里面的不安全操作及不确定异常，然而代价可能会对转化效率有影响。所以，关键就是需要一种不影响速度的json转化，fastjson基本满足条件。

```java
# SqliteHelper.java
/**
 * 根据结果集返回数据json
 */
public static String getDataJson(ResultSet rs, Map<String, String> columnMap) throws SQLException {
    String[] nameArr = null;
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    int rows = 1;
    while (rs.next()) {
        if (rows++ == 1) {
            nameArr = getNameArr(rs);// 获取列名
        }

        Map<String, Object> one = new LinkedHashMap<String, Object>();
        for (int i = 0; i < nameArr.length; i++) {
            String nameKey = null == columnMap ? nameArr[i] : columnMap.get(nameArr[i]);
            nameKey = null == nameKey ? nameArr[i] : nameKey;
            one.put(nameKey, rs.getObject(i + 1));
        }
        result.add(one);
    }
    String dataStr = SqliteUtils.getJsonList(result);
    System.out.println("执行查询语句结果==> " + dataStr);
    return dataStr;
}
```
```java
# SqliteUtils.java
/**
 * 转换 List<Object> 为 Json字符串
 */
public static String getJsonList(List list) {
    if (list == null) return "[]";
    StringBuffer jsonBuf = new StringBuffer("[");
    boolean flag = false;
    for (Object obj : list) {
        if (flag) {
            jsonBuf.append(",");
        }else{
            flag = true;
        }
        String jsonOne = getJsonObject(obj);
        jsonBuf.append(jsonOne);
    }
    jsonBuf.append("]");
    return jsonBuf.toString();
}
public static String getJsonObject(Object object) {
    try {
        String json = SqliteUtils.toString(JSONObject.fromObject(object));
        return json;
    } catch (Exception e) {
        e.printStackTrace();
        return "{}";
    }
}
/**
 * json字符串转对象，转对集合
 * @param jsonString json字符串
 * @param clazz 对象class，如果要转化为List<ObjectA> 传入ObjectA.class
 * @return Object [返回类型说明]
 * @throws throws [违例类型] [违例说明]
 * @see [类、类#方法、类#成员]T
 */
public static <T> T getInstance(String jsonString, Class clazz) {
    if (SqliteUtils.isBlank(jsonString)) return null;
    if ("[]".equals(SqliteUtils.trim(jsonString))) return null;
    Object json = new JSONTokener(jsonString).nextValue();//字符串 转json 类型对象
    if (json instanceof JSONObject) {  //这种   {"XXX": "101",{},[]} 对象
        return (T) SqliteJsonMapper.nonDefaultMapper().fromJson(json.toString(), clazz);
    } else {
        //如果集合不为null则是返回成功,则需要修改数据的时间
        //创建转换json的需要转换的集合类型   [{},{}]
        JavaType javaType = SqliteJsonMapper.nonDefaultMapper().contructCollectionType(List.class, clazz);
        return SqliteJsonMapper.nonDefaultMapper().fromJson(jsonString, javaType);//反序列化复杂List
    }
}
```