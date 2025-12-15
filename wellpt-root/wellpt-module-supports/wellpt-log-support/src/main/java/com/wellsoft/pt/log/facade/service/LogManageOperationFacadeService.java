/*
 * @(#)2021-06-28 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.log.dto.LogManageOperationDto;
import com.wellsoft.pt.log.dto.SaveLogManageOperationDto;
import com.wellsoft.pt.log.entity.UserBehaviorLogEntity;
import com.wellsoft.pt.log.enums.DataParseTypeEnum;

/**
 * Description: 数据库表LOG_MANAGE_OPERATION的门面服务接口，提供给其他模块以及前端调用的业务接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-06-28.1	zenghw		2021-06-28		Create
 * </pre>
 * @date 2021-06-28
 */
public interface LogManageOperationFacadeService extends Facade {

    /**
     * 获取管理日志详情
     *
     * @param logManageOperationUuid
     * @return
     **/
    public LogManageOperationDto getLogManageOperation(String logManageOperationUuid);

    /**
     * 保存管理操作日志
     *
     * @param dto               管理操作日志对象
     * @param dataParseTypeEnum 解析类型枚举
     * @return void
     **/
    public void saveLogManageOperation(SaveLogManageOperationDto dto, DataParseTypeEnum dataParseTypeEnum);

    /**
     * 保存流程清单导出-管理日志
     *
     * @param
     * @return void
     **/
    public void saveFlowListExportLogManageOperation();

    public void saveUserBehaviorLog(UserBehaviorLogEntity entity);

}
