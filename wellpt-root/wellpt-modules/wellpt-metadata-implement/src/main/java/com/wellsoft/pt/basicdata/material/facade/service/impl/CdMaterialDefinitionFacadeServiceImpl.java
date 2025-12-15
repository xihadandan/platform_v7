/*
 * @(#)4/28/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.Entity;
import com.wellsoft.pt.basicdata.material.dto.CdMaterialDefinitionDto;
import com.wellsoft.pt.basicdata.material.entity.CdMaterialDefinitionEntity;
import com.wellsoft.pt.basicdata.material.entity.CdMaterialDefinitionHisEntity;
import com.wellsoft.pt.basicdata.material.facade.service.CdMaterialDefinitionFacadeService;
import com.wellsoft.pt.basicdata.material.service.CdMaterialDefinitionHisService;
import com.wellsoft.pt.basicdata.material.service.CdMaterialDefinitionService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 4/28/23.1	zhulh		4/28/23		Create
 * </pre>
 * @date 4/28/23
 */
@Service
public class CdMaterialDefinitionFacadeServiceImpl implements CdMaterialDefinitionFacadeService {

    @Autowired
    private CdMaterialDefinitionService materialDefinitionService;

    @Autowired
    private CdMaterialDefinitionHisService materialDefinitionHisService;

    @Autowired
    private MongoFileService mongoFileService;


    /**
     * 获取材料定义
     *
     * @param uuid
     */
    @Override
    public CdMaterialDefinitionDto getDto(Long uuid) {
        CdMaterialDefinitionDto dto = new CdMaterialDefinitionDto();
        CdMaterialDefinitionEntity entity = materialDefinitionService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
            // 材料形式
            if (StringUtils.isNotBlank(entity.getMediumType())) {
                dto.setMediumTypes(Arrays.asList(StringUtils.split(entity.getMediumType(), Separator.SEMICOLON.getValue())));
            }
            // 电子材料格式
            if (StringUtils.isNotBlank(entity.getFormat())) {
                dto.setFormats(Arrays.asList(StringUtils.split(entity.getFormat(), Separator.SEMICOLON.getValue())));
            }
            // 附件
            List<LogicFileInfo> sampleFileInfos = mongoFileService.getNonioFilesFromFolder(entity.getUuid().toString(), "material_sample");
            dto.setSampleFileInfos(sampleFileInfos);
        }
        return dto;
    }

    @Override
    public List<String> listFormatByCodes(List<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return Collections.emptyList();
        }

        List<String> formats = materialDefinitionService.listFormatByCodes(codes);
        return formats.stream().flatMap(format -> {
            List<String> list = Lists.newLinkedList();
            if (StringUtils.isNotBlank(format)) {
                list.addAll(Arrays.asList(StringUtils.split(format, ";,")));
            }
            return list.stream();
        }).collect(Collectors.toList());
    }

    /**
     * 保存材料定义
     *
     * @param dto
     */
    @Override
    public void saveDto(CdMaterialDefinitionDto dto) {
        CdMaterialDefinitionEntity entity = new CdMaterialDefinitionEntity();
        // 保存新流水号定义 设置id值
        if (Objects.isNull(dto.getUuid())) {
            if (StringUtils.isNotBlank(dto.getCode()) && materialDefinitionService.existsByCode(dto.getCode())) {
                // 编码唯一性判断
                throw new RuntimeException("已经存在编码为[" + dto.getCode() + "]的材料定义!");
            }
            dto.setSystem(RequestSystemContextPathResolver.system());
            dto.setTenant(SpringSecurityUtils.getCurrentTenantId());
        } else {
            entity = this.materialDefinitionService.getOne(dto.getUuid());
            // 编码唯一性判断
            if (StringUtils.isNotBlank(dto.getCode()) && !materialDefinitionService.isUnique(dto.getUuid(), dto.getCode())) {
                throw new RuntimeException("已经存在编码为[" + dto.getCode() + "]的材料定义!");
            }
        }
        BeanUtils.copyProperties(dto, entity, Entity.BASE_FIELDS);

        // 材料形式
        if (CollectionUtils.isNotEmpty(dto.getMediumTypes())) {
            entity.setMediumType(StringUtils.join(dto.getMediumTypes(), Separator.SEMICOLON.getValue()));
        } else {
            entity.setMediumType(null);
        }

        // 电子材料格式
        if (CollectionUtils.isNotEmpty(dto.getFormats())) {
            entity.setFormat(StringUtils.join(dto.getFormats(), Separator.SEMICOLON.getValue()));
        } else {
            entity.setFormat(null);
        }

        this.materialDefinitionService.save(entity);

        // 样例文件放入夹
        mongoFileService.popAllFilesFromFolder(entity.getUuid().toString());
        if (StringUtils.isNotBlank(entity.getSampleRepoFileUuid())) {
            List<String> fileIds = Arrays.asList(StringUtils.split(entity.getSampleRepoFileUuid(), Separator.SEMICOLON.getValue()));
            mongoFileService.pushFilesToFolder(entity.getUuid().toString(), fileIds, "material_sample");
        }

        // 保存材料定义历史
        saveMaterialDefinitionHistory(entity);
    }

    /**
     * 保存材料定义历史
     *
     * @param entity
     */
    private void saveMaterialDefinitionHistory(CdMaterialDefinitionEntity entity) {
        CdMaterialDefinitionHisEntity hisEntity = new CdMaterialDefinitionHisEntity();
        BeanUtils.copyProperties(entity, hisEntity, Entity.BASE_FIELDS);
        hisEntity.setMaterialDefUuid(entity.getUuid());
        materialDefinitionHisService.save(hisEntity);
    }

    /**
     * 根据UUID，删除材料定义
     *
     * @param uuid
     */
    @Override
    public void deleteByUuid(Long uuid) {
        mongoFileService.popAllFilesFromFolder(uuid.toString());

        materialDefinitionService.delete(uuid);
    }

    /**
     * select2查询接口
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        String searchValue = queryInfo.getSearchValue();
        List<CdMaterialDefinitionEntity> entities = materialDefinitionService.queryByName(searchValue, queryInfo.getPagingInfo());
        return new Select2QueryData(entities, "code", "name");
    }

    /**
     * 通过ID查找Text.对于远程查找分页需要实现，否则无法设置选中。
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        return null;
    }

}
