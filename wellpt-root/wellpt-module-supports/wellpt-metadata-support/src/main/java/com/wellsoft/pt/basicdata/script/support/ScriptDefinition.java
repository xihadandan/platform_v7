/*
 * @(#)2018年9月29日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.script.support;

import com.wellsoft.context.base.BaseObject;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月29日.1	zhulh		2018年9月29日		Create
 * </pre>
 * @date 2018年9月29日
 */
public class ScriptDefinition extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5281992844912277301L;

    // 名称
    private String name;
    // ID
    private String id;
    // 编号
    private String code;
    // 类型
    private String type;
    // 内容
    private String content;
    // 备注
    private String remark;
    // 变量定义
    private List<ScriptVariableDefinition> variableDefinitions;

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
     * @return the variableDefinitions
     */
    public List<ScriptVariableDefinition> getVariableDefinitions() {
        return variableDefinitions;
    }

    /**
     * @param variableDefinitions 要设置的variableDefinitions
     */
    public void setVariableDefinitions(List<ScriptVariableDefinition> variableDefinitions) {
        this.variableDefinitions = variableDefinitions;
    }

}
