/*
 * @(#)11/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.utils;

import com.wellsoft.pt.biz.support.WorkflowIntegrationParams;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.support.InteractionTaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;

import java.util.ArrayDeque;
import java.util.Deque;
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
 * 11/18/22.1	zhulh		11/18/22		Create
 * </pre>
 * @date 11/18/22
 */
public class BusinessIntegrationContextHolder {

    private static final ThreadLocal<Deque<String>> itemInstUuidContextHolder = new NamedThreadLocal<>("Item instance uuid context");
    private static final ThreadLocal<Deque<WorkflowIntegrationParams>> workflowIntegrationParamsContextHolder = new NamedThreadLocal<>("Workflow integration params context");
    private static final ThreadLocal<Deque<Event>> workflowEventContextHolder = new NamedThreadLocal<>("Workflow current workflow event");
    private static final ThreadLocal<Deque<Map<String, InteractionTaskData>>> interactionTaskDataContextHolder = new NamedThreadLocal<>("Interaction task data context");
    private static Logger logger = LoggerFactory.getLogger(BusinessIntegrationContextHolder.class);

    /**
     * @return
     */
    public static String getItemInstUuid() {
        if (itemInstUuidContextHolder.get() != null && !itemInstUuidContextHolder.get().isEmpty()) {
            return itemInstUuidContextHolder.get().peek();
        }
        return null;
    }

    /**
     * @param itemInstUuid
     */
    public static void setItemInstUuid(String itemInstUuid) {
        Deque<String> stack = itemInstUuidContextHolder.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
        }
        stack.push(itemInstUuid);
        itemInstUuidContextHolder.set(stack);
    }

    /**
     *
     */
    public static void removeItemInstUuid() {
        if (itemInstUuidContextHolder.get() != null && !itemInstUuidContextHolder.get().isEmpty()) {
            itemInstUuidContextHolder.get().pop();
        }
    }

    /**
     * @return
     */
    public static WorkflowIntegrationParams getWorkflowIntegrationParams() {
        if (workflowIntegrationParamsContextHolder.get() != null && !workflowIntegrationParamsContextHolder.get().isEmpty()) {
            return workflowIntegrationParamsContextHolder.get().peek();
        }
        return null;
    }

    /**
     * @param workflowIntegrationParams
     */
    public static void setWorkflowIntegrationParams(WorkflowIntegrationParams workflowIntegrationParams) {
        Deque<WorkflowIntegrationParams> stack = workflowIntegrationParamsContextHolder.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
        }
        stack.push(workflowIntegrationParams);
        workflowIntegrationParamsContextHolder.set(stack);
    }

    /**
     *
     */
    public static void removeWorkflowIntegrationParams() {
        if (workflowIntegrationParamsContextHolder.get() != null && !workflowIntegrationParamsContextHolder.get().isEmpty()) {
            workflowIntegrationParamsContextHolder.get().pop();
        }
    }

    public static void setWorkflowEvent(Event event) {
        Deque<Event> stack = workflowEventContextHolder.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
        }
        stack.push(event);
        workflowEventContextHolder.set(stack);
    }

    public static Event getWorkflowEvent() {
        if (workflowEventContextHolder.get() != null && !workflowEventContextHolder.get().isEmpty()) {
            return workflowEventContextHolder.get().peek();
        }
        return null;
    }

    public static void removeWorkflowEvent() {
        if (workflowEventContextHolder.get() != null && !workflowEventContextHolder.get().isEmpty()) {
            workflowEventContextHolder.get().pop();
        }
    }

    /**
     * @return
     */
    public static Map<String, InteractionTaskData> getInteractionTaskDataMap() {
        if (interactionTaskDataContextHolder.get() != null && !interactionTaskDataContextHolder.get().isEmpty()) {
            return interactionTaskDataContextHolder.get().peek();
        }
        return null;
    }

    /**
     * @param interactionTaskData
     */
    public static void setInteractionTaskDataMap(Map<String, InteractionTaskData> interactionTaskData) {
        Deque<Map<String, InteractionTaskData>> stack = interactionTaskDataContextHolder.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
        }
        stack.push(interactionTaskData);
        interactionTaskDataContextHolder.set(stack);
    }

    /**
     *
     */
    public static void removeInteractionTaskDataMap() {
        if (interactionTaskDataContextHolder.get() != null && !interactionTaskDataContextHolder.get().isEmpty()) {
            interactionTaskDataContextHolder.get().pop();
        }
    }
}
