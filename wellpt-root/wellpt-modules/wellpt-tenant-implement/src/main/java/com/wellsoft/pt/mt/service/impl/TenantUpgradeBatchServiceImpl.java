/*
 * @(#)2016-03-01 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service.impl;

import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.bean.TenantUpgradeBatchBean;
import com.wellsoft.pt.mt.entity.TenantUpgradeBatch;
import com.wellsoft.pt.mt.entity.TenantUpgradeProcessLog;
import com.wellsoft.pt.mt.service.TenantUpgradeBatchService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description: 升级批次类
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
@Transactional
public class TenantUpgradeBatchServiceImpl extends BaseServiceImpl implements TenantUpgradeBatchService {

    @Override
    public TenantUpgradeBatch get(String uuid) {
        // TODO Auto-generated method stub
        return this.getCommonDao().get(TenantUpgradeBatch.class, uuid);
    }

    @Override
    public List<TenantUpgradeBatch> getAll() {
        // TODO Auto-generated method stub
        return this.getCommonDao().getAll(TenantUpgradeBatch.class);
    }

    @Override
    public List<TenantUpgradeBatch> findByExample(TenantUpgradeBatch example) {
        // TODO Auto-generated method stub
        return this.getCommonDao().findByExample(example);
    }

    @Override
    public TenantUpgradeBatch save(TenantUpgradeBatch entity) {
        // TODO Auto-generated method stub
        if (StringUtils.isBlank(entity.getUuid())) {
            SimpleDateFormat sm = new SimpleDateFormat("yyyyMMddHHmmss");
            entity.setName(sm.format(new Date()));
        }
        this.getCommonDao().save(entity);
        return entity;
    }

    @Override
    public void saveAll(Collection<TenantUpgradeBatch> entities) {
        // TODO Auto-generated method stub
        this.getCommonDao().saveAll(entities);
    }

    @Override
    public void remove(TenantUpgradeBatch entity) {
        // TODO Auto-generated method stub
        this.getCommonDao().delete(entity);
    }

    @Override
    public void removeAll(Collection<TenantUpgradeBatch> entities) {
        // TODO Auto-generated method stub
        this.getCommonDao().deleteAll(entities);
    }

    @Override
    public List<TenantUpgradeBatchBean> query(QueryInfo queryInfo) {
        // TODO Auto-generated method stub
        String hql = "from TenantUpgradeBatch t ";
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());
        if (values.keySet().size() > 0) {
            values.put("searchValue", "%" + values.get("searchValue") + "%");
            hql += " where t.repoFileNames like :searchValue or t.tenantUpgradeLog.tenant.name like :searchValue or t.tenantUpgradeLog.tenant.id like :searchValue or t.name like :searchValue";
        }
        hql += " order by " + queryInfo.getOrderBy();
        List<TenantUpgradeBatch> tenantUpgradeBatchs = this.getCommonDao().find(hql, values, TenantUpgradeBatch.class,
                queryInfo.getPagingInfo());
        List<TenantUpgradeBatchBean> tenantUpgradeBatchBeans = new ArrayList<TenantUpgradeBatchBean>();
        for (TenantUpgradeBatch tenantUpgradeBatch : tenantUpgradeBatchs) {
            TenantUpgradeBatchBean tenantUpgradeBatchBean = new TenantUpgradeBatchBean();
            BeanUtils.copyProperties(tenantUpgradeBatch, tenantUpgradeBatchBean);
            if (tenantUpgradeBatch.getTenantUpgradeLog() != null
                    && tenantUpgradeBatch.getTenantUpgradeLog().getTenant() != null) {
                tenantUpgradeBatchBean.setTenantId(tenantUpgradeBatch.getTenantUpgradeLog().getTenant().getId());
                tenantUpgradeBatchBean.setTenantCode(tenantUpgradeBatch.getTenantUpgradeLog().getTenant().getCode());
                tenantUpgradeBatchBean.setTenantName(tenantUpgradeBatch.getTenantUpgradeLog().getTenant().getName());
            }
            Set<TenantUpgradeProcessLog> tenantUpgradeProcessLogs = tenantUpgradeBatch.getTenantUpgradeProcessLogs();
            String status = "成功";
            for (TenantUpgradeProcessLog tenantUpgradeProcessLog : tenantUpgradeProcessLogs) {
                if (tenantUpgradeProcessLog.getStatus() == 0) {
                    status = "失败";
                }
            }
            tenantUpgradeBatchBean.setStatus(status);
            tenantUpgradeBatchBeans.add(tenantUpgradeBatchBean);
        }
        return tenantUpgradeBatchBeans;
    }
}
