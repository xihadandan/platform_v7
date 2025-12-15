package com.wellsoft.pt.app.dingtalk.enums;

/**
 * Description: 钉钉回调事件类型
 *
 * @author Well
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月29日.1	Well		2020年5月29日		Create
 * </pre>
 * @date 2020年5月29日
 */
// @EnumClass(objectName = "EnumCallBackEventType", keyName = "value", valueName = "remark")
public enum EnumCallBackEventType {

    user_add_org("user_add_org", "用户新增"), user_modify_org("user_modify_org", "用户修改"), user_leave_org(
            "user_leave_org", "用户删除"), user_active_org("user_active_org", "用户账号激活"), org_admin_add(
            "org_admin_add", "用户被设为管理员"), org_admin_remove("org_admin_remove", "用户被取消设置管理员"), org_dept_create(
            "org_dept_create", "部门新增"), org_dept_modify("org_dept_modify", "部门修改"), org_dept_remove(
            "org_dept_remove", "部门删除"), label_user_change("label_user_change", "员工角色信息发生变更"), label_conf_add(
            "label_conf_add", "增加角色或者角色组"), label_conf_del("label_conf_del", "删除角色或者角色组"), label_conf_modify(
            "label_conf_modify", "修改角色或者角色组");

    private String value = "";
    private String remark;

    private EnumCallBackEventType(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public static EnumCallBackEventType value2EnumObj(String value) {
        EnumCallBackEventType enumObj = null;
        for (EnumCallBackEventType status : EnumCallBackEventType.values()) {
            if (status.getValue().equals(value)) {
                enumObj = status;
            }
        }

        return enumObj;
    }

    public String getValue() {
        return value;
    }

    public String getRemark() {
        return remark;
    }
}
