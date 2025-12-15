/*
 * @(#)9/28/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.google.common.base.Function;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.tree.TreeUtils;
import com.wellsoft.pt.biz.dto.BizCategoryDto;
import com.wellsoft.pt.biz.entity.BizBusinessEntity;
import com.wellsoft.pt.biz.entity.BizCategoryEntity;
import com.wellsoft.pt.biz.facade.service.BizCategoryFacadeService;
import com.wellsoft.pt.biz.service.BizBusinessService;
import com.wellsoft.pt.biz.service.BizCategoryService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class BizCategoryFacadeServiceImpl extends AbstractApiFacade implements BizCategoryFacadeService {

    @Autowired
    private BizCategoryService bizCategoryService;

    @Autowired
    private BizBusinessService bizBusinessService;

    /**
     * 获取业务分类及业务树
     *
     * @return
     */
    @Override
    public TreeNode getCategoryAndBusinessTree() {
        // 所有业务分类
        List<BizCategoryEntity> bizCategoryEntities = bizCategoryService.listAllByOrderPage(new PagingInfo(1, Integer.MAX_VALUE), "code asc");
        List<BizBusinessEntity> bizBusinessEntities = null;
        if (CollectionUtils.isNotEmpty(bizCategoryEntities)) {
            List<String> categoryUuids = bizCategoryEntities.stream().map(r -> r.getUuid()).collect(Collectors.toList());
            bizBusinessEntities = bizBusinessService.listByCategoryUuids(categoryUuids);
        }

        // 业务分类树
        TreeNode treeNode = new TreeNode("-1", "业务分类", "");
        for (BizCategoryEntity entity : bizCategoryEntities) {
            TreeNode child = new TreeNode();
            child.setId(entity.getUuid());
            child.setName(entity.getName());
            child.setType("category");
            child.setData(entity);
            treeNode.getChildren().add(child);
        }

        // 业务分类节点下添加业务节点
        if (CollectionUtils.isNotEmpty(bizBusinessEntities)) {
            Map<String, List<BizBusinessEntity>> bizBusinessMap = ListUtils.list2group(bizBusinessEntities, "categoryUuid");
            TreeUtils.traverseTree(treeNode, new Function<TreeNode, Void>() {
                @Nullable
                @Override
                public Void apply(@Nullable TreeNode input) {
                    // 分类节点下添加业务节点
                    if (bizBusinessMap.containsKey(input.getId())) {
                        List<BizBusinessEntity> businessEntities = bizBusinessMap.get(input.getId());
                        for (BizBusinessEntity entity : businessEntities) {
                            TreeNode child = new TreeNode();
                            child.setId(entity.getUuid());
                            child.setName(entity.getName());
                            child.setType("business");
                            child.setData(entity);
                            input.getChildren().add(child);
                        }
                    }
                    return null;
                }
            });
        }
        return treeNode;
    }

    /**
     * 保存业务分类
     *
     * @param dto
     */
    @Override
    public void saveDto(BizCategoryDto dto) {
        BizCategoryEntity entity = new BizCategoryEntity();
        if (StringUtils.isNotBlank(dto.getUuid())) {
            entity = bizCategoryService.getOne(dto.getUuid());
        } else {
            // ID唯一性判断
            if (this.bizCategoryService.countById(dto.getId()) > 0) {
                throw new RuntimeException(String.format("已经存在ID为[%s]的业务分类!", dto.getId()));
            }
        }
        BeanUtils.copyProperties(dto, entity);
        bizCategoryService.save(entity);
    }

    /**
     * 根据业务分类UUID列表删除业务分类
     *
     * @param uuids
     */
    @Override
    public void deleteAll(List<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        if (isUsedByBusiness(uuids)) {
            throw new BusinessException("业务分类下存在业务，无法删除！");
        }
        bizCategoryService.deleteByUuids(uuids);
    }

    /**
     * 判断业务分类下是否存在业务
     *
     * @param uuids
     * @return
     */
    private boolean isUsedByBusiness(List<String> uuids) {
        return bizBusinessService.countByCategoryUuids(uuids) > 0;
    }

}
