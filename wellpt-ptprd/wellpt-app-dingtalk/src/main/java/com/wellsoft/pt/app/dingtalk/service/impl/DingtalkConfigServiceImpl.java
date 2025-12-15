/*
 * @(#)4/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.service.impl;

import com.wellsoft.pt.app.dingtalk.dao.DingtalkConfigDao;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkConfigEntity;
import com.wellsoft.pt.app.dingtalk.service.DingtalkConfigService;
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
 * 4/16/25.1	    zhulh		4/16/25		    Create
 * </pre>
 * @date 4/16/25
 */
@Service
public class DingtalkConfigServiceImpl extends AbstractJpaServiceImpl<DingtalkConfigEntity, DingtalkConfigDao, Long> implements
        DingtalkConfigService {

    /**
     * @param system
     * @return
     */
    @Override
    public DingtalkConfigEntity getBySystem(String system) {
        DingtalkConfigEntity entity = new DingtalkConfigEntity();
        entity.setSystem(system);
        List<DingtalkConfigEntity> entities = this.dao.listByEntity(entity);
        return Collections.isEmpty(entities) ? null : entities.get(0);
    }

    @Override
    public long countByAppId(String appId) {
        DingtalkConfigEntity entity = new DingtalkConfigEntity();
        entity.setAppId(appId);
        return this.dao.countByEntity(entity);
    }

    @Override
    public DingtalkConfigEntity getByAppId(String appId) {
        DingtalkConfigEntity entity = new DingtalkConfigEntity();
        entity.setAppId(appId);
        List<DingtalkConfigEntity> entities = this.dao.listByEntity(entity);
        return Collections.isEmpty(entities) ? null : entities.get(0);
    }

}
