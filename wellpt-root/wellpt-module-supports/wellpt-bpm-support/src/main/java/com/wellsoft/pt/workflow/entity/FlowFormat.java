package com.wellsoft.pt.workflow.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.context.validator.MaxLength;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author lilin
 * @ClassName: InfoFormat
 * @Description: 信息格式
 */
@Entity
@Table(name = "WF_DEF_FORMAT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "entity")
@DynamicUpdate
@DynamicInsert
@ApiModel("信息格式")
public class FlowFormat extends TenantEntity {
    private static final long serialVersionUID = -5694123915274275876L;

    // 名称
    @NotBlank
    @ApiModelProperty("名称")
    private String name;
    // 编号
    @NotBlank
    @ApiModelProperty("编号")
    private String code;
    // 格式内容
    @MaxLength(max = 4000)
    @ApiModelProperty("格式内容")
    private String value;
    // 清除HTML格式
    @ApiModelProperty("清除HTML格式")
    private Boolean isClear;

    @ApiModelProperty("归属系统")
    protected String system;
    @ApiModelProperty("归属租户")
    protected String tenant;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the value
     */
    @Column(length = 2000)
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the isClear
     */
    public Boolean getIsClear() {
        return isClear;
    }

    /**
     * @param isClear 要设置的isClear
     */
    public void setIsClear(Boolean isClear) {
        this.isClear = isClear;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
