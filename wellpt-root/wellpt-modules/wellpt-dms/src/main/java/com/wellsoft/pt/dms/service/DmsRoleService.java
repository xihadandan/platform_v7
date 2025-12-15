/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.basicdata.selective.support.DataItem;
import com.wellsoft.pt.dms.dao.DmsRoleDao;
import com.wellsoft.pt.dms.entity.DmsRoleEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Collection;
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
 * 2017-12-19.1	zhulh		2017-12-19		Create
 * </pre>
 * @date 2017-12-19
 */
public interface DmsRoleService extends JpaService<DmsRoleEntity, DmsRoleDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    DmsRoleEntity get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<DmsRoleEntity> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<DmsRoleEntity> findByExample(DmsRoleEntity example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(DmsRoleEntity entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<DmsRoleEntity> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(DmsRoleEntity entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<DmsRoleEntity> entities);

    /**
     * 根据UUID删除记录
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuids
     */
    void removeAllByPk(Collection<String> uuids);

    /**
     * 如何描述该方法
     *
     * @return
     */
    DmsRoleEntity getAdminRole();

    /**
     * 判断夹操作权限定义是否被引用
     *
     * @param uuid
     * @return
     */
    boolean isUse(String uuid);

    /**
     * 如何描述该方法
     *
     * @param category
     * @return
     */
    List<DmsRoleEntity> getByCategory(String category);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<DataItem> getRoleCategories();


    DmsRoleEntity getById(String id);

    /**
     * @param system
     * @return
     */
    List<DmsRoleEntity> listBySystem(String system);

    /**
     * @param system
     * @param tenant
     */
    void initBySystemAndTenant(String system, String tenant);

    /**
     * @param id
     * @param system
     * @param currentTenantId
     * @return
     */
    DmsRoleEntity getInitById(String id, String system, String currentTenantId);

    /**
     * @param ids
     * @param system
     * @return
     */
    List<DmsRoleEntity> listByIdsAndSystem(List<String> ids, String system);

}
