/*
 * @(#)2015-7-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.support;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-16.1	zhulh		2015-7-16		Create
 * </pre>
 * @date 2015-7-16
 */
public class WfGzDataConstant {
    public static final String KEY_WF_GZ_ENABLE = "workflow.gz.enable";
    public static final String TRUE = "true";
    public static final String KEY_WELL_PT_REST_ADDRESS = "api.restful.webservice.address";
    public static final String DEFAULT_WELL_PT_REST_ADDRESS = "http://localhost/webservices/wellpt/rest/service";
    public static final String FLOW_DEF_ID = "WF_GZ_TEMPLATE";
    public static final String GZ_TASK_ID = "T_GZ";
    public static final String FLOW_LISTENER = "gzWorkDataFlowListener";
    // 抄送状态
    public static final Integer STATE_COPY_TO = 3;
    // 源流程名称
    public static final String SOURCE_FLOW_NAME = "source_flow_name";
    // 源流程定义ID
    public static final String SOURCE_FLOW_DEF_ID = "source_flow_def_id";
    // 源标题
    public static final String SOURCE_TITLE = "source_tile";
    // 源租户ID
    public static final String SOURCE_TENANT_ID = "source_tenant_id";
    // 目标租户ID
    public static final String TARGET_TENANT_ID = "target_tenant_id";
    // 原流程实例UUID
    public static final String SOURCE_FLOW_INST_UUID = "source_flow_inst_uuid";
    // 目标流程实例UUID
    public static final String FLOW_INST_UUID = "flowInstUuid";
    // 原环节实例UUID
    public static final String SOURCE_TASK_INST_UUID = "source_task_inst_uuid";
    // 原流水号
    public static final String SOURCE_SERIAL_NO = "source_serial_no";
    // 当前环节名称
    public static final String CURRENT_TASK_NAME = "current_task_name";
    // 当前环节ID
    public static final String CURRENT_TASK_ID = "current_task_id";
    // 当前环节办理人名称
    public static final String TODO_USER_NAME = "todo_user_name";
    // 当前环节办理人ID
    public static final String TODO_USER_ID = "todo_user_id";
    // 前办理人名称
    public static final String PREVIOUS_OPERATOR_NAME = "previous_operator_name";
    // 前办理人ID
    public static final String PREVIOUS_OPERATOR_ID = "previous_operator_id";
    // 到达时间
    public static final String ARRIVE_TIME = "arrive_time";
    // 到期时间
    public static final String DUE_TIME = "due_time";
    // 办理环节名称
    public static final String DONE_TASK_NAME = "done_task_name";
    // 办理环节ID
    public static final String DONE_TASK_ID = "done_task_id";
    // 办理时间
    public static final String DONE_TIME = "done_time";

}
