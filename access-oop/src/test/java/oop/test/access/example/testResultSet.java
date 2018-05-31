package oop.test.access.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Time;

/* A demo show how to use UPDATABLE ResultSet. */
public class testResultSet {
    public static void main(String argv[]) {
        try {
            Class.forName("com.hxtt.sql.access.AccessDriver");

            String url = "jdbc:Access:/."; //Change "." to your data directory

            Connection con = DriverManager.getConnection(url, "", "");
            PreparedStatement stmt = con.prepareStatement(
                "select char1,number1,clob1 from test where number1<=?",
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            stmt.setMaxRows(50);
            stmt.setFetchSize(2);

            ResultSetMetaData resultSetMetaData = stmt.getMetaData();
            int iNumCols = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= iNumCols; i++) {
                System.out.println(resultSetMetaData.getColumnLabel(i)
                                    + "  " +
                                    resultSetMetaData.getColumnTypeName(i));
            }

            stmt.setDouble(1, 0);
            ResultSet rs = stmt.executeQuery();


            Object colval;
            while (rs.next()) {
                for (int i = 1; i <= iNumCols; i++) {
                    colval = rs.getObject(i);
                    System.out.print(colval + "  ");
                }
                System.out.println();
            }

            rs.first();
            rs.relative(5);
            rs.updateString(3, "eeees333ee3");
            rs.updateFloat("number1", 11111.2111f);
            rs.updateRow();

            rs.absolute(6);
            rs.deleteRow();

            rs.relative( -2);
            rs.refreshRow();

            rs.moveToInsertRow();
            rs.updateInt(1, 10000);
            rs.updateFloat(2, 1000000.0f);
            rs.updateObject(3,
                            "abc" + (new Time(System.currentTimeMillis())));
            rs.insertRow();
            rs.moveToCurrentRow();

            System.out.println("After be updated:");

            rs.beforeFirst();
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
