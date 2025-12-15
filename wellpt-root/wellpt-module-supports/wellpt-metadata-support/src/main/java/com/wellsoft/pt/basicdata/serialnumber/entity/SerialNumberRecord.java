package com.wellsoft.pt.basicdata.serialnumber.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Auther: yt
 * @Date: 2022/4/26 14:34
 * @Description: 流水号记录
 */
@Entity
@Table(name = "cd_serial_number_record")
@DynamicUpdate
@DynamicInsert
public class SerialNumberRecord extends TenantEntity {

    /**
     * 流水号关联表字段记录UUID
     */
    private String relationUuid;
    /**
     * 使用流水号的数据UUID
     */
    private String dataUuid;
    /**
     * 关键部分
     */
    private String keyPart;
    /**
     * 头部
     */
    private String headPart;
    /**
     * 尾部
     */
    private String lastPart;
    /**
     * 指针
     */
    private Integer pointer;
    /**
     * 流水号
     */
    private String serialNo;

    public String getRelationUuid() {
        return relationUuid;
    }

    public void setRelationUuid(String relationUuid) {
        this.relationUuid = relationUuid;
    }

    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public String getKeyPart() {
        return keyPart;
    }

    public void setKeyPart(String keyPart) {
        this.keyPart = keyPart;
    }

    public String getHeadPart() {
        return headPart;
    }

    public void setHeadPart(String headPart) {
        this.headPart = headPart;
    }

    public String getLastPart() {
        return lastPart;
    }

    public void setLastPart(String lastPart) {
        this.lastPart = lastPart;
    }

    public Integer getPointer() {
        return pointer;
    }

    public void setPointer(Integer pointer) {
        this.pointer = pointer;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
}
