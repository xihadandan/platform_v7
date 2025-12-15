/*
 * @(#)7/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberMaintainDto;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberCheckResult;

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
 * 7/27/22.1	zhulh		7/27/22		Create
 * </pre>
 * @date 7/27/22
 */
public interface SnSerialNumberMaintainFacadeService extends Facade {

    /**
     * 根据流水号定义UUID获取流水号维护
     *
     * @param serialNumberDefUuid
     * @return
     */
    List<SnSerialNumberMaintainDto> listBySerialNumberDefUuid(String serialNumberDefUuid);

    /**
     * 根据流水号维护UUID列表,删除流水号维护
     *
     * @param uuids
     */
    void deleteAllByUuids(List<String> uuids);

    /**
     * 根据流水号维护UUID,更新流水号维护指针
     *
     * @param uuid
     * @param pointer
     */
    void updatePointerByUuid(String uuid, String pointer);

    /**
     * 更新流水号维护指针初始值
     *
     * @param initialValueMap <uuid, initialValue>
     */
    void updateInitialValue(Map<String, String> initialValueMap);

    /**
     * 流水号检测结果
     *
     * @param serialNumberDefId
     * @param maintainUuids
     * @return
     */
    Map<String, SerialNumberCheckResult> checkSerialNumber(String serialNumberDefId, List<String> maintainUuids);
}
