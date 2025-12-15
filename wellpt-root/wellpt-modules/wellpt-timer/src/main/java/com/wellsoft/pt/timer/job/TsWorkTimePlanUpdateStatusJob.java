/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.job;

import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.timer.service.TsWorkTimePlanService;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.utils.ParamUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.well.annotation.WellXxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 工作时间方案状态更新任务——每分钟执行一次
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
public class TsWorkTimePlanUpdateStatusJob {

    @Autowired
    private TsWorkTimePlanService workTimePlanService;

    /**
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("tsWorkTimePlanUpdateStatusJob")
    @WellXxlJob(jobDesc = "计时_工作时间方案状态更新_定时任务", jobCron = "0 */1 * * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}")
    public ReturnT<String> syncWorkTimePlanStatus(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            workTimePlanService.syncWorkTimePlanStatus();
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
