/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.dao.DmsObjectAssignRoleDao;
import com.wellsoft.pt.dms.entity.DmsObjectAssignRoleEntity;
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
public interface DmsObjectAssignRoleService extends JpaService<DmsObjectAssignRoleEntity, DmsObjectAssignRoleDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    DmsObjectAssignRoleEntity get(String uuid);

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<DmsObjectAssignRoleEntity> findByExample(DmsObjectAssignRoleEntity example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(DmsObjectAssignRoleEntity entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<DmsObjectAssignRoleEntity> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(DmsObjectAssignRoleEntity entity);

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
     * @param objectIdentityUuid
     */
    void removeByObjectIdentityUuid(String objectIdentityUuid);

    /**
     * @param roleUuids
     * @return
     */
    List<DmsObjectAssignRoleEntity> listByRoleUuids(List<String> roleUuids);
}
