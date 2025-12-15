/*
 * @(#)2014-7-31 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 外部数据连接配置
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-31.1	wubin		2014-7-31		Create
 * </pre>
 * @date 2014-7-31
 */
@Entity
@Table(name = "data_source_profile")
@DynamicUpdate
@DynamicInsert
public class DataSourceProfile extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3479355408008620193L;
    // jqgridId
    private String id;
    // 外部数据源配置名称
    @NotBlank
    private String dataSourceProfileName;
    // id
    @NotBlank
    private String dataSourceProfileId;
    // 编号
    private String dataSourceProfileNum;
    // 类型，Database、XML、File、Web Services，目前只考虑Database
    private String outDataSourceType;
    // 数据库类型，oracle、sql server
    private String databaseType;
    // database(sid) 数据库服务名
    private String databaseSid;
    // 主机名
    private String host;
    // 端口
    private String port;
    // 用户名
    private String userName;
    // 密码
    private String passWord;
    // oracle下的分库名（无特殊情况即username）
    private String owner;
    // 字符集
    private String characterSet;

    /**
     * @return the characterSet
     */
    public String getCharacterSet() {
        return characterSet;
    }

    /**
     * @param characterSet 要设置的characterSet
     */
    public void setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
    }

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner 要设置的owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the dataSourceProfileName
     */
    public String getDataSourceProfileName() {
        return dataSourceProfileName;
    }

    /**
     * @param dataSourceProfileName 要设置的dataSourceProfileName
     */
    public void setDataSourceProfileName(String dataSourceProfileName) {
        this.dataSourceProfileName = dataSourceProfileName;
    }

    /**
     * @return the dataSourceProfileId
     */
    public String getDataSourceProfileId() {
        return dataSourceProfileId;
    }

    /**
     * @param dataSourceProfileId 要设置的dataSourceProfileId
     */
    public void setDataSourceProfileId(String dataSourceProfileId) {
        this.dataSourceProfileId = dataSourceProfileId;
    }

    /**
     * @return the dataSourceProfileNum
     */
    public String getDataSourceProfileNum() {
        return dataSourceProfileNum;
    }

    /**
     * @param dataSourceProfileNum 要设置的dataSourceProfileNum
     */
    public void setDataSourceProfileNum(String dataSourceProfileNum) {
        this.dataSourceProfileNum = dataSourceProfileNum;
    }

    /**
     * @return the outDataSourceType
     */
    public String getOutDataSourceType() {
        return outDataSourceType;
    }

    /**
     * @param outDataSourceType 要设置的outDataSourceType
     */
    public void setOutDataSourceType(String outDataSourceType) {
        this.outDataSourceType = outDataSourceType;
    }

    /**
     * @return the databaseType
     */
    public String getDatabaseType() {
        return databaseType;
    }

    /**
     * @param databaseType 要设置的databaseType
     */
    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    /**
     * @return the databaseSid
     */
    public String getDatabaseSid() {
        return databaseSid;
    }

    /**
     * @param databaseSid 要设置的databaseSid
     */
    public void setDatabaseSid(String databaseSid) {
        this.databaseSid = databaseSid;
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
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName 要设置的userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the passWord
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * @param passWord 要设置的passWord
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

}
