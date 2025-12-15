/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.bpm.engine.element.UnitElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

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
 * 2012-11-21.1	zhulh		2012-11-21		Create
 * </pre>
 * @date 2012-11-21
 */
public class Direction extends IdEntity implements Comparable<Direction> {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4101513107220798606L;

    private String name;
    private String useAsButton;
    private String buttonName;
    private String buttonClassName;
    private String buttonOrder;
    private String id;
    private String type;
    private String fromID;
    private String toID;
    private String sortOrder;
    private String remark;
    private boolean showRemark;

    private String terminalName;
    private String terminalType;
    private String terminalX;
    private String terminalY;
    private String terminalBody;
    private String lineLabel;
    private String line;
    private String isShowName;

    private List<String> conditions;
    private List<String> conditionDatas;
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
    private boolean isMainFormBranchCreateWay;
    // 字段
    private String branchTaskUsers;
    // 创建方式
    private String branchCreateInstanceWay;
    // 按从表行分批次生成实例
    private boolean branchCreateInstanceBatch;
    // 分支流接口
    private String branchInterface;
    // 分支流接口名称
    private String branchInterfaceName;
    // 共享分支
    private boolean shareBranch;
    // 该流向不参与聚合
    private boolean isIndependentBranch;

    private Script eventScript;

    private List<Archive> archives;

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
     * @return the useAsButton
     */
    public String getUseAsButton() {
        return useAsButton;
    }

    /**
     * @return the useAsButton
     */
    @JsonIgnore
    public boolean isUseAsButton() {
        return "1".equals(useAsButton);
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

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return
     */
    public String getIdMd5Hex() {
        if (StringUtils.isBlank(id)) {
            return id;
        }
        return DigestUtils.md5Hex(id);
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the fromID
     */
    public String getFromID() {
        return fromID;
    }

    /**
     * @param fromID 要设置的fromID
     */
    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    /**
     * @return the toID
     */
    public String getToID() {
        return toID;
    }

    /**
     * @param toID 要设置的toID
     */
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
    public boolean getShowRemark() {
        return showRemark;
    }

    /**
     * @param showRemark 要设置的showRemark
     */
    public void setShowRemark(boolean showRemark) {
        this.showRemark = showRemark;
    }

    /**
     * @return the terminalName
     */
    public String getTerminalName() {
        return terminalName;
    }

    /**
     * @param terminalName 要设置的terminalName
     */
    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    /**
     * @return the terminalType
     */
    public String getTerminalType() {
        return terminalType;
    }

    /**
     * @param terminalType 要设置的terminalType
     */
    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    /**
     * @return the terminalX
     */
    public String getTerminalX() {
        return terminalX;
    }

    /**
     * @param terminalX 要设置的terminalX
     */
    public void setTerminalX(String terminalX) {
        this.terminalX = terminalX;
    }

    /**
     * @return the terminalY
     */
    public String getTerminalY() {
        return terminalY;
    }

    /**
     * @param terminalY 要设置的terminalY
     */
    public void setTerminalY(String terminalY) {
        this.terminalY = terminalY;
    }

    /**
     * @return the terminalBody
     */
    public String getTerminalBody() {
        return terminalBody;
    }

    /**
     * @param terminalBody 要设置的terminalBody
     */
    public void setTerminalBody(String terminalBody) {
        this.terminalBody = terminalBody;
    }

    /**
     * @return the lineLabel
     */
    public String getLineLabel() {
        return lineLabel;
    }

    /**
     * @param lineLabel 要设置的lineLabel
     */
    public void setLineLabel(String lineLabel) {
        this.lineLabel = lineLabel;
    }

    /**
     * @return the line
     */
    public String getLine() {
        return line;
    }

    /**
     * @param line 要设置的line
     */
    public void setLine(String line) {
        this.line = line;
    }

    /**
     * @return the isShowName
     */
    public String getIsShowName() {
        return isShowName;
    }

    /**
     * @param isShowName 要设置的isShowName
     */
    public void setIsShowName(String isShowName) {
        this.isShowName = isShowName;
    }

    /**
     * @return the conditions
     */
    public List<String> getConditions() {
        return conditions;
    }

    /**
     * @param conditions 要设置的conditions
     */
    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    /**
     * @return the conditionDatas
     */
    public List<String> getConditionDatas() {
        return conditionDatas;
    }

    /**
     * @param conditionDatas 要设置的conditionDatas
     */
    public void setConditionDatas(List<String> conditionDatas) {
        this.conditionDatas = conditionDatas;
    }

    /**
     * @return the fileRecipients
     */
    public List<UnitElement> getFileRecipients() {
        return fileRecipients;
    }

    /**
     * @param fileRecipients 要设置的fileRecipients
     */
    public void setFileRecipients(List<UnitElement> fileRecipients) {
        this.fileRecipients = fileRecipients;
    }

    /**
     * @return the msgRecipients
     */
    public List<UserUnitElement> getMsgRecipients() {
        return msgRecipients;
    }

    /**
     * @param msgRecipients 要设置的msgRecipients
     */
    public void setMsgRecipients(List<UserUnitElement> msgRecipients) {
        this.msgRecipients = msgRecipients;
    }

    /**
     * @return the isDefault
     */
    public String getIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault 要设置的isDefault
     */
    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return the isDefault as Boolean
     */
    public Boolean isDefault() {
        return "1".equals(isDefault);
    }

    /**
     * @return the isEveryCheck
     */
    public String getIsEveryCheck() {
        return isEveryCheck;
    }

    /**
     * @param isEveryCheck 要设置的isEveryCheck
     */
    public void setIsEveryCheck(String isEveryCheck) {
        this.isEveryCheck = isEveryCheck;
    }

    /**
     * @return the isSendFile
     */
    public String getIsSendFile() {
        return isSendFile;
    }

    /**
     * @param isSendFile 要设置的isSendFile
     */
    public void setIsSendFile(String isSendFile) {
        this.isSendFile = isSendFile;
    }

    public boolean isSendFile() {
        return "1".equals(isSendFile);
    }

    /**
     * @return the isSendMsg
     */
    public String getIsSendMsg() {
        return isSendMsg;
    }

    /**
     * @param isSendMsg 要设置的isSendMsg
     */
    public void setIsSendMsg(String isSendMsg) {
        this.isSendMsg = isSendMsg;
    }

    public boolean isSendMsg() {
        return "1".equals(isSendMsg);
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
    public boolean isMainFormBranchCreateWay() {
        return isMainFormBranchCreateWay;
    }

    /**
     * @param isMainFormBranchCreateWay 要设置的isMainFormBranchCreateWay
     */
    public void setMainFormBranchCreateWay(boolean isMainFormBranchCreateWay) {
        this.isMainFormBranchCreateWay = isMainFormBranchCreateWay;
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
    public boolean isBranchCreateInstanceBatch() {
        return branchCreateInstanceBatch;
    }

    /**
     * @param branchCreateInstanceBatch 要设置的branchCreateInstanceBatch
     */
    public void setBranchCreateInstanceBatch(boolean branchCreateInstanceBatch) {
        this.branchCreateInstanceBatch = branchCreateInstanceBatch;
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
    public boolean isShareBranch() {
        return shareBranch;
    }

    /**
     * @param shareBranch 要设置的shareBranch
     */
    public void setShareBranch(boolean shareBranch) {
        this.shareBranch = shareBranch;
    }

    /**
     * @return the isIndependentBranch
     */
    public boolean isIndependentBranch() {
        return isIndependentBranch;
    }

    /**
     * @param isIndependentBranch 要设置的isIndependentBranch
     */
    public void setIndependentBranch(boolean isIndependentBranch) {
        this.isIndependentBranch = isIndependentBranch;
    }

    /**
     * @return the eventScript
     */
    public Script getEventScript() {
        return eventScript;
    }

    /**
     * @param eventScript 要设置的eventScript
     */
    public void setEventScript(Script eventScript) {
        this.eventScript = eventScript;
    }

    /**
     * @return the archives
     */
    public List<Archive> getArchives() {
        return archives;
    }

    /**
     * @param archives 要设置的archives
     */
    public void setArchives(List<Archive> archives) {
        this.archives = archives;
    }

    /**
     * 获取未解析的完成条件表达式
     *
     * @return
     */
    public String getRawConditionExpression() {
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isNotEmpty(conditions)) {
            for (int index = 0; index < conditions.size(); index++) {
                sb.append(conditions.get(index));
            }
        }
        return sb.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Direction o) {
        if (this.buttonOrder == null) {
            this.buttonOrder = StringUtils.EMPTY;
        }
        String anotherOrder = o.getButtonOrder();
        if (anotherOrder == null) {
            anotherOrder = StringUtils.EMPTY;
        }
        return this.buttonOrder.compareTo(anotherOrder);
    }

}
