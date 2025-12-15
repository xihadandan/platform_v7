/*
 * @(#)7/1/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 按钮权限配置
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/1/24.1	zhulh		2012-10-23		Create
 * </pre>
 * @date 7/1/24
 */
public class RightConfigElement extends BaseObject {

    // 提交必填意见
    private boolean requiredSubmitOpinion;

    // 提交自动套打
    private boolean printAfterSubmit;

    // 退回必填意见
    private boolean requiredRollbackOpinion;

    // 退回后提交方式default、alternative、direct
    private String submitModeOfAfterRollback;
    // 直接提交至本环节时抄送跳过的环节
    private boolean copyToSkipTask;

    // 转办必填意见
    private boolean requiredTransferOpinion;
    // 1、指定转办范围，2、可转办全组织
    private String isSetTransferUser;
    // 转办人员
    private List<UserUnitElement> transferUsers = Lists.newArrayListWithCapacity(0);
    // 转办表单权限default, readonly，custom
    private String transferViewFormMode;
    // 转办操作权限default、none
    private String transferOperateRight;

    // 会签必填意见
    private boolean requiredCounterSignOpinion;
    // 1、指定可会签范围，2、可会签全组织
    private String isSetCounterSignUser;
    // 会签人员
    private List<UserUnitElement> counterSignUsers = Lists.newArrayListWithCapacity(0);
    // 会签表单权限default, readonly，custom
    private String counterSignViewFormMode;
    // 会签操作权限default、none
    private String counterSignOperateRight;

    // 加签必填意见
    private boolean requiredAddSignOpinion;
    // 1、指定可加签范围，2、可加签全组织
    private String isSetAddSignUser;
    // 加签人员
    private List<UserUnitElement> addSignUsers = Lists.newArrayListWithCapacity(0);
    // 加签表单权限default, readonly，custom
    private String addSignViewFormMode;
    // 加签操作权限default、none
    private String addSignOperateRight;

    // 撤回必填意见
    private boolean requiredCancelOpinion;

    // 催办必填意见
    private boolean requiredRemindOpinion;

    // 移交必填意见
    private boolean requiredHandOverOpinion;

    // 跳转必填意见
    private boolean requiredGotoTaskOpinion;

    // 1、指定可抄送范围，2、可抄送全组织
    private String isSetCopyUser;
    // 抄送人员
    private List<UserUnitElement> copyUsers = new ArrayList<>(0);

    /**
     * @return the requiredSubmitOpinion
     */
    public boolean isRequiredSubmitOpinion() {
        return requiredSubmitOpinion;
    }

    /**
     * @param requiredSubmitOpinion 要设置的requiredSubmitOpinion
     */
    public void setRequiredSubmitOpinion(boolean requiredSubmitOpinion) {
        this.requiredSubmitOpinion = requiredSubmitOpinion;
    }

    /**
     * @return the printAfterSubmit
     */
    public boolean isPrintAfterSubmit() {
        return printAfterSubmit;
    }

    /**
     * @param printAfterSubmit 要设置的printAfterSubmit
     */
    public void setPrintAfterSubmit(boolean printAfterSubmit) {
        this.printAfterSubmit = printAfterSubmit;
    }

    /**
     * @return the requiredRollbackOpinion
     */
    public boolean isRequiredRollbackOpinion() {
        return requiredRollbackOpinion;
    }

    /**
     * @param requiredRollbackOpinion 要设置的requiredRollbackOpinion
     */
    public void setRequiredRollbackOpinion(boolean requiredRollbackOpinion) {
        this.requiredRollbackOpinion = requiredRollbackOpinion;
    }

    /**
     * @return the submitModeOfAfterRollback
     */
    public String getSubmitModeOfAfterRollback() {
        return submitModeOfAfterRollback;
    }

    /**
     * @param submitModeOfAfterRollback 要设置的submitModeOfAfterRollback
     */
    public void setSubmitModeOfAfterRollback(String submitModeOfAfterRollback) {
        this.submitModeOfAfterRollback = submitModeOfAfterRollback;
    }

    /**
     * @return the copyToSkipTask
     */
    public boolean isCopyToSkipTask() {
        return copyToSkipTask;
    }

    /**
     * @param copyToSkipTask 要设置的copyToSkipTask
     */
    public void setCopyToSkipTask(boolean copyToSkipTask) {
        this.copyToSkipTask = copyToSkipTask;
    }

    /**
     * @return the requiredTransferOpinion
     */
    public boolean isRequiredTransferOpinion() {
        return requiredTransferOpinion;
    }

    /**
     * @param requiredTransferOpinion 要设置的requiredTransferOpinion
     */
    public void setRequiredTransferOpinion(boolean requiredTransferOpinion) {
        this.requiredTransferOpinion = requiredTransferOpinion;
    }

    /**
     * @return the isSetTransferUser
     */
    public String getIsSetTransferUser() {
        return isSetTransferUser;
    }

    @JsonIgnore
    public boolean isSetTransferUser() {
        return StringUtils.equals("1", isSetTransferUser);
    }

    /**
     * @param isSetTransferUser 要设置的isSetTransferUser
     */
    public void setIsSetTransferUser(String isSetTransferUser) {
        this.isSetTransferUser = isSetTransferUser;
    }

    /**
     * @return the transferUsers
     */
    public List<UserUnitElement> getTransferUsers() {
        return transferUsers;
    }

    /**
     * @param transferUsers 要设置的transferUsers
     */
    public void setTransferUsers(List<UserUnitElement> transferUsers) {
        this.transferUsers = transferUsers;
    }

    /**
     * @return the transferViewFormMode
     */
    public String getTransferViewFormMode() {
        return transferViewFormMode;
    }

    /**
     * @param transferViewFormMode 要设置的transferViewFormMode
     */
    public void setTransferViewFormMode(String transferViewFormMode) {
        this.transferViewFormMode = transferViewFormMode;
    }

    /**
     * @return the transferOperateRight
     */
    public String getTransferOperateRight() {
        return transferOperateRight;
    }

    /**
     * @param transferOperateRight 要设置的transferOperateRight
     */
    public void setTransferOperateRight(String transferOperateRight) {
        this.transferOperateRight = transferOperateRight;
    }

    /**
     * @return the requiredCounterSignOpinion
     */
    public boolean isRequiredCounterSignOpinion() {
        return requiredCounterSignOpinion;
    }

    /**
     * @param requiredCounterSignOpinion 要设置的requiredCounterSignOpinion
     */
    public void setRequiredCounterSignOpinion(boolean requiredCounterSignOpinion) {
        this.requiredCounterSignOpinion = requiredCounterSignOpinion;
    }

    /**
     * @return the isSetCounterSignUser
     */
    public String getIsSetCounterSignUser() {
        return isSetCounterSignUser;
    }

    @JsonIgnore
    public boolean isSetCounterSignUser() {
        return StringUtils.equals("1", isSetCounterSignUser);
    }

    /**
     * @param isSetCounterSignUser 要设置的isSetCounterSignUser
     */
    public void setIsSetCounterSignUser(String isSetCounterSignUser) {
        this.isSetCounterSignUser = isSetCounterSignUser;
    }

    /**
     * @return the counterSignUsers
     */
    public List<UserUnitElement> getCounterSignUsers() {
        return counterSignUsers;
    }

    /**
     * @param counterSignUsers 要设置的counterSignUsers
     */
    public void setCounterSignUsers(List<UserUnitElement> counterSignUsers) {
        this.counterSignUsers = counterSignUsers;
    }

    /**
     * @return the counterSignViewFormMode
     */
    public String getCounterSignViewFormMode() {
        return counterSignViewFormMode;
    }

    /**
     * @param counterSignViewFormMode 要设置的counterSignViewFormMode
     */
    public void setCounterSignViewFormMode(String counterSignViewFormMode) {
        this.counterSignViewFormMode = counterSignViewFormMode;
    }

    /**
     * @return the counterSignOperateRight
     */
    public String getCounterSignOperateRight() {
        return counterSignOperateRight;
    }

    /**
     * @param counterSignOperateRight 要设置的counterSignOperateRight
     */
    public void setCounterSignOperateRight(String counterSignOperateRight) {
        this.counterSignOperateRight = counterSignOperateRight;
    }

    /**
     * @return the requiredAddSignOpinion
     */
    public boolean isRequiredAddSignOpinion() {
        return requiredAddSignOpinion;
    }

    /**
     * @param requiredAddSignOpinion 要设置的requiredAddSignOpinion
     */
    public void setRequiredAddSignOpinion(boolean requiredAddSignOpinion) {
        this.requiredAddSignOpinion = requiredAddSignOpinion;
    }

    /**
     * @return the isSetAddSignUser
     */
    public String getIsSetAddSignUser() {
        return isSetAddSignUser;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isSetAddSignUser() {
        return StringUtils.equals("1", isSetAddSignUser);
    }

    /**
     * @param isSetAddSignUser 要设置的isSetAddSignUser
     */
    public void setIsSetAddSignUser(String isSetAddSignUser) {
        this.isSetAddSignUser = isSetAddSignUser;
    }

    /**
     * @return the addSignUsers
     */
    public List<UserUnitElement> getAddSignUsers() {
        return addSignUsers;
    }

    /**
     * @param addSignUsers 要设置的addSignUsers
     */
    public void setAddSignUsers(List<UserUnitElement> addSignUsers) {
        this.addSignUsers = addSignUsers;
    }

    /**
     * @return the addSignViewFormMode
     */
    public String getAddSignViewFormMode() {
        return addSignViewFormMode;
    }

    /**
     * @param addSignViewFormMode 要设置的addSignViewFormMode
     */
    public void setAddSignViewFormMode(String addSignViewFormMode) {
        this.addSignViewFormMode = addSignViewFormMode;
    }

    /**
     * @return the addSignOperateRight
     */
    public String getAddSignOperateRight() {
        return addSignOperateRight;
    }

    /**
     * @param addSignOperateRight 要设置的addSignOperateRight
     */
    public void setAddSignOperateRight(String addSignOperateRight) {
        this.addSignOperateRight = addSignOperateRight;
    }

    /**
     * @return the requiredCancelOpinion
     */
    public boolean isRequiredCancelOpinion() {
        return requiredCancelOpinion;
    }

    /**
     * @param requiredCancelOpinion 要设置的requiredCancelOpinion
     */
    public void setRequiredCancelOpinion(boolean requiredCancelOpinion) {
        this.requiredCancelOpinion = requiredCancelOpinion;
    }

    /**
     * @return the requiredRemindOpinion
     */
    public boolean isRequiredRemindOpinion() {
        return requiredRemindOpinion;
    }

    /**
     * @param requiredRemindOpinion 要设置的requiredRemindOpinion
     */
    public void setRequiredRemindOpinion(boolean requiredRemindOpinion) {
        this.requiredRemindOpinion = requiredRemindOpinion;
    }

    /**
     * @return the requiredHandOverOpinion
     */
    public boolean isRequiredHandOverOpinion() {
        return requiredHandOverOpinion;
    }

    /**
     * @param requiredHandOverOpinion 要设置的requiredHandOverOpinion
     */
    public void setRequiredHandOverOpinion(boolean requiredHandOverOpinion) {
        this.requiredHandOverOpinion = requiredHandOverOpinion;
    }

    /**
     * @return the requiredGotoTaskOpinion
     */
    public boolean isRequiredGotoTaskOpinion() {
        return requiredGotoTaskOpinion;
    }

    /**
     * @param requiredGotoTaskOpinion 要设置的requiredGotoTaskOpinion
     */
    public void setRequiredGotoTaskOpinion(boolean requiredGotoTaskOpinion) {
        this.requiredGotoTaskOpinion = requiredGotoTaskOpinion;
    }

    /**
     * @return the isSetCopyUser
     */
    public String getIsSetCopyUser() {
        return isSetCopyUser;
    }

    @JsonIgnore
    public boolean isSetCopyUser() {
        return StringUtils.equals("1", isSetCopyUser);
    }

    /**
     * @param isSetCopyUser 要设置的isSetCopyUser
     */
    public void setIsSetCopyUser(String isSetCopyUser) {
        this.isSetCopyUser = isSetCopyUser;
    }

    /**
     * @return the copyUsers
     */
    public List<UserUnitElement> getCopyUsers() {
        return copyUsers;
    }

    /**
     * @param copyUsers 要设置的copyUsers
     */
    public void setCopyUsers(List<UserUnitElement> copyUsers) {
        this.copyUsers = copyUsers;
    }

}
