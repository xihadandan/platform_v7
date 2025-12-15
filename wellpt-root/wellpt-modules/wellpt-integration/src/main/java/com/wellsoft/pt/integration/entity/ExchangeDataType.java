/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Clob;

/**
 * Description: 交换数据数据类型
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-15.1	ruanhg		2013-11-15		Create
 * </pre>
 * @date 2013-11-15
 */
@Entity
@Table(name = "is_exchange_data_type")
@DynamicUpdate
@DynamicInsert
public class ExchangeDataType extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6265026439629210561L;
    //唯一识别号
    private String id;
    //编号用于排序
    private String code;
    //数据类型名称
    private String name;
    //动态表单id
    // TODO 实际存储的是dyform_form_definition.uuid字段
    private String formId;

    private String unitId;
    //生成XML数据格式
    @UnCloneable
    private Clob text;
    //是否永久留存0/1
    private Boolean retain;
    //终端数据表名称
    private String tableName;
    //业务类别id
    private String businessTypeId;
    //是否显示收件人（绑定抄送、密送）
    private Boolean showToUnit;
    //上传时限
    private Integer reportLimit;
    //接收时限
    private Integer receiveLimit;

    private String businessId;
    //是否查询自建系统数据
    private Boolean toSys;
    //解释对接系统
    private String unitSysSourceName;
    private String unitSysSourceId;
    //是否查询自建系统数据
    private Boolean synchronous;

    private Boolean merge;//是否合并发送，数据交换默认将dataList拆分发送

    public Integer getReportLimit() {
        return reportLimit;
    }

    public void setReportLimit(Integer reportLimit) {
        this.reportLimit = reportLimit;
    }

    public Integer getReceiveLimit() {
        return receiveLimit;
    }

    public void setReceiveLimit(Integer receiveLimit) {
        this.receiveLimit = receiveLimit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Clob getText() {
        return text;
    }

    public void setText(Clob text) {
        this.text = text;
    }

    public Boolean getRetain() {
        return retain;
    }

    public void setRetain(Boolean retain) {
        this.retain = retain;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public Boolean getShowToUnit() {
        return showToUnit;
    }

    public void setShowToUnit(Boolean showToUnit) {
        this.showToUnit = showToUnit;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Boolean getToSys() {
        return toSys;
    }

    public void setToSys(Boolean toSys) {
        this.toSys = toSys;
    }

    public Boolean getSynchronous() {
        return synchronous;
    }

    public void setSynchronous(Boolean synchronous) {
        this.synchronous = synchronous;
    }

    public String getUnitSysSourceName() {
        return unitSysSourceName;
    }

    public void setUnitSysSourceName(String unitSysSourceName) {
        this.unitSysSourceName = unitSysSourceName;
    }

    public String getUnitSysSourceId() {
        return unitSysSourceId;
    }

    public void setUnitSysSourceId(String unitSysSourceId) {
        this.unitSysSourceId = unitSysSourceId;
    }

    public Boolean getMerge() {
        return merge;
    }

    public void setMerge(Boolean merge) {
        this.merge = merge;
    }

}
