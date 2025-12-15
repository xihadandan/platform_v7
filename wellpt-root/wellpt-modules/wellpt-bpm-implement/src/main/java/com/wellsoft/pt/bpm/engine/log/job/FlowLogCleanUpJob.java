/*
 * @(#)8/27/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.log.job;

import com.wellsoft.pt.bpm.engine.log.service.FlowLogService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.workflow.entity.WfFlowSettingEntity;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.well.annotation.WellXxlJob;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/27/25.1	    zhulh		8/27/25		    Create
 * </pre>
 * @date 8/27/25
 */
@Component
public class FlowLogCleanUpJob {
    @Autowired
    private FlowLogService flowLogService;

    @Autowired
    private WfFlowSettingService flowSettingService;


    @XxlJob("flowLogCleanUpJob")
    @WellXxlJob(jobDesc = "工作流程_流程日志定时清理", jobCron = "0 0 3 * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}")
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("execute start");

        List<String> systemIds = flowSettingService.listSystemIds();

        systemIds.stream().forEach(system -> {
            try {
                if (StringUtils.isBlank(system)) {
                    return;
                }
                List<WfFlowSettingEntity> settingEntities = flowSettingService.listBySystemAndTenant(system, null);
                if (CollectionUtils.isEmpty(settingEntities)) {
                    return;
                }
                
                RequestSystemContextPathResolver.setSystem(system);
                IgnoreLoginUtils.login(settingEntities.get(0).getTenant(), settingEntities.get(0).getModifier());
                WorkFlowSettings flowSettings = new WorkFlowSettings(settingEntities);
                flowLogService.cleanUp(flowSettings.getLogRetentionDays(), system);
            } catch (Exception e) {
            } finally {
                IgnoreLoginUtils.logout();
                RequestSystemContextPathResolver.clear();
            }
        });

        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

}
