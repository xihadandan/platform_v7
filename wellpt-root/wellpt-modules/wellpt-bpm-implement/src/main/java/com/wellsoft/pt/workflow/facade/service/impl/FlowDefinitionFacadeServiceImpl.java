/*
 * @(#)2021年7月14日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.dto.FlowDefinitionDto;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.dto.WfFlowDefinitionCleanupConfigDto;
import com.wellsoft.pt.workflow.entity.FlowCategory;
import com.wellsoft.pt.workflow.entity.WfFlowDefinitionDeleteLogEntity;
import com.wellsoft.pt.workflow.enums.EnumFlowDefinitionDeleteStatus;
import com.wellsoft.pt.workflow.enums.EnumFlowDefinitionDeleteType;
import com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService;
import com.wellsoft.pt.workflow.facade.service.WfFlowDefinitionCleanupConfigFacadeService;
import com.wellsoft.pt.workflow.service.FlowCategoryService;
import com.wellsoft.pt.workflow.service.FlowDefineService;
import com.wellsoft.pt.workflow.service.WfFlowDefinitionDeleteLogService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
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
@Service
public class FlowDefinitionFacadeServiceImpl implements FlowDefinitionFacadeService {

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    @Autowired
    private FlowCategoryService flowCategoryService;

    @Autowired
    private FlowDefineService flowDefineService;

    @Autowired
    private WfFlowDefinitionCleanupConfigFacadeService flowDefinitionCleanupConfigFacadeService;

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private WfFlowDefinitionDeleteLogService flowDefinitionDeleteLogService;

    /**
     * @param flowDefId
     * @return
     */
    @Override
    public FlowDefinitionDto getById(String flowDefId) {
        FlowDefinitionDto dto = new FlowDefinitionDto();
        FlowDefinition entity = flowDefinitionService.getById(flowDefId);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(entity);
            dto.setXmlDefinition(flowDelegate.isXmlDefinition());
        }
        return dto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService#logicalDelete(java.lang.String)
     */
    @Override
    @Transactional
    public void logicalDelete(String uuid) {
        // 记录删除日志
        logFlowDefinitionDelete(uuid, EnumFlowDefinitionDeleteType.logicalDelete);

        FlowDefinition flowDefinition = flowDefinitionService.getOne(uuid);
        flowDefinition.setDeleteTime(Calendar.getInstance().getTime());
        flowDefinition.setDeleteStatus(EnumFlowDefinitionDeleteStatus.logicalDeleted.getCode());
        flowDefinition.setEnabled(false);
        flowDefinitionService.save(flowDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService#logicalDeleteAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void logicalDeleteAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            logicalDelete(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService#recovery(java.lang.String)
     */
    @Override
    @Transactional
    public void recovery(String uuid) {
        FlowDefinition flowDefinition = flowDefinitionService.getOne(uuid);
        flowDefinition.setDeleteTime(null);
        flowDefinition.setDeleteStatus(EnumFlowDefinitionDeleteStatus.normal.getCode());
        flowDefinitionService.save(flowDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService#recoveryAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void recoveryAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            recovery(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService#physicalDelete(java.lang.String)
     */
    @Override
    @Transactional
    public void physicalDelete(String uuid) {
        // 记录删除日志
        logFlowDefinitionDelete(uuid, EnumFlowDefinitionDeleteType.physicalDelete);
        flowDefineService.remove(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService#physicalDeleteAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void physicalDeleteAll(Collection<String> uuids) {
        List<String> usedFlowDefNames = Lists.newArrayList();
        for (String uuid : uuids) {
            if (flowInstanceService.isFlowDefInUse(uuid)) {
                usedFlowDefNames.add(flowDefineService.get(uuid).getName());
            } else {
                // 记录删除日志
                logFlowDefinitionDelete(uuid, EnumFlowDefinitionDeleteType.physicalDelete);
            }
        }
        if (CollectionUtils.isNotEmpty(usedFlowDefNames)) {
            throw new RuntimeException(
                    "流程定义已经被使用，无法彻底删除! 包括：" + StringUtils.join(usedFlowDefNames, Separator.SEMICOLON.getValue()));
        }
        flowDefineService.removeAll(uuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService#isEnableCleanup()
     */
    @Override
    public boolean isEnableCleanup() {
        WfFlowDefinitionCleanupConfigDto dto = flowDefinitionCleanupConfigFacadeService.getDto();
        return BooleanUtils.isTrue(dto.getEnabled());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService#listByDeleteStatus(int, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public List<FlowDefinition> listByDeleteStatus(int deleteStatus, PagingInfo pagingInfo) {
        String hql = "from FlowDefinition t where t.deleteStatus = :deleteStatus order by t.deleteTime asc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("deleteStatus", deleteStatus);
        List<FlowDefinition> definitions = flowDefinitionService.listByHQLAndPage(hql, values, pagingInfo);
        return definitions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService#listByDeleteStatusAndSystemUnitId(int, java.lang.String, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public List<FlowDefinition> listByDeleteStatusAndSystemUnitId(int deleteStatus, String systemUnitId,
                                                                  PagingInfo pagingInfo) {
        String hql = "from FlowDefinition t where t.deleteStatus = :deleteStatus and t.systemUnitId = :systemUnitId order by t.deleteTime asc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("deleteStatus", deleteStatus);
        values.put("systemUnitId", systemUnitId);
        List<FlowDefinition> definitions = flowDefinitionService.listByHQLAndPage(hql, values, pagingInfo);
        return definitions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService#isFlowDefinitionInUse(java.lang.String)
     */
    @Override
    public boolean isFlowDefinitionInUse(String uuid) {
        return flowInstanceService.isFlowDefInUse(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService#updateDeleteStatusByUuid(java.lang.String, java.lang.Integer)
     */
    @Override
    @Transactional
    public void updateDeleteStatusByUuid(String uuid, Integer deleteStatus) {
        String hql = "update FlowDefinition t set t.deleteStatus = :deleteStatus where t.uuid = :uuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("deleteStatus", deleteStatus);
        values.put("uuid", uuid);
        flowDefinitionService.updateByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService#autoCleanUp(java.lang.String)
     */
    @Override
    @Transactional
    public void autoCleanUp(String uuid) {
        // 物理删除
        physicalDelete(uuid);
    }

    /**
     * @param uuid
     * @param deleteType
     */
    private void logFlowDefinitionDelete(String uuid, EnumFlowDefinitionDeleteType deleteType) {
        FlowDefinition flowDefinition = flowDefinitionService.getOne(uuid);
        WfFlowDefinitionDeleteLogEntity entity = new WfFlowDefinitionDeleteLogEntity();
        entity.setFlowDefUuid(flowDefinition.getUuid());
        entity.setFlowDefName(flowDefinition.getName());
        entity.setFlowDefId(flowDefinition.getId());
        entity.setFlowDefVersion(flowDefinition.getVersion());
        entity.setCategoryUuid(flowDefinition.getCategory());
        if (StringUtils.isNotBlank(flowDefinition.getCategory())) {
            FlowCategory flowCategory = flowCategoryService.getOne(flowDefinition.getCategory());
            if (flowCategory != null) {
                entity.setCategoryName(flowCategory.getName());
            }
        }
        entity.setFormUuid(flowDefinition.getFormUuid());
        entity.setFormName(flowDefinition.getFormName());
        entity.setDeleteTime(Calendar.getInstance().getTime());
        entity.setDeleteType(deleteType.getCode());
        flowDefinitionDeleteLogService.save(entity);
    }

}
