/*
 * @(#)2022-03-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.handover.service.impl;

import com.wellsoft.pt.handover.dao.WhWorkSettingsDao;
import com.wellsoft.pt.handover.entity.WhWorkSettingsEntity;
import com.wellsoft.pt.handover.service.WhWorkSettingsService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 数据库表WH_WORK_SETTINGS的service服务接口实现类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-03-22.1	zenghw		2022-03-22		Create
 * </pre>
 * @date 2022-03-22
 */
@Service
public class WhWorkSettingsServiceImpl extends AbstractJpaServiceImpl<WhWorkSettingsEntity, WhWorkSettingsDao, String>
        implements WhWorkSettingsService {

    @Override
    public WhWorkSettingsEntity getDetailByCurrentUnitId(String systemUnitId) {
        WhWorkSettingsEntity entity = new WhWorkSettingsEntity();
        entity.setSystemUnitId(systemUnitId);
        List<WhWorkSettingsEntity> entities = this.dao.listByEntity(entity);
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }
}
