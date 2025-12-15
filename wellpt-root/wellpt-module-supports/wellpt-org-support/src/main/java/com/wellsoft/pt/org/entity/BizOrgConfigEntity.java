package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 业务组织配置项
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月25日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "BIZ_ORG_CONFIG")
@DynamicUpdate
@DynamicInsert
public class BizOrgConfigEntity extends SysEntity {


    private static final long serialVersionUID = -508322082737720990L;
    private String bizOrgDimensionId;
    private Integer allowDimensionLevel;
    private String syncOrgOption;
    private Boolean enableSyncOrg;
    private String allowOrgEleModel;
    private Integer allowOrgLevel;
    private Long bizOrgUuid;


    public Integer getAllowDimensionLevel() {
        return allowDimensionLevel;
    }

    public void setAllowDimensionLevel(Integer allowDimensionLevel) {
        this.allowDimensionLevel = allowDimensionLevel;
    }

    public String getSyncOrgOption() {
        return syncOrgOption;
    }

    public void setSyncOrgOption(String syncOrgOption) {
        this.syncOrgOption = syncOrgOption;
    }

    public Boolean getEnableSyncOrg() {
        return enableSyncOrg;
    }

    public void setEnableSyncOrg(Boolean enableSyncOrg) {
        this.enableSyncOrg = enableSyncOrg;
    }

    public String getAllowOrgEleModel() {
        return allowOrgEleModel;
    }

    public void setAllowOrgEleModel(String allowOrgEleModel) {
        this.allowOrgEleModel = allowOrgEleModel;
    }

    public Integer getAllowOrgLevel() {
        return allowOrgLevel;
    }

    public void setAllowOrgLevel(Integer allowOrgLevel) {
        this.allowOrgLevel = allowOrgLevel;
    }

    public Long getBizOrgUuid() {
        return bizOrgUuid;
    }

    public void setBizOrgUuid(Long bizOrgUuid) {
        this.bizOrgUuid = bizOrgUuid;
    }

    public String getBizOrgDimensionId() {
        return bizOrgDimensionId;
    }

    public void setBizOrgDimensionId(String bizOrgDimensionId) {
        this.bizOrgDimensionId = bizOrgDimensionId;
    }
}
