package com.wellsoft.pt.app.feishu.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;
import java.util.Date;

/**
 * 飞书同步日志表实体类
 */
@javax.persistence.Entity
@Table(name = "feishu_sync_log")
@DynamicUpdate
@DynamicInsert
public class FeishuSyncLogEntity extends SysEntity {

    /**
     * 飞书配置信息Uuid
     */
    private Long configUuid;

    private Long orgUuid;

    private String orgName;

    private Long orgVersionUuid;

    private String syncContent;

    /**
     * 同步类型：1:手动触发、2:定时任务触发
     */
    private String syncType;

    /**
     * 同步时间
     */
    private Date syncTime;

    /**
     * 同步状态：1:成功、0:失败
     */
    private Integer syncStatus;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 飞书同步配置简要说明
     */
    private String remark;

    /**
     * @return the configUuid
     */
    public Long getConfigUuid() {
        return configUuid;
    }

    /**
     * @param configUuid 要设置的configUuid
     */
    public void setConfigUuid(Long configUuid) {
        this.configUuid = configUuid;
    }

    /**
     * @return the orgUuid
     */
    public Long getOrgUuid() {
        return orgUuid;
    }

    /**
     * @param orgUuid 要设置的orgUuid
     */
    public void setOrgUuid(Long orgUuid) {
        this.orgUuid = orgUuid;
    }

    /**
     * @return the orgName
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * @param orgName 要设置的orgName
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * @return the orgVersionUuid
     */
    public Long getOrgVersionUuid() {
        return orgVersionUuid;
    }

    /**
     * @param orgVersionUuid 要设置的orgVersionUuid
     */
    public void setOrgVersionUuid(Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    /**
     * @return the syncContent
     */
    public String getSyncContent() {
        return syncContent;
    }

    /**
     * @param syncContent 要设置的syncContent
     */
    public void setSyncContent(String syncContent) {
        this.syncContent = syncContent;
    }

    /**
     * @return the syncType
     */
    public String getSyncType() {
        return syncType;
    }

    /**
     * @param syncType 要设置的syncType
     */
    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    /**
     * @return the syncTime
     */
    public Date getSyncTime() {
        return syncTime;
    }

    /**
     * @param syncTime 要设置的syncTime
     */
    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    /**
     * @return the syncStatus
     */
    public Integer getSyncStatus() {
        return syncStatus;
    }

    /**
     * @param syncStatus 要设置的syncStatus
     */
    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage 要设置的errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
