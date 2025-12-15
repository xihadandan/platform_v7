/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 职级
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-11-21.1	zyguo		2017-11-21		Create
 * </pre>
 * @date 2017-11-21
 */
@Entity
@Table(name = "MULTI_ORG_JOB_RANK")
@DynamicUpdate
@DynamicInsert
@ApiModel("职级")
public class MultiOrgJobRank extends TenantEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8870681631088128163L;
    // ID
    @ApiModelProperty("ID")
    private String id;
    // CODE
    @ApiModelProperty("编码")
    private String code;
    // NAME
    //@NotBlank
    @ApiModelProperty("名称")
    private String name;

    @NotBlank
    @ApiModelProperty("职级")
    private String jobRank;

    @ApiModelProperty("描述")
    private String describe;

    @ApiModelProperty("职务序列uuid")
    private String dutySeqUuid;

    @ApiModelProperty("职等")
    private Integer jobGrade;


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

    public String getJobRank() {
        return jobRank;
    }

    public void setJobRank(String jobRank) {
        this.jobRank = jobRank;
    }

    @Column(name = "\"DESCRIBE\"")
    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDutySeqUuid() {
        return dutySeqUuid;
    }

    public void setDutySeqUuid(String dutySeqUuid) {
        this.dutySeqUuid = dutySeqUuid;
    }

    public Integer getJobGrade() {
        return jobGrade;
    }

    public void setJobGrade(Integer jobGrade) {
        this.jobGrade = jobGrade;
    }

}
