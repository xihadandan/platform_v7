/*
 * @(#)2020年6月11日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.web.process;

import com.google.common.collect.Maps;
import org.springframework.core.NamedThreadLocal;

import java.util.concurrent.ConcurrentMap;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年6月11日.1	zhongzh		2020年6月11日		Create
 * </pre>
 * @date 2020年6月11日
 */
public abstract class ProcessUtils {

    protected final static ConcurrentMap<String, ProcessMessage> processMessages = Maps.newConcurrentMap();

    protected static final ThreadLocal<String> processHolder = new NamedThreadLocal<String>("Process Id");

    /**
     * 查询进度消息
     *
     * @param processId
     * @return
     */
    public static ProcessMessage get(String processId) {
        return processMessages.get(processId);
    }

    /**
     * 开始任务
     *
     * @param message
     */
    public static void start(String taskName) {
        String processId = processHolder.get();
        if (null == processId) {
            return;
        }
        ProcessMessage processMessage = processMessages.get(processId);
        if (null == processMessage) {
            return;
        }
        processMessage.setTotal(0);
        processMessage.setCurrent(0);
        processMessage.setTaskName(taskName);
        processMessage.setSubTaskName(null);
    }

    /**
     * 开始任务
     *
     * @param total
     * @param message
     */
    public static void start(long total, String taskName) {
        String processId = processHolder.get();
        if (null == processId) {
            return;
        }
        ProcessMessage processMessage = processMessages.get(processId);
        if (null == processMessage) {
            return;
        }
        processMessage.setCurrent(0);
        processMessage.setTotal(total);
        processMessage.setTaskName(taskName);
        processMessage.setSubTaskName(null);
    }

    /**
     * 更新进度
     *
     * @param inc
     * @param message
     */
    public static void increase(long increase, String subTaskName) {
        String processId = processHolder.get();
        if (null == processId) {
            return;
        }
        ProcessMessage processMessage = processMessages.get(processId);
        if (null == processMessage) {
            return;
        }
        processMessage.increase(increase);
        processMessage.setSubTaskName(subTaskName);
    }

    /**
     * 更新进度
     *
     * @param current
     * @param message
     */
    public static void update(long current, String subTaskName) {
        String processId = processHolder.get();
        if (null == processId) {
            return;
        }
        ProcessMessage processMessage = processMessages.get(processId);
        if (null == processMessage) {
            return;
        }
        processMessage.setCurrent(current);
        processMessage.setSubTaskName(subTaskName);
    }

}
