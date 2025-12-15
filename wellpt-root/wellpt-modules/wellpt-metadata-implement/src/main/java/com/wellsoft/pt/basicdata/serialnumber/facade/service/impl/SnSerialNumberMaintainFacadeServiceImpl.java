/*
 * @(#)7/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberMaintainDto;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberDefinitionEntity;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberMaintainEntity;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberRecordEntity;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SnSerialNumberMaintainFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberDefinitionService;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberMaintainService;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberRecordService;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberCheckResult;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
 * 7/27/22.1	zhulh		7/27/22		Create
 * </pre>
 * @date 7/27/22
 */
@Service
public class SnSerialNumberMaintainFacadeServiceImpl extends AbstractApiFacade implements SnSerialNumberMaintainFacadeService {

    @Autowired
    private SnSerialNumberMaintainService serialNumberMaintainService;

    @Autowired
    private SnSerialNumberRecordService snSerialNumberRecordService;

    @Autowired
    private SnSerialNumberDefinitionService snSerialNumberDefinitionService;

    /**
     * 根据流水号定义UUID获取流水号维护
     *
     * @param serialNumberDefUuid
     * @return
     */
    @Override
    public List<SnSerialNumberMaintainDto> listBySerialNumberDefUuid(String serialNumberDefUuid) {
        List<SnSerialNumberMaintainEntity> entities = serialNumberMaintainService.listBySerialNumberDefUuid(serialNumberDefUuid);
        return BeanUtils.copyCollection(entities, SnSerialNumberMaintainDto.class);
    }

    /**
     * 根据流水号维护UUID列表删除流水号维护
     *
     * @param uuids
     */
    @Override
    public void deleteAllByUuids(List<String> uuids) {
        serialNumberMaintainService.deleteByUuids(uuids);
    }

    /**
     * 根据流水号维护UUID,更新流水号维护指针
     *
     * @param uuid
     * @param pointer
     */
    @Override
    public void updatePointerByUuid(String uuid, String pointer) {
        if (StringUtils.isBlank(pointer) || !NumberUtils.isDigits(pointer)) {
            throw new BusinessException("无效的指针，请输入数字");
        }
        SnSerialNumberMaintainEntity entity = serialNumberMaintainService.getOne(uuid);
        entity.setPointer(pointer);
        serialNumberMaintainService.save(entity);
    }

    /**
     * 更新流水号维护指针初始值
     *
     * @param initialValueMap <uuid, initialValue>
     */
    @Override
    public void updateInitialValue(Map<String, String> initialValueMap) {
        serialNumberMaintainService.updateInitialValue(initialValueMap);
    }

    /**
     * 流水号检测结果
     *
     * @param serialNumberDefId
     * @param maintainUuids
     * @return
     */
    @Override
    public Map<String, SerialNumberCheckResult> checkSerialNumber(String serialNumberDefId, List<String> maintainUuids) {
        if (CollectionUtils.isEmpty(maintainUuids)) {
            return Collections.emptyMap();
        }

        Map<String, SerialNumberCheckResult> resultMap = Maps.newHashMap();

        // 可补号记录
        List<SnSerialNumberRecordEntity> recordEntities = snSerialNumberRecordService.listAvailableSerialNumberRecord(serialNumberDefId, maintainUuids.toArray(new String[0]));
        Map<String, List<SnSerialNumberRecordEntity>> fillRecordMap = ListUtils.list2group(recordEntities, "maintainUuid");

        SnSerialNumberDefinitionEntity definitionEntity = snSerialNumberDefinitionService.getById(serialNumberDefId);
        Integer increment = definitionEntity.getIncremental();
        List<SnSerialNumberMaintainEntity> maintainEntities = serialNumberMaintainService.listByUuids(maintainUuids);
        for (SnSerialNumberMaintainEntity entity : maintainEntities) {
            String maintainUuid = entity.getUuid();
            // 初始值
            Integer initialValue = Integer.valueOf(definitionEntity.getInitialValue());
            // 当前指针
            Long currentPointer = Long.valueOf(entity.getPointer());
            // 跳号指针
            List<Long> skipPointers = snSerialNumberRecordService.listSkipPointers(initialValue, increment, currentPointer, maintainUuid);
            // 可补记录
            List<SnSerialNumberRecordEntity> fileRecords = fillRecordMap.get(maintainUuid);
            List<String> fillSerialNos = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(fileRecords)) {
                // 可补记录指针加入跳号
                fileRecords.stream().forEach(r -> skipPointers.add(r.getPointer()));
                fillSerialNos = fileRecords.stream().map(r -> r.getSerialNo()).collect(Collectors.toList());

                // 指针排序
                Collections.sort(skipPointers);
            }

            SerialNumberCheckResult checkResult = new SerialNumberCheckResult();
            checkResult.setMaintainUuid(maintainUuid);
            checkResult.setSkipPointers(skipPointers);
            checkResult.setFillSerialNos(fillSerialNos);
            resultMap.put(maintainUuid, checkResult);
        }

        return resultMap;
    }


}
