/*
 * @(#)2012-11-27 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core.handler;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.FlowListener;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.bpm.engine.core.Pointcut;
import com.wellsoft.pt.bpm.engine.core.Script;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.repository.StartRepository;
import com.wellsoft.pt.bpm.engine.support.CustomRuntimeData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowScriptHelper;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-27.1	zhulh		2012-11-27		Create
 * </pre>
 * @date 2012-11-27
 */
@Service
@Transactional
public class StartHandlerImpl extends AbstractHandler implements StartHandler {

    @Autowired
    private StartRepository startRepository;

    @Autowired(required = false)
    private Map<String, FlowListener> listenerMap;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.AbstractHandler#enter(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void enter(Node node, ExecutionContext executionContext) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.AbstractHandler#execute(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void execute(Node node, ExecutionContext executionContext) {
        // System.out.println(startRepository);
        logger.debug(startRepository.toString());
        // executionContext.persistTask(node);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.handler.AbstractHandler#leave(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void leave(Node node, ExecutionContext executionContext) {
        // executionContext.persistTask(node);

        // 发布流程开始事件
        publishEvent(node, executionContext);

        // 执行事件脚本
        executeEventScript(node, executionContext, Pointcut.STARTED);
    }

    /**
     * @param node
     * @param executionContext
     */
    private void publishEvent(Node node, ExecutionContext executionContext) {
        String[] listeners = node.getListeners();
        String rtFlowListener = (String) executionContext.getToken().getTaskData()
                .getCustomData(CustomRuntimeData.KEY_FLOW_LISTENER);
        if (StringUtils.isNotBlank(rtFlowListener)) {
            listeners = (String[]) ArrayUtils.addAll(listeners, StringUtils.split(rtFlowListener, Separator.SEMICOLON.getValue()));
        }

        if (listeners == null || listeners.length == 0 || listenerMap == null || listenerMap.isEmpty()) {
            return;
        }

        Event event = getEvent(node, Listener.FLOW, executionContext);
        // task#8100: 跳转环节触发环节的监听器
        // if (!WorkFlowOperation.GOTO_TASK.equals(event.getActionType())) {
        for (String listener : listeners) {
            FlowListener flowListener = listenerMap.get(listener);
            if (flowListener == null) {
                continue;
            }
            try {
                flowListener.onStarted(event);
            } catch (Exception e) {
                String errorString = ExceptionUtils.getStackTrace(e);
                logger.error(errorString);
                if (e instanceof WorkFlowException) {
                    throw (WorkFlowException) e;
                } else {
                    throw new WorkFlowException("流程实例事件监听器" + "[" + flowListener.getName() + "]"
                            + "执行流程启动事件处理出现异常: " + errorString);
                }
            }
        }
        // }
    }

    /**
     * @param node
     * @param executionContext
     * @param pointcut
     */
    private void executeEventScript(Node node, ExecutionContext executionContext, String pointcut) {
        Script script = executionContext.getFlowDelegate().getFlowEventScript(pointcut);
        if (script == null) {
            return;
        }
        Event event = getEvent(node, Listener.FLOW, executionContext);
        WorkFlowScriptHelper.executeEventScript(event, script);
    }

}
