/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service.impl;

import com.wellsoft.pt.dms.bean.DmsFileBean;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.facade.service.DmsFileMgr;
import com.wellsoft.pt.dms.service.DmsFileService;
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
 * 2017-12-19.1	zhulh		2017-12-19		Create
 * </pre>
 * @date 2017-12-19
 */
@Service
@Transactional
public class DmsFileMgrImpl extends BaseServiceImpl implements DmsFileMgr {

    @Autowired
    private DmsFileService dmsFileService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsFileMgr#getBean(java.lang.String)
     */
    @Override
    public DmsFileBean getBean(String uuid) {
        DmsFileEntity entity = dmsFileService.get(uuid);
        DmsFileBean bean = new DmsFileBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsFileMgr#saveBean(com.wellsoft.pt.dms.bean.DmsFileBean)
     */
    @Override
    public void saveBean(DmsFileBean bean) {
        String uuid = bean.getUuid();
        DmsFileEntity entity = new DmsFileEntity();
        if (StringUtils.isNotBlank(uuid)) {
            entity = dmsFileService.get(uuid);
        }
        BeanUtils.copyProperties(bean, entity);
        dmsFileService.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsFileMgr#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        dmsFileService.remove(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsFileMgr#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        dmsFileService.removeAllByPk(uuids);
    }

}
