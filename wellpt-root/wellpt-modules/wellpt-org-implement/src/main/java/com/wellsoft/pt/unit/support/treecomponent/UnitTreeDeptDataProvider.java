/*
 * @(#)6 Mar 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.unit.support.treecomponent;

import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeType;
import com.wellsoft.pt.org.unit.service.impl.UnitTreeServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static com.wellsoft.pt.org.unit.service.impl.UnitTreeServiceImpl.ATTRIBUTE_TYPE_DEP;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 6 Mar 2017.1	Xiem		6 Mar 2017		Create
 * </pre>
 * @date 6 Mar 2017
 */
@Deprecated
public class UnitTreeDeptDataProvider extends AbstractUnitTreeDataProvider {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#getName()
     */
    @Override
    public String getName() {
        return "组织树-部门";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.support.treecomponent.AbstractUnitTreeDataProvider#getNodeTypes()
     */
    @Override
    public List<TreeType> getNodeTypes() {
        List<TreeType> types = new ArrayList<TreeType>();
        types.add(TreeType.createTreeType(ATTRIBUTE_TYPE_DEP, "部门"));
        return types;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.support.treecomponent.AbstractUnitTreeDataProvider#getOrgType()
     */
    @Override
    protected String getOrgType() {
        return UnitTreeServiceImpl.ID_DEPT;
    }

}
