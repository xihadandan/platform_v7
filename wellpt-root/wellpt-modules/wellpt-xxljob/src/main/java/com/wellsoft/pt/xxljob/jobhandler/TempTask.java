package com.wellsoft.pt.xxljob.jobhandler;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor;
import com.wellsoft.pt.bpm.engine.entity.FlowDelegationSettings;
import com.wellsoft.pt.bpm.engine.service.FlowDelegationSettingsService;
import com.wellsoft.pt.bpm.engine.timer.service.TaskTimerService;
import com.wellsoft.pt.bpm.engine.timer.service.impl.TaskAlarmSendRepeatMessageServiceImpl;
import com.wellsoft.pt.bpm.engine.timer.service.impl.TaskOverDueSendRepeatMessageServiceImpl;
import com.wellsoft.pt.handover.service.WhWorkHandoverService;
import com.wellsoft.pt.handover.service.impl.WhWorkHandoverServiceImpl;
import com.wellsoft.pt.org.service.DutyAgentService;
import com.wellsoft.pt.org.service.impl.DutyAgentServiceImpl;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.service.JobHandlerName;
import com.wellsoft.pt.xxljob.utils.ParamUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 临时任务
 */
@Component
public class TempTask {

    private Logger logger = LoggerFactory.getLogger(TempTask.class);

    /**
     * 任务逾期处理，消息通知
     */
    @XxlJob(value = JobHandlerName.Temp.TaskOverDueSendRepeatMessageJob)
    public ReturnT<String> taskOverDueSendRepeatMessageJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        String taskTimerUuid = executionParam.get("taskTimerUuid");
        if (StringUtils.isBlank(taskTimerUuid)) {
            XxlJobLogger.log("execute, but taskTimerUuid is blank");
            return ReturnT.SUCCESS;
        }
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            TaskTimerService taskTimerService = ApplicationContextHolder
                    .getBean(TaskOverDueSendRepeatMessageServiceImpl.class);
            taskTimerService.handler(taskTimerUuid);
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    @XxlJob(value = JobHandlerName.Temp.DutyAgentActiveStatusTraceJob)
    public ReturnT<String> dutyAgentActiveStatusTraceJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        String dutyAgentUuid = executionParam.get(DutyAgentServiceImpl.KEY_DUTY_AGENT_UUID);
        if (StringUtils.isBlank(dutyAgentUuid)) {
            XxlJobLogger.log("execute, but dutyAgentUuid is blank");
            return ReturnT.SUCCESS;
        }
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            DutyAgentService dutyAgentService = ApplicationContextHolder.getBean(DutyAgentService.class);
            dutyAgentService.deactive(dutyAgentUuid);
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    @XxlJob(value = JobHandlerName.Temp.TaskAlarmSendRepeatMessageJob)
    public ReturnT<String> taskAlarmSendRepeatMessageJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        String taskTimerUuid = executionParam.get("taskTimerUuid");
        if (StringUtils.isBlank(taskTimerUuid)) {
            XxlJobLogger.log("execute, but taskTimerUuid is blank");
            return ReturnT.SUCCESS;
        }
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            TaskTimerService taskTimerService = ApplicationContextHolder
                    .getBean(TaskAlarmSendRepeatMessageServiceImpl.class);
            taskTimerService.handler(taskTimerUuid);
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    @XxlJob(value = JobHandlerName.Temp.TaskDelegationCurrentWorkDueHandlerJob)
    public ReturnT<String> taskDelegationCurrentWorkDueHandlerJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        String delegationSettingsUuid = executionParam.get("delegationSettingsUuid");
        if (StringUtils.isBlank(delegationSettingsUuid)) {
            XxlJobLogger.log("execute, but delegationSettingsUuid is blank");
            return ReturnT.SUCCESS;
        }
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            FlowDelegationSettingsService flowDelegationSettingsService = ApplicationContextHolder
                    .getBean(FlowDelegationSettingsService.class);
            DelegationExecutor delegationExecutor = ApplicationContextHolder.getBean(DelegationExecutor.class);
            FlowDelegationSettings delegationSettings = flowDelegationSettingsService.getOne(delegationSettingsUuid);
            delegationExecutor.delegationCurrentWork(delegationSettings);
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    /**
     * 异步工作交接处理
     */
    @XxlJob(value = JobHandlerName.Temp.WorkHandoverHandlerJob)
    public ReturnT<String> workHandoverHandlerJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        String workHandoverUuid = executionParam.get("workHandoverUuid");

        if (StringUtils.isBlank(workHandoverUuid)) {
            XxlJobLogger.log("execute, but workHandoverUuid is blank");
            return ReturnT.SUCCESS;
        }
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            WhWorkHandoverService workHandoverService = ApplicationContextHolder
                    .getBean(WhWorkHandoverServiceImpl.class);
            workHandoverService.handoverTask(workHandoverUuid);
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

}
