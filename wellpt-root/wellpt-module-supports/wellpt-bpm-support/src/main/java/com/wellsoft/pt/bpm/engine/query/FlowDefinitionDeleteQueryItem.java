/*
 * @(#)2015-6-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query;

import com.wellsoft.context.jdbc.entity.IdEntity;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-19.1	zhulh		2015-6-19		Create
 * </pre>
 * @date 2015-6-19
 */
public class FlowDefinitionDeleteQueryItem extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4316911620809230858L;

    /**
     * 分类
     */
    private String category;

    /**
     * 名称
     */
    private String name;

    /**
     * 别名
     */
    private String id;

    /**
     * 编号
     */
    private String code;

    /**
     * 版本
     */
    private Double version;

    /**
     * 对应表单UUID
     */
    private String formUuid;

    /**
     * 对应表单名称
     */
    private String formName;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否是自由流程
     */
    private Boolean freeed;

    /**
     * 等价流程定义ID
     */
    private String equalFlowId;

    /**
     * 二次开发配置JSON信息
     */
    private String developJson;

    // WF_FLOW_DEFINITION
    private String flowSchemaUuid;

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
     * @return the freeed
     */
    public Boolean getFreeed() {
        return freeed;
    }

    /**
     * @param freeed 要设置的freeed
     */
    public void setFreeed(Boolean freeed) {
        this.freeed = freeed;
    }

    /**
     * @return the equalFlowId
     */
    public String getEqualFlowId() {
        return equalFlowId;
    }

    /**
     * @param equalFlowId 要设置的equalFlowId
     */
    public void setEqualFlowId(String equalFlowId) {
        this.equalFlowId = equalFlowId;
    }

    /**
     * @return the developJson
     */
    public String getDevelopJson() {
        return developJson;
    }

    /**
     * @param developJson 要设置的developJson
     */
    public void setDevelopJson(String developJson) {
        this.developJson = developJson;
    }

    /**
     * @return the flowSchemaUuid
     */
    public String getFlowSchemaUuid() {
        return flowSchemaUuid;
    }

    /**
     * @param flowSchemaUuid 要设置的flowSchemaUuid
     */
    public void setFlowSchemaUuid(String flowSchemaUuid) {
        this.flowSchemaUuid = flowSchemaUuid;
    }

}
