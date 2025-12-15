/*
 * @(#)2021年5月24日 V1.0
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
import com.wellsoft.pt.timer.dto.TsTimerCategoryDto;
import com.wellsoft.pt.timer.entity.TsTimerCategoryEntity;
import com.wellsoft.pt.timer.facade.service.TsTimerCategoryFacadeService;
import com.wellsoft.pt.timer.service.TsTimerCategoryService;
import com.wellsoft.pt.timer.support.TsTimerCategoryUsedChecker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
 * 2021年5月24日.1	zhulh		2021年5月24日		Create
 * </pre>
 * @date 2021年5月24日
 */
@Service
public class TsTimerCategoryFacadeServiceImpl implements TsTimerCategoryFacadeService {

    @Autowired
    private TsTimerCategoryService timerCategoryService;

    @Autowired
    private List<TsTimerCategoryUsedChecker> timerCategoryUsedCheckers;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerCategoryFacadeService#getAllBySystemUnitIdsLikeName(java.lang.String)
     */
    @Override
    public List<TsTimerCategoryDto> getAllBySystemUnitIdsLikeName(String name) {
        List<String> systemUnitIds = new ArrayList<String>();
        // systemUnitIds.add(MultiOrgSystemUnit.PT_ID);
        systemUnitIds.add(SpringSecurityUtils.getCurrentUserUnitId());
        List<TsTimerCategoryEntity> entities = timerCategoryService.getAllBySystemUnitIdsLikeName(systemUnitIds, name);
        return BeanUtils.copyCollection(entities, TsTimerCategoryDto.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerCategoryFacadeService#getDto(java.lang.String)
     */
    @Override
    public TsTimerCategoryDto getDto(String uuid) {
        TsTimerCategoryDto timerCategoryDto = new TsTimerCategoryDto();
        TsTimerCategoryEntity timerCategoryEntity = timerCategoryService.getOne(uuid);
        if (timerCategoryEntity != null) {
            BeanUtils.copyProperties(timerCategoryEntity, timerCategoryDto);
        }
        return timerCategoryDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerCategoryFacadeService#getById(java.lang.String)
     */
    @Override
    public TsTimerCategoryDto getById(String id) {
        TsTimerCategoryDto timerCategoryDto = new TsTimerCategoryDto();
        TsTimerCategoryEntity timerCategoryEntity = timerCategoryService.getById(id);
        if (timerCategoryEntity != null) {
            BeanUtils.copyProperties(timerCategoryEntity, timerCategoryDto);
        }
        return timerCategoryDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerCategoryFacadeService#saveDto(com.wellsoft.pt.timer.dto.TsTimerCategoryDto)
     */
    @Override
    public String saveDto(TsTimerCategoryDto timerCategoryDto) {
        TsTimerCategoryEntity timerCategoryEntity = new TsTimerCategoryEntity();
        if (StringUtils.isNotBlank(timerCategoryDto.getUuid())) {
            timerCategoryEntity = timerCategoryService.getOne(timerCategoryDto.getUuid());
        } else {
            // ID唯一性判断
            if (this.timerCategoryService.countById(timerCategoryDto.getId()) > 0) {
                throw new RuntimeException("已经存在ID为[" + timerCategoryDto.getId() + "]的计时服务分类!");
            }
            timerCategoryDto.setSystem(RequestSystemContextPathResolver.system());
            timerCategoryDto.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        BeanUtils.copyProperties(timerCategoryDto, timerCategoryEntity, IdEntity.BASE_FIELDS);
        timerCategoryService.save(timerCategoryEntity);
        return timerCategoryEntity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerCategoryFacadeService#deleteAll(java.util.List)
     */
    @Override
    public void deleteAll(List<String> uuids) {
        if (isUsedByUuidsForDeleteAll(uuids)) {
            throw new BusinessException("计时服务分类下存在计时服务，无法删除！");
        }
        timerCategoryService.deleteByUuids(uuids);
    }

    /**
     * @param uuids
     * @return
     */
    private boolean isUsedByUuidsForDeleteAll(List<String> uuids) {
        for (String uuid : uuids) {
            for (TsTimerCategoryUsedChecker timerCategoryUsedChecker : timerCategoryUsedCheckers) {
                if (timerCategoryUsedChecker.isTimerCategoryUsed(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsTimerCategoryFacadeService#isUsedByUuids(java.util.List)
     */
    @Override
    public ResultMessage isUsedByUuids(List<String> uuids) {
        ResultMessage message = new ResultMessage();
        List<String> usedUuids = Lists.newArrayList();
        for (String uuid : uuids) {
            for (TsTimerCategoryUsedChecker timerCategoryUsedChecker : timerCategoryUsedCheckers) {
                if (timerCategoryUsedChecker.isTimerCategoryUsed(uuid)) {
                    usedUuids.add(uuid);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(usedUuids)) {
            List<TsTimerCategoryEntity> timerCategoryEntities = timerCategoryService.listByUuids(usedUuids);
            StringBuilder sb = new StringBuilder("以下计时服务分类正在使用，无法删除");
            for (TsTimerCategoryEntity timerCategoryEntity : timerCategoryEntities) {
                sb.append(Separator.LINE.getValue());
                sb.append(timerCategoryEntity.getName());
            }
            message.clear();
            message.setMsg(sb);
            message.setData(timerCategoryEntities);
        }
        return message;
    }

}
