/*
 * @(#)1/31/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.pt.dyform.implement.definition.dao.DyformCategoryDao;
import com.wellsoft.pt.dyform.implement.definition.entity.DyformCategoryEntity;
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
 * 1/31/24.1	zhulh		1/31/24		Create
 * </pre>
 * @date 1/31/24
 */
public interface DyformCategoryService extends JpaService<DyformCategoryEntity, DyformCategoryDao, Long>, Select2QueryApi {

    /**
     * 根据模块ID获取流程分类
     *
     * @param moduleId
     * @return
     */
    List<DyformCategoryEntity> listByModuleId(String moduleId);

    /**
     * 根据关键字、模块ID获取流程分类
     *
     * @param keyword
     * @param moduleId
     * @return
     */
    List<DyformCategoryEntity> listByKeywordAndModuleId(String keyword, String moduleId);
}
