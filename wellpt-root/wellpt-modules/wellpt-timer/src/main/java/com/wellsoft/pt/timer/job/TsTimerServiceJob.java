/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.job;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.Entity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.timer.entity.TsTimerAlarmEntity;
import com.wellsoft.pt.timer.entity.TsTimerEntity;
import com.wellsoft.pt.timer.enums.EnumTimerStatus;
import com.wellsoft.pt.timer.facade.service.TsTimerFacadeService;
import com.wellsoft.pt.timer.service.TsTimerAlarmService;
import com.wellsoft.pt.timer.service.TsTimerConfigService;
import com.wellsoft.pt.timer.service.TsTimerService;
import com.wellsoft.pt.timer.support.TsTimerParam;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.utils.ParamUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.well.annotation.WellXxlJob;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Description: 计时服务定义任务
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年4月9日.1	zhulh		2021年4月9日		Create
 * </pre>
 * @date 2021年4月9日
 */
@Component
public class TsTimerServiceJob {

    protected static Map<String, String> scheduleMap = Maps.newHashMap();
    private static Logger LOG = LoggerFactory.getLogger(TsTimerServiceJob.class);
    private volatile static Object locked = new Object();
    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private TsTimerService timerService;

    @Autowired
    private TsTimerConfigService timerConfigService;

    @Autowired
    private TsTimerAlarmService timerAlarmService;

    @Autowired
    private TsTimerFacadeService timerFacadeService;

    @XxlJob("tsTimerServiceJob")
    @WellXxlJob(jobDesc = "计时_计时器服务_定时任务", jobCron = "0/30 * * * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}")
    public ReturnT<String> timming(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        // TODO调度锁
        try {
            // 调度到期计时器
            scheduledDueTimers(executionParam);
            // 调度逾期计时器
            scheduledOverDueTimers(executionParam);
            // 调度预警计时器
            scheduledAlarmTimers(executionParam);
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    /**
     * 如何描述该方法
     */
    private void scheduledDueTimers(ExecutionParam executionParam) {
        List<TsTimerEntity> dueTimers = timerService.getToScheduleOfDueTimers();
        for (TsTimerEntity timerEntity : dueTimers) {
            String timerUuid = timerEntity.getUuid();
            if (isInSchedule(timerUuid, "due")) {
                continue;
            }
            String systemId = timerConfigService.getOne(timerEntity.getConfigUuid()).getSystem();
            String tenantId = executionParam.getTenantId();
            String creator = timerEntity.getCreator();
            Date dueTime = timerEntity.getDueTime();
            TimerTask timerTask = new DueTimerTask(systemId, tenantId, creator, timerUuid, dueTime);
            scheduleTimerTask(timerUuid, dueTime, timerTask, "due");
        }
    }

    /**
     * @param executionParam
     */
    private void scheduledOverDueTimers(ExecutionParam executionParam) {
        List<TsTimerEntity> dueTimers = timerService.getToScheduleOfOverDueTimers();
        for (TsTimerEntity timerEntity : dueTimers) {
            String timerUuid = timerEntity.getUuid();
            if (isInSchedule(timerUuid, "overdue")) {
                continue;
            }
            String systemId = timerConfigService.getOne(timerEntity.getConfigUuid()).getSystem();
            String tenantId = executionParam.getTenantId();
            String creator = timerEntity.getCreator();
            Date overDueTime = timerService.getOverDueTime(timerEntity);
            TimerTask timerTask = new OverDueTimerTask(systemId, tenantId, creator, timerUuid, overDueTime);
            scheduleTimerTask(timerUuid, overDueTime, timerTask, "overdue");
        }
    }

    /**
     * @param executionParam
     */
    private void scheduledAlarmTimers(ExecutionParam executionParam) {
        List<TsTimerEntity> alarmTimers = timerService.getToScheduleOfAlarmTimers();
        // 生成预警计时器
        createAlarmTimers(alarmTimers, executionParam);

        List<TsTimerAlarmEntity> alarmEntities = timerAlarmService.list2Alarm();
        if (CollectionUtils.isEmpty(alarmEntities)) {
            return;
        }
        Set<String> timerUuids = alarmEntities.stream().map(alarmTimer -> alarmTimer.getTimerUuid()).collect(Collectors.toSet());
        List<TsTimerEntity> timerEntities = timerService.listByUuids(Lists.newArrayList(timerUuids));
        Map<String, TsTimerEntity> timerEntityMap = ConvertUtils.convertElementToMap(timerEntities, Entity.UUID);
        for (TsTimerAlarmEntity alarmEntity : alarmEntities) {
            TsTimerEntity timerEntity = timerEntityMap.get(alarmEntity.getTimerUuid());
            // 不在计时状态的计时器，删除预警
            if (!Integer.valueOf(EnumTimerStatus.STARTED.getValue()).equals(timerEntity.getStatus())
                    || (alarmEntity.getTotalAlarmCount() == null || alarmEntity.getTotalAlarmCount() <= 0)) {
                alarmEntity.setDeleteStatus(1);
                timerAlarmService.save(alarmEntity);
                continue;
            }

            String alarmUuid = alarmEntity.getUuid() + StringUtils.EMPTY;
            if (isInSchedule(alarmUuid, "alarm")) {
                continue;
            }

            String systemId = timerConfigService.getOne(timerEntity.getConfigUuid()).getSystem();
            String tenantId = executionParam.getTenantId();
            String creator = timerEntity.getCreator();
            String timerUuid = alarmEntity.getTimerUuid();
            Date alarmTime = getAlarmTime(alarmEntity, timerEntity);
            TimerTask timerTask = new AlarmTimerTask(systemId, tenantId, creator, alarmUuid, timerUuid, alarmTime);
            scheduleTimerTask(alarmUuid, alarmTime, timerTask, "alarm");
        }
    }

    /**
     * @param alarmEntity
     * @param timerEntity
     * @return
     */
    private Date getAlarmTime(TsTimerAlarmEntity alarmEntity, TsTimerEntity timerEntity) {
        Date alarmTime = alarmEntity.getAlarmTime();
        Date dueTime = timerEntity.getDueTime();
        Integer totalAlarmCount = alarmEntity.getTotalAlarmCount();
        Integer currentAlarmCount = alarmEntity.getCurrentAlarmCount();
        if (Integer.valueOf(1).equals(totalAlarmCount)) {
            return alarmTime;
        } else {
            long interval = (dueTime.getTime() - alarmTime.getTime()) / totalAlarmCount;
            long alarmTimeMillis = alarmTime.getTime() + interval * currentAlarmCount;
            alarmTime = Calendar.getInstance().getTime();
            alarmTime.setTime(alarmTimeMillis);
        }
        return alarmTime;
    }

    /**
     * @param alarmTimers
     */
    private void createAlarmTimers(List<TsTimerEntity> alarmTimers, ExecutionParam executionParam) {
        for (TsTimerEntity timerEntity : alarmTimers) {
            String timerData = timerEntity.getTimerData();
            if (StringUtils.isBlank(timerData)) {
                continue;
            }

            List<TsTimerAlarmEntity> alarmEntities = Lists.newArrayList();
            TsTimerParam timerParam = JsonUtils.json2Object(timerData, TsTimerParam.class);
            List<TsTimerParam.TsTimerAlarm> timerAlarms = timerParam.getTimerAlarms();
            for (TsTimerParam.TsTimerAlarm tsTimerAlarm : timerAlarms) {
                String timingMode = tsTimerAlarm.getTimingMode();
                if (StringUtils.isBlank(timingMode)) {
                    timingMode = timerEntity.getTimingMode();
                }
                double timeLimit = tsTimerAlarm.getTimeLimit() == null ? 0 : tsTimerAlarm.getTimeLimit();
                Date alarmTime = timerFacadeService.calculateTime(timerEntity.getUuid(), timerEntity.getDueTime(), -timeLimit, timingMode);
                TsTimerAlarmEntity entity = new TsTimerAlarmEntity();
                entity.setId(tsTimerAlarm.getId());
                entity.setTimeLimit(timeLimit);
                entity.setTimingMode(tsTimerAlarm.getTimingMode());
                entity.setTotalAlarmCount(tsTimerAlarm.getAlarmCount());
                entity.setCurrentAlarmCount(0);
                entity.setAlarmTime(alarmTime);
                entity.setAlarmDoingDone(false);
                entity.setDeleteStatus(0);
                entity.setTimerUuid(timerEntity.getUuid());
                alarmEntities.add(entity);
            }

            try {
                String tenantId = executionParam.getTenantId();
                String creator = timerEntity.getCreator();
                IgnoreLoginUtils.login(tenantId, creator);
                timerAlarmService.saveAll(alarmEntities);
            } catch (Exception error) {
                LOG.error("计时器生成预警信息执行异常：{}", error);
            } finally {
                IgnoreLoginUtils.logout();
            }
        }
    }

    /**
     * @param timerUuid
     * @param type
     */
    private static void addSchedule(String timerUuid, String type) {
        scheduleMap.put(timerUuid + type, timerUuid);
    }

    /**
     * @param timerUuid
     * @param type
     * @return
     */
    private static boolean isInSchedule(String timerUuid, String type) {
        return scheduleMap.containsKey(timerUuid + type);
    }

    /**
     * @param timerUuid
     * @param type
     */
    private static void removeSchedule(String timerUuid, String type) {
        scheduleMap.remove(timerUuid + type);
    }

    /**
     * @param timerUuid
     * @param time
     * @param timerTask
     * @param type
     */
    private void scheduleTimerTask(String timerUuid, Date time, TimerTask timerTask, String type) {
        addSchedule(timerUuid, type);
        scheduledExecutorService.schedule(timerTask, delay(time), TimeUnit.MILLISECONDS);
    }

    /**
     * @param time
     * @return
     */
    private long delay(Date time) {
        Date now = new Date();
        long delay = 0L;
        if (time.after(now)) {
            delay = time.getTime() - now.getTime();
        }
        return delay;
    }

    /**
     * Description: 计时器到期任务
     *
     * @author zhulh
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2021年4月10日.1	zhulh		2021年4月10日		Create
     * </pre>
     * @date 2021年4月10日
     */
    private static class DueTimerTask extends TimerTask {
        private String systemId;
        private String tenantId;
        private String creator;
        private String timerUuid;
        private Date dueTime;

        /**
         * @param systemId
         * @param tenantId
         * @param creator
         * @param timerUuid
         * @param dueTime
         */
        public DueTimerTask(String systemId, String tenantId, String creator, String timerUuid, Date dueTime) {
            super();
            this.systemId = systemId;
            this.tenantId = tenantId;
            this.creator = creator;
            this.timerUuid = timerUuid;
            this.dueTime = dueTime;
        }

        /**
         * (non-Javadoc)
         *
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {
            synchronized (locked) {
                TsTimerService timerService = ApplicationContextHolder.getBean(TsTimerService.class);
                try {
                    LOG.info("due doing thread start at " + DateUtils.formatDateTime(dueTime) + ", current time is "
                            + DateUtils.formatDateTime(Calendar.getInstance().getTime()));
                    RequestSystemContextPathResolver.setSystem(systemId);
                    IgnoreLoginUtils.login(tenantId, creator);
                    // 到期处理
                    timerService.dueDoing(timerUuid, this.dueTime);
                } catch (Exception e) {
                    String error = Throwables.getStackTraceAsString(e);
                    LOG.error("计时器到期处理任务执行异常：{}", error);
                    if (StringUtils.isBlank(error)) {
                        timerService.forceMarkDueInfo(timerUuid, error);
                    } else {
                        timerService.forceMarkDueInfo(timerUuid, error.length() <= 1000 ? error : error.substring(0, 1000));
                    }
                } finally {
                    removeSchedule(timerUuid, "due");
                    IgnoreLoginUtils.logout();
                    RequestSystemContextPathResolver.clear();
                }
            }
        }

    }

    /**
     * Description: 计时器到期任务
     *
     * @author zhulh
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2021年4月10日.1	zhulh		2021年4月10日		Create
     * </pre>
     * @date 2021年4月10日
     */
    private static class OverDueTimerTask extends TimerTask {
        private String systemId;
        private String tenantId;
        private String creator;
        private String timerUuid;
        private Date overDueTime;

        /**
         * @param systemId
         * @param tenantId
         * @param creator
         * @param timerUuid
         * @param overDueTime
         */
        public OverDueTimerTask(String systemId, String tenantId, String creator, String timerUuid, Date overDueTime) {
            super();
            this.systemId = systemId;
            this.tenantId = tenantId;
            this.creator = creator;
            this.timerUuid = timerUuid;
            this.overDueTime = overDueTime;
        }

        /**
         * (non-Javadoc)
         *
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {
            synchronized (locked) {
                TsTimerService timerService = ApplicationContextHolder.getBean(TsTimerService.class);
                try {
                    LOG.info("over due doing thread start at " + DateUtils.formatDateTime(overDueTime)
                            + ", current time is " + DateUtils.formatDateTime(Calendar.getInstance().getTime()));
                    RequestSystemContextPathResolver.setSystem(systemId);
                    IgnoreLoginUtils.login(tenantId, creator);
                    // 到期处理
                    timerService.overDueDoing(timerUuid, overDueTime);
                } catch (Exception e) {
                    String error = Throwables.getStackTraceAsString(e);
                    LOG.error("计时器逾期处理任务执行异常：{}", error);
                    if (StringUtils.isBlank(error)) {
                        timerService.forceMarkOverDueInfo(timerUuid, overDueTime, error);
                    } else {
                        timerService.forceMarkOverDueInfo(timerUuid, overDueTime, error.length() <= 1000 ? error : error.substring(0, 1000));
                    }
                } finally {
                    removeSchedule(timerUuid, "overdue");
                    IgnoreLoginUtils.logout();
                    RequestSystemContextPathResolver.clear();
                }
            }
        }

    }

    /**
     *
     */
    private static class AlarmTimerTask extends TimerTask {
        private String systemId;
        private String tenantId;
        private String creator;
        private String alarmUuid;
        private String timerUuid;
        private Date alarmTime;

        /**
         * @param systemId
         * @param tenantId
         * @param creator
         * @param alarmUuid
         * @param timerUuid
         * @param alarmTime
         */
        public AlarmTimerTask(String systemId, String tenantId, String creator, String alarmUuid, String timerUuid, Date alarmTime) {
            super();
            this.systemId = systemId;
            this.tenantId = tenantId;
            this.creator = creator;
            this.timerUuid = timerUuid;
            this.alarmUuid = alarmUuid;
            this.alarmTime = alarmTime;
        }

        @Override
        public void run() {
            TsTimerService timerService = ApplicationContextHolder.getBean(TsTimerService.class);
            try {
                LOG.info("alarm doing thread start at " + DateUtils.formatDateTime(alarmTime) + ", current time is "
                        + DateUtils.formatDateTime(Calendar.getInstance().getTime()));
                RequestSystemContextPathResolver.setSystem(systemId);
                IgnoreLoginUtils.login(tenantId, creator);
                // 预警处理
                timerService.alarmDoing(timerUuid, alarmUuid, this.alarmTime);
            } catch (Exception e) {
                String error = Throwables.getStackTraceAsString(e);
                LOG.error("计时器到期处理任务执行异常：{}", error);
                timerService.forceMarkAlarmInfo(timerUuid, alarmUuid);
            } finally {
                removeSchedule(alarmUuid, "alarm");
                IgnoreLoginUtils.logout();
                RequestSystemContextPathResolver.clear();
            }
        }
    }

}
