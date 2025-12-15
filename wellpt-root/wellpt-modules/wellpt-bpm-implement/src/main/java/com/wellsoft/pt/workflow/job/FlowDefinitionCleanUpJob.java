/*
 * @(#)2021年7月14日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.job;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.dto.WfFlowDefinitionCleanupConfigDto;
import com.wellsoft.pt.workflow.enums.EnumFlowDefinitionDeleteStatus;
import com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService;
import com.wellsoft.pt.workflow.facade.service.WfFlowDefinitionCleanupConfigFacadeService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.well.annotation.WellXxlJob;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Description: 流程定义定时清理任务
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月14日.1	zhulh		2021年7月14日		Create
 * </pre>
 * @date 2021年7月14日
 */
@Component
public class FlowDefinitionCleanUpJob {

    @XxlJob("flowDefinitionCleanUpJob")
    @WellXxlJob(jobDesc = "工作流程_流程定义_清理回收站的流程定义", jobCron = "0 0 1 * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}")
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("execute start");
        FlowDefinitionFacadeService flowDefinitionFacadeService = ApplicationContextHolder
                .getBean(FlowDefinitionFacadeService.class);
        WfFlowDefinitionCleanupConfigFacadeService flowDefinitionCleanupConfigFacadeService = ApplicationContextHolder
                .getBean(WfFlowDefinitionCleanupConfigFacadeService.class);
        List<WfFlowDefinitionCleanupConfigDto> flowDefinitionCleanupConfigDtos = flowDefinitionCleanupConfigFacadeService
                .listAllDto();
        for (WfFlowDefinitionCleanupConfigDto flowDefinitionCleanupConfigDto : flowDefinitionCleanupConfigDtos) {
            try {
                String tenantId = SpringSecurityUtils.getCurrentTenantId();
                IgnoreLoginUtils.login(tenantId, flowDefinitionCleanupConfigDto.getCreator());
                doCleanUp(flowDefinitionCleanupConfigDto, flowDefinitionFacadeService);
            } catch (Exception e) {
                XxlJobLogger.log(e);
            } finally {
                IgnoreLoginUtils.logout();
            }
        }
        XxlJobLogger.log("execute end");
        return ReturnT.SUCCESS;
    }

    /**
     * @param wfFlowDefinitionCleanupConfigDto
     */
    private void doCleanUp(WfFlowDefinitionCleanupConfigDto flowDefinitionCleanupConfigDto,
                           FlowDefinitionFacadeService flowDefinitionFacadeService) {
        if (BooleanUtils.isNotTrue(flowDefinitionCleanupConfigDto.getEnabled())) {
            return;
        }
        Date currentTime = Calendar.getInstance().getTime();
        List<FlowDefinition> flowDefinitions = flowDefinitionFacadeService.listByDeleteStatusAndSystemUnitId(
                EnumFlowDefinitionDeleteStatus.logicalDeleted.getCode(),
                flowDefinitionCleanupConfigDto.getSystemUnitId(), new PagingInfo(1, 20));
        for (FlowDefinition flowDefinition : flowDefinitions) {
            // 流程定义在使用更新为不可彻底删除
            if (flowDefinitionFacadeService.isFlowDefinitionInUse(flowDefinition.getUuid())) {
                flowDefinitionFacadeService.updateDeleteStatusByUuid(flowDefinition.getUuid(),
                        EnumFlowDefinitionDeleteStatus.cannotPhysicalDelete.getCode());
            } else {
                // 计算清理时间
                Date cleanUpTime = getCleanUpTime(flowDefinitionCleanupConfigDto, flowDefinition);
                // 彻底删除
                if (currentTime.after(cleanUpTime)) {
                    flowDefinitionFacadeService.autoCleanUp(flowDefinition.getUuid());
                }
            }
        }
    }

    /**
     * @param flowDefinitionCleanupConfigDto
     * @param flowDefinition
     * @return
     */
    private Date getCleanUpTime(WfFlowDefinitionCleanupConfigDto flowDefinitionCleanupConfigDto,
                                FlowDefinition flowDefinition) {
        Calendar cleanUpCalendar = Calendar.getInstance();
        cleanUpCalendar.setTime(flowDefinition.getDeleteTime());
        if (flowDefinitionCleanupConfigDto.getRetentionDays() != null) {
            cleanUpCalendar.add(Calendar.DAY_OF_YEAR, flowDefinitionCleanupConfigDto.getRetentionDays());
        } else {
            cleanUpCalendar.add(Calendar.DAY_OF_YEAR, 50);
        }
        Date cleanUpTime = cleanUpCalendar.getTime();
        return cleanUpTime;
    }

}
