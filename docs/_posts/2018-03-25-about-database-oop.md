---
layout: post
title: "Database-OOP规范说明&注意事项"
description: "结构化数据存储系统的面向对象封装"
categories: [数据存储系统, 面向对象封装, Java]
tags: [规范说明,注意事项]
redirect_from:
  - /2018/03/25/
---

## 计划封装
<pre>
database-oop
 |
 +──arangodb-oop
 +──db2-oop
 +──<span style="color:red;">elasticsearch</span>-oop
 +──mongodb-oop
 +──mysql-oop
 +──<span style="color:red;">oracle</span>-oop
 +──postgresql-oop
 +──<span style="color:red;">redis</span>-oop
 +──<span style="color:green;">sqlite</span>-oop
 +──sqlserver-oop
 +──xml-oop
 ├─access-oop
 │  ├─src
 │  │  ├─main
 │  │  │  ├─java
 │  │  │  └─resources
 │  │  └─test
 │  │      ├─java
 │  │      └─resources
 │  └─pom.xml
 ├─.gitignore
 ├─database-oop.iml
 ├─pom.xml
 └─README.md
 
 注：<span style="color:green;">绿色</span>代表以及完成初步[-ed-ing]；<span style="color:red;">红色</span>代表进行中[-ing]。
</pre>
## 封装要求

1. 面向对象封装
    - 通过对象可以抽象存储结构（最好包括SQL-Table、Redis-Key或NoSQL-Index等存储结构的自动生成）
    - 通过对象实现数据基本增删查改操作，最好可以扩展丰富的查询功能
    - 封装后要求使用方便，不暴露与数据库相关的操作
    - 配置信息简洁（最好只包含连接数据库相关的配置），尽量不用XML配置
    - 最好有默认配置，有抽象默认基本方法和服务
    
2. 只能用Java原生的JDBC和对应数据库官方API
    - 不能使用Spring框架，但是支持在Spring中使用
    - 不能使用Mybatis、Hibernate等数据库封装框架
    - 可以使用简单的工具包的Maven依赖
    
3. 每个存储系统的封装独立，友好兼容
    - 使用方便，基本功能实现可以从默认规则或配置
    - 每个存储系统的封装不能出现和使用其他存储系统，不得互相依赖
    - 存储系统封装的代码友好，不能出现在其他开源系统（如Spring）中使用不兼容情况
    - 不宜依赖太多Maven工具包，简单工具类可以自己实现
    - 不需要使用已有日志系统，减少原生代码依赖（可以自定义日志工具，便于其他框架使用时收集日志）

4. 每个功能必须写测试，在打包成Jar的情况下也能跑通测试
    - Access、Sqlite、XML这类文档类数据库要考虑路径查找问题
    - 连接第三方存储系统这类，在测试resources中配置测试，要兼容打包成Jar的情况
    - 使用基本的JDBC，测试类不要加载任何其他环境
    
5. 命名规范
    - 所有的java类统一在 ```oop.存储系统名.**``` 包名下面，如 ```oop.sqlite.base```
    - 所有测试java类统一在 ```oop.test.存储系统名.**``` 包名下，如 ```oop.test.sqlite``` 
    - 包结构层次要清晰，一眼就能看出职能
    - 类名采用驼峰命名，java代码规范采用阿里的idea-check默认规范即可
    
## 文档提交

1. 每封文档提交都是一个单独的MarkDown文件，粘贴到项目 ```docs/_posts/```下，每个存储系统建立自己的文件夹，如 sqlite数据库 相关文档则在 ```docs/_posts/sqlite``` 下面

2. 文件夹的名字和文件的名字要用英文字符，包括 ```字母、数字、- 和 _``` ，不要使用MarkDown关键字符如 ```#、@、[]``` 等

3. 每个MarkDown文件的开始，要包含如下格式，用于建立博客的文档的索引和标签，格式出错会影响文章的显示效果
![database-oop-head](/images/about-database-oop/about-database-oop-head.png)

4. 每个MarkDown文件名以 yyyy-MM-dd-自定义英文标题.md 前面的日期用于索引，后面的英文标题用于uri链接定位，如 上面地址栏本文档链接

5. 每个MarkDown文件用到的图片资源在```docs/_images/```下，如用到图片，请放到 ```docs/_images/自定义英文标题/```文件夹下面，防止文档图片资源混乱

6. git提交备注以 ```docs:``` 打头,可以参考这种格式 ```docs:存储系统英文名:文档标题[init/修改]...``` 

## 代码提交

1. 代码提交备注以 ```子项目名:``` 打头，可以参考这种格式 ```[子项目名/存储系统英文名]:[功能描述/BUG描述][新增/修改/调整/改进]...```
