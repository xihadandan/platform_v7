/*
 * @(#)2021-01-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.log.LogEvent;

/**
 * Description: 数据库表LOG_BUSINESS_OPERATION的门面服务接口，提供给其他模块以及前端调用的业务接口
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-01-08.1	zhongzh		2021-01-08		Create
 * </pre>
 * @date 2021-01-08
 */
public interface LogFacadeService extends Facade {


    public static final String TOPIC = "wellpt-log-topic";

    /**
     * 统一日志
     *
     * @param event
     */
    public void sendLogEvent(LogEvent event);

    public void sendLogEventOutTransation(LogEvent event);

    String getModuleNameById(String id);


}
