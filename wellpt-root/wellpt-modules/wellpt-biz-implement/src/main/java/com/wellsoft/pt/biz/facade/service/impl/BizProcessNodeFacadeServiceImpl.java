/*
 * @(#)10/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.biz.dto.BizProcessItemInstanceDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeDataDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeDataRequestParamDto;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessNodeInstanceEntity;
import com.wellsoft.pt.biz.facade.service.BizProcessNodeFacadeService;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceService;
import com.wellsoft.pt.biz.service.BizProcessNodeInstanceService;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
 * 10/18/22.1	zhulh		10/18/22		Create
 * </pre>
 * @date 10/18/22
 */
@Service
public class BizProcessNodeFacadeServiceImpl extends AbstractApiFacade implements BizProcessNodeFacadeService {

    @Autowired
    private BizProcessNodeInstanceService bizProcessNodeInstanceService;

    @Autowired
    private BizProcessItemInstanceService bizProcessItemInstanceService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * 根据过程节点实例UUID获取过程节点办件实例数据
     *
     * @param processNodeInstUuid
     * @return
     */
    @Override
    public BizProcessNodeDataDto getProcessNodeByUuid(String processNodeInstUuid) {
        BizProcessNodeInstanceEntity nodeInstanceEntity = bizProcessNodeInstanceService.getOne(processNodeInstUuid);
        // 过程节点配置
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(nodeInstanceEntity.getProcessDefUuid());
        ProcessNodeConfig processNodeConfig = parser.getProcessNodeConfigById(nodeInstanceEntity.getId());

        // 表单信息
        String formUuid = nodeInstanceEntity.getFormUuid();
        String dataUuid = nodeInstanceEntity.getDataUuid();

        BizProcessNodeDataDto processNodeDataDto = new BizProcessNodeDataDto();
        processNodeDataDto.setTitle(nodeInstanceEntity.getTitle());
        processNodeDataDto.setProcessNodeInstUuid(processNodeInstUuid);
        processNodeDataDto.setItemPlaceHolder(processNodeConfig.getFormConfig().getItemPlaceHolder());
        processNodeDataDto.setFormUuid(formUuid);
        processNodeDataDto.setDataUuid(dataUuid);
        processNodeDataDto.setFormConfig(processNodeConfig.getFormConfig());
        return processNodeDataDto;
    }

    /**
     * 获取过程节点办件实例数据
     *
     * @param requestParamDto
     * @return
     */
    @Override
    public BizProcessNodeDataDto getProcessNodeData(BizProcessNodeDataRequestParamDto requestParamDto) {
        BizProcessNodeInstanceEntity nodeInstanceEntity = bizProcessNodeInstanceService.getOne(requestParamDto.getProcessNodeInstUuid());
        // 过程节点配置
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(nodeInstanceEntity.getProcessDefUuid());
        ProcessNodeConfig processNodeConfig = parser.getProcessNodeConfigById(nodeInstanceEntity.getId());

        // 表单信息
        String formUuid = nodeInstanceEntity.getFormUuid();
        String dataUuid = nodeInstanceEntity.getDataUuid();
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);

        BizProcessNodeDataDto processNodeDataDto = new BizProcessNodeDataDto();
        processNodeDataDto.setTitle(nodeInstanceEntity.getTitle());
        processNodeDataDto.setProcessNodeInstUuid(nodeInstanceEntity.getUuid());
        processNodeDataDto.setItemPlaceHolder(processNodeConfig.getFormConfig().getItemPlaceHolder());
        processNodeDataDto.setFormUuid(formUuid);
        processNodeDataDto.setDataUuid(dataUuid);
        processNodeDataDto.setDyFormData(dyFormData);
        processNodeDataDto.setFormConfig(processNodeConfig.getFormConfig());
        return processNodeDataDto;
    }

    /**
     * 保存过程节点办件实例数据
     *
     * @param processNodeDataDto
     * @return
     */
    @Override
    public String save(BizProcessNodeDataDto processNodeDataDto) {
        String processNodeInstUuid = processNodeDataDto.getProcessNodeInstUuid();
        if (StringUtils.isBlank(processNodeInstUuid)) {
            throw new BusinessException("过程节点实例不存在，不能保存过程节点数据！");
        }
        DyFormData dyFormData = processNodeDataDto.getDyFormData();
        dyFormFacade.saveFormData(dyFormData);
        return processNodeInstUuid;
    }

    /**
     * 根据过程节点实例UUID获取业务事项实例列表
     *
     * @param processNodeInstUuid
     * @return
     */
    @Override
    public List<BizProcessItemInstanceDto> listProcessItemInstanceByUuid(String processNodeInstUuid) {
        List<BizProcessItemInstanceEntity> processItemInstanceEntities = bizProcessItemInstanceService.listByProcessNodeInstUuid(processNodeInstUuid);
        List<BizProcessItemInstanceDto> dtos = BeanUtils.copyCollection(processItemInstanceEntities, BizProcessItemInstanceDto.class);
        return dtos;
    }
}
