package com.wellsoft.pt.app.feishu.enums;

public enum FeishuEventEnum {

    user_created("contact.user.created_v3", "员工入职"),
    user_updated("contact.user.updated_v3", "员工信息被修改"),
    user_deleted("contact.user.deleted_v3", "员工离职"),
    department_created("contact.department.created_v3", "部门新建"),
    department_updated("contact.department.updated_v3", "部门信息变化"),
    department_deleted("contact.department.deleted_v3", "部门被删除");

    private String event;
    private String eventName;


    FeishuEventEnum(String event, String eventName) {
        this.event = event;
        this.eventName = eventName;
    }

    public String getEvent() {
        return event;
    }

    public String getEventName() {
        return eventName;
    }
}
