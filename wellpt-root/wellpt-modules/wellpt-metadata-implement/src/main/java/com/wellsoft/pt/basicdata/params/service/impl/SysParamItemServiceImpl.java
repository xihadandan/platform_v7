/*
 * @(#)2015-07-20 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.params.service.impl;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.params.core.precompressor.PrecompressorCenter;
import com.wellsoft.pt.basicdata.params.dao.SysParamItemDao;
import com.wellsoft.pt.basicdata.params.entity.SysParamItem;
import com.wellsoft.pt.basicdata.params.event.SysPatamItemRemoveEvent;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import com.wellsoft.pt.basicdata.params.service.SysParamInit;
import com.wellsoft.pt.basicdata.params.service.SysParamItemService;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-07-20.1	Lmw		2015-07-20		Create
 * </pre>
 * @date 2015-07-20
 */
@Service
public class SysParamItemServiceImpl extends AbstractJpaServiceImpl<SysParamItem, SysParamItemDao, String> implements
        SysParamItemService {

    @Autowired
    private CacheManager cacheManager;

    @SuppressWarnings("unchecked")
    private void init() {
        ApplicationContextHolder.getBean(SysParamInit.class).init();
    }

    @Transactional
    private void saveInit(Collection<SysParamItem> entities) {
        this.dao.saveAll(entities);
        ApplicationContextHolder.getBean(SysParamInit.class).init();
    }

    private void flushCache(String key, String value, String uuid) {
        Cache cache = cacheManager.getCache(ModuleID.SECURITY);
        cache.put(SystemParams.getCacheKey(key), value);
    }

    private void remveCache(SysParamItem paramItem) {
        init();
        ApplicationContextHolder.publishEvent(new SysPatamItemRemoveEvent(this, paramItem));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.params.service.SysParamItemService#get(java.lang.String)
     */
    @Override
    public SysParamItem get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.params.service.SysParamItemService#getAll()
     */
    @Override
    @Transactional(readOnly = true)
    public List<SysParamItem> getAll() {
        return listAll();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.params.service.SysParamItemService#findByExample(SysParamItem)
     */
    @Override
    public List<SysParamItem> findByExample(SysParamItem example) {
        return this.dao.listByEntity(example);
    }

    private void check(SysParamItem entity) {
        // 修改
        if (StringUtils.isNotBlank(entity.getUuid())) {
            switch (entity.getSourcetype()) {
                case PrecompressorCenter.DB:
                    break;
                case PrecompressorCenter.JVM:
                case PrecompressorCenter.PROP:
                    // entity.setValue(null);
                    break;
                default:
                    throw new RuntimeException("sourcetype is not support");
            }
        } else {
            if (isExistKey(entity.getKey()) > 0) {
                throw new RuntimeException("key [" + entity.getKey() + "] is exist");
            }
        }
    }

    private void check(Collection<SysParamItem> entities) {
        for (SysParamItem item : entities) {
            check(item);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.params.service.SysParamItemService#save(com.wellsoft.pt.params.entity.SysParamItem)
     */
    @Override
    @Transactional
    public void save(SysParamItem entity) {
        check(entity);
        this.dao.save(entity);
        flushCache(entity.getKey(), entity.getValue(), entity.getUuid());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.params.service.SysParamItemService#saveAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void saveAll(Collection<SysParamItem> entities) {
        check(entities);
        this.dao.saveAll(entities);
        for (SysParamItem item : entities) {
            flushCache(item.getKey(), item.getValue(), item.getUuid());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.params.service.SysParamItemService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        SysParamItem entity = this.dao.getOne(uuid);
        remveCache(entity);
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.params.SysParamItemService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<SysParamItem> entities) {
        for (SysParamItem item : entities) {
            remveCache(item);
        }
        this.dao.deleteByEntities(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.params.service.SysParamItemService#remove(SysParamItem)
     */
    @Override
    @Transactional
    public void remove(SysParamItem entity) {
        remveCache(entity);
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.params.service.SysParamItemService#removeAllByPk(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        for (String uuid : uuids) {
            remove(uuid);
        }
    }

    @Override
    @Transactional
    public void saveValue(SysParamItem entity) {
        if (isExistKey(entity.getKey()) > 0) {
            updateValue(entity.getKey(), entity.getValue());
        } else {
            this.dao.save(entity);
        }
    }

    public int size(SysParamItem sysParamItem) {
        StringBuilder stringBuilder = new StringBuilder("select count(uuid) from SysParamItem u where (1=1)");
        Map<String, Object> rMap = new HashMap<String, Object>();
        if (sysParamItem != null) {
            if (sysParamItem.getUuid() != null) {
                stringBuilder.append(" and u.uuid=:uuid");
                rMap.put("uuid", sysParamItem.getUuid());
            }
            if (sysParamItem.getKey() != null) {
                stringBuilder.append(" and u.key=:key");
                rMap.put("key", sysParamItem.getKey());
            }
        }
        return this.dao.getNumberByHQL(stringBuilder.toString(), rMap).intValue();
    }

    /**
     * 删除不是指定类型的记录
     *
     * @param type
     */
    public void deleteIsNotType(int type) {
        StringBuilder stringBuilder = new StringBuilder(" delete SysParamItem u where u.sourcetype=:sourcetype");
        Map<String, Object> rMap = new HashMap<String, Object>();
        rMap.put("sourcetype", type);
        this.dao.deleteByHQL(stringBuilder.toString(), rMap);
    }

    /**
     * 查询指定key的记录数
     *
     * @param key
     * @return
     */
    public int isExistKey(String key) {
        StringBuilder stringBuilder = new StringBuilder("select count(uuid) from SysParamItem u  where u.key=:key");
        Map<String, Object> rMap = new HashMap<String, Object>();
        rMap.put("key", key);
        return this.dao.getNumberByHQL(stringBuilder.toString(), rMap).intValue();
    }

    /**
     * 跟新指定键的值
     *
     * @param key
     * @param value
     */
    @Transactional
    public void updateValue(String key, String value) {
        StringBuilder stringBuilder = new StringBuilder(" update SysParamItem u set u.value=:value where u.key=:key");
        Map<String, Object> rMap = new HashMap<String, Object>();
        rMap.put("key", key);
        rMap.put("value", value);
        this.updateByHQL(stringBuilder.toString(), rMap);
    }
}
