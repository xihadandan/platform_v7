/*
 * @(#)7/22/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.service.impl;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/22/22.1	zhulh		7/22/22		Create
 * </pre>
 * @date 7/22/22
 */

import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.pt.basicdata.serialnumber.dao.SnSerialNumberRelationDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberRelationEntity;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberRecordService;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberRelationService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class SnSerialNumberRelationServiceImpl extends AbstractJpaServiceImpl<SnSerialNumberRelationEntity, SnSerialNumberRelationDao, String>
        implements SnSerialNumberRelationService {

    @Autowired
    private SnSerialNumberRecordService serialNumberRecordService;

    /**
     * 根据流水号定义ID、对象类型、对象名及字段名，获取关系表
     *
     * @param snId
     * @param objectType
     * @param objectName
     * @param fieldName
     * @return
     */
    @Override
    public SnSerialNumberRelationEntity getOne(String snId, Integer objectType, String objectName, String fieldName) {
        SnSerialNumberRelationEntity entity = new SnSerialNumberRelationEntity();
        entity.setSnId(snId);
        entity.setObjectType(objectType);
        entity.setObjectName(objectName);
        entity.setFieldName(fieldName);
        List<SnSerialNumberRelationEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 根据流水号定义ID、对象类型、对象名及字段名，保存关系表
     *
     * @param snId
     * @param objectType
     * @param objectName
     * @param fieldName
     * @return
     */
    @Override
    @Transactional
    public String save(String snId, Integer objectType, String objectName, String fieldName) {
        Assert.hasLength(objectName, "对象名称不能为空!");
        Assert.hasLength(fieldName, "字段名不能为空!");
        if (StringUtils.equals("null", objectName)) {
            throw new BusinessException(String.format("对象名称不能为%s!", objectName));
        }
        if (StringUtils.equals("null", fieldName)) {
            throw new BusinessException(String.format("字段名不能为空%s!", fieldName));
        }
        SnSerialNumberRelationEntity entity = new SnSerialNumberRelationEntity();
        entity.setSnId(snId);
        entity.setObjectType(objectType);
        entity.setObjectName(objectName);
        entity.setFieldName(fieldName);
        this.dao.save(entity);
        return entity.getUuid();
    }

    /**
     * 根据流水号定义ID获取所有关系表
     *
     * @param snId
     * @return
     */
    @Override
    public List<SnSerialNumberRelationEntity> listBySnId(String snId) {
        return this.dao.listByFieldEqValue("snId", snId);
    }

    /**
     * 删除不存在的流水号记录
     *
     * @param serialNumberRelation
     */
    @Override
    @Transactional
    public void deleteAbsentRecord(SnSerialNumberRelationEntity serialNumberRelation) {
        serialNumberRecordService.deleteAbsentRecordByRelation(serialNumberRelation.getUuid(), serialNumberRelation.getObjectName(), serialNumberRelation.getFieldName());
    }

}
