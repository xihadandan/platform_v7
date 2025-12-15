/*
 * @(#)2018年6月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
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
 * 2018年6月4日.1	zhulh		2018年6月4日		Create
 * </pre>
 * @date 2018年6月4日
 */
public interface NewFlowStages {
    // 阶段完成状态
    // 0 未完成
    public static final String STAGE_HANDLING_STATE_NONE = "0";
    // 1 已完成
    public static final String STAGE_HANDLING_STATE_COMPLETED = "1";

    // 分阶段状态
    public static final String STAGE_STATE_READY = "0";
    public static final String STAGE_STATE_DOING = "1";
    public static final String STAGE_STATE_COMPLETED = "2";
}
