/*
 * @(#)2018年9月25日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.script.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.context.validator.MaxLength;
import com.wellsoft.pt.basicdata.script.dto.CdScriptVariableDefinitionDto;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月25日.1	zhulh		2018年9月25日		Create
 * </pre>
 * @date 2018年9月25日
 */
@Entity
@Table(name = "CD_SCRIPT_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class CdScriptDefinitionEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 9009121583810218062L;

    // 名称
    @NotBlank
    private String name;
    // ID
    @NotBlank
    private String id;
    // 编号
    private String code;
    // 类型
    private String type;
    // 内容
    @MaxLength(max = 4000)
    private String content;
    // 备注
    @MaxLength(max = 500)
    private String remark;
    // 变量定义
    private String variablesDefinition;

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
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(String content) {
        this.content = content;
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
     * @return the variablesDefinition
     */
    public String getVariablesDefinition() {
        return variablesDefinition;
    }

    /**
     * @param variablesDefinition 要设置的variablesDefinition
     */
    public void setVariablesDefinition(String variablesDefinition) {
        this.variablesDefinition = variablesDefinition;
    }

    /**
     * @return the columnsDefinition
     */
    @SuppressWarnings("unchecked")
    @Transient
    public Collection<CdScriptVariableDefinitionDto> getVariableDefinitionDtos() {
        if (StringUtils.isBlank(variablesDefinition)) {
            return new ArrayList<CdScriptVariableDefinitionDto>();
        }
        return JSONArray.toCollection(JSONArray.fromObject(variablesDefinition), CdScriptVariableDefinitionDto.class);
    }

}
