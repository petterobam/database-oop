package oop.test.sqlite;

import oop.sqlite.config.SqliteConfig;
import oop.sqlite.constant.SqliteConstant;
import oop.sqlite.utils.SqliteUtils;
import oop.test.sqlite.entity.TestTable;
import oop.test.sqlite.service.TestTableService;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * sqlite测试
 *
 * @author 欧阳洁
 * @create 2017-09-29 18:10
 **/
public class SqliteTest {
    @Test
    public void test() throws ClassNotFoundException {
        System.out.println(SqliteConfig.getUri());
    }
    @Test
    public void test1() throws ClassNotFoundException {
        Connection connection = null;
        try {
            String TEST_DB_PATH = SqliteUtils.getClassRootPath(SqliteConstant.TEST_DB_PATH);
            // create a database connection
            String JDBC = "jdbc:sqlite:/" + TEST_DB_PATH;
            if (SqliteUtils.isWindows()) {
                TEST_DB_PATH = TEST_DB_PATH.toLowerCase();
                JDBC = "jdbc:sqlite:/" + TEST_DB_PATH;
            }
            connection = DriverManager.getConnection(JDBC);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.
            statement.executeUpdate("create table if not exists person (id integer, name string)");
            statement.executeUpdate("insert into person values(1, 'leo')");
            statement.executeUpdate("insert into person values(2, 'yui')");
            ResultSet rs = statement.executeQuery("select * from person");
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            String[] name = new String[count];
            for (int i = 0; i < count; i++) {
                name[i] = rsmd.getColumnName(i + 1);
            }
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

            while (rs.next()) {
                Map<String, Object> one = new LinkedHashMap<String, Object>();

                for (int i = 0; i < name.length; i++) {
                    Object value = rs.getObject(i + 1);
                    System.out.println(name[i] + ":" + value);
                    one.put(name[i], value);
                }
                result.add(one);
            }
            String dataRes = SqliteUtils.getJson(result);
            System.out.println(dataRes);

            PreparedStatement prep = connection.prepareStatement(
                    "insert into person values (?, ?)");
            prep.setObject(1, 5);
            prep.setObject(2, "asdfasdfas");
            prep.execute();
            prep = connection.prepareStatement(
                    "select * from person where id=?");
            prep.setObject(1, 5);
            rs = prep.executeQuery();
            while (rs.next()) {
                System.out.println("id = " + rs.getString("id"));
                System.out.println("name = " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test2() {
        TestTableService sqliteService = new TestTableService();//没有使用spring注入，暂时自己构建
        TestTable entity = new TestTable();
        entity.setName("test1");
        entity.setAuthor("petter");
        entity.setArticle("article1");
        entity.setCreateTime(SqliteUtils.getStringDate());
        sqliteService.insert(entity);
        entity.setName("title2");
        entity.setAuthor("bob");
        entity.setArticle("article2");
        entity.setCreateTime(SqliteUtils.getStringDate());
        sqliteService.insert(entity);

        // SELECT * FROM t_test_table WHERE 1=1
        TestTable queryEntity = new TestTable();
        sqliteService.query(queryEntity);
        // like 语句查询： SELECT * FROM t_test_table WHERE 1=1  and name like '%t%'
        queryEntity.setName("t");
        sqliteService.query(queryEntity);
        // SELECT * FROM t_test_table WHERE 1=1 and author=?
        queryEntity.setName(null);
        queryEntity.setAuthor("petter");
        sqliteService.query(queryEntity);
        // SELECT * FROM t_test_table WHERE 1=1  and name like ? and author=?
        queryEntity.setName("test");
        sqliteService.query(queryEntity);
        // SELECT * FROM t_test_table WHERE 1=1  and id=?
        queryEntity.setId(1);
        sqliteService.query(queryEntity);
    }

    @Test
    public void test3() {
        TestTableService sqliteService = new TestTableService();//没有使用spring注入，暂时自己构建
        List<TestTable> list = sqliteService.getByName("test");
    }

    @Test
    public void test4() {
        TestTableService sqliteService = new TestTableService();//没有使用spring注入，暂时自己构建
        List<TestTable> list = sqliteService.getByNameOrId("title", 1);
    }

    @Test
    public void test8(){
        TestTableService sqliteService = new TestTableService();//没有使用spring注入，暂时自己构建
        int count1 = sqliteService.count("select count(1) from t_test_table");
        System.out.println(count1);
        TestTable query = new TestTable();
        //query.setName("petter");
        int count = sqliteService.count(query);
        System.out.println(count);
    }
}
