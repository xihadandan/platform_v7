/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 系统配置实体
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-8.1	ruanhg		2014-8-8		Create
 * </pre>
 * @date 2014-8-8
 */
@Entity
@Table(name = "is_sys_properties")
@DynamicUpdate
@DynamicInsert
public class SysProperties extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7723754290631138415L;

    private String proCnName;//配置项中文名称

    private String proEnName;//配置项英文名称

    private String proValue;//配置项的值

    private String moduleId;//所属模块

    public String getProCnName() {
        return proCnName;
    }

    public void setProCnName(String proCnName) {
        this.proCnName = proCnName;
    }

    public String getProEnName() {
        return proEnName;
    }

    public void setProEnName(String proEnName) {
        this.proEnName = proEnName;
    }

    public String getProValue() {
        return proValue;
    }

    public void setProValue(String proValue) {
        this.proValue = proValue;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

}
