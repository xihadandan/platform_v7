/*
 * @(#)2017-02-22 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
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
 * 2017-02-22.1	zhulh		2017-02-22		Create
 * </pre>
 * @date 2017-02-22
 */
@Entity
@Table(name = "DMS_SHARE_INFO")
@DynamicUpdate
@DynamicInsert
public class DmsShareInfoEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1487735472484L;

    // 数据类型
    private String dataType;
    // 数据UUID
    private String dataUuid;
    // 数据所有者用户ID
    private String ownerId;
    // 分享的组织ID(用户、职位、部门等ID)，多个以分号隔开
    private String shareOrgId;
    // 分享的组织名称(用户、职位、部门等名称)，多个以分号隔开
    private String shareOrgName;
    // 分享时间
    private Date shareTime;
    // 浏览次数
    private String viewTimes;
    // 下载次数
    private String downloadTimes;

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
     * @return the ownerId
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId 要设置的ownerId
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * @return the shareOrgId
     */
    public String getShareOrgId() {
        return shareOrgId;
    }

    /**
     * @param shareOrgId 要设置的shareOrgId
     */
    public void setShareOrgId(String shareOrgId) {
        this.shareOrgId = shareOrgId;
    }

    /**
     * @return the shareOrgName
     */
    public String getShareOrgName() {
        return shareOrgName;
    }

    /**
     * @param shareOrgName 要设置的shareOrgName
     */
    public void setShareOrgName(String shareOrgName) {
        this.shareOrgName = shareOrgName;
    }

    /**
     * @return the shareTime
     */
    public Date getShareTime() {
        return shareTime;
    }

    /**
     * @param shareTime 要设置的shareTime
     */
    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    /**
     * @return the viewTimes
     */
    public String getViewTimes() {
        return viewTimes;
    }

    /**
     * @param viewTimes 要设置的viewTimes
     */
    public void setViewTimes(String viewTimes) {
        this.viewTimes = viewTimes;
    }

    /**
     * @return the downloadTimes
     */
    public String getDownloadTimes() {
        return downloadTimes;
    }

    /**
     * @param downloadTimes 要设置的downloadTimes
     */
    public void setDownloadTimes(String downloadTimes) {
        this.downloadTimes = downloadTimes;
    }

}
