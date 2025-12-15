/*
 * @(#)5/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.service.impl;

import com.wellsoft.pt.app.weixin.dao.WeixinConfigDao;
import com.wellsoft.pt.app.weixin.entity.WeixinConfigEntity;
import com.wellsoft.pt.app.weixin.service.WeixinConfigService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import io.jsonwebtoken.lang.Collections;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class WeixinConfigServiceImpl extends AbstractJpaServiceImpl<WeixinConfigEntity, WeixinConfigDao, Long> implements WeixinConfigService {

    @Override
    public WeixinConfigEntity getBySystem(String system) {
        WeixinConfigEntity entity = new WeixinConfigEntity();
        entity.setSystem(system);
        List<WeixinConfigEntity> entities = this.dao.listByEntity(entity);
        return Collections.isEmpty(entities) ? null : entities.get(0);
    }

    @Override
    public long countByCorpIdAndAppId(String corpId, String appId) {
        WeixinConfigEntity entity = new WeixinConfigEntity();
        entity.setCorpId(corpId);
        entity.setAppId(appId);
        return this.dao.countByEntity(entity);
    }

}
