package com.wellsoft.pt.task.scheduler;

import com.google.gson.Gson;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.task.service.JobDetailsService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.quartz.*;
import org.quartz.impl.jdbcjobstore.JobStoreCMT;
import org.quartz.simpl.SimpleClassLoadHelper;
import org.quartz.spi.OperableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.PatternMatchUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Description: 集群化的定时任务拉取
 * 1).支持指定ip执行定时任务（实例ID通过com.wellsoft.pt.task.support.HostIpInstanceIdGenerator生成）
 *
 * @author chenq
 * @date 2019/7/29
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/29    chenq		2019/7/29		Create
 * </pre>
 */
public class JobStoreClusterCMT extends JobStoreCMT {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Gson gson = new Gson();

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

    public static void main(String[] arrs) {
        String[] x = "1111/192.168.0.145:8080/3333".split("/");
        String address = x[1];
        String test = "192.168.0.14*";
        System.out.println(ipMatch(test, address));
    }

    @Override
    protected List<OperableTrigger> acquireNextTrigger(Connection conn, long noLaterThan,
                                                       int maxCount, long timeWindow)
            throws JobPersistenceException {
        if (timeWindow < 0) {
            throw new IllegalArgumentException();
        }

        List<OperableTrigger> acquiredTriggers = new ArrayList<OperableTrigger>();
        Set<JobKey> acquiredJobKeysForNoConcurrentExec = new HashSet<JobKey>();
        final int MAX_DO_LOOP_RETRY = 3;
        int currentLoopCount = 0;
//        maxCount = 10;
        do {
            currentLoopCount++;
            try {
                long noEarLierThan = getMisfireTime();
                logger.info("获取任务触发，noLaterThan = {} , noEarlierThan = {} , maxCount = {} ",
                        new Object[]{DateFormatUtils.format(new Date(noLaterThan + timeWindow), "yyyy-MM-dd HH:mm:ss"),
                                DateFormatUtils.format(new Date(noEarLierThan), "yyyy-MM-dd HH:mm:ss"), maxCount});
                List<TriggerKey> keys = getDelegate().selectTriggerToAcquire(conn,
                        noLaterThan + timeWindow, noEarLierThan, maxCount);
                logger.info("获取到任务触发结果：{}", gson.toJson(keys));
                // No trigger is ready to fire yet.
                if (keys == null || keys.size() == 0)
                    return acquiredTriggers;

                long batchEnd = noLaterThan;

                for (TriggerKey triggerKey : keys) {
                    // If our trigger is no longer available, try a new one.
                    OperableTrigger nextTrigger = retrieveTrigger(conn, triggerKey);
                    if (nextTrigger == null) {
                        continue; // next trigger
                    }

                    if (!allowIpFireTrigger(nextTrigger, conn)) {
                        // 更新trigger为miss
                        updateMisfiredTrigger(conn, triggerKey, "WAITING", true);
                        continue;
                    }


                    // If trigger's job is set as @DisallowConcurrentExecution, and it has already been added to result, then
                    // put it back into the timeTriggers set and continue to search for next trigger.
                    JobKey jobKey = nextTrigger.getJobKey();
                    JobDetail job;
                    try {
                        job = retrieveJob(conn, jobKey);
                    } catch (JobPersistenceException jpe) {
                        try {
                            getLog().error("Error retrieving job, setting trigger state to ERROR.",
                                    jpe);
                            getDelegate().updateTriggerState(conn, triggerKey, STATE_ERROR);
                        } catch (SQLException sqle) {
                            getLog().error("Unable to set trigger state to ERROR.", sqle);
                        }
                        continue;
                    }

                    if (job.isConcurrentExectionDisallowed()) {
                        if (acquiredJobKeysForNoConcurrentExec.contains(jobKey)) {
                            continue; // next trigger
                        } else {
                            acquiredJobKeysForNoConcurrentExec.add(jobKey);
                        }
                    }

                    if (nextTrigger.getNextFireTime().getTime() > batchEnd) {
                        break;
                    }
                    // We now have a acquired trigger, let's add to return list.
                    // If our trigger was no longer in the expected state, try a new one.
                    int rowsUpdated = getDelegate().updateTriggerStateFromOtherState(conn,
                            triggerKey, STATE_ACQUIRED, STATE_WAITING);
                    if (rowsUpdated <= 0) {
                        continue; // next trigger
                    }
                    nextTrigger.setFireInstanceId(getFiredTriggerRecordId());
                    getDelegate().insertFiredTrigger(conn, nextTrigger, STATE_ACQUIRED, null);

                    if (acquiredTriggers.isEmpty()) {
                        batchEnd = Math.max(nextTrigger.getNextFireTime().getTime(),
                                System.currentTimeMillis()) + timeWindow;
                    }
                    acquiredTriggers.add(nextTrigger);
                    if (nextTrigger.getJobDataMap().containsKey("jobUuid")) {
                        updateJobFireInstance(nextTrigger.getJobDataMap().getString("jobUuid"));
                    }
                }

                // if we didn't end up with any trigger to fire from that first
                // batch, try again for another batch. We allow with a max retry count.
                if (acquiredTriggers.size() == 0 && currentLoopCount < MAX_DO_LOOP_RETRY) {
                    continue;
                }

                // We are done with the while loop.
                break;
            } catch (Exception e) {
                throw new JobPersistenceException(
                        "Couldn't acquire next trigger: " + e.getMessage(), e);
            }
        } while (true);

        // Return the acquired trigger list
        return acquiredTriggers;
    }

    /**
     * 更新定时任务的执行机器实例
     *
     * @param jobName
     */
    private void updateJobFireInstance(String jobName) {

        ApplicationContextHolder.getBean(
                JobDetailsService.class).updateLastExecuteInstance(jobName, this.getInstanceId());

    }

    private boolean allowIpFireTrigger(Trigger trigger, Connection conn) throws Exception {
        JobDetail jobDetail = getDelegate().selectJobDetail(conn, trigger.getJobKey(),
                new SimpleClassLoadHelper());
        if (jobDetail != null) {
            JobDataMap jsonDataMap = jobDetail.getJobDataMap();
            String assignIp = jsonDataMap.getString("assignIp");
            String[] idParts = getInstanceId().split("/");
            if (idParts.length == 3) {
                String address = idParts[1];
                if (StringUtils.isNotBlank(assignIp)) {
                    if (!ipMatch(assignIp, address)) {
                        logger.warn("任务=[{}]，指定IP=[{}]运行，与本机IP=[{}]不匹配，无法使用该节点处理任务",
                                new Object[]{trigger.getJobKey().getName(),
                                        assignIp, address});
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
