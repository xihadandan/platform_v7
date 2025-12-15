/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.org.bean.OrgCorporationBean;
import com.wellsoft.pt.org.entity.OrgCorporation;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-11.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
public interface OrgCorporationService extends BaseService {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    OrgCorporation get(String uuid);

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    OrgCorporationBean getBean(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<OrgCorporation> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<OrgCorporation> findByExample(OrgCorporation example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(OrgCorporation entity);

    /**
     * 保存
     *
     * @param bean
     */
    void saveBean(OrgCorporationBean bean);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<OrgCorporation> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(OrgCorporation entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<OrgCorporation> entities);

    /**
     * 根据UUID删除记录
     *
     * @param uuid
     */
    void removeByPk(String uuid);

    /**
     * 批量删除
     *
     * @param uuids
     */
    void removeAllByPk(Collection<String> uuids);

}
