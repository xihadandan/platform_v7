/*
 * @(#)2018年4月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;

import java.util.Collection;
import java.util.List;

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
public interface PrintTemplateFacadeService extends Facade {

    /**
     * @param printTempltId
     * @return
     */
    PrintTemplate getPrintTemplateById(String printTempltId);

    List<TreeNode> getPrintTemplateTree(List<String> printTemplateUuidList);

    /**
     * @param printTempltIds
     * @return
     */
    List<PrintTemplate> getPrintTemplatesByIds(Collection<String> printTempltIds);

    /**
     * 如何描述该方法
     *
     * @param printTempltUuid
     * @return
     */
    PrintTemplate getByUuid(String printTempltUuid);

    Select2QueryData loadPrintTemplateSelectionByModule(Select2QueryInfo select2QueryInfo);

}
