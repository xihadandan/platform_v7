/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.domain;

import com.wellsoft.pt.api.WellptObject;

import java.util.Date;

/**
 * Description: 流程定义信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
public class FlowDefinition extends WellptObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4402875647332085428L;

    // 流程定义ID
    private String id;

    // 流程定义名称
    private String name;

    // 版本号
    private Double version;

    // 使用表单的UUID
    private String formUuid;

    // 使用表单的名称
    private String formName;

    // 创建时间
    private Date createTime;

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
     * @return the version
     */
    public Double getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(Double version) {
        this.version = version;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the formName
     */
    public String getFormName() {
        return formName;
    }

    /**
     * @param formName 要设置的formName
     */
    public void setFormName(String formName) {
        this.formName = formName;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime 要设置的createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
