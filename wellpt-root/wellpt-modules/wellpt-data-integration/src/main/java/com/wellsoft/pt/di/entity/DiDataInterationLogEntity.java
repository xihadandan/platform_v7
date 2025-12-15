/*
 * @(#)2019-08-10 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;


/**
 * Description: 数据库表DI_DATA_INTERATION_LOG的实体类
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-08-10.1	chenq		2019-08-10		Create
 * </pre>
 * @date 2019-08-10
 */
@Entity
@Table(name = "DI_DATA_INTERATION_LOG")
@DynamicUpdate
@DynamicInsert
public class DiDataInterationLogEntity extends IdEntity {

    private static final long serialVersionUID = 1565422141936L;

    // 交换配置
    private String diConfigUuid;
    // 交换ID
    private String exchangeId;

    private Integer status;

    private Boolean isLatest;


    private Integer pageIndex;

    private Integer totalPage;

    private Integer pageLimit;

    private String exception;

    private Date dataBeginTime;

    private Date dataEndTime;

    /**
     * @return the diConfigUuid
     */
    public String getDiConfigUuid() {
        return this.diConfigUuid;
    }

    /**
     * @param diConfigUuid
     */
    public void setDiConfigUuid(String diConfigUuid) {
        this.diConfigUuid = diConfigUuid;
    }

    /**
     * @return the exchangeId
     */
    public String getExchangeId() {
        return this.exchangeId;
    }

    /**
     * @param exchangeId
     */
    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsLatest() {
        return isLatest;
    }

    public void setIsLatest(Boolean latest) {
        isLatest = latest;
    }


    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getPageLimit() {
        return pageLimit;
    }

    public void setPageLimit(Integer pageLimit) {
        this.pageLimit = pageLimit;
    }


    public Date getDataBeginTime() {
        return dataBeginTime;
    }

    public void setDataBeginTime(Date dataBeginTime) {
        this.dataBeginTime = dataBeginTime;
    }

    public Date getDataEndTime() {
        return dataEndTime;
    }

    public void setDataEndTime(Date dataEndTime) {
        this.dataEndTime = dataEndTime;
    }
}
