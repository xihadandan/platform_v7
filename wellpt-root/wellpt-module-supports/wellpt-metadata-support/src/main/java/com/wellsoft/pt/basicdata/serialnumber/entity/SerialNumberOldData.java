package com.wellsoft.pt.basicdata.serialnumber.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Auther: yt
 * @Date: 2022/5/13 15:26
 * @Description:
 */
@Entity
@Table(name = "cd_serial_number_old_data")
@DynamicUpdate
@DynamicInsert
public class SerialNumberOldData extends TenantEntity {

    /**
     * 流水号分类
     */
    private String snType;
    /**
     * 流水号定义Id
     */
    private String snId;
    /**
     * 流水号
     */
    private String serialNo;
    /**
     * 对象名称
     */
    private String objectName;
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 数据UUID
     */
    private String dataUuid;

    /**
     * 数据状态：1：已占用，2：匹配有重复（重复记录只记录一条RECORD_DATA_UUID）3：不匹配,
     */
    private Integer dataState;

    /**
     * 流水号维护记录uuid
     */
    private String maintainUuid;

    /**
     * 已记录数据UUID
     */
    private String recordDataUuid;


    public String getSnType() {
        return snType;
    }

    public void setSnType(String snType) {
        this.snType = snType;
    }

    public String getSnId() {
        return snId;
    }

    public void setSnId(String snId) {
        this.snId = snId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public Integer getDataState() {
        return dataState;
    }

    public void setDataState(Integer dataState) {
        this.dataState = dataState;
    }

    public String getRecordDataUuid() {
        return recordDataUuid;
    }

    public void setRecordDataUuid(String recordDataUuid) {
        this.recordDataUuid = recordDataUuid;
    }

    public String getMaintainUuid() {
        return maintainUuid;
    }

    public void setMaintainUuid(String maintainUuid) {
        this.maintainUuid = maintainUuid;
    }
}
