/*
 * @(#)7/21/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.serialnumber.dao.SnSerialNumberMaintainDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberMaintainEntity;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberMaintainService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
 * 7/21/22.1	zhulh		7/21/22		Create
 * </pre>
 * @date 7/21/22
 */
@Service
public class SnSerialNumberMaintainServiceImpl extends AbstractJpaServiceImpl<SnSerialNumberMaintainEntity, SnSerialNumberMaintainDao, String>
        implements SnSerialNumberMaintainService {
    /**
     * 根据流水号定义UUID，不包含重围规则，获取流水号维护
     *
     * @param serialNumberDefUuid
     * @return
     */
    @Override
    public SnSerialNumberMaintainEntity getOneBySerialNumberDefUuidWithoutPointerResetRule(String serialNumberDefUuid) {
        String hql = "from SnSerialNumberMaintainEntity t where t.serialNumberDefUuid = :serialNumberDefUuid and t.pointerResetType is null";
        Map<String, Object> values = Maps.newHashMap();
        values.put("serialNumberDefUuid", serialNumberDefUuid);
        List<SnSerialNumberMaintainEntity> entities = this.dao.listByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 根据流水号定义UUID及重置规则，获取流水号维护
     *
     * @param serialNumberDefUuid
     * @param pointerResetType
     * @param pointerResetRule
     * @param pointResetRuleValue
     * @return
     */
    @Override
    public SnSerialNumberMaintainEntity getOneBySerialNumberDefUuidAndPointerResetRule(String serialNumberDefUuid, String pointerResetType, String pointerResetRule, String pointResetRuleValue) {
        SnSerialNumberMaintainEntity entity = new SnSerialNumberMaintainEntity();
        entity.setSerialNumberDefUuid(serialNumberDefUuid);
        entity.setPointerResetType(pointerResetType);
        entity.setPointerResetRule(pointerResetRule);
        entity.setPointerResetRuleValue(pointResetRuleValue);
        List<SnSerialNumberMaintainEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 根据流水号定义UUID获取流水号维护数量
     *
     * @param serialNumberDefUuid
     * @return
     */
    @Override
    public long countBySerialNumberDefUuid(String serialNumberDefUuid) {
        SnSerialNumberMaintainEntity entity = new SnSerialNumberMaintainEntity();
        entity.setSerialNumberDefUuid(serialNumberDefUuid);
        return this.dao.countByEntity(entity);
    }

    /**
     * 更新流水号维护指针初始值
     *
     * @param initialValueMap <uuid, initialValue>
     */
    @Override
    @Transactional
    public void updateInitialValue(Map<String, String> initialValueMap) {
        String hql = "update SnSerialNumberMaintainEntity t set t.initialValue = :initialValue where t.uuid = :uuid";
        for (Map.Entry<String, String> entry : initialValueMap.entrySet()) {
            Map<String, Object> values = Maps.newHashMap();
            values.put("uuid", entry.getKey());
            values.put("initialValue", entry.getValue());
            this.dao.updateByHQL(hql, values);
        }
    }

    /**
     * 根据流水号定义UUID获取流水号维护
     *
     * @param serialNumberDefUuid
     * @return
     */
    @Override
    public List<SnSerialNumberMaintainEntity> listBySerialNumberDefUuid(String serialNumberDefUuid) {
        Assert.hasLength(serialNumberDefUuid, "流水号定义UUID不能为空！");

        String hql = "from SnSerialNumberMaintainEntity t where t.serialNumberDefUuid = :serialNumberDefUuid order by pointerResetType asc, pointerResetRule asc, pointerResetRuleValue desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("serialNumberDefUuid", serialNumberDefUuid);
        return this.dao.listByHQL(hql, params);
    }

}
