/*
 * @(#)2021年2月5日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.job;

import com.wellsoft.pt.app.workflow.facade.service.WorkFlowSimulationService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.utils.ParamUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.well.annotation.WellXxlJob;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年2月5日.1	zhulh		2021年2月5日		Create
 * </pre>
 * @date 2021年2月5日
 */
@Component
public class WorkFlowSimulationCleanUpJob {

    @Autowired
    private WorkFlowSimulationService workFlowSimulationService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @XxlJob("workFlowSimulationCleanUpJob")
    @WellXxlJob(jobDesc = "工作流程_流程仿真_数据清理", jobCron = "0 0/15 * * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}")
    public ReturnT<String> workFlowSimulationCleanUpJob(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        List<String> systemIds = flowSettingService.listSystemIds();
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            systemIds.forEach(systemId -> {
                if (StringUtils.isNotBlank(systemId)) {
                    try {
                        RequestSystemContextPathResolver.setSystem(systemId);
                        workFlowSimulationService.cleanSimulationData();
                    } catch (Exception e) {
                    } finally {
                        RequestSystemContextPathResolver.clear();
                    }
                } else {
                    workFlowSimulationService.cleanSimulationData();
                }

            });
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

}
