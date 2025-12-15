/*
 * @(#)2021-10-15 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.util.tree.TreeUtils;
import com.wellsoft.pt.basicdata.printtemplate.bean.PrintTemplateCategoryBean;
import com.wellsoft.pt.basicdata.printtemplate.dao.PrintTemplateCategoryDao;
import com.wellsoft.pt.basicdata.printtemplate.dao.PrintTemplateDao;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateCategoryService;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PrintTemplateCategoryServiceImpl
        extends AbstractJpaServiceImpl<PrintTemplateCategory, PrintTemplateCategoryDao, String>
        implements PrintTemplateCategoryService {

    // 打印模版分类编号
    private static final String FLOW_CATEGORY_CODE_PATTERN = "00000";

    @Autowired
    private PrintTemplateDao printTemplateDao;

    @Autowired
    private PrintTemplateService printTemplateService;

    @Autowired
    private IdGeneratorService idGeneratorService;

    @Override
    public PrintTemplateCategoryBean getBean(String uuid) {
        PrintTemplateCategoryBean bean = new PrintTemplateCategoryBean();
        PrintTemplateCategory category = dao.getOne(uuid);

        BeanUtils.copyProperties(category, bean);

        PrintTemplateCategory parent = null;
        if (StringUtils.isNotBlank(category.getParentUuid())) {
            parent = dao.getOne(category.getParentUuid());
        }
        // PrintTemplateCategory parent = category.getParent();
        if (parent != null) {
            bean.setParentUuid(parent.getUuid());
            bean.setParentName(parent.getName());
        }

        return bean;
    }

    @Override
    public List<PrintTemplateCategory> getAll() {
        List<PrintTemplateCategory> categories = listAll();
        return BeanUtils.convertCollection(categories, PrintTemplateCategory.class);
    }

    @Override
    public List<PrintTemplateCategory> getAllBySystemUnitIds() {
        return this.getAllBySystemUnitIdsLikeName(null);
    }

    @Override
    public List<PrintTemplateCategory> getAllBySystemUnitIds(List<String> systemUnitIds) {
        Map<String, Object> values = new HashMap<String, Object>();
        String hql = "from PrintTemplateCategory t where 1=1";
        if (CollectionUtils.isNotEmpty(systemUnitIds)) {
            values.put("systemUnitIds", systemUnitIds);
            hql += " and t.systemUnitId in(:systemUnitIds) ";
        }
        hql += " order by code asc, name asc ";
        List<PrintTemplateCategory> categories = listByHQL(hql, values);
        return BeanUtils.convertCollection(categories, PrintTemplateCategory.class);
    }

    @Override
    public List<TreeNode> getPrintTemplateCategoryTree(String searchName) {
        List<PrintTemplateCategory> printTemplateCategoryList = this.getAllBySystemUnitIdsLikeName(null);
        TreeNode printTemplateCategoryTree = getPrintTemplateCategoryTree(printTemplateCategoryList, searchName);
        if (StringUtils.isNotEmpty(printTemplateCategoryTree.getName())) {
            // 当只有一个节点时，只有一级 TreeUtils.buildTree
            return new ArrayList<>(Collections.singleton(printTemplateCategoryTree));
        } else {
            return printTemplateCategoryTree.getChildren();
        }
    }

    public TreeNode getPrintTemplateCategoryTree(List<PrintTemplateCategory> printTemplateCategoryList, String name) {
        TreeNode printTemplateCategoryTree = getPrintTemplateCategoryTree(printTemplateCategoryList);
        if (StringUtils.isNotEmpty(name)) {
            filterTreeByName(printTemplateCategoryTree, name);
        }
        return printTemplateCategoryTree;
    }

    public boolean filterTreeByName(TreeNode treeNode, String name) {

        boolean result = false;
        if (treeNode.getName() != null && treeNode.getName().contains(name)) {
            result = true;
        }

        if (CollectionUtils.isNotEmpty(treeNode.getChildren())) {
            List<TreeNode> children = treeNode.getChildren();
            for (Iterator<TreeNode> iterator = children.iterator(); iterator.hasNext(); ) {
                TreeNode child = iterator.next();
                if (filterTreeByName(child, name)) {
                    result = true;
                } else {
                    iterator.remove();
                }
            }
        }

        return result;
    }

    public TreeNode getPrintTemplateCategoryTree(List<PrintTemplateCategory> printTemplateCategoryList) {
        return TreeUtils.buildTree(null, printTemplateCategoryList, "uuid", "parentUuid",
                new Function<PrintTemplateCategory, TreeNode>() {
                    @Override
                    public TreeNode apply(PrintTemplateCategory treeNodeDto) {
                        TreeNode treeNode1 = new TreeNode();
                        treeNode1.setId(treeNodeDto.getUuid());
                        treeNode1.setName(treeNodeDto.getName());
                        treeNode1.setData(treeNodeDto);
                        return treeNode1;
                    }
                });
    }

    @Override
    public List<PrintTemplateCategory> getAllBySystemUnitIdsLikeName(String name) {
        Map<String, Object> values = new HashMap<String, Object>();
        // List<String> systemUnitIds = new ArrayList<String>();
        // systemUnitIds.add(MultiOrgSystemUnit.PT_ID);
        // systemUnitIds.add(SpringSecurityUtils.getCurrentUserUnitId());
        // values.put("systemUnitIds", systemUnitIds);
        String hql = "from PrintTemplateCategory t where 1=1 "; // t.systemUnitId in(:systemUnitIds)
        if (StringUtils.isNotBlank(name)) {
            values.put("name", "%" + name + "%");
            hql += " and t.name like :name ";
        }
        hql += " order by code asc, name asc ";
        List<PrintTemplateCategory> categories = listByHQL(hql, values);
        return BeanUtils.convertCollection(categories, PrintTemplateCategory.class);
    }

    @Override
    @Transactional
    public PrintTemplateCategory saveAndPublishData(PrintTemplateCategory entity) {
        dao.save(entity);
        return entity;
    }

    @Override
    @Transactional
    public int deleteWhenNotUsed(String uuid) {

        PrintTemplateCategory templateCategory = this.dao.getOne(uuid);
        if (templateCategory != null) {
            PrintTemplateCategory queryPrintTemplateCategory = new PrintTemplateCategory();
            queryPrintTemplateCategory.setParentUuid(templateCategory.getUuid());
            List<PrintTemplateCategory> printTemplateCategoryList = this.dao.listByEntity(queryPrintTemplateCategory);
            // 更新子级父类属性
            if (CollectionUtils.isNotEmpty(printTemplateCategoryList)) {
                for (PrintTemplateCategory printTemplateCategory : printTemplateCategoryList) {
                    printTemplateCategory.setParentUuid(templateCategory.getParentUuid());
                    this.dao.save(printTemplateCategory);
                }
            }
        }

        dao.delete(uuid);
        PrintTemplate queryPrintTemplate = new PrintTemplate();
        queryPrintTemplate.setCategory(uuid);
        List<PrintTemplate> printTemplateList = printTemplateDao.listByEntity(queryPrintTemplate);
        for (PrintTemplate printTemplate : printTemplateList) {
            printTemplate.setCategory(null);
            printTemplateDao.save(printTemplate);
        }
        return getOne(uuid) != null ? -1 : 0;
    }

    @Override
    @Transactional
    public void saveBean(PrintTemplateCategoryBean bean) {
        PrintTemplateCategory category = new PrintTemplateCategory();
        // String newCategoryCode = bean.getCode();
        // String oldCategoryCode = newCategoryCode;
        if (StringUtils.isNotBlank(bean.getUuid())) {
            category = this.dao.getOne(bean.getUuid());
            // oldCategoryCode = category.getCode();
        } else {
            // 编号唯一性判断，空编码不校验
            if (StringUtils.isNotBlank(category.getCode())) {
                category.setCode(bean.getCode());
                if (CollectionUtils.isNotEmpty(this.dao.listByEntity(category))) {
                    throw new RuntimeException("已经存在编号为[" + category.getCode() + "]的打印模版分类!");
                }
            }
        }

        BeanUtils.copyPropertiesExcludeBaseField(bean, category, new String[]{"systemUnitId"});
        // if (StringUtils.isNotBlank(bean.getParentUuid())) {
        // category.setParent(this.dao.getOne(bean.getParentUuid()));
        // }

        this.dao.save(category);

        // 打印模版分类编号变更处理
        // if (!StringUtils.equals(oldCategoryCode, newCategoryCode)) {
        // printTemplateService.updateCategory(oldCategoryCode, newCategoryCode);
        // }
    }

    @Override
    @Transactional
    public List<PrintTemplateCategory> saveAll(Collection<PrintTemplateCategory> entities) {
        for (PrintTemplateCategory category : entities) {
            saveAndPublishData(category);
        }
        return Arrays.asList(entities.toArray(new PrintTemplateCategory[0]));
    }

    @Override
    @Transactional
    public void remove(PrintTemplateCategory entity) {
        dao.delete(entity);
    }

    @Override
    @Transactional
    public void removeAll(Collection<PrintTemplateCategory> entities) {
        for (PrintTemplateCategory category : entities) {
            remove(category);
        }
    }

    @Override
    @Transactional
    public void removeByPk(String uuid) {
        // PrintTemplateCategory flowCategory = this.dao.getOne(uuid);
        // if (printTemplateDao.countByCategory(flowCategory.getCode()) > 0) {
        // throw new RuntimeException("打印模版分类[" + flowCategory.getName() + "]已经被使用!");
        // } else {
        dao.delete(uuid);
        PrintTemplate queryPrintTemplate = new PrintTemplate();
        queryPrintTemplate.setCategory(uuid);
        List<PrintTemplate> printTemplateList = printTemplateDao.listByEntity(queryPrintTemplate);
        for (PrintTemplate printTemplate : printTemplateList) {
            printTemplate.setCategory(null);
            printTemplateDao.save(printTemplate);
        }
        // }
    }

    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uids) {
        for (String uid : uids) {
            removeByPk(uid);
        }
    }

    // @Override
    // public TreeNode getAsTreeAsyncByUnitId(String systemUnitId) {
    // List<PrintTemplateCategory> categories =
    // this.dao.getAsTreeAsyncByUnitId(systemUnitId);
    // // 全部打印模版
    // TreeNode root = new TreeNode();
    // root.setId(TreeNode.ROOT_ID);
    // root.setName("全部打印模版");
    // for (PrintTemplateCategory flowCategory : categories) {
    // TreeNode node = new TreeNode();
    // node.setId(flowCategory.getUuid());
    // node.setName(flowCategory.getName());
    // node.setData(flowCategory.getCode());
    // root.getChildren().add(node);
    // }
    // return root;
    // }

    @Override
    @Transactional
    public String generatePrintTemplateCategoryCode() {
        return idGeneratorService.generate(PrintTemplateCategory.class, FLOW_CATEGORY_CODE_PATTERN, false);
    }

    @Override
    public PrintTemplateCategory getByCode(String code) {
        return this.dao.getByCode(code);
    }

    @Override
    public List<PrintTemplateCategory> getTopLevel() {
        return dao.getTopLevel();
    }

    @Override
    public List<PrintTemplateCategory> getListByUuids(Set<String> uuidList) {
        if (CollectionUtils.isEmpty(uuidList)) {
            return Lists.newArrayList();
        }
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuidList", uuidList);
        return this.dao.listByNameSQLQuery("getCategoryListByUuids", values);
    }

    @Override
    public List<PrintTemplateCategory> getChildNodeListByUuids(Set<String> uuidList) {
        List<PrintTemplateCategory> printTemplateCategories = Lists.newArrayList();
        // 递归
        List<PrintTemplateCategory> printTemplateCategoryList = getChildNodeListByUuidsRecursive(uuidList);
        if (printTemplateCategoryList.size() > 0) {
            printTemplateCategories.addAll(printTemplateCategoryList);
        }
        return printTemplateCategories;
    }

    private List<PrintTemplateCategory> getChildNodeListByUuidsRecursive(Set<String> uuidList) {
        if (CollectionUtils.isEmpty(uuidList)) {
            return Lists.newArrayList();
        }
        List<PrintTemplateCategory> printTemplateCategories = Lists.newArrayList();
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuidList", uuidList);
        List<PrintTemplateCategory> printTemplateCategoriesList = this.dao
                .listByNameSQLQuery("getChildNodeListByUuidsRecursive", values);
        if (CollectionUtils.isNotEmpty(printTemplateCategoriesList)) {
            printTemplateCategories.addAll(printTemplateCategoriesList);
            Set<String> uuids = getPrintTemplateCategorieUuids(printTemplateCategoriesList);
            printTemplateCategories.addAll(getChildNodeListByUuidsRecursive(uuids));
        }

        return printTemplateCategories;
    }

    private Set<String> getPrintTemplateCategorieUuids(List<PrintTemplateCategory> printTemplateCategoriesList) {
        Set<String> uuids = Sets.newHashSet();
        if (CollectionUtils.isEmpty(printTemplateCategoriesList)) {
            return uuids;
        }

        for (PrintTemplateCategory printTemplateCategory : printTemplateCategoriesList) {
            uuids.add(printTemplateCategory.getUuid());
        }
        return uuids;
    }

}
