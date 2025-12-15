package com.wellsoft.pt.theme.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;
import java.math.BigDecimal;

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
@Table(name = "THEME_SPECIFICATION")
@DynamicInsert
@DynamicUpdate
@javax.persistence.Entity
public class ThemeSpecificationEntity extends Entity {

    private BigDecimal version;

    private Boolean enabled;

    private Long sourceUuid;

    private String remark;

    private String defJson;

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
}
