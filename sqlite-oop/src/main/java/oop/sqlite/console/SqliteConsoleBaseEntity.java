package my.sqlite.console;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 控制台返回的基本实体
 * @author 欧阳洁
 */
public class SqliteConsoleBaseEntity {
    /**
     * 查询结果集
     */
    private List<Map<String, Object>> queryResult;
    /**
     * 命令行执行结果
     */
    private String cmdResult;
    /**
     * 影响行数
     */
    private int infactLine;
    /**
     * SQL语句执行异常
     */
    private SQLException sqlException;
    /**
     * 程序异常
     */
    private Exception exception;
    /**
     * 是否存在异常
     */
    private boolean hasException = false;

    public List<Map<String, Object>> getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(List<Map<String, Object>> queryResult) {
        this.queryResult = queryResult;
    }

    public String getCmdResult() {
        return cmdResult;
    }

    public void setCmdResult(String cmdResult) {
        this.cmdResult = cmdResult;
    }

    public int getInfactLine() {
        return infactLine;
    }

    public void setInfactLine(int infactLine) {
        this.infactLine = infactLine;
    }

    public SQLException getSqlException() {
        return sqlException;
    }

    public void setSqlException(SQLException sqlException) {
        this.sqlException = sqlException;
    }

    public java.lang.Exception getException() {
        return exception;
    }

    public void setException(java.lang.Exception exception) {
        this.exception = exception;
    }

    public boolean isHasException() {
        return hasException;
    }

    public void setHasException(boolean hasException) {
        this.hasException = hasException;
    }
}
