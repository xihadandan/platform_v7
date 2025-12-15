/*
 * @(#)8/26/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.log.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.log.service.FlowLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/26/25.1	    zhulh		8/26/25		    Create
 * </pre>
 * @date 8/26/25
 */
public class FlowOperationLoggerHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowOperationLoggerHolder.class);

    private static final ThreadLocal<FlowOperationLogger> flowOperationLoggerHolder = new NamedThreadLocal<>("FlowOperationLogger");

    public static void create(String input) {
        FlowOperationLogger operationLogger = new FlowOperationLogger();
        operationLogger.setInput(input);
        flowOperationLoggerHolder.set(operationLogger);
    }

    public static void addTaskOperationUuid(String taskOperationUuid) {
        FlowOperationLogger operationLogger = flowOperationLoggerHolder.get();
        if (operationLogger != null) {
            operationLogger.getTaskOperationUuids().add(taskOperationUuid);
        }
    }

    public static void commit(Object output, HttpServletRequest request) {
        FlowOperationLogger operationLogger = flowOperationLoggerHolder.get();
        if (operationLogger != null) {
            FlowLogService flowLogService = ApplicationContextHolder.getBean(FlowLogService.class);
            flowLogService.logOperation(operationLogger.getInput(), output, operationLogger.getTaskOperationUuids(), request);
        }
    }

    public static void clear() {
        flowOperationLoggerHolder.remove();
    }

    private static class FlowOperationLogger {
        private String input;
        private String output;
        private List<String> taskOperationUuids = Lists.newArrayList();

        /**
         * @return the input
         */
        public String getInput() {
            return input;
        }

        /**
         * @param input 要设置的input
         */
        public void setInput(String input) {
            this.input = input;
        }

        /**
         * @return the output
         */
        public String getOutput() {
            return output;
        }

        /**
         * @param output 要设置的output
         */
        public void setOutput(String output) {
            this.output = output;
        }

        /**
         * @return the taskOperationUuids
         */
        public List<String> getTaskOperationUuids() {
            return taskOperationUuids;
        }

        /**
         * @param taskOperationUuids 要设置的taskOperationUuids
         */
        public void setTaskOperationUuids(List<String> taskOperationUuids) {
            this.taskOperationUuids = taskOperationUuids;
        }
    }
}
