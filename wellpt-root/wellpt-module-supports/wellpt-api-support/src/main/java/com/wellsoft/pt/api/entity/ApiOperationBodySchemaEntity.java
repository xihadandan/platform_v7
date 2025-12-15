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
@Table(name = "API_OPERATION_BODY_SCHEMA")
@DynamicUpdate
@DynamicInsert
public class ApiOperationBodySchemaEntity extends SysEntity {

    private Long operationUuid;
    private String applyTo;
    private String schemaDefinition;
    private String schemaConfig;
    private String exampleBody;

    public Long getOperationUuid() {
        return operationUuid;
    }

    public void setOperationUuid(Long operationUuid) {
        this.operationUuid = operationUuid;
    }

    public String getApplyTo() {
        return applyTo;
    }

    public void setApplyTo(String applyTo) {
        this.applyTo = applyTo;
    }

    public String getSchemaDefinition() {
        return schemaDefinition;
    }

    public void setSchemaDefinition(String schemaDefinition) {
        this.schemaDefinition = schemaDefinition;
    }

    public String getExampleBody() {
        return exampleBody;
    }

    public void setExampleBody(String exampleBody) {
        this.exampleBody = exampleBody;
    }

    public String getSchemaConfig() {
        return schemaConfig;
    }

    public void setSchemaConfig(String schemaConfig) {
        this.schemaConfig = schemaConfig;
    }
}
