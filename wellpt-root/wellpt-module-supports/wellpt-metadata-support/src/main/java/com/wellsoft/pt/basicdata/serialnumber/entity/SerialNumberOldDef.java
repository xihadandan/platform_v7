package com.wellsoft.pt.basicdata.serialnumber.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Auther: yt
 * @Date: 2022/5/13 15:19
 * @Description:
 */
@Entity
@Table(name = "cd_serial_number_old_def")
@DynamicUpdate
@DynamicInsert
public class SerialNumberOldDef extends TenantEntity {
    private static final long serialVersionUID = 3438535425212622864L;

    /**
     * 定义类型：1，表单，2，流程
     */
    private Integer definitionType;
    /**
     * 定义uuid
     */
    private String definitionUuid;
    /**
     * 定义名称
     */
    private String definitionName;
    /**
     * 数据库表名
     */
    private String tableName;
    /**
     * 数据处理状态：0：待处理，1：已处理，2：错误，3:数据已处理
     */
    private Integer dataState;

    /**
     * 流水号字段相关信息json
     */
    private String snData;

    public Integer getDefinitionType() {
        return definitionType;
    }

    public void setDefinitionType(Integer definitionType) {
        this.definitionType = definitionType;
    }

    public String getDefinitionUuid() {
        return definitionUuid;
    }

    public void setDefinitionUuid(String definitionUuid) {
        this.definitionUuid = definitionUuid;
    }

    public String getDefinitionName() {
        return definitionName;
    }

    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getDataState() {
        return dataState;
    }

    public void setDataState(Integer dataState) {
        this.dataState = dataState;
    }

    public String getSnData() {
        return snData;
    }

    public void setSnData(String snData) {
        this.snData = snData;
    }
}
