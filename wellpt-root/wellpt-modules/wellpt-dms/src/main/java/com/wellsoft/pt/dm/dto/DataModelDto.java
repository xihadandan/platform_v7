package com.wellsoft.pt.dm.dto;

import com.wellsoft.pt.dm.entity.DataModelEntity;

import java.io.Serializable;

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
public class DataModelDto implements Serializable {

    private static final long serialVersionUID = 8743406700402823449L;

    private Long uuid;

    private String id;

    private String name;

    private String remark;

    private String module;

    private DataModelEntity.Type type;

    private String modelJson;

    private String columnJson;

    private String ruleJson;

    private String indexJson;

    private String sql;


    private String sqlParameter;

    private String sqlObjJson;

    private Boolean createMainTable = true;
    private Boolean createRlTable = true;
    private Boolean createRnTable = true;
    private Boolean createVnTable = true;
    private Boolean createMkTable = true;
    private Boolean createAcTable = true;


    public Long getUuid() {
        return this.uuid;
    }

    public void setUuid(final Long uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(final String module) {
        this.module = module;
    }

    public DataModelEntity.Type getType() {
        return this.type;
    }

    public void setType(final DataModelEntity.Type type) {
        this.type = type;
    }

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

    public Boolean getCreateMainTable() {
        return createMainTable;
    }

    public void setCreateMainTable(Boolean createMainTable) {
        this.createMainTable = createMainTable;
    }

    public Boolean getCreateRlTable() {
        return createRlTable;
    }

    public void setCreateRlTable(Boolean createRlTable) {
        this.createRlTable = createRlTable;
    }

    public Boolean getCreateRnTable() {
        return createRnTable;
    }

    public void setCreateRnTable(Boolean createRnTable) {
        this.createRnTable = createRnTable;
    }

    public Boolean getCreateVnTable() {
        return createVnTable;
    }

    public void setCreateVnTable(Boolean createVnTable) {
        this.createVnTable = createVnTable;
    }

    public Boolean getCreateMkTable() {
        return createMkTable;
    }

    public void setCreateMkTable(Boolean createMkTable) {
        this.createMkTable = createMkTable;
    }

    public Boolean getCreateAcTable() {
        return createAcTable;
    }

    public void setCreateAcTable(Boolean createAcTable) {
        this.createAcTable = createAcTable;
    }

    public String getSqlObjJson() {
        return sqlObjJson;
    }

    public void setSqlObjJson(String sqlObjJson) {
        this.sqlObjJson = sqlObjJson;
    }
}
