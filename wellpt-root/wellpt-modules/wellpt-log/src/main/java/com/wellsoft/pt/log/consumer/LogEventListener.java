/*
 * @(#)2021年1月8日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.consumer;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.facade.support.DyformTitleUtils;
import com.wellsoft.pt.log.LogEvent;
import com.wellsoft.pt.log.entity.BusinessDetailsLog;
import com.wellsoft.pt.log.entity.BusinessOperationLog;
import com.wellsoft.pt.log.facade.service.LogFacadeService;
import com.wellsoft.pt.log.service.BusinessDetailsLogService;
import com.wellsoft.pt.log.service.BusinessOperationLogService;
import com.wellsoft.pt.rocketmq.annotation.RocketMqListener;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年1月8日.1	zhongzh		2021年1月8日		Create
 * </pre>
 * @date 2021年1月8日
 */
@Component
public class LogEventListener {

    @Autowired
    private WorkService workService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private BusinessDetailsLogService businessDetailsLogService;

    @Autowired
    private BusinessOperationLogService logBusinessOperationService;

    @Transactional
    @RocketMqListener(requireNew = true, topic = LogFacadeService.TOPIC, body = LogEvent.class, tags = LogEvent.TAGS_LOG)
    public void handle(LogEvent event) {
        BusinessOperationLog source = event.getSource();
        if (null == source) {
            return;
        }
        String startUserId = source.getCreator();
        try {
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, startUserId);
            handleInternal(event, source, startUserId);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    /**
     * @param event
     * @param source
     * @param startUserId
     */
    public void handleInternal(LogEvent event, BusinessOperationLog source, String startUserId) {
        // 表单标题保持不变
        if (StringUtils.equals(ModuleID.DYFORM.getValue(), source.getDataDefType())) {
            DyFormFormDefinition dyFormFormDefinition = getFormDefinition(source.getDataDefId());
            if (null == source.getDataDefName()) {
                source.setDataDefId(dyFormFormDefinition.getId());
                source.setDataDefName(dyFormFormDefinition.getName());
            }
            String dataName = getDyformDataName(source.getDataDefId(), source.getDataId());
            if (null != dataName) {
                source.setDataName(dataName);
            } else {
                DyFormData dyFormData = dyFormFacade.getDyFormData(dyFormFormDefinition.getUuid(), source.getDataId());
                dataName = DyformTitleUtils.generateDyformTitle(startUserId, dyFormFormDefinition, dyFormData);
                source.setDataName(dataName);
            }
        }
        logBusinessOperationService.saveLog(source);
        List<BusinessDetailsLog> details = event.getDetails();
        if (false == CollectionUtils.isEmpty(details)) {
            for (BusinessDetailsLog detail : details) {
                if (null == detail) {
                    continue;
                }
                detail.setLogId(source.getUuid());
                if (StringUtils.equals(ModuleID.DYFORM.getValue(), detail.getDataDefType())) {
                    DyFormFormDefinition dyFormFormDefinition = getFormDefinition(detail.getDataDefId());
                    if (null == detail.getDataDefName()) {
                        detail.setDataDefId(dyFormFormDefinition.getId());
                        detail.setDataDefName(dyFormFormDefinition.getName());
                    }
                    String dataName = getDyformDataName(detail.getDataDefId(), detail.getDataId());
                    if (null != dataName) {
                        detail.setDataName(dataName);
                    } else {
                        DyFormData dyFormData = dyFormFacade.getDyFormData(dyFormFormDefinition.getUuid(),
                                detail.getDataId());
                        dataName = DyformTitleUtils.generateDyformTitle(startUserId, dyFormFormDefinition, dyFormData);
                        detail.setDataName(dataName);
                    }
                }
                businessDetailsLogService.saveDetail(detail);
            }
        }
        Map<String, Object> params = event.getParams();
        if (null != params) {
            String dyformDataJson = (String) params.get("dyform");
            if (StringUtils.isBlank(dyformDataJson)) {
                return;
            }
            String flowInstUuid = (String) params.get("flowInstUuid");
            String taskInstUuid = (String) params.get("taskInstUuid");
            DyFormData dyFormData = dyFormFacade.parseDyformData(dyformDataJson);
            System.out.println(dyFormData);
            System.out.println(flowInstUuid);
            System.out.println(taskInstUuid);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param dataDefId
     * @return
     */
    protected DyFormFormDefinition getFormDefinition(String dataDefId) {
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinitionById(dataDefId);
        if (null == dyFormFormDefinition) {
            dyFormFormDefinition = dyFormFacade.getFormDefinition(dataDefId);
        }
        return dyFormFormDefinition;
    }

    /**
     * 查询历史表单标题
     *
     * @param dataDefId
     * @param dataId
     * @return
     */
    public String getDyformDataName(String dataDefId, String dataId) {
        PagingInfo pagingInfo = new PagingInfo(1, 1, false);
        BusinessOperationLog entity = new BusinessOperationLog();
        entity.setDataDefType(ModuleID.DYFORM.getValue());
        entity.setDataDefId(dataDefId);
        entity.setDataId(dataId);
        List<BusinessOperationLog> entities = logBusinessOperationService.listAllByPage(entity, pagingInfo, null);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0).getDataName();
        }
        BusinessDetailsLog entity2 = new BusinessDetailsLog();
        entity2.setDataDefType(ModuleID.DYFORM.getValue());
        entity2.setDataDefId(dataDefId);
        entity2.setDataId(dataId);
        List<BusinessDetailsLog> entities2 = businessDetailsLogService.listAllByPage(entity2, pagingInfo, null);
        if (CollectionUtils.isNotEmpty(entities2)) {
            return entities2.get(0).getDataName();
        }
        return null;
    }

}
