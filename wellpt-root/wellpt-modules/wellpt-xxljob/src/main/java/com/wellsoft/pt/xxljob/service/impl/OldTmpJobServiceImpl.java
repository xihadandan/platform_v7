package com.wellsoft.pt.xxljob.service.impl;

import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.service.OldTmpJobService;
import com.wellsoft.pt.xxljob.service.XxlJobService;
import com.xxl.job.core.well.model.TmpJobParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.sql.Blob;
import java.util.*;

/**
 * @Auther: yt
 * @Date: 2021/1/11 10:07
 * @Description:
 */
@Service
public class OldTmpJobServiceImpl extends BaseServiceImpl implements OldTmpJobService {


    @Autowired
    private XxlJobService xxlJobService;

    @Override
    @Transactional
    public synchronized void oldTmpJobToXxlJob(Set<String> jobClassNameSet, String tablePrefix) {
        if (jobClassNameSet == null || jobClassNameSet.size() == 0) {
            jobClassNameSet = new HashSet<>();
        }
        jobClassNameSet.add("DutyAgentActiveStatusTraceJob");
        jobClassNameSet.add("TaskOverDueSendRepeatMessageJob");
        jobClassNameSet.add("TaskAlarmSendRepeatMessageJob");
        jobClassNameSet.add("TaskDelegationCurrentWorkDueHandlerJob");

        if (StringUtils.isBlank(tablePrefix)) {
            tablePrefix = DEFAULT_TABLE_PREFIX;
        }

        List<Map<String, Object>> jobDetailList = new ArrayList();
        try {
            StringBuilder jobSql = new StringBuilder("select * from " + tablePrefix + TABLE_JOB_DETAILS + " where (" + COL_DESCRIPTION + " !='xxlJob' or " + COL_DESCRIPTION + " is null )  and (");
            List<String> list = new ArrayList<>(jobClassNameSet);
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    jobSql.append(" or ");
                }
                jobSql.append(COL_JOB_CLASS).append(" like '%").append(list.get(i)).append("'");
            }
            jobSql.append(") order by ").append(COL_JOB_NAME);
            jobDetailList = this.getCommonDao().query(jobSql.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Map<String, Object> jobDetail : jobDetailList) {
            String schedName = jobDetail.get(COL_SCHEDULER_NAME.toLowerCase()).toString();
            String jobName = jobDetail.get(COL_JOB_NAME.toLowerCase()).toString();
            String jobGrup = jobDetail.get(COL_JOB_GROUP.toLowerCase()).toString();
            String jobClassName = jobDetail.get(COL_JOB_CLASS.toLowerCase()).toString();
            jobClassName = jobClassName.substring(jobClassName.lastIndexOf(".") + 1, jobClassName.length());
            if (jobClassNameSet.contains(jobClassName)) {
                List<Map<String, Object>> triggerList = new ArrayList<>();
                try {
                    StringBuilder sql = new StringBuilder("select * from " + tablePrefix + TABLE_TRIGGERS + " where (" + COL_DESCRIPTION + " not like 'xxlJob%' or " + COL_DESCRIPTION + " is null ) and ");
                    sql.append(COL_JOB_NAME).append("='").append(jobName)
                            .append("' and ").append(COL_JOB_GROUP).append("='").append(jobGrup)
                            .append("' and ").append(COL_SCHEDULER_NAME).append("='").append(schedName)
                            .append("' and ").append(COL_TRIGGER_STATE).append("='").append(STATE_WAITING)
                            .append("' and ").append(COL_TRIGGER_TYPE).append("='").append(TTYPE_SIMPLE).append("'");
                    triggerList = this.getCommonDao().query(sql.toString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                for (Map<String, Object> trigger : triggerList) {
                    String triggerName = trigger.get(COL_TRIGGER_NAME.toLowerCase()).toString();
                    String triggerGroup = trigger.get(COL_TRIGGER_GROUP.toLowerCase()).toString();
                    long nextFireTime = Long.parseLong(trigger.get(COL_NEXT_FIRE_TIME.toLowerCase()).toString());

                    Blob blob = (Blob) trigger.get(COL_JOB_DATAMAP.toLowerCase());
                    Map<String, String> jobData = new HashMap<>();
                    if (blob != null) {
                        try {
                            InputStream is = blob.getBinaryStream();
                            Properties properties = new Properties();
                            if (is != null) {
                                try {
                                    properties.load(is);
                                } finally {
                                    is.close();
                                }
                            }
                            jobData = new HashMap(properties);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }

                    StringBuilder simpleSql = new StringBuilder("select * from " + tablePrefix + TABLE_SIMPLE_TRIGGERS + " where ");
                    simpleSql.append(COL_TRIGGER_NAME).append("='").append(triggerName)
                            .append("' and ").append(COL_TRIGGER_GROUP).append("='").append(triggerGroup)
                            .append("' and ").append(COL_SCHEDULER_NAME).append("='").append(schedName).append("'");

                    List<Map<String, Object>> simpleList = null;
                    try {
                        simpleList = this.getCommonDao().query(simpleSql.toString());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    if (simpleList == null || simpleList.size() != 1) {
                        throw new RuntimeException(triggerName + ":SIMPLE_TRIGGERS 数据不对");
                    }
                    Map<String, Object> simple = simpleList.get(0);

                    long repeat_count = Long.parseLong(simple.get(COL_REPEAT_COUNT.toLowerCase()).toString());
                    long repeat_interval = Long.parseLong(simple.get(COL_REPEAT_INTERVAL.toLowerCase()).toString());
                    long times_triggered = Long.parseLong(simple.get(COL_TIMES_TRIGGERED.toLowerCase()).toString());
                    long count = repeat_count - times_triggered;
                    if (count >= 0) {
                        count = count + 1;
                    }
                    Date startTime = new Date(nextFireTime);
                    Date endTime = new Date(nextFireTime + repeat_interval * count);
                    List<Date> dateList = DateUtils.calculationInterval(startTime, endTime, Integer.valueOf(String.valueOf(count)));

                    if (jobData.size() > 0) {
                        jobData.remove("type");
                        jobData.remove("jobUuid");
                        jobData.remove("assignIp");
                    }

                    //xxlJob执行需要的参数
                    ExecutionParam executionParam = new ExecutionParam();
                    executionParam.putAll(jobData);
                    TmpJobParam.Builder builder = TmpJobParam.toBuilder()
                            .setJobDesc(DEFAULT_TABLE_PREFIX + triggerName)
                            .setExecutorHandler(jobClassName);
                    for (Date date : dateList) {
                        builder.addExecutionTimeParams(date, executionParam.toJson());
                    }
                    List<String> xxlJobIdList = xxlJobService.addTmpStart(builder.build());
                    String jobIdStr = xxlJobIdList.get(0);
                    if (xxlJobIdList.size() > 1) {
                        jobIdStr = jobIdStr + "-" + xxlJobIdList.get(xxlJobIdList.size() - 1);
                    }
                    String updateStr = "xxlJob:" + jobIdStr;
                    StringBuilder updateSql = new StringBuilder("update " + tablePrefix + TABLE_TRIGGERS + " set " + COL_DESCRIPTION + "='" + updateStr + "' where ");
                    updateSql.append(COL_TRIGGER_NAME).append("='").append(triggerName)
                            .append("' and ").append(COL_TRIGGER_GROUP).append("='").append(triggerGroup)
                            .append("' and ").append(COL_SCHEDULER_NAME).append("='").append(schedName).append("'");
                    this.getCommonDao().getSession().createSQLQuery(updateSql.toString()).executeUpdate();
                }

                StringBuilder updateSql = new StringBuilder("update " + tablePrefix + TABLE_JOB_DETAILS + " set " + COL_DESCRIPTION + "='xxlJob' where ");
                updateSql.append(COL_SCHEDULER_NAME).append("='").append(schedName)
                        .append("' and ").append(COL_JOB_NAME).append("='").append(jobName)
                        .append("' and ").append(COL_JOB_GROUP).append("='").append(jobGrup)
                        .append("' and ").append(COL_JOB_CLASS).append("='").append(jobDetail.get(COL_JOB_CLASS.toLowerCase()).toString())
                        .append("'");
                this.getCommonDao().getSession().createSQLQuery(updateSql.toString()).executeUpdate();

            }
        }

    }
}
