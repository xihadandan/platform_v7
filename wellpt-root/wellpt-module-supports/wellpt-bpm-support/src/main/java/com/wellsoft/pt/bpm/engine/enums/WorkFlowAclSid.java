/*
 * @(#)2013-3-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-30.1	zhulh		2013-3-30		Create
 * </pre>
 * @date 2013-3-30
 */
public enum WorkFlowAclSid {
    // 基于流程创建者群组的SID
    GROUP_FLOW_CREATOR,
    // 基于流程创建者角色的SID
    ROLE_FLOW_CREATOR,
    // 基于流程创建者所有创建权限的SID
    //	GROUP_FLOW_ALL_CREATOR,
    // 基于流程创建者所有创建权限的角色SID
    ROLE_FLOW_ALL_CREATOR,
    /*add by huanglinchuan 2014.10.28 begin*/
    GROUP_FLOW_VIEWER,
    ROLE_FLOW_VIEWER,
    /*add by huanglinchuan 2014.10.28 end*/
    // 基于流程参与者群组的SID
    GROUP_FLOW_USER,
    // 基于流程参与者角色的SID
    ROLE_FLOW_USER,
    // 基于流程参与者群组所有参与权限的SID
    //	GROUP_FLOW_ALL_USER,
    // 基于流程参与者群组所有参与权限的角色SID
    ROLE_FLOW_ALL_USER
}
