package com.wellsoft.pt.multi.org.entity;


import com.wellsoft.context.jdbc.entity.TenantEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 * 单位参数实体
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/22   Create
 * </pre>
 */
@Entity
@Table(name = "UNIT_PARAM")
public class UnitParamEntity extends TenantEntity {

    private String paramKey;

    private String paramValue;

    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
