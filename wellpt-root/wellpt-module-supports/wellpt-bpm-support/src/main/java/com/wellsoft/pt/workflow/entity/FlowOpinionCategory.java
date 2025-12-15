/*
 * @(#)2013-7-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 意见立场分类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-7-30.1	zhulh		2013-7-30		Create
 * </pre>
 * @date 2013-7-30
 */
@Entity
@Table(name = "WF_DEF_OPINION_CATEGORY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "entity")
@DynamicUpdate
@DynamicInsert
@ApiModel("流程意见分类")
public class FlowOpinionCategory extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4887640190226185105L;

    // 分类名称
    @ApiModelProperty("分类名称")
    private String name;

    // ID
    @ApiModelProperty("ID")
    private String id;

    // 编号
    @ApiModelProperty("编号")
    private String code;

    // 归属的行业标记
    @ApiModelProperty("归属的行业标记")
    private String businessFlag;

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
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    public String getBusinessFlag() {
        return businessFlag;
    }

    public void setBusinessFlag(String businessFlag) {
        this.businessFlag = businessFlag;
    }

}
