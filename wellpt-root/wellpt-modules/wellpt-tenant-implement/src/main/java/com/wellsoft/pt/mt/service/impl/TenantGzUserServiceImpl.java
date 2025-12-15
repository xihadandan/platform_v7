/*
 * @(#)2015-08-14 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.mt.entity.TenantGzUser;
import com.wellsoft.pt.mt.service.TenantGzUserService;
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
 * 2015-08-14.1	zhulh		2015-08-14		Create
 * </pre>
 * @date 2015-08-14
 */
@Service
@Transactional
public class TenantGzUserServiceImpl extends BaseServiceImpl implements TenantGzUserService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantGzUserService#get(java.lang.String)
     */
    @Override
    public TenantGzUser get(String uuid) {
        return this.getCommonDao().get(TenantGzUser.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantGzUserService#getAll()
     */
    @Override
    public List<TenantGzUser> getAll() {
        return this.getCommonDao().getAll(TenantGzUser.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantGzUserService#findByExample(TenantGzUser)
     */
    @Override
    public List<TenantGzUser> findByExample(TenantGzUser example) {
        return this.getCommonDao().findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantGzUserService#save(com.wellsoft.pt.mt.entity.TenantGzUser)
     */
    @Override
    public void save(TenantGzUser entity) {
        this.getCommonDao().save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantGzUserService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<TenantGzUser> entities) {
        this.getCommonDao().saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantGzUserService#getOthers(java.lang.String, java.lang.String)
     */
    @Override
    public List<TenantGzUser> getTenantGzUser(String userId, String tenantId) {
        // 如果是主职租户，返回所有
        TenantGzUser example = new TenantGzUser();
        example.setUserId(userId);
        example.setZzTenantId(tenantId);
        List<TenantGzUser> tenantGzUsers = this.getCommonDao().findByExample(example);
        if (tenantGzUsers.size() > 0) {
            return tenantGzUsers;
        }

        // 如果是挂职租户，返回所有
        example = new TenantGzUser();
        example.setUserId(userId);
        example.setGzTenantId(tenantId);
        tenantGzUsers = this.getCommonDao().findByExample(example);
        return tenantGzUsers;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantGzUserService#deleteById(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteById(String userId, String tenantId) {
        // 主职账号存在挂职账号，不能删除
        TenantGzUser example = new TenantGzUser();
        example.setUserId(userId);
        example.setZzTenantId(tenantId);
        List<TenantGzUser> tenantGzUsers = this.getCommonDao().findByExample(example);
        if (tenantGzUsers.size() > 0) {
            throw new RuntimeException("用户在租户[" + tenantGzUsers.get(0).getGzTenantName() + "]挂职，不能删除!");
        }

        // 删除挂职账号
        example = new TenantGzUser();
        example.setUserId(userId);
        example.setGzTenantId(tenantId);
        tenantGzUsers = this.getCommonDao().findByExample(example);
        this.getCommonDao().deleteAll(tenantGzUsers);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantGzUserService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.getCommonDao().deleteByPk(TenantGzUser.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.TenantGzUserService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<TenantGzUser> entities) {
        this.getCommonDao().deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantGzUserService#remove(TenantGzUser)
     */
    @Override
    public void remove(TenantGzUser entity) {
        this.getCommonDao().delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantGzUserService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.getCommonDao().deleteAllByPk(TenantGzUser.class, list);
    }

}
