/*
 * @(#)2012-12-3 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.workflow.entity.FlowCategory;

import java.util.List;

/**
 * Description: 工作流分类持久层操作类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-3.1	zhulh		2012-12-3		Create
 * </pre>
 * @date 2012-12-3
 */
public interface FlowCategoryDao extends JpaDao<FlowCategory, String> {
    List<FlowCategory> getTopLevel();

    FlowCategory getByCode(String code);

    List<FlowCategory> getAllByUnitId(String unitId);

    List<FlowCategory> getAsTreeAsyncByUnitId(String systemUnitId);
}
