/*
 * @(#)2013-3-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-25.1	zhulh		2013-3-25		Create
 * </pre>
 * @date 2013-3-25
 */
public enum WorkFlowFieldMapping {
    // 创建人
    CREATOR("Creator", "WORKFLOW_CREATOR"),
    // 创建时间
    CREATE_TIME("Create Time", "WORKFLOW_CREATE_TIME"),
    // 创建人部门
    CREATOR_DEPARTMENT("Creator Department", "WORKFLOW_CREATOR_DEPARTMENT"),
    // 创建人岗位
    CREATOR_POST("Creator Post", "WORKFLOW_CREATOR_POST"),
    // 标题
    TITLE("Title", "WORKFLOW_TITLE"),
    // 流程实例UUID
    FLOW_INST_UUID("FlowInstUuid", "FLOW_INST_UUID"),
    // 环节实例UUID
    TASK_INST_UUID("TaskInstUuid", "TASK_INST_UUID"),
    // 当前流程状态名称
    CURRENT_FLOW_STATE_NAME("Flow State Name", "WORKFLOW_CURRENT_FLOW_STATE_NAME"),
    // 当前流程状态代码
    CURRENT_FLOW_STATE_CODE("Flow State Code", "WORKFLOW_CURRENT_FLOW_STATE_CODE"),
    // 当前环节名称
    CURRENT_TASK("Current Task", "WORKFLOW_CURRENT_TASK"),
    // 当前环节ID
    CURRENT_TASK_ID("Current Task ID", "WORKFLOW_CURRENT_TASK_ID"),
    // 当前环节办理人名称
    CURRENT_TASK_TODO_USER_NAME("Current Task Todo User Name", "WORKFLOW_CURRENT_TASK_TODO_USER_NAME"),
    // 当前环节办理人ID
    CURRENT_TASK_TODO_USER_ID("Current Task Todo User Id", "WORKFLOW_CURRENT_TASK_TODO_USER_ID"),
    // 下一环节
    NEXT_TASK("Next Task", "WORKFLOW_NEXT_TASK"),
    // 办理意见
    OPINION("Workflow Opinion", "WORKFLOW_OPINION"),
    // 流水号
    SERIAL_NO("Serial Number", "WORKFLOW_SERIAL_NO"),
    // 办理时限
    TIME_LIMIT("Time Limit", "WORKFLOW_TIME_LIMIT"),
    // 到期时间，如果有值会更新表单对应的映射值，没值不处理
    DUE_TIME("DUE TIME", "WORKFLOW_DUE_TIME"),
    // 计时器状态
    TIMER_STATUS("TIMER STATUS", "WORKFLOW_TIMER_STATUS"),

    /**
     * 预留字段
     **/
    // 16字符长度
    RESERVED_TEXT_1("reservedText1", "WORKFLOW_RESERVED_TEXT_1"),
    // 64字符长度
    RESERVED_TEXT_2("reservedText2", "WORKFLOW_RESERVED_TEXT_2"),
    // 64字符长度
    RESERVED_TEXT_3("reservedText3", "WORKFLOW_RESERVED_TEXT_3"),
    // 255字符长度
    RESERVED_TEXT_4("reservedText4", "WORKFLOW_RESERVED_TEXT_4"),
    // 255字符长度
    RESERVED_TEXT_5("reservedText5", "WORKFLOW_RESERVED_TEXT_5"),
    // 255字符长度
    RESERVED_TEXT_6("reservedText6", "WORKFLOW_RESERVED_TEXT_6"),
    // 255字符长度
    RESERVED_TEXT_7("reservedText7", "WORKFLOW_RESERVED_TEXT_7"),
    // 255字符长度
    RESERVED_TEXT_8("reservedText8", "WORKFLOW_RESERVED_TEXT_8"),
    // 255字符长度
    RESERVED_TEXT_9("reservedText9", "WORKFLOW_RESERVED_TEXT_9"),
    // 255字符长度
    RESERVED_TEXT_10("reservedText10", "WORKFLOW_RESERVED_TEXT_10"),
    // 255字符长度
    RESERVED_TEXT_11("reservedText11", "WORKFLOW_RESERVED_TEXT_11"),
    // 255字符长度
    RESERVED_TEXT_12("reservedText12", "WORKFLOW_RESERVED_TEXT_12"),
    // 整型
    RESERVED_NUMBER_1("reservedNumber1", "WORKFLOW_RESERVED_NUMBER_1"),
    // 浮点型
    RESERVED_NUMBER_2("reservedNumber2", "WORKFLOW_RESERVED_NUMBER_2"),
    // 浮点型
    RESERVED_NUMBER_3("reservedNumber3", "WORKFLOW_RESERVED_NUMBER_3"),
    // 日期
    RESERVED_DATE_1("reservedDate1", "WORKFLOW_RESERVED_DATE_1"),
    // 日期
    RESERVED_DATE_2("reservedDate2", "WORKFLOW_RESERVED_DATE_2");

    private String name;
    private String value;

    /**
     * 如何描述该构造方法
     *
     * @param name
     * @param value
     */
    private WorkFlowFieldMapping(String name, String value) {
        this.name = name;
        this.value = value;
    }

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
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
