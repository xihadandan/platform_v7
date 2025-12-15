/*
 * @(#)2018年8月11日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;


/**
 * Description: 子流程数据
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年8月11日.1	zhulh		2018年8月11日		Create
 * </pre>
 * @date 2018年8月11日
 */
@ApiModel("子流程数据")
public class SubTaskData extends BranchTaskData {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3343430104653777606L;

    // 主环节实例UUID
    @ApiModelProperty("主环节实例UUID")
    private String parentTaskInstUuid;
    // 主流程实例UUID
    @ApiModelProperty("主流程实例UUID")
    private String parentFlowInstUuid;
    // 要加载的子流程实例UUID列表，为空获取所有
    @ApiModelProperty("要加载的子流程实例UUID列表，为空获取所有")
    private List<String> subFlowInstUuids;

    // 是否作为父流程实例
    private boolean parentFlowInstance;

    // 是否作为子流程实例
    private boolean childFlowInstance;

    // 主流程作为子流程承办显示位置
    private String asChildUndertakeSituationPlaceHolder;
    // 主流程作为子流程信息分发显示位置
    private String asChildInfoDistributionPlaceHolder;
    // 主流程作为子流程操作记录显示位置
    private String asChildOperationRecordPlaceHolder;

    // 列表展开
    private String expandList;

    /**
     * @return the parentTaskInstUuid
     */
    public String getParentTaskInstUuid() {
        return parentTaskInstUuid;
    }

    /**
     * @param parentTaskInstUuid 要设置的parentTaskInstUuid
     */
    public void setParentTaskInstUuid(String parentTaskInstUuid) {
        this.parentTaskInstUuid = parentTaskInstUuid;
    }

    /**
     * @return the parentFlowInstUuid
     */
    public String getParentFlowInstUuid() {
        return parentFlowInstUuid;
    }

    /**
     * @param parentFlowInstUuid 要设置的parentFlowInstUuid
     */
    public void setParentFlowInstUuid(String parentFlowInstUuid) {
        this.parentFlowInstUuid = parentFlowInstUuid;
    }

    /**
     * @return the subFlowInstUuids
     */
    public List<String> getSubFlowInstUuids() {
        return subFlowInstUuids;
    }

    /**
     * @param subFlowInstUuids 要设置的subFlowInstUuids
     */
    public void setSubFlowInstUuids(List<String> subFlowInstUuids) {
        this.subFlowInstUuids = subFlowInstUuids;
    }

    /**
     * @return the parentFlowInstance
     */
    public boolean isParentFlowInstance() {
        return parentFlowInstance;
    }

    /**
     * @param parentFlowInstance 要设置的parentFlowInstance
     */
    public void setParentFlowInstance(boolean parentFlowInstance) {
        this.parentFlowInstance = parentFlowInstance;
    }

    /**
     * @return the childFlowInstance
     */
    public boolean isChildFlowInstance() {
        return childFlowInstance;
    }

    /**
     * @param childFlowInstance 要设置的childFlowInstance
     */
    public void setChildFlowInstance(boolean childFlowInstance) {
        this.childFlowInstance = childFlowInstance;
    }

    /**
     * @return the asChildUndertakeSituationPlaceHolder
     */
    public String getAsChildUndertakeSituationPlaceHolder() {
        return asChildUndertakeSituationPlaceHolder;
    }

    /**
     * @param asChildUndertakeSituationPlaceHolder 要设置的asChildUndertakeSituationPlaceHolder
     */
    public void setAsChildUndertakeSituationPlaceHolder(String asChildUndertakeSituationPlaceHolder) {
        this.asChildUndertakeSituationPlaceHolder = asChildUndertakeSituationPlaceHolder;
    }

    /**
     * @return the asChildInfoDistributionPlaceHolder
     */
    public String getAsChildInfoDistributionPlaceHolder() {
        return asChildInfoDistributionPlaceHolder;
    }

    /**
     * @param asChildInfoDistributionPlaceHolder 要设置的asChildInfoDistributionPlaceHolder
     */
    public void setAsChildInfoDistributionPlaceHolder(String asChildInfoDistributionPlaceHolder) {
        this.asChildInfoDistributionPlaceHolder = asChildInfoDistributionPlaceHolder;
    }

    /**
     * @return the asChildOperationRecordPlaceHolder
     */
    public String getAsChildOperationRecordPlaceHolder() {
        return asChildOperationRecordPlaceHolder;
    }

    /**
     * @param asChildOperationRecordPlaceHolder 要设置的asChildOperationRecordPlaceHolder
     */
    public void setAsChildOperationRecordPlaceHolder(String asChildOperationRecordPlaceHolder) {
        this.asChildOperationRecordPlaceHolder = asChildOperationRecordPlaceHolder;
    }

    public String getExpandList() {
        return expandList;
    }

    public void setExpandList(String expandList) {
        this.expandList = expandList;
    }
}
