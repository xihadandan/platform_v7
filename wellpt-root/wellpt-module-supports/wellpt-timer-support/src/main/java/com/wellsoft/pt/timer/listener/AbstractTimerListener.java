/*
 * @(#)8/9/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.listener;

import com.wellsoft.pt.timer.support.event.TimerEvent;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/9/24.1	    zhulh		8/9/24		    Create
 * </pre>
 * @date 8/9/24
 */
public abstract class AbstractTimerListener implements TimerListener {

    @Override
    public void onTimerAlarm(TimerEvent event) {
    }

    @Override
    public void onTimerDue(TimerEvent event) {
    }

    @Override
    public void onTimerOverDue(TimerEvent event) {
    }

}
