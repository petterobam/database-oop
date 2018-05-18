---
layout: post
title: "Sqlite连接池和批量操作实现"
author: "petterobam"
description: "Sqlite连接池和批量操作实现"
categories: [Sqlite, 面向对象封装, Java]
tags: [Sqlite,连接池,批量]
redirect_from:
  - /2018/05/18/
---

## 前情提要

针对 Sqlite-OOP 最近重新扩展列出了一些要封装的功能，就对最近实现的连接池和批量操作做一些总结。

1. ~~自动建表~~
1. ~~增删查改等基本功能~~
1. ~~数量查询功能~~
1. ~~分表分库~~
1. ~~Sqlite控制台基础功能类~~
1. ~~自定义配置加载~~
1. **批量操作（事务）**
1. 缓存库基础功能类实现
1. 缓存库和文件库混用实现
1. **连接池实现**
1. 查询分页功能实现
1. 备份工具实现
1. 密码连接实现
1. 带密码数据库生成实现

## 连接池

连接池，众所周知，做过有数据库的应用的程序员都应该听过这个名词。鉴于数据库是一个独立的系统，应用要想和它交互就需要建立连接，但是就这个过程慢成狗（相比其他操作）。因此，在抗压、响应速度等方面都因为这个瓶颈，相比于其他更慢的语言（比如python）都没有什么大的优势，就算虚拟机再优化提升也不能提高数据库的效率。所以知道了瓶颈，就能发现问题，解决最慢的部分就是必须要去考虑的，那就是连接。连接最慢的不是通过连接的交互过程，而是建立连接的过程。

解决方案：预先建好一堆连接屯在那里，等到想用的时候拿一个出来直接用，我们称这堆连接和其相关的机制是一个连接池（Connection Pool）。

### 自定义连接对象包装盒

为了便于管理，需要在容纳原生连接对象的同时记录连接建立时的一些额外信息，以便于连接的管理。至于为什么不用继承，那是为了后期扩展能够更方便、灵活。连接池的实现可以想象成连接对象的生产和消费的市场，每个连接像是一个产品，预先生产出来后，用于应用中不同的地方去消费。而市场的良好状态就是供需平衡，不能频繁出现供不应求和供应过剩的情况。

```
/**
 * Sqlite连接自定义封装类
 * @author 欧阳洁
 * @since 2018-05-02 13:26
 */
public class SqliteBaseConnection {
    private String uri;//数据库连接
    private long createTime;// 时间戳
    private Connection connection;// 链接对象

    /**
     * 重置连接uri，并且同时会刷新连接对象
     * @param uri
     */
    public boolean resetUri(String uri){
        try {
            this.uri = uri;
            this.createTime = SqliteUtils.getNowStamp();
            if(SqliteBaseConnectionFactory.USE_SELF_INNER_CONFIG){
                this.connection = DriverManager.getConnection(uri, SqliteConfig.getConfig().toProperties());
            }else {
                this.connection = DriverManager.getConnection(uri);
            }
            return true;
        } catch (SQLException e) {
            SqliteLogUtils.error("[resetUri]重置连接对象失败！",e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 刷新连接对象
     * @return
     */
    public boolean refreshConnection(){
        try {
            this.createTime = SqliteUtils.getNowStamp();
            if(SqliteBaseConnectionFactory.USE_SELF_INNER_CONFIG){
                this.connection = DriverManager.getConnection(uri, SqliteConfig.getConfig().toProperties());
            }else {
                this.connection = DriverManager.getConnection(uri);
            }
            return true;
        } catch (SQLException e) {
            SqliteLogUtils.error("[refreshConnection]重新建立连接对象失败！",e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取连接
     * @return
     */
    public Connection getConnection() {
        if(null == this.connection){
            try {
                if(SqliteBaseConnectionFactory.USE_SELF_INNER_CONFIG){
                    this.connection = DriverManager.getConnection(uri, SqliteConfig.getConfig().toProperties());
                }else {
                    this.connection = DriverManager.getConnection(uri);
                }
            } catch (SQLException e) {
                SqliteLogUtils.error("连接建立失败！",e);
                e.printStackTrace();
            }
        }
        return connection;
    }

    ...
}
```

### 连接工厂功能车间

连接工厂用于提供连接池连接的生产、质检、发放的功能，让连接池公司能够正常的运作的功能车间组。

```
/**
 * Sqlite连接工厂
 * @author 欧阳洁
 * @since 2018-05-02 13:30
 */
public class SqliteBaseConnectionFactory {
    private static String DEFAULT_DB_PATH = SqliteConfig.getUri();//默认库
    private static int CON_MAX = SqliteConfig.getPoolConnectionMax();// 最大池链接
    private static int CON_MIN = SqliteConfig.getPoolConnectionMin();// 初始池链接
    private static int CON_STEP = SqliteConfig.getPoolConnectionStep();// 每次最大补充线程数量
    protected static boolean REFRESH_CON_POOL = false;
    protected static boolean USE_CONNECT_POOL = SqliteConfig.isConnectionPoolEnable();
    protected static long CON_TIMEOUT = SqliteConfig.getPoolConnectionTimeout();// 超时线程回收
    protected static boolean USE_SELF_INNER_CONFIG = SqliteConfig.isConnectionWithCofig();
    protected static Vector<SqliteBaseConnection> idleConList = new Vector<SqliteBaseConnection>();// 闲置连接
    protected static Vector<SqliteBaseConnection> runConList = new Vector<SqliteBaseConnection>();// 已分配的连接

    static {
        init();
        // 是否启用线程池
        if(USE_CONNECT_POOL){
            SqliteConnectionPool.initConnectPoolThreads();
        }
    }

    /**
     * 初始化连接池
     */
    private static void init() {
        try {
            if(SqliteConfig.isPathBaseClasspath()){
                DEFAULT_DB_PATH = SqliteUtils.getClassRootPath(DEFAULT_DB_PATH);
            }
            loadSqliteJdbcClass();//加载 org.sqlite.JDBC
            addConnection(DEFAULT_DB_PATH, CON_MIN);//默认预先建立一些连接到链接对象
        } catch (Exception e) {
            idleConList.clear();
            SqliteLogUtils.error("ERROR:[池初始化失败][池容器已清空]");
            e.printStackTrace();
        }
    }

    /**
     * 加载 org.sqlite.JDBC
     *
     * @return 返回结果
     */
    private static void loadSqliteJdbcClass() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
    }

    /**
     * 获取链接
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        // 先进先出原则
        SqliteBaseConnection currCon = null;
        synchronized (idleConList) {
            // 当可用连接池不为空时候
            if (SqliteUtils.isNotEmpty(idleConList)) {
                currCon = idleConList.get(0);
                idleConList.remove(0);
                addRunningConnection(currCon);
            }
            if (currCon == null || currCon.getConnection() == null || currCon.getConnection().isClosed()) {
                currCon = createBaseConnection();
                addRunningConnection(currCon);
            }
        }
        return currCon.getConnection();
    }

    /**
     * 获取链接
     *
     * @return
     * @throws Exception
     * @throws SQLException
     */
    public static Connection getConnection(String dbPath) throws SQLException {
        if(DEFAULT_DB_PATH.equals(dbPath)){
            return getConnection();
        }else {
            SqliteBaseConnection currCon = createBaseConnection(dbPath);
            addRunningConnection(currCon);
            return currCon.getConnection();
        }
    }

    /**
     * 添加已分配的连接到已分配队列
     * @param running
     * @return
     */
    private static boolean addRunningConnection(SqliteBaseConnection running) {
        if (runConList.size() < CON_MAX) {
            runConList.add(running);
            return true;
        }
        SqliteLogUtils.warn("当前连接数量大于自定义的最大连接数量！");
        return false;
    }

    /**
     * 检查容器是否满足使用需求
     * @return
     */
    protected static boolean checkConnectionBox(String dbPath){
        // 如果连接池里闲置的连接没有
        int addNum = 0;
        if (idleConList.size() == 0) {
            // 超过最大线程数
            addNum = CON_MAX - runConList.size();
            if (addNum <= 0) {
                SqliteLogUtils.warn("当前使用的连接数量大于自定义的最大连接[{}]限制！", CON_MAX);
                return false;
            }
        } else if (idleConList.size() < CON_MIN) {
            addNum = CON_MAX - idleConList.size() - runConList.size();
            if (addNum <= 0) {
                SqliteLogUtils.warn("连接池中当前使用的连接数量太多，闲置连接数量小于自定义的最小[{}]闲置连接！", CON_MIN);
                return false;
            }
        }
        // 剩余可增加连接对象数，默认每次最多增加 CON_STEP 个
        addNum = addNum > CON_STEP ? CON_STEP : addNum;
        if (SqliteUtils.isBlank(dbPath)) {
            addConnection(DEFAULT_DB_PATH, addNum);
        } else {
            addConnection(dbPath, addNum);
        }
        return true;
    }

    /**
     * 检查闲置连接
     * @return 检查到并重置的无效闲置连接数量
     * @author 欧阳洁
     */
    protected synchronized static int checkAllIdleConnection() {
        int idleRefreshCount = 0;
        SqliteLogUtils.info("INFO:[可用链接数:{}]，[已用连接数:{}]",idleConList.size(),runConList.size());
        try {
            if (REFRESH_CON_POOL) {//是否刷新闲置连接池里面的连接
                for (SqliteBaseConnection con : idleConList) {
                    if (null == con.getConnection() || con.getConnection().isClosed()) {
                        con.refreshConnection();
                        idleRefreshCount++;
                    }
                }
                REFRESH_CON_POOL = false;
            }
        } catch (Exception e) {
            SqliteLogUtils.error("ERROR:[新日期检查更新失败][池容器已清空]");
            e.printStackTrace();
        }
        SqliteLogUtils.info("检测到闲置连接池中无效连接并重置的数量：{}",idleRefreshCount);
        return idleRefreshCount;
    }

    /**
     * 检查已分配的连接
     * @return 清除已分配使用中的无效连接数量
     * @author 欧阳洁
     */
    protected synchronized static int checkAllRunningConnection() {
        int runningRemoveCount = 0;
        SqliteLogUtils.info("INFO:[可用链接数:{}]，[已用连接数:{}]",idleConList.size(),runConList.size());
        SqliteBaseConnection con = null;
        for (int i = 0; i < runConList.size(); i++) {
            con = runConList.get(i);
            try {
                if(null == con || null == con.getConnection() || con.getConnection().isClosed() || SqliteUtils.getNowStamp() - con.getCreateTime() > CON_TIMEOUT){
                    runConList.remove(i--);
                    runningRemoveCount++;
                }
            } catch (SQLException e) {
                SqliteLogUtils.error("检查已分配的连接出现异常！",e);
                e.printStackTrace();
            }
        }
        SqliteLogUtils.info("定时清除已分配的废弃或超时连接，清除数量：{}",runningRemoveCount);
        return runningRemoveCount;
    }

    /**
     * 添加新的链接到容器
     *
     * @param dbPath 数据库的路径
     * @param num    添加的数量
     * @throws SQLException
     */
    private static void addConnection(String dbPath, int num) {
        for (int i = 0; i < num; i++) {
            // 添加前检查连接池里面所有连接对象
            // checkAllIdleConnection();
            // 创建新的连接对象
            SqliteBaseConnection newSqliteConnection = null;
            try {
                newSqliteConnection = createBaseConnection(dbPath);
            } catch (SQLException e) {
                SqliteLogUtils.error("[addConnection]添加新连接异常！",e);
                e.printStackTrace();
            }
            idleConList.add(newSqliteConnection);
        }
    }
    ...
}
```

### 连接池工厂员工

此前连接池的基本解决方案就是预先建好一堆连接屯在那里，等到想用的时候拿一个出来直接用。因此这个预先不能在主进程里面去干。当然，上面功能车间已经有了，我们只需要在每个这间里安排相应的员工就行，每个员工负责自己的那部分，工厂就能按部就班的运作了。而这些员工的特性就像一个个线程，各尽其责，互不干扰。

```
/**
 * 池回收线程
 *
 * @author 欧阳洁
 * @since 2018-05-02 13:41
 */
public class SqliteConnectionPool extends SqliteBaseConnectionFactory {
    private static int SLEEP = SqliteConfig.getPoolThreadSleep();// 线程每次SLEEP时长
    private static boolean CHECK_RUN_ACTIVE = false;// 检查 ClearRunConnectionThread 线程是否在
    private static boolean CHECK_IDLE_ACTIVE = false;// 检查 RefreshIdleConnectionThread 线程是否在
    private static boolean CHECK_MONITOR_ACTIVE = false;// 检查 MonitorConnectionPoolThread 线程是否在
    private static int COUNT_RUN_ACTIVE = 0;// 检查 ClearRunConnectionThread 线程活跃数量
    private static int COUNT_IDLE_ACTIVE = 0;// 检查 RefreshIdleConnectionThread 线程活跃数量
    private static int COUNT_MONITOR_ACTIVE = 0;// 检查 MonitorConnectionPoolThread 线程活跃数量

    /**
     * LINK 线程池
     */
    private static final ExecutorService CONNECTION_POOL_EXETHREAD = new ThreadPoolExecutor(3, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(2000),
            SqliteThreadUtils.buildJobFactory("Sqlite 连接池 监控系统 线程池"), new ThreadPoolExecutor.AbortPolicy());

    /**
     * 获取用于Sqlite连接池监控用的线程池服务
     * @return
     */
    public static ExecutorService getExethread() {
        return CONNECTION_POOL_EXETHREAD;
    }

    /**
     * 开启连接池的线程检查，一次性
     */
    public static void checkTreadActiveStatus() {
        SqliteConnectionPool.CHECK_RUN_ACTIVE = true;
        SqliteConnectionPool.CHECK_IDLE_ACTIVE = true;
        SqliteConnectionPool.CHECK_MONITOR_ACTIVE = true;
    }

    /**
     * 开启或关闭连接池线程
     */
    public static void switchPool(boolean on_off) {
        SqliteConnectionPool.USE_CONNECT_POOL = on_off;
        if (!on_off) {
            idleConList.clear();
            runConList.clear();
        }
    }

    /**
     * 初始化连接池线程
     */
    public static void initConnectPoolThreads() {
        SqliteConnectionPool.USE_CONNECT_POOL = true;
        // 添加 池监控并适时生产新连接对象的 线程
        SqliteConnectionPool.addMonitorConnectionPoolThread();
        // 添加 池回收无效或久置超时的连接对象的 线程
        SqliteConnectionPool.addClearRunConnectionThread();
        // 添加 池检查刷新闲置连接对象的 线程
        SqliteConnectionPool.addRefreshIdleConnectionThread();
    }

    /**
     * 监控连接池中已分配的连接对象，定时收取没用的连接对象
     */
    public static void addClearRunConnectionThread() {
        CONNECTION_POOL_EXETHREAD.execute(new Runnable() {
            public void run() {
                COUNT_RUN_ACTIVE++;
                while (true) {
                    try {
                        if (SqliteConnectionPool.CHECK_RUN_ACTIVE) {
                            SqliteLogUtils.info("池回收无效或久置超时的连接对象的 线程运行中...当前该类线程数量：{}", COUNT_RUN_ACTIVE);
                            SqliteConnectionPool.CHECK_RUN_ACTIVE = false;
                        }
                        if (!SqliteConnectionPool.USE_CONNECT_POOL) {
                            SqliteLogUtils.info("池回收无效或久置超时的连接对象的 线程结束...当前该类线程数量：{}", COUNT_RUN_ACTIVE - 1);
                            break;// 如果配置不使用连接池，结束线程
                        }
                        SqliteThreadUtils.sleep(SLEEP);
                        checkAllRunningConnection();
                    } catch (InterruptedException e) {
                        idleConList.clear();
                        runConList.clear();
                        addClearRunConnectionThread();
                        SqliteLogUtils.error("ERROR:[池回收无效或久置超时的连接对象的 线程死掉,重新添加新线程！]", e);
                        e.printStackTrace();
                        break;
                    }
                }
                COUNT_RUN_ACTIVE--;
            }
        });
    }

    /**
     * 监控连接池中闲置的连接对象，定时收取没用的连接对象
     */
    public static void addRefreshIdleConnectionThread() {
        CONNECTION_POOL_EXETHREAD.execute(new Runnable() {
            public void run() {
                COUNT_IDLE_ACTIVE++;
                while (true) {
                    try {
                        if (SqliteConnectionPool.CHECK_MONITOR_ACTIVE) {
                            SqliteLogUtils.info("池检查刷新闲置连接对象的 线程运行中...当前该类线程数量：{}", COUNT_IDLE_ACTIVE);
                            SqliteConnectionPool.CHECK_MONITOR_ACTIVE = false;
                        }
                        if (!SqliteConnectionPool.USE_CONNECT_POOL) {
                            SqliteLogUtils.info("池检查刷新闲置连接对象的 线程结束...当前该类线程数量：{}", COUNT_IDLE_ACTIVE - 1);
                            break;// 如果配置不使用连接池，结束线程
                        }
                        SqliteThreadUtils.sleep(SLEEP);
                        REFRESH_CON_POOL = true;
                        checkAllIdleConnection();
                    } catch (InterruptedException e) {
                        idleConList.clear();
                        runConList.clear();
                        addRefreshIdleConnectionThread();
                        SqliteLogUtils.error("ERROR:[池检查刷新闲置连接对象的 线程死掉,重新添加新线程！]", e);
                        e.printStackTrace();
                        break;
                    }
                }
                COUNT_IDLE_ACTIVE--;
            }
        });
    }

    /**
     * 监控连接池中闲置的连接对象，定时收取没用的连接对象
     */
    public static void addMonitorConnectionPoolThread() {
        CONNECTION_POOL_EXETHREAD.execute(new Runnable() {
            public void run() {
                COUNT_MONITOR_ACTIVE++;
                while (true) {
                    try {
                        if (SqliteConnectionPool.CHECK_IDLE_ACTIVE) {
                            SqliteLogUtils.info("池监控并适时生产新连接对象的 线程运行中...当前该类线程数量：{}", COUNT_MONITOR_ACTIVE);
                            SqliteConnectionPool.CHECK_IDLE_ACTIVE = false;
                        }
                        if (!SqliteConnectionPool.USE_CONNECT_POOL) {
                            SqliteLogUtils.info("池监控并适时生产新连接对象的 线程结束...当前该类线程数量：{}", COUNT_MONITOR_ACTIVE - 1);
                            break;// 如果配置不使用连接池，结束线程
                        }
                        SqliteThreadUtils.sleep(SLEEP);
                        checkConnectionBox(null);
                    } catch (InterruptedException e) {
                        idleConList.clear();
                        runConList.clear();
                        addMonitorConnectionPoolThread();
                        SqliteLogUtils.error("ERROR:[池监控并适时生产新连接对象的 线程死掉,重新添加新线程！]", e);
                        e.printStackTrace();
                        break;
                    }
                }
                COUNT_MONITOR_ACTIVE--;
            }
        });
    }
}
```

### 默认配置

```
# 自定义属性配置
# 默认路径从class路径，此时sqlite.uri可以写相对路径
sqlite.path.classpath=true
# 数据库路径
sqlite.uri=database/sqlite.db

# 自定义连接池配置
# 是否开启连接池，如果不用配置开启可以手动代码开启：SqliteConnectionPool.initConnectPoolThreads();
sqlite.connection.pool.enable=false
# 最大连接数
sqlite.connection.max=20
# 最少连接数
sqlite.connection.min=10
# 每次最大添加数
sqlite.connection.step=5
# 已分配连接的超时设置，超时会被回收
sqlite.connection.timeout=5000
# 连接池线程轮询间隔
sqlite.pool.thread.sleep=2000

# 用户名密码登陆支持
sqlite.username=
sqlite.password=


# 是否启用非自定义属性的自定义，下面这些属性是Sqlite或JDBC自带属性，一般有默认值，自定义请在专业选手陪同下，默认不启用
sqlite.config.enable=false
# 内置属性配置 详细请见：https://github.com/petterobam/database-oop/blob/master/sqlite-oop/src/main/resources/config/sqlite.properties
...
```

## 批量操作

数据库的增、删、改语句都是独立的句子，每个句子都能独立的表达各自的意思，而不会因为上一句没有，下一句不能独立执行。JDBC 默认执行每个非查询SQL语句都会自动提交，完成一个原子操作。然而，每次提交就意味着一次对库的修改操作，这对于常规沟通是没有问题的，也是值得肯定的，但是如果是一个批量修改，一个个提交就太耗时了，而且提交还可能导致连接关闭。所以像这种作文题要把所有的句子组成文章才能交卷，否则时间不够只能交白卷了。

```
/**
 * 非查询语句批量执行Sql语句
 * @param sqlList
 * @param batchCount
 * @return
 */
public int batchExecuteSql(List<String> sqlList, int batchCount) {
    Connection connection = null;
    try {
        int result = 0;
        if (SqliteUtils.isNotEmpty(sqlList)) {
            if (batchCount <= 0) {//默认批量提交粒度100条
                batchCount = SqliteConstant.DEFAULT_BATCH_COUNT;
            }
            // create a database connection
            connection = this.getConnection();
            connection.setAutoCommit(false);//单次执行不自动提交
            Statement statement = connection.createStatement();
            for (String sql : sqlList) {
                if (!SqliteUtils.isBlank(sql)) {
                    statement.addBatch(sql);
                    result++;
                }
                if (result % batchCount == 0) {
                    statement.executeBatch();
                    connection.commit();// 提交
                    if (null == connection || connection.isClosed()) {
                        //如果连接关闭了 就在创建一个 为什么要这样 原因是 connection.commit()后可能conn被关闭
                        connection = this.getConnection();
                        connection.setAutoCommit(false);
                        statement = connection.createStatement();
                    }
                }
            }
            statement.executeBatch();
            statement.close();
            connection.commit();// 提交
            connection.setAutoCommit(true);
        }
        return result;
    } catch (SQLException e) {
        e.printStackTrace();
        return -1;
    } finally {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 非查询语句（带参数）批量执行Sql语句
 *
 * @param sqlWithParamList
 * @param batchCount
 * @return
 */
public int batchExecute(List<T> sqlWithParamList, int batchCount) {
    Connection connection = null;
    try {
        int result = 0;
        if (SqliteUtils.isNotEmpty(sqlWithParamList)) {
            if (batchCount <= 0) {//默认批量提交粒度100条
                batchCount = SqliteConstant.DEFAULT_BATCH_COUNT;
            }
            // create a database connection
            connection = this.getConnection();
            connection.setAutoCommit(false);//单次执行不自动提交
            PreparedStatement prep = null;
            String preSql = null;
            int currCount = 0;
            for (T sqlAndParam : sqlWithParamList) {
                if (SqliteUtils.isBlank(sqlAndParam.getCurrentSql())) {
                    continue;
                }
                if (!SqliteUtils.equals(preSql, sqlAndParam.getCurrentSql()) || currCount % batchCount == 0) {
                    if(currCount > 0) {
                        currCount = 0;
                        prep.executeBatch();
                        connection.commit();// 提交
                        if (null == connection || connection.isClosed()) {
                            //如果连接关闭了 就在创建一个 为什么要这样 原因是 connection.commit()后可能conn被关闭
                            connection = this.getConnection();
                            connection.setAutoCommit(false);
                        }
                    }
                    prep = connection.prepareStatement(sqlAndParam.getCurrentSql());
                }
                if (SqliteUtils.isNotEmpty(sqlAndParam.getCurrentParam())) {
                    int count = 1;
                    for (Object o : sqlAndParam.getCurrentParam()) {
                        prep.setObject(count++, o);
                    }
                }
                prep.addBatch();
                result++;
                currCount++;
                preSql = sqlAndParam.getCurrentSql();
            }
            prep.executeBatch();
            connection.commit();// 提交
            connection.setAutoCommit(true);
        }
        return result;
    } catch (SQLException e) {
        e.printStackTrace();
        return -1;
    } finally {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
...
```

## 测试

### 连接池测试

```
/**
 * Sqlite线程连接池测试
 * @author 欧阳洁
 * @since 2018-05-03 18:27
 */
public class SqliteConnectionPoolTest {
    @Test
    public void test() throws ClassNotFoundException {
        // 初始化连接池线程
        SqliteConnectionPool.initConnectPoolThreads();
        // 检查连接池线程
        SqliteConnectionPool.checkTreadActiveStatus();
        try {
            // 取两个连接，观察十秒看线程执行打印日志
            SqliteConnectionPool.getConnection();
            SqliteConnectionPool.getConnection();
            SqliteThreadUtils.sleep(10000);
            // 取三个连接，观察十秒看线程执行打印日志
            SqliteConnectionPool.getConnection();
            SqliteConnectionPool.getConnection();
            SqliteConnectionPool.getConnection();
            SqliteThreadUtils.sleep(10000);
            // 关闭所有连接池线程，观察十秒看线程执行打印日志
            SqliteConnectionPool.switchPool(false);
            SqliteThreadUtils.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

测试结果：

>[sqlite-oop]-[info]-[Fri May 18 22:15:00 CST 2018]-[池检查刷新闲置连接对象的 线程运行中...当前该类线程数量：1]
[sqlite-oop]-[info]-[Fri May 18 22:15:02 CST 2018]-[池监控并适时生产新连接对象的 线程运行中...当前该类线程数量：1]
[sqlite-oop]-[info]-[Fri May 18 22:15:02 CST 2018]-[INFO:[可用链接数:13]，[已用连接数:2]]
[sqlite-oop]-[info]-[Fri May 18 22:15:02 CST 2018]-[定时清除已分配的废弃或超时连接，清除数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:02 CST 2018]-[池回收无效或久置超时的连接对象的 线程运行中...当前该类线程数量：1]
[sqlite-oop]-[info]-[Fri May 18 22:15:02 CST 2018]-[INFO:[可用链接数:13]，[已用连接数:2]]
[sqlite-oop]-[info]-[Fri May 18 22:15:02 CST 2018]-[检测到闲置连接池中无效连接并重置的数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:04 CST 2018]-[INFO:[可用链接数:13]，[已用连接数:2]]
[sqlite-oop]-[info]-[Fri May 18 22:15:04 CST 2018]-[定时清除已分配的废弃或超时连接，清除数量：1]
[sqlite-oop]-[info]-[Fri May 18 22:15:04 CST 2018]-[INFO:[可用链接数:13]，[已用连接数:1]]
[sqlite-oop]-[info]-[Fri May 18 22:15:04 CST 2018]-[检测到闲置连接池中无效连接并重置的数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:06 CST 2018]-[INFO:[可用链接数:13]，[已用连接数:1]]
[sqlite-oop]-[info]-[Fri May 18 22:15:06 CST 2018]-[定时清除已分配的废弃或超时连接，清除数量：1]
[sqlite-oop]-[info]-[Fri May 18 22:15:06 CST 2018]-[INFO:[可用链接数:13]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:06 CST 2018]-[检测到闲置连接池中无效连接并重置的数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:08 CST 2018]-[INFO:[可用链接数:13]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:08 CST 2018]-[定时清除已分配的废弃或超时连接，清除数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:08 CST 2018]-[INFO:[可用链接数:13]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:08 CST 2018]-[检测到闲置连接池中无效连接并重置的数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:10 CST 2018]-[INFO:[可用链接数:10]，[已用连接数:3]]
[sqlite-oop]-[info]-[Fri May 18 22:15:10 CST 2018]-[定时清除已分配的废弃或超时连接，清除数量：3]
[sqlite-oop]-[info]-[Fri May 18 22:15:10 CST 2018]-[INFO:[可用链接数:10]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:10 CST 2018]-[检测到闲置连接池中无效连接并重置的数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:12 CST 2018]-[INFO:[可用链接数:10]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:12 CST 2018]-[定时清除已分配的废弃或超时连接，清除数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:12 CST 2018]-[INFO:[可用链接数:10]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:12 CST 2018]-[检测到闲置连接池中无效连接并重置的数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:14 CST 2018]-[INFO:[可用链接数:10]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:14 CST 2018]-[定时清除已分配的废弃或超时连接，清除数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:14 CST 2018]-[INFO:[可用链接数:10]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:14 CST 2018]-[检测到闲置连接池中无效连接并重置的数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:16 CST 2018]-[INFO:[可用链接数:10]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:16 CST 2018]-[定时清除已分配的废弃或超时连接，清除数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:16 CST 2018]-[INFO:[可用链接数:10]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:16 CST 2018]-[检测到闲置连接池中无效连接并重置的数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:18 CST 2018]-[INFO:[可用链接数:10]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:18 CST 2018]-[定时清除已分配的废弃或超时连接，清除数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:18 CST 2018]-[INFO:[可用链接数:10]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:18 CST 2018]-[检测到闲置连接池中无效连接并重置的数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:20 CST 2018]-[池监控并适时生产新连接对象的 线程结束...当前该类线程数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:20 CST 2018]-[INFO:[可用链接数:5]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:20 CST 2018]-[定时清除已分配的废弃或超时连接，清除数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:20 CST 2018]-[池回收无效或久置超时的连接对象的 线程结束...当前该类线程数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:20 CST 2018]-[INFO:[可用链接数:5]，[已用连接数:0]]
[sqlite-oop]-[info]-[Fri May 18 22:15:20 CST 2018]-[检测到闲置连接池中无效连接并重置的数量：0]
[sqlite-oop]-[info]-[Fri May 18 22:15:20 CST 2018]-[池检查刷新闲置连接对象的 线程结束...当前该类线程数量：0]


### 批量操作测试

```
/**
 * Sqlite批量操作测试
 * @author 欧阳洁
 * @since 2018-05-14 16:47
 */
public class SqliteBatchTest {
    @Test
    public void test() throws ClassNotFoundException {
        TestTableService sqliteService = new TestTableService();//没有使用spring注入，暂时自己构建

        sqliteService.delete("delete from t_test_table");
        SqliteLogUtils.info("===数据总条数：{}",sqliteService.count(new TestTable()));

        List<TestTable> batchList = new ArrayList<TestTable>();
        TestTable entity = new TestTable();
        entity.setName("test1");
        entity.setAuthor("petter");
        entity.setArticle("article1");
        entity.setCreateTime(SqliteUtils.getStringDate());
        batchList.add(entity);

        entity = new TestTable();
        entity.setName("test2");
        entity.setAuthor("petter");
        entity.setArticle("article1");
        entity.setCreateTime(SqliteUtils.getStringDate());
        batchList.add(entity);

        entity = new TestTable();
        entity.setName("title4");
        entity.setAuthor("bob");
        entity.setArticle("article2");
        entity.setCreateTime(SqliteUtils.getStringDate());
        batchList.add(entity);

        SqliteLogUtils.info("--开始执行批量插入操作！");
        sqliteService.batchInsert(batchList);
        SqliteLogUtils.info("--结束执行批量插入操作！");

        SqliteLogUtils.info("===数据总条数：{}",sqliteService.count(new TestTable()));
        List<TestTable> tableList = sqliteService.query(new TestTable());

        if(SqliteUtils.isNotEmpty(tableList)){
            tableList.remove(0);
        }

        for (TestTable testTable : tableList) {
            testTable.setName("Update");
        }

        SqliteLogUtils.info("--开始执行批量修改操作！");
        sqliteService.batchUpdate(tableList);
        SqliteLogUtils.info("--结束执行批量修改操作！");

        SqliteLogUtils.info("===数据总条数：{}",sqliteService.count(new TestTable()));
        sqliteService.query(new TestTable());

        SqliteLogUtils.info("--开始执行批量删除操作！");
        sqliteService.batchDelete(tableList);
        SqliteLogUtils.info("--结束执行批量删除操作！");

        SqliteLogUtils.info("===数据总条数：{}",sqliteService.count(new TestTable()));
    }
}
```

测试结果：

>[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[执行非查询语句==> create table if not exists t_test_table(id integer  primary key autoincrement not null,name char(100)  not null,author char(20)  not null,article text ,create_time char(20)  not null)]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[执行非查询语句影响行数==> 0]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[执行非查询语句==> delete from t_test_table]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[执行非查询语句影响行数==> 0]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[执行查询语句==> SELECT COUNT(1) FROM t_test_table WHERE 1=1 ]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[===数据总条数：0]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[--开始执行批量插入操作！]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[--结束执行批量插入操作！]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[执行查询语句==> SELECT COUNT(1) FROM t_test_table WHERE 1=1 ]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[===数据总条数：3]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[执行查询语句==> SELECT * FROM t_test_table WHERE 1=1 ]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[执行查询语句结果==> [{"id":1,"name":"test1","author":"petter","article":"article1","createTime":"2018-05-18 22:08:10"},{"id":2,"name":"test2","author":"petter","article":"article1","createTime":"2018-05-18 22:08:10"},{"id":3,"name":"title4","author":"bob","article":"article2","createTime":"2018-05-18 22:08:10"}]]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[--开始执行批量修改操作！]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[--结束执行批量修改操作！]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[执行查询语句==> SELECT COUNT(1) FROM t_test_table WHERE 1=1 ]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[===数据总条数：3]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[执行查询语句==> SELECT * FROM t_test_table WHERE 1=1 ]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[执行查询语句结果==> [{"id":1,"name":"test1","author":"petter","article":"article1","createTime":"2018-05-18 22:08:10"},{"id":2,"name":"Update","author":"petter","article":"article1","createTime":"2018-05-18 22:08:10"},{"id":3,"name":"Update","author":"bob","article":"article2","createTime":"2018-05-18 22:08:10"}]]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[--开始执行批量删除操作！]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[--结束执行批量删除操作！]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[执行查询语句==> SELECT COUNT(1) FROM t_test_table WHERE 1=1 ]
[sqlite-oop]-[info]-[Fri May 18 22:08:10 CST 2018]-[===数据总条数：1]

## 后记

1. 当前的连接池还是属于初级阶段，只是实现基本功能，也没有过多验证其可行性，后期借鉴优秀的连接池思路进一步优化。
2. 批量操作只是实现单表的单函数级别批量操作，并没有实现多表操作的批量操作，没有实现事务回滚功能，后期研究。
