/*
 * @(#)7/12/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberCategoryDto;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberDefinitionDto;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberDefinitionEntity;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SnSerialNumberCategoryFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SnSerialNumberDefinitionFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberDefinitionService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
 * 7/12/22.1	zhulh		7/12/22		Create
 * </pre>
 * @date 7/12/22
 */
@Service
public class SnSerialNumberDefinitionFacadeServiceImpl extends AbstractApiFacade implements SnSerialNumberDefinitionFacadeService {
    @Autowired
    private SnSerialNumberDefinitionService snSerialNumberDefinitionService;

    @Autowired
    private CommonValidateService commonValidateService;

    @Autowired
    private SnSerialNumberCategoryFacadeService snSerialNumberCategoryFacadeService;

    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    /**
     * 根据UUID获取流水号定义
     *
     * @param uuid
     * @return
     */
    @Override
    public SnSerialNumberDefinitionDto getDto(String uuid) {
        SnSerialNumberDefinitionDto dto = new SnSerialNumberDefinitionDto();
        SnSerialNumberDefinitionEntity entity = snSerialNumberDefinitionService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
            dto.setI18ns(appDefElementI18nService.getI18ns(entity.getId(), null, new BigDecimal(1), IexportType.SnSerialNumberDefinition));
        }
        return dto;
    }

    /**
     * 根据ID获取流水号定义
     *
     * @param id
     * @return
     */
    @Override
    public SnSerialNumberDefinitionDto getById(String id) {
        SnSerialNumberDefinitionDto dto = new SnSerialNumberDefinitionDto();
        SnSerialNumberDefinitionEntity entity = snSerialNumberDefinitionService.getById(id);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * 获取所有流水号定义
     *
     * @return
     */
    @Override
    public List<SnSerialNumberDefinitionDto> listAll() {
        String hql = "from SnSerialNumberDefinitionEntity t where 1 = 1";
        String system = RequestSystemContextPathResolver.system();
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(system)) {
            hql += " and (exists(select pm.moduleId from AppProdModuleEntity pm, AppSystemInfoEntity si " +
                    "where pm.prodVersionUuid = si.prodVersionUuid and t.moduleId = pm.moduleId and si.system = :system) or t.system = :system) ";
            params.put("system", system);
        }
        hql += " order by t.code asc ";
        List<SnSerialNumberDefinitionEntity> entities = snSerialNumberDefinitionService.listByHQL(hql, params);// snSerialNumberDefinitionService.listAllByOrderPage(new PagingInfo(1, Integer.MAX_VALUE), "code");
        return BeanUtils.copyCollection(entities, SnSerialNumberDefinitionDto.class);
    }

    /**
     * 保存流水号定义
     *
     * @param dto
     */
    @Override
    public void saveDto(SnSerialNumberDefinitionDto dto) {
        SnSerialNumberDefinitionEntity entity = new SnSerialNumberDefinitionEntity();
        // 保存新流水号定义 设置id值
        if (StringUtils.isBlank(dto.getUuid())) {
            dto.setUuid(null);
            dto.setSystem(RequestSystemContextPathResolver.system());
            dto.setTenant(SpringSecurityUtils.getCurrentTenantId());
            if (StringUtils.isNotBlank(dto.getId())
                    && commonValidateService.checkExists("snSerialNumberDefinitionEntity", "id", dto.getId())) {
                // ID非空唯一性判断
                throw new RuntimeException("已经存在ID为[" + dto.getId() + "]的流水号定义!");
            }
        } else {
            entity = this.snSerialNumberDefinitionService.getOne(dto.getUuid());
            // ID非空唯一性判断
            if (StringUtils.isNotBlank(dto.getId())
                    && !commonValidateService.checkUnique(dto.getUuid(), "snSerialNumberDefinitionEntity", "id", dto.getId())) {
                throw new RuntimeException("已经存在ID为[" + dto.getId() + "]的流水号定义!");
            }
        }
        BeanUtils.copyProperties(dto, entity, IdEntity.BASE_FIELDS);

        // 初始值不能为空
        if (StringUtils.isBlank(entity.getInitialValue())) {
            throw new RuntimeException("初始值不能为空");
        }
        // 增量大于0验证
        if (entity.getIncremental() == null || entity.getIncremental() <= 0) {
            throw new RuntimeException("流水号增量必须大于0");
        }

        this.snSerialNumberDefinitionService.save(entity);

        if (CollectionUtils.isNotEmpty(dto.getI18ns())) {
            appDefElementI18nService.deleteAllI18n(null, entity.getId(), new BigDecimal(1), IexportType.SnSerialNumberDefinition);
            for (AppDefElementI18nEntity i : dto.getI18ns()) {
                i.setDefId(entity.getId());
                i.setApplyTo(IexportType.SnSerialNumberDefinition);
                i.setVersion(new BigDecimal(1));
            }
            appDefElementI18nService.saveAll(dto.getI18ns());
        }
    }

    /**
     * 删除没用的流水号定义
     *
     * @param uuid
     * @return
     */
    @Override
    public int deleteWhenNotUsed(String uuid) {
        return this.snSerialNumberDefinitionService.deleteWhenNotUsed(uuid);
    }

    /**
     * 异步加载流水号定义选择树
     *
     * @param treeId
     * @return
     */
    @Override
    public List<TreeNode> asyncLoadSerialNumberTree(String treeId) {
        List<TreeNode> treeNodes = null;
        // 根结点加载流水号分类
        if (TreeNode.ROOT_ID.equals(treeId)) {
            List<SnSerialNumberCategoryDto> categories = snSerialNumberCategoryFacadeService.listAllByCodeAsc();
            treeNodes = serialNumberCategories2TreeNodes(categories);
        } else {
            List<SnSerialNumberDefinitionEntity> entities = snSerialNumberDefinitionService.listByCategoryUuid(treeId);
            treeNodes = serialNumberDefinitions2TreeNodes(entities);
        }
        return treeNodes;
    }

    /**
     * 加载流水号定义选择树
     *
     * @return
     */
    @Override
    public List<TreeNode> loadSerialNumberTree() {
        String system = RequestSystemContextPathResolver.system();
        List<SnSerialNumberCategoryDto> categories = snSerialNumberCategoryFacadeService.listBySystem(system);
        List<SnSerialNumberDefinitionEntity> entities = snSerialNumberDefinitionService.listBySystem(system);
        Map<String, List<SnSerialNumberDefinitionEntity>> entityMap = ListUtils.list2group(entities, "categoryUuid");
        List<TreeNode> treeNodes = serialNumberCategories2TreeNodes(categories);
        for (TreeNode treeNode : treeNodes) {
            List<SnSerialNumberDefinitionEntity> categoryEntities = entityMap.remove(treeNode.getId());
            if (CollectionUtils.isNotEmpty(categoryEntities)) {
                treeNode.getChildren().addAll(serialNumberDefinitions2TreeNodes(categoryEntities));
            }
        }
        // 添加分类不存在的流水号定义
        List<SnSerialNumberDefinitionEntity> addToRoot = Lists.newLinkedList();
        for (Map.Entry<String, List<SnSerialNumberDefinitionEntity>> entry : entityMap.entrySet()) {
            addToRoot.addAll(entry.getValue());
            treeNodes.addAll(serialNumberDefinitions2TreeNodes(entry.getValue()));
        }
        // 添加无分类的流水号定义
        treeNodes.addAll(serialNumberDefinitions2TreeNodes(entities.stream()
                .filter(entity -> entity.getCategoryUuid() == null && !addToRoot.contains(entity)).collect(Collectors.toList())));
        return treeNodes;
    }

    /**
     * 根据流水号分类UUID或流水号定义ID获取流水号定义列表
     *
     * @param categoryUuidOrIds
     * @return
     */
    @Override
    public List<SnSerialNumberDefinitionDto> listByCategoryUuidOrId(List<String> categoryUuidOrIds) {
        if (CollectionUtils.isEmpty(categoryUuidOrIds)) {
            return Collections.emptyList();
        }
        List<SnSerialNumberDefinitionEntity> entities = snSerialNumberDefinitionService.listByCategoryUuidOrId(categoryUuidOrIds);
        if (CollectionUtils.isNotEmpty(entities) && !LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            Map<String, SnSerialNumberDefinitionEntity> entityMap = Maps.newHashMap();
            for (SnSerialNumberDefinitionEntity e : entities) {
                entityMap.put(e.getId(), e);
            }
            List<AppDefElementI18nEntity> i18nEntities = appDefElementI18nService.getI18ns(entityMap.keySet(), IexportType.SnSerialNumberDefinition, "name", LocaleContextHolder.getLocale().toString());
            if (CollectionUtils.isNotEmpty(i18nEntities)) {
                for (AppDefElementI18nEntity i : i18nEntities) {
                    if (entityMap.containsKey(i.getDefId()) && StringUtils.isNotBlank(i.getContent())) {
                        entityMap.get(i.getDefId()).setName(i.getContent());
                    }
                }
            }
        }
        return BeanUtils.copyCollection(entities, SnSerialNumberDefinitionDto.class);
    }

    private List<TreeNode> serialNumberCategories2TreeNodes(List<SnSerialNumberCategoryDto> categories) {
        List<TreeNode> treeNodes = Lists.newArrayList();
        for (SnSerialNumberCategoryDto dto : categories) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(dto.getUuid());
            treeNode.setName(dto.getName());
            treeNode.setData(dto.getUuid());
            treeNode.setNocheck(true);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    private List<TreeNode> serialNumberDefinitions2TreeNodes(List<SnSerialNumberDefinitionEntity> entities) {
        List<TreeNode> treeNodes = Lists.newArrayList();
        for (SnSerialNumberDefinitionEntity entity : entities) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(entity.getId());
            treeNode.setName(entity.getName());
            treeNode.setData(entity.getId());
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

}
