/*
 * @(#)10/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.biz.dto.BizProcessDataDto;
import com.wellsoft.pt.biz.dto.BizProcessDataRequestParamDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeInstanceDto;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessNodeInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumBizProcessNodeState;
import com.wellsoft.pt.biz.facade.service.BizProcessFacadeService;
import com.wellsoft.pt.biz.service.BizProcessInstanceService;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceService;
import com.wellsoft.pt.biz.service.BizProcessNodeInstanceService;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
 * 10/18/22.1	zhulh		10/18/22		Create
 * </pre>
 * @date 10/18/22
 */
@Service
public class BizProcessFacadeServiceImpl extends AbstractApiFacade implements BizProcessFacadeService {

    @Autowired
    private BizProcessInstanceService bizProcessInstanceService;

    @Autowired
    private BizProcessNodeInstanceService bizProcessNodeInstanceService;

    @Autowired
    private BizProcessItemInstanceService bizProcessItemInstanceService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * 根据业务流程实例UUID获取业务流程办件实例数据
     *
     * @param processInstUuid
     * @return
     */
    @Override
    public BizProcessDataDto getProcessByUuid(String processInstUuid) {
        BizProcessInstanceEntity processInstanceEntity = bizProcessInstanceService.getOne(processInstUuid);
        // 业务流程配置
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processInstanceEntity.getProcessDefUuid());
        String processNodePlaceHolder = parser.getProcessFormConfig().getProcessNodePlaceHolder();

        // 表单信息
        String formUuid = processInstanceEntity.getFormUuid();
        String dataUuid = processInstanceEntity.getDataUuid();

        BizProcessDataDto processDataDto = new BizProcessDataDto();
        processDataDto.setTitle(processInstanceEntity.getTitle());
        processDataDto.setProcessInstUuid(processInstUuid);
        processDataDto.setProcessNodePlaceHolder(processNodePlaceHolder);
        processDataDto.setFormUuid(formUuid);
        processDataDto.setDataUuid(dataUuid);
        processDataDto.setFormConfig(parser.getProcessFormConfig());
        return processDataDto;
    }

    /**
     * 获取业务流程办件实例数据
     *
     * @param requestParamDto
     * @return
     */
    @Override
    public BizProcessDataDto getProcessData(BizProcessDataRequestParamDto requestParamDto) {
        BizProcessInstanceEntity processInstanceEntity = bizProcessInstanceService.getOne(requestParamDto.getProcessInstUuid());
        // 业务流程配置
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processInstanceEntity.getProcessDefUuid());
        String processNodePlaceHolder = parser.getProcessFormConfig().getProcessNodePlaceHolder();

        // 表单数据
        String formUuid = processInstanceEntity.getFormUuid();
        String dataUuid = processInstanceEntity.getDataUuid();
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);

        BizProcessDataDto processDataDto = new BizProcessDataDto();
        processDataDto.setTitle(processInstanceEntity.getTitle());
        processDataDto.setProcessInstUuid(processInstanceEntity.getUuid());
        processDataDto.setProcessNodePlaceHolder(processNodePlaceHolder);
        processDataDto.setFormUuid(formUuid);
        processDataDto.setDataUuid(dataUuid);
        processDataDto.setDyFormData(dyFormData);
        processDataDto.setFormConfig(parser.getProcessFormConfig());
        return processDataDto;
    }

    /**
     * 保存业务流程办件实例数据
     *
     * @param processDataDto
     * @return
     */
    @Override
    public String save(BizProcessDataDto processDataDto) {
        String processInstUuid = processDataDto.getProcessInstUuid();
        if (StringUtils.isBlank(processInstUuid)) {
            throw new BusinessException("业务流程实例不存在，不能保存业务流程数据！");
        }
        DyFormData dyFormData = processDataDto.getDyFormData();
        dyFormFacade.saveFormData(dyFormData);
        return processInstUuid;
    }

    /**
     * 根据业务流程实例UUID获取过程结点实例列表
     *
     * @param processInstUuid
     * @param loadItemInstCount
     * @return
     */
    @Override
    public List<BizProcessNodeInstanceDto> listProcessNodeInstanceByUuid(String processInstUuid, boolean loadItemInstCount) {
        List<BizProcessNodeInstanceEntity> processNodeInstanceEntities = bizProcessNodeInstanceService.listByProcessInstUuid(processInstUuid);
        List<BizProcessNodeInstanceDto> dtos = BeanUtils.copyCollection(processNodeInstanceEntities, BizProcessNodeInstanceDto.class);
        List<String> processNodeInstUuids = Lists.newArrayList();
        Map<String, Long> countMap = null;
        if (loadItemInstCount && CollectionUtils.isNotEmpty(dtos)) {
            dtos.stream().forEach(node -> processNodeInstUuids.add(node.getUuid()));
            countMap = bizProcessItemInstanceService.getCountMapByProcessNodeInstUuids(processNodeInstUuids);
        }
        for (BizProcessNodeInstanceDto dto : dtos) {
            // 状态名称
            dto.setStateName(EnumBizProcessNodeState.getNameByValue(dto.getState()));
            // 业务事项办件数量
            if (countMap != null && countMap.containsKey(dto.getUuid())) {
                dto.setProcessItemInstCount(countMap.get(dto.getUuid()).intValue());
            }
        }
        return dtos;
    }

    /**
     * 获取业务主体主表数据
     *
     * @param entityFormUuid
     * @param entityIdValue
     * @param entityIdField
     * @return
     */
    @Override
    public Map<String, Object> getEntityFormDataOfMainform(String entityFormUuid, String entityIdValue, String entityIdField) {
        return bizProcessInstanceService.getEntityFormDataOfMainform(entityFormUuid, entityIdValue, entityIdField);
    }

}
