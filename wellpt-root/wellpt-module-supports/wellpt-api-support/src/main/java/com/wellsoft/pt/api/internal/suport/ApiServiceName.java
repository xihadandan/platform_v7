/*
 * @(#)2014-8-16 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.internal.suport;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-16.1	zhulh		2014-8-16		Create
 * </pre>
 * @date 2014-8-16
 */
public class ApiServiceName {
    // 获取某流程定义信息
    public static final String FLOW_DEFINITION_GET = "workflow.flow.definition.get";

    public static final String FLOW_DEFINITION_DETAIL_GET = "workflow.flow.definition.detail.get";

    // 流程定义列表查询
    public static final String FLOW_DEFINITION_QUERY = "workflow.flow.definition.query";

    // 流程实例列表查询
    public static final String FLOW_INSTANCE_QUERY = "workflow.flow.instance.query";

    // 流程实例列表草稿查询
    public static final String FLOW_INSTANCE_DRAFT_QUERY = "workflow.flow.instance.draft.query";

    // 流程实例列表查询
    public static final String FLOW_INSTANCE_START = "workflow.flow.instance.start";

    public static final String FLOW_INSTANCE_END = "workflow.flow.instance.end";

    public static final String FLOW_PROCESS_GET = "workflow.flow.process.get";

    public static final String FLOW_FORM_DATA_SAVE = "workflow.flow.form.data.save";

    // 流程实例列表查询
    public static final String TASK_CANCEL = "workflow.task.cancel";

    public static final String TASK_COUNTER_SIGN = "workflow.task.counter.sign";

    public static final String TASK_DETAIL_GET = "workflow.task.detail.get";

    public static final String TASK_DETAIL_GET_FOR_HL = "workflow.task.detail.get.hl";

    public static final String TASK_GET = "workflow.task.get";

    // 工作查询
    public static final String TASK_QUERY = "workflow.task.query";
    // 待办工作查询
    public static final String TASK_TODO_QUERY = "workflow.task.todo.query";
    // 已办工作查询
    public static final String TASK_DONE_QUERY = "workflow.task.done.query";
    // 办结工作查询
    public static final String TASK_OVER_QUERY = "workflow.task.over.query";
    // 关注工作查询
    public static final String TASK_ATTENTION_QUERY = "workflow.task.attention.query";
    // 已阅工作查询
    public static final String TASK_READ_QUERY = "workflow.task.read.query";
    // 未阅工作查询
    public static final String TASK_UNREAD_QUERY = "workflow.task.unread.query";
    // 督办工作查询
    public static final String TASK_SUPERVISE_QUERY = "workflow.task.supervise.query";
    // 监控工作查询
    public static final String TASK_MONITOR_QUERY = "workflow.task.monitor.query";

    public static final String TASK_DIRECT_ROLL_BACK = "workflow.task.directRollback";

    public static final String TASK_ROLL_BACK = "workflow.task.rollback";

    public static final String TASK_SUBMIT = "workflow.task.submit";

    public static final String TASK_TRANSFER = "workflow.task.transfer";

    public static final String TASK_COPY_TO = "workflow.task.copyTo";

    public static final String TASK_ATTENTION = "workflow.task.attention";

    public static final String TASK_UNFOLLOW = "workflow.task.unfollow";

    public static final String TASK_REMIND = "workflow.task.remind";

    public static final String TASK_HAND_OVER = "workflow.task.handOver";

    public static final String TASK_GOTO_TASK = "workflow.task.gotoTask";

    public static final String TASK_SUSPEND = "workflow.task.suspend";

    public static final String TASK_RESUME = "workflow.task.resume";

    public static final String TASK_DELETE = "workflow.task.delete";

    public static final String GZ_FLOW_INSTANCE_START = "workflow.gz.flow.instance.start";

    public static final String GZ_TASK_SUBMIT = "workflow.gz.task.submit";

    public static final String GZ_TASK_CANCEL = "workflow.gz.task.cancel";

    public static final String GZ_TASK_COPY_TO = "workflow.gz.task.copy.to";

    public static final String GZ_TASK_DELETE = "workflow.gz.task.delete";

    public static final String TASK_OPERATE_PROCESS_POST = "index.index.flowResult";// 流程

    public static final String SECURITY_LOGIN = "security.login";// 用户登录

    public static final String SECURITY_SMSVERIFY = "security.smsverify";// 短信验证

    public static final String SECURITY_MODIFYPASSWORD = "security.modifyPassword";// 修改密码

    public static final String SECURITY_FINDPASSWORD = "security.findPassword";// 找回密码

    public static final String CHATAPP_GETVERSION = "chatapp.getversion";// 检查更新

    public static final String USER_GETINFO = "user.getInfo";// 个人资料获取
    public static final String USER_MODIFY = "user.modify";// 个人资料获取

    public static final String GROUP_GETINFO = "group.getInfo";// 群资料获取
    public static final String GROUP_ADD = "group.add";// 群创建
    public static final String GROUP_DELETE = "group.delete";// 群删除
    public static final String GROUP_MODIFY = "group.modify";// 群修改

    public static final String GROUPUSER_ADD = "groupuser.add";// 群加人
    public static final String GROUPUSER_DELETE = "groupuser.delete";// 群删人

    public static final String SCHEDULE_ADD = "schedule.add";// 日历新增
    public static final String SCHEDULE_DELETE = "schedule.delete";// 日历删除
    public static final String SCHEDULE_MODIFY = "schedule.modify";// 日历修改
    public static final String SCHEDULE_MYSCHEDULES = "schedule.mySchedules";// 我的日历
    public static final String SCHEDULE_QUERYBYDATE = "schedule.queryByDate";// 按时间查找日历
    public static final String SCHEDULETAG_GET = "scheduleTag.get"; //获取我的日历类型
    public static final String SCHEDULETAG_GET_DAY_COUNT = "schedule.getDayCount"; //每日事件数量
    // 公告查询
    public static final String NOTICE_QUERY = "notice.query";

    public static final String ORG_GET = "org.get";// 组织树获取（包括部门，人员）

    public static final String ORG_USER_GET = "org.user.get";// 获取人员

    public static final String SECURITY_QUERY = "security.query";// 权限查询

    public static final String ORG_USER_ORGINFO_QUERY = "org.user.orginfo.query";// 用户组织信息查询

    //发送消息
    public static final String MESSAGE_SEND = "message.send";// 发送消息
    public static final String MESSAGE_CANCEL = "message.cancel";// 取消消息

    public static final String CHATSERVER_MESSAGE_SENG = "chatServer.msgSend"; //app消息发送

    // 工作日服务
    public static final String WORKHOURSERVICE = "workHour";

    // 获取最新的可用的手机应用版本信息
    public static final String CHATVERSION = "chatVersion";

    // 查询用户
    public static final String QUERYFORUSERS = "workService.queryForUsers";

    //文件上传
    public static final String FILEUPLOAD = "fileUpload";

}
