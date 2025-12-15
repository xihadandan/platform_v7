/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.webmail.bean.WmMailboxBean;
import com.wellsoft.pt.webmail.entity.WmMailbox;
import com.wellsoft.pt.webmail.facade.service.WmMailboxViewMaintain;
import com.wellsoft.pt.webmail.service.WmMailboxService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Description: 邮箱视图维护实现
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
@Transactional
public class WmMailboxViewMaintainImpl extends BaseServiceImpl implements WmMailboxViewMaintain {

    @Autowired
    private WmMailboxService wmMailboxService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.WmMailboxMgrViewMaintain#getBean(java.lang.String)
     */
    @Override
    public WmMailboxBean getBean(String uuid) {
        WmMailbox entity = wmMailboxService.get(uuid);
        WmMailboxBean bean = new WmMailboxBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.WmMailboxMgrViewMaintain#saveBean(com.wellsoft.pt.app.bean.WmMailboxBean)
     */
    @Override
    public void saveBean(WmMailboxBean bean) {
        String uuid = bean.getUuid();
        WmMailbox entity = new WmMailbox();
        if (StringUtils.isNotBlank(uuid)) {
            entity = wmMailboxService.get(uuid);
        }
        BeanUtils.copyProperties(bean, entity);
        wmMailboxService.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.WmMailboxMgrViewMaintain#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        wmMailboxService.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.WmMailboxMgrViewMaintain#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        wmMailboxService.deleteByUuids(Lists.newArrayList(uuids));
    }

}
