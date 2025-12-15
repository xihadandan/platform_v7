/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.job;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.log.entity.UserOperationLog;
import com.wellsoft.pt.log.service.UserOperationLogService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 系统任务接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-29.1	zhulh		2012-10-29		Create
 * </pre>
 * @date 2012-10-29
 */
@Deprecated
public abstract class Job implements org.quartz.Job {
    private static final ThreadLocal<Map<String, Integer>> refireHolder = new ThreadLocal<Map<String, Integer>>();
    protected static Logger logger = LoggerFactory.getLogger(Job.class);
    private static List<String> jobIds = new ArrayList<String>();
    private static Map<Class<?>, Object> lockedMap = new HashMap<Class<?>, Object>();

    /**
     * @param jobData
     * @param jobId
     * @param e
     */
    private static void log(JobData jobData, String jobId, Exception e) {
        UserOperationLog log = new UserOperationLog();
        log.setModuleId(ModuleID.INFO_SHARED.getValue());
        log.setModuleName("任务错误日志");
        log.setContent(jobId);
        log.setOperation("执行任务");
        log.setUserName(jobData.getUserId());
        log.setDetails(e.getMessage());
        UserOperationLogService userOperationLogService = ApplicationContextHolder
                .getBean(UserOperationLogService.class);
        userOperationLogService.save(log);
    }

    /**
     * 任务是否异步执行
     *
     * @param data
     */
    protected boolean isAsync() {
        return false;
    }

    /**
     * 任务调度未完成时，是否忽略新任务
     *
     * @param data
     */
    protected boolean ignoreWhileExecuting() {
        return true;
    }

    /**
     * 任务是否自动调度
     *
     * @return
     */
    public boolean isAutoScheduling() {
        return false;
    }

    /**
     * 执行任务
     *
     * @param data
     */
    protected abstract void execute(JobExecutionContext context, JobData data);

    /**
     * 获取JobDetail，只填充属性值name、jobClass、tenantId
     *
     * @param context
     * @return
     */
    protected JobDetail getJobDetail(JobExecutionContext context) {
        org.quartz.JobDetail detail = context.getJobDetail();
        JobDetail jobDetail = new JobDetail();
        jobDetail.setName(detail.getKey().getName());
        jobDetail.setJobClass(detail.getJobClass());
        jobDetail.setTenantId(detail.getKey().getGroup());
        return jobDetail;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap map = context.getMergedJobDataMap();
        JobData jobData = null;
        if (map instanceof JobData) {
            jobData = (JobData) map;
        } else {
            jobData = new JobData();
            jobData.putAll(map);
        }

        String jobType = jobData.getType();
        // 任务运行中忽略掉
        String jobId = context.getJobDetail().getKey().getName() + "_" + this.getClass().getCanonicalName();
        if (ignoreWhileExecuting() && jobIds.contains(jobId)) {
            logger.error("任务[" + jobId + "]任务运行中忽略掉");
            return;
        }

        if (StringUtils.isBlank(jobData.getTenantId())) {
            logger.warn(
                    "任务[" + context.getJobDetail().getKey().getName() + "]没有指定租户ID，无法断续执行，当执行后台任务时必须指定租户ID！");
        } else {
            try {
                logger.info(
                        "开始执行任务[" + context.getJobDetail().getKey().getName() + "]，租户ID[" + jobData.getTenantId()
                                + "]，用户ID[" + jobData.getUserId() + "]");
                jobIds.add(jobId);
                IgnoreLoginUtils.login(jobData.getTenantId(), jobData.getUserId());
                if (isAsync()) {
                    execute(context, jobData);
                } else {
                    Object locked = lockedMap.get(this.getClass());
                    if (locked == null) {
                        locked = new Object();
                        lockedMap.put(this.getClass(), locked);
                    }
                    synchronized (locked) {
                        execute(context, jobData);
                    }
                }
                logger.info(
                        "结束执行任务[" + context.getJobDetail().getKey().getName() + "]，租户ID[" + jobData.getTenantId()
                                + "]，用户ID[" + jobData.getUserId() + "]");
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
                // 临时任务重新执行，最大重试50次
                // if (JobDetail.TYPE_TEMPORARY.equals(jobType)) {
                // Map<String, Integer> refireMap = refireHolder.get();
                // if (refireMap == null) {
                // refireMap = new HashMap<String, Integer>();
                // refireMap.put(jobId, 0);
                // refireHolder.set(refireMap);
                // }
                // Integer refireTimes = refireMap.get(jobId);
                // if (refireTimes != null && refireTimes < 50) {
                // // 记录错误日志
                log(jobData, jobId, e);
                //
                // // 暂停1秒
                // try {
                // Thread.sleep(1000);
                // } catch (InterruptedException e1) {
                // logger.error(ExceptionUtils.getStackTrace(e1));
                // }
                //
                // refireTimes++;
                // refireMap.put(jobId, refireTimes);
                // JobExecutionException ex = new JobExecutionException(e);
                // ex.setRefireImmediately(true);
                // throw ex;
                // } else {
                // refireHolder.remove();
                // }
                // }
            } finally {
                jobIds.remove(jobId);
                IgnoreLoginUtils.logout();
            }
        }
    }


}
