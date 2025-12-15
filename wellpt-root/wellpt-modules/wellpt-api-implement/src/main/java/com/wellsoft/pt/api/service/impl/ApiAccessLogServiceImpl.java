/*
 * @(#)2015-09-23 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.service.impl;

import com.wellsoft.pt.api.entity.ApiAccessLog;
import com.wellsoft.pt.api.service.ApiAccessLogService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-09-23.1	zhulh		2015-09-23		Create
 * </pre>
 * @date 2015-09-23
 */
@Service
@Transactional
public class ApiAccessLogServiceImpl extends BaseServiceImpl implements ApiAccessLogService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.service.ApiAccessLogService#get(java.lang.String)
     */
    @Override
    public ApiAccessLog get(String uuid) {
        return this.getCommonDao().get(ApiAccessLog.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.service.ApiAccessLogService#getAll()
     */
    @Override
    public List<ApiAccessLog> getAll() {
        return this.getCommonDao().getAll(ApiAccessLog.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.service.ApiAccessLogService#findByExample(ApiAccessLog)
     */
    @Override
    public List<ApiAccessLog> findByExample(ApiAccessLog example) {
        return this.getCommonDao().findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.service.ApiAccessLogService#save(com.wellsoft.pt.api.entity.ApiAccessLog)
     */
    @Override
    public void save(ApiAccessLog entity) {
        this.getCommonDao().save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.service.ApiAccessLogService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<ApiAccessLog> entities) {
        this.getCommonDao().saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.service.ApiAccessLogService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.getCommonDao().deleteByPk(ApiAccessLog.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.ApiAccessLogService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<ApiAccessLog> entities) {
        this.getCommonDao().deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.service.ApiAccessLogService#remove(ApiAccessLog)
     */
    @Override
    public void remove(ApiAccessLog entity) {
        this.getCommonDao().delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.service.ApiAccessLogService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.getCommonDao().deleteAllByPk(ApiAccessLog.class, list);
    }

}
