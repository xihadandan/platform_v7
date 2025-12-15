/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Description: RecordElement.java
 *
 * @author zhulh
 * @date 2012-11-17
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-17.1	zhulh		2012-11-17		Create
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordElement implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7780651616580262651L;

    // 信息格式名称
    private String name;

    // 动态表单字段
    private String field;

    // 记录方式
    private String way;

    // 组装器默认按时间先后记录
    private String assembler;

    // 忽略空意见
    private String ignoreEmpty;

    // 字段不参与表单数据的变更校验
    private String fieldNotValidate;

    // 意见即时显示
    private String enableWysiwyg;

    // 信息格式ID
    private String value;

    // 作用的环节ID
    private String taskIds;

    // 前置条件开关
    private String enablePreCondition;

    // 前置条件列表
    private List<ConditionUnitElement> conditions;

    // 历史内容来源 1：流程信息记录，2：表单字段值
    private String contentOrigin;

    public String getContentOrigin() {
        return contentOrigin;
    }

    public void setContentOrigin(String contentOrigin) {
        this.contentOrigin = contentOrigin;
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
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field 要设置的field
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the way
     */
    public String getWay() {
        return way;
    }

    /**
     * @param way 要设置的way
     */
    public void setWay(String way) {
        this.way = way;
    }

    /**
     * @return the assembler
     */
    public String getAssembler() {
        return assembler;
    }

    /**
     * @param assembler 要设置的assembler
     */
    public void setAssembler(String assembler) {
        this.assembler = assembler;
    }

    /**
     * @return the ignoreEmpty
     */
    public String getIgnoreEmpty() {
        return ignoreEmpty;
    }

    /**
     * @param ignoreEmpty 要设置的ignoreEmpty
     */
    public void setIgnoreEmpty(String ignoreEmpty) {
        this.ignoreEmpty = ignoreEmpty;
    }

    /**
     * @return the fieldNotValidate
     */
    public String getFieldNotValidate() {
        return fieldNotValidate;
    }

    /**
     * @param fieldNotValidate 要设置的fieldNotValidate
     */
    public void setFieldNotValidate(String fieldNotValidate) {
        this.fieldNotValidate = fieldNotValidate;
    }

    /**
     * @return the enableWysiwyg
     */
    public String getEnableWysiwyg() {
        return enableWysiwyg;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsEnableWysiwyg() {
        return "1".equals(enableWysiwyg);
    }

    /**
     * @param enableWysiwyg 要设置的enableWysiwyg
     */
    public void setEnableWysiwyg(String enableWysiwyg) {
        this.enableWysiwyg = enableWysiwyg;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the taskIds
     */
    public String getTaskIds() {
        return taskIds;
    }

    /**
     * @param taskIds 要设置的taskIds
     */
    public void setTaskIds(String taskIds) {
        this.taskIds = taskIds;
    }

    /**
     * @return the enablePreCondition
     */
    public String getEnablePreCondition() {
        return enablePreCondition;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsEnablePreCondition() {
        return "1".equals(enablePreCondition);
    }

    /**
     * @param enablePreCondition 要设置的enablePreCondition
     */
    public void setEnablePreCondition(String enablePreCondition) {
        this.enablePreCondition = enablePreCondition;
    }

    /**
     * @return the conditions
     */
    public List<ConditionUnitElement> getConditions() {
        return conditions;
    }

    /**
     * @param conditions 要设置的conditions
     */
    public void setConditions(List<ConditionUnitElement> conditions) {
        this.conditions = conditions;
    }

}
