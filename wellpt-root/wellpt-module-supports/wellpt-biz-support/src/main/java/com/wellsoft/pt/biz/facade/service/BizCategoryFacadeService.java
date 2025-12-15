/*
 * @(#)9/28/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.biz.dto.BizCategoryDto;

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
 * 9/28/22.1	zhulh		9/28/22		Create
 * </pre>
 * @date 9/28/22
 */
public interface BizCategoryFacadeService extends Facade {
    /**
     * 获取业务分类及业务树
     *
     * @return
     */
    TreeNode getCategoryAndBusinessTree();

    /**
     * 保存业务分类
     *
     * @param dto
     */
    void saveDto(BizCategoryDto dto);

    /**
     * 根据业务分类UUID列表删除业务分类
     *
     * @param uuids
     */
    void deleteAll(List<String> uuids);
}
