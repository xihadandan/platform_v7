/*
 * @(#)2013-10-31 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core.handler;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bot.facade.service.BotFacadeService;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bot.support.BotParam.BotFromParam;
import com.wellsoft.pt.bot.support.BotResult;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.DirectionListener;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.bpm.engine.core.*;
import com.wellsoft.pt.bpm.engine.element.AlarmElement;
import com.wellsoft.pt.bpm.engine.element.TimerElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlow;
import com.wellsoft.pt.bpm.engine.enums.DirectionType;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.service.FlowArchiveService;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.service.TaskSubFlowService;
import com.wellsoft.pt.bpm.engine.support.CustomRuntimeData;
import com.wellsoft.pt.bpm.engine.support.MessageTemplate;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowScriptHelper;
import com.wellsoft.pt.bpm.engine.timer.TimerExecutor;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-31.1	zhulh		2013-10-31		Create
 * </pre>
 * @date 2013-10-31
 */
@Service
@Transactional
public class TransitionHanlderImpl extends AbstractHandler implements TransitionHanlder {

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private TaskSubFlowService taskSubFlowService;

    @Autowired
    private IdentityResolverStrategy identityResolverStrategy;

    @Autowired(required = false)
    private List<FlowArchiveService> flowArchiveServices;

    @Autowired
    private BotFacadeService botFacadeService;

    @Autowired
    private TimerExecutor timerExecutor;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.core.handler.AbstractHandler#execute(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    public void execute(Transition transition, ExecutionContext executionContext) {
        Node from = transition.getFrom();
        Node to = transition.getRuntimeToNode();
        String directionId = to.getDirectionId();
        Direction direction = null;
        // 满足判断条件时的流程
        if (StringUtils.isNotBlank(directionId)) {
            direction = executionContext.getFlowDelegate().getDirection(directionId);
        } else {
            direction = executionContext.getFlowDelegate().getDirection(from.getId(), to.getId());
        }
        if (direction == null) {
            direction = new Direction();
            direction.setFromID(from.getId());
            direction.setToID(to.getId());
            direction.setType(DirectionType.TaskToTask.getValue());
        }
        if (direction == null || StringUtils.isBlank(direction.getId())) {
            if (executionContext.getToken().getTaskData().isGotoTask(from.getId())) {
                // 发送流向消息
                direction.setFromID(from.getId());
                direction.setToID(to.getId());
                sendDirectionMessage(executionContext, to, direction);
            }
            return;
        }

        // 发送流向消息
        sendDirectionMessage(executionContext, to, direction);

        // 发布流向事件
        publishEvent(direction, executionContext);

        // 执行事件脚本
        executeEventScript(direction, executionContext);

        // 归档处理
        archive(direction, executionContext);

        // 反馈数据给主流程
        merge2ParentFlow(direction, executionContext);

        // 流向停止计时
        stopTimerByDirectionIfRequire(direction, executionContext);
    }

    /**
     * 如何描述该方法
     *
     * @param executionContext
     * @param to
     * @param direction
     */
    private void sendDirectionMessage(ExecutionContext executionContext, Node to, Direction direction) {
        /* modified by huanglinchuan 2014.10.27 begin*/
        // 消息分发
        List<UserUnitElement> unitElements = direction.getMsgRecipients();
        List<FlowUserSid> userIds = new ArrayList<FlowUserSid>(0);
        if (CollectionUtils.isNotEmpty(unitElements)) {
            userIds = identityResolverStrategy.resolve(to, executionContext.getToken(), unitElements,
                    ParticipantType.CopyUser);
        }
        List<MessageTemplate> templates = executionContext.getFlowDelegate().getMessageTemplateMap()
                .get(WorkFlowMessageTemplate.WF_WORK_DIRECTION_SEND_MSG.getType());
        MessageClientUtils.send(WorkFlowMessageTemplate.WF_WORK_DIRECTION_SEND_MSG, templates, direction, to,
                executionContext.getToken(), userIds);
        /* modified by huanglinchuan 2014.10.27 end*/
    }

    /**
     * @param executionContext
     */
    private void publishEvent(Direction direction, ExecutionContext executionContext) {
        String[] listeners = StringUtils.split(direction.getListener(), Separator.SEMICOLON.getValue());
        String rtDirectionListener = (String) executionContext.getToken().getTaskData()
                .getCustomData(CustomRuntimeData.KEY_DIRECTION_LISTENER);
        if (StringUtils.isNotBlank(rtDirectionListener)) {
            listeners = (String[]) ArrayUtils.addAll(listeners, StringUtils.split(rtDirectionListener, Separator.SEMICOLON.getValue()));
        }
        if (ArrayUtils.isEmpty(listeners)) {
            return;
        }
        Map<String, DirectionListener> listenerMap = ApplicationContextHolder.getApplicationContext().getBeansOfType(
                DirectionListener.class);
        if (MapUtils.isEmpty(listenerMap)) {
            return;
        }

        Event event = getEvent(null, Listener.DIRECTION, executionContext);
        // task#8100: 跳转环节触发环节的监听器
        // if (!WorkFlowOperation.GOTO_TASK.equals(event.getActionType())) {
        for (String listener : listeners) {
            DirectionListener directionListener = listenerMap.get(listener);
            if (directionListener == null) {
                continue;
            }
            try {
                directionListener.transition(event);
            } catch (Exception e) {
                String errorString = ExceptionUtils.getStackTrace(e);
                logger.error(errorString);
                if (e instanceof WorkFlowException) {
                    throw (WorkFlowException) e;
                } else {
                    throw new WorkFlowException("环节流向[" + direction.getName() + "]事件监听器" + "["
                            + directionListener.getName() + "]" + "执行环节流向转变事件处理出现异常: " + errorString);
                }
            }
        }
        // }
    }

    /**
     * @param direction
     * @param executionContext
     */
    private void executeEventScript(Direction direction, ExecutionContext executionContext) {
        Script script = direction.getEventScript();
        if (script == null) {
            return;
        }
        Event event = getEvent(null, Listener.DIRECTION, executionContext);
        WorkFlowScriptHelper.executeEventScript(event, script);
    }

    /**
     * @param direction
     * @param executionContext
     */
    private void archive(Direction direction, ExecutionContext executionContext) {
        // 不归档处理
        if (!executionContext.getToken().getTaskData().isArchive()) {
            return;
        }
        List<Archive> archives = direction.getArchives();
        if (CollectionUtils.isEmpty(archives)) {
            return;
        }
        if (CollectionUtils.isEmpty(flowArchiveServices)) {
            return;
        }
        Event event = getEvent(null, Listener.DIRECTION, executionContext);
        for (Archive archive : archives) {
            for (FlowArchiveService flowArchiveService : flowArchiveServices) {
                flowArchiveService.archive(event, archive);
            }
        }
    }

    /**
     * 合并数据到主流程
     *
     * @param direction
     * @param executionContext
     */
    private void merge2ParentFlow(Direction direction, ExecutionContext executionContext) {
        FlowInstance flowInstance = executionContext.getFlowInstance();
        FlowInstance parentFlowInstance = flowInstance.getParent();
        if (parentFlowInstance == null) {
            return;
        }

        // 正在发起子流程，不合并数据
        TaskData taskData = executionContext.getToken().getTaskData();
        if (taskData.getStartNewFlow(flowInstance.getUuid())) {
            return;
        }

        // 子流程信息
        TaskSubFlow taskSubFlow = taskSubFlowService.get(parentFlowInstance.getUuid(), flowInstance.getUuid());
        if (taskSubFlow == null) {
            return;
        }
        Boolean returnWithDirection = taskSubFlow.getReturnWithDirection();
        String returnDirectionId = taskSubFlow.getReturnDirectionId();
        String returnBotRuleId = taskSubFlow.getReturnBotRuleId();
        if (Boolean.TRUE.equals(returnWithDirection) && StringUtils.equals(direction.getId(), returnDirectionId)
                && StringUtils.isNotBlank(returnBotRuleId)) {
            Set<BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
            BotFromParam botFromParam = new BotFromParam(flowInstance.getDataUuid(), flowInstance.getFormUuid(), taskData.getDyFormData(flowInstance.getDataUuid()));
            froms.add(botFromParam);
            BotParam botParam = new BotParam(returnBotRuleId, froms);
            botParam.setFroms(froms);
            botParam.setTargetUuid(flowInstanceService.get(parentFlowInstance.getUuid()).getDataUuid());
            try {
                BotResult botResult = botFacadeService.startBot(botParam);
                logger.info("bot result dataUuid is {} for direction {}", botResult.getDataUuid(), returnDirectionId);
            } catch (Exception e) {
                throw new RuntimeException("流程数据合并到主流程时出错！", e);
            }
        }
    }

    /**
     * @param direction
     * @param executionContext
     */
    private void stopTimerByDirectionIfRequire(Direction direction, ExecutionContext executionContext) {
        String directionId = direction.getId();
        List<TimerElement> timerElements = executionContext.getFlowDelegate().getTimers();
        boolean isRequireStop = false;
        for (TimerElement timerElement : timerElements) {
            String overDirections = timerElement.getOverDirections();
            if (!AlarmElement.ALARM_TIMER_END_BY_DIRECTION.equals(timerElement.getTimeEndType())
                    || StringUtils.isBlank(overDirections)) {
                continue;
            }
            List<String> overDirectionList = Arrays.asList(StringUtils.split(overDirections,
                    Separator.SEMICOLON.getValue()));
            if (overDirectionList.contains(directionId)) {
                isRequireStop = true;
                break;
            }
        }

        if (!isRequireStop) {
            return;
        }

        timerExecutor.stopByDirection(direction, executionContext);
    }

}
