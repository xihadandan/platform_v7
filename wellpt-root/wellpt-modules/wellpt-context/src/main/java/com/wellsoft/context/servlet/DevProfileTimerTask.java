/*
 * @(#)3/28/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.servlet;

import java.util.TimerTask;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 3/28/24.1	zhulh		3/28/24		Create
 * </pre>
 * @date 3/28/24
 */
public class DevProfileTimerTask extends TimerTask {
    private int runningDay;

    public DevProfileTimerTask(int runningDay) {
        this.runningDay = runningDay;
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        System.err.println("开发版单次运行超过" + runningDay + "天限制，自动退回！");
        System.exit(0);
    }
}
