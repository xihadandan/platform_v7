/*
 * @(#)Mar 14, 2022 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.documentlink.service;

import com.wellsoft.pt.basicdata.documentlink.dao.CdDocumentLinkDao;
import com.wellsoft.pt.basicdata.documentlink.entity.CdDocumentLinkEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface CdDocumentLinkService extends JpaService<CdDocumentLinkEntity, CdDocumentLinkDao, String> {

    /**
     * @param sourceDataUuid
     * @param targetDataUuid
     * @return
     */
    boolean existsBySourceAndTargetDataUuid(String sourceDataUuid, String targetDataUuid);

}
