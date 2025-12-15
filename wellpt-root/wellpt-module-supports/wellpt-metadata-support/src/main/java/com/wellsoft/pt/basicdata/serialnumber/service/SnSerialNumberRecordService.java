/*
 * @(#)7/22/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.service;

import com.wellsoft.pt.basicdata.serialnumber.dao.SnSerialNumberRecordDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberRecordEntity;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberInfo;
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
public interface SnSerialNumberRecordService extends JpaService<SnSerialNumberRecordEntity, SnSerialNumberRecordDao, String> {

    /**
     * 保存流水号记录
     *
     * @param relationUuid
     * @param maintainUuid
     * @param serialNumberInfo
     * @return
     */
    String save(String relationUuid, String maintainUuid, SerialNumberInfo serialNumberInfo);

    /**
     * 获取最小的流水号补号记录
     *
     * @param serialNumberDefId
     * @param maintainUuid
     * @return
     */
    SnSerialNumberRecordEntity getMinimalMissingSerialNumberRecord(String serialNumberDefId, String maintainUuid);

    /**
     * 根据UUID更新关系表UUID
     *
     * @param uuid
     * @param relationUuid
     */
    void updateRelationUuidByUuid(String uuid, String relationUuid);


    /**
     * 获取可补的流水号记录
     *
     * @param serialNumberDefId
     * @param maintainUuids
     * @return
     */
    List<SnSerialNumberRecordEntity> listAvailableSerialNumberRecord(String serialNumberDefId, String... maintainUuids);


    /**
     * 是否不存在流水号
     *
     * @param recordUuid
     * @param serialNumberDefId
     * @param maintainUuid
     * @return
     */
    boolean isMissingSerialNumberRecord(String recordUuid, String serialNumberDefId, String maintainUuid);

    /**
     * 是否不存在流水号
     *
     * @param serialNo
     * @param serialNumberDefId
     * @param maintainUuid
     * @return
     */
    boolean isMissingSerialNo(String serialNo, String serialNumberDefId, String maintainUuid);

    /**
     * 列出跳过的指针
     *
     * @param initialValue
     * @param increment
     * @param currentPointer
     * @param maintainUuid
     * @return
     */
    List<Long> listSkipPointers(Integer initialValue, Integer increment, Long currentPointer, String maintainUuid);

    /**
     * 根据指针及流水号维护UUID，判断指针是否存在
     *
     * @param pointer
     * @param maintainUuid
     * @return
     */
    boolean isExistsPointer(Long pointer, String maintainUuid);

    /**
     * 根据流水号关系删除不存在的记录
     *
     * @param relationUuid
     * @param objectName
     * @param fieldName
     */
    void deleteAbsentRecordByRelation(String relationUuid, String objectName, String fieldName);
}
