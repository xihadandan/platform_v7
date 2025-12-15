/*
 * @(#)Dec 27, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 27, 2017.1	zhulh		Dec 27, 2017		Create
 * </pre>
 * @date Dec 27, 2017
 */
@Entity
@Table(name = "DMS_OPERATE_LOG")
@DynamicUpdate
@DynamicInsert
public class DmsOperateLogEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8535216020537567758L;

    // 操作类型名称，如新增、修改
    private String name;
    // 操作类型代码，如add、modify
    private String type;
    // 操作对象名称
    private String objectName;
    // 操作对象类型、1夹、2文件、表单ID
    private String objectType;
    // 操作对象UUID
    private String objectId;
    // 操作对象UUID对应的KEY
    private String objectKey;
    // 操作对象全路径
    private String objectPath;
    // 操作人ID
    private String operatorId;
    // 操作人名称
    private String operatorName;
    // 操作时间
    private Date logTime;
    // 操作详细信息
    private String details;
    // 操作来源的客户端IP
    private String clientIp;
    // 操作来源的客户端浏览器
    private String clientBrowser;

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
     * @return the objectName
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * @param objectName 要设置的objectName
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * @return the objectType
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * @param objectType 要设置的objectType
     */
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * @return the objectId
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * @param objectId 要设置的objectId
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * @return the objectKey
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * @param objectKey 要设置的objectKey
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    /**
     * @return the objectPath
     */
    public String getObjectPath() {
        return objectPath;
    }

    /**
     * @param objectPath 要设置的objectPath
     */
    public void setObjectPath(String objectPath) {
        this.objectPath = objectPath;
    }

    /**
     * @return the operatorId
     */
    public String getOperatorId() {
        return operatorId;
    }

    /**
     * @param operatorId 要设置的operatorId
     */
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * @return the operatorName
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * @param operatorName 要设置的operatorName
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
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
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * @param details 要设置的details
     */
    public void setDetails(String details) {
        this.details = details;
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

    /**
     * @return the clientBrowser
     */
    public String getClientBrowser() {
        return clientBrowser;
    }

    /**
     * @param clientBrowser 要设置的clientBrowser
     */
    public void setClientBrowser(String clientBrowser) {
        this.clientBrowser = clientBrowser;
    }

}
