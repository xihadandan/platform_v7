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

import javax.persistence.*;
import java.sql.Clob;

/**
 * Description: 交换数据
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
@Table(name = "is_dx_exchange_data")
@DynamicUpdate
@DynamicInsert
public class DXExchangeData extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2401986533121922152L;

    @UnCloneable
    private DXExchangeBatch dXExchangeBatch;//批次号

    private String dataId;//统一查询号

    private Integer dataRecVer;//版本号

    private Clob text;//XML数据

    private String sourceDataUuid;//转换的源数据Uuid

    private String formUuid;//发送的表单id

    private String formDataUuid;//发送的表单数据id

    private String validData;//是否有效数据

    private String validMsg;//无效数据说明

    private String signDigest;//签名摘要

    private String params;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_uuid")
    public DXExchangeBatch getdXExchangeBatch() {
        return dXExchangeBatch;
    }

    public void setdXExchangeBatch(DXExchangeBatch dXExchangeBatch) {
        this.dXExchangeBatch = dXExchangeBatch;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Integer getDataRecVer() {
        return dataRecVer;
    }

    public void setDataRecVer(Integer dataRecVer) {
        this.dataRecVer = dataRecVer;
    }

    public Clob getText() {
        return text;
    }

    public void setText(Clob text) {
        this.text = text;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getFormDataUuid() {
        return formDataUuid;
    }

    public void setFormDataUuid(String formDataUuid) {
        this.formDataUuid = formDataUuid;
    }

    public String getValidData() {
        return validData;
    }

    public void setValidData(String validData) {
        this.validData = validData;
    }

    public String getValidMsg() {
        return validMsg;
    }

    public void setValidMsg(String validMsg) {
        this.validMsg = validMsg;
    }

    public String getSignDigest() {
        return signDigest;
    }

    public void setSignDigest(String signDigest) {
        this.signDigest = signDigest;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getSourceDataUuid() {
        return sourceDataUuid;
    }

    public void setSourceDataUuid(String sourceDataUuid) {
        this.sourceDataUuid = sourceDataUuid;
    }

}
