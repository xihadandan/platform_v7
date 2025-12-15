package com.wellsoft.pt.workflow.enums;

/**
 * Description:
 * 排序字段
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2022/1/6   Create
 * </pre>
 */
public enum SortFieldEnum {

    taskInstUuid("taskInstUuid", "任务实列UUID", "t1.uuid"),
    flowInstUuid("flowInstUuid", "流程实例UUID", "t1.flow_inst_uuid"),
    todoId("todoId", "承办部门ID", "t3.todo_id"),
    todoName("todoName", "承办部门名称", "t3.todo_name"),
    currentTaskId("currentTaskId", "当前任务Id", "t1.id"),
    currentTaskName("currentTaskName", "当前任务名称", "t1.name"),
    currentTodoUserId("currentTodoUserId", "当前承办用户Id", "t1.todo_user_id"),
    currentTodoUserName("currentTodoUserName", "当前承办用户名称", "t1.todo_user_name"),
    limitUnit("limitUnit", "限制单位", "t4.limit_unit"),
    timerUuid("timerUuid", "时间UUID", "t4.timer_uuid"),
    flowDefId("flowDefId", "流程ID", "t3.flow_id"),
    isMajor("isMajor", "是否主流程", "t3.is_major"),
    isShare("isShare", "是否共享", "t3.is_share"),
    isWait("isWait", "是否等待", "t3.is_wait"),
    completionState("completionState", "完成状态", "t3.completion_state"),
    belongToTaskId("belongToTaskId", "所属任务ID", "t3.parent_task_id"),
    belongToTaskInstUuid("belongToTaskInstUuid", "所属任务实例UUID", "t3.parent_task_inst_uuid"),
    belongToFlowInstUuid("belongToFlowInstUuid", "所属流程实例UUID", "t3.parent_flow_inst_uuid"),
    ID("id", "流程定义ID", "t2.id"),
    name("name", "流程实例名称", "t2.name"),
    title("title", "标题", "t2.title"),
    startTime("startTime", "开始时间", "t2.start_time"),
    isTiming("isTiming", "是否计时", "t2.is_timing"),
    timingState("timingState", "计时状态", "t2.timing_state"),
    isOverDue("isOverDue", "是否逾期", "t2.is_over_due"),
    alarmTime("alarmTime", "预警时间", "t2.alarm_time"),
    dueTime("dueTime", "逾期时间", "t2.due_time"),
    endTime("endTime", "结束时间", "t2.end_time"),
    duration("duration", "流程总时间", "t2.duration"),
    startUserId("startUserId", "流程启动者ID", "t2.start_user_id"),
    ownerId("ownerId", "流程实例所有者ID", "t2.owner_id"),
    startDepartmentId("startDepartmentId", "流程发起部门ID", "t2.start_department_id"),
    startJobId("startJobId", "开始职位ID", "t2.startJobId"),
    ownerDepartmentId("ownerDepartmentId", "流程所属部门ID", "t2.owner_department_id"),
    startUnitId("startUnitId", "流程发起单位ID", "t2.start_unit_id"),
    ownerUnitId("ownerUnitId", "流程所属单位ID", "t2.owner_unit_id"),
    isActive("isActive", "当前流程是否处理活动状态", "t2.is_active");
    private String id;
    private String text;
    private String field;

    SortFieldEnum(String id, String text, String field) {
        this.id = id;
        this.text = text;
        this.field = field;
    }

    public static SortFieldEnum getSortField(String id) {
        for (SortFieldEnum sortField : SortFieldEnum.values()) {
            if (sortField.getId().equals(id)) {
                return sortField;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getField() {
        return field;
    }
}
