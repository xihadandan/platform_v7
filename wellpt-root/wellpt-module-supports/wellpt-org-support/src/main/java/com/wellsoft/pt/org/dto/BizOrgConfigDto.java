package com.wellsoft.pt.org.dto;

import com.wellsoft.pt.org.entity.BizOrgRoleEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月27日   chenq	 Create
 * </pre>
 */
public class BizOrgConfigDto implements Serializable {
    private static final long serialVersionUID = -2436555509341671359L;

    private String bizOrgDimensionId;
    private Integer allowDimensionLevel;
    private String syncOrgOption;
    private Boolean enableSyncOrg;
    private String allowOrgEleModel;
    private Integer allowOrgLevel;
    private Long uuid;
    private Long bizOrgUuid;

    private List<BizOrgRoleEntity> bizOrgRoles;


    public String getBizOrgDimensionId() {
        return bizOrgDimensionId;
    }

    public void setBizOrgDimensionId(String bizOrgDimensionId) {
        this.bizOrgDimensionId = bizOrgDimensionId;
    }

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

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public List<BizOrgRoleEntity> getBizOrgRoles() {
        return bizOrgRoles;
    }

    public void setBizOrgRoles(List<BizOrgRoleEntity> bizOrgRoles) {
        this.bizOrgRoles = bizOrgRoles;
    }

    public Long getBizOrgUuid() {
        return bizOrgUuid;
    }

    public void setBizOrgUuid(Long bizOrgUuid) {
        this.bizOrgUuid = bizOrgUuid;
    }
}
