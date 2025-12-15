/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.job;

import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.timer.facade.service.TsHolidayInstanceFacadeService;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.utils.ParamUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.well.annotation.WellXxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 节假日实例自动生成任务——每天凌晨1点执行一次(0 0 1 * * ?)
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
public class TsHolidayInstanceJob {

    @Autowired
    private TsHolidayInstanceFacadeService holidayInstanceFacadeService;

    /**
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("tsHolidayInstanceJob")
    @WellXxlJob(jobDesc = "计时_节假日实例自动生成_定时任务", jobCron = "0 0 1 * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}")
    public ReturnT<String> syncHolidayInstances(String param) throws Exception {
        XxlJobLogger.log("execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            holidayInstanceFacadeService.syncHolidayInstances();
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
