/*
 * @(#)2021-01-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.imgcategory.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;


/**
 * Description: 数据库表CD_IMG_CATEGORY的实体类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-01-22.1	zhongzh		2021-01-22		Create
 * </pre>
 * @date 2021-01-22
 */
@Entity
@Table(name = "CD_IMG_CATEGORY")
@DynamicUpdate
@DynamicInsert
public class CdImgCategoryEntity extends TenantEntity {

    private static final long serialVersionUID = 1611281606517L;


    private String code;
    // 分类描述
    private String description;
    // 图标颜色
    private String color;
    // 图标
    private String icon;
    // 分类名称
    private String name;

    private List<AppDefElementI18nEntity> i18ns;

    /**
     * @return the code
     */
    public String getCode() {
        return this.code;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

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
     * @return the icon
     */
    public String getIcon() {
        return this.icon;
    }

    /**
     * @param icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
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

    @Transient
    public List<AppDefElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<AppDefElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
