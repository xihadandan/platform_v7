/*
 * @(#)2016-03-01 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.bean.TenantTemplateModuleBean;
import com.wellsoft.pt.mt.dao.TenantTemplateModuleDao;
import com.wellsoft.pt.mt.entity.TenantTemplateModule;
import com.wellsoft.pt.mt.service.TenantTemplateModuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-01.1	linz		2016-03-01		Create
 * </pre>
 * @date 2016-03-01
 */
@Service
public class TenantTemplateModuleServiceImpl extends
        AbstractJpaServiceImpl<TenantTemplateModule, TenantTemplateModuleDao, String> implements
        TenantTemplateModuleService {
    private static final String DATA_GRID_QUERY = "from TenantTemplateModule w where w.tenantTemplate.uuid =:uuid order by w.sortOrder ";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantTemplateModuleService#get(java.lang.String)
     */
    @Override
    public TenantTemplateModule get(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantTemplateModuleService#getAll()
     */
    @Override
    public List<TenantTemplateModule> getAll() {
        return listAll();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantTemplateModuleService#findByExample(TenantTemplateModule)
     */
    @Override
    public List<TenantTemplateModule> findByExample(TenantTemplateModule example) {
        return dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantTemplateModuleService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.TenantTemplateModuleService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<TenantTemplateModule> entities) {
        deleteByEntities(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantTemplateModuleService#remove(TenantTemplateModule)
     */
    @Override
    @Transactional
    public void remove(TenantTemplateModule entity) {
        dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantTemplateModuleService#removeAllByPk(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        deleteByUuids(Lists.newArrayList(uuids));
    }

    @Override
    public QueryData queryDataGrid(String templateUuid, JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo) {
        // TODO Auto-generated method stub
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("uuid", templateUuid);
        List<TenantTemplateModule> tenantTemplatemModules = listByHQLAndPage(DATA_GRID_QUERY, obj,
                queryInfo.getPagingInfo());
        List<TenantTemplateModuleBean> beans = new ArrayList<TenantTemplateModuleBean>();
        for (TenantTemplateModule templateModule : tenantTemplatemModules) {
            TenantTemplateModuleBean templateModuleBean = new TenantTemplateModuleBean();
            BeanUtils.copyProperties(templateModule, templateModuleBean);
            beans.add(templateModuleBean);
        }
        QueryData queryData = new QueryData();
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        queryData.setDataList(beans);
        return queryData;
    }
}
