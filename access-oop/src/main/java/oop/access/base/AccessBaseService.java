package oop.access.base;

import oop.access.utils.AccessUtils;

import java.util.List;

/**
 * access的基础service
 *
 * @author 欧阳洁
 * @create 2017-09-30 15:18
 **/
public abstract class AccessBaseService<T extends AccessBaseEntity, D extends AccessBaseDao<T>> {
    private D baseDao;

    public AccessBaseService() {
        Class<D> daoClass = AccessUtils.getSecondSuperClassGenricType(getClass());
        this.baseDao = AccessUtils.getInstance(daoClass);
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
     * 插入
     *
     * @param entity
     * @return
     */
    public int insert(T entity) {
        return this.baseDao.insert(entity);
    }

    /**
     * 批量插入功能
     * @param list
     * @return
     */
    public int batchInsert(List<T> list){
        return this.baseDao.batchInsert(list);
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
     * 修改
     *
     * @param entity
     * @return
     */
    public int update(T entity) {
        return this.baseDao.update(entity);
    }

    /**
     * 批量修改功能
     * @param list
     * @return
     */
    public int batchUpdate(List<T> list){
        return this.baseDao.batchUpdate(list);
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
     * 删除
     *
     * @param entity
     * @return
     */
    public int delete(T entity) {
        return this.baseDao.delete(entity);
    }

    /**
     * 批量删除功能
     * @param list
     * @return
     */
    public int batchDelete(List<T> list){
        return this.baseDao.batchDelete(list);
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
     * 删除
     *
     * @param id
     * @param tableExt 分表字段值
     * @return
     */
    public int deleteById(Object id, String tableExt) {
        return this.baseDao.deleteById(id, tableExt);
    }

    /**
     * 根据ID集合，批量删除功能
     * @return
     */
    public int batchDeleteByIdList(List<Object> idList){
        return this.baseDao.batchDeleteByIdList(idList);
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
     * 查询语句执行，返回List<T>
     *
     * @param entity
     * @return
     */
    public List<T> query(T entity) {
        return this.baseDao.query(entity);
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

    /**
     * 查询语句执行，返回List<T>
     *
     * @param id
     * @param tableExt 分表字段的值
     * @return
     */
    public T queryById(Object id, String tableExt) {
        return this.baseDao.queryById(id, tableExt);
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
     * 查询条数语句执行，返回条数
     *
     * @param entity
     * @return
     */
    public int count(T entity) {
        return this.baseDao.count(entity);
    }
}
