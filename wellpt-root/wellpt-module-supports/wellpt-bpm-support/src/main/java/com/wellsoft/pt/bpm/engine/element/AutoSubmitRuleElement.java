/*
 * @(#)12/20/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/20/24.1	    zhulh		12/20/24		    Create
 * </pre>
 * @date 12/20/24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutoSubmitRuleElement extends BaseObject {
    private static final long serialVersionUID = 2951638490435313453L;

    // 审批去重模式，before前置审批、after后置审批
    private String mode;

    // 规则生效环节，all全流程、include指定环节、exclude除指定环节外的其他环节
    private String effectiveTask;

    // 指定的环节ID列表
    private List<String> taskIds;

    // 规则生效人员，all所有人、include指定人员、exclude除指定人员外的其他人员
    private String effectiveUser;

    // 指定的人员
    private List<UserUnitElement> users;

    // 重复审批人员判定，task审批节点办理人、taskIncludeStart含发起环节办理人、collaboration协作节点办理人(不含决策人)、collaborationIncludeStart含发起环节办理人、branch包含分支、条件分支之后环节的办理人
    private List<String> matchTypes;

    // 处理方式，submit自动审批，skip自动跳过
    private String handleMode;

    // 自动提交意见，latest使用最后一次人工填写意见、default使用缺省意见
    private String submitOpinionMode;

    // 缺省意见内容
    private String defaultSubmitOpinionText;

    // 是否留痕，true留痕、false不留痕
    private boolean keepRecord;

    // 退出条件，dataChanged后续环节数据版本发生变更、canEditForm后续环节可编辑表单时/前序环节可编辑表单时、
    // singleUserOnCanEditForm仅一人办理或办理人全部去重时判断、
    // editAndRequiredField后续环节可编辑表单且存在必填字段时/前序环节可编辑表单且存在必填字段时、
    // singleUserOnEditAndRequiredField仅一人办理或办理人全部去重时判断、
    // chooseDirection后续环节需要选择流向时/前序环节需要选择流向时、
    // singleUserOnChooseDirection仅一人办理或办理人全部去重时判断、
    // chooseUser后续环节需要选择下一环节办理人/抄送人时/前序环节需要选择下一环节办理人/抄送人时、
    // singleUserOnChooseUser仅一人办理或办理人全部去重时判断
    private List<String> exitConditions;

    // 退出范围，single单次退出前置审批、all全流程退出前置审批
    private String exitScope;

    // 补审补办规则，1存在未审批过的人员时补审补办、2存在未审批人员，且有环节被跳过时补审补办
    private String supplementRule;

    // 补审补办方式，task按最后跳过环节补审补办、user按人员补审补办
    private String supplementMode;

    // 补审补办环节名称
    private String supplementTaskName;

    // 补审补办操作权限，submit只有提交权限、default同补审环节权限
    private String supplementOperateRight;

    // 补审补办表单权限, 只有阅读权限readonly、default同补审环节权限
    private String supplementViewFormMode;

    /**
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode 要设置的mode
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * @return the effectiveTask
     */
    public String getEffectiveTask() {
        return effectiveTask;
    }

    /**
     * @param effectiveTask 要设置的effectiveTask
     */
    public void setEffectiveTask(String effectiveTask) {
        this.effectiveTask = effectiveTask;
    }

    /**
     * @return the taskIds
     */
    public List<String> getTaskIds() {
        return taskIds;
    }

    /**
     * @param taskIds 要设置的taskIds
     */
    public void setTaskIds(List<String> taskIds) {
        this.taskIds = taskIds;
    }

    /**
     * @return the effectiveUser
     */
    public String getEffectiveUser() {
        return effectiveUser;
    }

    /**
     * @param effectiveUser 要设置的effectiveUser
     */
    public void setEffectiveUser(String effectiveUser) {
        this.effectiveUser = effectiveUser;
    }

    /**
     * @return the users
     */
    public List<UserUnitElement> getUsers() {
        return users;
    }

    /**
     * @param users 要设置的users
     */
    public void setUsers(List<UserUnitElement> users) {
        this.users = users;
    }

    /**
     * @return the matchTypes
     */
    public List<String> getMatchTypes() {
        return matchTypes;
    }

    /**
     * @param matchTypes 要设置的matchTypes
     */
    public void setMatchTypes(List<String> matchTypes) {
        this.matchTypes = matchTypes;
    }

    /**
     * @return the handleMode
     */
    public String getHandleMode() {
        return handleMode;
    }

    /**
     * @param handleMode 要设置的handleMode
     */
    public void setHandleMode(String handleMode) {
        this.handleMode = handleMode;
    }

    /**
     * @return the submitOpinionMode
     */
    public String getSubmitOpinionMode() {
        return submitOpinionMode;
    }

    /**
     * @param submitOpinionMode 要设置的submitOpinionMode
     */
    public void setSubmitOpinionMode(String submitOpinionMode) {
        this.submitOpinionMode = submitOpinionMode;
    }

    /**
     * @return the defaultSubmitOpinionText
     */
    public String getDefaultSubmitOpinionText() {
        return defaultSubmitOpinionText;
    }

    /**
     * @param defaultSubmitOpinionText 要设置的defaultSubmitOpinionText
     */
    public void setDefaultSubmitOpinionText(String defaultSubmitOpinionText) {
        this.defaultSubmitOpinionText = defaultSubmitOpinionText;
    }

    /**
     * @return the keepRecord
     */
    public boolean isKeepRecord() {
        return keepRecord;
    }

    /**
     * @param keepRecord 要设置的keepRecord
     */
    public void setKeepRecord(boolean keepRecord) {
        this.keepRecord = keepRecord;
    }

    /**
     * @return the exitConditions
     */
    public List<String> getExitConditions() {
        return exitConditions;
    }

    /**
     * @param exitConditions 要设置的exitConditions
     */
    public void setExitConditions(List<String> exitConditions) {
        this.exitConditions = exitConditions;
    }

    /**
     * @return the exitScope
     */
    public String getExitScope() {
        return exitScope;
    }

    /**
     * @param exitScope 要设置的exitScope
     */
    public void setExitScope(String exitScope) {
        this.exitScope = exitScope;
    }

    /**
     * @return the supplementRule
     */
    public String getSupplementRule() {
        return supplementRule;
    }

    /**
     * @param supplementRule 要设置的supplementRule
     */
    public void setSupplementRule(String supplementRule) {
        this.supplementRule = supplementRule;
    }

    /**
     * @return the supplementMode
     */
    public String getSupplementMode() {
        return supplementMode;
    }

    /**
     * @param supplementMode 要设置的supplementMode
     */
    public void setSupplementMode(String supplementMode) {
        this.supplementMode = supplementMode;
    }

    /**
     * @return the supplementTaskName
     */
    public String getSupplementTaskName() {
        return supplementTaskName;
    }

    /**
     * @param supplementTaskName 要设置的supplementTaskName
     */
    public void setSupplementTaskName(String supplementTaskName) {
        this.supplementTaskName = supplementTaskName;
    }

    /**
     * @return the supplementOperateRight
     */
    public String getSupplementOperateRight() {
        return supplementOperateRight;
    }

    /**
     * @param supplementOperateRight 要设置的supplementOperateRight
     */
    public void setSupplementOperateRight(String supplementOperateRight) {
        this.supplementOperateRight = supplementOperateRight;
    }

    /**
     * @return the supplementViewFormMode
     */
    public String getSupplementViewFormMode() {
        return supplementViewFormMode;
    }

    /**
     * @param supplementViewFormMode 要设置的supplementViewFormMode
     */
    public void setSupplementViewFormMode(String supplementViewFormMode) {
        this.supplementViewFormMode = supplementViewFormMode;
    }
}
