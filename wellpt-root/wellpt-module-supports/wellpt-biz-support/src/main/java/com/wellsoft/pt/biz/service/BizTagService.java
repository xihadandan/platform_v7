/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizTagDao;
import com.wellsoft.pt.biz.entity.BizTagEntity;
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
public interface BizTagService extends JpaService<BizTagEntity, BizTagDao, String> {
    /**
     * 根据ID列表获取业务标签
     *
     * @param ids
     * @return
     */
    List<BizTagEntity> listByIds(List<String> ids);

    /**
     * 根据ID获取数量
     *
     * @param id
     * @return
     */
    Long countById(String id);

    /**
     * @param entity
     * @return
     */
    long countByEntity(BizTagEntity entity);

}
