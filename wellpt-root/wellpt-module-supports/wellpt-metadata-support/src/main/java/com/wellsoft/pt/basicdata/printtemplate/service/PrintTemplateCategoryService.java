/*
 * @(#)2021-10-15 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.printtemplate.bean.PrintTemplateCategoryBean;
import com.wellsoft.pt.basicdata.printtemplate.dao.PrintTemplateCategoryDao;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PrintTemplateCategoryService
        extends JpaService<PrintTemplateCategory, PrintTemplateCategoryDao, String> {

    PrintTemplateCategory saveAndPublishData(PrintTemplateCategory entity);

    PrintTemplateCategory getOne(String uuid);

    @Override
    void delete(String uuid);

    int deleteWhenNotUsed(String uuid);

    PrintTemplateCategoryBean getBean(String uuid);

    void saveBean(PrintTemplateCategoryBean bean);

    List<PrintTemplateCategory> saveAll(Collection<PrintTemplateCategory> entities);

    void remove(PrintTemplateCategory entity);

    void removeAll(Collection<PrintTemplateCategory> entities);

    void removeByPk(String uid);

    void removeAllByPk(Collection<String> uids);

    // TreeNode getAsTreeAsyncByUnitId(String systemUnitId);

    String generatePrintTemplateCategoryCode();

    PrintTemplateCategory getByCode(String code);

    List<PrintTemplateCategory> getAll();

    List<PrintTemplateCategory> getAllBySystemUnitIds();

    /**
     * 通过指定的系统单位ID列表获取打印模板分类
     *
     * @param systemUnitIds
     * @return
     **/
    List<PrintTemplateCategory> getAllBySystemUnitIds(List<String> systemUnitIds);

    List<TreeNode> getPrintTemplateCategoryTree(String searchName);

    List<PrintTemplateCategory> getAllBySystemUnitIdsLikeName(String name);

    List<PrintTemplateCategory> getTopLevel();

    /**
     * 通过Uuid集合获取打印模板分类对象列表
     *
     * @param uuidList
     * @return
     **/
    public List<PrintTemplateCategory> getListByUuids(Set<String> uuidList);

    /**
     * 查询所有的分类子结点
     *
     * @param uuidList
     * @return java.util.List<com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory>
     **/
    public List<PrintTemplateCategory> getChildNodeListByUuids(Set<String> uuidList);
}
