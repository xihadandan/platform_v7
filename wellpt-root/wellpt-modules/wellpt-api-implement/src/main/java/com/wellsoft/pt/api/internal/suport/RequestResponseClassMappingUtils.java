/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.internal.suport;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.request.*;

import java.util.HashMap;
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
 * 2014-8-11.1	zhulh		2014-8-11		Create
 * </pre>
 * @date 2014-8-11
 */
public class RequestResponseClassMappingUtils {

    private static Map<String, Class<? extends WellptRequest<?>>> response2RequestMap = new HashMap<String, Class<? extends WellptRequest<?>>>();

    static {
        response2RequestMap.put(ApiServiceName.FLOW_DEFINITION_DETAIL_GET, FlowDefinitionDetailGetReqeust.class);
        response2RequestMap.put(ApiServiceName.FLOW_DEFINITION_GET, FlowDefinitionGetRequest.class);
        response2RequestMap.put(ApiServiceName.FLOW_DEFINITION_QUERY, FlowDefinitionQueryRequest.class);
        response2RequestMap.put(ApiServiceName.FLOW_INSTANCE_QUERY, FlowInstanceQueryRequest.class);
        response2RequestMap.put(ApiServiceName.FLOW_INSTANCE_DRAFT_QUERY, FlowInstanceDraftQueryRequest.class);
        response2RequestMap.put(ApiServiceName.FLOW_INSTANCE_START, FlowInstanceStartRequest.class);
        response2RequestMap.put(ApiServiceName.FLOW_INSTANCE_END, FlowInstanceEndRequest.class);
        response2RequestMap.put(ApiServiceName.FLOW_FORM_DATA_SAVE, FlowFormDataSaveRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_CANCEL, TaskCancelRequest.class);
        response2RequestMap.put(ApiServiceName.FLOW_PROCESS_GET, FlowProcessGetRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_COUNTER_SIGN, TaskCounterSignRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_DETAIL_GET, TaskDetailGetRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_GET, TaskGetRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_QUERY, TaskQueryRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_TODO_QUERY, TaskTodoQueryRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_DONE_QUERY, TaskDoneQueryRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_OVER_QUERY, TaskOverQueryRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_ATTENTION_QUERY, TaskAttentionQueryRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_READ_QUERY, TaskReadQueryRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_UNREAD_QUERY, TaskUnreadQueryRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_SUPERVISE_QUERY, TaskSuperviseQueryRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_MONITOR_QUERY, TaskMonitorQueryRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_DIRECT_ROLL_BACK, TaskDirectRollbackRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_ROLL_BACK, TaskRollbackRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_SUBMIT, TaskSubmitRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_TRANSFER, TaskTransferRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_COPY_TO, TaskCopyToRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_ATTENTION, TaskAttentionRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_UNFOLLOW, TaskUnfollowRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_REMIND, TaskRemindRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_HAND_OVER, TaskHandOverRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_GOTO_TASK, TaskGotoTaskRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_SUSPEND, TaskSuspendRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_RESUME, TaskResumeRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_DELETE, TaskDeleteRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_OPERATE_PROCESS_POST, TaskOperateProcessPostRequest.class);

        response2RequestMap.put(ApiServiceName.GZ_FLOW_INSTANCE_START, GzFlowInstanceStartRequest.class);
        response2RequestMap.put(ApiServiceName.GZ_TASK_SUBMIT, GzTaskSubmitRequest.class);
        response2RequestMap.put(ApiServiceName.GZ_TASK_CANCEL, GzTaskCancelRequest.class);
        response2RequestMap.put(ApiServiceName.GZ_TASK_COPY_TO, GzTaskCopyToRequest.class);
        response2RequestMap.put(ApiServiceName.GZ_TASK_DELETE, GzTaskDeleteRequest.class);

        response2RequestMap.put(ApiServiceName.NOTICE_QUERY, NoticeQueryRequest.class);
        response2RequestMap.put(ApiServiceName.SECURITY_LOGIN, SecurityLoginRequest.class);
        // response2RequestMap.put(ApiServiceName.ORG_GET, OrgGetRequest.class);
        // response2RequestMap.put(ApiServiceName.ORG_USER_GET,
        // OrgUserGetRequest.class);
        response2RequestMap.put(ApiServiceName.SECURITY_QUERY, SecurityQueryRequest.class);
        // response2RequestMap.put(ApiServiceName.ORG_USER_ORGINFO_QUERY,
        // OrgUserOrgInfoQueryRequest.class);
        response2RequestMap.put(ApiServiceName.MESSAGE_SEND, MessageSendRequest.class);
        response2RequestMap.put(ApiServiceName.MESSAGE_CANCEL, MessageCancelRequest.class);
        response2RequestMap.put(ApiServiceName.SECURITY_FINDPASSWORD, SecurityFindPasswordRequest.class);
        response2RequestMap.put(ApiServiceName.SECURITY_MODIFYPASSWORD, SecurityModifyPasswordRequest.class);
        response2RequestMap.put(ApiServiceName.SECURITY_SMSVERIFY, SecuritySmsVerifyRequest.class);
        response2RequestMap.put(ApiServiceName.CHATAPP_GETVERSION, ChatAppGetVersionRequest.class);
        // response2RequestMap.put(ApiServiceName.GROUP_GETINFO,
        // OrgGroupInfoRequest.class);
        // response2RequestMap.put(ApiServiceName.USER_GETINFO,
        // OrgUserInfoRequest.class);
        response2RequestMap.put(ApiServiceName.USER_MODIFY, UserModifyRequest.class);

        response2RequestMap.put(ApiServiceName.GROUP_ADD, GroupAddRequest.class);
        response2RequestMap.put(ApiServiceName.GROUP_DELETE, GroupDeleteRequest.class);
        response2RequestMap.put(ApiServiceName.GROUP_MODIFY, GroupModifyRequest.class);

        response2RequestMap.put(ApiServiceName.GROUPUSER_ADD, GroupUserAddRequest.class);
        response2RequestMap.put(ApiServiceName.GROUPUSER_DELETE, GroupUserDeleteRequest.class);

        response2RequestMap.put(ApiServiceName.SCHEDULE_ADD, ScheduleAddRequest.class);
        response2RequestMap.put(ApiServiceName.SCHEDULE_DELETE, ScheduleDeleteRequest.class);
        response2RequestMap.put(ApiServiceName.SCHEDULE_MODIFY, ScheduleModifyRequest.class);
        response2RequestMap.put(ApiServiceName.SCHEDULE_MYSCHEDULES, ScheduleMySchedulesRequest.class);
        response2RequestMap.put(ApiServiceName.SCHEDULE_QUERYBYDATE, ScheduleQueryByDateRequest.class);
        response2RequestMap.put(ApiServiceName.CHATSERVER_MESSAGE_SENG, ChatServerMsgSendRequest.class);
        response2RequestMap.put(ApiServiceName.SCHEDULETAG_GET, ScheduleTagGetRequest.class);
        response2RequestMap.put(ApiServiceName.SCHEDULETAG_GET_DAY_COUNT, ScheduleGetDayCountRequest.class);
        response2RequestMap.put(ApiServiceName.WORKHOURSERVICE, WorkHourRequest.class);
        response2RequestMap.put(ApiServiceName.QUERYFORUSERS, QueryForUsersRequest.class);

        response2RequestMap.put(ApiServiceName.FILEUPLOAD, FileUploadRequest.class);
        response2RequestMap.put(ApiServiceName.TASK_DETAIL_GET_FOR_HL, TaskDetailForHlGetRequest.class);
    }

    public static final Class<? extends WellptRequest<?>> getRequestClass(String apiServiceName) {
        return response2RequestMap.get(apiServiceName);
    }

    public static void registerResponse2RequestMap(String apiServiceName, Class clazz) {
        response2RequestMap.put(apiServiceName, clazz);
    }
}
