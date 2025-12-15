/*
 * @(#)2014-2-27 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 流程实例运行时输入参数
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-2-27.1	zhulh		2014-2-27		Create
 * </pre>
 * @date 2014-2-27
 */
@Entity
@Table(name = "wf_flow_instance_param")
public class FlowInstanceParameter extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3566976647578706684L;

    // 流程实例UUID
    private String flowInstUuid;
    // 参数名
    private String name;
    // 参数值
    private String value;

    /**
     * 获取流程实例UUID
     *
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * 设置流程实例UUID
     *
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * 获取参数名
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置参数名
     *
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取参数值
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置参数值
     *
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
