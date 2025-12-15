/*
 * @(#)2014-5-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service.impl;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.Job;
import com.wellsoft.pt.org.service.DepartmentJobService;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.JobService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: DepartmentJobServiceImpl.java
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-12.1  zhengky	2014-8-12	  Create
 * </pre>
 * @date 2014-8-12
 */
@Service
@Transactional
public class DepartmentJobServiceImpl extends BaseServiceImpl implements DepartmentJobService {

    private static final String QUERY_FOR_DEPARTMENT_TREE = "select department.uuid as uuid, department.id as id, department.name as name, department.path as path, department.parent.uuid as parentUuid from Department department where department.isVisible='1' and tenantId=:tenantId";
    @Autowired
    private JobService jobService;
    @Autowired
    private DepartmentService departmentService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentJobService#getDepartmentAsTree(java.lang.String)
     */
    @Override
    public TreeNode getDepartmentAsTree(String excludeUuid) {
		/*		List<Department> departments = this.departmentDao.getTopLevel();
				TreeNode treeNode = new TreeNode();
				treeNode.setId(TreeNode.ROOT_ID);
				buildDepartmentTree(treeNode, departments, excludeUuid);*/
        HashMap queryMap = new HashMap<String, Object>();
        queryMap.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        List<QueryItem> allResources = this.dao.query(QUERY_FOR_DEPARTMENT_TREE, queryMap, QueryItem.class);
        TreeNode treeNode = new TreeNode();
        treeNode.setId(TreeNode.ROOT_ID);
        treeNode.setNocheck(true);
        List<QueryItem> topResources = new ArrayList<QueryItem>();
        Map<String, List<QueryItem>> parentResourceMap = new HashMap<String, List<QueryItem>>();
        for (QueryItem queryItem : allResources) {
            String parentUuid = queryItem.getString("parentUuid");
            if (StringUtils.isNotBlank(parentUuid)) {
                if (!parentResourceMap.containsKey(parentUuid)) {
                    parentResourceMap.put(parentUuid, new ArrayList<QueryItem>());
                }
                parentResourceMap.get(parentUuid).add(queryItem);
            } else {
                topResources.add(queryItem);
            }
        }
        for (QueryItem queryItem : topResources) {
            TreeNode node = new TreeNode();
            String departmentUuid = queryItem.getString("uuid");
            String departmentName = queryItem.getString("name");
            String departmentCode = queryItem.getString("id");
            String departmentPath = queryItem.getString("path");
            Map<String, String> map = new HashMap<String, String>();
            map.put("uuid", departmentUuid);
            map.put("id", departmentCode);
            map.put("name", departmentName);
            map.put("path", departmentPath);

            node.setId(departmentUuid);
            node.setName(departmentName);
            node.setData(map);

            node.setIsParent(true);
            node.setNocheck(true);

            // 生成子结点
            buildChildNodes(node, parentResourceMap);

            treeNode.getChildren().add(node);
        }
        return treeNode;
    }

    private void buildChildNodes(TreeNode node, Map<String, List<QueryItem>> parentResourceMap) {
        String key = node.getId();
        List<QueryItem> queryItems = parentResourceMap.get(key);
        if (queryItems == null) {
            return;
        }

        for (QueryItem queryItem : queryItems) {
            TreeNode child = new TreeNode();
            String uuid = queryItem.getString("uuid");
            String name = queryItem.getString("name");
            String id = queryItem.getString("id");
            String path = queryItem.getString("path");

            child.setId(uuid);
            child.setName(name);
            Map<String, String> map = new HashMap<String, String>();
            map.put("uuid", uuid);
            map.put("id", id);
            map.put("name", name);
            map.put("path", path);
            child.setData(map);

            node.getChildren().add(child);
            node.setIsParent(true);
            node.setNocheck(true);

            buildChildNodes(child, parentResourceMap);
        }
    }

    /**
     * @param treeNode
     * @param departments
     * @param excludeUuid
     */
    private void buildDepartmentTree(TreeNode treeNode, List<Department> departments, String excludeUuid) {
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (Department department : departments) {

            TreeNode child = new TreeNode();
            child.setId(department.getUuid());
            child.setName(department.getName());
            Map<String, String> map = new HashMap<String, String>();
            map.put("uuid", department.getUuid());
            map.put("id", department.getId());
            map.put("name", department.getName());
            map.put("path", department.getPath());
            child.setData(map);
            children.add(child);

            if (department.getChildren().size() != 0) {
                buildDepartmentTree(child, Arrays.asList(department.getChildren().toArray(new Department[0])),
                        excludeUuid);
            }

        }
        treeNode.setChildren(children);
    }

    @Override
    public QueryData queryByDepartment(String departmentUuid, JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo) {
        // 设置查询字段条件
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());
        List<String> departmentuuids = new ArrayList<String>();
        Department department = this.departmentService.get(departmentUuid);
        if (department != null) {
            departmentuuids.add(departmentUuid);
            // this.buildDepartmentChildrenId(department, departmentuuids);
        }
        values.put("departmentUuids", departmentuuids.toArray(new String[0]));
        values.put("orderBy", queryInfo.getOrderBy());
        values.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        // 查询父节点为null的部门
        List<Job> results = null;
        if (department == null) {
            results = jobService.namedQuery("jobQuery", values, Job.class, queryInfo.getPagingInfo());
        } else {
            results = jobService.namedQuery("jobByDepartmentQuery", values, Job.class, queryInfo.getPagingInfo());
        }
        /*begin 2014-12-11 yuyq 添加职位系列和职级 end 2014-12-11
         * 原来直接将job的结果直接放到queryData,现在用自己收集的list数据传到queryData
         * */
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        for (Job j : results) {
            Map<String, String> map = new HashMap<String, String>();
            String departmentname = j.getDepartmentName();
            String departmentname1 = departmentname.substring(departmentname.indexOf("/") + 1, departmentname.length());
            map.put("uuid", j.getUuid());
            map.put("id", j.getId());
            map.put("seriesName", j.getDuty().getSeriesName());
            map.put("name", j.getName());
            map.put("dutyLevel", j.getDuty().getDutyLevel());
            map.put("code", j.getCode());
            map.put("dutyName", j.getDutyName());
            map.put("functionNames", j.getFunctionNames());
            map.put("departmentName", departmentname1);
            resultList.add(map);
        }
        QueryData queryData = new QueryData();
        queryData.setDataList(resultList);
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    /**
     * @param department
     * @param departmentUuids
     */
    private void buildDepartmentChildrenId(Department department, List<String> departmentUuids) {
        departmentUuids.add(department.getUuid());

        List<Department> departments = department.getChildren();
        for (Department child : departments) {
            this.buildDepartmentChildrenId(child, departmentUuids);
        }
    }

}
