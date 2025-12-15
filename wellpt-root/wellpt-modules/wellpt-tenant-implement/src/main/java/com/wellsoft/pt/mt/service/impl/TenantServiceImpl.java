/*
 * @(#)2012-10-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service.impl;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.basicdata.iexport.service.IexportService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.bean.TenantBean;
import com.wellsoft.pt.mt.dao.TenantDao;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.entity.TenantTemplate;
import com.wellsoft.pt.mt.mgr.TenantManagerService;
import com.wellsoft.pt.mt.service.TenantService;
import com.wellsoft.pt.mt.service.TenantTemplateModuleService;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author lilin
 * @version 1.0
 * @date 2012-10-21
 */
@Service
public class TenantServiceImpl extends AbstractJpaServiceImpl<Tenant, TenantDao, String> implements TenantService {
    @Autowired
    private TenantManagerService tenantManagerService;
    @Autowired
    private IexportService iexportService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private TenantTemplateModuleService tenantTemplateModuleService;

    /**
     * @return the dao
     */
    public TenantDao getTenantDao() {
        return dao;
    }

    @Override
    @Transactional(readOnly = true)
    public Tenant getByAccount(String account) {
        Tenant tenant = dao.getByAccount(account);
        // 如果account传入的为ID，则通过ID取租户
        if (tenant == null && account != null && account.length() == 4
                && account.startsWith(IdPrefix.TENANT.getValue())) {
            tenant = dao.getById(account);
        }
        return tenant;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#deleteTenant(java.lang.String)
     */
    @Override
    @Transactional
    public void deleteTenant(String uuid) {
        dao.delete(uuid);
    }

    @Override
    public List<Tenant> listByExample(Tenant tenant) {
        return dao.listByEntity(tenant);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#deleteTenant(com.wellsoft.pt.core.mt.entity.Tenant)
     */
    @Override
    @Transactional
    public void deleteTenant(Tenant tenant) {
        dao.delete(tenant);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#getTenantByUUID(java.lang.String)
     */
    @Override
    public Tenant getTenantByUUID(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#get(java.lang.String)
     */
    @Override
    public Tenant get(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * 获取所有有效的租户
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#getActiveTenants()
     */
    @Override
    public List<Tenant> getActiveTenants() {
        return dao.getActiveTenants();
    }

    /**
     * 根据用户ID获取租户信息
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#getById(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public Tenant getById(String id) {
        return this.dao.getById(id);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.facade.service.TenantService#findByExample(com.wellsoft.pt.mt.entity.Tenant)
     */
    @Override
    public List<Tenant> findByExample(Tenant example) {
        return this.dao.listByEntity(example);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.facade.service.TenantService#countByDatabaseConfigUuid(java.lang.String)
     */
    @Override
    public long countByDatabaseConfigUuid(String uuid) {
        return this.dao.countByDatabaseConfigUuid(uuid);
    }

    @Override
    public List<Tenant> getNormalTenants() {
        return dao.getNormalTenants();
    }

    @Override
    public List<Tenant> getReviewTenants() {
        return dao.getReviewTenants();
    }

    @Override
    public List<Tenant> getByUserId(String userId) {
        return dao.getByUserId(userId);
    }

    @Override
    public List<TenantBean> query(QueryInfo queryInfo) {
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());
        values.put("orderBy", queryInfo.getOrderBy());
        List<Tenant> result = listByNameHQLQueryAndPage("tenantQuery", values, queryInfo.getPagingInfo());
        List<TenantBean> beans = new ArrayList<TenantBean>();
        for (Tenant tenant : result) {
            TenantBean tenantBean = new TenantBean();
            TenantTemplate template = tenant.getTenantTemplate();
            BeanUtils.copyProperties(tenant, tenantBean);
            if (template != null) {
                tenantBean.setTenantTemplateName(template.getName());
            } else {
                tenantBean.setTenantTemplateName("无");
            }
            beans.add(tenantBean);
        }
        return BeanUtils.convertCollection(beans, TenantBean.class);
    }

}
