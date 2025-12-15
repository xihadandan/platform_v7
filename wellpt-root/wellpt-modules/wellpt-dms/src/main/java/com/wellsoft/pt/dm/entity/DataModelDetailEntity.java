package com.wellsoft.pt.dm.entity;

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
 * 2023年04月06日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "DATA_MODEL_DETAIL")
@DynamicUpdate
@DynamicInsert
public class DataModelDetailEntity extends SysEntity {

    private String modelJson;

    private String columnJson;

    private String ruleJson;

    private String indexJson;

    private String id;

    private Long dataModelUuid;

    private String sql;

    private String sqlParameter;

    private String sqlObjJson;

    public String getModelJson() {
        return this.modelJson;
    }

    public void setModelJson(final String modelJson) {
        this.modelJson = modelJson;
    }

    public String getColumnJson() {
        return this.columnJson;
    }

    public void setColumnJson(final String columnJson) {
        this.columnJson = columnJson;
    }

    public String getRuleJson() {
        return this.ruleJson;
    }

    public void setRuleJson(final String ruleJson) {
        this.ruleJson = ruleJson;
    }

    public String getIndexJson() {
        return this.indexJson;
    }

    public void setIndexJson(final String indexJson) {
        this.indexJson = indexJson;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Long getDataModelUuid() {
        return this.dataModelUuid;
    }

    public void setDataModelUuid(final Long dataModelUuid) {
        this.dataModelUuid = dataModelUuid;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(final String sql) {
        this.sql = sql;
    }

    public String getSqlParameter() {
        return this.sqlParameter;
    }

    public void setSqlParameter(final String sqlParameter) {
        this.sqlParameter = sqlParameter;
    }

    public String getSqlObjJson() {
        return sqlObjJson;
    }

    public void setSqlObjJson(String sqlObjJson) {
        this.sqlObjJson = sqlObjJson;
    }
}
