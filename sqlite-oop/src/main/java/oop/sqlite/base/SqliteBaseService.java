package oop.sqlite.base;

import oop.sqlite.utils.SqliteUtils;

import java.util.List;

/**
 * Sqlite的基础service
 *
 * @author 欧阳洁
 * @create 2017-09-30 15:18
 **/
public abstract class SqliteBaseService<T extends SqliteBaseEntity, D extends SqliteBaseDao<T>> {
    private D baseDao;

    public SqliteBaseService(Class<D> daoClass) {
        this.baseDao = SqliteUtils.getInstance(daoClass);
    }

    public D getBaseDao() {
        return baseDao;
    }

    public void setBaseDao(D baseDao) {
        this.baseDao = baseDao;
    }

    /**
     * 插入
     *
     * @param sql
     * @return
     */
    public int insert(String sql) {
        return this.baseDao.insert(sql);
    }

    /**
     * 修改
     *
     * @param sql
     * @return
     */
    public int update(String sql) {
        return this.baseDao.update(sql);
    }

    /**
     * 删除
     *
     * @param sql
     * @return
     */
    public int delete(String sql) {
        return this.baseDao.delete(sql);
    }

    /**
     * 查询语句执行，返回List<T>
     *
     * @param sql
     * @return
     */
    public List<T> query(String sql) {
        return this.baseDao.query(sql);
    }
    /**
     * 查询条数语句执行，返回条数
     *
     * @param sql
     * @return
     */
    public int count(String sql) {
        return this.baseDao.count(sql);
    }


    /**
     * 插入
     *
     * @param entity
     * @return
     */
    public int insert(T entity) {
        return this.baseDao.insert(entity);
    }

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    public int update(T entity) {
        return this.baseDao.update(entity);
    }

    /**
     * 删除
     *
     * @param entity
     * @return
     */
    public int delete(T entity) {
        return this.baseDao.delete(entity);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public int deleteById(Object id) {
        return this.baseDao.deleteById(id);
    }

    /**
     * 查询语句执行，返回List<T>
     *
     * @param entity
     * @return
     */
    public List<T> query(T entity) {
        return this.baseDao.query(entity);
    }
    /**
     * 查询条数语句执行，返回条数
     *
     * @param entity
     * @return
     */
    public int count(T entity) {
        return this.baseDao.count(entity);
    }

    /**
     * 查询语句执行，返回List<T>
     *
     * @param id
     * @return
     */
    public T queryById(Object id) {
        return this.baseDao.queryById(id);
    }
}
