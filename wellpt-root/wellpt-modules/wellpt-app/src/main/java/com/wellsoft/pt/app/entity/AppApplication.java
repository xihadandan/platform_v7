/*
 * @(#)2015年11月16日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.context.jdbc.entity.Exposed;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 应用实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年11月16日.1	zhulh		2015年11月16日		Create
 * </pre>
 * @date 2015年11月16日
 */
@Entity
@Table(name = "APP_APPLICATION")
@DynamicUpdate
@DynamicInsert
@Exposed(name = "应用实体")
public class AppApplication extends TenantEntity implements ConfigurableIdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1554710315925228070L;

    @ApiModelProperty("名称")
    @NotBlank
    private String name;

    @ApiModelProperty("ID")
    @NotBlank
    private String id;

    @ApiModelProperty("编号")
    @NotBlank
    private String code;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("状态")
    private Boolean enabled;

    @ApiModelProperty("标识该应用对于普通用户是否可配置")
    private Boolean configurable;

    @ApiModelProperty("应用类别 1(UI交互)、2 (服务)")
    private Integer type;

    @ApiModelProperty("加载自定义JavaScript模块，以模块化的形式开发JavaScript，多个以逗号隔开")
    private String jsModule;

    @ApiModelProperty("关联的功能UUID")
    private String correlativeFunction;

    @ApiModelProperty("备注")
    private String remark;

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
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled 要设置的enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the configurable
     */
    public Boolean getConfigurable() {
        return configurable;
    }

    /**
     * @param configurable 要设置的configurable
     */
    public void setConfigurable(Boolean configurable) {
        this.configurable = configurable;
    }

    /**
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return the jsModule
     */
    public String getJsModule() {
        return jsModule;
    }

    /**
     * @param jsModule 要设置的jsModule
     */
    public void setJsModule(String jsModule) {
        this.jsModule = jsModule;
    }

    /**
     * @return the correlativeFunction
     */
    public String getCorrelativeFunction() {
        return correlativeFunction;
    }

    /**
     * @param correlativeFunction 要设置的correlativeFunction
     */
    public void setCorrelativeFunction(String correlativeFunction) {
        this.correlativeFunction = correlativeFunction;
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

}
