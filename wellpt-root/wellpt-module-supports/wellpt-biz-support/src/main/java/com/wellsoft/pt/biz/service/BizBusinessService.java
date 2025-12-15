/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizBusinessDao;
import com.wellsoft.pt.biz.entity.BizBusinessEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
public interface BizBusinessService extends JpaService<BizBusinessEntity, BizBusinessDao, String> {

    /**
     * 根据ID获取业务
     *
     * @param id
     * @return
     */
    BizBusinessEntity getById(String id);

    /**
     * 根据ID获取数量
     *
     * @param id
     * @return
     */
    Long countById(String id);

    /**
     * 根据业务分类UUID列表，获取业务
     *
     * @param categoryUuids
     * @return
     */
    List<BizBusinessEntity> listByCategoryUuids(List<String> categoryUuids);

    /**
     * 根据业务分类UUID列表，获取数量
     *
     * @param categoryUuids
     * @return
     */
    Long countByCategoryUuids(List<String> categoryUuids);

    /**
     * 根据UUID列表，获取ID列表
     *
     * @param uuids
     * @return
     */
    List<String> listIdByUuids(List<String> uuids);

    /**
     * 根据业务分类UUID，获取ID列表
     *
     * @param categoryUuid
     * @return
     */
    List<String> listIdByCategoryUuid(String categoryUuid);

}
