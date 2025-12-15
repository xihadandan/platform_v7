package com.wellsoft.pt.ei.dto.flow.exportNew;


import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.pt.ei.annotate.FieldType;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;

import java.io.Serializable;
import java.util.Date;

public class TaskInstanceExportData implements Serializable {
    /**
     * 自关联
     */
//    @UnCloneable
//    private Set<TaskInstance> children;
    // ---------------------------------------


    // UUID
    @FieldType(desc = "UUID，系统字段", type = ExportFieldTypeEnum.STRING)
    protected String uuid;
    /**
     * 基本属性
     */
    @FieldType(desc = "环节ID", type = ExportFieldTypeEnum.STRING)
    private String id;
    @FieldType(desc = "环节名称", type = ExportFieldTypeEnum.STRING)
    private String name;
    @FieldType(desc = "环节类型1普通环节、2子流程环节", type = ExportFieldTypeEnum.STRING)
    private Integer type;
    @FieldType(desc = "流程实例UUID", type = ExportFieldTypeEnum.STRING)
    private String flowInstUuid;
    @FieldType(desc = "流程定义UUID", type = ExportFieldTypeEnum.STRING)
    private String flowDefUuid;
    /**
     * 环节定义的动态表单定义UUID
     */
    @FieldType(desc = "环节定义的动态表单定义UUID", type = ExportFieldTypeEnum.STRING)
    private String formUuid;
    /**
     * 环节定义的动态表单数据UUID
     */
    @FieldType(desc = "环节定义的动态表单数据UUID", type = ExportFieldTypeEnum.STRING)
    private String dataUuid;
    /**
     * 流水号
     */
    @FieldType(desc = "流水号", type = ExportFieldTypeEnum.STRING)
    private String serialNo;
    /**
     * 任务所有者
     */
    @FieldType(desc = "任务所有者", type = ExportFieldTypeEnum.STRING)
    private String owner;
    /**
     * 前办理人ID
     */
    @FieldType(desc = "前办理人ID", type = ExportFieldTypeEnum.STRING)
    private String assignee;
    /**
     * 前办理人名称
     */
    @FieldType(desc = "前办理人名称", type = ExportFieldTypeEnum.STRING)
    private String assigneeName;
    /**
     * 任务待办人员ID，多个以";"隔开
     */
    @FieldType(desc = "任务待办人员ID，多个以\";\"隔开 ", type = ExportFieldTypeEnum.STRING)
    private String todoUserId;
    /**
     * 任务待办人员名称，多个以";"隔开
     */
    @FieldType(desc = "任务待办人员名称，多个以\";\"隔开", type = ExportFieldTypeEnum.STRING)
    private String todoUserName;
    /**
     * 操作动作
     */
    @FieldType(desc = "操作动作", type = ExportFieldTypeEnum.STRING)
    private String action;
    /**
     * 操作动作类型
     */
    @FieldType(desc = "操作动作类型", type = ExportFieldTypeEnum.STRING)
    private String actionType;
    /**
     * 是否并行任务
     */
    @FieldType(desc = "是否并行任务", type = ExportFieldTypeEnum.BOOLEAN)
    private Boolean isParallel = false;
    /**
     * 发起并行任务的任务UUID
     */
    @FieldType(desc = "发起并行任务的任务UUID", type = ExportFieldTypeEnum.STRING)
    private String parallelTaskInstUuid;
    /**
     * 开始时间
     */
    @FieldType(desc = "开始时间", type = ExportFieldTypeEnum.DATE)
    private Date startTime;
    /**
     * 预警时间
     */
    @FieldType(desc = "预警时间", type = ExportFieldTypeEnum.DATE)
    private Date alarmTime;
    /**
     * 到期时间、逾期时间、承诺时间
     */
    @FieldType(desc = "到期时间、逾期时间、承诺时间", type = ExportFieldTypeEnum.DATE)
    private Date dueTime;
    /**
     * 计时状态(0正常、1预警、2到期、3逾期)
     */
    @FieldType(desc = "计时状态(0正常、1预警、2到期、3逾期)", type = ExportFieldTypeEnum.INTEGER)
    private Integer timingState;
    /**
     * 预警状态(0未预警、1预警中)
     */
    @FieldType(desc = "预警状态(0未预警、1预警中)", type = ExportFieldTypeEnum.INTEGER)
    private Integer alarmState;
    /**
     * 逾期状态(0未逾期、1逾期中)
     */
    @FieldType(desc = "逾期状态(0未逾期、1逾期中)", type = ExportFieldTypeEnum.INTEGER)
    private Integer overDueState;
    /**
     * 结束时间
     */
    @FieldType(desc = "结束时间", type = ExportFieldTypeEnum.DATE)
    private Date endTime;
    /**
     * 持续时间
     */
    @FieldType(desc = "持续时间", type = ExportFieldTypeEnum.LONG)
    private long duration;
    /**
     * 挂起状态(0正常、1挂起、2删除)
     */
    @FieldType(desc = "挂起状态(0正常、1挂起、2删除)", type = ExportFieldTypeEnum.INTEGER)
    private int suspensionState = 0;
    /**
     * 父结点
     */
    @UnCloneable
    @FieldType(desc = "父结点uuid", type = ExportFieldTypeEnum.STRING)
    private String parentTaskInstUuid;
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

    // 附件属性
//    @ApiModelProperty("附件属性")
//    @FieldType(desc = "", type = ExportFieldTypeEnum.STRING)
//    private String attach;


//    --------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    public String getFlowDefUuid() {
        return flowDefUuid;
    }

    public void setFlowDefUuid(String flowDefUuid) {
        this.flowDefUuid = flowDefUuid;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getTodoUserId() {
        return todoUserId;
    }

    public void setTodoUserId(String todoUserId) {
        this.todoUserId = todoUserId;
    }

    public String getTodoUserName() {
        return todoUserName;
    }

    public void setTodoUserName(String todoUserName) {
        this.todoUserName = todoUserName;
    }

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

    public Boolean getIsParallel() {
        return isParallel;
    }

    public void setIsParallel(Boolean parallel) {
        isParallel = parallel;
    }

    public String getParallelTaskInstUuid() {
        return parallelTaskInstUuid;
    }

    public void setParallelTaskInstUuid(String parallelTaskInstUuid) {
        this.parallelTaskInstUuid = parallelTaskInstUuid;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public Integer getTimingState() {
        return timingState;
    }

    public void setTimingState(Integer timingState) {
        this.timingState = timingState;
    }

    public Integer getAlarmState() {
        return alarmState;
    }

    public void setAlarmState(Integer alarmState) {
        this.alarmState = alarmState;
    }

    public Integer getOverDueState() {
        return overDueState;
    }

    public void setOverDueState(Integer overDueState) {
        this.overDueState = overDueState;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getSuspensionState() {
        return suspensionState;
    }

    public void setSuspensionState(int suspensionState) {
        this.suspensionState = suspensionState;
    }

    public String getParentTaskInstUuid() {
        return parentTaskInstUuid;
    }

    public void setParentTaskInstUuid(String parentTaskInstUuid) {
        this.parentTaskInstUuid = parentTaskInstUuid;
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
