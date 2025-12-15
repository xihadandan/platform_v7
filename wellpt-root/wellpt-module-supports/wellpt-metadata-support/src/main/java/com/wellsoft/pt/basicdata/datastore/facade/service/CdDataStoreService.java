/*
 * @(#)2016年10月27日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.context.util.groovy.GroovyUseable;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreDefinitionBean;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreRendererBean;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfiguration;
import com.wellsoft.pt.basicdata.datastore.support.export.ExportParams;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeDataStoreRequestParam;

import javax.activation.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月27日.1	xiem		2016年10月27日		Create
 * </pre>
 * @date 2016年10月27日
 */
@GroovyUseable
public interface CdDataStoreService extends BaseService {

    /**
     * 数据仓库查询数据
     *
     * @param cdDataStoreParams
     * @return
     */
    public DataStoreData loadData(DataStoreParams params);

    public DataStoreData loadData(DataStoreParams params, DataStoreConfiguration dataStoreConfiguration);


    public DataStoreData loadDataWithNewTransaction(DataStoreParams params);

    public Map<String, Object> loadFieldRenderData(DataStoreRendererBean renderer,
                                                   Map<String, Object> data,
                                                   Map<String, Integer> indexMap);

    public Select2QueryData loadSelect2Data(Select2QueryInfo query);

    /**
     * 数据仓库查询数据
     *
     * @param cdDataStoreParams
     * @return
     */
    public long loadCount(DataStoreParams params);

    public long loadCount(DataStoreParams params, DataStoreConfiguration dataStoreConfiguration);

    /**
     * 导出数据
     *
     * @param type
     * @param exportParams
     * @return
     */
    public DataSource exportData(String type, ExportParams exportParams);

    public CdDataStoreDefinitionBean getBeanById(String id);

    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo);

    public List<TreeNode> loadTreeNodes(TreeDataStoreRequestParam param);


    public List<Map<String, String>> getQueryInterfaceParams(String className);


}
