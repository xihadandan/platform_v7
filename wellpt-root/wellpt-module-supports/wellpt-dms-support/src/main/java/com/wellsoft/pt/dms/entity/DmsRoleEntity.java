/*
 * @(#)Dec 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.jpa.service.UUIDGeneratorIndicate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 15, 2017.1	zhulh		Dec 15, 2017		Create
 * </pre>
 * @date Dec 15, 2017
 */
@Entity
@Table(name = "DMS_ROLE")
@DynamicUpdate
@DynamicInsert
public class DmsRoleEntity extends TenantEntity implements ConfigurableIdEntity, UUIDGeneratorIndicate {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2864533594083564843L;

    // 名称
    @NotBlank
    private String name;
    // ID
    private String id;
    // 编号
    private String code;
    // 分类
    private String category;
    // 备注
    private String remark;
    // 分配的操作，多个以分号隔开，冗余
    private String actions;
    // 定义JSON信息
    private String definitionJson;
    // 是否内置
    private Boolean builtIn;

    // 归属系统
    private String system;
    // 归属租户
    private String tenant;

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

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category 要设置的category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the actions
     */
    public String getActions() {
        return actions;
    }

    /**
     * @param actions 要设置的actions
     */
    public void setActions(String actions) {
        this.actions = actions;
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
     * @return the builtIn
     */
    public Boolean getBuiltIn() {
        return builtIn;
    }

    /**
     * @param builtIn 要设置的builtIn
     */
    public void setBuiltIn(Boolean builtIn) {
        this.builtIn = builtIn;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

}
