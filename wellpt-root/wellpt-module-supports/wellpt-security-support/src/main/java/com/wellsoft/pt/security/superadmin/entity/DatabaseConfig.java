/*
 * @(#)2013-4-23 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.entity;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

/**
 * Description: 数据库管理员
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-23.1	rzhu		2013-4-23		Create
 * </pre>
 * @date 2013-4-23
 */
@Entity
@CommonEntity
@Table(name = "MT_DATABASE_CONFIG")
@DynamicUpdate
@DynamicInsert
public class DatabaseConfig extends IdEntity {
    private static final long serialVersionUID = -6005055012110606422L;

    // 配置名称
    @NotBlank
    private String name;
    // 编号
    @NotBlank
    private String code;
    // 登录名
    @NotBlank
    private String loginName;
    // 密码
    @NotBlank
    private String password;
    // 服务器地址
    @NotBlank
    private String host;
    // 端口
    @NotBlank
    @Digits(fraction = 0, integer = 10)
    private String port;
    // 数据库
    @NotBlank
    private String databaseName;
    // 数据库类型
    @NotBlank
    private String type;
    // 数据库类型名称
    @NotBlank
    private String typeName;

    // TODO:单存在多个模板时,再添加模板表与该实例关联,现在可以建立多个defaultDbTemplate不同的实例来满足多模板。
    /**
     * 默认的租户库模板,用于创建租户库：定义用户名、密码、文件、表空间,可用占位符见DatabaseConfig、Tenant的field
     */
    private String defaultDbTemplate;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the loginName
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * @param loginName 要设置的loginName
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password 要设置的password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host 要设置的host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port 要设置的port
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * @param databaseName 要设置的databaseName
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName 要设置的typeName
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @return the defaultDbTemplate
     */
    public String getDefaultDbTemplate() {
        return defaultDbTemplate;
    }

    /**
     * @param defaultDbTemplate 要设置的defaultDbTemplate
     */
    public void setDefaultDbTemplate(String defaultDbTemplate) {
        this.defaultDbTemplate = defaultDbTemplate;
    }

}
