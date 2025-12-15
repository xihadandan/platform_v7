/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service.impl;

import com.wellsoft.pt.common.fdext.facade.service.CdFieldFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.bean.OrgCorporationBean;
import com.wellsoft.pt.org.entity.OrgCorporation;
import com.wellsoft.pt.org.service.OrgCorporationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-11.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
@Service
@Transactional
public class OrgCorporationServiceImpl extends BaseServiceImpl implements OrgCorporationService {

    @Autowired
    private CdFieldFacade cdFieldFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.OrgCorporationService#get(java.lang.String)
     */
    @Override
    public OrgCorporation get(String uuid) {
        return this.dao.get(OrgCorporation.class, uuid);
    }

    @Override
    public OrgCorporationBean getBean(String uuid) {
        OrgCorporation entity = this.get(uuid);
        OrgCorporationBean bean = new OrgCorporationBean();
        BeanUtils.copyProperties(entity, bean);
        bean.setExtValue(cdFieldFacade.getData(bean.getUuid(), OrgCorporationBean.EXTVALUE_GROUP_TYPE));
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.OrgCorporationService#getAll()
     */
    @Override
    public List<OrgCorporation> getAll() {
        return this.dao.getAll(OrgCorporation.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.OrgCorporationService#findByExample(OrgCorporation)
     */
    @Override
    public List<OrgCorporation> findByExample(OrgCorporation example) {
        return this.dao.findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.OrgCorporationService#save(com.wellsoft.pt.org.entity.OrgCorporation)
     */
    @Override
    public void save(OrgCorporation entity) {
        this.dao.save(entity);
    }

    @Override
    public void saveBean(OrgCorporationBean bean) {
        String uuid = bean.getUuid();
        OrgCorporation entity = new OrgCorporation();
        if (StringUtils.isNotBlank(uuid)) {
            entity = this.get(uuid);
        }
        BeanUtils.copyProperties(bean, entity);
        this.save(entity);
        cdFieldFacade.saveData(entity.getUuid(), OrgCorporationBean.EXTVALUE_GROUP_TYPE, bean.getExtValue());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.OrgCorporationService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<OrgCorporation> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.OrgCorporationService#remove(java.lang.String)
     */
    @Override
    public void removeByPk(String uuid) {
        this.dao.deleteByPk(OrgCorporation.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.OrgCorporationService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<OrgCorporation> entities) {
        this.dao.deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.OrgCorporationService#remove(OrgCorporation)
     */
    @Override
    public void remove(OrgCorporation entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.OrgCorporationService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.dao.deleteAllByPk(OrgCorporation.class, list);
    }

}
