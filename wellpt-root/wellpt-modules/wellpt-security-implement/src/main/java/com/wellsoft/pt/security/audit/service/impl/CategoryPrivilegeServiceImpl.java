/*
 * @(#)2014-5-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.service.impl;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.service.CategoryPrivilegeService;
import com.wellsoft.pt.security.audit.service.PrivilegeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
@Service
@Transactional(readOnly = true)
public class CategoryPrivilegeServiceImpl implements CategoryPrivilegeService {

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Autowired
    private PrivilegeService privilegeService;

    @Override
    public QueryData queryByCategory(String categoryCode, JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo) {
        // 设置查询字段条件
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());
        List<String> categoryUuids = new ArrayList<String>();
        DataDictionary dataDictionary = this.dataDictionaryService.getByParentTypeAndCode("MODULE_CATEGORY",
                categoryCode);
        if (dataDictionary != null) {
            categoryUuids.add(categoryCode);
        }
        values.put("categoryUuids", categoryUuids.toArray(new String[0]));

        values.put("orderBy", queryInfo.getOrderBy());

        // 查询父节点为null的部门
        List<Privilege> results = null;
        if (dataDictionary == null) {
            if ("NOCATEGORY".equals(categoryCode)) {
                values.put("noCategory", "NOCATEGORY");
            }
            results = privilegeService.listByNameHQLQueryAndPage("privilegeQuery", values, queryInfo.getPagingInfo());
        } else {
            results = privilegeService.listByNameHQLQueryAndPage("privilegeByCategoryQuery", values,
                    queryInfo.getPagingInfo());
        }
        QueryData queryData = new QueryData();
        queryData.setDataList(BeanUtils.convertCollection(results, Privilege.class));
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    @Override
    public List<TreeNode> getAsTreeAsync(String uuid) {
        List<DataDictionary> list = new ArrayList<DataDictionary>();
        TreeNode node = new TreeNode();
        // 查询所有根结点
        if (TreeNode.ROOT_ID.equals(uuid)) {
            list = Arrays.asList(this.dataDictionaryService.getByType("SECURITY_CATEGORY").getChildren()
                    .toArray(new DataDictionary[0]));
        } else {
            // 查询指定结点的下一级子结点
            DataDictionary dataDictionary = this.dataDictionaryService.get(uuid);
            if (dataDictionary != null) {
                list = Arrays.asList(dataDictionary.getChildren().toArray(new DataDictionary[0]));
            }
        }

        List<TreeNode> children = node.getChildren();
        for (DataDictionary dataDictionary : list) {
            TreeNode child = new TreeNode();
            child.setId(dataDictionary.getUuid());
            child.setName(dataDictionary.getName());
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("uuid", dataDictionary.getUuid());
            map.put("name", dataDictionary.getName());
            child.setData(map);
            child.setIsParent(dataDictionary.getChildren().size() > 0);
            child.setNocheck(dataDictionary.getChildren().size() > 0);
            children.add(child);
        }

        if (TreeNode.ROOT_ID.equals(uuid)) {
            TreeNode nulltreeNode = new TreeNode();
            nulltreeNode.setName("无分类");
            nulltreeNode.setId("NOCATEGORY");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("uuid", "NOCATEGORY");
            map.put("name", "无分类");
            nulltreeNode.setData(map);
            nulltreeNode.setNocheck(true);
            children.add(nulltreeNode);
        }
        return children;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.CategoryPrivilegeService#getAllAsTreeAsync(java.lang.String)
     */
    @Override
    public TreeNode getAllAsTreeAsync(String uuid) {
        TreeNode treeNode = new TreeNode();
        treeNode.setName("权限");
        treeNode.setId(TreeNode.ROOT_ID);
        treeNode.setNocheck(true);
        List<DataDictionary> allList = this.dataDictionaryService.getAll();
        List<DataDictionary> topList = new ArrayList<DataDictionary>();
        if (TreeNode.ROOT_ID.equals(uuid)) {
            topList = Arrays.asList(this.dataDictionaryService.getByType("MODULE_CATEGORY").getChildren()
                    .toArray(new DataDictionary[0]));
        }
        Map<String, List<DataDictionary>> ddMap = new HashMap<String, List<DataDictionary>>();
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
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("uuid", dd.getCode());
            map.put("name", dd.getName());
            node.setId(dd.getCode());
            node.setName(dd.getName());
            node.setData(map);
            node.setIsParent(dd.getChildren().size() > 0);
            node.setNocheck(dd.getChildren().size() > 0);
            // 生成子结点
            buildChildNodes(node, ddMap);
            treeNode.getChildren().add(node);
        }
        return treeNode;
    }

    private void buildChildNodes(TreeNode node, Map<String, List<DataDictionary>> ddMap) {
        String key = node.getId();
        List<DataDictionary> ddList = ddMap.get(key);
        if (ddList == null) {
            return;
        }
        for (DataDictionary dd : ddList) {
            TreeNode child = new TreeNode();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("uuid", dd.getUuid());
            map.put("name", dd.getName());
            child.setId(dd.getUuid());
            child.setName(dd.getName());
            child.setData(map);
            child.setIsParent(dd.getChildren().size() > 0);
            child.setNocheck(dd.getChildren().size() > 0);
            node.getChildren().add(child);
            buildChildNodes(child, ddMap);
        }
    }
}
