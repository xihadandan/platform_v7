/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.dto;

import java.io.Serializable;


/**
 * Description: 数据库表DI_DATA_PROCESSOR的对应的DTO类
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
public class DiDataProcessorDto implements Serializable {

    private static final long serialVersionUID = 1563853755694L;

    private String uuid;

    // 处理器参数(json)
    private String processorParameter;
    // 处理器类
    private String processorClass;
    // 数据交换处理器顺序
    private Integer seq;
    // 数据交换配置UUID
    private String diConfigUuid;
    // 数据交换配置类型
    private Integer type;

    /**
     * @return the processorParameter
     */
    public String getProcessorParameter() {
        return this.processorParameter;
    }

    /**
     * @param processorParameter
     */
    public void setProcessorParameter(String processorParameter) {
        this.processorParameter = processorParameter;
    }

    /**
     * @return the processorClass
     */
    public String getProcessorClass() {
        return this.processorClass;
    }

    /**
     * @param processorClass
     */
    public void setProcessorClass(String processorClass) {
        this.processorClass = processorClass;
    }

    /**
     * @return the seq
     */
    public Integer getSeq() {
        return this.seq;
    }

    /**
     * @param seq
     */
    public void setSeq(Integer seq) {
        this.seq = seq;
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

    /**
     * @return the type
     */
    public Integer getType() {
        return this.type;
    }

    /**
     * @param type
     */
    public void setType(Integer type) {
        this.type = type;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
