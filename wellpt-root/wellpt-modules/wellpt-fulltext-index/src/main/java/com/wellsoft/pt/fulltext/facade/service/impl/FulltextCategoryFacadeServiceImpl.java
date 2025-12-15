/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.facade.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.entity.SysEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.tree.TreeUtils;
import com.wellsoft.pt.fulltext.dto.FulltextCategoryDto;
import com.wellsoft.pt.fulltext.entity.FulltextCategoryEntity;
import com.wellsoft.pt.fulltext.facade.service.FulltextCategoryFacadeService;
import com.wellsoft.pt.fulltext.service.FulltextCategoryService;
import com.wellsoft.pt.fulltext.service.FulltextModelService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/13/25.1	    zhulh		6/13/25		    Create
 * </pre>
 * @date 6/13/25
 */
@Service
public class FulltextCategoryFacadeServiceImpl extends AbstractApiFacade implements FulltextCategoryFacadeService {

    private static final String CACHE_NAME = "Basic Data";

    @Autowired
    private FulltextCategoryService fulltextCategoryService;

    @Autowired
    private FulltextModelService fulltextModelService;

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public Long saveDto(FulltextCategoryDto dto) {
        FulltextCategoryEntity entity = null;
        if (dto.getUuid() == null) {
            entity = new FulltextCategoryEntity();
        } else {
            entity = fulltextCategoryService.getOne(dto.getUuid());
        }
        BeanUtils.copyProperties(dto, entity, SysEntity.BASE_FIELDS);
        entity.setSystem(RequestSystemContextPathResolver.system());
        entity.setTenant(SpringSecurityUtils.getCurrentTenantId());

        fulltextCategoryService.save(entity);
        return entity.getUuid();
    }

    @Override
    public FulltextCategoryDto getDto(Long uuid) {
        FulltextCategoryDto dto = new FulltextCategoryDto();
        FulltextCategoryEntity entity = fulltextCategoryService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    @Override
    public List<TreeNode> listAsTreeByModuleId(String moduleId) {
        if (StringUtils.isBlank(moduleId)) {
            return Collections.emptyList();
        }

        List<FulltextCategoryEntity> entities = fulltextCategoryService.listByModuleId(moduleId);
        TreeNode treeNode = TreeUtils.buildTree(entities, "uuid", "parentUuid", new Function<FulltextCategoryEntity, TreeNode>() {
            @Override
            public TreeNode apply(FulltextCategoryEntity entity) {
                TreeNode node = new TreeNode();
                node.setId(entity.getUuid().toString());
                node.setName(entity.getName());
                node.setData(entity);
                return node;
            }
        });
        return (TreeNode.ROOT_ID.equals(treeNode.getId()) || StringUtils.isBlank(treeNode.getId()))
                ? treeNode.getChildren() : Collections.singletonList(treeNode);
    }

    @Override
    public List<FulltextCategoryDto> listBySystem(String systemId) {
        if (StringUtils.isBlank(systemId)) {
            return Collections.emptyList();
        }

        List<FulltextCategoryEntity> entities = fulltextCategoryService.listBySystem(systemId);
        return BeanUtils.copyCollection(entities, FulltextCategoryDto.class);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void deleteByUuid(Long uuid) {
        List<Long> categoryUuids = Lists.newArrayList(uuid);
        List<Long> childrenUuids = fulltextCategoryService.listUuidByParentUuids(categoryUuids);
        while (CollectionUtils.isNotEmpty(childrenUuids)) {
            categoryUuids.addAll(childrenUuids);
            childrenUuids = fulltextCategoryService.listUuidByParentUuids(childrenUuids);
        }
        this.fulltextModelService.deleteByCategoryUuids(categoryUuids);
        this.fulltextCategoryService.deleteByUuids(categoryUuids);
    }

    @Override
    @Cacheable(value = CACHE_NAME)
    public List<Long> listUuidBySystem(String systemId) {
        if (StringUtils.isBlank(systemId)) {
            return Collections.emptyList();
        }

        List<FulltextCategoryEntity> entities = fulltextCategoryService.listBySystem(systemId);
        return entities.stream().map(FulltextCategoryEntity::getUuid).collect(Collectors.toList());
    }

}
