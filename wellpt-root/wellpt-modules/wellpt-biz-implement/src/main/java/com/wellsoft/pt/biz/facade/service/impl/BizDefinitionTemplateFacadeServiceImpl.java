/*
 * @(#)11/22/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.biz.dto.BizDefinitionTemplateDto;
import com.wellsoft.pt.biz.entity.BizDefinitionTemplateEntity;
import com.wellsoft.pt.biz.facade.service.BizDefinitionTemplateFacadeService;
import com.wellsoft.pt.biz.query.BizDefinitionTemplateQueryItem;
import com.wellsoft.pt.biz.service.BizDefinitionTemplateService;
import com.wellsoft.pt.biz.service.BizProcessDefinitionService;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
 * 11/22/22.1	zhulh		11/22/22		Create
 * </pre>
 * @date 11/22/22
 */
@Service
public class BizDefinitionTemplateFacadeServiceImpl extends AbstractApiFacade implements BizDefinitionTemplateFacadeService {

    @Autowired
    private BizDefinitionTemplateService definitionTemplateService;

    @Autowired
    private BizProcessDefinitionService processDefinitionService;

    /**
     * 根据UUID获取业务流程配置项模板
     *
     * @param uuid
     * @return
     */
    @Override
    public BizDefinitionTemplateDto getDto(String uuid) {
        BizDefinitionTemplateDto dto = new BizDefinitionTemplateDto();
        BizDefinitionTemplateEntity entity = definitionTemplateService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * 保存业务流程配置项模板
     *
     * @param dto
     */
    @Override
    public void saveDto(BizDefinitionTemplateDto dto) {
        BizDefinitionTemplateEntity entity = new BizDefinitionTemplateEntity();
        if (StringUtils.isNotBlank(dto.getUuid())) {
            entity = definitionTemplateService.getOne(dto.getUuid());
        }
        BeanUtils.copyProperties(dto, entity, JpaEntity.BASE_FIELDS);
        definitionTemplateService.save(entity);
    }

    /**
     * 根据UUID列表删除业务流程配置项模板
     *
     * @param uuids
     */
    @Override
    public void deleteAll(List<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }

        // 判断业务流程配置项模板是否被使用
        if (isUsed(uuids)) {
            throw new BusinessException("业务流程配置项模板已经被引用，无法删除！");
        }

        definitionTemplateService.deleteByUuids(uuids);
    }

    @Override
    public void deleteByIdAndTypeAndProcessDefUuid(String id, String type, String processDefUuid) {
        definitionTemplateService.deleteByIdAndTypeAndProcessDefUuid(id, type, processDefUuid);
        ProcessDefinitionUtils.clearCache(processDefUuid);
    }

    /**
     * 判断业务流程配置项模板是否被使用
     *
     * @param uuids
     * @return
     */
    private boolean isUsed(List<String> uuids) {
        return processDefinitionService.isUsedDefinitionTemplateByTemplateUuids(uuids);
    }

    /**
     * select2查询接口
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        String processDefUuid = queryInfo.getOtherParams("processDefUuid");
        String templateType = queryInfo.getOtherParams("templateType");
        String selectedTemplateUuid = queryInfo.getOtherParams("selectedTemplateUuid");
        if (StringUtils.isBlank(processDefUuid) || StringUtils.isBlank(templateType)) {
            return new Select2QueryData();
        }

        String hql = "select t.uuid as uuid, t.name as name from BizDefinitionTemplateEntity t where 1 = 1 ";
        if (StringUtils.isNotBlank(selectedTemplateUuid)) {
            hql += "and (t.uuid = :templateUuid or (t.processDefUuid = :processDefUuid and t.type = :type)) order by t.modifyTime desc";
        } else {
            hql += "and t.processDefUuid = :processDefUuid and t.type = :type order by t.modifyTime desc";
        }
        Map<String, Object> values = Maps.newHashMap();
        values.put("processDefUuid", processDefUuid);
        values.put("type", templateType);
        values.put("templateUuid", selectedTemplateUuid);
        List<BizDefinitionTemplateQueryItem> items = this.definitionTemplateService.listItemHqlQuery(hql, BizDefinitionTemplateQueryItem.class, values);
        return new Select2QueryData(items, "uuid", "name");
    }

    /**
     * 通过ID查找Text.对于远程查找分页需要实现，否则无法设置选中。
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        List<String> uuids = Arrays.asList(queryInfo.getIds());
        if (CollectionUtils.isEmpty(uuids)) {
            return new Select2QueryData();
        }

        String hql = "select t.uuid as uuid, t.name as name from BizDefinitionTemplateEntity t where t.uuid in(:uuids) order by t.modifyTime desc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuids", uuids);
        List<BizDefinitionTemplateQueryItem> items = this.definitionTemplateService.listItemHqlQuery(hql, BizDefinitionTemplateQueryItem.class, values);
        return new Select2QueryData(items, "uuid", "name");
    }
}
