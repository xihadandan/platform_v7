package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Description: 流程任务实例类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-4.1	zhulh		2012-12-4		Create
 * </pre>
 * @date 2012-12-4
 */
@Entity
@Table(name = "WF_TASK_INSTANCE")
@DynamicUpdate
@DynamicInsert
public class TaskInstance extends IdEntity {
    private static final long serialVersionUID = -6371095800880351054L;

    /**
     * 基本属性
     */
    private String name;
    private String id;
    private Integer type;
    @UnCloneable
    private FlowInstance flowInstance;
    @UnCloneable
    private FlowDefinition flowDefinition;
    /**
     * 环节定义的动态表单定义UUID
     */
    private String formUuid;
    /**
     * 环节定义的动态表单数据UUID
     */
    private String dataUuid;
    /**
     * 流水号
     */
    private String serialNo;
    /**
     * 任务所有者
     */
    private String owner;
    /**
     * 前办理人ID
     */
    private String assignee;
    /**
     * 前办理人名称
     */
    private String assigneeName;
    /**
     * 任务待办人员ID，多个以";"隔开
     */
    private String todoUserId;
    /**
     * 任务待办人员名称，多个以";"隔开
     */
    private String todoUserName;
    /**
     * 操作动作
     */
    private String action;
    /**
     * 操作动作类型
     */
    private String actionType;
    /**
     * 是否并行任务
     */
    private Boolean isParallel = false;
    /**
     * 发起并行任务的任务UUID
     */
    private String parallelTaskInstUuid;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 预警时间
     */
    private Date alarmTime;
    /**
     * 到期时间、逾期时间、承诺时间
     */
    private Date dueTime;
    /**
     * 逾期时间
     */
    private Date overdueTime;
    /**
     * 计时状态(0正常、1预警、2到期、3逾期)
     */
    private Integer timingState;
    /**
     * 预警状态(0未预警、1预警中)
     */
    private Integer alarmState;
    /**
     * 逾期状态(0未逾期、1逾期中)
     */
    private Integer overDueState;
    /**
     * 环节计时器UUID
     */
    private String taskTimerUuid;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 持续时间
     */
    private long duration;
    /**
     * 挂起状态(0正常、1失效、2删除、3管理员删除、4挂起)
     */
    private int suspensionState = 0;

    /**
     * 父结点
     */
    @UnCloneable
    private TaskInstance parent;
    /**
     * 自关联
     */
    @UnCloneable
    private Set<TaskInstance> children;

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
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return the flowInstance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_inst_uuid", nullable = false)
    public FlowInstance getFlowInstance() {
        return flowInstance;
    }

    /**
     * @param flowInstance 要设置的flowInstance
     */
    public void setFlowInstance(FlowInstance flowInstance) {
        this.flowInstance = flowInstance;
    }

    /**
     * @return the flowDefinition
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_def_uuid", nullable = false)
    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

    /**
     * @param flowDefinition 要设置的flowDefinition
     */
    public void setFlowDefinition(FlowDefinition flowDefinition) {
        this.flowDefinition = flowDefinition;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the serialNo
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * @param serialNo 要设置的serialNo
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner 要设置的owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return the assignee
     */
    public String getAssignee() {
        return assignee;
    }

    /**
     * @param assignee 要设置的assignee
     */
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    /**
     * @return the assigneeName
     */
    public String getAssigneeName() {
        return assigneeName;
    }

    /**
     * @param assigneeName 要设置的assigneeName
     */
    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    /**
     * @return the todoUserIds
     */
    @Column(length = 4000)
    public String getTodoUserId() {
        return todoUserId;
    }

    /**
     * @param todoUserIds 要设置的todoUserIds
     */
    public void setTodoUserId(String todoUserId) {
        this.todoUserId = todoUserId;
    }

    /**
     * @return the todoUserNames
     */
    @Column(length = 4000)
    public String getTodoUserName() {
        return todoUserName;
    }

    /**
     * @param todoUserNames 要设置的todoUserNames
     */
    public void setTodoUserName(String todoUserName) {
        this.todoUserName = todoUserName;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action 要设置的action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the actionType
     */
    public String getActionType() {
        return actionType;
    }

    /**
     * @param actionType 要设置的actionType
     */
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    /**
     * @return the isParallel
     */
    public Boolean getIsParallel() {
        return isParallel;
    }

    /**
     * @param isParallel 要设置的isParallel
     */
    public void setIsParallel(Boolean isParallel) {
        this.isParallel = isParallel;
    }

    /**
     * @return the parallelTaskInstUuid
     */
    public String getParallelTaskInstUuid() {
        return parallelTaskInstUuid;
    }

    /**
     * @param parallelTaskInstUuid 要设置的parallelTaskInstUuid
     */
    public void setParallelTaskInstUuid(String parallelTaskInstUuid) {
        this.parallelTaskInstUuid = parallelTaskInstUuid;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime 要设置的startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the alarmTime
     */
    public Date getAlarmTime() {
        return alarmTime;
    }

    /**
     * @param alarmTime 要设置的alarmTime
     */
    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    /**
     * @return the dueTime
     */
    public Date getDueTime() {
        return dueTime;
    }

    /**
     * @param dueTime 要设置的dueTime
     */
    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    /**
     * @return the overdueTime
     */
    public Date getOverdueTime() {
        return overdueTime;
    }

    /**
     * @param overdueTime 要设置的overdueTime
     */
    public void setOverdueTime(Date overdueTime) {
        this.overdueTime = overdueTime;
    }

    /**
     * @return the timingState
     */
    public Integer getTimingState() {
        return timingState;
    }

    /**
     * @param timingState 要设置的timingState
     */
    public void setTimingState(Integer timingState) {
        this.timingState = timingState;
    }

    /**
     * @return the alarmState
     */
    public Integer getAlarmState() {
        return alarmState;
    }

    /**
     * @param alarmState 要设置的alarmState
     */
    public void setAlarmState(Integer alarmState) {
        this.alarmState = alarmState;
    }

    /**
     * @return the overDueState
     */
    public Integer getOverDueState() {
        return overDueState;
    }

    /**
     * @param overDueState 要设置的overDueState
     */
    public void setOverDueState(Integer overDueState) {
        this.overDueState = overDueState;
    }

    /**
     * @return the taskTimerUuid
     */
    public String getTaskTimerUuid() {
        return taskTimerUuid;
    }

    /**
     * @param taskTimerUuid 要设置的taskTimerUuid
     */
    public void setTaskTimerUuid(String taskTimerUuid) {
        this.taskTimerUuid = taskTimerUuid;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * @param duration 要设置的duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * @return the suspensionState
     */
    public int getSuspensionState() {
        return suspensionState;
    }

    /**
     * @param suspensionState 要设置的suspensionState
     */
    public void setSuspensionState(int suspensionState) {
        this.suspensionState = suspensionState;
    }

    /**
     * @return the parent
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_inst_uuid")
    public TaskInstance getParent() {
        return parent;
    }

    /**
     * @param parent 要设置的parent
     */
    public void setParent(TaskInstance parent) {
        this.parent = parent;
    }

    /**
     * @return the children
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cascade({CascadeType.ALL})
    public Set<TaskInstance> getChildren() {
        return children;
    }

    /**
     * @param children 要设置的children
     */
    public void setChildren(Set<TaskInstance> children) {
        this.children = children;
    }

}
