/*
 * @(#)2018年11月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.service;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年11月9日.1	zhulh		2018年11月9日		Create
 * </pre>
 * @date 2018年11月9日
 */
public interface TaskDueHanlderService extends TaskTimerService {

    /**
     * 标记信息
     */
    boolean markDueInfo(String taskTimerUuid);

}
