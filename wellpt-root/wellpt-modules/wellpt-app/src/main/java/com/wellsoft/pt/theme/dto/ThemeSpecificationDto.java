package com.wellsoft.pt.theme.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年06月19日   Qiong	 Create
 * </pre>
 */
public class ThemeSpecificationDto implements Serializable {
    private static final long serialVersionUID = 5145672879867145538L;

    private Long uuid;

    private BigDecimal version;

    private Boolean enabled;

    private Long sourceUuid;

    private String remark;

    private String defJson;

    private Boolean newVersion = false;


    protected String creator;

    protected Date createTime;

    protected String modifier;

    protected Date modifyTime;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getVersion() {
        return version;
    }

    public void setVersion(BigDecimal version) {
        this.version = version;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getSourceUuid() {
        return sourceUuid;
    }

    public void setSourceUuid(Long sourceUuid) {
        this.sourceUuid = sourceUuid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDefJson() {
        return defJson;
    }

    public void setDefJson(String defJson) {
        this.defJson = defJson;
    }

    public Boolean getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(Boolean newVersion) {
        this.newVersion = newVersion;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
