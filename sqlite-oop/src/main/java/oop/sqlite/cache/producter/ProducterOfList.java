package oop.sqlite.cache.producter;

import java.util.List;

/**
 * List<T> 生产者定义
 *
 * @author ouyangjie
 * @Title: ProducterOfList
 * @date 2020/5/28 5:44 下午
 */
public interface ProducterOfList<T> {
    /**
     * 生产 List<T>
     * @return List<T>
     */
    List<T> fetchList();
}
