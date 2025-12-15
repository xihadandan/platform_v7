package com.wellsoft.pt.job.manager.support;

import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractCustomDataStoreRenderer;
import com.wellsoft.pt.task.JobStateEnum;
import com.wellsoft.pt.task.entity.JobDetails;
import com.wellsoft.pt.task.service.JobDetailsService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/15    chenq		2019/6/15		Create
 * </pre>
 */
@Component
public class TaskJobRuntimeInfoDataStoreRender extends AbstractCustomDataStoreRenderer {


    @Autowired
    JobDetailsService jobDetailsService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired(required = false)
    private StdScheduler scheduler;

    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               String exParams) {
        if (scheduler == null) {
            return null;
        }
        String jobUuid = (String) rowData.get("uuid");
        JobDetails details = jobDetailsService.getOne(jobUuid);
        String triggerName = details.getName();
        String triggerGroup = details.getTenantId();
        try {
            if ("state".equals(exParams)) {
                return JobStateEnum.fromTriggerState(
                        scheduler.getTriggerState(TriggerKey.triggerKey(triggerName, triggerGroup))).getName();
            } else if ("nextFireTime".equals(exParams)) {
                Trigger trigger = scheduler.getTrigger(
                        TriggerKey.triggerKey(triggerName, triggerGroup));
                if (trigger != null) {
                    return DateFormatUtils.format(trigger.getNextFireTime(), "yyyy-MM-dd HH:mm:ss");
                }
            } else if ("schedulerInstanceId".equals(exParams)) {
                Trigger trigger = scheduler.getTrigger(
                        TriggerKey.triggerKey(triggerName, triggerGroup));
                if (trigger != null) {
                    return scheduler.getSchedulerInstanceId();
                }
            }
        } catch (Exception e) {
            logger.error("查询定时任务运行期信息异常：", e);
        }

        return "";
    }


    @Override
    public String getType() {
        return "taskJobRenderer";
    }

    @Override
    public String getName() {
        return "定时任务_运行期信息渲染器";
    }
}
