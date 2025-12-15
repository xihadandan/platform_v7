/*
 * @(#)2013-3-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 导入日志
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年12月7日.1	zhulh		2018年12月7日		Create
 * </pre>
 * @date 2018年12月7日
 */
@Entity
@Table(name = "cd_data_import_log")
@DynamicUpdate
@DynamicInsert
public class DataImportLogEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2842669211418446494L;

    // 数据UUID
    private String dataUuid;
    // 数据名称
    private String dataName;
    // 数据类型
    private String dataType;
    // 导入数据ID
    private String importIds;
    // 登录名
    private String loginName;
    // 用户名
    private String userName;
    // 部门职位
    private String departmentJob;
    // 导入时间
    private Date logTime;
    // 导入主机IP
    private String clientIp;

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the dataName
     */
    public String getDataName() {
        return dataName;
    }

    /**
     * @param dataName 要设置的dataName
     */
    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType 要设置的dataType
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the importIds
     */
    public String getImportIds() {
        return importIds;
    }

    /**
     * @param importIds 要设置的importIds
     */
    public void setImportIds(String importIds) {
        this.importIds = importIds;
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
     * @return the departmentJob
     */
    public String getDepartmentJob() {
        return departmentJob;
    }

    /**
     * @param departmentJob 要设置的departmentJob
     */
    public void setDepartmentJob(String departmentJob) {
        this.departmentJob = departmentJob;
    }

    /**
     * @return the logTime
     */
    public Date getLogTime() {
        return logTime;
    }

    /**
     * @param logTime 要设置的logTime
     */
    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    /**
     * @return the clientIp
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * @param clientIp 要设置的clientIp
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

}
