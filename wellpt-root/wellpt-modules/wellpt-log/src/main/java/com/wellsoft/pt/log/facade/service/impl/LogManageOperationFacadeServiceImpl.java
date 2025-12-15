/*
 * @(#)2021-06-28 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.facade.service.impl;

import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.log.dto.LogManageOperationDto;
import com.wellsoft.pt.log.dto.SaveLogManageOperationDto;
import com.wellsoft.pt.log.entity.LogManageDetailsEntity;
import com.wellsoft.pt.log.entity.LogManageOperationEntity;
import com.wellsoft.pt.log.entity.UserBehaviorLogEntity;
import com.wellsoft.pt.log.enums.DataParseTypeEnum;
import com.wellsoft.pt.log.enums.LogManageDataTypeEnum;
import com.wellsoft.pt.log.enums.LogManageModuleEnum;
import com.wellsoft.pt.log.enums.LogManageOperationEnum;
import com.wellsoft.pt.log.facade.service.LogManageOperationFacadeService;
import com.wellsoft.pt.log.service.LogManageDetailsService;
import com.wellsoft.pt.log.service.LogManageOperationService;
import com.wellsoft.pt.log.service.UserBehaviorLogService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 数据库表LOG_MANAGE_OPERATION的门面服务实现类
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
@Service
public class LogManageOperationFacadeServiceImpl extends AbstractApiFacade implements LogManageOperationFacadeService {

    @Autowired
    private LogManageOperationService logManageOperationService;
    @Autowired
    private LogManageDetailsService logManageDetailsService;
    @Autowired
    private UserBehaviorLogService userBehaviorLogService;

    @Override
    public LogManageOperationDto getLogManageOperation(String logManageOperationUuid) {
        LogManageOperationEntity logManageOperationEntity = logManageOperationService.getOne(logManageOperationUuid);
        if (logManageOperationEntity == null) {
            throw new BusinessException("管理日志不存在");
        }
        LogManageOperationDto logManageOperationDto = new LogManageOperationDto();
        BeanUtils.copyProperties(logManageOperationEntity, logManageOperationDto);
        LogManageDetailsEntity logManageDetailsEntity = new LogManageDetailsEntity();
        logManageDetailsEntity.setLogId(logManageOperationUuid);
        List<LogManageDetailsEntity> entityList = logManageDetailsService.listByEntity(logManageDetailsEntity);
        logManageOperationDto.setLogManageDetailsEntity(entityList);
        return logManageOperationDto;
    }

    @Override
    @Transactional
    public void saveLogManageOperation(SaveLogManageOperationDto dto, DataParseTypeEnum dataParseTypeEnum) {
        LogManageOperationEntity logManageOperationEntity = new LogManageOperationEntity();

        BeanUtils.copyProperties(dto, logManageOperationEntity);
        logManageOperationEntity.setDataParseType(dataParseTypeEnum.getValue());
        logManageOperationEntity.setUserId(SpringSecurityUtils.getCurrentUserId());
        logManageOperationEntity.setUserName(SpringSecurityUtils.getCurrentUserName());
        logManageOperationEntity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        logManageOperationService.save(logManageOperationEntity);
        logManageOperationService.flushSession();
        switch (dataParseTypeEnum) {
            case Entity:
                // logManageDetailsEntity 表直接保存
                List<LogManageDetailsEntity> entities = dto.getLogManageDetailsEntity();
                if (entities != null && entities.size() > 0) {
                    for (LogManageDetailsEntity logManageDetailsEntity : entities) {
                        logManageDetailsEntity.setLogId(logManageOperationEntity.getUuid());
                        logManageDetailsService.save(logManageDetailsEntity);
                    }
                    logManageDetailsService.flushSession();
                }
                break;
            case Xml:
                // logManageDetailsEntity 表不直接保存，留扩展口子
                break;
            case Json:
                // logManageDetailsEntity 表不直接保存，留扩展口子
                break;
            default:
                break;
        }
    }

    @Override
    public void saveFlowListExportLogManageOperation() {
        SaveLogManageOperationDto dto = new SaveLogManageOperationDto();
        dto.setDataType(LogManageDataTypeEnum.FlowDef.getName());
        dto.setDataTypeId(LogManageDataTypeEnum.FlowDef.getValue());
        dto.setModuleId(LogManageModuleEnum.FlowDef.getValue());
        dto.setModuleName(LogManageModuleEnum.FlowDef.getName());
        dto.setOperation(LogManageOperationEnum.flowListExport.getName());
        dto.setOperationId(LogManageOperationEnum.flowListExport.getValue());
        saveLogManageOperation(dto, DataParseTypeEnum.Xml);
    }

    @Override
    public void saveUserBehaviorLog(UserBehaviorLogEntity entity) {
        userBehaviorLogService.saveUserBehaviorLog(entity);
    }

}
