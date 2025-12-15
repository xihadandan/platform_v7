/*
 * @(#)2017-02-22 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.dms.bean.DmsTagBean;
import com.wellsoft.pt.dms.entity.DmsTagEntity;
import com.wellsoft.pt.dms.facade.service.DmsTagMgr;
import com.wellsoft.pt.dms.service.DmsTagService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-02-22.1	zhulh		2017-02-22		Create
 * </pre>
 * @date 2017-02-22
 */
@Service
@Transactional
public class DmsTagMgrImpl extends BaseServiceImpl implements DmsTagMgr {

    @Autowired
    private DmsTagService dmsTagService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagMgr#getBean(java.lang.String)
     */
    @Override
    public DmsTagBean getBean(String uuid) {
        DmsTagEntity entity = dmsTagService.getOne(uuid);
        DmsTagBean bean = new DmsTagBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagMgr#saveBean(com.wellsoft.pt.dms.bean.DmsTagBean)
     */
    @Override
    public void saveBean(DmsTagBean bean) {
        String uuid = bean.getUuid();
        DmsTagEntity entity = new DmsTagEntity();
        if (StringUtils.isNotBlank(uuid)) {
            entity = dmsTagService.getOne(uuid);
        }
        BeanUtils.copyProperties(bean, entity);
        dmsTagService.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagMgr#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        dmsTagService.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsTagMgr#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        dmsTagService.deleteByUuids(Lists.newArrayList(uuids));
    }

}
