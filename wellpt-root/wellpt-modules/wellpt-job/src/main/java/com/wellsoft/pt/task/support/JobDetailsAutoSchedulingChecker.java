/*
 * @(#)2018年11月13日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.task.service.JobDetailsService;
import com.wellsoft.pt.task.service.QrtzTriggersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年11月13日.1	zhulh		2018年11月13日		Create
 * </pre>
 * @date 2018年11月13日
 */
public class JobDetailsAutoSchedulingChecker {

    // 最小检查时间间隔，默认5分钟
    private static final int MIN_REPEAT_INTERVAL = 5;
    // 最大检查时间间隔，一天至少检测一次
    private static final int MAX_REPEAT_INTERVAL = 1440;
    private Logger logger = LoggerFactory.getLogger(JobDetailsAutoSchedulingChecker.class);
    // 任务配置UUID
    private String jobDetailsUuid;
    // triggerName
    private String triggerName;
    // triggerGroup
    private String triggerGroup;
    // 任务创建者
    private String userId;
    // 所在租户
    private String tenantId;
    // 下次检查时间
    private Date nextCheckTime = Calendar.getInstance().getTime();
    // 连续运行检测次数
    private int continuousRunningCheckCount = 0;

    /**
     * @param jobDetailsUuid
     * @param userId
     * @param tenantId
     */
    public JobDetailsAutoSchedulingChecker(String jobDetailsUuid, String triggerName, String userId, String tenantId) {
        this.jobDetailsUuid = jobDetailsUuid;
        this.triggerName = triggerName;
        this.triggerGroup = tenantId;
        this.userId = userId;
        this.tenantId = tenantId;
    }

    /**
     * @return the jobDetailsUuid
     */
    public String getJobDetailsUuid() {
        return jobDetailsUuid;
    }

    /**
     *
     */
    public void autoSchedulingIfRequire() {
        Date currentTime = Calendar.getInstance().getTime();
        QrtzTriggersService qrtzTriggersService = ApplicationContextHolder.getBean(QrtzTriggersService.class);
        // 下一次检查时间
        if (currentTime.after(this.nextCheckTime)) {
            // 任务已运行，更新下次检测时间
            if (qrtzTriggersService.isExists(triggerName, triggerGroup)) {
                increaseNextCheckTime(MIN_REPEAT_INTERVAL);
            } else {
                // 如果任务没运行，调度任务
                try {
                    IgnoreLoginUtils.login(tenantId, userId);
                    schedulingIfRequire();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    IgnoreLoginUtils.logout();
                }
            }
        }
    }

    /**
     *
     */
    public void schedulingIfRequire() {
        JobDetailsService jobDetailsService = ApplicationContextHolder.getBean(JobDetailsService.class);
        // 如果任务在运行，延长下次检测时间，一天至少检测一次
        if (jobDetailsService.isRunning(jobDetailsUuid)) {
            continuousRunningCheckCount++;
        } else {
            continuousRunningCheckCount = 0;
            jobDetailsService.start(jobDetailsUuid);
        }

        int nextCheckMinute = MIN_REPEAT_INTERVAL;
        nextCheckMinute += continuousRunningCheckCount * continuousRunningCheckCount;
        if (nextCheckMinute > MAX_REPEAT_INTERVAL) {
            nextCheckMinute = MAX_REPEAT_INTERVAL;
        }

        increaseNextCheckTime(nextCheckMinute);
    }

    /**
     * @param nextCheckMinute
     */
    private void increaseNextCheckTime(int nextCheckMinute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, nextCheckMinute);
        nextCheckTime = calendar.getTime();
    }

}
