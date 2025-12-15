/*
 * @(#)2014-8-1 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.enums;

/**
 * Description: 参与者类型
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-1.1	zhulh		2014-8-1		Create
 * </pre>
 * @date 2014-8-1
 */
public enum ParticipantType {
    // 承办人
    TodoUser,
    // 转办人
    TransferUser,
    // 抄送人
    CopyUser,
    // 督办人
    SuperviseUser,
    // 监控人
    MonitorUser,
    // 阅读人
    ViewerUser
}
