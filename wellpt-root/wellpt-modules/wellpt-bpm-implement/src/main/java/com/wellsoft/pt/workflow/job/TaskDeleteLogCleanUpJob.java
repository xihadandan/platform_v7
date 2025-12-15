/*
 * @(#)10/22/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.job;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.service.TaskDeleteLogService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.well.annotation.WellXxlJob;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * Description: 流程删除日志定时清理
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 10/22/24.1	    zhulh		10/22/24		    Create
 * </pre>
 * @date 10/22/24
 */
@Component
public class TaskDeleteLogCleanUpJob {

    @XxlJob("taskDeleteLogCleanUpJob")
    @WellXxlJob(jobDesc = "工作流程_流程删除日志定时清理", jobCron = "0 0 2 * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}")
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("execute start");

        TaskDeleteLogService taskDeleteLogService = ApplicationContextHolder.getBean(TaskDeleteLogService.class);
        // 删除30天以前的数据
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        taskDeleteLogService.deleteBeforeCreateTime(calendar.getTime());

        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

}
