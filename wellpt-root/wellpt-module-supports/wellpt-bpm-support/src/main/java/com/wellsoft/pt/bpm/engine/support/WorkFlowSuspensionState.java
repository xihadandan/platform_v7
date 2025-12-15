/*
 * @(#)2014-9-30 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
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
 * 2014-9-30.1	zhulh		2014-9-30		Create
 * </pre>
 * @date 2014-9-30
 */
public class WorkFlowSuspensionState {
    // 正常
    public static final Integer Normal = 0;

    // 挂起
    public static final Integer Suspend = 1;

    // 删除
    public static final Integer Delete = 2;

    // 逻辑删除
    public static final Integer LOGIC_DELETED = 3;

    // 逻辑挂起
    public static final Integer LOGIC_SUSPEND = 4;
}
