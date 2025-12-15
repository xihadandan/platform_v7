/*
 * @(#)5/5/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.Entity;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.material.entity.CdMaterialDefinitionEntity;
import com.wellsoft.pt.basicdata.material.entity.CdMaterialEntity;
import com.wellsoft.pt.basicdata.material.entity.CdMaterialHisEntity;
import com.wellsoft.pt.basicdata.material.facade.service.CdMaterialFacadeService;
import com.wellsoft.pt.basicdata.material.service.CdMaterialDefinitionService;
import com.wellsoft.pt.basicdata.material.service.CdMaterialHisService;
import com.wellsoft.pt.basicdata.material.service.CdMaterialService;
import com.wellsoft.pt.basicdata.material.support.CdMaterialParams;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
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
 * 5/5/23.1	zhulh		5/5/23		Create
 * </pre>
 * @date 5/5/23
 */
@Service
public class CdMaterialFacadeServiceImpl implements CdMaterialFacadeService {

    @Autowired
    private CdMaterialService materialService;

    @Autowired
    private CdMaterialHisService materialHisService;

    @Autowired
    private CdMaterialDefinitionService materialDefinitionService;

    /**
     * 生成业务材料
     *
     * @param materialParams
     */
    @Override
    @Transactional
    public List<Long> generateMaterial(CdMaterialParams materialParams) {
        if (materialParams.isUpdate()) {
            return updateMaterial(materialParams);
        }
        return createMaterial(materialParams);
    }

    /**
     * 生成业务材料实例
     *
     * @param bizMaterialParams
     * @return
     */
    private List<Long> createMaterial(CdMaterialParams materialParams) {
        List<Long> bizMaterialUuids = Lists.newArrayList();
        List<String> materialCodes = materialParams.getMaterialCodes();
        if (CollectionUtils.isEmpty(materialCodes)) {
            return Collections.emptyList();
        }

        materialCodes.forEach(materialCode -> {
            Long uuid = saveMaterial(materialCode, materialParams);
            if (uuid != null) {
                bizMaterialUuids.add(uuid);
            }
        });
        return bizMaterialUuids;
    }

    /**
     * 保存业务材料
     *
     * @param materialCode
     * @param materialParams
     * @return
     */
    private Long saveMaterial(String materialCode, CdMaterialParams materialParams) {
        CdMaterialDefinitionEntity definitionEntity = materialDefinitionService.getByCode(materialCode);
        if (definitionEntity == null) {
            return null;
        }
        CdMaterialEntity entity = new CdMaterialEntity();
        entity.setMaterialDefUuid(definitionEntity.getUuid());
        entity.setMaterialName(definitionEntity.getName());
        entity.setMaterialCode(definitionEntity.getCode());
        entity.setBizType(materialParams.getBizType());
        entity.setBizId(materialParams.getBizId());
        entity.setDataUuid(materialParams.getDataUuid());
        entity.setPurpose(materialParams.getPurpose());
        entity.setOwnerId(StringUtils.join(materialParams.getOwnerIds(), Separator.SEMICOLON.getValue()));
        entity.setVersion(1d);
        entity.setRepoFileUuids(StringUtils.join(materialParams.getRepoFileUuids(), Separator.SEMICOLON.getValue()));
        entity.setValidationFlag(materialParams.getValidationFlag());
        entity.setAttributes(JsonUtils.object2Json(materialParams.getAttributes()));
        entity.setRemark(materialParams.getRemark());
        materialService.save(entity);

        // 保存历史版本
        saveMaterialHistory(entity);

        return entity.getUuid();
    }

    private void saveMaterialHistory(CdMaterialEntity materialEntity) {
        CdMaterialHisEntity hisEntity = new CdMaterialHisEntity();
        BeanUtils.copyProperties(materialEntity, hisEntity, Entity.BASE_FIELDS);
        hisEntity.setBizMaterialUuid(materialEntity.getUuid());
        materialHisService.save(hisEntity);
    }

    /**
     * 更新业务材料实例
     *
     * @param materialParams
     * @return
     */
    private List<Long> updateMaterial(CdMaterialParams materialParams) {
        List<Long> bizMaterialUuids = Lists.newArrayList();
        List<String> materialCodes = materialParams.getMaterialCodes();
        if (CollectionUtils.isEmpty(materialCodes)) {
            return Collections.emptyList();
        }

        materialCodes.forEach(materialCode -> {
            Long uuid = saveOrUpdateBizMaterial(materialCode, materialParams);
            if (uuid != null) {
                bizMaterialUuids.add(uuid);
            }
        });
        return bizMaterialUuids;
    }

    /**
     * 保存或更新业务材料
     *
     * @param materialCode
     * @param materialParams
     * @return
     */
    private Long saveOrUpdateBizMaterial(String materialCode, CdMaterialParams materialParams) {
        String dataUuid = materialParams.getDataUuid();
        String purpose = materialParams.getPurpose();
        CdMaterialEntity entity = materialService.getByMaterialCodeAndDataUuidAndPurpose(materialCode, dataUuid, purpose);
        // 业务材料不存在，保存新的业务材料
        if (entity == null) {
            return saveMaterial(materialCode, materialParams);
        }

        // 已经存在附件，进行添加处理
        List<String> existFileIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(entity.getRepoFileUuids())) {
            existFileIds.addAll(Arrays.asList(StringUtils.split(entity.getRepoFileUuids(), Separator.SEMICOLON.getValue())));
        }
        existFileIds.addAll(materialParams.getRepoFileUuids());

        // 更新材料
        entity.setBizType(materialParams.getBizType());
        entity.setBizId(materialParams.getBizId());
        entity.setDataUuid(materialParams.getDataUuid());
        entity.setPurpose(materialParams.getPurpose());
        entity.setOwnerId(StringUtils.join(materialParams.getOwnerIds(), Separator.SEMICOLON.getValue()));
        entity.setVersion(entity.getVersion() + 0.1);
        entity.setRepoFileUuids(StringUtils.join(existFileIds, Separator.SEMICOLON.getValue()));
        entity.setValidationFlag(materialParams.getValidationFlag());
        entity.setAttributes(JsonUtils.object2Json(materialParams.getAttributes()));
        entity.setRemark(materialParams.getRemark());
        materialService.save(entity);

        // 保存历史版本
        saveMaterialHistory(entity);

        return entity.getUuid();
    }

}
