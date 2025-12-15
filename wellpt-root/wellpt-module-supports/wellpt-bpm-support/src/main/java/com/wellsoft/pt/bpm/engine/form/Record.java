/*
 * @(#)2013-5-2 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.form;

import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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
 * 2013-5-2.1	zhulh		2013-5-2		Create
 * </pre>
 * @date 2013-5-2
 */
@ApiModel("信息记录")
public class Record extends BaseObject {
    // 不替换
    public static final String WAY_NO_REPLACE = "1";
    // 替换原值
    public static final String WAY_REPLACE = "2";
    // 附加
    public static final String WAY_APPEND = "3";
    // 历史内容来源 1：流程信息记录，2：表单字段值
    public static final String CONTENT_ORIGIN_FLOW_RECORD = "1";
    public static final String CONTENT_ORIGIN_FORM_FIELD = "2";
    // 忽略空意见
    public static final String IGNORE_EMPTY_VALUE_TRUE = "1";
    // 字段不参与表单数据的变更校验
    public static final String FIELD_NOT_VALIDATE_TRUE = "1";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8150889044009312195L;
    // 信息格式名称
    @ApiModelProperty("信息格式名称")
    private String name;

    // 动态表单字段
    @ApiModelProperty("动态表单字段")
    private String field;

    // 记录方式
    @ApiModelProperty("记录方式")
    private String way;

    // 组装器默认按时间先后记录
    @ApiModelProperty("组装器默认按时间先后记录")
    private String assembler;

    // 忽略空意见
    @ApiModelProperty("忽略空意见，1是0否")
    private String ignoreEmpty;

    // 字段不参与表单数据的变更校验
    @ApiModelProperty("字段不参与表单数据的变更校验，1是0否")
    private String fieldNotValidate;

    // 忽略空意见
    @ApiModelProperty("意见即时显示")
    private boolean enableWysiwyg;

    // 信息格式ID
    @ApiModelProperty("信息格式ID")
    private String value;

    // 前置条件开关
    @ApiModelProperty("前置条件开关")
    private boolean enablePreCondition;

    // 前置条件开关
    @ApiModelProperty("前置条件开关")
    private List<RecordCondition> recordConditions;

    // 信息格式是否包含办理意见变量
    @ApiModelProperty("信息格式是否包含办理意见变量")
    private boolean includeOpinionTextVariable;

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
    public boolean isEnableWysiwyg() {
        return enableWysiwyg;
    }

    /**
     * @param enableWysiwyg 要设置的enableWysiwyg
     */
    public void setEnableWysiwyg(boolean enableWysiwyg) {
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
     * @return the enablePreCondition
     */
    public boolean isEnablePreCondition() {
        return enablePreCondition;
    }

    /**
     * @param enablePreCondition 要设置的enablePreCondition
     */
    public void setEnablePreCondition(boolean enablePreCondition) {
        this.enablePreCondition = enablePreCondition;
    }

    /**
     * @return the recordConditions
     */
    public List<RecordCondition> getRecordConditions() {
        return recordConditions;
    }

    /**
     * @param recordConditions 要设置的recordConditions
     */
    public void setRecordConditions(List<RecordCondition> recordConditions) {
        this.recordConditions = recordConditions;
    }

    /**
     * @return the includeOpinionTextVariable
     */
    public boolean isIncludeOpinionTextVariable() {
        return includeOpinionTextVariable;
    }

    /**
     * @param includeOpinionTextVariable 要设置的includeOpinionTextVariable
     */
    public void setIncludeOpinionTextVariable(boolean includeOpinionTextVariable) {
        this.includeOpinionTextVariable = includeOpinionTextVariable;
    }

}
