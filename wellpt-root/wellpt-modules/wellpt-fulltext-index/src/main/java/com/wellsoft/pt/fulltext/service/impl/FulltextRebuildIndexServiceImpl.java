/*
 * @(#)6/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.fulltext.entity.FulltextRebuildLogEntity;
import com.wellsoft.pt.fulltext.facade.service.FulltextSettingFacadeService;
import com.wellsoft.pt.fulltext.service.FulltextRebuildIndexService;
import com.wellsoft.pt.fulltext.service.FulltextRebuildLogService;
import com.wellsoft.pt.fulltext.support.FulltextRebuildIndexTask;
import com.wellsoft.pt.fulltext.support.FulltextSetting;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/20/25.1	    zhulh		6/20/25		    Create
 * </pre>
 * @date 6/20/25
 */
@Service
public class FulltextRebuildIndexServiceImpl implements FulltextRebuildIndexService, ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FulltextSettingFacadeService fulltextSettingFacadeService;

    @Autowired
    private FulltextRebuildLogService fulltextRebuildLogService;

    @Autowired(required = false)
    private List<FulltextRebuildIndexTask> fulltextRebuildIndexTasks;

    private Map<String, List<ScheduledFuture>> scheduledFutureMap = Maps.newHashMap();

    private boolean contextRefreshed = false;

    @Autowired
    private TaskScheduler taskScheduler;

    @Override
    public void rebuildIndex() {
        List<FulltextSetting> fulltextSettings = fulltextSettingFacadeService.getAllFulltextSettings();
        fulltextSettings.forEach(fulltextSetting -> rebuild(fulltextSetting));
    }

    @Override
    public void rebuildIndex(FulltextSetting fulltextSetting) {
        rebuild(fulltextSetting);
    }

    private void rebuild(FulltextSetting fulltextSetting) {
        List<FulltextSetting.RebuildRule> rebuildRules = fulltextSetting.getRebuildRules();
//        if (CollectionUtils.isEmpty(rebuildRules)) {
//            rebuildRules = Lists.newArrayList();
//            FulltextSetting.RebuildRule rebuildRule = new FulltextSetting.RebuildRule();
//            rebuildRule.setRepeatType("repeat");
//            rebuildRule.setRepeatInterval(1);
//            rebuildRule.setRepeatUnit("week");
//            rebuildRule.setRepeatDaysOfWeek(Lists.newArrayList("Mon", "Tue"));
//            rebuildRule.setTimePoint("10:58");
//            rebuildRules.add(rebuildRule);
//
////            rebuildRule = new FulltextSetting.RebuildRule();
////            rebuildRule.setRepeatType("none");
////            Calendar calendar = Calendar.getInstance();
////            calendar.add(Calendar.MINUTE, 1);
////            rebuildRule.setExecuteTime(DateUtils.formatDateTime(calendar.getTime()));
//            rebuildRules.add(rebuildRule);
//        }
        List<ScheduledFuture> scheduledFutures = getScheduledFutures(fulltextSetting.getSystem());
        cancelScheduledFutures(scheduledFutures);

        if (CollectionUtils.isNotEmpty(rebuildRules)) {
            rebuildRules.forEach(rebuildRule -> {
                ScheduledFuture scheduledFuture = rebuild(rebuildRule, fulltextSetting);
                if (scheduledFuture != null) {
                    scheduledFutures.add(scheduledFuture);
                }
            });
        }

//        fulltextSetting.setUpdateMode("regular");
//        fulltextSetting.setRegularTimePoint("14:37");
        // 定时更新
        String updateMode = fulltextSetting.getUpdateMode();
        if (StringUtils.equals(updateMode, "regular") && StringUtils.isNotBlank(fulltextSetting.getRegularTimePoint())) {
            ScheduledFuture scheduledFuture = buildRegularScheduledFuture(fulltextSetting);
            scheduledFutures.add(scheduledFuture);
        }
    }

    private ScheduledFuture buildRegularScheduledFuture(FulltextSetting fulltextSetting) {
        String[] timeParts = StringUtils.split(fulltextSetting.getRegularTimePoint(), Separator.COLON.getValue());
        int second = 0;
        int minute = 0;
        int hour = 0;
        if (timeParts.length == 1) {
            hour = Integer.parseInt(timeParts[0]);
        } else if (timeParts.length == 2) {
            hour = Integer.parseInt(timeParts[0]);
            minute = Integer.parseInt(timeParts[1]);
        } else if (timeParts.length == 3) {
            hour = Integer.parseInt(timeParts[0]);
            minute = Integer.parseInt(timeParts[1]);
            second = Integer.parseInt(timeParts[2]);
        }
        String cronExpression = String.format("%d %d %d * * ?", second, minute, hour);
        CronTrigger cronTrigger = new CronTrigger(cronExpression);
        ScheduledFuture scheduledFuture = taskScheduler.schedule(new BuildIncrementIndexTimerTask(fulltextSetting), cronTrigger);
        return scheduledFuture;
    }

    private void cancelScheduledFutures(List<ScheduledFuture> scheduledFutures) {
        scheduledFutures.forEach(scheduledFuture -> {
            scheduledFuture.cancel(false);
        });
        scheduledFutures.clear();
    }

    private List<ScheduledFuture> getScheduledFutures(String system) {
        List<ScheduledFuture> scheduledFutures = scheduledFutureMap.get(system);
        if (scheduledFutures == null) {
            scheduledFutures = Lists.newArrayList();
            scheduledFutureMap.put(system, scheduledFutures);
        }
        return scheduledFutures;
    }

    private ScheduledFuture rebuild(FulltextSetting.RebuildRule rebuildRule, FulltextSetting fulltextSetting) {
        if (!StringUtils.equals(FulltextSetting.RebuildRule.STATE_ENABLED, rebuildRule.getState())) {
            return null;
        }

        String repeatType = rebuildRule.getRepeatType();
        ScheduledFuture scheduledFuture = null;
        if (StringUtils.equals("none", repeatType)) {
            scheduledFuture = rebuildAtTime(rebuildRule, fulltextSetting);
        } else {
            scheduledFuture = rebuildAtCronTrigger(rebuildRule, fulltextSetting);
        }
        return scheduledFuture;
    }

    private ScheduledFuture rebuildAtTime(FulltextSetting.RebuildRule rebuildRule, FulltextSetting fulltextSetting) {
        String executeTime = rebuildRule.getExecuteTime();
        try {
//            Timer timer = new Timer();
            Date scheduleTime = DateUtils.parse(executeTime);
//            if (scheduleTime.before(Calendar.getInstance().getTime())) {
//                fulltextSettingFacadeService.updateRuleState(fulltextSetting.getUuid(), rebuildRule, FulltextSetting.RebuildRule.STATE_DISABLED);
//                return null;
//            }
//            timer.schedule(new RebuildIndexTimerTask(rebuildRule, fulltextSetting, true), scheduleTime);
            return taskScheduler.schedule(new RebuildIndexTimerTask(rebuildRule, fulltextSetting, true), scheduleTime);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private ScheduledFuture rebuildAtCronTrigger(FulltextSetting.RebuildRule rebuildRule, FulltextSetting fulltextSetting) {
        String cronExpression = parseCronExpression(rebuildRule);
        CronTrigger cronTrigger = new CronTrigger(cronExpression);
        ScheduledFuture scheduledFuture = taskScheduler.schedule(new RebuildIndexTimerTask(rebuildRule, fulltextSetting, false), cronTrigger);
        return scheduledFuture;
    }

    private String parseCronExpression(FulltextSetting.RebuildRule rebuildRule) {
        String expression = null;
        String repeatUnit = rebuildRule.getRepeatUnit();
        int repeatInterval = rebuildRule.getRepeatInterval();
        String timePoint = rebuildRule.getTimePoint();
        String[] timeParts = StringUtils.split(timePoint, Separator.COLON.getValue());
        int second = 0;
        int minute = 0;
        int hour = 0;
        if (timeParts.length == 1) {
            hour = Integer.parseInt(timeParts[0]);
        } else if (timeParts.length == 2) {
            hour = Integer.parseInt(timeParts[0]);
            minute = Integer.parseInt(timeParts[1]);
        } else if (timeParts.length == 3) {
            hour = Integer.parseInt(timeParts[0]);
            minute = Integer.parseInt(timeParts[1]);
            second = Integer.parseInt(timeParts[2]);
        }
        switch (repeatUnit) {
            case "day":
                expression = String.format("%d %d %d */%d * ?", second, minute, hour, repeatInterval);
                break;
            case "week":
                List<String> daysOfWeek = rebuildRule.getRepeatDaysOfWeek();
                String days = daysOfWeek.stream().map(day -> day.toUpperCase()).collect(Collectors.joining(Separator.COMMA.getValue()));
                expression = String.format("%d %d %d ? * %s", second, minute, hour, days);
                break;
            case "month":
            case "year":
                String dayOfMonth = rebuildRule.getRepeatDayOfMonth();
                String day = "1";
                if (StringUtils.equals(dayOfMonth, "fifteen")) {
                    day = "15";
                } else if (StringUtils.equals(dayOfMonth, "last")) {
                    day = "L";
                }
                if (StringUtils.equals(repeatUnit, "month")) {
                    expression = String.format("%d %d %d %s * ?", second, minute, hour, day);
                } else {
                    String monthOfYear = rebuildRule.getRepeatMonthOfYear();
                    String year = "*";
                    if (repeatInterval > 1) {
                        year = String.format("%d/%d", Calendar.getInstance().get(Calendar.YEAR), repeatInterval);
                    }
                    expression = String.format("%d %d %d %s %s ?", second, minute, hour, day, monthOfYear, year);
                }
                break;
        }
        return expression;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (contextRefreshed) {
                return;
            }
            contextRefreshed = true;
            rebuildIndex();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    class RebuildIndexTimerTask extends TimerTask {

        private FulltextSetting.RebuildRule rebuildRule;
        private FulltextSetting fulltextSetting;
        private boolean updateRuleState;

        public RebuildIndexTimerTask(FulltextSetting.RebuildRule rebuildRule, FulltextSetting fulltextSetting, boolean updateRuleState) {
            this.rebuildRule = rebuildRule;
            this.fulltextSetting = fulltextSetting;
            this.updateRuleState = updateRuleState;
        }

        @Override
        public void run() {
            String ruleId = rebuildRule.getUuid();
            if (StringUtils.isBlank(ruleId)) {
                ruleId = fulltextSetting.getUuid() + StringUtils.EMPTY;
            }
            FulltextRebuildLogEntity logEntity = new FulltextRebuildLogEntity();
            try {
                RequestSystemContextPathResolver.setSystem(fulltextSetting.getSystem());
                IgnoreLoginUtils.login(fulltextSetting.getTenant(), fulltextSetting.getCreator());

                boolean getLock = tryGetTaskLock(fulltextSetting.getUuid(), ruleId);
                if (!getLock) {
                    return;
                }

                Date startTime = Calendar.getInstance().getTime();
                logEntity.setSettingUuid(fulltextSetting.getUuid());
                logEntity.setRuleId(rebuildRule.getUuid());
                logEntity.setStartTime(startTime);
                logEntity.setExecuteState(FulltextRebuildLogEntity.EXECUTE_STATE_RUNNING);
                fulltextRebuildLogService.save(logEntity);

                // 验证当前锁
                getLock = tryGetTaskCurrentLock(fulltextSetting.getUuid(), ruleId);
                if (!getLock) {
                    fulltextRebuildLogService.delete(logEntity);
                    return;
                }

                long totalCount = 0;
                if (CollectionUtils.isNotEmpty(fulltextRebuildIndexTasks)) {
                    for (FulltextRebuildIndexTask task : fulltextRebuildIndexTasks) {
                        try {
                            totalCount += task.indexCount(rebuildRule, fulltextSetting);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
                logEntity.setOriginalIndexCount(totalCount);
                logEntity.setSystem(fulltextSetting.getSystem());
                logEntity.setTenant(fulltextSetting.getTenant());
                fulltextRebuildLogService.save(logEntity);

                // 执行中
                if (CollectionUtils.isNotEmpty(fulltextRebuildIndexTasks)) {
                    fulltextRebuildIndexTasks.forEach(task -> {
                        try {
                            task.rebuildIndex(rebuildRule, fulltextSetting);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    });
                }

                // 执行成功
                long rebuildCount = 0;
                if (CollectionUtils.isNotEmpty(fulltextRebuildIndexTasks)) {
                    for (FulltextRebuildIndexTask task : fulltextRebuildIndexTasks) {
                        rebuildCount += task.indexCount(rebuildRule, fulltextSetting);
                    }
                }
                logEntity.setEndTime(Calendar.getInstance().getTime());
                logEntity.setElapsedTimeInSecond((logEntity.getEndTime().getTime() - logEntity.getStartTime().getTime()) / 1000);
                logEntity.setRebuildIndexCount(rebuildCount);
                logEntity.setExecuteState(FulltextRebuildLogEntity.EXECUTE_STATE_SUCCESS);
                fulltextRebuildLogService.save(logEntity);
                if (updateRuleState) {
                    fulltextSettingFacadeService.updateRuleState(fulltextSetting.getUuid(), rebuildRule, FulltextSetting.RebuildRule.STATE_COMPLETED);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                logEntity.setExecuteState(FulltextRebuildLogEntity.EXECUTE_STATE_FAILED);
                fulltextRebuildLogService.save(logEntity);
            } finally {
                IgnoreLoginUtils.logout();
                RequestSystemContextPathResolver.clear();
            }
        }

    }

    class BuildIncrementIndexTimerTask extends TimerTask {
        private FulltextSetting fulltextSetting;

        public BuildIncrementIndexTimerTask(FulltextSetting fulltextSetting) {
            this.fulltextSetting = fulltextSetting;
        }

        @Override
        public void run() {
            String ruleId = "increment";
            try {
                RequestSystemContextPathResolver.setSystem(fulltextSetting.getSystem());
                IgnoreLoginUtils.login(fulltextSetting.getTenant(), fulltextSetting.getCreator());

                boolean getLock = tryGetTaskLock(fulltextSetting.getUuid(), ruleId);
                if (!getLock) {
                    return;
                }

                FulltextRebuildLogEntity logEntity = new FulltextRebuildLogEntity();
                Date startTime = Calendar.getInstance().getTime();
                logEntity.setSettingUuid(fulltextSetting.getUuid());
                logEntity.setRuleId(ruleId);
                logEntity.setStartTime(startTime);
                logEntity.setExecuteState(FulltextRebuildLogEntity.EXECUTE_STATE_RUNNING);
                fulltextRebuildLogService.save(logEntity);

                // 验证当前锁
                getLock = tryGetTaskCurrentLock(fulltextSetting.getUuid(), ruleId);
                if (!getLock) {
                    fulltextRebuildLogService.delete(logEntity);
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                // 执行中
                if (CollectionUtils.isNotEmpty(fulltextRebuildIndexTasks)) {
                    fulltextRebuildIndexTasks.forEach(task -> task.buildIncrementIndex(calendar.getTime(), fulltextSetting));
                }

                fulltextRebuildLogService.delete(logEntity);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                IgnoreLoginUtils.logout();
                RequestSystemContextPathResolver.clear();
            }
        }
    }

    private boolean tryGetTaskLock(Long settingUuid, String ruleId) {
        Random random = new Random();
        try {
            Thread.sleep(1000 * random.nextInt(10));
        } catch (InterruptedException e) {
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        long executeCount = fulltextRebuildLogService.countBySettingUuidAndRuleIdAndExecuteState(settingUuid, ruleId, FulltextRebuildLogEntity.EXECUTE_STATE_RUNNING, calendar.getTime());
        return executeCount <= 0;
    }

    private boolean tryGetTaskCurrentLock(Long settingUuid, String ruleId) {
        Random random = new Random();
        try {
            Thread.sleep(1000 * random.nextInt(10));
        } catch (InterruptedException e) {
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        long executeCount = fulltextRebuildLogService.countBySettingUuidAndRuleIdAndExecuteState(settingUuid, ruleId, FulltextRebuildLogEntity.EXECUTE_STATE_RUNNING, calendar.getTime());
        return executeCount <= 1;
    }

}
