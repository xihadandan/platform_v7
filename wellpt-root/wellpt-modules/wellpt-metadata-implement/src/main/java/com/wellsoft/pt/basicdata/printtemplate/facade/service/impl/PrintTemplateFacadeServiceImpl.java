/*
 * @(#)2018年4月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory;
import com.wellsoft.pt.basicdata.printtemplate.facade.service.PrintTemplateFacadeService;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateCategoryService;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月19日.1	chenqiong		2018年4月19日		Create
 * </pre>
 * @date 2018年4月19日
 */
@Service
public class PrintTemplateFacadeServiceImpl extends AbstractApiFacade implements PrintTemplateFacadeService {

    @Autowired
    PrintTemplateService printTemplateService;

    @Autowired
    PrintTemplateCategoryService printTemplateCategoryService;

    @Override
    public PrintTemplate getPrintTemplateById(String printTempltId) {
        return printTemplateService.getPrintTemplateById(printTempltId);
    }

    @Override
    public List<TreeNode> getPrintTemplateTree(List<String> printTemplateUuidList) {
        List<PrintTemplateCategory> printTemplateCategoryList = printTemplateCategoryService.getAllBySystemUnitIds();
        List<PrintTemplate> printTemplateList = printTemplateService.listByUuids(printTemplateUuidList);

        List<TreeNode> printTemplateTree = printTemplateService.getPrintTemplateTree(printTemplateCategoryList,
                printTemplateList);
        TreeNode treeNode = new TreeNode();
        treeNode.setChildren(printTemplateTree);
        filterHasPrintTemplateChild(treeNode);
        return treeNode.getChildren();
    }

    private boolean filterHasPrintTemplateChild(TreeNode treeNode) {
        boolean hasPrintTemplateChild = false;

        if (PrintTemplate.class.getSimpleName().equals(treeNode.getType())) {
            hasPrintTemplateChild = true;
        }

        if (CollectionUtils.isNotEmpty(treeNode.getChildren())) {
            List<TreeNode> children = treeNode.getChildren();
            for (Iterator<TreeNode> iterator = children.iterator(); iterator.hasNext(); ) {
                TreeNode child = iterator.next();
                if (filterHasPrintTemplateChild(child)) {
                    hasPrintTemplateChild = true;
                } else {
                    iterator.remove();
                }
            }
        }
        return hasPrintTemplateChild;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.printtemplate.facade.service.PrintTemplateFacadeService#getPrintTemplatesByIds(java.util.Collection)
     */
    @Override
    public List<PrintTemplate> getPrintTemplatesByIds(Collection<String> printTempltIds) {
        List<PrintTemplate> printTemplates = Lists.newArrayList();
        Set<String> printTempltIdSet = Sets.newLinkedHashSet();
        printTempltIdSet.addAll(printTempltIds);
        for (String printTempltId : printTempltIds) {
            if (StringUtils.isBlank(printTempltId)) {
                continue;
            }
            PrintTemplate printTemplate = printTemplateService.getPrintTemplateById(printTempltId);
            if (printTemplate != null) {
                printTemplates.add(printTemplate);
            }
        }
        return printTemplates;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.printtemplate.facade.service.PrintTemplateFacadeService#getByUuid(java.lang.String)
     */
    @Override
    public PrintTemplate getByUuid(String printTempltUuid) {
        return printTemplateService.getByUuid(printTempltUuid);
    }

    @Override
    public Select2QueryData loadPrintTemplateSelectionByModule(Select2QueryInfo select2QueryInfo) {

        Map<String, Object> params = Maps.newHashMap();
        String moduleId = select2QueryInfo.getOtherParams("moduleId");
        String idProperty = select2QueryInfo.getOtherParams("idProperty", "id");
        String excludeModuleIds = select2QueryInfo.getOtherParams("excludeModuleIds");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(moduleId)) {
            params.put("moduleId", moduleId);
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(excludeModuleIds)) {
            params.put("excludeModuleIds", Arrays.asList(excludeModuleIds.split(Separator.SEMICOLON.getValue())));
        }
        String systemUnitId = select2QueryInfo.getOtherParams("systemUnitId");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(systemUnitId)) {
            params.put("systemUnitId", systemUnitId);
        }
        List<PrintTemplate> list = this.printTemplateService.listByNameHQLQuery("queryAllPrintTemplateByModuleId",
                params);
        return new Select2QueryData(list, idProperty, "name");
    }

}
