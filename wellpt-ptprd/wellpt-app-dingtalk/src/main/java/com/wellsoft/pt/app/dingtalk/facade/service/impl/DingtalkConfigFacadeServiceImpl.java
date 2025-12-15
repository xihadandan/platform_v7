/*
 * @(#)4/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.facade.service.impl;

import com.wellsoft.context.jdbc.entity.SysEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.app.dingtalk.dto.DingtalkConfigDto;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkConfigEntity;
import com.wellsoft.pt.app.dingtalk.facade.service.DingtalkConfigFacadeService;
import com.wellsoft.pt.app.dingtalk.facade.service.DingtalkEventCallbackFacadeService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkConfigService;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiV2Utils;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkConfigVo;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/16/25.1	    zhulh		4/16/25		    Create
 * </pre>
 * @date 4/16/25
 */
@Service
public class DingtalkConfigFacadeServiceImpl extends AbstractApiFacade implements DingtalkConfigFacadeService {

    private static final String CACHE_NAME = "Basic Data";

    @Autowired
    private DingtalkConfigService dingtalkConfigService;

    @Autowired
    private DingtalkEventCallbackFacadeService dingtalkEventCallbackFacadeService;

    @Override
    public DingtalkConfigDto getDtoBySystem(String system) {
        DingtalkConfigDto dto = new DingtalkConfigDto();
        DingtalkConfigEntity entity = dingtalkConfigService.getBySystem(system);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public Long saveDto(DingtalkConfigDto dingtalkConfigDto) {
        if (StringUtils.isNotBlank(dingtalkConfigDto.getAppId())
                && dingtalkConfigService.countByAppId(dingtalkConfigDto.getAppId()) > 1) {
            throw new IllegalArgumentException("App ID已在使用");
        }

        DingtalkConfigEntity entity = null;
        Long uuid = dingtalkConfigDto.getUuid();
        if (uuid != null) {
            entity = dingtalkConfigService.getOne(uuid);
        } else {
            entity = new DingtalkConfigEntity();
            entity.setSystem(RequestSystemContextPathResolver.system());
            entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        BeanUtils.copyProperties(dingtalkConfigDto, entity, SysEntity.BASE_FIELDS);
        this.dingtalkConfigService.save(entity);

        dingtalkEventCallbackFacadeService.updateWsClient(entity);
        return entity.getUuid();
    }

    @Override
    public void testCreateToken(String clientId, String clientSecret, String baseUrl) {
        DingtalkConfigVo dingtalkConfigVo = new DingtalkConfigVo();
        dingtalkConfigVo.setClientId(clientId);
        dingtalkConfigVo.setClientSecret(clientSecret);
        dingtalkConfigVo.setServiceUri(baseUrl);
        String accessToken = DingtalkApiV2Utils.getAccessToken(dingtalkConfigVo);
        logger.info("accessToken: " + accessToken);
        if (StringUtils.isBlank(accessToken)) {
            throw new RuntimeException("获取access_token失败");
        }
    }

    @Override
    public DingtalkConfigVo getVoByUuid(Long uuid) {
        DingtalkConfigEntity entity = dingtalkConfigService.getOne(uuid);
        return DingtalkConfigVo.fromEntity(entity);
    }

    @Override
    @Cacheable(value = CACHE_NAME)
    public DingtalkConfigVo getVoBySystemAndTenant(String system, String tenant) {
        DingtalkConfigEntity entity = dingtalkConfigService.getBySystem(system);
        return DingtalkConfigVo.fromEntity(entity);
    }

}
