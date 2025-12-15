/*
 * @(#)7/21/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.service;

import com.wellsoft.pt.basicdata.serialnumber.dao.SnSerialNumberMaintainDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberMaintainEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface SnSerialNumberMaintainService extends JpaService<SnSerialNumberMaintainEntity, SnSerialNumberMaintainDao, String> {
    /**
     * 根据流水号定义UUID，不包含重置规则，获取流水号维护
     *
     * @param serialNumberDefUuid
     * @return
     */
    SnSerialNumberMaintainEntity getOneBySerialNumberDefUuidWithoutPointerResetRule(String serialNumberDefUuid);

    /**
     * 根据流水号定义UUID及重置规则，获取流水号维护
     *
     * @param serialNumberDefUuid
     * @param pointerResetType
     * @param pointerResetRule
     * @param pointResetRuleValue
     * @return
     */
    SnSerialNumberMaintainEntity getOneBySerialNumberDefUuidAndPointerResetRule(String serialNumberDefUuid, String pointerResetType, String pointerResetRule, String pointResetRuleValue);

    /**
     * 根据流水号定义UUID获取流水号维护数量
     *
     * @param serialNumberDefUuid
     * @return
     */
    long countBySerialNumberDefUuid(String serialNumberDefUuid);

    /**
     * 更新流水号维护指针初始值
     *
     * @param initialValueMap <uuid, initialValue>
     */
    void updateInitialValue(Map<String, String> initialValueMap);

    /**
     * 根据流水号定义UUID获取流水号维护
     *
     * @param serialNumberDefUuid
     * @return
     */
    List<SnSerialNumberMaintainEntity> listBySerialNumberDefUuid(String serialNumberDefUuid);
}
