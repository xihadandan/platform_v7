/*
 * @(#)9/23/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.serialnumber.job.SnSerialNumberRecordCleanUpJob;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceParameterService;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.dao.WfFlowSimulationRecordDao;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationRecordEntity;
import com.wellsoft.pt.workflow.service.WfFlowSimulationRecordItemService;
import com.wellsoft.pt.workflow.service.WfFlowSimulationRecordService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 9/23/24.1	    zhulh		9/23/24		    Create
 * </pre>
 * @date 9/23/24
 */
@Service
public class WfFlowSimulationRecordServiceImpl extends AbstractJpaServiceImpl<WfFlowSimulationRecordEntity, WfFlowSimulationRecordDao, Long>
        implements WfFlowSimulationRecordService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;

    @Autowired
    private WfFlowSimulationRecordItemService flowSimulationRecordItemService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * @param flowInstUuid
     * @return
     */
    @Override
    public Long getUuidByFlowInstUuid(String flowInstUuid) {
        Assert.hasLength(flowInstUuid, "流程实例UUID不能为空！");

        String hql = "select t.uuid as uuid from WfFlowSimulationRecordEntity t where t.flowInstUuid = :flowInstUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("flowInstUuid", flowInstUuid);
        Long uuid = this.dao.getNumberByHQL(hql, params);
        return uuid;
    }

    @Override
    public WfFlowSimulationRecordEntity getByFlowInstUuid(String flowInstUuid) {
        Assert.hasLength(flowInstUuid, "流程实例UUID不能为空！");

        WfFlowSimulationRecordEntity entity = new WfFlowSimulationRecordEntity();
        entity.setFlowInstUuid(flowInstUuid);
        List<WfFlowSimulationRecordEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * @param flowDefUuid
     * @return
     */
    @Override
    public List<WfFlowSimulationRecordEntity> listByFlowDefUuid(String flowDefUuid) {
        Assert.hasLength(flowDefUuid, "流程定义UUID不能为空！");

        WfFlowSimulationRecordEntity entity = new WfFlowSimulationRecordEntity();
        entity.setFlowDefUuid(flowDefUuid);
        return this.dao.listByEntity(entity);
    }

    /**
     * @param uuid
     * @param state
     */
    @Override
    @Transactional
    public void updateStateByUuid(Long uuid, String state) {
        Assert.notNull(uuid, "仿真记录UUID不能为空！");

        WfFlowSimulationRecordEntity entity = this.dao.getOne(uuid);
        entity.setState(state);
        this.dao.save(entity);
    }

    @Override
    @Transactional
    public void deleteAllByUuids(List<Long> uuids) {
        uuids.forEach(uuid -> {
            WfFlowSimulationRecordEntity entity = this.getOne(uuid);
            if (StringUtils.isNotBlank(entity.getFlowInstUuid())) {
                this.deleteFlowInstance(entity.getFlowInstUuid());
            }
            flowSimulationRecordItemService.deleteByRecordUuid(uuid);
            this.delete(entity);
        });

        // 流水号清理
        if (CollectionUtils.isNotEmpty(uuids)) {
            SnSerialNumberRecordCleanUpJob snSerialNumberRecordCleanUpJob = ApplicationContextHolder.getBean(SnSerialNumberRecordCleanUpJob.class);
            scheduledExecutorService.execute(() -> {
                try {
                    snSerialNumberRecordCleanUpJob.execute("");
                } catch (Exception e) {
                }
            });
        }
    }

    /**
     * @param flowInstUuid
     */
    private void deleteFlowInstance(String flowInstUuid) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String taskInstUuid = taskService
                .getLastTaskInstanceUuidByFlowInstUuid(flowInstUuid);
        if (StringUtils.isNotBlank(taskInstUuid)) {
            taskService.forceDelete(userId, taskInstUuid);
        } else {
            flowInstanceParameterService.removeByFlowInstUuid(flowInstUuid);
            FlowInstance flowInstance = flowService.getFlowInstance(flowInstUuid);
            if (flowInstance != null) {
                flowService.deleteDraft(userId, flowInstUuid);
            }
        }
    }

    @Override
    public WfFlowSimulationRecordEntity getByFormUuidAndDataUuid(String formUuid, String dataUuid) {
        Assert.hasLength(formUuid, "表单定义UUID不能为空！");
        Assert.hasLength(dataUuid, "数据定义UUID不能为空！");

        WfFlowSimulationRecordEntity entity = new WfFlowSimulationRecordEntity();
        entity.setFormUuid(formUuid);
        entity.setDataUuid(dataUuid);
        List<WfFlowSimulationRecordEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    @Override
    public List<Long> listUuidByHQL(String hql, Map<String, Object> values) {
        return this.dao.listNumberByHQL(hql, values);
    }

}
