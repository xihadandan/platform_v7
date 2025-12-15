package com.wellsoft.pt.xxljob.service;

/**
 * JobHandlerName 集中管理
 */
public interface JobHandlerName {

    /**
     * 临时任务
     */
    interface Temp {

        String DutyAgentActiveStatusTraceJob = "DutyAgentActiveStatusTraceJob";
        String TaskOverDueSendRepeatMessageJob = "TaskOverDueSendRepeatMessageJob";
        String TaskAlarmSendRepeatMessageJob = "TaskAlarmSendRepeatMessageJob";
        String TaskDelegationCurrentWorkDueHandlerJob = "TaskDelegationCurrentWorkDueHandlerJob";
        String WorkHandoverHandlerJob = "WorkHandoverHandlerJob";
    }

    /**
     * 定时任务
     */
    interface Timed {

        String TestXxlJob = "TestXxlJob";

        String SerialNumberDataCleaningTask = "SerialNumberDataCleaningTask";
        String DocExchangeExpireMsgTask = "DocExchangeExpireMsgTask";
        String ApiRetryCommandJob = "ApiRetryCommandJob";// 迁移
        String CleanUpAppTempDataJob = "CleanUpAppTempDataJob";// 迁移
        String DefaultWebmailClientSyncInboxMailJob = "DefaultWebmailClientSyncInboxMailJob";
        String SendMailJob = "SendMailJob";
        String DiExecuteJob = "DiExecuteJob";// 迁移
        String DingtalkEventCallBackJob = "DingtalkEventCallBackJob";// 迁移
        String DingtalkOrgSyncJob = "DingtalkOrgSyncJob";// 迁移
        String DingtalkWorkRecordJob = "DingtalkWorkRecordJob";// 迁移
        String DmsDocExchangeOvertimeCalculateJob = "DmsDocExchangeOvertimeCalculateJob"; // 迁移
        String GarbageCollectorStartUp = "GarbageCollectorStartUp";// 迁移
        String Message2BackupJob = "Message2BackupJob";// 迁移
        String MessageQueueJob = "MessageQueueJob";// 迁移
        String TaskDelegationCleanUpJob = "TaskDelegationCleanUpJob";// 迁移
        String TaskTimerCleanUpJob = "TaskTimerCleanUpJob";// 迁移
        String WebmailCapacityCalculateJob = "WebmailCapacityCalculateJob";// 迁移
        String WebmailCapacityLimitWarnJob = "WebmailCapacityLimitWarnJob";// 迁移
        String UploadFileFromSftp2MongoJob = "UploadFileFromSftp2MongoJob";

        String PwdValidityWarnJob = "PwdValidityWarnJob";
        String PwdUnlockJob = "PwdUnlockJob";


        // 组织任务
        String OrgVersionSchedulePublish = "OrgVersionSchedulePublish";// 组织版本按时间发布
        String BizOrgExpiredJob = "BizOrgExpiredJob";// 组织版本按时间发布

        /**
         删除
         String TaskDelegationDueHandlerJob = "TaskDelegationDueHandlerJob";
         String ElasticSearchLogIndicesDropJob = "ElasticSearchLogIndicesDropJob";
         String ExchangeDataRepeatTask = "ExchangeDataRepeatTask";
         String GuangDunFeedBackJob = "GuangDunFeedBackJob";
         String GuangDunRepeatJob = "GuangDunRepeatJob";
         String GuangDunRestoreDataJob = "GuangDunRestoreDataJob";
         String GuangDunSendDataJob = "GuangDunSendDataJob";
         String InitJobDetailJob = "InitJobDetailJob";
         String James3MailClientFetchJob = "James3MailClientFetchJob";
         String James3WebmailClientFetchMailJob = "James3WebmailClientFetchMailJob";
         String JobMasJob = "JobMasJob"; //job.MasJob
         String SmsMasJob = "SmsMasJob"; //sms.MasJob
         String OpenMailServerFetchMailJob = "OpenMailServerFetchMailJob";
         String QrtzFiredTriggerHisClearJob = "QrtzFiredTriggerHisClearJob";
         String ScheduleMessageQueueJob = "ScheduleMessageQueueJob";
         String SynchronousClearTask = "SynchronousClearTask";
         String SynchronousDelDbTask = "SynchronousDelDbTask";
         String SynchronousInTask = "SynchronousInTask";
         String SynchronousNewInTask = "SynchronousNewInTask";
         String SynchronousNewOutTask = "SynchronousNewOutTask";
         String SynchronousOutTask = "SynchronousOutTask";
         String SynchronousSQLDCommonTask = "SynchronousSQLDCommonTask";
         String SynchronousSQLDJMOutTask = "SynchronousSQLDJMOutTask";
         String SynchronousSQLDJMTriggerOutTask = "SynchronousSQLDJMTriggerOutTask";
         String SynchronousSQLDOutTask = "SynchronousSQLDOutTask";
         String SynchronousStaticsTask = "SynchronousStaticsTask";
         String SynchronousThreeACLBackTask = "SynchronousThreeACLBackTask";
         String SynchronousThreeACLInTask = "SynchronousThreeACLInTask";
         String SynchronousThreeACLOutTask = "SynchronousThreeACLOutTask";
         String SynchronousThreeBackTask = "SynchronousThreeBackTask";
         String SynchronousThreeBackWithClobTask = "SynchronousThreeBackWithClobTask";
         String SynchronousThreeFLOWBackTask = "SynchronousThreeFLOWBackTask";
         String SynchronousThreeFLOWInTask = "SynchronousThreeFLOWInTask";
         String SynchronousThreeFLOWOutTask = "SynchronousThreeFLOWOutTask";
         String SynchronousThreeFileBackTask = "SynchronousThreeFileBackTask";
         String SynchronousThreeFileInTask = "SynchronousThreeFileInTask";
         String SynchronousThreeFileOutTask = "SynchronousThreeFileOutTask";
         String SynchronousThreeInBackTask = "SynchronousThreeInBackTask";
         String SynchronousThreeInTask = "SynchronousThreeInTask";
         String SynchronousThreeInWithClobTask = "SynchronousThreeInWithClobTask";
         String SynchronousThreeMSGBackTask = "SynchronousThreeMSGBackTask";
         String SynchronousThreeMSGInTask = "SynchronousThreeMSGInTask";
         String SynchronousThreeMSGOutTask = "SynchronousThreeMSGOutTask";
         String SynchronousThreeOutBackTask = "SynchronousThreeOutBackTask";
         String SynchronousThreeOutTask = "SynchronousThreeOutTask";
         String SynchronousThreeOutWithClobTask = "SynchronousThreeOutWithClobTask";

         */

    }
}
