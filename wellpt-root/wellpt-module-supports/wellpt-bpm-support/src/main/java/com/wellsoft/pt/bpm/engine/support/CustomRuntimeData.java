/*
 * @(#)2015年7月23日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年7月23日.1	zhulh		2015年7月23日		Create
 * </pre>
 * @date 2015年7月23日
 */
public class CustomRuntimeData {
    public static final String PREFIX = "custom_rt_";

    public static final String KEY_MESSAGE_SENDER_DISPATCHER = "custom_rt_messageSenderDispatcher";

    public static final String KEY_BIZ_ROLE_IDENTITY_RESOLVER = "custom_rt_bizRoleIdentityResolver";

    public static final String KEY_DISPATCHER_FLOW_RESOLVER = "custom_rt_dispatcherFlowResolver";

    public static final String KEY_DISPATCHER_BRANCH_TASK_RESOLVER = "custom_rt_dispatcherBranchTaskResolver";

    public static final String KEY_DYNAMIC_BUTTON_FILTER = "custom_rt_dynamic_button_filter";

    public static final String KEY_FLOW_LISTENER = "custom_rt_flowListener";

    public static final String KEY_TASK_LISTENER = "custom_rt_taskListener";

    public static final String KEY_DIRECTION_LISTENER = "custom_rt_directionListener";

    public static final String KEY_TIMER_LISTENER = "custom_rt_timerListener";

    public static final String KEY_FLOW_BIZ_DEF_ID = "custom_rt_flowBizDefId";

}
