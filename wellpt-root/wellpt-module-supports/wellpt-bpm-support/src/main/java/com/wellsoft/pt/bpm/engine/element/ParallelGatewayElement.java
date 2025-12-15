/*
 * @(#)2014-8-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
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
 * 2014-8-24.1	zhulh		2014-8-24		Create
 * </pre>
 * @date 2014-8-24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParallelGatewayElement implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1502457530566306952L;

    // 分支模式(1 单一分支 2 多路分支 3 全部分支)
    private int forkMode = 1;

    // 提交时可选择流向(0 不选择 1 选择)
    private String chooseForkingDirection;

    // 业务类别
    private String businessType;

    // 业务类别名称
    private String businessTypeName;

    // 业务角色
    private String businessRole;

    // 业务角色名称
    private String businessRoleName;

    // 跟进人员
    private List<UserUnitElement> branchTaskMonitors;

    // 办理进度显示位置
    private String undertakeSituationPlaceHolder;

    // 办理进度列表标题表达式
    private String undertakeSituationTitleExpression;

    // 按钮配置
    private List<ButtonElement> undertakeSituationButtons;

    // 列配置
    private List<ColumnElement> undertakeSituationColumns;

    private String sortRule;
    // 排序列配置
    private List<OrderElement> undertakeSituationOrders;

    // 信息分发显示位置
    private String infoDistributionPlaceHolder;

    // 信息分发列表标题表达式
    private String infoDistributionTitleExpression;

    // 操作记录显示位置
    private String operationRecordPlaceHolder;

    // 操作记录列表标题表达式
    private String operationRecordTitleExpression;

    // 聚合模式(1 单一聚合 2 多路聚合 3 全部聚合)
    private int joinMode = 1;

    /**
     * @return the forkMode
     */
    public int getForkMode() {
        return forkMode;
    }

    /**
     * @param forkMode 要设置的forkMode
     */
    public void setForkMode(int forkMode) {
        this.forkMode = forkMode;
    }

    /**
     * @return the chooseForkingDirection
     */
    public String getChooseForkingDirection() {
        return chooseForkingDirection;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsChooseForkingDirection() {
        return 2 == forkMode && "1".equals(chooseForkingDirection);
    }

    /**
     * @param chooseForkingDirection 要设置的chooseForkingDirection
     */
    public void setChooseForkingDirection(String chooseForkingDirection) {
        this.chooseForkingDirection = chooseForkingDirection;
    }

    /**
     * @return the businessType
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * @param businessType 要设置的businessType
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /**
     * @return the businessTypeName
     */
    public String getBusinessTypeName() {
        return businessTypeName;
    }

    /**
     * @param businessTypeName 要设置的businessTypeName
     */
    public void setBusinessTypeName(String businessTypeName) {
        this.businessTypeName = businessTypeName;
    }

    /**
     * @return the businessRole
     */
    public String getBusinessRole() {
        return businessRole;
    }

    /**
     * @param businessRole 要设置的businessRole
     */
    public void setBusinessRole(String businessRole) {
        this.businessRole = businessRole;
    }

    /**
     * @return the businessRoleName
     */
    public String getBusinessRoleName() {
        return businessRoleName;
    }

    /**
     * @param businessRoleName 要设置的businessRoleName
     */
    public void setBusinessRoleName(String businessRoleName) {
        this.businessRoleName = businessRoleName;
    }

    /**
     * @return the branchTaskMonitors
     */
    public List<UserUnitElement> getBranchTaskMonitors() {
        return branchTaskMonitors;
    }

    /**
     * @param branchTaskMonitors 要设置的branchTaskMonitors
     */
    public void setBranchTaskMonitors(List<UserUnitElement> branchTaskMonitors) {
        this.branchTaskMonitors = branchTaskMonitors;
    }

    /**
     * @return the undertakeSituationPlaceHolder
     */
    public String getUndertakeSituationPlaceHolder() {
        return undertakeSituationPlaceHolder;
    }

    /**
     * @param undertakeSituationPlaceHolder 要设置的undertakeSituationPlaceHolder
     */
    public void setUndertakeSituationPlaceHolder(String undertakeSituationPlaceHolder) {
        this.undertakeSituationPlaceHolder = undertakeSituationPlaceHolder;
    }

    /**
     * @return the undertakeSituationTitleExpression
     */
    public String getUndertakeSituationTitleExpression() {
        return undertakeSituationTitleExpression;
    }

    /**
     * @param undertakeSituationTitleExpression 要设置的undertakeSituationTitleExpression
     */
    public void setUndertakeSituationTitleExpression(String undertakeSituationTitleExpression) {
        this.undertakeSituationTitleExpression = undertakeSituationTitleExpression;
    }

    /**
     * @return the undertakeSituationButtons
     */
    public List<ButtonElement> getUndertakeSituationButtons() {
        return undertakeSituationButtons;
    }

    /**
     * @param undertakeSituationButtons 要设置的undertakeSituationButtons
     */
    public void setUndertakeSituationButtons(List<ButtonElement> undertakeSituationButtons) {
        this.undertakeSituationButtons = undertakeSituationButtons;
    }

    /**
     * @return the undertakeSituationColumns
     */
    public List<ColumnElement> getUndertakeSituationColumns() {
        return undertakeSituationColumns;
    }

    /**
     * @param undertakeSituationColumns 要设置的undertakeSituationColumns
     */
    public void setUndertakeSituationColumns(List<ColumnElement> undertakeSituationColumns) {
        this.undertakeSituationColumns = undertakeSituationColumns;
    }

    public String getSortRule() {
        return sortRule;
    }

    public void setSortRule(String sortRule) {
        this.sortRule = sortRule;
    }

    public List<OrderElement> getUndertakeSituationOrders() {
        return undertakeSituationOrders;
    }

    public void setUndertakeSituationOrders(List<OrderElement> undertakeSituationOrders) {
        this.undertakeSituationOrders = undertakeSituationOrders;
    }

    /**
     * @return the infoDistributionPlaceHolder
     */
    public String getInfoDistributionPlaceHolder() {
        return infoDistributionPlaceHolder;
    }

    /**
     * @param infoDistributionPlaceHolder 要设置的infoDistributionPlaceHolder
     */
    public void setInfoDistributionPlaceHolder(String infoDistributionPlaceHolder) {
        this.infoDistributionPlaceHolder = infoDistributionPlaceHolder;
    }

    /**
     * @return the infoDistributionTitleExpression
     */
    public String getInfoDistributionTitleExpression() {
        return infoDistributionTitleExpression;
    }

    /**
     * @param infoDistributionTitleExpression 要设置的infoDistributionTitleExpression
     */
    public void setInfoDistributionTitleExpression(String infoDistributionTitleExpression) {
        this.infoDistributionTitleExpression = infoDistributionTitleExpression;
    }

    /**
     * @return the operationRecordPlaceHolder
     */
    public String getOperationRecordPlaceHolder() {
        return operationRecordPlaceHolder;
    }

    /**
     * @param operationRecordPlaceHolder 要设置的operationRecordPlaceHolder
     */
    public void setOperationRecordPlaceHolder(String operationRecordPlaceHolder) {
        this.operationRecordPlaceHolder = operationRecordPlaceHolder;
    }

    /**
     * @return the operationRecordTitleExpression
     */
    public String getOperationRecordTitleExpression() {
        return operationRecordTitleExpression;
    }

    /**
     * @param operationRecordTitleExpression 要设置的operationRecordTitleExpression
     */
    public void setOperationRecordTitleExpression(String operationRecordTitleExpression) {
        this.operationRecordTitleExpression = operationRecordTitleExpression;
    }

    /**
     * @return the joinMode
     */
    public int getJoinMode() {
        return joinMode;
    }

    /**
     * @param joinMode 要设置的joinMode
     */
    public void setJoinMode(int joinMode) {
        this.joinMode = joinMode;
    }

}
