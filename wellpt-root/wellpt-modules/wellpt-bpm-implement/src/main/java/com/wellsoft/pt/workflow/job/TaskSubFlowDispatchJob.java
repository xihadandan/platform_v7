/*
 * @(#)2021年7月14日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.job;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bot.exception.BotException;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlowDispatch;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.exception.*;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskSubFlowDispatchService;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.well.annotation.WellXxlJob;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Description: 子流程异步分发任务
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月14日.1	zhulh		2021年7月14日		Create
 * </pre>
 * @date 2021年7月14日
 */
@Component
public class TaskSubFlowDispatchJob {

    private static final String TASK_LOCK_CACHE_ID = ModuleID.WORKFLOW.getName();
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private TaskSubFlowDispatchService taskSubFlowDispatchService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private FlowInstanceService flowInstanceService;

    @XxlJob("taskSubFlowDispatchJob")
    @WellXxlJob(jobDesc = "工作流程_子流程_异步分发", jobCron = "0/30 * * * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}")
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("execute start");

        // List<TaskSubFlowDispatch> taskSubFlowDispatchs = taskSubFlowDispatchService.list2Dispatching();
        List<String> parentInstUuids = taskSubFlowDispatchService.listParentFlowInstUuidOfDispatching();
        Map<String, UserDetails> userDetailsMap = Maps.newConcurrentMap();

        // List<String> errorMsgs = executeDispatching(taskSubFlowDispatchs, userDetailsMap);//batchDispatching(taskSubFlowDispatchs);
        executeDispatching(parentInstUuids, userDetailsMap, true);

        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    /**
     * @param parentFlowInstUuid
     * @param taskSubFlowDispatchs
     * @param userDetailsMap
     * @return
     */
    private List<String> executeDispatching(String parentFlowInstUuid, List<TaskSubFlowDispatch> taskSubFlowDispatchs, Map<String, UserDetails> userDetailsMap) {
        // 归属系统信息
        String system = flowInstanceService.getSystemByUuid(parentFlowInstUuid);
        RequestSystemContextPathResolver.setSystem(system);

        List<String> errorMsgs = Lists.newArrayList();
        Cache cache = cacheManager.getCache(TASK_LOCK_CACHE_ID);
        Stopwatch timer = Stopwatch.createStarted();
        Map<String, Object> options = Maps.newHashMap();
        for (TaskSubFlowDispatch taskSubFlowDispatch : taskSubFlowDispatchs) {
            try {
                boolean isDispatch = getDispatch(cache, taskSubFlowDispatch);
                if (isDispatch) {
                    continue;
                }
                setDispatch(cache, taskSubFlowDispatch, true);

                login(taskSubFlowDispatch.getCreator(), userDetailsMap);

                taskSubFlowDispatchService.dispatchByUuid(taskSubFlowDispatch.getUuid(), options);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                XxlJobLogger.log(e);
                String errorMsg = handleDispatchException(e, taskSubFlowDispatch, taskSubFlowDispatchService);
                errorMsgs.add(errorMsg);
            } finally {
                setDispatch(cache, taskSubFlowDispatch, false);
                IgnoreLoginUtils.logout();
            }
        }
        RequestSystemContextPathResolver.clear();
        logger.info("分发{}个流程耗时：{}", taskSubFlowDispatchs.size(), timer.stop());
        return errorMsgs;
    }

    /**
     * @param parentFlowInstUuids
     * @param userDetailsMap
     * @param multiThread
     * @return
     */
    private void executeDispatching(List<String> parentFlowInstUuids, Map<String, UserDetails> userDetailsMap, boolean multiThread) {
        Cache cache = cacheManager.getCache(TASK_LOCK_CACHE_ID);
        for (String parentFlowInstUuid : parentFlowInstUuids) {
            List<String> errorMsgs = Lists.newArrayList();
            try {
                boolean isDispatch = getParentInstanceDispatch(cache, parentFlowInstUuid);
                if (isDispatch) {
                    continue;
                }
                setParentInstanceDispatch(cache, parentFlowInstUuid, true);

                List<String> result = null;
                List<TaskSubFlowDispatch> taskSubFlowDispatches = taskSubFlowDispatchService.list2DispatchingByParenFlowInstUuid(parentFlowInstUuid);
                if (multiThread) {
                    Future<List<String>> future = scheduledExecutorService.submit(() -> executeDispatching(parentFlowInstUuid, taskSubFlowDispatches, userDetailsMap));
                    try {
                        result = future.get();
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    } catch (ExecutionException e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    result = executeDispatching(parentFlowInstUuid, taskSubFlowDispatches, userDetailsMap);
                }
                errorMsgs.addAll(result);

                // 发送消息
                if (CollectionUtils.isNotEmpty(errorMsgs) && CollectionUtils.isNotEmpty(taskSubFlowDispatches)) {
                    try {
                        login(taskSubFlowDispatches.get(0).getCreator(), userDetailsMap);
                        sendDispatchMsg(taskSubFlowDispatches.get(0), errorMsgs);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        IgnoreLoginUtils.logout();
                    }
                }
            } finally {
                setParentInstanceDispatch(cache, parentFlowInstUuid, false);
            }
        }
    }


    /**
     * @param userId
     * @param userDetailsMap
     * @throws Exception
     */
    private void login(String userId, Map<String, UserDetails> userDetailsMap) throws Exception {
        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        UserDetails userDetails = userDetailsMap.get(userId);
        if (userDetails != null) {
            IgnoreLoginUtils.login(userDetails);
        } else {
            IgnoreLoginUtils.login(tenantId, userId);
            userDetails = SpringSecurityUtils.getCurrentUser();
            userDetailsMap.put(userId, userDetails);
        }
    }

    /**
     * @param cache
     * @param taskSubFlowDispatch
     * @return
     */
    private boolean getDispatch(Cache cache, TaskSubFlowDispatch taskSubFlowDispatch) {
        if (cache == null) {
            return false;
        }
        String cacheKey = getDispatchCacheKey(taskSubFlowDispatch);
        ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper != null) {
            Boolean dispatch = (Boolean) valueWrapper.get();
            return BooleanUtils.isTrue(dispatch);
        }
        return false;
    }

    /**
     * @param cache
     * @param taskSubFlowDispatch
     * @param dispatch
     */
    private void setDispatch(Cache cache, TaskSubFlowDispatch taskSubFlowDispatch, boolean dispatch) {
        if (cache == null) {
            return;
        }
        String cacheKey = getDispatchCacheKey(taskSubFlowDispatch);
        cache.put(cacheKey, dispatch);
    }

    /**
     * @param taskSubFlowDispatch
     * @return
     */
    private String getDispatchCacheKey(TaskSubFlowDispatch taskSubFlowDispatch) {
        return "taskSubFlowDispatch_" + taskSubFlowDispatch.getUuid();
    }

    /**
     * @param cache
     * @param parentInstUuid
     * @return
     */
    private boolean getParentInstanceDispatch(Cache cache, String parentInstUuid) {
        if (cache == null) {
            return false;
        }
        String cacheKey = getParentInstanceDispatchCacheKey(parentInstUuid);
        ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper != null) {
            Boolean dispatch = (Boolean) valueWrapper.get();
            return BooleanUtils.isTrue(dispatch);
        }
        return false;
    }

    /**
     * @param cache
     * @param parentInstUuid
     * @param dispatch
     */
    private void setParentInstanceDispatch(Cache cache, String parentInstUuid, boolean dispatch) {
        if (cache == null) {
            return;
        }
        String cacheKey = getParentInstanceDispatchCacheKey(parentInstUuid);
        cache.put(cacheKey, dispatch);
    }

    /**
     * @param parentInstUuid
     * @return
     */
    private String getParentInstanceDispatchCacheKey(String parentInstUuid) {
        return "parentInstanceDispatch_" + parentInstUuid;
    }

    /**
     * @param e
     * @param taskSubFlowDispatchService
     */
    private String handleDispatchException(Exception e, TaskSubFlowDispatch taskSubFlowDispatch,
                                           TaskSubFlowDispatchService taskSubFlowDispatchService) {
        String errorMsg = null;
        if (e instanceof TaskNotAssignedCopyUserException) {
            errorMsg = "无法指定抄送人！";
        } else if (e instanceof TaskNotAssignedMonitorException) {
            errorMsg = "无法指定督办人！";
        } else if (e instanceof MultipleJudgmentBranchFlowException) {
            errorMsg = "找不到指定的提交环节！";
        } else if (e instanceof MultiJobNotSelectedException) {
            errorMsg = "没有选择职位！";
        } else if (e instanceof TaskNotAssignedUserException) {
            errorMsg = "任务没有指定办理人！";
        } else if (e instanceof StaleObjectStateException) {
            StaleObjectStateException staleObjectStateException = (StaleObjectStateException) e;
            errorMsg = String.format("流程实例[%s]已被修改，请确保只有一个XXL-JOB服务执行任务！", staleObjectStateException.getIdentifier());
        } else if (e instanceof BotException) {
            errorMsg = "子流程分发时单据转换出错！" + e.getMessage();
        } else {
            errorMsg = ExceptionUtils.getStackTrace(e);
            if (StringUtils.length(errorMsg) > 500) {
                errorMsg = StringUtils.substring(errorMsg, 0, 500);
            }
        }
        if (e instanceof StaleObjectStateException) {
            taskSubFlowDispatchService.updateDispatchMsgByUuid(taskSubFlowDispatch.getUuid(), errorMsg);
        } else {
            taskSubFlowDispatchService.markDispatchErrorByUuid(taskSubFlowDispatch.getUuid(), errorMsg);
        }
        return errorMsg;
    }

    /**
     * @param taskSubFlowDispatch
     * @param errorMsgs
     */
    private void sendDispatchMsg(TaskSubFlowDispatch taskSubFlowDispatch, List<String> errorMsgs) {
        FlowService flowService = ApplicationContextHolder.getBean(FlowService.class);
        MessageClientApiFacade messageClientApiFacade = ApplicationContextHolder.getBean(MessageClientApiFacade.class);
        String templateId = WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_DISPATCH_FAILURE.name();
        List<IdEntity> entities = Lists.newArrayList();
        entities.add(taskSubFlowDispatch);
        FlowInstance parentFlowInstance = flowService.getFlowInstance(taskSubFlowDispatch.getParentFlowInstUuid());
        Map<Object, Object> values = Maps.newHashMap();
        values.put("title", parentFlowInstance.getTitle());
        values.put("parentTaskInstUuid", taskSubFlowDispatch.getParentTaskInstUuid());
        values.put("parentFlowInstUuid", taskSubFlowDispatch.getParentFlowInstUuid());
        values.put("errorMsg", StringUtils.join(errorMsgs, Separator.SEMICOLON.getValue()));
        List<String> userIds = Lists.newArrayList();
        userIds.add(taskSubFlowDispatch.getCreator());
        messageClientApiFacade.send(templateId, entities, values, userIds);
    }

}
