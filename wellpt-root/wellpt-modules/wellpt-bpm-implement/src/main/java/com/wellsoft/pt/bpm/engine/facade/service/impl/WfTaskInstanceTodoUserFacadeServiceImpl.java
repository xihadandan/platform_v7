/*
 * @(#)2021-09-24 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.bpm.engine.facade.service.WfTaskInstanceTodoUserFacadeService;
import com.wellsoft.pt.bpm.engine.service.WfTaskInstanceTodoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 数据库表WF_TASK_INSTANCE_TODO_USER的门面服务实现类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-09-24.1	zenghw		2021-09-24		Create
 * </pre>
 * @date 2021-09-24
 */
@Service
public class WfTaskInstanceTodoUserFacadeServiceImpl extends AbstractApiFacade implements WfTaskInstanceTodoUserFacadeService {

    @Autowired
    private WfTaskInstanceTodoUserService wfTaskInstanceTodoUserService;


}
