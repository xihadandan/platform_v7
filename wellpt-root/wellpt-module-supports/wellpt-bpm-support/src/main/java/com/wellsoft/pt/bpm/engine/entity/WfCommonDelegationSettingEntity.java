/*
 * @(#)2018年8月14日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 流程常用工作委托设置
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年8月14日.1	zhulh		2018年8月14日		Create
 * </pre>
 * @date 2018年8月14日
 */
@Entity
@Table(name = "wf_common_delegation_setting")
@DynamicUpdate
@DynamicInsert
@ApiModel("流程常用工作委托设置")
public class WfCommonDelegationSettingEntity extends SysEntity {

    private static final long serialVersionUID = 3073477013110994309L;

    @ApiModelProperty("常用委托名称")
    private String name;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("委托定义JSON")
    private String definitionJson;

    @ApiModelProperty("使用次数")
    private Integer usedCount;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the definitionJson
     */
    public String getDefinitionJson() {
        return definitionJson;
    }

    /**
     * @param definitionJson 要设置的definitionJson
     */
    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    /**
     * @return the usedCount
     */
    public Integer getUsedCount() {
        return usedCount;
    }

    /**
     * @param usedCount 要设置的usedCount
     */
    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

}
