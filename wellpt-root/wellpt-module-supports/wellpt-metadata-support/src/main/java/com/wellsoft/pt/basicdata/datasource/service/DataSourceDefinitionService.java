/*
 * @(#)2014-7-31 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.datasource.bean.DataSourceDefinitionBean;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceDefinition;

import java.util.List;
import java.util.Set;

/**
 * Description: 数据源的服务类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-31.1	wubin		2014-7-31		Create
 * </pre>
 * @date 2014-7-31
 */
public interface DataSourceDefinitionService {

    /**
     * 保存数据源定义
     *
     * @param bean
     * @return
     */
    String saveBean(DataSourceDefinitionBean bean);

    /**
     * 根据Id删除
     *
     * @param id
     */
    void deleteById(String id);

    /**
     * 根据id批量删除
     *
     * @param ids
     */
    void deleteAllById(String[] ids);

    /**
     * 根据jqgridId获得bean
     *
     * @param id
     * @return
     */
    DataSourceDefinitionBean getBeanByJqGridId(String id);

    /**
     * 根据Id获得bean
     *
     * @param id
     * @return
     */
    DataSourceDefinitionBean getBeanById(String id);

    /**
     * 根据uuid获得bean
     *
     * @param uuid
     * @return
     */
    DataSourceDefinitionBean getBeanByUuid(String uuid);

    /**
     * 获得所有的数据源定义
     *
     * @return
     */
    List<DataSourceDefinition> getAll();

    /**
     * 获得所有数据源(treeNode树形)
     *
     * @return
     */
    List<TreeNode> getAllByTreeNode(String s);

    Set<DataSourceColumn> getDataSourceFieldsById(String dataSourceDefId);

    List<TreeNode> getDataSourceFieldsByTree(String s, String dataSourceDefId);

}
