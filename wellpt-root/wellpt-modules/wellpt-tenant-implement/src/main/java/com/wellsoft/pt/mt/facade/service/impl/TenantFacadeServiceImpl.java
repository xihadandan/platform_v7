/*
 * @(#)2018年4月18日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.facade.service.impl;

import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.bean.TenantBean;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.entity.TenantPinyin;
import com.wellsoft.pt.mt.entity.TenantTemplate;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.mt.service.TenantPinyinService;
import com.wellsoft.pt.mt.service.TenantService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月18日.1	chenqiong		2018年4月18日		Create
 * </pre>
 * @date 2018年4月18日
 */
@Service
public class TenantFacadeServiceImpl extends AbstractApiFacade implements TenantFacadeService {

    @Resource
    TenantService tenantService;

    @Resource
    TenantPinyinService tenantPinyinService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#query(com.wellsoft.pt.core.support.QueryInfo)
     */
    @Override
    public List<TenantBean> query(QueryInfo queryInfo) {
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());
        values.put("orderBy", queryInfo.getOrderBy());
        List<Tenant> result = tenantService.listByNameHQLQueryAndPage("tenantQuery", values, queryInfo.getPagingInfo());
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

    @Override
    public Tenant getByAccount(String account) {
        return tenantService.getByAccount(account);
    }

    @Override
    public void deleteTenant(String uuid) {
        tenantService.deleteTenant(uuid);
    }

    @Override
    public void deleteTenant(Tenant tenant) {
        tenantService.deleteTenant(tenant);
    }

    @Override
    public Tenant getTenantByUUID(String uuid) {
        return tenantService.getTenantByUUID(uuid);
    }

    @Override
    public Tenant get(String uuid) {
        return tenantService.getOne(uuid);
    }

    @Override
    public List<Tenant> getActiveTenants() {
        return tenantService.getActiveTenants();
    }

    @Override
    public Tenant getById(String id) {
        return tenantService.getById(id);
    }

    @Override
    public void saveTenantPinyin(TenantPinyin pinyin) {
        tenantPinyinService.save(pinyin);
    }

    @Override
    public void deleteTenantPinyinByUuid(String uuid) {
        tenantPinyinService.deleteByEntityUuid(uuid);
    }

    @Override
    public List<Tenant> listByExample(Tenant tenant) {
        return tenantService.listByExample(tenant);
    }

    @Override
    public void saveTenant(Tenant tenant) {
        tenantService.save(tenant);
    }

    @Override
    public long countByDatabaseConfigUuid(String uuid) {
        return tenantService.countByDatabaseConfigUuid(uuid);
    }

}
