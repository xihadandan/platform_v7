package com.wellsoft.context.jdbc.entity;

/**
 * Description: 存在归属系统ID数据隔离的实体类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class SysEntity extends Entity {

    private static final long serialVersionUID = -22102890844570253L;
    @ApiModelProperty("归属系统")
    protected String system;
    @ApiModelProperty("归属租户")
    protected String tenant;

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
