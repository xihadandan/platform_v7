/*
 * @(#)2015年9月16日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年9月16日.1	zhulh		2015年9月16日		Create
 * </pre>
 * @date 2015年9月16日
 */
public interface DataDictionaryMaintain extends BaseService {

    /**
     * 一次性加载平台树
     *
     * @param uuid
     * @return
     */
    TreeNode getPtAsTree(String uuid);

    /**
     * 一次性加载应用树
     *
     * @param uuid
     * @return
     */
    TreeNode getAppAsTree(String uuid);

    /**
     * 一次性加载应用树
     *
     * @param uuid
     * @return
     */
    List<TreeNode> getBizAsTree(String uuid);

    /**
     * 是否为业务结点
     *
     * @param uuid
     * @return
     */
    boolean isBizNode(String uuid);

    /**
     * 获取数据字典树
     *
     * @param uuid
     * @return
     */
    public TreeNode getAllAsTree(String uuid);

    /**
     * 获取某个类型数据字典下拉数据
     *
     * @param queryInfo
     * @return
     */
    List<DataDictionary> getDataDictionariesByType(String type);

    /**
     * 获取某个类型数据字典下拉数据
     *
     * @param queryInfo
     * @return
     */
    List<DataDictionary> getDataDictionariesByParentUuid(String uuid);

    List<DataDictionary> getDataDictionariesByParentUuidAndLevel(String uuid, int level);

}
