package com.wellsoft.pt.xxljob.jobhandler;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.org.entity.OrgVersionEntity;
import com.wellsoft.pt.org.service.BizOrganizationService;
import com.wellsoft.pt.org.service.OrgVersionService;
import com.wellsoft.pt.xxljob.service.JobHandlerName;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.well.annotation.WellXxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月27日   chenq	 Create
 * </pre>
 */
@Component
public class OrgTimedTask {
    private Logger logger = LoggerFactory.getLogger(OrgTimedTask.class);

    /**
     * 每天的凌晨0点执行一次
     */
    @XxlJob(JobHandlerName.Timed.OrgVersionSchedulePublish)
    @WellXxlJob(jobDesc = "组织版本定时发布", jobCron = "0 0 0 * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = false)
    public ReturnT<String> orgVersionSchedulePublish(String param) throws Exception {
        XxlJobLogger.log(JobHandlerName.Timed.OrgVersionSchedulePublish + " execute start");
        try {
            OrgVersionService orgVersionService = ApplicationContextHolder.getBean(OrgVersionService.class);
            Map<String, Object> params = Maps.newHashMap();
            params.put("state", OrgVersionEntity.State.DESIGNING);
            params.put("time", new Date());
            orgVersionService.batchUpdatePublish(new Date());
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
        }
        XxlJobLogger.log(JobHandlerName.Timed.OrgVersionSchedulePublish + " execute end");
        return ReturnT.SUCCESS;
    }


    @XxlJob(JobHandlerName.Timed.BizOrgExpiredJob)
    @WellXxlJob(jobDesc = "业务组织失效更新", jobCron = "0 0 0 * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = false)
    public ReturnT<String> bizOrgExpiredJob(String param) throws Exception {
        XxlJobLogger.log(JobHandlerName.Timed.BizOrgExpiredJob + " execute start");
        try {
            BizOrganizationService bizOrganizationService = ApplicationContextHolder.getBean(BizOrganizationService.class);
            bizOrganizationService.batchUpdateAllBizOrgExpiredExceedExpiredTime();
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
        }
        XxlJobLogger.log(JobHandlerName.Timed.OrgVersionSchedulePublish + " execute end");
        return ReturnT.SUCCESS;
    }
}
