package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description:
 * 职务序列实体
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/20   Create
 * </pre>
 */
@ApiModel("职务序列")
@Entity
@Table(name = "ORG_DUTY_SEQ")
public class OrgDutySeqEntity extends TenantEntity {

    @ApiModelProperty("职务序列编号")
    private String dutySeqCode;

    @NotBlank
    @ApiModelProperty("职务序列名称")
    private String dutySeqName;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("图标背景色")
    private String backgroundColor;

    @ApiModelProperty("描述")
    private String describe;

    @ApiModelProperty("父类uuid")
    private String parentUuid;

    @ApiModelProperty("父类序列名称")
    private String parentSeqName;

    private String tenant;

    private String system;

    public String getDutySeqCode() {
        return dutySeqCode;
    }

    public void setDutySeqCode(String dutySeqCode) {
        this.dutySeqCode = dutySeqCode;
    }

    public String getDutySeqName() {
        return dutySeqName;
    }

    public void setDutySeqName(String dutySeqName) {
        this.dutySeqName = dutySeqName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Column(name = "\"DESCRIBE\"")
    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Transient
    public String getParentSeqName() {
        return parentSeqName;
    }

    public void setParentSeqName(String parentSeqName) {
        this.parentSeqName = parentSeqName;
    }

    public String getTenant() {
        return this.tenant;
    }

    public void setTenant(final String tenant) {
        this.tenant = tenant;
    }

    public String getSystem() {
        return this.system;
    }

    public void setSystem(final String system) {
        this.system = system;
    }
}
