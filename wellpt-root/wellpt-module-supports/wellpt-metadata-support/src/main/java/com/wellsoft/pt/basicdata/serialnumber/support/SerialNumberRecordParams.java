package com.wellsoft.pt.basicdata.serialnumber.support;

import com.wellsoft.pt.basicdata.serialnumber.bean.SerialNumberMaintainBean;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberRecord;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberRelation;
import com.wellsoft.pt.basicdata.serialnumber.enums.ObjectTypeEnum;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * @Auther: yt
 * @Date: 2022/5/6 17:31
 * @Description:
 */
public class SerialNumberRecordParams implements Serializable {
    private static final long serialVersionUID = -3722165611531710438L;

    /**
     * 流水号定义Id
     * serialNumberSupplement
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


    public static SerialNumberRecordParams recordTableParams(SerialNumberBuildParams buildParams, SerialNumberMaintainBean maintain) {
        SerialNumberRecordParams recordParams = new SerialNumberRecordParams();
        recordParams.setSnId(buildParams.getSerialNumberId());
        recordParams.setObjectType(ObjectTypeEnum.TABLE.getType());
        recordParams.setObjectName(buildParams.getTableName());
        recordParams.setFieldName(buildParams.getFormField());
        recordParams.setDataUuid(buildParams.getDataUuid());
        recordParams.setKeyPart(maintain.getKeyPart());
        recordParams.setHeadPart(maintain.getHeadPart());
        recordParams.setLastPart(maintain.getLastPart());
        recordParams.setPointer(Integer.valueOf(maintain.getPointer()));
        String serialNo = maintain.getHeadPart() + maintain.getPointer() + maintain.getLastPart();
        recordParams.setSerialNo(serialNo);
        return recordParams;
    }


    public void paramValid() {
        Assert.hasText(this.snId, "流水号定义Id不能为空");
        Assert.notNull(this.objectType, "对象类型不能为空");
        Assert.hasText(this.objectName, "对象名称不能为空");
        Assert.hasText(this.fieldName, "字段名不能为空");
        Assert.hasText(this.dataUuid, "使用流水号的数据UUID不能为空");
        Assert.notNull(this.pointer, "指针不能为空");
        Assert.hasText(this.serialNo, "流水号不能为空");
        this.objectName = this.objectName.toLowerCase();
        this.fieldName = this.fieldName.toLowerCase();
    }

    public SerialNumberRelation relation() {
        SerialNumberRelation relation = new SerialNumberRelation();
        relation.setSnId(this.snId);
        relation.setObjectType(this.objectType);
        relation.setObjectName(this.objectName);
        relation.setFieldName(this.fieldName);
        return relation;
    }

    public SerialNumberRecord record(String relationUuid) {
        SerialNumberRecord record = new SerialNumberRecord();
        record.setRelationUuid(relationUuid);
        this.convert(record);
        return record;
    }

    public void convert(SerialNumberRecord record) {
        record.setDataUuid(this.dataUuid);
        record.setKeyPart(this.keyPart);
        record.setHeadPart(this.headPart);
        record.setLastPart(this.lastPart);
        record.setPointer(this.pointer);
        record.setSerialNo(this.serialNo);
    }


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
