/*
 * @(#)2015年9月16日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.facade.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.facade.service.DataDictionaryMaintain;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.datadict.service.impl.DataDictionaryServiceImpl;
import com.wellsoft.pt.basicdata.datadict.support.DataDictCategory;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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
 * 2015年9月16日.1	zhulh		2015年9月16日		Create
 * </pre>
 * @date 2015年9月16日
 */
@Service
@Transactional
public class DataDictionaryMaintainImpl extends BaseServiceImpl implements DataDictionaryMaintain {
    @Autowired
    private DataDictionaryService dataDictionaryService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.facade.DataDictionaryMaintain#getPtAsTree(java.lang.String)
     */
    @Override
    public TreeNode getPtAsTree(String uuid) {
        return getByCategory(DataDictCategory.PT);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.facade.DataDictionaryMaintain#getAppAsTree(java.lang.String)
     */
    @Override
    public TreeNode getAppAsTree(String uuid) {
        return getByCategory(DataDictCategory.APP);
    }

    /**
     * @return
     */
    private TreeNode getByCategory(String category) {
        TreeNode treeNode = new TreeNode();
        DataDictionary root = this.dao.findUniqueBy(DataDictionary.class, "type", category);
        List<DataDictionary> topList = new ArrayList<DataDictionary>();
        topList.addAll(root.getChildren());
        treeNode.setName(root.getName());
        treeNode.setId(root.getUuid());
        treeNode.setNocheck(true);

        Map<String, List<DataDictionary>> ddMap = new HashMap<String, List<DataDictionary>>();
        List<DataDictionary> allList = this.dao.getAll(DataDictionary.class);
        for (DataDictionary dd : allList) {
            if (dd.getParent() != null) {
                String parentUuid = dd.getParent().getUuid();
                if (StringUtils.isNotBlank(parentUuid)) {
                    if (!ddMap.containsKey(parentUuid)) {
                        ddMap.put(parentUuid, new ArrayList<DataDictionary>());
                    }
                    ddMap.get(parentUuid).add(dd);
                }
            }
        }

        for (DataDictionary dd : topList) {
            TreeNode node = new TreeNode();
            node.setId(dd.getUuid());
            node.setName(dd.getName());
            node.setData(dd.getCode());

            // 生成子结点
            DataDictionaryServiceImpl.buildChildNodes(node, ddMap, false);
            treeNode.getChildren().add(node);
        }
        return treeNode;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.facade.DataDictionaryMaintain#getBizAsTree(java.lang.String)
     */
    @Override
    public List<TreeNode> getBizAsTree(String uuid) {
        List<TreeNode> roots = new ArrayList<TreeNode>();
        roots.add(getByCategory(DataDictCategory.PT));
        roots.add(getByCategory(DataDictCategory.APP));
        roots.add(getByCategory(DataDictCategory.BIZ));
        return roots;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.facade.DataDictionaryMaintain#isBizNode(java.lang.String)
     */
    @Override
    public boolean isBizNode(String uuid) {
        DataDictionary dataDict = this.dao.get(DataDictionary.class, uuid);
        if (dataDict == null) {
            return true;
        }
        return dataDict.getType().equals(DataDictCategory.BIZ);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.facade.service.DataDictionaryMaintain#getAsTreeAsyncForControl(java.lang.String)
     */
    @Override
    public TreeNode getAllAsTree(String uuid) {
        return dataDictionaryService.getAllAsTree(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.facade.service.DataDictionaryMaintain#loadData4OptionValue(java.lang.String)
     */
    @Override
    public List<DataDictionary> getDataDictionariesByType(String type) {
        return dataDictionaryService.getDataDictionariesByType(type);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datadict.facade.service.DataDictionaryMaintain#getDataDictionariesByParentUuid(java.lang.String)
     */
    @Override
    public List<DataDictionary> getDataDictionariesByParentUuid(String uuid) {
        return dataDictionaryService.getDataDictionariesByParentUuid(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataDictionary> getDataDictionariesByParentUuidAndLevel(String uuid, int level) {
        List<DataDictionary> dataDictionaries = getDataDictionariesByParentUuid(uuid);
        if (level == 1) {
            return dataDictionaries;
        }
        level = level - 1;
        for (DataDictionary dd : dataDictionaries) {
            List<DataDictionary> children = getDataDictionariesByParentUuid(dd.getUuid());
            dd.setChildren(children);
            if (CollectionUtils.isNotEmpty(children)) {
                for (DataDictionary d : children) {
                    getCascadeChildren(d, level);
                }
            }
        }
        return dataDictionaries;
    }

    private void getCascadeChildren(DataDictionary dataDictionary, int level) {
        List<DataDictionary> dataDictionaries = getDataDictionariesByParentUuid(dataDictionary.getUuid());
        dataDictionary.setChildren(dataDictionaries);
        level = level - 1;
        if (level == 0) {
            return;
        }
        if (CollectionUtils.isNotEmpty(dataDictionaries)) {
            for (DataDictionary d : dataDictionaries) {
                getCascadeChildren(d, level);
            }
        }
    }

}
