/*
 * @(#)2018年9月25日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.script.facade.service.impl;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.pt.basicdata.script.dto.CdScriptDefinitionDto;
import com.wellsoft.pt.basicdata.script.dto.CdScriptVariableDefinitionDto;
import com.wellsoft.pt.basicdata.script.entity.CdScriptDefinitionEntity;
import com.wellsoft.pt.basicdata.script.facade.service.CdScriptDefinitionFacadeService;
import com.wellsoft.pt.basicdata.script.service.CdScriptDefinitionService;
import com.wellsoft.pt.basicdata.script.support.ScriptDefinition;
import com.wellsoft.pt.basicdata.script.support.ScriptVariableDefinition;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
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
 * 2018年9月25日.1	zhulh		2018年9月25日		Create
 * </pre>
 * @date 2018年9月25日
 */
@Service
public class CdScriptDefinitionFacadeServiceImpl implements CdScriptDefinitionFacadeService {

    @Autowired
    private CdScriptDefinitionService cdScriptDefinitionService;

    @Autowired
    private CommonValidateService commonValidateService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.script.facade.service.CdScriptDefinitionFacadeService#get(java.lang.String)
     */
    @Override
    public CdScriptDefinitionDto get(String uuid) {
        CdScriptDefinitionEntity cdScriptDefinitionEntity = cdScriptDefinitionService.getOne(uuid);
        CdScriptDefinitionDto cdScriptDefinitionDto = new CdScriptDefinitionDto();
        BeanUtils.copyProperties(cdScriptDefinitionEntity, cdScriptDefinitionDto);
        return cdScriptDefinitionDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.script.facade.service.CdScriptDefinitionFacadeService#save(com.wellsoft.pt.basicdata.script.dto.CdScriptDefinitionDto)
     */
    @Override
    @Transactional
    public void save(CdScriptDefinitionDto cdScriptDefinitionDto) {
        CdScriptDefinitionEntity scriptDefinitionEntity = new CdScriptDefinitionEntity();
        String uuid = cdScriptDefinitionDto.getUuid();
        String id = cdScriptDefinitionDto.getId();
        if (StringUtils.isNotBlank(uuid)) {
            scriptDefinitionEntity = cdScriptDefinitionService.getOne(uuid);
            // 类型非空唯一性判断
            if (StringUtils.isNotBlank(id)
                    && !commonValidateService.checkUnique(uuid, "cdScriptDefinitionEntity", "id", id)) {
                throw new RuntimeException("已经存在ID为[" + id + "]的脚本!");
            }
        } else if (StringUtils.isNotBlank(id)
                && commonValidateService.checkExists("cdScriptDefinitionEntity", "id", id)) {
            // 类型非空唯一性判断
            throw new RuntimeException("已经存在ID为[" + id + "]的脚本!");
        }

        BeanUtils.copyProperties(cdScriptDefinitionDto, scriptDefinitionEntity);

        cdScriptDefinitionService.save(scriptDefinitionEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.script.facade.service.CdScriptDefinitionFacadeService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(List<String> uuids) {
        cdScriptDefinitionService.deleteByUuids(uuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.context.component.select2.Select2QueryApi#loadSelectData(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        String type = queryInfo.getOtherParams("type", "");
        List<CdScriptDefinitionEntity> cdScriptDefinitionEntities = cdScriptDefinitionService.getAllByType(type);
        return new Select2QueryData(cdScriptDefinitionEntities, "id", "name", queryInfo.getPagingInfo());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.context.component.select2.Select2QueryApi#loadSelectDataByIds(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.script.facade.service.CdScriptDefinitionFacadeService#getScriptDefinitionById(java.lang.String)
     */
    @Override
    public ScriptDefinition getScriptDefinitionById(String scriptDefinitionId) {
        CdScriptDefinitionEntity cdScriptDefinitionEntity = cdScriptDefinitionService.getById(scriptDefinitionId);
        Collection<CdScriptVariableDefinitionDto> variableDefinitionDtos = cdScriptDefinitionEntity
                .getVariableDefinitionDtos();
        ScriptDefinition scriptDefinition = new ScriptDefinition();
        BeanUtils.copyProperties(cdScriptDefinitionEntity, scriptDefinition);
        List<ScriptVariableDefinition> variableDefinitions = new ArrayList<ScriptVariableDefinition>();
        for (CdScriptVariableDefinitionDto variableDefinitionDto : variableDefinitionDtos) {
            ScriptVariableDefinition variableDefinition = new ScriptVariableDefinition();
            BeanUtils.copyProperties(variableDefinitionDto, variableDefinition);
            variableDefinitions.add(variableDefinition);
        }
        scriptDefinition.setVariableDefinitions(variableDefinitions);
        return scriptDefinition;
    }
}
