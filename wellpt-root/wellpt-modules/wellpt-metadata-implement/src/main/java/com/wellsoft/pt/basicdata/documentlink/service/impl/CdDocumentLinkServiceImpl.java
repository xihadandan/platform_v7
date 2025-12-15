/*
 * @(#)Mar 14, 2022 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.documentlink.service.impl;

import com.wellsoft.pt.basicdata.documentlink.dao.CdDocumentLinkDao;
import com.wellsoft.pt.basicdata.documentlink.entity.CdDocumentLinkEntity;
import com.wellsoft.pt.basicdata.documentlink.service.CdDocumentLinkService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Mar 14, 2022.1	zhulh		Mar 14, 2022		Create
 * </pre>
 * @date Mar 14, 2022
 */
@Service
public class CdDocumentLinkServiceImpl extends AbstractJpaServiceImpl<CdDocumentLinkEntity, CdDocumentLinkDao, String>
        implements CdDocumentLinkService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.documentlink.service.CdDocumentLinkService#existsBySourceAndTargetDataUuid(java.lang.String, java.lang.String)
     */
    @Override
    public boolean existsBySourceAndTargetDataUuid(String sourceDataUuid, String targetDataUuid) {
        Assert.notNull(sourceDataUuid, "sourceDataUuid is null");
        Assert.notNull(targetDataUuid, "targetDataUuid is null");
        CdDocumentLinkEntity entity = new CdDocumentLinkEntity();
        entity.setSourceDataUuid(sourceDataUuid);
        entity.setTargetDataUuid(targetDataUuid);
        return this.dao.countByEntity(entity) > 0;
    }

}
