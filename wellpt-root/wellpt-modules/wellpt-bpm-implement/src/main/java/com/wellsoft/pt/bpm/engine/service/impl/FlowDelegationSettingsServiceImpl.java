/*
 * @(#)2018年8月14日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.pt.bpm.engine.dao.FlowDelegationSettingsDao;
import com.wellsoft.pt.bpm.engine.delegation.service.TaskDelegationTakeBackService;
import com.wellsoft.pt.bpm.engine.entity.FlowDelegationSettings;
import com.wellsoft.pt.bpm.engine.service.FlowDelegationSettingsService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.org.entity.DutyAgent;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年8月14日.1	zhulh		2018年8月14日		Create
 * </pre>
 * @date 2018年8月14日
 */
@Service
public class FlowDelegationSettingsServiceImpl
        extends AbstractJpaServiceImpl<FlowDelegationSettings, FlowDelegationSettingsDao, String>
        implements FlowDelegationSettingsService {

    private String GET_OVERDUE_SETTINGS = "from FlowDelegationSettings t where (t.status = 1 or t.status = 2) and t.toTime is not null and t.toTime < :toTime";

    @Autowired
    private TaskDelegationTakeBackService taskDelegationTakeBackService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDelegationSettingsService#getByUserIds(java.util.List)
     */
    @Override
    public List<FlowDelegationSettings> getByUserIds(List<String> userIds) {
        if (userIds == null || userIds.size() == 0) {
            return new ArrayList<>();
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", FlowDelegationSettings.STATUS_ACTIVE);
        StringBuilder hqlsb = new StringBuilder("from FlowDelegationSettings t where t.status = :status and ");
        HqlUtils.appendSql("t.consignor", values, hqlsb, Sets.newHashSet(userIds));
        return this.dao.listByHQL(hqlsb.toString(), values);
    }

    @Override
    public List<FlowDelegationSettings> getByUserIdsAndSystem(List<String> userIds, String system) {
        if (userIds == null || userIds.size() == 0) {
            return Lists.newArrayListWithCapacity(0);
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", FlowDelegationSettings.STATUS_ACTIVE);
        values.put("userIds", userIds);
        values.put("system", system);
        String hql = "from FlowDelegationSettings t where t.status = :status and t.consignor in (:userIds)";
        if (StringUtils.isNotBlank(system)) {
            hql += " and t.system = :system";
        }
        return this.dao.listByHQL(hql, values);
    }

    @Override
    public List<FlowDelegationSettings> getNotDeactiveByUserIds(List<String> userIds) {
        if (userIds == null || userIds.size() == 0) {
            return new ArrayList<>();
        }
        Map<String, Object> values = Maps.newHashMap();
        values.put("status", FlowDelegationSettings.STATUS_DEACTIVE);
        values.put("userIds", userIds);
        return this.dao.listByNameSQLQuery("getNotDeactiveByUserIds", values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDelegationSettingsService#deactive(java.lang.String)
     */
    @Override
    @Transactional
    public void deactive(String uuid) {
        FlowDelegationSettings delegationSettings = this.dao.getOne(uuid);
        delegationSettings.setStatus(DutyAgent.STATUS_DEACTIVE);
        this.dao.save(delegationSettings);
        // 手动终止时回收受拖人未处理的工作
        if (Boolean.TRUE.equals(delegationSettings.getDeactiveToTakeBackWork())) {
            String consignor = delegationSettings.getConsignor();
            if (StringUtils.equals(consignor, SpringSecurityUtils.getCurrentUserId())) {
                taskDelegationTakeBackService.deactiveToTakeBack(uuid);
            } else {
                try {
                    IgnoreLoginUtils.login(SpringSecurityUtils.getCurrentTenantId(), consignor);
                    RequestSystemContextPathResolver.setSystem(delegationSettings.getSystem());
                    taskDelegationTakeBackService.deactiveToTakeBack(uuid);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    RequestSystemContextPathResolver.clear();
                    IgnoreLoginUtils.logout();
                }
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDelegationSettingsService#getOverdueSettings()
     */
    @Override
    public List<FlowDelegationSettings> getOverdueSettings() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("toTime", Calendar.getInstance().getTime());
        return this.dao.listByHQL(GET_OVERDUE_SETTINGS, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDelegationSettingsService#getForCheckTheSameDelegationSettings(com.wellsoft.pt.bpm.engine.entity.FlowDelegationSettings)
     */
    @Override
    public List<FlowDelegationSettings> getForCheckTheSameDelegationSettings(
            FlowDelegationSettings delegationSettings) {
        FlowDelegationSettings entity = new FlowDelegationSettings();
        entity.setConsignor(delegationSettings.getConsignor());
        entity.setTrustee(delegationSettings.getTrustee());
        entity.setFromTime(delegationSettings.getFromTime());
        entity.setToTime(delegationSettings.getToTime());
        return this.dao.listByEntity(entity);
    }

}
