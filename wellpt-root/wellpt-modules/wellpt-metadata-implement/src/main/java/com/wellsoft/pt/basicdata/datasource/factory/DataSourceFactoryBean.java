package com.wellsoft.pt.basicdata.datasource.factory;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月13日   chenq	 Create
 * </pre>
 */
public class DataSourceFactoryBean implements FactoryBean<DataSource> {

    ClassLoader driverClassLoader = null;

    String driverClass;

    DbType dbType;

    String host;

    int port;

    String url;

    String sname;

    String sid;

    String username;

    String password;

    Properties properties;

//  sname:  jdbc:oracle:thin:@//remotehost:1521/orclservice
//  sid:  jdbc:oracle:thin:@192.168.0.213:1521:orcl

    @Override
    public DataSource getObject() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        if (StringUtils.isNotBlank(url)) {
            dataSource.setUrl(url);
        } else {
            dataSource.setUrl(resolveJdbcUrl());
        }
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        if (MapUtils.isNotEmpty(properties)) {
            dataSource.setConnectProperties(properties);
        }
        if (driverClassLoader != null) {
            dataSource.setDriverClassLoader(driverClassLoader);
        }
        if (StringUtils.isNotBlank(driverClass)) {
            dataSource.setDriverClassName(driverClass);
        }
        return dataSource;
    }

    private String resolveJdbcUrl() {
        return null;
    }


    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public static enum DbType {
        oracle, mysql, sqlserver;
    }


    public ClassLoader getDriverClassLoader() {
        return driverClassLoader;
    }

    public void setDriverClassLoader(ClassLoader driverClassLoader) {
        this.driverClassLoader = driverClassLoader;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public static void main(String[] args) {
        DataSourceFactoryBean factoryBean = new DataSourceFactoryBean();
        factoryBean.setUrl("jdbc:oracle:thin:@192.168.0.213:1521:orcl");
        factoryBean.setUsername("USR_PT_7_0_DEV");
        factoryBean.setPassword("USR_PT_7_0_DEV");
        try {
            DataSource dataSource = factoryBean.getObject();
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("select 1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
