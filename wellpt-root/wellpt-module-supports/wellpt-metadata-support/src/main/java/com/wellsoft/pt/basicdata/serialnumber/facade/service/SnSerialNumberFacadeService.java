/*
 * @(#)7/21/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberDefinitionDto;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberRecordDto;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberBuildParams;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberInfo;

import java.util.List;

/**
 * Description: 流水号对外接口
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
public interface SnSerialNumberFacadeService extends Facade {

    /**
     * 生成流水号
     *
     * @param params
     * @return
     */
    SerialNumberInfo generateSerialNumber(SerialNumberBuildParams params);

    /**
     * 生成指定指针的流水号
     *
     * @param params
     * @return
     */
    ResultMessage generateSerialNumberWithPointer(Long pointer, SerialNumberBuildParams params);

    /**
     * 获取可补的流水号记录(只能被当前重围周期内的数据)
     *
     * @param params
     * @return
     */
    List<SnSerialNumberRecordDto> listAvailableSerialNumber(SerialNumberBuildParams params);

    /**
     * 检测流水号是否被占用
     *
     * @param pointer
     * @param params
     * @return
     */
    boolean checkIsOccupied(Long pointer, SerialNumberBuildParams params);


    /**
     * 根据流水号定义ID，获取流水号信息
     *
     * @param serialNumberDefId
     * @return
     */
    SnSerialNumberDefinitionDto getSerialNumberDefinitionById(String serialNumberDefId);

    /**
     * 根据流水号分类UUID或流水号定义ID获取流水号定义列表，并进行使用人权限过滤
     *
     * @param categoryUuidOrIds
     * @return
     */
    List<SnSerialNumberDefinitionDto> listByCategoryUuidOrId(List<String> categoryUuidOrIds);
}
