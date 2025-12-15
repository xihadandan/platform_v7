/*
 * @(#)2021-10-15 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.dao;

import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

public interface PrintTemplateCategoryDao extends JpaDao<PrintTemplateCategory, String> {
    List<PrintTemplateCategory> getTopLevel();

    PrintTemplateCategory getByCode(String code);

    List<PrintTemplateCategory> getAllByUnitId(String unitId);

    List<PrintTemplateCategory> getAsTreeAsyncByUnitId(String systemUnitId);
}
