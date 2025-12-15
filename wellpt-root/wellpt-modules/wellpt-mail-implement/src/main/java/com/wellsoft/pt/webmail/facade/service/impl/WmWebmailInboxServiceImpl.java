/*
 * @(#)2016年6月3日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.webmail.bean.WmWebmailBean;
import com.wellsoft.pt.webmail.facade.service.WmWebmailInboxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Description: 草稿箱，收件箱
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年6月3日.1	zhulh		2016年6月3日		Create
 * </pre>
 * @date 2016年6月3日
 */
@Service
@Transactional
public class WmWebmailInboxServiceImpl extends BaseServiceImpl implements WmWebmailInboxService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailInboxService#getForView(java.lang.String)
     */
    @Override
    public WmWebmailBean getForView(String mailboxUuid) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailInboxService#delete(java.util.Collection)
     */
    @Override
    public void delete(Collection<String> mailboxUuids) {
        // TODO Auto-generated method stub

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmWebmailInboxService#deletePhysical(java.util.Collection)
     */
    @Override
    public void deletePhysical(Collection<String> mailboxUuids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteMailByMailbox(String mailbox, String userId) {

    }

}
