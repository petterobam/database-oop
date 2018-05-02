package oop.sqlite.connection;

import oop.sqlite.utils.SqliteLogUtils;
import oop.sqlite.utils.SqliteUtils;

import java.sql.SQLException;

/**
 * 池回收线程
 *
 * @author 欧阳洁
 * @since 2018-05-02 13:41
 */
public class SqliteConnectionPool extends SqliteBaseConnectionFactory implements Runnable {
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                //SqliteLogUtils.error("INFO:[可用链接数:", sList.size(), "]", "[已用连接数:", sRunList.size(), "]");
                for (SqliteBaseConnection con : sRunList) {
                    if(null == con || null == con.getConnection() || con.getConnection().isClosed() || SqliteUtils.getNowStamp() - con.getCreateTime() > CON_TIMEOUT){
                        sRunList.remove(con);
                    }
                }
            } catch (InterruptedException e) {
                sList.clear();
                sRunList.clear();
                SqliteLogUtils.error("ERROR:[池回收线程死掉]");
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
