/*
 * @(#)4/24/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.dao.WfFlowSettingDao;
import com.wellsoft.pt.workflow.entity.WfFlowSettingEntity;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 4/24/24.1	zhulh		4/24/24		Create
 * </pre>
 * @date 4/24/24
 */
@Service
public class WfFlowSettingServiceImpl extends AbstractJpaServiceImpl<WfFlowSettingEntity, WfFlowSettingDao, Long> implements WfFlowSettingService {

    private static final String FLOW_DEFINITION_CACHE_ID = ModuleID.WORKFLOW.getName();

    @Autowired
    private CacheManager cacheManager;

    /**
     * 根据系统及租户获取流程设置
     *
     * @param system
     * @param tenant
     * @return
     */
    @Override
    public List<WfFlowSettingEntity> listBySystemAndTenant(String system, String tenant) {
        if (StringUtils.isBlank(system)) {
            String hql = "from WfFlowSettingEntity t1 where t1.system is null and t1.tenant = :tenant";
            Map<String, Object> params = Maps.newHashMap();
            params.put("tenant", tenant);
            return this.dao.listByHQL(hql, params);
        }
        WfFlowSettingEntity entity = new WfFlowSettingEntity();
        entity.setSystem(system);
        entity.setTenant(tenant);
        return this.dao.listByEntity(entity);
    }

    @Override
    public WfFlowSettingEntity getByAttrKeyAndSystemAndTenant(String attrKey, String system, String tenant) {
        Assert.hasLength(attrKey, "属性键不能为空!");

        if (StringUtils.isBlank(system)) {
            String hql = "from WfFlowSettingEntity t1 where t1.attrKey = :attrKey and t1.system is null and t1.tenant = :tenant";
            Map<String, Object> params = Maps.newHashMap();
            params.put("attrKey", attrKey);
            params.put("tenant", tenant);
            List<WfFlowSettingEntity> entities = this.dao.listByHQL(hql, params);
            if (CollectionUtils.isNotEmpty(entities)) {
                return entities.get(0);
            }
            return null;
        }
        WfFlowSettingEntity entity = new WfFlowSettingEntity();
        entity.setAttrKey(attrKey);
        entity.setSystem(system);
        entity.setTenant(tenant);
        List<WfFlowSettingEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    @Override
    public WorkFlowSettings getWorkFlowSettings() {
        String system = RequestSystemContextPathResolver.system();
        String tenant = SpringSecurityUtils.getCurrentTenantId();

        WorkFlowSettings flowSettings = null;
        Cache cache = cacheManager.getCache(FLOW_DEFINITION_CACHE_ID);
        String cacheKey = getFlowSettingsCacheKey(system, tenant);
        Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper != null) {
            flowSettings = (WorkFlowSettings) valueWrapper.get();
        }
        if (flowSettings == null) {
            flowSettings = new WorkFlowSettings(this.listBySystemAndTenant(system, tenant));
            cache.put(cacheKey, flowSettings);
        }
        return flowSettings;
    }

    @Override
    public void clearFlowSettingsCache() {
        String system = RequestSystemContextPathResolver.system();
        String tenant = SpringSecurityUtils.getCurrentTenantId();

        Cache cache = cacheManager.getCache(FLOW_DEFINITION_CACHE_ID);
        String cacheKey = getFlowSettingsCacheKey(system, tenant);
        cache.evict(cacheKey);
    }

    /**
     * @param system
     * @param tenant
     * @return
     */
    private String getFlowSettingsCacheKey(String system, String tenant) {
        return "FlowSettings_" + system + "_" + tenant;
    }

    /**
     * @param system
     * @param tenant
     * @return
     */
    @Override
    public long countBySystemAndTenant(String system, String tenant) {
        Assert.hasLength(system, "归属系统不能为空！");
        Assert.hasLength(tenant, "归属租户不能为空！");

        WfFlowSettingEntity entity = new WfFlowSettingEntity();
        entity.setSystem(system);
        entity.setTenant(tenant);
        return this.dao.countByEntity(entity);
    }

    /**
     * @return
     */
    @Override
    public List<String> listSystemIds() {
        String hql = "select distinct t.system as system from WfFlowSettingEntity t";
        return this.dao.listCharSequenceByHQL(hql, null);
    }

}
