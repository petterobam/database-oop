# 新建数据库

    access.mdb

# 建表

    create table t_test_table(
        id integer primary key autoincrement not null,
        name char(100) not null,
        author char(100) not null,
        article text,
        create_time char(20) not null
    );

# 新增

    insert into t_test_table(name,author, artitcle,create_time)
        values ("test11","petter","article1","2017-09-29 17:01:22");

# 查询

    select * from t_test_table;
    select * from t_test_table where id=1;

# 修改

    update t_test_table
        set name = "test11_修改", article = "article1_修改", create_time = "2017-09-29 17:01:27"
            where id=1;
    select * from t_test_table where id = 1;

# 删除

    delete from t_test_table where id = 1;
    select count(1) from t_test_table where id = 1;

