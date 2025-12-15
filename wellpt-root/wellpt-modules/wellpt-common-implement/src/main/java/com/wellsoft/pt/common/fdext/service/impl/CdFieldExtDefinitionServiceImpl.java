/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.service.impl;

import com.wellsoft.pt.common.fdext.bean.CdFieldExtDefinitionBean;
import com.wellsoft.pt.common.fdext.entity.CdFieldExtDefinition;
import com.wellsoft.pt.common.fdext.service.CdFieldExtDefinitionService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
public class CdFieldExtDefinitionServiceImpl extends BaseServiceImpl implements CdFieldExtDefinitionService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtDefinitionService#get(java.lang.String)
     */
    @Override
    public CdFieldExtDefinition get(String uuid) {
        return this.dao.get(CdFieldExtDefinition.class, uuid);
    }

    @Override
    public CdFieldExtDefinitionBean getBean(String uuid) {
        CdFieldExtDefinition entity = this.get(uuid);
        CdFieldExtDefinitionBean bean = new CdFieldExtDefinitionBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtDefinitionService#getAll()
     */
    @Override
    public List<CdFieldExtDefinition> getAll() {
        return this.dao.getAll(CdFieldExtDefinition.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtDefinitionService#findByExample(CdFieldExtDefinition)
     */
    @Override
    public List<CdFieldExtDefinition> findByExample(CdFieldExtDefinition example) {
        return this.dao.findByExample(example);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtDefinitionService#find(java.lang.String)
     */
    @Override
    public List<CdFieldExtDefinition> find(String hql, Map<String, Object> values) {
        return dao.find(hql, values, CdFieldExtDefinition.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtDefinitionService#save(com.wellsoft.pt.common.fdext.entity.CdFieldExtDefinition)
     */
    @Override
    public void save(CdFieldExtDefinition entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtDefinitionService#saveBean(com.wellsoft.pt.common.fdext.entity.CdFieldExtDefinition)
     */
    @Override
    public void saveBean(CdFieldExtDefinitionBean bean) {
        String uuid = bean.getUuid();
        CdFieldExtDefinition entity = new CdFieldExtDefinition();
        if (StringUtils.isNotBlank(uuid)) {
            entity = this.get(uuid);
        } else if (StringUtils.isBlank(bean.getTenantId()))/*写租户ID*/ {
            bean.setTenantId(SpringSecurityUtils.getCurrentTenantId());
        }
        BeanUtils.copyProperties(bean, entity);
        this.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtDefinitionService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<CdFieldExtDefinition> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtDefinitionService#remove(java.lang.String)
     */
    @Override
    public void removeByPk(String uuid) {
        this.dao.deleteByPk(CdFieldExtDefinition.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.CdFieldExtDefinitionService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<CdFieldExtDefinition> entities) {
        this.dao.deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtDefinitionService#remove(CdFieldExtDefinition)
     */
    @Override
    public void remove(CdFieldExtDefinition entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtDefinitionService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.dao.deleteAllByPk(CdFieldExtDefinition.class, list);
    }

}
