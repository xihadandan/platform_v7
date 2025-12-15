/*
 * @(#)8/14/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.log.service.impl;

import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.NetUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.script.support.ScriptDefinition;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.Pointcut;
import com.wellsoft.pt.bpm.engine.core.Script;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskActivity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.enums.TaskNodeType;
import com.wellsoft.pt.bpm.engine.log.model.FlowAutoHandleLogModel;
import com.wellsoft.pt.bpm.engine.log.model.FlowMessageSendLogModel;
import com.wellsoft.pt.bpm.engine.log.model.FlowOperationLogModel;
import com.wellsoft.pt.bpm.engine.log.model.FlowReadLogModel;
import com.wellsoft.pt.bpm.engine.log.service.FlowLogService;
import com.wellsoft.pt.bpm.engine.node.ScriptNode;
import com.wellsoft.pt.bpm.engine.node.TaskNode;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskActivityService;
import com.wellsoft.pt.bpm.engine.service.TaskOperationService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.service.impl.FlowSamplerServiceImpl;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.document.MongoDocumentService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.entity.MessageOutbox;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
@Service
public class FlowLogServiceImpl implements FlowLogService {

    private static final String FLOW_AUTO_HANDLE_LOG_COLLECTION = "flow_auto_handle_log";
    private static final String FLOW_OPERATION_LOG_COLLECTION = "flow_operation_log";
    private static final String FLOW_READ_LOG_COLLECTION = "flow_read_log";
    private static final String FLOW_MESSAGE_SEND_LOG_COLLECTION = "flow_message_send_log";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MongoDocumentService mongoDocumentService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskActivityService taskActivityService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    @Override
    public void logReader(WorkBean workData, UserDetails userDetails, HttpServletRequest request) {
        if (StringUtils.isBlank(workData.getTaskInstUuid())) {
            return;
        }

        String system = RequestSystemContextPathResolver.system();
        scheduledExecutorService.execute(() -> {
            try {
                asyncLogReader(workData, userDetails, system, request);
            } catch (Exception e) {
                logger.error("日志记录失败", e);
            }
        });
    }

    private void asyncLogReader(WorkBean workData, UserDetails userDetails, String system, HttpServletRequest request) {
        FlowReadLogModel logModel = new FlowReadLogModel();
        logModel.setTitle(workData.getTitle());
        logModel.setFlowName(workData.getName());
        logModel.setFlowDefId(workData.getFlowDefId());
        logModel.setTaskInstUuid(workData.getTaskInstUuid());
        logModel.setFlowInstUuid(workData.getFlowInstUuid());
        logModel.setReaderName(userDetails.getUserName());
        logModel.setReaderId(userDetails.getUserId());
        logModel.setReadTime(Calendar.getInstance().getTime());
        logModel.setClientIp(ServletUtils.getRemoteAddr(request));
        logModel.setCreateTime(Calendar.getInstance().getTime());
        logModel.setSystem(system);
        logModel.setTenant(userDetails.getTenantId());
        mongoDocumentService.save(FLOW_READ_LOG_COLLECTION, logModel);
    }


    @Override
    public void logAutoHandleTaskNode(ExecutionContext executionContext, TaskNode taskNode) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        FlowInstance flowInstance = executionContext.getToken().getFlowInstance();
        TaskInstance taskInstance = executionContext.getToken().getTask();
        TaskData taskData = executionContext.getToken().getTaskData();
        TaskElement taskElement = getOperateTaskElement(taskData, executionContext.getFlowDelegate(), taskNode.getId());
        FlowAutoHandleLogModel logModel = new FlowAutoHandleLogModel();
        logModel.setTitle(flowInstance.getTitle());
        logModel.setFlowName(flowInstance.getName());
        logModel.setFlowDefId(flowInstance.getId());
        logModel.setFlowInstUuid(flowInstance.getUuid());
        logModel.setTaskInstUuid(taskInstance.getUuid());
        logModel.setExpectTaskName(taskNode.getName());
        logModel.setExpectTaskId(taskNode.getId());
        logModel.setHandleResultCode(1);
        if (taskNode instanceof ScriptNode) {
            logModel.setType("scriptTask");
            logModel.setDetails(getAutoHandleScriptTaskDetails(executionContext, (ScriptNode) taskNode));
        } else {
            logModel.setType("skipTask");
            logModel.setDetails(getAutoHandleSkipTaskDetails(executionContext, taskNode));
        }
        if (taskElement != null) {
            logModel.setOperateTaskName(taskElement.getName());
            logModel.setOperateTaskId(taskElement.getId());
        }
        logModel.setOperatorName(userDetails.getUserName());
        logModel.setOperatorId(userDetails.getUserId());
        logModel.setOperateTime(Calendar.getInstance().getTime());
        logModel.setCreateTime(Calendar.getInstance().getTime());
        logModel.setSystem(flowInstance.getSystem());
        logModel.setTenant(userDetails.getTenantId());
        String logId = mongoDocumentService.save(FLOW_AUTO_HANDLE_LOG_COLLECTION, logModel);
        scheduledExecutorService.schedule(() -> {
            try {
                updateLogAutoHandleResult(logId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }, 5, TimeUnit.SECONDS);
    }

    private void updateLogAutoHandleResult(String logId) {
        DBObject dbObject = mongoDocumentService.getOne(FLOW_AUTO_HANDLE_LOG_COLLECTION, logId);
        if (dbObject == null) {
            return;
        }
        String taskInstUuid = Objects.toString(dbObject.get("taskInstUuid"), StringUtils.EMPTY);
        if (StringUtils.isBlank(taskInstUuid)) {
            return;
        }

        boolean exists = taskService.existsByUuid(taskInstUuid);
        if (exists) {
            dbObject.put("handleResultCode", 0);
            mongoDocumentService.updateByObjectId(FLOW_AUTO_HANDLE_LOG_COLLECTION, dbObject, logId);
        }
    }

    private TaskElement getOperateTaskElement(TaskData taskData, FlowDelegate flowDelegate, String toTaskId) {
        TaskElement taskElement = null;
        String preTaskId = taskData.getPreTaskId(toTaskId);
        if (StringUtils.isNotBlank(preTaskId)) {
            taskElement = flowDelegate.getFlow().getTask(preTaskId);
        }
        if (taskElement != null && TaskNodeType.ScriptTask.getValue().equals(taskElement.getType())) {
            return getOperateTaskElement(taskData, flowDelegate, taskElement.getId());
        }
        return taskElement;
    }

    private String getAutoHandleSkipTaskDetails(ExecutionContext executionContext, TaskNode taskNode) {
        StringBuilder sb = new StringBuilder();
        String emptyToTaskId = executionContext.getToken().getTaskData().getEmptyToTask(taskNode.getId());
        String emptyToTaskName = emptyToTaskId;
        if (StringUtils.isNotBlank(emptyToTaskId)) {
            TaskElement taskElement = executionContext.getToken().getFlowDelegate().getFlow().getTask(emptyToTaskId);
            if (taskElement != null) {
                emptyToTaskName = taskElement.getName();
            }
        }
        sb.append(String.format("跳过环节：%s，进入环节：%s", taskNode.getName(), emptyToTaskName));
        return sb.toString();
    }

    private String getAutoHandleScriptTaskDetails(ExecutionContext executionContext, ScriptNode scriptNode) {
        StringBuilder sb = new StringBuilder();
        Script createScript = executionContext.getFlowDelegate().getTaskEventScript(scriptNode.getId(), Pointcut.CREATED);
        if (createScript != null) {
            String scriptContent = getScriptContent(createScript);
            sb.append(String.format("环节创建执行%s脚本： %s", createScript.getType(), scriptContent));
        }

        Script endScript = executionContext.getFlowDelegate().getTaskEventScript(scriptNode.getId(), Pointcut.COMPLETED);
        if (endScript != null) {
            if (sb.length() > 0) {
                sb.append(Separator.LINE.getValue());
            }
            String scriptContent = getScriptContent(endScript);
            sb.append(String.format("环节完成执行%s脚本： %s", createScript.getType(), scriptContent));
        }
        return sb.toString();
    }

    /**
     * @param script
     * @return
     */
    private static String getScriptContent(Script script) {
        String contentType = script.getContentType();
        if (!StringUtils.equals(contentType, "1")) {
            return script.getContent();
        }
        BasicDataApiFacade basicDataApiFacade = ApplicationContextHolder.getBean(
                BasicDataApiFacade.class);
        ScriptDefinition scriptDefinitionEntity = basicDataApiFacade.getScriptDefinitionById(script.getContent());
        return scriptDefinitionEntity != null ? scriptDefinitionEntity.getContent() : script.getContent();
    }

    @Override
    public void logMessageSend(FlowMessageSendLogModel logModel) {
        String logId = mongoDocumentService.save(FLOW_MESSAGE_SEND_LOG_COLLECTION, logModel);

        AtomicInteger count = new AtomicInteger(0);
        scheduledExecutorService.schedule(() -> {
            try {
                updateLogMessageSendResult(logId, count);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }, 10, TimeUnit.SECONDS);
    }

    private void updateLogMessageSendResult(String logId, AtomicInteger count) {
        int tryCount = count.incrementAndGet();
        if (tryCount > 10) {
            return;
        }

        DBObject dbObject = mongoDocumentService.getOne(FLOW_MESSAGE_SEND_LOG_COLLECTION, logId);
        if (dbObject == null) {
            return;
        }
        String msgId = Objects.toString(dbObject.get("msgId"), StringUtils.EMPTY);
        if (StringUtils.isBlank(msgId)) {
            return;
        }

        String sendWay = Objects.toString(dbObject.get("sendWay"), StringUtils.EMPTY);

        MessageOutbox messageOutbox = messageClientApiFacade.getOutBoxByMessageId(msgId);
        Integer sendResultCode = messageClientApiFacade.getSendResultCodeByMessageId(msgId, StringUtils.split(sendWay, ","));
        if (messageOutbox != null) {
            dbObject.put("sendResultCode", sendResultCode);
            dbObject.put("messageOutboxUuid", messageOutbox.getUuid());
            dbObject.put("details", JsonUtils.object2Json(messageOutbox));
            mongoDocumentService.updateByObjectId(FLOW_MESSAGE_SEND_LOG_COLLECTION, dbObject, logId);
        }

        if (sendResultCode == null || !sendResultCode.equals(0)) {
            scheduledExecutorService.schedule(() -> {
                try {
                    updateLogMessageSendResult(logId, count);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }, 10, TimeUnit.SECONDS);
        }
    }

    @Override
    public void logOperation(String input, Object output, List<String> taskOperationUuids, HttpServletRequest request) {
        if (CollectionUtils.isEmpty(taskOperationUuids)) {
            return;
        }

        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String system = RequestSystemContextPathResolver.system();
        String clientIp = ServletUtils.getRemoteAddr(request);
        scheduledExecutorService.execute(() -> {
            try {
                RequestSystemContextPathResolver.setSystem(system);
                asyncLogOperation(input, output, taskOperationUuids, userDetails, clientIp, system);
            } catch (Exception e) {
                logger.error("日志记录失败", e);
            } finally {
                RequestSystemContextPathResolver.clear();
            }
        });
    }

    private void asyncLogOperation(String input, Object output, List<String> taskOperationUuids, UserDetails userDetails, String clientIp, String system) {
        List<TaskOperation> taskOperations = taskOperationService.listByUuids(taskOperationUuids);
        taskOperations.stream().forEach(taskOperation -> {
            FlowOperationLogModel logModel = new FlowOperationLogModel();
            BeanUtils.copyProperties(taskOperation, logModel);
            String flowInstUuid = taskOperation.getFlowInstUuid();
            FlowInstance flowInstance = flowService.getFlowInstance(flowInstUuid);
            logModel.setTitle(flowInstance.getTitle());
            logModel.setFlowName(flowInstance.getName());
            logModel.setFlowDefId(flowInstance.getId());
            logModel.setClientIp(clientIp);
            logModel.setServerIp(NetUtils.getLocalAddress());
            logModel.setResultCode(0);
            Map<String, Object> details = Maps.newHashMap();
            Map<String, Object> transition = Maps.newHashMap();
            TaskActivity taskActivity = taskActivityService.getByCreatorAndPreTaskInstUuid(taskOperation.getCreator(), taskOperation.getTaskInstUuid());
            if (taskActivity != null) {
                TaskInstance taskInstance = taskService.getTask(taskActivity.getTaskInstUuid());
                transition.put("fromTaskId", taskOperation.getTaskId());
                transition.put("fromTaskName", taskOperation.getTaskName());
                transition.put("toTaskId", taskInstance.getId());
                transition.put("toTaskName", taskInstance.getName());
                transition.put("todoUserName", taskInstance.getTodoUserName());
                transition.put("todoUserId", taskInstance.getTodoUserId());
            }
            details.put("input", JsonUtils.json2Object(input, Map.class));
            details.put("output", output);
            details.put("transition", transition);
            logModel.setDetails(JsonUtils.object2Json(details));
            logModel.setSystem(system);
            logModel.setTenant(userDetails.getTenantId());
            mongoDocumentService.save(FLOW_OPERATION_LOG_COLLECTION, logModel);
        });
    }

    @Override
    public void cleanUp(Integer retentionDays, String system) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -retentionDays);
        Date queryTime = calendar.getTime();
        DBObject query = new BasicDBObject()
                .append("createTime", new BasicDBObject("$lt", queryTime))
                .append("system", system);
        deleteLog(FLOW_AUTO_HANDLE_LOG_COLLECTION, query);
        deleteLog(FLOW_OPERATION_LOG_COLLECTION, query);
        deleteLog(FLOW_READ_LOG_COLLECTION, query);
        deleteLog(FLOW_MESSAGE_SEND_LOG_COLLECTION, query);
        deleteLog(FlowSamplerServiceImpl.MONGO_COLLECTION_NAME, query);
    }

    private void deleteLog(String collectionName, DBObject query) {
        try {
            mongoDocumentService.delete(collectionName, query);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
