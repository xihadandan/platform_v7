# 1. 模块说明
该模块主要负责物理文件存储
# 2. 模块使用
## 一、引入依赖，在pom.xml中添加如下xml代码:
```
<dependency>
   <groupId>com.wellsoft</groupId>
   <artifactId>wellpt-repository-interfaces</artifactId>
   <version>${平台版本号}</version>
</dependency>

<dependency>
	<groupId>com.wellsoft</groupId>
	<artifactId>wellpt-repository</artifactId>
  <version>${平台版本号}</version>
</dependency>
```
## 二、配置说明, 配置仓库的mongodb属性
 在所在项目的web工程的src/main/resources目录下创建文件system-mongodb.properties,并在该文件中加入如下属性:
```
############mongodb parameters########################
#mongodb数据库IP地址
mongodb.server.url=127.0.0.1
#mongodb数据库服务端口号
mongodb.server.port=27017
#mongodb存储DB
mongodb.server.dbname=filedb
#mongodb存储DB的用户名
mongodb.server.username=
#mongodb存储DB的密码
mongodb.server.password=
#garbage will collect since "mongodb.server.gc.synbeforedays" days ago
mongodb.server.gc.synbeforedays=300
#office文档转成swf文件的工具（SWFTOOLS）的安装路径
SWFTools_pdf2swf.dir=F:/Program Files (x86)/SWFTools/pdf2swf.exe
############mongodb parameters########################
```
**注:如果mongodb服务端的验证配置auth=false, 那么这里的参数"mongodb.server.username"及"mongodb.server.password"的值须如上放空即可**

另:
SWFTOOLS [点击下载] (ftp://oa.well-soft.com/开发工具/swftools-0.9.0.exe )
