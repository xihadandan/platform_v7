/*
 * @(#)2012-11-16 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
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
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-16.1	zhulh		2012-11-16		Create
 * </pre>
 * @date 2012-11-16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectionElement implements Serializable, Comparable<DirectionElement> {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3058351446016766760L;

    private String name;
    private String useAsButton;
    private String buttonName;
    private String buttonClassName;
    private String btnStyle;
    private String buttonOrder;
    private String id;
    private String type;
    private String fromID;
    private String toID;
    private String sortOrder;
    private String remark;
    private String showRemark;

    private String terminalName;
    private String terminalType;
    private String terminalX;
    private String terminalY;
    private String terminalBody;
    private String lineLabel;
    private String line;
    private String isShowName;

    private List<ConditionUnitElement> conditions;
    private List<UnitElement> fileRecipients;
    private List<UserUnitElement> msgRecipients;

    private String isDefault;
    private String isEveryCheck;
    private String isSendFile;
    private String isSendMsg;
    private String listener;

    // 分支模式(1、静态分支;2、动态多分支)
    private String branchMode;
    // 分支实例类型
    private String branchInstanceType;
    // 来源
    private String branchCreateWay;
    // 表单
    private String branchCreateInstanceFormId;
    // 是否主表创建方式
    private String isMainFormBranchCreateWay;
    // 字段
    private String branchTaskUsers;
    // 创建方式
    private String branchCreateInstanceWay;
    // 按从表行分批次生成实例
    private String branchCreateInstanceBatch;
    // 分支计时器
    private String branchTimer;
    // 分支流接口
    private String branchInterface;
    // 分支流接口名称
    private String branchInterfaceName;
    // 共享分支
    private String shareBranch;
    // 该流向不参与聚合
    private String isIndependentBranch;

    private ScriptElement eventScript;

    private List<ArchiveElement> archives;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the useAsButton
     */
    public String getUseAsButton() {
        return useAsButton;
    }

    /**
     * @param useAsButton 要设置的useAsButton
     */
    public void setUseAsButton(String useAsButton) {
        this.useAsButton = useAsButton;
    }

    /**
     * @return the buttonName
     */
    public String getButtonName() {
        return buttonName;
    }

    /**
     * @param buttonName 要设置的buttonName
     */
    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    /**
     * @return the buttonClassName
     */
    public String getButtonClassName() {
        return buttonClassName;
    }

    /**
     * @param buttonClassName 要设置的buttonClassName
     */
    public void setButtonClassName(String buttonClassName) {
        this.buttonClassName = buttonClassName;
    }

    /**
     * @return the btnStyle
     */
    public String getBtnStyle() {
        return btnStyle;
    }

    /**
     * @param btnStyle 要设置的btnStyle
     */
    public void setBtnStyle(String btnStyle) {
        this.btnStyle = btnStyle;
    }

    /**
     * @return the buttonOrder
     */
    public String getButtonOrder() {
        return buttonOrder;
    }

    /**
     * @param buttonOrder 要设置的buttonOrder
     */
    public void setButtonOrder(String buttonOrder) {
        this.buttonOrder = buttonOrder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    /**
     * @return the sortOrder
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }


    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the showRemark
     */
    public String getShowRemark() {
        return showRemark;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsShowRemark() {
        return "1".equals(showRemark);
    }

    /**
     * @param showRemark 要设置的showRemark
     */
    public void setShowRemark(String showRemark) {
        this.showRemark = showRemark;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getTerminalX() {
        return terminalX;
    }

    public void setTerminalX(String terminalX) {
        this.terminalX = terminalX;
    }

    public String getTerminalY() {
        return terminalY;
    }

    public void setTerminalY(String terminalY) {
        this.terminalY = terminalY;
    }

    public String getTerminalBody() {
        return terminalBody;
    }

    public void setTerminalBody(String terminalBody) {
        this.terminalBody = terminalBody;
    }

    public String getLineLabel() {
        return lineLabel;
    }

    public void setLineLabel(String lineLabel) {
        this.lineLabel = lineLabel;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getIsShowName() {
        return isShowName;
    }

    public void setIsShowName(String isShowName) {
        this.isShowName = isShowName;
    }

    public List<ConditionUnitElement> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionUnitElement> conditions) {
        this.conditions = conditions;
    }

    public List<UnitElement> getFileRecipients() {
        return fileRecipients;
    }

    public void setFileRecipients(List<UnitElement> fileRecipients) {
        this.fileRecipients = fileRecipients;
    }

    public List<UserUnitElement> getMsgRecipients() {
        return msgRecipients;
    }

    public void setMsgRecipients(List<UserUnitElement> msgRecipients) {
        this.msgRecipients = msgRecipients;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getIsEveryCheck() {
        return isEveryCheck;
    }

    public void setIsEveryCheck(String isEveryCheck) {
        this.isEveryCheck = isEveryCheck;
    }

    public String getIsSendFile() {
        return isSendFile;
    }

    public void setIsSendFile(String isSendFile) {
        this.isSendFile = isSendFile;
    }

    public String getIsSendMsg() {
        return isSendMsg;
    }

    public void setIsSendMsg(String isSendMsg) {
        this.isSendMsg = isSendMsg;
    }

    /**
     * @return the listener
     */
    public String getListener() {
        return listener;
    }

    /**
     * @param listener 要设置的listener
     */
    public void setListener(String listener) {
        this.listener = listener;
    }

    /**
     * @return the branchMode
     */
    public String getBranchMode() {
        return branchMode;
    }

    /**
     * @param branchMode 要设置的branchMode
     */
    public void setBranchMode(String branchMode) {
        this.branchMode = branchMode;
    }

    /**
     * @return the branchInstanceType
     */
    public String getBranchInstanceType() {
        return branchInstanceType;
    }

    /**
     * @param branchInstanceType 要设置的branchInstanceType
     */
    public void setBranchInstanceType(String branchInstanceType) {
        this.branchInstanceType = branchInstanceType;
    }

    /**
     * @return the branchCreateWay
     */
    public String getBranchCreateWay() {
        return branchCreateWay;
    }

    /**
     * @param branchCreateWay 要设置的branchCreateWay
     */
    public void setBranchCreateWay(String branchCreateWay) {
        this.branchCreateWay = branchCreateWay;
    }

    /**
     * @return the branchCreateInstanceFormId
     */
    public String getBranchCreateInstanceFormId() {
        return branchCreateInstanceFormId;
    }

    /**
     * @param branchCreateInstanceFormId 要设置的branchCreateInstanceFormId
     */
    public void setBranchCreateInstanceFormId(String branchCreateInstanceFormId) {
        this.branchCreateInstanceFormId = branchCreateInstanceFormId;
    }

    /**
     * @return the isMainFormBranchCreateWay
     */
    public String getIsMainFormBranchCreateWay() {
        return isMainFormBranchCreateWay;
    }

    /**
     * @param isMainFormBranchCreateWay 要设置的isMainFormBranchCreateWay
     */
    public void setIsMainFormBranchCreateWay(String isMainFormBranchCreateWay) {
        this.isMainFormBranchCreateWay = isMainFormBranchCreateWay;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isMainFormBranchCreateWay() {
        return "1".equals(isMainFormBranchCreateWay);
    }

    /**
     * @return the branchTaskUsers
     */
    public String getBranchTaskUsers() {
        return branchTaskUsers;
    }

    /**
     * @param branchTaskUsers 要设置的branchTaskUsers
     */
    public void setBranchTaskUsers(String branchTaskUsers) {
        this.branchTaskUsers = branchTaskUsers;
    }

    /**
     * @return the branchCreateInstanceWay
     */
    public String getBranchCreateInstanceWay() {
        return branchCreateInstanceWay;
    }

    /**
     * @param branchCreateInstanceWay 要设置的branchCreateInstanceWay
     */
    public void setBranchCreateInstanceWay(String branchCreateInstanceWay) {
        this.branchCreateInstanceWay = branchCreateInstanceWay;
    }

    /**
     * @return the branchCreateInstanceBatch
     */
    public String getBranchCreateInstanceBatch() {
        return branchCreateInstanceBatch;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsBranchCreateInstanceBatch() {
        return "1".equals(branchCreateInstanceBatch);
    }

    /**
     * @param branchCreateInstanceBatch 要设置的branchCreateInstanceBatch
     */
    public void setBranchCreateInstanceBatch(String branchCreateInstanceBatch) {
        this.branchCreateInstanceBatch = branchCreateInstanceBatch;
    }

    /**
     * @return the branchTimer
     */
    public String getBranchTimer() {
        return branchTimer;
    }

    /**
     * @param branchTimer 要设置的branchTimer
     */
    public void setBranchTimer(String branchTimer) {
        this.branchTimer = branchTimer;
    }

    /**
     * @return the branchInterface
     */
    public String getBranchInterface() {
        return branchInterface;
    }

    /**
     * @param branchInterface 要设置的branchInterface
     */
    public void setBranchInterface(String branchInterface) {
        this.branchInterface = branchInterface;
    }

    /**
     * @return the branchInterfaceName
     */
    public String getBranchInterfaceName() {
        return branchInterfaceName;
    }

    /**
     * @param branchInterfaceName 要设置的branchInterfaceName
     */
    public void setBranchInterfaceName(String branchInterfaceName) {
        this.branchInterfaceName = branchInterfaceName;
    }

    /**
     * @return the shareBranch
     */
    public String getShareBranch() {
        return shareBranch;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsShareBranch() {
        return "1".equals(shareBranch);
    }

    /**
     * @param shareBranch 要设置的shareBranch
     */
    public void setShareBranch(String shareBranch) {
        this.shareBranch = shareBranch;
    }

    /**
     * @return the isIndependentBranch
     */
    public String getIsIndependentBranch() {
        return isIndependentBranch;
    }

    /**
     * @param isIndependentBranch 要设置的isIndependentBranch
     */
    public void setIsIndependentBranch(String isIndependentBranch) {
        this.isIndependentBranch = isIndependentBranch;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isIndependentBranch() {
        return "1".equals(isIndependentBranch);
    }

    /**
     * @return the eventScript
     */
    public ScriptElement getEventScript() {
        return eventScript;
    }

    /**
     * @param eventScript 要设置的eventScript
     */
    public void setEventScript(ScriptElement eventScript) {
        this.eventScript = eventScript;
    }

    /**
     * @return the archives
     */
    public List<ArchiveElement> getArchives() {
        return archives;
    }

    /**
     * @param archives 要设置的archives
     */
    public void setArchives(List<ArchiveElement> archives) {
        this.archives = archives;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(DirectionElement o) {
        String anotherSortOrder = o.getSortOrder();
        if (sortOrder == null && anotherSortOrder == null) {
            return 0;
        } else if (sortOrder != null && anotherSortOrder == null) {
            return 1;
        } else if (sortOrder == null && anotherSortOrder != null) {
            return -1;
        }
        return sortOrder.compareTo(anotherSortOrder);
    }

}
