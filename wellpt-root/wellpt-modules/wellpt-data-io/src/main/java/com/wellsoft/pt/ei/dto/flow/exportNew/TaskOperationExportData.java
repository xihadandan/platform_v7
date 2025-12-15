package com.wellsoft.pt.ei.dto.flow.exportNew;

import com.wellsoft.pt.ei.annotate.FieldType;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;

import java.io.Serializable;
import java.util.Date;

public class TaskOperationExportData implements Serializable {

    // UUID
    @FieldType(desc = "UUID，系统字段", type = ExportFieldTypeEnum.STRING)
    protected String uuid;
    // 操作动作
    @FieldType(desc = "操作动作", type = ExportFieldTypeEnum.STRING)
    private String action;
    // 操作类型
    @FieldType(desc = "操作类型", type = ExportFieldTypeEnum.STRING)
    private String actionType;
    // 操作代码
    @FieldType(desc = "操作代码", type = ExportFieldTypeEnum.INTEGER)
    private Integer actionCode;
    // 办理意见立场
    @FieldType(desc = "办理意见立场", type = ExportFieldTypeEnum.STRING)
    private String opinionValue;
    // 办理意见立场文本
    @FieldType(desc = "办理意见立场文本", type = ExportFieldTypeEnum.STRING)
    private String opinionLabel;
    // 办理意见内容
    @FieldType(desc = "办理意见内容", type = ExportFieldTypeEnum.STRING)
    private String opinionText;
    // 操作人ID
    @FieldType(desc = "操作人ID", type = ExportFieldTypeEnum.STRING)
    private String assignee;
    // 操作人名称
    @FieldType(desc = "操作人名称", type = ExportFieldTypeEnum.STRING)
    private String assigneeName;
    // 主送人ID
    @FieldType(desc = "主送人ID", type = ExportFieldTypeEnum.STRING)
    private String userId;
    // 抄送人ID
    @FieldType(desc = "抄送人ID", type = ExportFieldTypeEnum.STRING)
    private String copyUserId;
    // 所在任务实例
    @FieldType(desc = "所在任务实例id", type = ExportFieldTypeEnum.STRING)
    private String taskId;
    // 所在任务实例
    @FieldType(desc = "所在任务实例名称", type = ExportFieldTypeEnum.STRING)
    private String taskName;
    // 所在任务实例
    @FieldType(desc = "所在任务实例uuid", type = ExportFieldTypeEnum.STRING)
    private String taskInstUuid;
    // 所在流程实例
    @FieldType(desc = "所在流程实例uuid", type = ExportFieldTypeEnum.STRING)
    private String flowInstUuid;
    // 所在待办实体UUID
    @FieldType(desc = "所在待办实体UUID", type = ExportFieldTypeEnum.STRING)
    private String taskIdentityUuid;
    // 附加信息
    @FieldType(desc = "附加信息", type = ExportFieldTypeEnum.STRING)
    private String extraInfo;

    // -------------------------------------------------------
    // 是否移动端应用的操作
    @FieldType(desc = "是否移动端应用的操作", type = ExportFieldTypeEnum.BOOLEAN)
    private Boolean isMobileApp;
    // 版本号
    @FieldType(desc = "数据版本号", type = ExportFieldTypeEnum.INTEGER)
    private Integer recVer;

    // 创建人
    @FieldType(desc = "创建人", type = ExportFieldTypeEnum.STRING)
    private String creator;

    // 创建时间
    @FieldType(desc = "创建时间", type = ExportFieldTypeEnum.DATE)
    private Date createTime;

    // 修改人
    @FieldType(desc = "修改人", type = ExportFieldTypeEnum.STRING)
    private String modifier;

    // 修改时间
    @FieldType(desc = "修改时间", type = ExportFieldTypeEnum.DATE)
    private Date modifyTime;


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Integer getActionCode() {
        return actionCode;
    }

    public void setActionCode(Integer actionCode) {
        this.actionCode = actionCode;
    }

    public String getOpinionValue() {
        return opinionValue;
    }

    public void setOpinionValue(String opinionValue) {
        this.opinionValue = opinionValue;
    }

    public String getOpinionLabel() {
        return opinionLabel;
    }

    public void setOpinionLabel(String opinionLabel) {
        this.opinionLabel = opinionLabel;
    }

    public String getOpinionText() {
        return opinionText;
    }

    public void setOpinionText(String opinionText) {
        this.opinionText = opinionText;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCopyUserId() {
        return copyUserId;
    }

    public void setCopyUserId(String copyUserId) {
        this.copyUserId = copyUserId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    public String getTaskIdentityUuid() {
        return taskIdentityUuid;
    }

    public void setTaskIdentityUuid(String taskIdentityUuid) {
        this.taskIdentityUuid = taskIdentityUuid;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Boolean getIsMobileApp() {
        return isMobileApp;
    }

    public void setIsMobileApp(Boolean mobileApp) {
        isMobileApp = mobileApp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getRecVer() {
        return recVer;
    }

    public void setRecVer(Integer recVer) {
        this.recVer = recVer;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
