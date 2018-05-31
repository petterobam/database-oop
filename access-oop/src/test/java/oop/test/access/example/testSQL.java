package oop.test.access.example;

import java.sql.*;
import java.util.Properties;

/* A demo show how to load some sql statements. */
public class testSQL {
    private final static Object[] getSQLStatements(java.util.Vector v) {
        Object[] statements = new Object[v.size()];
        Object temp;
        for (int i = 0; i < v.size(); i++) {
            temp = v.elementAt(i);
            if (temp instanceof java.util.Vector)
                statements[i] = getSQLStatements( (java.util.Vector) temp);
            else
                statements[i] = temp;
        }
        return statements;
    }

    public final static Object[] getSQLStatements(String sqlFile) throws java.
        io.IOException {
        java.util.Vector v = new java.util.Vector(1000);
        try {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.
                FileReader(sqlFile));
            java.util.Vector batchs = new java.util.Vector(10);
            String temp;
            while ( (temp = br.readLine()) != null) {
                temp = temp.trim();
                if (temp.length() == 0)
                    continue;
                switch (temp.charAt(0)) {
                    case '*':
                    case '"':
                    case '\'':

//                    System.out.println(temp);
                        break; //Ignore any line which begin with the above character

                    case '#': //Used to begin a new sql statement
                        if (batchs.size() > 0) {
                            v.addElement(getSQLStatements(batchs));
                            batchs.removeAllElements();
                        }
                        break;
                    case '!': //Use it to get a large number of simple update statements
                        if (batchs.size() > 0) {
                            v.addElement(getSQLStatements(batchs));
                            batchs.removeAllElements();
                        }
                        String part1 = temp.substring(1);
                        String part2 = br.readLine();
                        for (int i = -2890; i < 1388; i += 39)
                            batchs.addElement(part1 + i + part2);
                        for (int i = 1890; i < 2388; i += 53) {
                            batchs.addElement(part1 + i + part2);
                            batchs.addElement(part1 + i + part2);
                        }
                        for (int i = 4320; i > 4268; i--) {
                            batchs.addElement(part1 + i + part2);
                            batchs.addElement(part1 + i + part2);
                        }
                        for (int i = 9389; i > 7388; i -= 83)
                            batchs.addElement(part1 + i + part2);
                        v.addElement(getSQLStatements(batchs));
                        batchs.removeAllElements();
                        break;
                    case 'S':
                    case 's':
                        if(temp.toUpperCase().startsWith("SELECT")){
                            if (batchs.size() > 0) {
                                v.addElement(getSQLStatements(batchs));
                                batchs.removeAllElements();
                            }
                            v.addElement(temp);
                            break;
                        }
                    default:
                        batchs.addElement(temp);
                        break;                    
                }

            }
            if (batchs.size() > 0) {
                v.addElement(getSQLStatements(batchs));
                batchs.removeAllElements();
            }
            br.close();
            br = null;
        }
        catch (java.io.FileNotFoundException fnfe) {
            v.addElement(sqlFile); //sqlFile is a sql command, not a file Name
        }

        Object[] statements = new Object[v.size()];
        for (int i = 0; i < v.size(); i++)
            statements[i] = v.elementAt(i);
        return statements;
    }

    public static void main(String argv[]) {
        try {
            String url;
            Object[] statements;
            switch (argv.length) {
                case 0: //Use it for the simplest test
                case 1:
                    url = "jdbc:Access:/.";
                    if (argv.length == 0) {
                        statements = new String[1];
                        statements[0] = "select * from test";
                    }
                    else
                        statements = argv;
                    break;
                case 2:
                    url = argv[0];
                    statements = getSQLStatements(argv[1]);
                    break;
                default:
                    throw new Exception(
                        "Syntax Error: java testSQL url sqlfile");
            }

            Class.forName("com.hxtt.sql.access.AccessDriver").newInstance();

            //Please see Connecting to the Database section of Chapter 2. Installation in Development Document
            Properties properties = new Properties();

            Connection con = DriverManager.getConnection(url, properties);

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                 ResultSet.CONCUR_READ_ONLY);
            //Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);

//            stmt.setMaxRows(0);
              stmt.setFetchSize(10);

            final boolean serializeFlag = false;//A test switch to serialize/deserialize the resultSet

            ResultSet rs;

            for (int i = 0; i < statements.length; i++) {
                if (statements[i] instanceof String) {
                    String temp = (String) statements[i];
                    switch (temp.charAt(0)) {
                        case 'S':
                        case 's':
                            System.out.println(temp);
                            rs = stmt.executeQuery(temp);

                            if (serializeFlag) {

                                // serialize the resultSet
                                try {
                                    java.io.FileOutputStream fileOutputStream = new
                                        java.io.FileOutputStream("testrs.tmp");
                                    java.io.ObjectOutputStream
                                        objectOutputStream = new java.io.
                                        ObjectOutputStream(fileOutputStream);
                                    objectOutputStream.writeObject(rs);
                                    objectOutputStream.flush();
                                    objectOutputStream.close();
                                    fileOutputStream.close();
                                }
                                catch (Exception e) {
                                    System.out.println(e);
                                    e.printStackTrace();
                                    System.exit(1);
                                }

                                rs.close(); //Let the CONCUR_UPDATABLE resultSet release its open files at once.
                                rs = null;

                                // deserialize the resultSet
                                try {
                                    java.io.FileInputStream fileInputStream = new
                                        java.io.FileInputStream("testrs.tmp");
                                    java.io.ObjectInputStream objectInputStream = new
                                        java.io.ObjectInputStream(
                                        fileInputStream);
                                    rs = (ResultSet) objectInputStream.
                                        readObject();
                                    objectInputStream.close();
                                    fileInputStream.close();
                                }
                                catch (Exception e) {
                                    System.out.println(e);
                                    e.printStackTrace();
                                    System.exit(1);
                                }
                            }

                            ResultSetMetaData resultSetMetaData = rs.
                                getMetaData();
                            int iNumCols = resultSetMetaData.getColumnCount();
                            for (int j = 1; j <= iNumCols; j++) {
                                //              System.out.println(resultSetMetaData.getColumnName(j));
                                /*                          System.out.println(resultSetMetaData.getColumnType(j));
                                     System.out.println(resultSetMetaData.getColumnDisplaySize(j));
                                     System.out.println(resultSetMetaData.getPrecision(j));
                                     System.out.println(resultSetMetaData.getScale(j));
                                 */
                                System.out.println(resultSetMetaData.
                                    getColumnLabel(j)
                                    + "  " +
                                    resultSetMetaData.getColumnTypeName(j));
                            }
                            Object colval;
                            rs.beforeFirst();
                            long ncount = 0;
                            while (rs.next()) {
//                            System.out.print(rs.rowDeleted()+" ");
                                ncount++;
                                for (int j = 1; j <= iNumCols; j++) {
                                    colval = rs.getObject(j);
                                    System.out.print(colval + "  ");
                                }
                                System.out.println();
                            }
                            rs.close(); //Let the resultSet release its open tables at once.
                            rs = null;
                            System.out.println(
                                "The total row number of resultset: " + ncount);
                            System.out.println();
                            break;
                        default:
                            int updateCount = stmt.executeUpdate(temp);
                            System.out.println(temp + " : " + updateCount);
                            System.out.println();
                    }
                }
                else if (statements[i] instanceof Object[]) {
                    int[] updateCounts;
                    Object[] temp = (Object[]) statements[i];
                    try {
                        for (int j = 0; j < temp.length; j++){
                            System.out.println( temp[j]);
                            stmt.addBatch( (String) temp[j]);
                        }
                        updateCounts = stmt.executeBatch();

                        for (int j = 0; j < temp.length; j++)
                            System.out.println((j+1)+":"+temp[j]);

                        for (int j = 0; j < updateCounts.length; j++)
                            System.out.println((j+1)+":" +updateCounts[j]);
                    }
                    catch (BatchUpdateException e) {
                        updateCounts = e.getUpdateCounts();
                        for (int j = 0; j < updateCounts.length; j++)
                            System.out.println((j+1)+":"+updateCounts[j]);
                        SQLException sqle = e;
                        do {
                            System.out.println(sqle.getMessage());
                            System.out.println("Error Code:" +
                                               sqle.getErrorCode());
                            System.out.println("SQL State:" + sqle.getSQLState());
                            sqle.printStackTrace();
                        }
                        while ( (sqle = sqle.getNextException()) != null);
                    }
                    catch (SQLException sqle) {
                        do {
                            System.out.println(sqle.getMessage());
                            System.out.println("Error Code:" +
                                               sqle.getErrorCode());
                            System.out.println("SQL State:" + sqle.getSQLState());
                            sqle.printStackTrace();
                        }
                        while ( (sqle = sqle.getNextException()) != null);
                    }

                    stmt.clearBatch();
                    System.out.println();
                }
            }

            stmt.close();
            con.close();
        }
        catch (SQLException sqle) {
            do {
                System.out.println(sqle.getMessage());
                System.out.println("Error Code:" + sqle.getErrorCode());
                System.out.println("SQL State:" + sqle.getSQLState());
                sqle.printStackTrace();
            }
            while ( (sqle = sqle.getNextException()) != null);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
