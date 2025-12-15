/*
 * @(#)9/28/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.google.common.base.Function;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.tree.TreeUtils;
import com.wellsoft.pt.biz.dto.BizBusinessDto;
import com.wellsoft.pt.biz.entity.BizBusinessEntity;
import com.wellsoft.pt.biz.facade.service.BizBusinessFacadeService;
import com.wellsoft.pt.biz.facade.service.BizCategoryFacadeService;
import com.wellsoft.pt.biz.service.BizBusinessService;
import com.wellsoft.pt.biz.service.BizItemDefinitionService;
import com.wellsoft.pt.biz.service.BizProcessDefinitionService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
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
@Service
public class BizBusinessFacadeServiceImpl extends AbstractApiFacade implements BizBusinessFacadeService {

    @Autowired
    private BizBusinessService bizBusinessService;

    @Autowired
    private BizProcessDefinitionService bizProcessDefinitionService;

    @Autowired
    private BizItemDefinitionService itemDefinitionService;

    @Autowired
    private BizCategoryFacadeService bizCategoryFacadeService;

    /**
     * 获取业务树
     *
     * @return
     */
    @Override
    public TreeNode getBusinessTree() {
        TreeNode treeNode = bizCategoryFacadeService.getCategoryAndBusinessTree();
        // 业务结点的ID设置为ID
        TreeUtils.traverseTree(treeNode, new Function<TreeNode, Void>() {
            @Nullable
            @Override
            public Void apply(@Nullable TreeNode input) {
                if (StringUtils.equals("business", input.getType()) && input.getData() instanceof BizBusinessEntity) {
                    BizBusinessEntity entity = (BizBusinessEntity) input.getData();
                    input.setId(entity.getId());
                }
                return null;
            }
        });
        return treeNode;
    }

    /**
     * 保存业务
     *
     * @param dto
     */
    @Override
    public void saveDto(BizBusinessDto dto) {
        BizBusinessEntity entity = new BizBusinessEntity();
        if (StringUtils.isNotBlank(dto.getUuid())) {
            entity = bizBusinessService.getOne(dto.getUuid());
        } else {
            // ID唯一性判断
            if (this.bizBusinessService.countById(dto.getId()) > 0) {
                throw new RuntimeException(String.format("已经存在ID为[%s]的业务!", dto.getId()));
            }
        }
        BeanUtils.copyProperties(dto, entity);
        bizBusinessService.save(entity);
    }

    /**
     * 根据业务UUID列表删除业务
     *
     * @param uuids
     */
    @Override
    public void deleteAll(List<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        List<String> ids = bizBusinessService.listIdByUuids(uuids);
        // 是否被业务流程定义使用
        if (isUsedByProcessDefinition(ids)) {
            throw new BusinessException("业务下存在业务流程定义，无法删除！");
        }
        // 是否被业务事项定义使用
        if (isUsedByItemDefinition(ids)) {
            throw new BusinessException("业务下存在业务事项定义，无法删除！");
        }
        bizBusinessService.deleteByUuids(uuids);
    }

    private boolean isUsedByProcessDefinition(List<String> ids) {
        return bizProcessDefinitionService.countByBusinessIds(ids) > 0;
    }

    private boolean isUsedByItemDefinition(List<String> ids) {
        return itemDefinitionService.countByBusinessIds(ids) > 0;
    }

}
