/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 交换数据路由结果记录
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
@Table(name = "is_exchange_data_route")
@DynamicUpdate
@DynamicInsert
public class ExchangeDataRoute extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3629401808466565878L;
    //单位id
    private String unitId;
    //统一查询号
    private String dataId;
    //	//数据版本
    //	private Integer recVer;
    //上传结果
    private Boolean status;
    //上传结果说明
    private String msg;

    private String matterId;

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    //	public Integer getRecVer() {
    //		return recVer;
    //	}
    //
    //	public void setRecVer(Integer recVer) {
    //		this.recVer = recVer;
    //	}

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMatterId() {
        return matterId;
    }

    public void setMatterId(String matterId) {
        this.matterId = matterId;
    }

}
