/*
 * @(#)2017-02-13 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.dms.bean.DmsDataManagementDefinitionBean;
import com.wellsoft.pt.dms.entity.DmsDataManagementDefinition;
import com.wellsoft.pt.dms.facade.service.DmsDataManagementDefinitionMgr;
import com.wellsoft.pt.dms.service.DmsDataManagementDefinitionService;
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
 * 2017-02-13.1	zhulh		2017-02-13		Create
 * </pre>
 * @date 2017-02-13
 */
@Service
@Transactional
public class DmsDataManagementDefinitionMgrImpl extends BaseServiceImpl implements DmsDataManagementDefinitionMgr {

    @Autowired
    private DmsDataManagementDefinitionService dmsDataManagementDefinitionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmDataManagementDefinitionMgr#getBean(java.lang.String)
     */
    @Override
    public DmsDataManagementDefinitionBean getBean(String uuid) {
        DmsDataManagementDefinition entity = dmsDataManagementDefinitionService.getOne(uuid);
        DmsDataManagementDefinitionBean bean = new DmsDataManagementDefinitionBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmDataManagementDefinitionMgr#saveBean(com.wellsoft.pt.dms.bean.DmsDataManagementDefinitionBean)
     */
    @Override
    public void saveBean(DmsDataManagementDefinitionBean bean) {
        String uuid = bean.getUuid();
        DmsDataManagementDefinition entity = new DmsDataManagementDefinition();
        if (StringUtils.isNotBlank(uuid)) {
            entity = dmsDataManagementDefinitionService.getOne(uuid);
        }
        BeanUtils.copyProperties(bean, entity);
        dmsDataManagementDefinitionService.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmDataManagementDefinitionMgr#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        dmsDataManagementDefinitionService.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmDataManagementDefinitionMgr#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        dmsDataManagementDefinitionService.deleteByUuids(Lists.newArrayList(uuids));
    }

}
