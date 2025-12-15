package com.wellsoft.pt.api.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: api指令
 *
 * @author chenq
 * @date 2018/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/13    chenq		2018/8/13		Create
 * </pre>
 */
@Table(name = "API_COMMAND")
@DynamicInsert
@DynamicUpdate
@Entity
public class ApiCommandEntity extends TenantEntity {

    private static final long serialVersionUID = -7634480753257820585L;


    private String serialNumber;//流水号

    private String idempotentKey;//幂等值

    private Integer retryNum;//重试次数

    private Date nextRetryTime;//下一次重试时间

    private String bizCorrelationKey;//业务关联值

    private String businessType;//业务类型描述

    private Integer status;//指令状态位

    private String outSystemCode;//外部系统编码

    private String serviceCode;//服务调用编码

    private Date responseTime;//响应时间

    private String apiMode;


    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getIdempotentKey() {
        return idempotentKey;
    }

    public void setIdempotentKey(String idempotentKey) {
        this.idempotentKey = idempotentKey;
    }

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public Date getNextRetryTime() {
        return nextRetryTime;
    }

    public void setNextRetryTime(Date nextRetryTime) {
        this.nextRetryTime = nextRetryTime;
    }

    public String getBizCorrelationKey() {
        return bizCorrelationKey;
    }

    public void setBizCorrelationKey(String bizCorrelationKey) {
        this.bizCorrelationKey = bizCorrelationKey;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOutSystemCode() {
        return outSystemCode;
    }

    public void setOutSystemCode(String outSystemCode) {
        this.outSystemCode = outSystemCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }


    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public String getApiMode() {
        return apiMode;
    }

    public void setApiMode(String apiMode) {
        this.apiMode = apiMode;
    }
}
