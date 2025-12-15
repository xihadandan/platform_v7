/*
 * @(#)5/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.facade.service.impl;

import com.wellsoft.context.jdbc.entity.SysEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.app.weixin.dto.WeixinConfigDto;
import com.wellsoft.pt.app.weixin.entity.WeixinConfigEntity;
import com.wellsoft.pt.app.weixin.facade.service.WeixinConfigFacadeService;
import com.wellsoft.pt.app.weixin.service.WeixinConfigService;
import com.wellsoft.pt.app.weixin.utils.WeixinApiUtils;
import com.wellsoft.pt.app.weixin.vo.WeixinConfigVo;
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
 * 5/20/25.1	    zhulh		5/20/25		    Create
 * </pre>
 * @date 5/20/25
 */
@Service
public class WeixinConfigFacadeServiceImpl extends AbstractApiFacade implements WeixinConfigFacadeService {

    private static final String CACHE_NAME = "Basic Data";

    @Autowired
    private WeixinConfigService weixinConfigService;

    @Override
    public WeixinConfigDto getDtoBySystem(String system) {
        WeixinConfigDto dto = new WeixinConfigDto();
        WeixinConfigEntity entity = weixinConfigService.getBySystem(system);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public Long saveDto(WeixinConfigDto weixinConfigDto) {
        if (StringUtils.isNotBlank(weixinConfigDto.getCorpId())
                && weixinConfigService.countByCorpIdAndAppId(weixinConfigDto.getCorpId(), weixinConfigDto.getAppId()) > 1) {
            throw new IllegalArgumentException("企业应用已在使用");
        }

        WeixinConfigEntity entity = null;
        Long uuid = weixinConfigDto.getUuid();
        if (uuid != null) {
            entity = weixinConfigService.getOne(uuid);
        } else {
            entity = new WeixinConfigEntity();
            entity.setSystem(RequestSystemContextPathResolver.system());
            entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        BeanUtils.copyProperties(weixinConfigDto, entity, SysEntity.BASE_FIELDS);
        weixinConfigService.save(entity);

        return entity.getUuid();
    }

    @Override
    public void testCreateToken(String corpId, String appSecret) {
        WeixinConfigVo weixinConfigVo = new WeixinConfigVo();
        weixinConfigVo.setCorpId(corpId);
        weixinConfigVo.setAppSecret(appSecret);
        String accessToken = WeixinApiUtils.getAccessToken(weixinConfigVo);
        logger.info("accessToken: " + accessToken);
        if (StringUtils.isBlank(accessToken)) {
            throw new RuntimeException("获取access_token失败");
        }
    }

    @Override
    public WeixinConfigVo getVoByUuid(Long uuid) {
        WeixinConfigEntity entity = weixinConfigService.getOne(uuid);
        return WeixinConfigVo.fromEntity(entity);
    }

    @Override
    @Cacheable(value = CACHE_NAME)
    public WeixinConfigVo getVoBySystem(String system) {
        WeixinConfigEntity entity = weixinConfigService.getBySystem(system);
        return WeixinConfigVo.fromEntity(entity);
    }

}
