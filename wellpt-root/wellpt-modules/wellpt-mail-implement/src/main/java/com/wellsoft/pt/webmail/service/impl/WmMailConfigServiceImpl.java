/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.webmail.dao.WmMailConfigDao;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;
import com.wellsoft.pt.webmail.service.WmMailConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 邮件配置服务实现
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-06-03.1	t		2016-06-03		Create
 * </pre>
 * @date 2016-06-03
 */
@Service
public class WmMailConfigServiceImpl extends
        AbstractJpaServiceImpl<WmMailConfigEntity, WmMailConfigDao, String> implements
        WmMailConfigService {


    @Override
    public List<WmMailConfigEntity> findByExample(WmMailConfigEntity example) {
        return this.dao.listByEntity(example);
    }

    @Override
    public WmMailConfigEntity getBySystemUnitId(String systemUnitId) {
        List<WmMailConfigEntity> configEntities = this.dao.listByFieldEqValue("systemUnitId",
                systemUnitId);
        return CollectionUtils.isNotEmpty(configEntities) ? configEntities.get(0) : null;
    }

    @Override
    public List<WmMailConfigEntity> listByDomain(String domain) {
        return this.dao.listByFieldEqValue("domain",
                domain);
    }

}
