/*
 * @(#)2015-5-28 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.notice.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.notice.dao.MtNoticeDao;
import com.wellsoft.pt.notice.entity.MtNotice;
import com.wellsoft.pt.notice.service.MtNoticeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-5-28.1	Administrator		2015-5-28		Create
 * </pre>
 * @date 2015-5-28
 */
@Service
public class MtNoticeServiceImpl extends AbstractJpaServiceImpl<MtNotice, MtNoticeDao, String> implements
        MtNoticeService {
    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.notice.service.MtNoticeService#isOtherMtNotice(java.lang.String)
     */
    @Override
    public boolean isOtherMtNotice(Collection<String> dataUuids) {
        for (String dataUuid : dataUuids) {
            MtNotice mtNotice = getByDataUuid(dataUuid);
            if (mtNotice == null) {
                return true;
            }
            String currentTenantId = SpringSecurityUtils.getCurrentTenantId();
            String tenantId = mtNotice.getTenantId();
            if (!StringUtils.equals(tenantId, currentTenantId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.notice.service.MtNoticeService#getByDataUuid(java.lang.String)
     */
    @Override
    public MtNotice getByDataUuid(String dataUuid) {
        MtNotice example = new MtNotice();
        example.setDataUuid(dataUuid);
        List<MtNotice> mtNotices = dao.listByEntity(example);
        if (mtNotices.isEmpty()) {
            return null;
        }
        return mtNotices.get(0);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.notice.service.MtNoticeService#removeByDataUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void removeByDataUuid(String dataUuid) {
        MtNotice example = new MtNotice();
        example.setDataUuid(dataUuid);
        List<MtNotice> mtNotices = dao.listByEntity(example);
        for (MtNotice mtNotice : mtNotices) {
            delete(mtNotice);
        }
    }
}
