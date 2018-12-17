package oop.sqlite.base;

import oop.sqlite.constant.SqliteConstant;
import oop.sqlite.utils.SqliteHelper;
import oop.sqlite.utils.SqliteSqlHelper;
import oop.sqlite.utils.SqliteUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Sqlite基础dao
 *
 * @author 欧阳洁
 * @create 2017-09-30 10:28
 **/
public abstract class SqliteBaseDao<T extends SqliteBaseEntity> {
    private String tableName;
    private Class entityClazz;
    private SqliteSqlHelper sqlHelper;
    private SqliteHelper sqliteHelper;

    public SqliteBaseDao() {
        this.entityClazz = SqliteUtils.getFirstSuperClassGenricType(getClass());
        this.sqlHelper = new SqliteSqlHelper(this.entityClazz);
        this.tableName = this.sqlHelper.getTableName();
        this.sqliteHelper = new SqliteHelper(this.entityClazz);
        //调用该方法就能在使用时检查和创建表，可以通过配置信息判断是否执调用，达到开关控制的效果
        this.existOrCreateTable();
    }

    /**
     * 检查表是否存在，不存在则创建
     */
    public void existOrCreateTable() {
        String sql = this.sqlHelper.createTableSql();
        this.sqliteHelper.execute(sql);
    }

    /**
     * 插入
     *
     * @param sql
     * @return
     */
    public int insert(String sql) {
        return this.sqliteHelper.insert(sql);
    }

    /**
     * 插入
     *
     * @param entity
     * @return
     */
    public int insert(T entity) {
        this.sqlHelper.createInsert(entity);
        if (!SqliteUtils.isBlank(entity.getNeedCreateBefSql())) {
            //插入数据之前判断是否需要建表
            this.sqliteHelper.execute(entity.getNeedCreateBefSql());
        }
        return this.sqliteHelper.insert(entity.getCurrentSql(), entity.getCurrentParam());
    }

    /**
     * 批量插入
     *
     * @param sqlList
     * @return
     */
    public int batchInsertSql(List<String> sqlList) {
        return this.sqliteHelper.batchInsertSql(sqlList);
    }

    /**
     * 批量插入
     *
     * @param sqlList
     * @param batchCount
     * @return
     */
    public int batchInsertSql(List<String> sqlList, int batchCount) {
        return this.sqliteHelper.batchInsertSql(sqlList, batchCount);
    }

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    public int batchInsert(List<T> list) {
        return this.batchInsert(list, SqliteConstant.DEFAULT_BATCH_COUNT);
    }

    /**
     * 批量插入
     *
     * @param list
     * @param batchCount
     * @return
     */
    public int batchInsert(List<T> list, int batchCount) {
        if (SqliteUtils.isNotEmpty(list)) {
            for (T entity : list) {
                this.sqlHelper.createInsert(entity);
            }
        } else {
            return 0;
        }
        return this.sqliteHelper.batchInsert(list, batchCount);
    }

    /**
     * 修改
     *
     * @param sql
     * @return
     */
    public int update(String sql) {
        return this.sqliteHelper.update(sql);
    }

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    public int update(T entity) {
        this.sqlHelper.createUpdate(entity);
        return this.sqliteHelper.update(entity.getCurrentSql(), entity.getCurrentParam());
    }

    /**
     * 批量修改
     *
     * @param sqlList
     * @return
     */
    public int batchUpdateSql(List<String> sqlList) {
        return this.sqliteHelper.batchUpdateSql(sqlList);
    }

    /**
     * 批量修改
     *
     * @param sqlList
     * @param batchCount
     * @return
     */
    public int batchUpdateSql(List<String> sqlList, int batchCount) {
        return this.sqliteHelper.batchUpdateSql(sqlList, batchCount);
    }

    /**
     * 批量修改
     *
     * @param list
     * @return
     */
    public int batchUpdate(List<T> list) {
        return this.batchUpdate(list, SqliteConstant.DEFAULT_BATCH_COUNT);
    }

    /**
     * 批量修改
     *
     * @param list
     * @param batchCount
     * @return
     */
    public int batchUpdate(List<T> list, int batchCount) {
        if (SqliteUtils.isNotEmpty(list)) {
            for (T entity : list) {
                this.sqlHelper.createUpdate(entity);
            }
        } else {
            return 0;
        }
        return this.sqliteHelper.batchUpdate(list, batchCount);
    }

    /**
     * 删除
     *
     * @param sql
     * @return
     */
    public int delete(String sql) {
        return this.sqliteHelper.delete(sql);
    }

    /**
     * 删除
     *
     * @param entity
     * @return
     */
    public int delete(T entity) {
        this.sqlHelper.createDelete(entity);
        return this.sqliteHelper.delete(entity.getCurrentSql(), entity.getCurrentParam());
    }

    /**
     * 批量删除
     *
     * @param sqlList
     * @return
     */
    public int batchDeleteSql(List<String> sqlList) {
        return this.sqliteHelper.batchDeleteSql(sqlList);
    }

    /**
     * 批量删除
     *
     * @param sqlList
     * @param batchCount
     * @return
     */
    public int batchDeleteSql(List<String> sqlList, int batchCount) {
        return this.sqliteHelper.batchDeleteSql(sqlList, batchCount);
    }

    /**
     * 批量删除
     *
     * @param list
     * @return
     */
    public int batchDelete(List<T> list) {
        return this.batchDelete(list, SqliteConstant.DEFAULT_BATCH_COUNT);
    }

    /**
     * 批量删除
     *
     * @param list
     * @param batchCount
     * @return
     */
    public int batchDelete(List<T> list, int batchCount) {
        if (SqliteUtils.isNotEmpty(list)) {
            for (T entity : list) {
                this.sqlHelper.createDelete(entity);
            }
        } else {
            return 0;
        }
        return this.sqliteHelper.batchDelete(list, batchCount);
    }

    /**
     * 通过ID集合，批量删除
     *
     * @param list
     * @return
     */
    public int batchDeleteByIdList(List<Object> list) {
        return this.batchDeleteByIdList(list, SqliteConstant.DEFAULT_BATCH_COUNT);
    }

    /**
     * 通过ID集合，批量删除
     *
     * @param list
     * @param batchCount
     * @return
     */
    public int batchDeleteByIdList(List<Object> list, int batchCount) {
        String sql = this.sqlHelper.createDeleteById();
        return this.sqliteHelper.batchDeleteByIdList(sql, list, batchCount);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public int deleteById(Object id) {
        return this.deleteById(id, null);
    }

    /**
     * 删除，分表字段值
     *
     * @param id
     * @return
     */
    public int deleteById(Object id, String tableExt) {
        String sql = this.sqlHelper.createDeleteById(tableExt);
        List<Object> param = new ArrayList<Object>(1);
        param.add(id);
        return this.sqliteHelper.delete(sql, param);
    }

    /**
     * 非查询语句执行，返回List<T>
     *
     * @param sql
     * @return
     */
    public List<T> query(String sql) {
        String jsonStr = this.sqliteHelper.queryJsonResult(sql, this.getColumMap());
        if (jsonStr == null) return null;
        List<T> result = SqliteUtils.getInstance(jsonStr, this.entityClazz);
        return result;
    }

    /**
     * 查询语句执行，返回List<T>
     *
     * @param entity
     * @return
     */
    public List<T> query(T entity) {
        this.sqlHelper.createSelect(entity);
        String jsonStr = this.sqliteHelper.queryJsonResult(entity.getCurrentSql(), entity.getCurrentParam(), this.getColumMap());
        if (jsonStr == null) return null;
        List<T> result = SqliteUtils.getInstance(jsonStr, entity.getClass());
        return result;
    }

    /**
     * 查询语句执行，返回T
     *
     * @param id
     * @return
     */
    public T queryById(Object id) {
        return this.queryById(id, null);
    }

    /**
     * 查询语句执行，返回T
     *
     * @param id
     * @param tableExt 分表字段的值
     * @return
     */
    public T queryById(Object id, String tableExt) {
        String sql = this.sqlHelper.createSelectById(id, tableExt);
        List<Object> param = new ArrayList<Object>(1);
        param.add(id);
        String jsonStr = this.sqliteHelper.queryJsonResult(sql, param, this.getColumMap());
        if (jsonStr == null) return null;
        List<T> result = SqliteUtils.getInstance(jsonStr, this.entityClazz);
        if (SqliteUtils.isNotEmpty(result)) {
            return result.get(0);
        } else {
            return null;
        }
    }

    /**
     * 查询条数
     *
     * @param sql
     * @return
     */
    public int count(String sql) {
        return sqliteHelper.queryCountResult(sql);
    }

    /**
     * 查询条数语句执行，返回List<T>
     *
     * @param entity
     * @return
     */
    public int count(T entity) {
        this.sqlHelper.createCount(entity);
        return this.sqliteHelper.queryCountResult(entity.getCurrentSql(), entity.getCurrentParam());
    }


    /**
     * 通过自定义注解执行查询的语句
     *
     * @param entity
     * @return
     */
    public List<T> excuteQuery(T entity) {
        //[0]为getStackTrace方法，[1]当前的excuteQuery方法，[2]为调用excuteQuery方法的方法
        StackTraceElement parrentMethodInfo = Thread.currentThread().getStackTrace()[2];// [2]该结果不可能为空
        this.sqlHelper.convertSelfSql(parrentMethodInfo, entity);
        String jsonStr = this.sqliteHelper.queryJsonResult(entity.getCurrentSql(), entity.getCurrentParam(), this.getColumMap());
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
     *
     * @param params
     * @return
     */
    public List<T> excuteQuery(Object... params) {
        //[0]为getStackTrace方法，[1]当前的excuteQuery方法，[2]为调用excuteQuery方法的方法
        StackTraceElement parrentMethodInfo = Thread.currentThread().getStackTrace()[2];// [2]该结果不可能为空
        String sql = this.sqlHelper.convertSelfSql(parrentMethodInfo, params);
        List<Object> paramList = new Vector<Object>();
        if (null != params && params.length > 0) {
            for (Object o : params) {
                paramList.add(o);
            }
        }
        String jsonStr = this.sqliteHelper.queryJsonResult(sql, paramList, this.getColumMap());
        if (jsonStr == null) return null;
        List<T> result = SqliteUtils.getInstance(jsonStr, this.entityClazz);
        if (SqliteUtils.isNotEmpty(result)) {
            return result;
        } else {
            return null;
        }
    }

    /**
     * 通过自定义注解执行非查询的语句
     *
     * @param params
     * @return
     */
    public int excute(Object... params) {
        //[0]为getStackTrace方法，[1]当前的excute方法，[2]为调用excute方法的方法
        StackTraceElement parrentMethodInfo = Thread.currentThread().getStackTrace()[2];// [2]该结果不可能为空
        String sql = this.sqlHelper.convertSelfSql(parrentMethodInfo, params);
        List<Object> paramList = new Vector<Object>();
        if (null != params && params.length > 0) {
            for (Object o : params) {
                paramList.add(o);
            }
        }
        return this.sqliteHelper.execute(sql, paramList);
    }

    /**
     * 通过自定义注解执行非查询的语句
     *
     * @param entity
     * @return
     */
    public int excute(T entity) {
        //[0]为getStackTrace方法，[1]当前的excute方法，[2]为调用excute方法的方法
        StackTraceElement parrentMethodInfo = Thread.currentThread().getStackTrace()[2];// [2]该结果不可能为空
        this.sqlHelper.convertSelfSql(parrentMethodInfo, entity);
        return this.sqliteHelper.execute(entity.getCurrentSql(), entity.getCurrentParam());
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Class getEntityClazz() {
        return entityClazz;
    }

    public void setEntityClazz(Class entityClazz) {
        this.entityClazz = entityClazz;
    }

    public SqliteSqlHelper getSqlHelper() {
        return sqlHelper;
    }

    public void setSqlHelper(SqliteSqlHelper sqlHelper) {
        this.sqlHelper = sqlHelper;
    }

    public Map<String, String> getColumMap() {
        return this.sqlHelper.getColumnMap();
    }
}
