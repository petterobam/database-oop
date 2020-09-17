package oop.sqlite.cache.producter;

/**
 * Object <T> 生产者定义
 *
 * @author ouyangjie
 * @Title: ProducterOfObject
 * @date 2020/5/28 5:33 下午
 */
public interface ProducterOfObject<T> {
    /**
     * 获取对象
     * @return <T>
     */
    T fetchObject();
}
