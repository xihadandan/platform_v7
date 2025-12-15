package com.wellsoft.pt.app.dingtalk.utils;

import com.wellsoft.pt.app.dingtalk.constants.DingtalkInfo;

/**
 * Description: 业务事件类型工具类
 *
 * @author liuyz
 * @version 1.0
 * @date 2021/8/20
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/8/20.1	liuyz		2021/8/20		Create
 * </pre>
 */
@Deprecated
public class EventTypeUtils {

    /**
     * 将事件类型转为同步内容
     *
     * @param callBackTag
     * @return
     */
    public static String eventType2SyncContent(String callBackTag) {
        switch (callBackTag) {
            case "user_add_org":// 通讯录用户增加
            case "user_modify_org":// 通讯录用户更改
                return DingtalkInfo.SYNC_CONTENT_USER + "、" + DingtalkInfo.SYNC_CONTENT_USER_WORK;
            case "user_leave_org":// 通讯录用户离职
            case "user_active_org":// 加入企业后用户激活
            case "org_admin_add":// 通讯录用户被设为管理员
            case "org_admin_remove":// 通讯录用户被取消设置管理员
                return DingtalkInfo.SYNC_CONTENT_USER;
            case "org_dept_create":// 通讯录企业部门创建
            case "org_dept_modify":// 通讯录企业部门修改
            case "org_dept_remove":// 通讯录企业部门删除
                return DingtalkInfo.SYNC_CONTENT_DEPT;
            default:
                return "";
        }
    }

}
