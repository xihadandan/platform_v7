/*
 * @(#)2018年11月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.support;

/**
 * Description: 计时状态
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年11月12日.1	zhulh		2018年11月12日		Create
 * </pre>
 * @date 2018年11月12日
 */
public interface TimingState {

    // 正常
    public static final int NORMAL = 0;

    // 预警中
    public static final int ALARM = 1;

    // 到期中
    public static final int DUE = 2;

    // 逾期中
    public static final int OVER_DUE = 3;

}
