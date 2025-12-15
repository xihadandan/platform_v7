/*
 * @(#)2019年3月11日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.bpm.engine.support.FlowShareData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * Description: 分支流数据
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年3月11日.1	zhulh		2019年3月11日		Create
 * </pre>
 * @date 2019年3月11日
 */
@ApiModel("分支流数据")
public class BranchTaskData extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4218119705891835693L;

    // 业务类别
    @ApiModelProperty("业务类别")
    private String businessType;
    // 业务角色
    @ApiModelProperty("业务角色")
    private String businessRole;
    // 流程实例UUID
    @ApiModelProperty("流程实例UUID")
    private String flowInstUuid;
    // 环节实例UUID
    @ApiModelProperty("环节实例UUID")
    private String taskInstUuid;
    // 承办显示位置
    @ApiModelProperty("承办显示位置")
    private String undertakeSituationPlaceHolder;
    // 信息分发显示位置
    @ApiModelProperty("信息分发显示位置")
    private String infoDistributionPlaceHolder;
    // 操作记录显示位置
    @ApiModelProperty("操作记录显示位置")
    private String operationRecordPlaceHolder;

    // 0:默认不可查看主流程  1:默认可查看主流程
    @ApiModelProperty("查看主流程（0:否，1:是）")
    private String childLookParent;
    // 允许主流程更改查看权限
    @ApiModelProperty("允许主流程更改查看权限（0:否，1:是）")
    private String parentSetChild;
    // <显示位置，承办信息列表>
    @ApiModelProperty("承办信息")
    private Map<String, List<FlowShareData>> shareDatas = Maps.newHashMapWithExpectedSize(0);
    // <显示位置，信息分发列表>
    @ApiModelProperty("信息分发")
    private Map<String, List<TaskInfoDistributionData>> distributeInfos = Maps.newHashMapWithExpectedSize(0);
    // <显示位置，操作记录列表>
    @ApiModelProperty("操作记录")
    private Map<String, List<TaskOperationData>> taskOperations = Maps.newHashMapWithExpectedSize(0);

    /**
     * 获取业务类别
     *
     * @return the businessType
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * 设置业务类别
     *
     * @param businessType 要设置的businessType
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /**
     * 获取业务角色
     *
     * @return the businessRole
     */
    public String getBusinessRole() {
        return businessRole;
    }

    /**
     * 设置业务角色
     *
     * @param businessRole 要设置的businessRole
     */
    public void setBusinessRole(String businessRole) {
        this.businessRole = businessRole;
    }

    /**
     * 获取流程实例UUID
     *
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * 设置流程实例UUID
     *
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * 获取环节实例UUID
     *
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * 设置环节实例UUID
     *
     * @param taskInstUuid 要设置的taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    /**
     * 设置获取承办显示位置
     *
     * @return the undertakeSituationPlaceHolder
     */
    public String getUndertakeSituationPlaceHolder() {
        return undertakeSituationPlaceHolder;
    }

    /**
     * 设置承办显示位置
     *
     * @param undertakeSituationPlaceHolder 要设置的undertakeSituationPlaceHolder
     */
    public void setUndertakeSituationPlaceHolder(String undertakeSituationPlaceHolder) {
        this.undertakeSituationPlaceHolder = undertakeSituationPlaceHolder;
    }

    /**
     * 获取信息分发显示位置
     *
     * @return the infoDistributionPlaceHolder
     */
    public String getInfoDistributionPlaceHolder() {
        return infoDistributionPlaceHolder;
    }

    /**
     * 设置信息分发显示位置
     *
     * @param infoDistributionPlaceHolder 要设置的infoDistributionPlaceHolder
     */
    public void setInfoDistributionPlaceHolder(String infoDistributionPlaceHolder) {
        this.infoDistributionPlaceHolder = infoDistributionPlaceHolder;
    }

    /**
     * 获取操作记录显示位置
     *
     * @return the operationRecordPlaceHolder
     */
    public String getOperationRecordPlaceHolder() {
        return operationRecordPlaceHolder;
    }

    /**
     * 设置操作记录显示位置
     *
     * @param operationRecordPlaceHolder 要设置的operationRecordPlaceHolder
     */
    public void setOperationRecordPlaceHolder(String operationRecordPlaceHolder) {
        this.operationRecordPlaceHolder = operationRecordPlaceHolder;
    }

    /**
     * 获取查看主流程
     *
     * @return
     */
    public String getChildLookParent() {
        return childLookParent;
    }

    /**
     * 设置查看主流程
     *
     * @param childLookParent
     */
    public void setChildLookParent(String childLookParent) {
        this.childLookParent = childLookParent;
    }

    /**
     * 获取允许主流程更改查看权限
     *
     * @return
     */
    public String getParentSetChild() {
        return parentSetChild;
    }

    /**
     * 设置允许主流程更改查看权限
     *
     * @param parentSetChild
     */
    public void setParentSetChild(String parentSetChild) {
        this.parentSetChild = parentSetChild;
    }

    /**
     * @return the shareDatas
     */
    public Map<String, List<FlowShareData>> getShareDatas() {
        return shareDatas;
    }

    /**
     * @param shareDatas 要设置的shareDatas
     */
    public void setShareDatas(Map<String, List<FlowShareData>> shareDatas) {
        this.shareDatas = shareDatas;
    }

    /**
     * @return the distributeInfos
     */
    public Map<String, List<TaskInfoDistributionData>> getDistributeInfos() {
        return distributeInfos;
    }

    /**
     * @param distributeInfos 要设置的distributeInfos
     */
    public void setDistributeInfos(Map<String, List<TaskInfoDistributionData>> distributeInfos) {
        this.distributeInfos = distributeInfos;
    }

    /**
     * @return the taskOperations
     */
    public Map<String, List<TaskOperationData>> getTaskOperations() {
        return taskOperations;
    }

    /**
     * @param taskOperations 要设置的taskOperations
     */
    public void setTaskOperations(Map<String, List<TaskOperationData>> taskOperations) {
        this.taskOperations = taskOperations;
    }

}
