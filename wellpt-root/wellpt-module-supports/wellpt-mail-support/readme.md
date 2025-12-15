
# 1. 模块说明
该模块主要负责邮件服务, 邮件服务主要有两部分组成: 服务端(邮局)及客户端。
服务端: 为独立的应用服务, 可根据第二点进行安装
客户端: 根据平台定制, 提供给项目使用, 可根据第三点进行引入到项目中
# 2. james服务端安装包下载及安装说明
  [点击下载](http://oa.well-soft.com:3001/svn/yfzx/j2ee/文档/产品区/威尔业务基础云平台/50 其他管理/99 其他文档/James/James 邮件系统配置说明.rar)

# 3. james客户端模块的使用
## 一、引入依赖，在pom.xml中添加如下xml代码:
```
<dependency><!-- 平台邮件客户端接口声明 -->
   <groupId>com.wellsoft</groupId>
   <artifactId>wellpt-mail-support</artifactId>
   <version>${平台版本号}</version>
</dependency>


<dependency><!-- 平台邮件客户端接口实现 -->
	<groupId>com.wellsoft</groupId>
	<artifactId>wellpt-mail</artifactId>
  <version>${平台版本号}</version>
</dependency>

<dependency><!-- james官方jar包 -->
    <groupId>org.apache.james</groupId>
    <artifactId>james</artifactId>
    <version>2.3.2</version>
</dependency>
```

## 二、配置说明, james属性配置

 在所在项目的web工程的src/main/resources目录下创建文件system-james.properties,并在该文件中加入如下属性:
```
############james parameters########################
# James Server Data
multi.tenancy.james.datasource=james
multi.tenancy.james.session_factory.bean_name=jamesSessionFactory
multi.tenancy.james.server_name=127.0.0.1
multi.tenancy.james.url=jdbc:oracle:thin:@127.0.0.1:1521:orcl
multi.tenancy.james.database_name=orcl
multi.tenancy.james.username=james
multi.tenancy.james.password=james
############james parameters########################
```
## 三、与spring的集成配置
### (1)数据库连接配置
 在所在项目的web工程的src/main/resources目录下创建文件applicationContext-james.xml, 内容如下:
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:jee="http://www.springframework.org/schema/jee" xmlns:tx="http://www.springframework.org/schema/tx"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
	http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.2.xsd
	http://www.springframework.org/schema/jee http://www.springframework.org/schema/jee/spring-jee-3.2.xsd
	http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.2.xsd"
	default-lazy-init="true">

	<description>James 3.0  数据源配置 </description>

	<!-- 租户库数据源配置,使用应用服务器的数据库连接池 -->
	<bean id="ds_james" class="com.wellsoft.pt.jpa.hibernate.BitronixDataSourceFactoryBean" lazy-init="false">
		<property name="uniqueName" value="jdbc/ds_james"></property>
		<property name="maxPoolSize" value="30"></property>
		<property name="databaseType" value="${database.type}"></property>
		<property name="driverProperties">
			<props>
				<prop key="ServerName">${multi.tenancy.james.server_name}</prop>
				<prop key="DatabaseName">${multi.tenancy.james.database_name}</prop>
				<prop key="SelectMethod">cursor</prop>
				<prop key="URL">${multi.tenancy.james.url}</prop>
				<prop key="User">${multi.tenancy.james.username}</prop>
				<prop key="Password">${multi.tenancy.james.password}</prop>
			</props>
		</property>
	</bean>

	<bean id="james" class="org.springframework.orm.hibernate4.LocalSessionFactoryBean">
		<property name="dataSource" ref="ds_james"></property>
		<property name="namingStrategy">
			<bean class="org.hibernate.cfg.ImprovedNamingStrategy" />
		</property>
		<property name="annotatedClasses">
			<bean class="com.wellsoft.pt.jpa.support.TenantEntityAnnotatedClassesFactoryBean">
				<property name="packagesToScan" value="com.wellsoft.pt.mail.***.**" />
			</bean>
		</property>
		<property name="mappingLocations">
			<bean class="com.wellsoft.pt.core.support.HibernateMappingLocationsFactoryBean">
				<property name="ignoreUrlResource" value="true"></property>
			</bean>
		</property>
		<property name="hibernateProperties">
			<props>
				<prop key="hibernate.dialect">${hibernate.dialect}</prop>
				<prop key="hibernate.show_sql">${hibernate.show_sql}</prop>
				<prop key="hibernate.format_sql">${hibernate.format_sql}</prop>
				<prop key="hibernate.cache.use_query_cache">${hibernate.cache.use_query_cache}</prop>
				<prop key="hibernate.cache.use_second_level_cache">${hibernate.cache.use_second_level_cache}</prop>
				<prop key="hibernate.cache.region.factory_class">${hibernate.cache.region.factory_class}</prop>
				<prop key="hibernate.jdbc.fetch_size">${hibernate.jdbc.fetch_size}</prop>
				<prop key="hibernate.jdbc.batch_size">${hibernate.jdbc.batch_size}</prop>

				<prop key="hibernate.connection.datasource">jdbc/ds_james</prop>
				<prop key="hibernate.connection.release_mode">after_statement</prop>
				<prop key="hibernate.jndi.class">bitronix.tm.jndi.BitronixInitialContextFactory</prop>
				<prop key="hibernate.current_session_context_class">org.springframework.orm.hibernate4.SpringJtaSessionContext</prop>
				<prop key="hibernate.transaction.factory_class">org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory
				</prop>
				<prop key="hibernate.transaction.jta.platform">org.hibernate.service.jta.platform.internal.BitronixJtaPlatform
				</prop>
			</props>
		</property>
	</bean>
</beans>
```
### (2)数据库连接池初始化
在web.xml中为spring参数**contextConfigLocation**追加值:**classpath*:/applicationContext-james.xml**
