/*
 * @(#)2021-07-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.dao.DmsDocExchangeFieldVerDao;
import com.wellsoft.pt.dms.entity.DmsDocExchangeFieldVerEntity;
import com.wellsoft.pt.dms.service.DmsDocExchangeFieldVerService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 数据库表DMS_DOC_EXCHANGE_FIELD_VER的service服务接口实现类
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-22.1	leo		2021-07-22		Create
 * </pre>
 * @date 2021-07-22
 */
@Service
public class DmsDocExchangeFieldVerServiceImpl extends AbstractJpaServiceImpl<DmsDocExchangeFieldVerEntity, DmsDocExchangeFieldVerDao, String> implements DmsDocExchangeFieldVerService {


    @Override
    @Transactional
    public void recordRead(String userId, String version) {
        DmsDocExchangeFieldVerEntity fieldVerEntity = new DmsDocExchangeFieldVerEntity();
        fieldVerEntity.setUserId(userId);
        fieldVerEntity.setVersion(version);
        long count = this.getDao().countByEntity(fieldVerEntity);
        if (count == 0) {
            this.save(fieldVerEntity);
        }
    }


    @Override
    public boolean isRead(String userId, String version) {
        DmsDocExchangeFieldVerEntity fieldVerEntity = new DmsDocExchangeFieldVerEntity();
        fieldVerEntity.setUserId(userId);
        fieldVerEntity.setVersion(version);
        long count = this.getDao().countByEntity(fieldVerEntity);
        return count > 0;
    }
}
