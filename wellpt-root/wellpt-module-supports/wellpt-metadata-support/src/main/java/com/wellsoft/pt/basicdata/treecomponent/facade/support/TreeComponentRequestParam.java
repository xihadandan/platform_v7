/*
 * @(#)2017年5月6日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.treecomponent.facade.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;

import java.util.ArrayList;
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
 * 2017年5月6日.1	zhulh		2017年5月6日		Create
 * </pre>
 * @date 2017年5月6日
 */
public class TreeComponentRequestParam extends BaseObject {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3561572605414073898L;
    private ArrayList<TreeNodeConfig> treeNodeConfigs; // 各个组织节点的配置情况
    private String dataFilter; // 过滤条件
    private String parentId; // 异步加载数据时使用的上个节点ID
    private String searchText; // 异步加载数据时使用的远程搜索文字

    private Map<String, Object> intfParams = Maps.newHashMap();//接口参数

    /**
     * @return the parentId
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * @param parentId 要设置的parentId
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the searchText
     */
    public String getSearchText() {
        return searchText;
    }

    /**
     * @param searchText 要设置的searchText
     */
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * @return the dataFilter
     */
    public String getDataFilter() {
        return dataFilter;
    }

    /**
     * @param dataFilter 要设置的dataFilter
     */
    public void setDataFilter(String dataFilter) {
        this.dataFilter = dataFilter;
    }

    /**
     * @return the treeNodeConfigs
     */
    public ArrayList<TreeNodeConfig> getTreeNodeConfigs() {
        return treeNodeConfigs;
    }

    /**
     * @param treeNodeConfigs 要设置的treeNodeConfigs
     */
    public void setTreeNodeConfigs(ArrayList<TreeNodeConfig> treeNodeConfigs) {
        this.treeNodeConfigs = treeNodeConfigs;
    }

    public Map<String, Object> getIntfParams() {
        return intfParams;
    }

    public void setIntfParams(Map<String, Object> intfParams) {
        this.intfParams = intfParams;
    }


}
