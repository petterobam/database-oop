package oop.sqlite.console;

import oop.sqlite.utils.SqliteHelper;
import oop.sqlite.utils.SqliteUtils;

/**
 * 控制台返回的基本Dao
 *
 * @author 欧阳洁
 */
public class SqliteConsoleBaseDao {
    /**
     * 执行控制台语句
     *
     * @param sqlOrCmd
     * @return
     */
    public SqliteConsoleBaseEntity excute(String sqlOrCmd) {
        if (SqliteUtils.isBlank(sqlOrCmd)) {
            return null;
        }
        String sqlLower = sqlOrCmd.toLowerCase().trim();
        if(sqlLower.startsWith(".")){
            return this.sqliteHelper.cmdExecForConsole(sqlOrCmd);
        }else if (sqlLower.startsWith("insert ") || sqlLower.startsWith("delete ")
                || sqlLower.startsWith("update ") || sqlLower.startsWith("create ")
                || sqlLower.startsWith("alter ") || sqlLower.startsWith("drop ")) {
            return this.sqliteHelper.executeForConsole(sqlOrCmd);
        } else {
            return this.sqliteHelper.queryForConsole(sqlOrCmd);
        }
    }

    /**
     * 获取数据库表名集合
     * @return
     */
    public String[] getTableNameArr(){
        return this.sqliteHelper.getTableNameArr();
    }


    private SqliteHelper sqliteHelper;

    public SqliteConsoleBaseDao(String dbPath, boolean absolute) {
        this.sqliteHelper = new SqliteHelper(dbPath, absolute);
    }

    public SqliteConsoleBaseDao(String dbPath) {
        this.sqliteHelper = new SqliteHelper(dbPath);
    }

    public SqliteHelper getSqliteHelper() {
        return sqliteHelper;
    }

    public void setSqliteHelper(SqliteHelper sqliteHelper) {
        this.sqliteHelper = sqliteHelper;
    }
}
