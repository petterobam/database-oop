package oop.test.access.example;

import java.sql.*;

/* A demo show how to use Statement.executeQuery(sql). */
public class Example1 {
    public static void main(String argv[]) {
        try {
            Class.forName("com.hxtt.sql.access.AccessDriver").newInstance();

            //Please see Connecting to the Database section of Chapter 2. Installation in Development Document
            //Please change "demodata" to your database directory
            String url = "jdbc:Access:///demodata";

            //Please replace with your query statement.
            //You should read SQL syntax in HXTT Access Development Document
            String sql = "select * from test where number1>0";

            Connection con = DriverManager.getConnection(url, "", "");

            Statement stmt = con.createStatement();
            stmt.setFetchSize(10);

            ResultSet rs = stmt.executeQuery(sql);

            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int iNumCols = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= iNumCols; i++) {
                System.out.println(resultSetMetaData.getColumnLabel(i)
                                   + "  " +
                                   resultSetMetaData.getColumnTypeName(i));
            }

            Object colval;
            while (rs.next()) {
                for (int i = 1; i <= iNumCols; i++) {
                    colval = rs.getObject(i);
                    System.out.print(colval + "  ");
                }
                System.out.println();
            }

            rs.close();
            stmt.close();
            con.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
