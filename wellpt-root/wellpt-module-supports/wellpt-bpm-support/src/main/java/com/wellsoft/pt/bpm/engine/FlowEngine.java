/*
 * @(#)2012-11-2 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor;
import com.wellsoft.pt.bpm.engine.query.api.TaskQuery;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.jpa.query.Query;

/**
 * Description: 流程引擎类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-2.1	zhulh		2012-11-2		Create
 * </pre>
 * @date 2012-11-2
 */
public class FlowEngine {
    private static FlowEngine flowEngine = new FlowEngine();

    /**
     *
     */
    private FlowEngine() {
    }

    /**
     * 获取流程引擎实例
     *
     * @return
     */
    public static FlowEngine getInstance() {
        return flowEngine;
    }

    /**
     * 获取流程流程服务
     *
     * @return
     */
    public FlowService getFlowService() {
        FlowService flowService = ApplicationContextHolder.getBean(FlowService.class);
        return flowService;
    }

    /**
     * 获取任务环节服务
     *
     * @return
     */
    public TaskService getTaskService() {
        TaskService taskService = ApplicationContextHolder.getBean(TaskService.class);
        return taskService;
    }

    /**
     * 获取任务环节服务
     *
     * @return
     */
    public DelegationExecutor getDelegationExecutor() {
        DelegationExecutor delegationExecutor = ApplicationContextHolder.getBean(DelegationExecutor.class);
        return delegationExecutor;
    }

    /**
     * 获取查询服务
     *
     * @param queryClass
     * @return
     */
    public <T extends Query<?, ?>> T createQuery(Class<T> queryClass) {
        if (TaskQuery.class.equals(queryClass)) {
            return ApplicationContextHolder.getBean("taskQuery", queryClass);
        }
        return ApplicationContextHolder.getBean(queryClass);
    }

}
