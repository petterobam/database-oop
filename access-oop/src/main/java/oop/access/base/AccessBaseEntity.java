package oop.access.base;

import java.util.List;

/**
 * access基础Entity
 *
 * @author 欧阳洁
 * @create 2017-09-30 10:41
 **/
public class AccessBaseEntity {
    private String currentSql;

    private List<Object> currentParam;

    private String needCreateBefSql;

    private String currentTableName;

    public Class getCurrentClass(){
        return this.getClass();
    }

    public String getCurrentSql() {
        return currentSql;
    }

    public void setCurrentSql(String currentSql) {
        this.currentSql = currentSql;
    }

    public List<Object> getCurrentParam() {
        return currentParam;
    }

    public void setCurrentParam(List<Object> currentParam) {
        this.currentParam = currentParam;
    }

    public String getNeedCreateBefSql() {
        return needCreateBefSql;
    }

    public void setNeedCreateBefSql(String needCreateBefSql) {
        this.needCreateBefSql = needCreateBefSql;
    }

    public String getCurrentTableName() {
        return currentTableName;
    }

    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
    }
}
