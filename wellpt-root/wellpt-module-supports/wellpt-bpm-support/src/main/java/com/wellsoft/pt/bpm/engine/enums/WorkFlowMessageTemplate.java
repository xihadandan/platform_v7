/*
 * @(#)2013-4-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.enums;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-6.1	zhulh		2013-4-6		Create
 * </pre>
 * @date 2013-4-6
 */
public enum WorkFlowMessageTemplate {
    // 1、工作到达通知
    WF_WORK_TODO("TODO", "流程到达通知"),
    // 2、工作到达抄送通知
    WF_WORK_COPY("COPY", "流程到达抄送通知"),
    // 3、督办工作到达通知
    WF_WORK_SUPERVISE("SUPERVISE", "督办流程到达通知"),
    // 4、会签工作到达通知
    WF_WORK_COUNTER_SIGN("COUNTER_SIGN", "会签流程到达通知"),
    WF_WORK_ADD_SIGN("ADD_SIGN", "加签流程到达通知"),
    // 5、转办工作到达通知
    WF_WORK_TRANSFER("TRANSFER", "转办流程到达通知"),
    // 6、工作委托到达通知
    WF_WORK_ENTRUST("ENTRUST", "流程委托到达通知"),
    // 7、会签工作返回通知
    WF_WORK_COUNTER_SIGN_RETURN("COUNTER_SIGN_RETURN", "会签流程返回通知"),
    // 8、工作退回通知
    WF_WORK_ROLL_BACK("ROLL_BACK", "流程退回通知"),
    // 工作退回通知全部已办人员
    WF_WORK_ROLL_BACK_DONE("WORK_ROLL_BACK_DONE", "流程退回通知全部已办人员"),
    // 9、流程结束消息通知
    WF_WORK_OVER("OVER", "流程结束消息通知"),
    // 10、流程结束文件分发
    // WF_WORK_OVER_SEND_FILE("OVER_SEND_FILE"), // TODO
    // 10、流向消息通知
    WF_WORK_DIRECTION_SEND_MSG("DIRECTION_SEND_MSG", "流向消息通知"), // TODO
    // 10、流向文件分发
    // WF_WORK_DIRECTION_SEND_FILE("DIRECTION_SEND_FILE"), // TODO
    // 11、办理人阅读回执
    WF_WORK_READ_RETURN_RECEIPT("READ_RETURN_RECEIPT", "办理人阅读回执"), // TODO
    // 12、办理人为空消息通知
    WF_WORK_EMPTY_NOTE_DONE("EMPTY_NOTE_DONE", "办理人为空跳过环节消息通知"),
    // 13、预警提醒办理人员
    WF_WORK_ALARM_DOING("ALARM_DOING"),
    // 14、预警提醒督办人员
    WF_WORK_ALARM_SUPERVISE("ALARM_SUPERVISE"),
    // 15、预警提醒跟踪人员
    WF_WORK_ALARM_TRACER("ALARM_TRACER"),
    // 16、预警提醒流程其他人员
    WF_WORK_ALARM_OTHER("ALARM_OTHER"),
    // 17、预警提醒流程管理员
    WF_WORK_ALARM_ADMIN("ALARM_ADMIN"),
    // 18、逾期工作通知办理人员
    WF_WORK_DUE_DOING("DUE_DOING"),
    // 29、逾期工作通知督办人员
    WF_WORK_DUE_SUPERVISE("DUE_SUPERVISE"),
    // 20、逾期工作通知跟踪人员
    WF_WORK_DUE_TRACER("DUE_TRACER"),
    // 21、逾期工作通知其他人员
    WF_WORK_DUE_OTHER("DUE_OTHER"),
    // 22、逾期工作通知流程管理员
    WF_WORK_DUE_ADMIN("DUE_ADMIN"),
    // 23、逾期工作移交给B岗人员办理通知
    WF_WORK_DUE_TURN_OVER_TRUSTEE("DUE_TURN_OVER_TRUSTEE", "逾期流程移交给B岗人员办理通知"),
    // 24、逾期工作移交给督办人员办理通知
    WF_WORK_DUE_TURN_OVER_SUPERVISE("DUE_TURN_OVER_SUPERVISE", "逾期流程移交给督办人员办理通知"),
    // 25、逾期工作移交给其他人员办理通知
    WF_WORK_DUE_TURN_OVER_OTHER("DUE_TURN_OVER_OTHER", "逾期流程移交给其他人员办理通知"),
    // 24、逾期工作处理通知原办理人-退回原环节
    // 25、逾期工作处理通知原办理人-特送
    // 26、逾期工作处理通知原办理人-自动提交到下一环节
    // 27、终止工作委托通知被委托人
    // 28、文件特送通知原办理人
    // 29、工作撤回通知原办理人
    // 26、手动工作催办通知
    WF_WORK_REMIND("REMIND", "催办意见通知在办人"),
    // 31、督办意见通知在办人
    // 32、办理人为空转办提醒
    // 33、催办意见通知在办人
    // 27、子流程办结通知其他子流程在办人员
    WF_WORK_NOTIFY_SUB_FLOW_DOING("NOTIFY_SUB_FLOW_DOING", "子流程办结通知其他子流程在办人员"),

    // 预警通知在办人员的上级领导
    WF_WORK_ALARM_DOING_SUPERIOR("ALARM_DOING_SUPERIOR"),

    // 逾期通知在办人员的上级领导
    WF_WORK_DUE_DOING_SUPERIOR("DUE_DOING_SUPERIOR"),

    // 流程撤回消息通知
    WF_WORK_REVOKE("REVOKE", "流程撤回通知"),

    WF_WORK_DUE_RETRUN_PREV_TASK("DUE_RETURN_PREV_TASK", "逾期流程自动退回上一环节通知"),

    WF_WORK_DUE_ENTER_NEXT_TASK("DUE_ENTER_NEXT_TASK", "逾期流程自动进入下一环节通知"),

    WF_WORK_MAIN_SUB_FLOW_RETURN_OVER_OTHER_SUB_FLOW("MAIN_SUB_FLOW_RETURN_OVER_OTHER_SUB_FLOW", "主办子流程退回时自动办结协办子流程通知"),

    WF_WORK_SUB_FLOW_REDO("SUB_FLOW_REDO", "子流程重办通知"),

    WF_WORK_SUB_FLOW_REDO_DONE("SUB_FLOW_REDO_DONE", "子流程重办通知子流程全部已办人员"),

    WF_WORK_SUB_FLOW_STOP("SUB_FLOW_END", "子流程终止通知"),

    WF_WORK_SUB_FLOW_STOP_DONE("SUB_FLOW_END_DONE", "子流程终止通知子流程全部已办人员"),

    WF_WORK_SUB_FLOW_REMIND("SUB_FLOW_REMIND", "子流程催办通知"),

    WF_WORK_SUB_FLOW_REMIND_DOING_SUPERIOR("SUB_FLOW_REMIND_DOING_SUPERIOR", "子流程催办通知子流程办理人上级领导"),

    WF_WORK_SUB_FLOW_REMIND_SUPERVISE("SUB_FLOW_REMIND_SUPERVISE", "子流程催办通知子流程督办人员"),

    WF_WORK_SUB_FLOW_REMIND_TRACER("SUB_FLOW_REMIND_TRACER", "子流程催办通知子流程跟踪人员"),

    WF_WORK_SUB_FLOW_REMIND_ADMIN("SUB_FLOW_REMIND_ADMIN", "子流程催办通知子流程流程管理人员"),

    WF_WORK_SUB_FLOW_TIMELIMIT_MODIFY("SUB_FLOW_TIMELIMIT_MODIFY", "办理时限修改通知"),

    WF_WORK_SUB_FLOW_DISPATCH_FAILURE("SUB_FLOW_DISPATCH_FAILURE", "子流程分发失败通知"),

    WF_WORK_TASK_ARRIVE_NOTIFY("TASK_ARRIVE_NOTIFY", "环节到达消息通知"),

    WF_WORK_TASK_LEAVE_NOTIFY("TASK_LEAVE_NOTIFY", "环节离开消息通知"),

    WF_WORK_TASK_JUMP_FORWARD_NOTIFY("TASK_JUMP_FORWARD", "环节跳转消息通知"),

    WF_WORK_TASK_SUBMIT_NOTIFY("TASK_SUBMIT_NOTIFY", "环节提交消息通知"),

    WF_WORK_TASK_TRANSFER_NOTIFY("TASK_TRANSFER_NOTIFY", "环节转办消息通知"),

    WF_WORK_TASK_COUNTERSIGN_NOTIFY("TASK_COUNTERSIGN_NOTIFY", "环节会签消息通知"),

    WF_WORK_TASK_ADD_SIGN_NOTIFY("TASK_ADD_SIGN_NOTIFY", "环节加签消息通知"),

    WF_WORK_TASK_RETURN_NOTIFY("TASK_RETURN_NOTIFY", "环节退回消息通知"),

    WF_WORK_FLOW_ALARM("FLOW_ALARM", "预警提醒"),

    WF_WORK_FLOW_DUE("FLOW_DUE", "逾期提醒"),

    WF_WORK_OVER_NOTIFY_SPECIFIC_USER("OVER_NOTIFY_SPECIFIC_USER", "流程结束消息通知指定人员"),

    WF_WORK_DUE_TURN_OVER_NOTIFY_OLD_DOING("WORK_DUE_TURN_OVER_NOTIFY_OLD_DOING", "逾期流程移交给督办人员办理通知原办理人"),

    WF_WORK_DUE_TURN_OVER_OTHER_NOTIFY_OLD_DOING("WORK_DUE_TURN_OVER_OTHER_NOTIFY_OLD_DOING", "逾期流程移交给其他人员办理通知原办理人"),

    WF_WORK_DUE_RETRUN_PREV_TASK_NOTIFY_OLD_DOING("WORK_DUE_RETRUN_PREV_TASK_NOTIFY_OLD_DOING", "逾期流程自动退回上一环节通知原办理人"),

    WF_WORK_DUE_ENTER_NEXT_TASK_NOTIFY_OLD_DOING("WF_WORK_DUE_ENTER_NEXT_TASK_NOTIFY_OLD_DOING", "逾期流程自动进入下一环节通知原办理人");

    public static final Map<String, List<WorkFlowMessageTemplate>> FLOW_MSG_TEMPLATE_CATEGORY = Maps.newLinkedHashMap();

    static {
        FLOW_MSG_TEMPLATE_CATEGORY.put("流转通知", Lists.<WorkFlowMessageTemplate>newArrayList(WF_WORK_TODO, WF_WORK_COPY,
                WF_WORK_SUPERVISE, WF_WORK_COUNTER_SIGN, WF_WORK_ADD_SIGN, WF_WORK_TRANSFER, WF_WORK_ENTRUST,
                WF_WORK_COUNTER_SIGN_RETURN, WF_WORK_ROLL_BACK, WF_WORK_REVOKE, WF_WORK_EMPTY_NOTE_DONE,
                WF_WORK_READ_RETURN_RECEIPT, WF_WORK_REMIND, WF_WORK_OVER

        ));
        FLOW_MSG_TEMPLATE_CATEGORY.put("异常提醒", Lists.<WorkFlowMessageTemplate>newArrayList(WF_WORK_FLOW_ALARM,
                WF_WORK_FLOW_DUE, WF_WORK_DUE_TURN_OVER_TRUSTEE, WF_WORK_DUE_TURN_OVER_SUPERVISE,
                WF_WORK_DUE_TURN_OVER_OTHER, WF_WORK_DUE_RETRUN_PREV_TASK, WF_WORK_DUE_ENTER_NEXT_TASK));
        FLOW_MSG_TEMPLATE_CATEGORY.put("子流程", Lists.<WorkFlowMessageTemplate>newArrayList(
                WF_WORK_NOTIFY_SUB_FLOW_DOING, WF_WORK_MAIN_SUB_FLOW_RETURN_OVER_OTHER_SUB_FLOW, WF_WORK_SUB_FLOW_REDO,
                WF_WORK_SUB_FLOW_STOP, WF_WORK_SUB_FLOW_REMIND, WF_WORK_SUB_FLOW_TIMELIMIT_MODIFY));
        FLOW_MSG_TEMPLATE_CATEGORY.put("自定义", Lists.<WorkFlowMessageTemplate>newArrayList(WF_WORK_TASK_ARRIVE_NOTIFY,
                WF_WORK_TASK_LEAVE_NOTIFY, WF_WORK_TASK_JUMP_FORWARD_NOTIFY, WF_WORK_TASK_SUBMIT_NOTIFY,
                WF_WORK_TASK_TRANSFER_NOTIFY, WF_WORK_TASK_COUNTERSIGN_NOTIFY, WF_WORK_TASK_ADD_SIGN_NOTIFY,
                WF_WORK_TASK_RETURN_NOTIFY, WF_WORK_DIRECTION_SEND_MSG));
    }

    // 消息模板类型
    private String type;
    private String name;

    private WorkFlowMessageTemplate(String type) {
        this(type, "");
    }

    private WorkFlowMessageTemplate(String type, String name) {
        this.type = type;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
