package com.wellsoft.pt.di.dto;

import java.io.Serializable;
import java.util.Date;

public class DiDataInterationLogDto implements Serializable {

    private String uuid;
    // 交换配置
    private String diConfigUuid;
    // 交换ID
    private String exchangeId;

    private Integer status;

    private Boolean isLatest;

    private Date dataTime;

    private String exception;

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

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}