/*
 * @(#)7/22/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.service;

import com.wellsoft.pt.basicdata.serialnumber.dao.SnSerialNumberRelationDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberRelationEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 7/22/22.1	zhulh		7/22/22		Create
 * </pre>
 * @date 7/22/22
 */
public interface SnSerialNumberRelationService extends JpaService<SnSerialNumberRelationEntity, SnSerialNumberRelationDao, String> {
    /**
     * 根据流水号定义ID、对象类型、对象名及字段名，获取关系表
     *
     * @param snId
     * @return
     */
    SnSerialNumberRelationEntity getOne(String snId, Integer objectType, String objectName, String fieldName);

    /**
     * 根据流水号定义ID、对象类型、对象名及字段名，保存关系表
     *
     * @param snId
     * @param objectType
     * @param objectName
     * @param fieldName
     * @return
     */
    String save(String snId, Integer objectType, String objectName, String fieldName);

    /**
     * 根据流水号定义ID获取所有关系表
     *
     * @param snId
     * @return
     */
    List<SnSerialNumberRelationEntity> listBySnId(String snId);

    /**
     * 删除不存在的流水号记录
     *
     * @param serialNumberRelation
     */
    void deleteAbsentRecord(SnSerialNumberRelationEntity serialNumberRelation);
}
