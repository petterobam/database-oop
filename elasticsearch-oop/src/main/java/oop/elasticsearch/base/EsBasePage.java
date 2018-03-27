package oop.elasticsearch.base;

import oop.elasticsearch.utils.EsUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 基础分页类
 * @param <T>
 * @author 欧阳洁
 */
public class EsBasePage<T>  implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int NO_ROW_OFFSET = 0;
    public static final int NO_ROW_LIMIT = 2147483647;
    private int offset;
    private int limit;
    private int total;
    private int size;
    private int pages;
    private int current;
    private boolean searchCount;
    private boolean openSort;
    private String orderByField;
    private boolean isAsc;
    private List<T> records = Collections.emptyList();
    private Map<String, Object> condition;

    public EsBasePage() {
        this.size = 10;
        this.current = 1;
        this.searchCount = true;
        this.openSort = true;
        this.isAsc = true;
        this.offset = 0;
        this.limit = 2147483647;
    }

    public EsBasePage(int current, int size) {
        this(current, size, true);
    }

    public EsBasePage(int current, int size, boolean searchCount) {
        this(current, size, searchCount, true);
    }

    public EsBasePage(int current, int size, boolean searchCount, boolean openSort) {
        this.offset = offsetCurrent(current, size);
        this.limit = size;

        this.searchCount = true;
        this.openSort = true;
        this.isAsc = true;
        if (current > 1) {
            this.current = current;
        }else{
            this.current = 1;
        }
        this.size = size;
        this.searchCount = searchCount;
        this.openSort = openSort;
    }

    protected static int offsetCurrent(int current, int size) {
        return current > 0 ? (current - 1) * size : 0;
    }

    public int getOffsetCurrent() {
        return offsetCurrent(this.current, this.size);
    }

    public boolean hasPrevious() {
        return this.current > 1;
    }

    public boolean hasNext() {
        return this.current < this.pages;
    }

    public int getTotal() {
        return this.total;
    }

    public EsBasePage setTotal(int total) {
        this.total = total;
        return this;
    }

    public int getSize() {
        return this.size;
    }

    public EsBasePage setSize(int size) {
        this.size = size;
        return this;
    }

    public int getPages() {
        if (this.size == 0) {
            return 0;
        } else {
            this.pages = this.total / this.size;
            if (this.total % this.size != 0) {
                ++this.pages;
            }

            return this.pages;
        }
    }

    public int getCurrent() {
        return this.current;
    }

    public EsBasePage setCurrent(int current) {
        this.current = current;
        return this;
    }

    public boolean isSearchCount() {
        return this.searchCount;
    }

    public EsBasePage setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
        return this;
    }

    public String getOrderByField() {
        return this.orderByField;
    }

    public EsBasePage setOrderByField(String orderByField) {
        if (EsUtils.isNotEmpty(orderByField)) {
            this.orderByField = orderByField;
        }

        return this;
    }

    public boolean isOpenSort() {
        return this.openSort;
    }

    public EsBasePage setOpenSort(boolean openSort) {
        this.openSort = openSort;
        return this;
    }

    public boolean isAsc() {
        return this.isAsc;
    }

    public EsBasePage setAsc(boolean isAsc) {
        this.isAsc = isAsc;
        return this;
    }

    public List<T> getRecords() {
        return this.records;
    }

    public EsBasePage<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    public Map<String, Object> getCondition() {
        return this.condition;
    }

    public EsBasePage<T> setCondition(Map<String, Object> condition) {
        this.condition = condition;
        return this;
    }

    public String toString() {
        StringBuilder pg = new StringBuilder();
        pg.append(" EsBasePage:{ [").append(this.toBaseString()).append("], ");
        if (this.records != null) {
            pg.append("records-size:").append(this.records.size());
        } else {
            pg.append("records is null");
        }

        return pg.append(" }").toString();
    }

    public String toBaseString() {
        return new StringBuilder("{ total=").append(this.total).append(" ,size=").append(this.size).append(" ,pages=").append(this.pages).append(" ,current=").append(this.current).append(" }").toString();
    }

}
