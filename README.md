[![Build Status](https://travis-ci.org/petterobam/database-oop.svg?branch=master)](https://travis-ci.org/petterobam/database-oop)

# 结构化数据存储系统的面向对象封装
博客地址：[http://www.db2oop.cn](http://www.db2oop.cn)

自动构建：[https://travis-ci.org/petterobam/database-oop](https://travis-ci.org/petterobam/database-oop)

欢迎参与：请骚扰 1460300366@qq.com

当前参与人：[petterobam](https://github.com/petterobam)、[oysb](https://github.com/oysb)

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