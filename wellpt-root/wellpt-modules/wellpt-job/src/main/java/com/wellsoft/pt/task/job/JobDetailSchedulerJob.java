/*
 * @(#)Apr 26, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.job;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.task.dto.JobFiredDetailsDTO;
import com.wellsoft.pt.task.entity.JobDetails;
import com.wellsoft.pt.task.service.JobDetailsService;
import com.wellsoft.pt.task.service.JobFiredDetailsService;
import com.wellsoft.pt.task.support.JobDetailsAutoSchedulingChecker;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.PatternMatchUtils;

import java.util.List;
import java.util.Map;

/**
 * Description: JobDetail调度任务，系统守护任务
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Apr 26, 2018.1	zhulh		Apr 26, 2018		Create
 * </pre>
 * @date Apr 26, 2018
 */
@Service
public class JobDetailSchedulerJob {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private Scheduler scheduler;


    @Autowired
    private JobDetailsService jobDetailsService;

    @Autowired
    private JobDetailResolver jobDetailResolver;

    @Autowired
    private JobFiredDetailsService jobFiredDetailsService;

    @Value("${org.quartz.shceduler.jobDetail.replace:true}")
    private String jobDetailReplace;

    private Map<String, JobDetailsAutoSchedulingChecker> autoSchedulingCheckerMap = Maps.newHashMap();

    private static boolean ipMatch(String ip, String ipMatch) {
        if (ip.indexOf(",") != -1) {
            String[] ipParts = ip.split(",");
            for (String i : ipParts) {
                if (ipMatch(i, ipMatch)) {
                    return true;
                }
            }
        }

        if (ip.indexOf("-") != -1) { // 192.168.0.100-112 端口范围
            String[] ipParts = ip.split("-");
            if (ipParts.length == 2) {
                int sign = ipParts[0].lastIndexOf(".");
                String head = ipParts[0].substring(0, sign);
                int beginPort = Integer.parseInt(ipParts[0].substring(sign + 1));
                int endPort = Integer.parseInt(ipParts[1]);
                for (int i = beginPort; i <= endPort; i++) {
                    if (ipMatch(head + "." + i, ipMatch)) {
                        return true;
                    }
                }
            }
        } else if (ip.indexOf(":") != -1) {//限定端口匹配
            //ip、端口匹配
            String[] ipports = ip.split(":");
            String[] matchs = ipMatch.split(":");
            if (ipports.length == 2) {//限制端口号的匹配
                return ipMatch(ipports[0], matchs[0]) && ipMatch(ipports[1], matchs[1]);
            }
        } else if (ip.indexOf("*") != -1) {
            return PatternMatchUtils.simpleMatch(ip, ipMatch);
        } else {
            //无限定端口的情况下
            String[] matchs = ipMatch.split(":");
            if (ip.indexOf("*") != -1) {
                return PatternMatchUtils.simpleMatch(ip, matchs[0]);
            }
            return ip.equals(matchs[0]);

        }
        return ip.equals(ipMatch);
    }

    public void execute() {
        // 1、任务调度
        List<JobFiredDetailsDTO> jobFiredDetailsDTOs = jobFiredDetailsService.getAll();
        boolean execute = false;
        for (JobFiredDetailsDTO jobFiredDetailsDTO : jobFiredDetailsDTOs) {
            Integer result = 1;
            String resultMsg = "任务调度成功！";
            try {
                IgnoreLoginUtils.login(Config.DEFAULT_TENANT, jobFiredDetailsDTO.getCreator());
                execute = parseAndExecuteJobDetail(jobFiredDetailsDTO);
            } catch (Exception e) {
                result = 0;
                resultMsg = e.getMessage();
                logger.error(resultMsg, e);
            } finally {
                if (execute) {
                    jobFiredDetailsService.moveToHistory(jobFiredDetailsDTO.getUuid(), result,
                            resultMsg);
                }
                IgnoreLoginUtils.logout();
            }
        }

        // 2、自动调度
        List<JobDetails> autoSchedulings = jobDetailsService.listAutoScheduling();
        for (JobDetails jobDetails : autoSchedulings) {
            try {
                JobDetailsAutoSchedulingChecker checker = getJobDetailsAutoSchedulingChecker(
                        jobDetails);
                checker.autoSchedulingIfRequire();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * @param jobDetails
     * @return
     */
    private JobDetailsAutoSchedulingChecker getJobDetailsAutoSchedulingChecker(
            JobDetails jobDetails) {
        String key = jobDetails.getUuid();
        JobDetailsAutoSchedulingChecker autoSchedulingChecker = autoSchedulingCheckerMap.get(key);
        if (autoSchedulingChecker == null) {
            autoSchedulingChecker = new JobDetailsAutoSchedulingChecker(jobDetails.getUuid(),
                    jobDetails.getName(),
                    jobDetails.getCreator(), jobDetails.getTenantId());
            autoSchedulingCheckerMap.put(key, autoSchedulingChecker);
        }
        return autoSchedulingChecker;
    }

    /**
     * 如何描述该方法
     *
     * @param jobFiredDetailsDTO
     */
    public boolean parseAndExecuteJobDetail(JobFiredDetailsDTO jobFiredDetailsDTO) {
        Integer firedType = jobFiredDetailsDTO.getFiredType();
        JobDetail jobDetail = jobFiredDetailsDTO.getJobDetail();
        try {
            if (!this.allowIpFireJob(jobDetail)) {
                return false;
            }
            // 并发占用
            int row = jobFiredDetailsService.updateOccupied(jobFiredDetailsDTO.getUuid());
            if (row == 0) {
                return true;
            }
        } catch (Exception e) {
            logger.error("判断该节点是否可发起任务异常：", e);
            return false;
        }
        switch (firedType) {
            case 1:
                start(jobDetail);
                break;
            case 2:
                pause(jobDetail);
                break;
            case 3:
                resume(jobDetail);
                break;
            case 4:
                restart(jobDetail);
                break;
            case 5:
                stop(jobDetail);
                break;
            case 6:
                delete(jobDetail);
                break;
            default:
                break;
        }

        return true;
    }

    private boolean allowIpFireJob(JobDetail jobDetail) throws Exception {

        if (jobDetail != null) {
            String assignIp = jobDetail.getAssignIp();
            if (StringUtils.isBlank(assignIp)) {
                return true;
            }
            String[] idParts = scheduler.getSchedulerInstanceId().split("/");
            if (idParts.length == 3) {
                String address = idParts[1];
                if (StringUtils.isNotBlank(assignIp)) {
                    if (!ipMatch(assignIp, address)) {
                        logger.warn("任务=[{}]，指定IP=[{}]运行，与本机IP=[{}]不匹配，无法使用使用该节点发起任务",
                                new Object[]{jobDetail.getName(),
                                        assignIp, address});
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 如何描述该方法
     *
     * @param jobDetail
     */
    private void start(JobDetail jobDetail) {
        try {
            jobDetail.setSchedulerName(scheduler.getSchedulerName());
            JobInfo jobInfo = jobDetailResolver.resolve(jobDetail);
            try {
                scheduler.deleteJob(jobInfo.getJobDetail().getKey());//先删除job
            } catch (Exception e) {
                logger.warn("无法删除定时任务[{}]", jobInfo.getJobDetail().getKey().toString());
            }
            scheduler.scheduleJob(jobInfo.getJobDetail(), Sets.newHashSet(jobInfo.getTrigger()),
                    "true".equalsIgnoreCase(jobDetailReplace));
        } catch (SchedulerException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param jobDetail
     */
    private void pause(JobDetail jobDetail) {
        String jobName = jobDetail.getName();
        String groupName = jobDetail.getTenantId();
        try {
            scheduler.pauseJob(JobKey.jobKey(jobName, groupName));
        } catch (SchedulerException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param jobDetail
     */
    private void resume(JobDetail jobDetail) {
        String jobName = jobDetail.getName();
        String groupName = jobDetail.getTenantId();
        try {
            scheduler.resumeJob(JobKey.jobKey(jobName, groupName));
        } catch (SchedulerException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param jobDetail
     */
    private void restart(JobDetail jobDetail) {
        try {
            jobDetail.setSchedulerName(scheduler.getSchedulerName());
            JobInfo jobInfo = jobDetailResolver.resolve(jobDetail);
            try {
                scheduler.deleteJob(jobInfo.getJobDetail().getKey());//先删除job
            } catch (Exception e) {
                logger.warn("无法删除定时任务[{}]", jobInfo.getJobDetail().getKey().toString());
            }
            scheduler.rescheduleJob(jobInfo.getTrigger().getKey(), jobInfo.getTrigger());
        } catch (SchedulerException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param jobDetail
     */
    private void stop(JobDetail jobDetail) {
        String jobName = jobDetail.getName();
        String groupName = jobDetail.getTenantId();
        try {
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, groupName));
        } catch (SchedulerException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param jobDetail
     */
    private void delete(JobDetail jobDetail) {
        String jobName = jobDetail.getName();
        String groupName = jobDetail.getTenantId();
        try {
            scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
        } catch (SchedulerException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断任务是否存在任务
     *
     * @param jobDetail
     * @return
     */
    public boolean isExists(JobDetail jobDetail) {
        boolean result = false;
        try {
            String jobName = jobDetail.getName();
            String groupName = jobDetail.getTenantId();
            result = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName)) != null;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
        return result;
    }

}
