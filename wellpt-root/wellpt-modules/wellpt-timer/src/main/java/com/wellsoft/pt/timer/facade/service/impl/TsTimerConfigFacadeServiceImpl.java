/*
 * @(#)2021年6月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.dto.TsTimerConfigDto;
import com.wellsoft.pt.timer.entity.TsTimerConfigEntity;
import com.wellsoft.pt.timer.enums.EnumTimeLimitType;
import com.wellsoft.pt.timer.enums.EnumTimingMode;
import com.wellsoft.pt.timer.enums.EnumTimingModeType;
import com.wellsoft.pt.timer.enums.EnumTimingModeUnit;
import com.wellsoft.pt.timer.facade.service.TsTimerConfigFacadeService;
import com.wellsoft.pt.timer.service.TsTimerConfigService;
import com.wellsoft.pt.timer.support.TsTimerConfigUsedChecker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
 * 2021年6月7日.1	zhulh		2021年6月7日		Create
 * </pre>
 * @date 2021年6月7日
 */
@Service
public class TsTimerConfigFacadeServiceImpl implements TsTimerConfigFacadeService {

    @Autowired
    private TsTimerConfigService timerConfigService;

    @Autowired
    private List<TsTimerConfigUsedChecker> timerConfigUsedCheckers;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerConfigFacadeService#listBySystemUnitIds(java.util.List)
     */
    @Override
    public List<TsTimerConfigEntity> listBySystemUnitIds(List<String> systemUnitIds) {
        return timerConfigService.listBySystemUnitIds(systemUnitIds);
    }

    /**
     * @param system
     * @param tenant
     * @param excludedCategoryId
     * @return
     */
    @Override
    public List<TsTimerConfigEntity> listBySystemAndTenant(String system, String tenant, String excludedCategoryId) {
        return timerConfigService.listBySystemAndTenant(system, tenant, excludedCategoryId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerFacadeService#getTimerConfigDto(java.lang.String)
     */
    @Override
    public TsTimerConfigDto getDto(String uuid) {
        TsTimerConfigEntity timerConfigEntity = timerConfigService.getOne(uuid);
        TsTimerConfigDto timerConfigDto = new TsTimerConfigDto();
        if (timerConfigEntity != null) {
            BeanUtils.copyProperties(timerConfigEntity, timerConfigDto);
        }
        return timerConfigDto;
    }

    /**
     * 根据计时器配置ID获取计时器
     *
     * @param id
     * @return
     */
    @Override
    public TsTimerConfigDto getDtoById(String id) {
        TsTimerConfigEntity timerConfigEntity = timerConfigService.getById(id);
        TsTimerConfigDto timerConfigDto = new TsTimerConfigDto();
        if (timerConfigEntity != null) {
            BeanUtils.copyProperties(timerConfigEntity, timerConfigDto);
        }
        return timerConfigDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerConfigFacadeService#saveDto(com.wellsoft.pt.timer.dto.TsTimerConfigDto)
     */
    @Override
    public String saveDto(TsTimerConfigDto timerConfigDto) {
        TsTimerConfigEntity timerConfigEntity = new TsTimerConfigEntity();
        if (StringUtils.isNotBlank(timerConfigDto.getUuid())) {
            timerConfigEntity = timerConfigService.getOne(timerConfigDto.getUuid());
        } else {
            // ID唯一性判断
            if (this.timerConfigService.countById(timerConfigDto.getId()) > 0) {
                throw new RuntimeException("已经存在ID为[" + timerConfigDto.getId() + "]的计时服务!");
            }
            timerConfigDto.setSystem(RequestSystemContextPathResolver.system());
            timerConfigDto.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        BeanUtils.copyProperties(timerConfigDto, timerConfigEntity, IdEntity.BASE_FIELDS);
        String timingMode = timerConfigDto.getTimingMode();
        timerConfigEntity.setTimingModeType(getTimingModeType(timingMode, timerConfigEntity.getTimingModeType()));
        timerConfigEntity.setTimingModeTypeName(getTimingModeTypeName(timerConfigEntity.getTimingModeType()));
        timerConfigEntity.setTimingModeUnit(getTimingModeUnit(timingMode, timerConfigEntity.getTimingModeUnit()));
        timerConfigEntity.setTimingModeUnitName(getTimingModeUnitName(timerConfigEntity.getTimingModeUnit()));
        timerConfigEntity.setTimeLimitTypeName(getTimeLimitTypeName(timerConfigEntity.getTimeLimitType()));
        timerConfigService.save(timerConfigEntity);
        return timerConfigEntity.getUuid();
    }

    /**
     * @param timingMode
     * @return
     */
    private String getTimingModeType(String timingMode, String defaultTimingModeType) {
        EnumTimingMode enumTimingMode = EnumTimingMode.getByValue(timingMode);
        if (enumTimingMode == null) {
            return StringUtils.EMPTY;
        }
        String timingModeType = defaultTimingModeType;
        switch (enumTimingMode) {
            case WORKING_DAY:
            case WORKING_HOUR:
            case WORKING_MINUTE:
                timingModeType = EnumTimingModeType.WORKING_DAY.getValue();
                break;
            case WORKING_MINUTE_24:
            case WORKING_DAY_24:
            case WORKING_HOUR_24:
                timingModeType = EnumTimingModeType.WORKING_DAY_24.getValue();
                break;
            case DAY:
            case HOUR:
            case MINUTE:
                timingModeType = EnumTimingModeType.DAY.getValue();
                break;
            case CUSTOM:
                timingModeType = EnumTimingModeType.CUSTOM.getValue();
            default:
                break;
        }
        return timingModeType;
    }

    /**
     * @param timingModeType
     * @return
     */
    private String getTimingModeTypeName(String timingModeType) {
        String timingModeTypeName = null;
        if (StringUtils.equals(EnumTimingModeType.WORKING_DAY.getValue(), timingModeType)) {
            timingModeTypeName = EnumTimingModeType.WORKING_DAY.getName();
        } else if (StringUtils.equals(EnumTimingModeType.WORKING_DAY_24.getValue(), timingModeType)) {
            timingModeTypeName = EnumTimingModeType.WORKING_DAY_24.getName();
        } else if (StringUtils.equals(EnumTimingModeType.DAY.getValue(), timingModeType)) {
            timingModeTypeName = EnumTimingModeType.DAY.getName();
        } else if (StringUtils.equals(EnumTimingModeType.CUSTOM.getValue(), timingModeType)) {
            timingModeTypeName = EnumTimingModeType.CUSTOM.getName();
        }
        return timingModeTypeName;
    }

    /**
     * @param timingMode
     * @return
     */
    private String getTimingModeUnit(String timingMode, String defaultTimingModeUnit) {
        EnumTimingMode enumTimingMode = EnumTimingMode.getByValue(timingMode);
        if (enumTimingMode == null) {
            return StringUtils.EMPTY;
        }
        String timingModeUnit = defaultTimingModeUnit;
        switch (enumTimingMode) {
            case WORKING_DAY:
            case WORKING_DAY_24:
            case DAY:
                timingModeUnit = EnumTimingModeUnit.DAY.getValue();
                break;
            case WORKING_HOUR:
            case WORKING_HOUR_24:
            case HOUR:
                timingModeUnit = EnumTimingModeUnit.HOUR.getValue();
                break;
            case WORKING_MINUTE:
            case WORKING_MINUTE_24:
            case MINUTE:
                timingModeUnit = EnumTimingModeUnit.MINUTE.getValue();
                break;
            default:
                break;
        }
        return timingModeUnit;
    }

    /**
     * @param timingModeUnit
     * @return
     */
    private String getTimingModeUnitName(String timingModeUnit) {
        String timingModeUnitName = null;
        if (StringUtils.equals(EnumTimingModeUnit.DAY.getValue(), timingModeUnit)) {
            timingModeUnitName = EnumTimingModeUnit.DAY.getName();
        } else if (StringUtils.equals(EnumTimingModeUnit.HOUR.getValue(), timingModeUnit)) {
            timingModeUnitName = EnumTimingModeUnit.HOUR.getName();
        } else if (StringUtils.equals(EnumTimingModeUnit.MINUTE.getValue(), timingModeUnit)) {
            timingModeUnitName = EnumTimingModeUnit.MINUTE.getName();
        }
        return timingModeUnitName;
    }

    /**
     * @param timeLimitTypeName
     * @return
     */
    private String getTimeLimitTypeName(String timeLimitType) {
        EnumTimeLimitType enumTimeLimitType = EnumTimeLimitType.getByValue(timeLimitType);
        return enumTimeLimitType != null ? enumTimeLimitType.getName() : StringUtils.EMPTY;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerConfigFacadeService#deleteAll(java.util.List)
     */
    @Override
    public void deleteAll(List<String> uuids) {
        if (isUsedByUuidsForDeleteAll(uuids)) {
            throw new BusinessException("计时服务正被使用，无法删除！");
        }
        timerConfigService.deleteByUuids(uuids);
    }

    /**
     * @param uuids
     * @return
     */
    private boolean isUsedByUuidsForDeleteAll(List<String> uuids) {
        for (String uuid : uuids) {
            for (TsTimerConfigUsedChecker timerConfigUsedChecker : timerConfigUsedCheckers) {
                if (timerConfigUsedChecker.isTimerConfigUsed(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerConfigFacadeService#isUsedByUuids(java.util.List)
     */
    @Override
    public ResultMessage isUsedByUuids(List<String> uuids) {
        ResultMessage message = new ResultMessage();
        List<String> usedUuids = Lists.newArrayList();
        for (String uuid : uuids) {
            for (TsTimerConfigUsedChecker timerConfigUsedChecker : timerConfigUsedCheckers) {
                if (timerConfigUsedChecker.isTimerConfigUsed(uuid)) {
                    usedUuids.add(uuid);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(usedUuids)) {
            List<TsTimerConfigEntity> timerConfigEntities = timerConfigService.listByUuids(usedUuids);
            StringBuilder sb = new StringBuilder("以下计时服务正在使用，无法删除");
            for (TsTimerConfigEntity timerConfigEntity : timerConfigEntities) {
                sb.append(Separator.LINE.getValue());
                sb.append(timerConfigEntity.getName());
            }
            message.clear();
            message.setMsg(sb);
            message.setData(timerConfigEntities);
        }
        return message;
    }

}
