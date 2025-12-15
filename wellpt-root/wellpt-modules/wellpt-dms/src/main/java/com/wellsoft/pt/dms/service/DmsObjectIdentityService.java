/*
 * @(#)2017-12-27 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.dms.dao.DmsObjectIdentityDao;
import com.wellsoft.pt.dms.entity.DmsObjectIdentityEntity;
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
 * 2017-12-27.1	zhulh		2017-12-27		Create
 * </pre>
 * @date 2017-12-27
 */
public interface DmsObjectIdentityService extends JpaService<DmsObjectIdentityEntity, DmsObjectIdentityDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    DmsObjectIdentityEntity get(String uuid);

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<DmsObjectIdentityEntity> findByExample(DmsObjectIdentityEntity example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(DmsObjectIdentityEntity entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<DmsObjectIdentityEntity> entities);

    /**
     * 获取或创建对象实体
     *
     * @param idEntity
     * @return
     */
    DmsObjectIdentityEntity getOrCreate(IdEntity idEntity);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(DmsObjectIdentityEntity entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<DmsObjectIdentityEntity> entities);

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
     * 根据业务主体标识删除记录
     *
     * @param objectIdIdentity
     */
    void removeByObjectIdIdentity(String objectIdIdentity);
}
