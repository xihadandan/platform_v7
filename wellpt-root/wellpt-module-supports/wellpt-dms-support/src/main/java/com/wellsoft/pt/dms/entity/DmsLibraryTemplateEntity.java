/*
 * @(#)11/26/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 11/26/25.1	    zhulh		11/26/25		    Create
 * </pre>
 * @date 11/26/25
 */
@Entity
@Table(name = "DMS_LIBRARY_TEMPLATE")
@DynamicUpdate
@DynamicInsert
@ApiModel("库模板")
public class DmsLibraryTemplateEntity extends SysEntity {
    private static final long serialVersionUID = -4396938313586712938L;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("编号")
    private String code;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("定义JSON信息")
    private String definitionJson;

    @ApiModelProperty("使用次数")
    private Integer usedCount;

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
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon 要设置的icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
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
