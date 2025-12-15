package com.wellsoft.pt.basicdata.datastore.entity;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.SysEntity;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.StringReader;
import java.util.Properties;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月10日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "DB_LINK_CONFIG")
@DynamicUpdate
@DynamicInsert
public class DbLinkConfigEntity extends SysEntity {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final long serialVersionUID = -1122733936127960904L;
    private String name;
    private String linkType;
    private String host;
    private Integer port;
    private String url;
    private String userName;
    private String password;
    private String userRole;
    private String dbType;
    private String sname;
    private String sid;
    private String connectStype;
    private String param;
    private String driverClass;
    private String driverJarFile;
    private String remark;
    private Properties paramProperties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
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

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getDriverJarFile() {
        return driverJarFile;
    }

    public void setDriverJarFile(String driverJarFile) {
        this.driverJarFile = driverJarFile;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getConnectStype() {
        return connectStype;
    }

    public void setConnectStype(String connectStype) {
        this.connectStype = connectStype;
    }

    public static enum DbType {
        oracle, mysql, kingbase, dameng, sqlserver;
    }


    @Transient
    public Properties getParamProperties() {
        if (StringUtils.isNotBlank(this.getParam()) && this.paramProperties == null) {
            try {
                Properties properties = new Properties();
                properties.load(new StringReader(this.getParam()));
                this.paramProperties = properties;
                return this.paramProperties;
            } catch (Exception e) {
                logger.error("数据库连接加载配置异常: ", e);
            }
        }
        return this.paramProperties;
    }

    @Transient
    public boolean isSQLDatabase() {
        return Lists.newArrayList(DbType.oracle.name(), DbType.mysql.name(),
                DbType.kingbase.name(), DbType.dameng.name(), DbType.sqlserver.name()).indexOf(this.dbType) != -1;
    }

    public static enum LinkType {
        host, url;
    }

    public static enum ConnectStype {
        sname, sid;
    }
}
