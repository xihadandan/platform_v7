/*
 * @(#)2015-9-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.facade;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.security.audit.support.AbstractResourceDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-9-25.1	zhulh		2015-9-25		Create
 * </pre>
 * @date 2015-9-25
 */
//@Component
public class DataDictResourceDataSource extends AbstractResourceDataSource {

    @Autowired
    private DataDictionaryService dataDictionaryService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.AbstractResourceDataSource#getName()
     */
    @Override
    public String getName() {
        return "数据字典资源";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.AbstractResourceDataSource#getId()
     */
    @Override
    public String getId() {
        return "DATA_DICT";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.AbstractResourceDataSource#getOrder()
     */
    @Override
    public int getOrder() {
        return 100;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.ResourceDataSource#getData()
     */
    @Override
    public List<TreeNode> getData(Map<String, Object> params) {
        return dataDictionaryService.getAllAsTree(TreeNode.ROOT_ID).getChildren();
    }

}
