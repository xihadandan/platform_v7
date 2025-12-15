/*
 * @(#)2022-04-15 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Description: 数据库表APP_COLOR_SETTING的实体类
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-04-15.1	shenhb		2022-04-15		Create
 * </pre>
 * @date 2022-04-15
 */
@Entity
@Table(name = "APP_COLOR_SETTING")
@DynamicUpdate
@DynamicInsert
public class AppColorSettingEntity extends TenantEntity {

    private static final long serialVersionUID = 1650014559439L;

    // 颜色
    private String color;
    // 模块编码
    private String moduleCode;
    // id
    private String id;
    // 分类/类型
    private String type;
    // 名称
    private String name;

    /**
     * @return the color
     */
    public String getColor() {
        return this.color;
    }

    /**
     * @param color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the moduleCode
     */
    public String getModuleCode() {
        return this.moduleCode;
    }

    /**
     * @param moduleCode
     */
    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

}
