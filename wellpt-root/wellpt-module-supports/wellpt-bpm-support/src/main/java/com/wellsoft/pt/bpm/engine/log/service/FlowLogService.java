/*
 * @(#)8/14/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.log.service;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.log.model.FlowMessageSendLogModel;
import com.wellsoft.pt.bpm.engine.node.TaskNode;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.workflow.work.bean.WorkBean;

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
 * 8/14/25.1	    zhulh		8/14/25		    Create
 * </pre>
 * @date 8/14/25
 */
public interface FlowLogService {

    /**
     * 记录阅读日志
     *
     * @param workData
     * @param userDetails
     * @param request
     */
    void logReader(WorkBean workData, UserDetails userDetails, HttpServletRequest request);

    /**
     * 自动处理日志
     *
     * @param executionContext
     * @param taskNode
     */
    void logAutoHandleTaskNode(ExecutionContext executionContext, TaskNode taskNode);

    /**
     * 记录消息发送日志
     *
     * @param logModel
     */
    void logMessageSend(FlowMessageSendLogModel logModel);


    /**
     * 流转详细日志
     *
     * @param input
     * @param output
     * @param taskOperationUuids
     * @param request
     */
    void logOperation(String input, Object output, List<String> taskOperationUuids, HttpServletRequest request);

    /**
     * 清理日志
     */
    void cleanUp(Integer retentionDays, String system);
}
