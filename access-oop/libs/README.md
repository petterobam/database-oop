http://www.hxtt.com/access.zip

这种方式最大限制就是，一般的驱动文件都是试用版的，一次不能超过50次查询，报错如下：

```
java.sql.SQLFeatureNotSupportedException: HXTT Access Version 5.1 For Evaluation Purpose allows executing not more than 50 queries once.
```

够买正式版的则很贵了，所以一般就不采用这种方式了


```Maven```依赖：

```
<!-- http://www.hxtt.com/access.zip -->
<dependency>
    <groupId>com.hxtt</groupId>
    <artifactId>jdbc-access-hxtt</artifactId>
    <version>30</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/Access_JDBC30.jar</systemPath>
</dependency>
```

代码示例：

```java
Class.forName("com.hxtt.sql.access.AccessDriver").newInstance();
String url = "jdbc:Access:///demodata";
String sql = "select * from test where number1 > 0";
Connection con = DriverManager.getConnection(url, "", "");
// Statement无参语句
Statement stmt = con.createStatement();
stmt.setFetchSize(10);
ResultSet rs = stmt.executeQuery(sql);
ResultSetMetaData resultSetMetaData = rs.getMetaData();
int iNumCols = resultSetMetaData.getColumnCount();
for (int i = 1; i <= iNumCols; i++) {
    System.out.println(resultSetMetaData.getColumnLabel(i) + "  " + resultSetMetaData.getColumnTypeName(i));
}
while (rs.next()) {
    for (int i = 1; i <= iNumCols; i++) {
        System.out.print(rs.getObject(i));
    }
}
rs.close();
stmt.close();
// PrepareStatement预处理
sql = "SELECT number1 FROM test WHERE number1 != ?";
PrepareStatement pstmt = con.prepareStatement(sql);
pstmt.setString(1,"223.00");
rs = pstmt.executeQuery();
while (rs.next()){
   System.out.print(rs.getObject(1));
}
rs.close();
pstmt.close();
con.close();
```
