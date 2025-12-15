package com.wellsoft.pt.api.entity;

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
 * 2025年05月13日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "API_OPERATION_PARAM")
@DynamicUpdate
@DynamicInsert
public class ApiOperationParamEntity extends SysEntity {

    private Long operationUuid;
    private String name;
    private String remark;
    private String paramType;
    private String dataType;
    private String defaultValue;
    private String exampleValue;
    private Boolean isRequired;

    public Long getOperationUuid() {
        return operationUuid;
    }

    public void setOperationUuid(Long operationUuid) {
        this.operationUuid = operationUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getExampleValue() {
        return exampleValue;
    }

    public void setExampleValue(String exampleValue) {
        this.exampleValue = exampleValue;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean required) {
        isRequired = required;
    }
}
