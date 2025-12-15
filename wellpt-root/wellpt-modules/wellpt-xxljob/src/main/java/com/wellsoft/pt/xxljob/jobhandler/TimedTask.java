package com.wellsoft.pt.xxljob.jobhandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.api.entity.ApiCommandEntity;
import com.wellsoft.pt.api.service.ApiCommandService;
import com.wellsoft.pt.app.dingtalk.facade.DingtalkOrgSyncApi;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiUtils;
import com.wellsoft.pt.basicdata.serialnumber.service.ISerialNumberRelationService;
import com.wellsoft.pt.bpm.engine.delegation.service.TaskDelegationTakeBackService;
import com.wellsoft.pt.bpm.engine.entity.FlowDelegationSettings;
import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;
import com.wellsoft.pt.bpm.engine.service.FlowDelegationSettingsService;
import com.wellsoft.pt.bpm.engine.service.TaskDelegationService;
import com.wellsoft.pt.bpm.engine.timer.job.TaskTimerCleanUpJob;
import com.wellsoft.pt.common.job.CleanUpAppTempDataJob;
import com.wellsoft.pt.di.entity.DiConfigEntity;
import com.wellsoft.pt.di.service.DiConfigService;
import com.wellsoft.pt.di.util.CamelContextUtils;
import com.wellsoft.pt.dms.enums.DocExchangeRecordStatusEnum;
import com.wellsoft.pt.dms.service.DmsDocExchangeExpireService;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordService;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.message.server.impl.JmsMessageConsumer;
import com.wellsoft.pt.message.service.MessageBackupService;
import com.wellsoft.pt.repository.jobs.GarbageCollectorStartUp;
import com.wellsoft.pt.repository.jobs.UploadFileFromSftp2MongoJob;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;
import com.wellsoft.pt.webmail.entity.WmMailUserEntity;
import com.wellsoft.pt.webmail.service.WmMailConfigService;
import com.wellsoft.pt.webmail.service.WmMailUserService;
import com.wellsoft.pt.webmail.service.WmMailboxInfoUserService;
import com.wellsoft.pt.webmail.support.WebmailUseCapacityDataStoreRender;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.service.JobHandlerName;
import com.wellsoft.pt.xxljob.utils.ParamUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.well.annotation.WellXxlJob;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务
 */
@Component
public class TimedTask {
    private Logger logger = LoggerFactory.getLogger(TimedTask.class);

    @XxlJob(JobHandlerName.Timed.TestXxlJob)
    @WellXxlJob(jobDesc = "测试调度网络是否畅通，请手动执行查看日志",
            jobCron = "0 0 0 * * ?", triggerStatus = false)
    public ReturnT<String> testXxlJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        XxlJobLogger.log(param);
        logger.error("xxl-job测试调度网络是否畅通");
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    /**
     * 每分钟执行一次
     */
    /*@XxlJob(JobHandlerName.Timed.DefaultWebmailClientSyncInboxMailJob)
    @WellXxlJob(jobDesc = "默认Webmail客户端同步收件箱",
            jobCron = "0 * * * * ?",
            executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = false)
    public ReturnT<String> defaultWebmailClientSyncInboxMailJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            DefaultWebmailClientSyncInboxMailJob.startSyncInboxMail(null);
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }*/

    /**
     * 每天凌晨1点执行
     *
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob(JobHandlerName.Timed.SerialNumberDataCleaningTask)
    @WellXxlJob(jobDesc = "流水号数据清理任务",
            jobCron = "0 0 01 * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = true)
    public ReturnT<String> serialNumberDataCleaningTask(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            ISerialNumberRelationService serialNumberRelationService = ApplicationContextHolder.getBean(ISerialNumberRelationService.class);
            serialNumberRelationService.dataCleaningTask(executionParam);
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
     * 每秒执行一次
     */
    @XxlJob(JobHandlerName.Timed.DocExchangeExpireMsgTask)
    @WellXxlJob(jobDesc = "公文交换到期消息任务",
            jobCron = "0/30 * * * * ?", triggerStatus = true)
    public ReturnT<String> docExchangeExpireMsgTask(String param) throws Exception {
        XxlJobLogger.log("execute start");
        try {
            DmsDocExchangeExpireService dmsDocExchangeExpireService = ApplicationContextHolder.getBean(DmsDocExchangeExpireService.class);
            dmsDocExchangeExpireService.taskJob();
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    /**
     * 每10秒执行一次
     */
    @XxlJob(JobHandlerName.Timed.SendMailJob)
    @WellXxlJob(jobDesc = "邮件异步发送",
            jobCron = "0/10 * * * * ?", triggerStatus = true)
    public ReturnT<String> SendMailJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        WmMailboxInfoUserService wmMailboxInfoUserService = ApplicationContextHolder.getBean(WmMailboxInfoUserService.class);
        Date now = new Date();
        Map<String, Object> params = new HashMap<>();
        params.put("mailboxName", WmWebmailConstants.OUTBOX);
//        旧状态 （0：待解析，1：已发送，2：解析失败，3,实际邮件发送失败）
//        发送状态（0：待解析，1：发送成功（全部发送成功）2：解析失败（具体报错原因存入failMsg），3：发送失败（全部失败，或部分失败），4：解析成功（投递中））
//        params.put("sendStatus", Lists.newArrayList(0,3));
        params.put("sendStatus", Lists.newArrayList(0, 4));
        params.put("now", now);
        params.put("sendCount", 10);
        List<QueryItem> wmMailboxList = wmMailboxInfoUserService.listQueryItemByHQL("select uuid as uuid from WmMailboxInfoUser where mailboxName=:mailboxName and sendStatus in (:sendStatus) and nextSendTime<=:now and sendCount<=:sendCount and revokeStatus is null ", params, null);
        for (QueryItem queryItem : wmMailboxList) {
            String uuid = queryItem.getString("uuid");
            wmMailboxInfoUserService.syncSendMail(uuid);
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }


    @XxlJob(JobHandlerName.Timed.ApiRetryCommandJob)
    @WellXxlJob(jobDesc = "api重试失败的指令",
            jobCron = "0 * * * * ?", triggerStatus = false)
    public ReturnT<String> apiRetryCommandJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ApiCommandService apiCommandService = ApplicationContextHolder.getBean(
                ApiCommandService.class);
        List<ApiCommandEntity> commandEntityList = apiCommandService.listByGreaterThanRetryTime(
                new Date());
        for (ApiCommandEntity commandEntity : commandEntityList) {
            try {
                IgnoreLoginUtils.login(Config.DEFAULT_TENANT, commandEntity.getCreator());
                apiCommandService.retryCommand(commandEntity.getUuid());
            } catch (Exception e) {
                XxlJobLogger.log("重试失败的指令异常，指令uuid=[{}]", commandEntity.getUuid(), e);
            } finally {
                IgnoreLoginUtils.logout();
            }

        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    @XxlJob(JobHandlerName.Timed.CleanUpAppTempDataJob)
    @WellXxlJob(jobDesc = "清理应用临时文件任务",
            jobCron = "0 0 0 * * ? *",
            executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = false)
    public ReturnT<String> cleanUpAppTempDataJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            CleanUpAppTempDataJob.execute();
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    @XxlJob(JobHandlerName.Timed.DiExecuteJob)
    @WellXxlJob(jobDesc = "数据交换路由执行任务",
            jobCron = "0 0 0 * * ? *", triggerStatus = false)
    public ReturnT<String> diExecuteJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        //获取所有挂载该定时任务的数据交换配置
        DiConfigService configService = ApplicationContextHolder.getBean(DiConfigService.class);
        /**
         * TODO: JobUuid 需改为 DiExecuteJob
         * @see com.wellsoft.pt.di.facade.service.impl.DiConfigFacadeServiceImpl#diJobSelections(Select2QueryInfo)
         */
        List<DiConfigEntity> configEntityList = configService.listByJobUuid(JobHandlerName.Timed.DiExecuteJob);
        for (DiConfigEntity entity : configEntityList) {
            try {
                if (CamelContextUtils.context().getRouteStatus(entity.getUuid()).isStarting()) {
                    continue;
                }
                CamelContextUtils.context().startRoute(entity.getUuid());//启动路由，路由执行完毕，会再次挂起路由
            } catch (Exception e) {
                XxlJobLogger.log("数据交换定时任务启动路由[]失败：", entity.getUuid(), e);
            }
        }

        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    @XxlJob(JobHandlerName.Timed.DingtalkEventCallBackJob)
    @WellXxlJob(jobDesc = "定时钉钉事件回调任务",
            jobCron = "50 * * * * ?",
            executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = false)
    public ReturnT<String> dingtalkEventCallBackJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            DingtalkOrgSyncApi api = ApplicationContextHolder.getBean(DingtalkOrgSyncApi.class);
            api.syncFailedList();// 同步失败列表
            api.executeCallBack();// 执行回调事件
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    @XxlJob(JobHandlerName.Timed.DingtalkOrgSyncJob)
    @WellXxlJob(jobDesc = "定时钉钉同步组织任务",
            jobCron = "50 * * * * ?",
            executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = false)
    public ReturnT<String> dingtalkOrgSyncJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            ApplicationContextHolder.getBean(DingtalkOrgSyncApi.class).syncOrgFromDingtalk();
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    @XxlJob(JobHandlerName.Timed.DingtalkWorkRecordJob)
    @WellXxlJob(jobDesc = "钉钉-待办事项重新发送",
            jobCron = "0/3 * * * * ?",
            executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = false)
    public ReturnT<String> dingtalkWorkRecordJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            DingtalkApiUtils.executeWorkRecord();
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    @XxlJob(JobHandlerName.Timed.DmsDocExchangeOvertimeCalculateJob)
    @WellXxlJob(jobDesc = "文档交换紧要性计算",
            jobCron = "30 * * * * ?", triggerStatus = false)
    public ReturnT<String> dmsDocExchangeOvertimeCalculateJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        DmsDocExchangeRecordService dmsDocExchangeRecordService = ApplicationContextHolder.getBean(
                DmsDocExchangeRecordService.class);
        List<String> uuids = dmsDocExchangeRecordService.listUuidsByStatus(
                Lists.newArrayList(DocExchangeRecordStatusEnum.WAIT_SIGN.ordinal(),
                        DocExchangeRecordStatusEnum.SIGNED.ordinal(),
                        DocExchangeRecordStatusEnum.WAI_FEEDBACK.ordinal()));
        for (String uuid : uuids) {
            dmsDocExchangeRecordService.updateOvertimeLevelByUuid(uuid);
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }


    @XxlJob(JobHandlerName.Timed.GarbageCollectorStartUp)
    @WellXxlJob(jobDesc = "mongo垃圾文件收集器",
            jobCron = "0 0 23 * * ?",
            executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = false)
    public ReturnT<String> garbageCollectorStartUp(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            GarbageCollectorStartUp.doExecute();
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    @XxlJob(JobHandlerName.Timed.Message2BackupJob)
    @WellXxlJob(jobDesc = "在线消息备份",
            jobCron = "0 0/1 * * * ?",
            executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = false)
    public ReturnT<String> message2BackupJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            MessageBackupService backupService = ApplicationContextHolder.getBean(MessageBackupService.class);
            int backup_during = -1;// 备份之前一个月
            backupService.BackupMessage(backup_during);
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    @XxlJob(JobHandlerName.Timed.MessageQueueJob)
    @WellXxlJob(jobDesc = "消息队列发送",
            jobCron = "0/10 * * * * ?")
    public ReturnT<String> messageQueueJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        JmsMessageConsumer jmsMessageConsumer = ApplicationContextHolder.getBean(JmsMessageConsumer.class);
        jmsMessageConsumer.receiveMessage();
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    @XxlJob(JobHandlerName.Timed.TaskDelegationCleanUpJob)
    @WellXxlJob(jobDesc = "工作委托到期数据清理",
            jobCron = "30 * * * * ?")
    public ReturnT<String> taskDelegationCleanUpJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        // 清理过期的委托配置
        FlowDelegationSettingsService flowDelegationSettingsService = ApplicationContextHolder
                .getBean(FlowDelegationSettingsService.class);
        List<FlowDelegationSettings> overDueSettings = flowDelegationSettingsService.getOverdueSettings();
        for (FlowDelegationSettings delegationSettings : overDueSettings) {
            delegationSettings.setStatus(FlowDelegationSettings.STATUS_DEACTIVE);
            flowDelegationSettingsService.save(delegationSettings);
        }
        // 清理过期的工作委托
        TaskDelegationTakeBackService takeBackService = ApplicationContextHolder
                .getBean(TaskDelegationTakeBackService.class);
        TaskDelegationService taskDelegationService = ApplicationContextHolder.getBean(TaskDelegationService.class);
        List<TaskDelegation> taskDelegations = taskDelegationService.getOverdueTaskDelegation();
        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        if (StringUtils.isBlank(tenantId)) {
            tenantId = Config.DEFAULT_TENANT;
        }
        for (TaskDelegation taskDelegation : taskDelegations) {
            String userId = taskDelegation.getConsignor();
            String taskDelegationUuid = taskDelegation.getUuid();
            try {
                IgnoreLoginUtils.login(tenantId, userId);
                FlowDelegationSettings delegationSettings = flowDelegationSettingsService.getOne(taskDelegation.getDelegationSettingsUuid());
                RequestSystemContextPathResolver.setSystem(delegationSettings.getSystem());
                takeBackService.takeBack(taskDelegationUuid);
            } catch (Exception e) {
                XxlJobLogger.log(e.getMessage(), e);
            } finally {
                RequestSystemContextPathResolver.clear();
                IgnoreLoginUtils.logout();
            }
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

//    @XxlJob(JobHandlerName.Timed.WebmailCapacityCalculateJob)
//    @WellXxlJob(jobDesc = "平台邮件用户使用空间初始化计算",
//            jobCron = "0/5 * * * * ?",
//            executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = false)
//    public ReturnT<String> webmailCapacityCalculateJob(String param) throws Exception {
//        XxlJobLogger.log("execute start");
//        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
//        try {
//            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
//            WmMailUserService wmMailUserService = ApplicationContextHolder.getBean(
//                    WmMailUserService.class);
//            List<WmMailUserEntity> userEntityList = wmMailUserService.listAll();
//            WmMailUseCapacityService wmMailUseCapacityService = ApplicationContextHolder.getBean(
//                    WmMailUseCapacityService.class);
//            for (WmMailUserEntity userEntity : userEntityList) {
//                if (userEntity.getIsInnerUser()) {
//                    try {
//                        wmMailUseCapacityService.saveUserUseCapacityInitial(userEntity.getUserId(),
//                                true);
//                    } catch (Exception e) {
//                        XxlJobLogger.log("计算用户邮件使用空间异常：", e);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            XxlJobLogger.log(e);
//            throw e;
//        } finally {
//            IgnoreLoginUtils.logout();
//        }
//        XxlJobLogger.log("execute end");
//        return ReturnT.SUCCESS;
//    }


    @XxlJob(JobHandlerName.Timed.WebmailCapacityLimitWarnJob)
    @WellXxlJob(jobDesc = "平台邮件用户剩余空间不足提醒",
            jobCron = "0 0 23 * * ?",
            executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = false)
    public ReturnT<String> webmailCapacityLimitWarnJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            WmMailUserService wmMailUserService = ApplicationContextHolder.getBean(
                    WmMailUserService.class);
            List<WmMailUserEntity> userEntityList = wmMailUserService.listAll();
            Map<String, WmMailConfigEntity> userMailConfigs = Maps.newHashMap();
            WmMailConfigService wmMailConfigService = ApplicationContextHolder.getBean(
                    WmMailConfigService.class);
            MessageClientApiFacade messageClientApiFacade = ApplicationContextHolder.getBean(
                    MessageClientApiFacade.class);

            for (WmMailUserEntity userEntity : userEntityList) {
                if (userEntity.getIsInnerUser()) {
                    try {
                        WmMailConfigEntity configEntity = userMailConfigs.get(
                                userEntity.getSystemUnitId());
                        if (configEntity == null) {
                            configEntity = wmMailConfigService.getBySystemUnitId(
                                    userEntity.getSystemUnitId());
                            userMailConfigs.put(userEntity.getSystemUnitId(), configEntity);
                        }
                        if (userEntity.getLimitCapacity() != null && configEntity.getDeadlineCapacity() != null
                                //邮箱占用空间达到一定百分比的情况下发送消息提醒
                                && new BigDecimal(
                                (/*userEntity.getLimitCapacity() * 1024 * 1024 -*/ userEntity.getUsedCapacity()) /
                                        (userEntity.getLimitCapacity() * 1024 * 1024.00d)).doubleValue() >= (configEntity.getDeadlineCapacity() / 100.00d)) {
                            userEntity.setUsedCapacityReadable(
                                    WebmailUseCapacityDataStoreRender.mailCapacityReadableFormate(
                                            userEntity.getUsedCapacity()));
                            userEntity.setLimitCapacityReadable(
                                    WebmailUseCapacityDataStoreRender.mailCapacityReadableFormate(
                                            (long) userEntity.getLimitCapacity()));
                            userEntity.setRemainCapacityReadable(
                                    WebmailUseCapacityDataStoreRender.mailCapacityReadableFormate(
                                            userEntity.getLimitCapacity() - userEntity.getUsedCapacity()));
                            //有容量限制且剩余空间不足的情况下，提醒用户
                            messageClientApiFacade.send("WEBMAIL_USE_CAPACITY_LIMIT_WARN", userEntity,
                                    Lists.newArrayList(userEntity.getUserId()));
                        }
                    } catch (Exception e) {
                        XxlJobLogger.log("检测用户邮件剩余空间异常：", e);
                    }
                }
            }
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }


    @XxlJob(JobHandlerName.Timed.TaskTimerCleanUpJob)
    @WellXxlJob(jobDesc = "工作流程_计时系统_清理过期的定时器数据",
            jobCron = "0/10 * * * * ?",
            executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}")
    public ReturnT<String> taskTimerCleanUpJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            TaskTimerCleanUpJob.execute();
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    @XxlJob(JobHandlerName.Timed.UploadFileFromSftp2MongoJob)
    @WellXxlJob(jobDesc = "文件服务_从SFTP下载文件到MongoDB",
            jobCron = "0 0/1 * * * ?",
            executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}")
    public ReturnT<String> uploadFileFromSftp2MongoJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            UploadFileFromSftp2MongoJob.execute();
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
