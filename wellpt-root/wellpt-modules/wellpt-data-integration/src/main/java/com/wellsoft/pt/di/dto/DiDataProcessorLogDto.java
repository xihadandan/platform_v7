/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.dto;

import java.io.Serializable;
import java.util.Date;


/**
 * Description: 数据库表DI_DATA_PROCESSOR_LOG的对应的DTO类
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-23.1	chenq		2019-07-23		Create
 * </pre>
 * @date 2019-07-23
 */
public class DiDataProcessorLogDto implements Serializable {

    private static final long serialVersionUID = 1563853756340L;

    private String uuid;

    private String processorName;


    // 处理时间耗时
    private Integer timeConsuming;
    // 处理器UUID
    private String diProcessorUuid;
    // 数据交换配置UUID
    private String diConfigUuid;


    private Date createTime;

    private Date modifyTime;


    // 处理前的消息内容
    private String inMessage;
    // 处理后的消息内容
    private String outMessage;


    /**
     * @return the timeConsuming
     */
    public Integer getTimeConsuming() {
        return this.timeConsuming;
    }

    /**
     * @param timeConsuming
     */
    public void setTimeConsuming(Integer timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    /**
     * @return the diProcessorUuid
     */
    public String getDiProcessorUuid() {
        return this.diProcessorUuid;
    }

    /**
     * @param diProcessorUuid
     */
    public void setDiProcessorUuid(String diProcessorUuid) {
        this.diProcessorUuid = diProcessorUuid;
    }

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

    public String getInMessage() {
        return inMessage;
    }

    public void setInMessage(String inMessage) {
        this.inMessage = inMessage;
    }

    public String getOutMessage() {
        return outMessage;
    }

    public void setOutMessage(String outMessage) {
        this.outMessage = outMessage;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
