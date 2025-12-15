package com.wellsoft.pt.basicdata.serialnumber.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Auther: yt
 * @Date: 2022/4/26 14:34
 * @Description: 流水号关联表字段记录
 */
@Entity
@Table(name = "cd_serial_number_relation")
@DynamicUpdate
@DynamicInsert
public class SerialNumberRelation extends TenantEntity {

    /**
     * 流水号定义Id
     */
    private String snId;
    /**
     * 对象类型：（1：数据库表）
     */
    private Integer objectType;
    /**
     * 对象名称
     */
    private String objectName;
    /**
     * 字段名
     */
    private String fieldName;

    public String getSnId() {
        return snId;
    }

    public void setSnId(String snId) {
        this.snId = snId;
    }

    public Integer getObjectType() {
        return objectType;
    }

    public void setObjectType(Integer objectType) {
        this.objectType = objectType;
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
}
