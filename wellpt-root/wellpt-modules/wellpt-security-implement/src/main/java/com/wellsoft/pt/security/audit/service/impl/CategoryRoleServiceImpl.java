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
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.service.CategoryRoleService;
import com.wellsoft.pt.security.audit.service.RoleService;
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
public class CategoryRoleServiceImpl implements CategoryRoleService {

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Autowired
    private RoleService roleService;

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
        List<Role> results = null;
        if (dataDictionary == null) {
            if ("NOCATEGORY".equals(categoryCode)) {
                values.put("noCategory", "NOCATEGORY");
            }

            results = roleService.listByNameHQLQueryAndPage("roleQuery", values, queryInfo.getPagingInfo());
        } else {
            results = roleService.listByNameHQLQueryAndPage("roleByCategoryQuery", values, queryInfo.getPagingInfo());
        }
        QueryData queryData = new QueryData();
        queryData.setDataList(BeanUtils.convertCollection(results, Role.class));
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    @Override
    public List<TreeNode> getAsTreeAsync(String uuid) {
        List<DataDictionary> list = new ArrayList<DataDictionary>();
        TreeNode node = new TreeNode();
        // 查询所有根结点
        if (TreeNode.ROOT_ID.equals(uuid)) {
            list = Arrays.asList(this.dataDictionaryService.getByType("MODULE_CATEGORY").getChildren()
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
            child.setId(dataDictionary.getCode());
            child.setName(dataDictionary.getName());
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("uuid", dataDictionary.getCode());
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

}
