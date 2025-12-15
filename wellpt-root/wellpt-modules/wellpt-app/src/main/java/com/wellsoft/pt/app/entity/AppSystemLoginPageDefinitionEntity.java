package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年01月16日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "APP_SYSTEM_LOGIN_PAGE_DEF")
@DynamicUpdate
@DynamicInsert
public class AppSystemLoginPageDefinitionEntity extends SysEntity {
    private Long sourceUuid;
    private Boolean enabled;
    private Boolean isPc;
    private String defJson;
    private String name;
    private String title;
    private String remark;
    private Long prodVersionUuid; // 归属产品版本
    private Boolean isDefault; // 是否默认


    public Long getSourceUuid() {
        return sourceUuid;
    }

    public void setSourceUuid(Long sourceUuid) {
        this.sourceUuid = sourceUuid;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getIsPc() {
        return isPc;
    }

    public void setIsPc(Boolean pc) {
        isPc = pc;
    }

    public String getDefJson() {
        return defJson;
    }

    public void setDefJson(String defJson) {
        this.defJson = defJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getProdVersionUuid() {
        return prodVersionUuid;
    }

    public void setProdVersionUuid(Long prodVersionUuid) {
        this.prodVersionUuid = prodVersionUuid;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
