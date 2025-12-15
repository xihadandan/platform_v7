/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.dao.TsWorkTimePlanDao;
import com.wellsoft.pt.timer.entity.TsWorkTimePlanEntity;
import com.wellsoft.pt.timer.entity.TsWorkTimePlanHistoryEntity;
import com.wellsoft.pt.timer.enums.EnumWorkTimePlanStatus;
import com.wellsoft.pt.timer.service.TsWorkTimePlanHistoryService;
import com.wellsoft.pt.timer.service.TsWorkTimePlanService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
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
 * 2021年4月7日.1	zhulh		2021年4月7日		Create
 * </pre>
 * @date 2021年4月7日
 */
@Service
public class TsWorkTimePlanServiceImpl extends AbstractJpaServiceImpl<TsWorkTimePlanEntity, TsWorkTimePlanDao, String>
        implements TsWorkTimePlanService {

    @Autowired
    private TsWorkTimePlanHistoryService workTimePlanHistoryService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanService#getSysDate()
     */
    @Override
    public Date getSysDate() {
        return this.dao.getSysDate();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanService#getAllBySystemUnitIdsLikeName(java.util.List, java.lang.String)
     */
    @Override
    public List<TsWorkTimePlanEntity> getAllBySystemUnitIdsLikeName(List<String> systemUnitIds, String name) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("systemUnitIds", systemUnitIds);
        values.put("status", EnumWorkTimePlanStatus.Active.getValue());
        String hql = "from TsWorkTimePlanEntity t where t.status = :status and t.systemUnitId in(:systemUnitIds) ";
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            hql += " and t.system = :system and t.tenant = :tenant ";
            values.put("system", system);
            values.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        }
        if (StringUtils.isNotBlank(name)) {
            values.put("name", "%" + name + "%");
            hql += " and t.name like :name ";
        }
        hql += " order by code asc ";
        return this.listByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanService#setAsDefaultByUuidAndSystemUnitId(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void setAsDefaultByUuidAndSystemUnitId(String uuid, String systemUnitId) {
        String system = RequestSystemContextPathResolver.system();
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        values.put("systemUnitId", systemUnitId);
        values.put("systemId", system);
        String hql1 = "update TsWorkTimePlanEntity t set t.isDefault = false where t.systemUnitId = :systemUnitId";
        String hql2 = "update TsWorkTimePlanEntity t set t.isDefault = true where t.uuid = :uuid";
        if (StringUtils.isNotBlank(system)) {
            hql1 += " and t.system = :systemId";
            hql2 += " and t.system = :systemId";
        }
        this.dao.updateByHQL(hql1, values);
        this.dao.updateByHQL(hql2, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanService#setIsDefaultByUuid(java.lang.String, boolean)
     */
    @Override
    @Transactional
    public void setIsDefaultByUuid(String uuid, boolean isDefault) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        values.put("isDefault", isDefault);
        String hql = "update TsWorkTimePlanEntity t set t.isDefault = :isDefault where t.uuid = :uuid";
        this.dao.updateByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.support.TsHolidayUsedChecker#isHolidayUsed(java.lang.String)
     */
    @Override
    public boolean isHolidayUsed(String holidayUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("holidayUuid", holidayUuid);
        String hql = "select t.uuid from TsWorkTimePlanEntity t where t.holidaySchedule like '%" + holidayUuid + "%'";
        return this.dao.countByHQL(hql, values) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanService#syncWorkTimePlanStatus()
     */
    @Override
    @Transactional
    public void syncWorkTimePlanStatus() {
        Map<String, Object> values = Maps.newHashMap();
        Date currentTime = Calendar.getInstance().getTime();
        values.put("currentTime", currentTime);
        values.put("status", EnumWorkTimePlanStatus.READY.getValue());
        String hql = "from TsWorkTimePlanEntity t where t.activeTime <= :currentTime and t.status = :status order by t.createTime desc";
        List<TsWorkTimePlanEntity> workTimePlanEntities = this.listByHQL(hql, values);
        // 更新工作方案状态
        for (TsWorkTimePlanEntity workTimePlanEntity : workTimePlanEntities) {
            // 旧版本失效
            List<TsWorkTimePlanEntity> activeEntities = listActiveWorkTimePlanById(workTimePlanEntity.getId());
            for (TsWorkTimePlanEntity activeWorkTimePlanEntity : activeEntities) {
                activeWorkTimePlanEntity.setDeactiveTime(workTimePlanEntity.getActiveTime());
                activeWorkTimePlanEntity.setStatus(EnumWorkTimePlanStatus.Deactive.getValue());
            }
            this.saveAll(activeEntities);
            // 历史版本失效
            deactiveWorkTimePlanHistory(activeEntities, workTimePlanEntity.getActiveTime());

            // 新版本生效
            workTimePlanEntity.setStatus(EnumWorkTimePlanStatus.Active.getValue());
            this.save(workTimePlanEntity);
            // 历史版本生效
            activeWorkTimePlanHistory(workTimePlanEntity);
        }
    }

    /**
     * @param activeEntities
     * @param deactiveTime
     */
    private void deactiveWorkTimePlanHistory(List<TsWorkTimePlanEntity> activeEntities, Date deactiveTime) {
        for (TsWorkTimePlanEntity workTimePlanEntity : activeEntities) {
            workTimePlanHistoryService.deactiveByWorkTimePlanUuid(workTimePlanEntity.getUuid(), deactiveTime);
        }
    }

    /**
     * @param workTimePlanEntity
     */
    private void activeWorkTimePlanHistory(TsWorkTimePlanEntity workTimePlanEntity) {
        TsWorkTimePlanHistoryEntity entity = new TsWorkTimePlanHistoryEntity();
        entity.setWorkTimePlanUuid(workTimePlanEntity.getUuid());
        List<TsWorkTimePlanHistoryEntity> historyEntities = workTimePlanHistoryService.listByEntity(entity);
        for (TsWorkTimePlanHistoryEntity workTimePlanHistoryEntity : historyEntities) {
            workTimePlanHistoryEntity.setStatus(EnumWorkTimePlanStatus.Active.getValue());
        }
        workTimePlanHistoryService.saveAll(historyEntities);
    }

    /**
     * @param id
     * @return
     */
    private List<TsWorkTimePlanEntity> listActiveWorkTimePlanById(String id) {
        TsWorkTimePlanEntity entity = new TsWorkTimePlanEntity();
        entity.setId(id);
        entity.setStatus(EnumWorkTimePlanStatus.Active.getValue());
        return this.listByEntity(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanService#getDefaultWorkTimePlan()
     */
    @Override
    public TsWorkTimePlanEntity getDefaultWorkTimePlan() {
        TsWorkTimePlanEntity entity = new TsWorkTimePlanEntity();
        entity.setIsDefault(true);
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        if (StringUtils.isNotBlank(systemUnitId)) {
            entity.setSystemUnitId(systemUnitId);
        }
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            entity.setSystem(system);
        }
        List<TsWorkTimePlanEntity> entities = this.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        throw new RuntimeException("默认工作时间方案没有配置！");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanService#getActiveWorkTimePlanById(java.lang.String)
     */
    @Override
    public TsWorkTimePlanEntity getActiveWorkTimePlanById(String id) {
        String hql = "from TsWorkTimePlanEntity t where t.id = :id and t.status = :status order by t.createTime desc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("id", id);
        values.put("status", EnumWorkTimePlanStatus.Active.getValue());
        List<TsWorkTimePlanEntity> entities = this.listByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanService#getLatestToBeActiveVersionById(java.lang.String)
     */
    @Override
    public TsWorkTimePlanEntity getLatestToBeActiveVersionById(String id) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("id", id);
        values.put("status", EnumWorkTimePlanStatus.READY.getValue());
        String hql = "from TsWorkTimePlanEntity t where t.id = :id and t.status = :status order by t.createTime asc";
        List<TsWorkTimePlanEntity> entities = this.listByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanService#getLatestToBeActiveVersionByUuid(java.lang.String)
     */
    @Override
    public TsWorkTimePlanEntity getLatestToBeActiveVersionByUuid(String uuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        values.put("status", EnumWorkTimePlanStatus.READY.getValue());
        String hql = "from TsWorkTimePlanEntity t where t.id = (select id from TsWorkTimePlanEntity where uuid = :uuid) and t.status = :status order by t.createTime asc";
        List<TsWorkTimePlanEntity> entities = this.listByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsWorkTimePlanService#getMaxVersionByUuid(java.lang.String)
     */
    @Override
    public String getMaxVersionByUuid(String uuid) {
        String hql = "select max(t.version) as version from TsWorkTimePlanEntity t where t.id in(select id from "
                + "TsWorkTimePlanEntity where uuid = :uuid)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        List<String> versions = this.dao.listCharSequenceByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(versions)) {
            return versions.get(0);
        }
        return null;
    }

    @Override
    public List<TsWorkTimePlanEntity> getAllBySystem(List<String> system) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("system", system);
        values.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        return dao.listByHQL("from TsWorkTimePlanEntity where system in :system and tenant=:tenant", values);
    }

    @Override
    public String getIdByUuid(String uuid) {
        String hql = "select t.id as id from TsWorkTimePlanEntity t where t.uuid = :uuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        List<String> ids = this.dao.listCharSequenceByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(ids)) {
            return ids.get(0);
        }
        return null;
    }

}
