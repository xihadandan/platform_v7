/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.listener;

import com.wellsoft.pt.timer.support.event.TimerEvent;
import org.springframework.core.Ordered;

/**
 * Description: 计时器监听器接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年4月9日.1	zhulh		2021年4月9日		Create
 * </pre>
 * @date 2021年4月9日
 */
public interface TimerListener extends Ordered {

    /**
     * 监听器名称
     *
     * @return
     */
    String getName();

    /**
     * 预警
     *
     * @param event
     */
    void onTimerAlarm(TimerEvent event);

    /**
     * 到期
     */
    void onTimerDue(TimerEvent event);

    /**
     * 逾期
     */
    void onTimerOverDue(TimerEvent event);

}
