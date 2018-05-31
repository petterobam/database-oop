package oop.test.access.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;

/* A demo show how to use PreparedStatement. */
public class testPreparedStatement
{
    public static void main(String argv[])
    {
//		java.sql.DriverManager.setLogStream(System.out);

        try
        {
            Class.forName("com.hxtt.sql.access.AccessDriver").newInstance();

            String dir = "/datafiles"; //Change it to your data directory
            String url;
            if(argv.length<1)
                url = "jdbc:Access:///"+dir;
            else
                url = argv[0];

            Properties properties=new Properties();

            Connection con = DriverManager.getConnection(url, properties);
            PreparedStatement pstmt;
            ResultSet rs;
            String colval;
            String temp;
            ResultSetMetaData resultSetMetaData;
            int iNumCols;

            String sql;

          sql  = "SELECT number1 FROM test WHERE ?!=number1";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,"223.00");
            rs = pstmt.executeQuery();
           while (rs.next()){
               colval = rs.getObject(1)+"";
               System.out.print(colval+"  ");
            }
            rs.close();

            pstmt.close();

            sql  = "SELECT number1,'aaa'+char1+'aaa',date1,* FROM test WHERE number1>? and (char1 like ? and date1<curdate()+? and date1>{d '1000-11-22'}) and ?!=number1 order by char1,date1,boolean1";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,"100");
//            pstmt.setInt(1,100);
            pstmt.setString(2,"DDSSE");
            pstmt.setObject(3,"1992-02-03",Types.DATE);
//            pstmt.setString(3,"{d '1992-02-03'}");
//            pstmt.setDate(3,new java.sql.Date(System.currentTimeMillis()));
            pstmt.setString(4,"100.32");

//            String sql = "SELECT * FROM test";
//            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            resultSetMetaData = rs.getMetaData();
            iNumCols = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= iNumCols; i++)
                System.out.println(resultSetMetaData.getColumnLabel(i));
            while (rs.next()){
                for(int i=1;i<=iNumCols;i++){
                    colval = rs.getObject(i)+"";
                    System.out.print(colval+"  ");
                }
                System.out.println();
            }
            rs.close();



            pstmt.setInt(1,150);
            pstmt.setString(2,"ZZAA");
            pstmt.setDate(3,new Date(System.currentTimeMillis()));
           pstmt.setString(4,"100.32");

//            String sql = "SELECT * FROM test";
//            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            resultSetMetaData = rs.getMetaData();
            iNumCols = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= iNumCols; i++)
                System.out.println(resultSetMetaData.getColumnLabel(i));
            while (rs.next())
            {
                for(int i=1;i<=iNumCols;i++)
                {
                    colval = rs.getObject(i)+"";
                    System.out.print(colval+"  ");
                }
                System.out.println();
            }
            rs.close();

//            if(true)return ;

            for(int i=0;i<100;i++)
            {
              pstmt.setInt(1,i);
              pstmt.setString(2,"aaa"+i);
              pstmt.setDate(3,new Date(System.currentTimeMillis()*i));
              ResultSet rset = pstmt.executeQuery();
            }


            pstmt.setInt(1,100);
            pstmt.setString(2,"CC");
            pstmt.setDate(3,new Date(System.currentTimeMillis()));

            rs = pstmt.executeQuery();


            resultSetMetaData = rs.getMetaData();
            iNumCols = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= iNumCols; i++)
                System.out.println(resultSetMetaData.getColumnLabel(i));
            while (rs.next()){
                for(int i=1;i<=iNumCols;i++){
                    colval = rs.getObject(i)+"";
                    System.out.print(colval+"  ");
                }
                System.out.println();
            }
            rs.close();


            rs.close();

            pstmt.close();


            sql="update test SET clob1 = ?, blob1=? WHERE number1>=?*PI()%5 or number1=0";
            System.out.println(sql);
            pstmt = con.prepareStatement(sql);

            java.io.File file = new java.io.File(dir+"/somechar.txt");
            int fileLength =(int) file.length();
            java.io.InputStream fin = new java.io.FileInputStream(file);
            pstmt.setCharacterStream(1,new java.io.InputStreamReader(fin), fileLength);


/*
It's unnecessary to use writeObject code for a serialized object, because the driver
can write a serialized object.
            java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream(1024);
            java.io.ObjectOutputStream  oos = new java.io.ObjectOutputStream (baos);
            oos.writeObject("A serialized class");
            byte b[]=baos.toByteArray();

            pstmt.setBytes(2, b);//OR use setBinaryStream
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(b);
            pstmt.setBinaryStream(2, bais, b.length);

*/
            pstmt.setObject(2, "A serialized class");

            pstmt.setFloat(3,0);

            pstmt.executeUpdate();

            pstmt.close();

            String ins = "INSERT INTO test (char1,clob1,number1) VALUES(2," + "?" + ",22.21)";
            System.out.println(ins);
            pstmt = con.prepareStatement(ins,Statement.RETURN_GENERATED_KEYS);

            String descr = "Ciao provo ad inserire\r\nquesta stringa\\r\\ntesese";
            pstmt.setString(1,descr);
            pstmt.executeUpdate();

            rs=pstmt.getGeneratedKeys();
            rs.next();
            System.out.print("getGeneratedKeys():"+rs.getObject(1));
            pstmt.close();

            con.close();
        }
        catch( Exception e )
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

