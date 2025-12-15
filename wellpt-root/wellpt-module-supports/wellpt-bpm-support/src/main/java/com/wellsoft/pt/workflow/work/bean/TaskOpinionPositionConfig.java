/*
 * @(#)2021年9月25日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;

import java.util.List;

/**
 * Description: 环节意见立场配置信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年9月25日.1	zhulh		2021年9月25日		Create
 * </pre>
 * @date 2021年9月25日
 */
public class TaskOpinionPositionConfig extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6993224315286584593L;

    // 环节名称
    private String taskName;

    // 环节ID
    private String taskId;

    // 启用待办意见立场
    private boolean enableOpinionPosition;

    // 意见立场必填
    private boolean requiredOpinionPosition;

    // 显示用户意见立场值
    private boolean showUserOpinionPosition;

    // 显示意见立场统计
    private boolean showOpinionPositionStatistics;

    // 意见立场
    private List<OpinionPosition> opinionPositions = Lists.newArrayListWithExpectedSize(0);

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName 要设置的taskName
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the enableOpinionPosition
     */
    public boolean isEnableOpinionPosition() {
        return enableOpinionPosition;
    }

    /**
     * @param enableOpinionPosition 要设置的enableOpinionPosition
     */
    public void setEnableOpinionPosition(boolean enableOpinionPosition) {
        this.enableOpinionPosition = enableOpinionPosition;
    }

    /**
     * @return the requiredOpinionPosition
     */
    public boolean isRequiredOpinionPosition() {
        return requiredOpinionPosition;
    }

    /**
     * @param requiredOpinionPosition 要设置的requiredOpinionPosition
     */
    public void setRequiredOpinionPosition(boolean requiredOpinionPosition) {
        this.requiredOpinionPosition = requiredOpinionPosition;
    }

    /**
     * @return the showUserOpinionPosition
     */
    public boolean isShowUserOpinionPosition() {
        return showUserOpinionPosition;
    }

    /**
     * @param showUserOpinionPosition 要设置的showUserOpinionPosition
     */
    public void setShowUserOpinionPosition(boolean showUserOpinionPosition) {
        this.showUserOpinionPosition = showUserOpinionPosition;
    }

    /**
     * @return the showOpinionPositionStatistics
     */
    public boolean isShowOpinionPositionStatistics() {
        return showOpinionPositionStatistics;
    }

    /**
     * @param showOpinionPositionStatistics 要设置的showOpinionPositionStatistics
     */
    public void setShowOpinionPositionStatistics(boolean showOpinionPositionStatistics) {
        this.showOpinionPositionStatistics = showOpinionPositionStatistics;
    }

    /**
     * @return the opinionPositions
     */
    public List<OpinionPosition> getOpinionPositions() {
        return opinionPositions;
    }

    /**
     * @param opinionPositions 要设置的opinionPositions
     */
    public void setOpinionPositions(List<OpinionPosition> opinionPositions) {
        this.opinionPositions = opinionPositions;
    }

}
