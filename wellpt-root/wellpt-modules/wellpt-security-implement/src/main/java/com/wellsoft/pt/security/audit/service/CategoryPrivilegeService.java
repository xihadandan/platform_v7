/*
 * @(#)2014-5-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.BaseService;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-10-11.1  zhengky	2014-10-11	  Create
 * </pre>
 * @date 2014-10-11
 */
public interface CategoryPrivilegeService extends BaseService {

    List<TreeNode> getAsTreeAsync(String id);

    TreeNode getAllAsTreeAsync(String id);

    /**
     * 查询指定分类下的权限
     *
     * @param category
     * @param jqGridQueryInfo
     * @param queryInfo
     * @return
     */
    QueryData queryByCategory(String category, JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo);

}
