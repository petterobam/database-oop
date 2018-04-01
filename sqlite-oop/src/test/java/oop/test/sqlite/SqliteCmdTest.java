package oop.test.sqlite;

import oop.sqlite.utils.SqliteHelper;
import oop.sqlite.utils.SqliteUtils;
import org.junit.Test;

public class SqliteCmdTest {
    @Test
    public void test7(){
        SqliteHelper sqliteHelper = new SqliteHelper("/D:/Sqlite/dbs/test.db",true);
        sqliteHelper.queryJsonResult("select * from person");
        sqliteHelper.cmdExec(".dd");
        String result = sqliteHelper.cmdExec(".tables");
        if(!SqliteUtils.isBlank(result)) {
            result = result.replaceAll("\r", " ");
            result = result.replaceAll("\n", " ");
            while (result.indexOf("  ") > 0) {
                result = result.replaceAll("  ", " ");
            }
            String[] arr = result.split(" ");
            if(null != arr){
                for (String s : arr) {
                    System.out.println(s);
                }
            }
        }
    }
}
