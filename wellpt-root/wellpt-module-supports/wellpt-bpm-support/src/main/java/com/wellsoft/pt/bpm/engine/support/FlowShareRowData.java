/*
 * @(#)2019年2月27日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicColumnValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年2月27日.1	zhulh		2019年2月27日		Create
 * </pre>
 * @date 2019年2月27日
 */
@ApiModel("办理信息")
public class FlowShareRowData extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1260059518115683992L;

    // 流程定义ID
    @ApiModelProperty("流程定义ID")
    private String flowDefId;
    // 流程实例UUID
    @ApiModelProperty("流程实例UUID")
    private String flowInstUuid;
    // 环节实例UUID
    @ApiModelProperty("环节实例UUID")
    private String taskInstUuid;
    // 表单定义UUID
    @ApiModelProperty("表单定义UUID")
    private String formUuid;
    // 表单数据UUID
    @ApiModelProperty("表单数据UUID")
    private String dataUuid;
    // 归属环节ID
    @ApiModelProperty("归属环节ID")
    private String belongToTaskId;
    // 归属环节实例UUID
    @ApiModelProperty("归属环节实例UUID")
    private String belongToTaskInstUuid;
    // 归属流程实例UUID
    @ApiModelProperty("归属流程实例UUID")
    private String belongToFlowInstUuid;
    // 流程计时状态
    @ApiModelProperty("流程计时状态")
    private Integer timingState;
    // 分发完成状态
    @ApiModelProperty("分发完成状态")
    private Integer dispatchState;
    // 分发结果信息
    @ApiModelProperty("分发结果信息")
    private String dispatchResultMsg;

    private String flowLabelId;

    private String flowLabel;

    // 自定义列数据
    @ApiModelProperty("自定义列数据")
    private List<CustomDynamicColumnValue> columnValues;

    private Map<String, String> i18ns = Maps.newHashMap();

    /**
     * @return the flowDefId
     */
    public String getFlowDefId() {
        return flowDefId;
    }

    /**
     * @param flowDefId 要设置的flowDefId
     */
    public void setFlowDefId(String flowDefId) {
        this.flowDefId = flowDefId;
    }

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * @param taskInstUuid 要设置的taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
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
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the belongToTaskId
     */
    public String getBelongToTaskId() {
        return belongToTaskId;
    }

    /**
     * @param belongToTaskId 要设置的belongToTaskId
     */
    public void setBelongToTaskId(String belongToTaskId) {
        this.belongToTaskId = belongToTaskId;
    }

    /**
     * @return the belongToTaskInstUuid
     */
    public String getBelongToTaskInstUuid() {
        return belongToTaskInstUuid;
    }

    /**
     * @param belongToTaskInstUuid 要设置的belongToTaskInstUuid
     */
    public void setBelongToTaskInstUuid(String belongToTaskInstUuid) {
        this.belongToTaskInstUuid = belongToTaskInstUuid;
    }

    /**
     * @return the belongToFlowInstUuid
     */
    public String getBelongToFlowInstUuid() {
        return belongToFlowInstUuid;
    }

    /**
     * @param belongToFlowInstUuid 要设置的belongToFlowInstUuid
     */
    public void setBelongToFlowInstUuid(String belongToFlowInstUuid) {
        this.belongToFlowInstUuid = belongToFlowInstUuid;
    }

    public Integer getTimingState() {
        return timingState;
    }

    public void setTimingState(Integer timingState) {
        this.timingState = timingState;
    }

    /**
     * @return the dispatchState
     */
    public Integer getDispatchState() {
        return dispatchState;
    }

    /**
     * @param dispatchState 要设置的dispatchState
     */
    public void setDispatchState(Integer dispatchState) {
        this.dispatchState = dispatchState;
    }

    /**
     * @return the dispatchResultMsg
     */
    public String getDispatchResultMsg() {
        return dispatchResultMsg;
    }

    /**
     * @param dispatchResultMsg 要设置的dispatchResultMsg
     */
    public void setDispatchResultMsg(String dispatchResultMsg) {
        this.dispatchResultMsg = dispatchResultMsg;
    }

    /**
     * @return the columnValues
     */
    public List<CustomDynamicColumnValue> getColumnValues() {
        return columnValues;
    }

    /**
     * @param columnValues 要设置的columnValues
     */
    public void setColumnValues(List<CustomDynamicColumnValue> columnValues) {
        this.columnValues = columnValues;
    }

    public Map<String, String> getI18ns() {
        return i18ns;
    }

    public void setI18ns(Map<String, String> i18ns) {
        this.i18ns = i18ns;
    }

    public String getFlowLabelId() {
        return flowLabelId;
    }

    public void setFlowLabelId(String flowLabelId) {
        this.flowLabelId = flowLabelId;
    }

    public String getFlowLabel() {
        return flowLabel;
    }

    public void setFlowLabel(String flowLabel) {
        this.flowLabel = flowLabel;
    }
}
