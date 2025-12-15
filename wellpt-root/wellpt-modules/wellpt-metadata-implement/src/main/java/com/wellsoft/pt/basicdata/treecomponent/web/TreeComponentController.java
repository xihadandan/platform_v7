/*
 * @(#)2 Mar 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.treecomponent.web;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.datastore.provider.DatastoreTreeDataProvider;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.*;
import com.wellsoft.pt.jpa.hibernate4.CustomeImprovedNamingStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2 Mar 2017.1	Xiem		2 Mar 2017		Create
 * </pre>
 * @date 2 Mar 2017
 */
@Controller
@RequestMapping("/basicdata/treecomponent")
public class TreeComponentController extends BaseController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private ImprovedNamingStrategy improvedNamingStrategy = new CustomeImprovedNamingStrategy();

    @RequestMapping(value = "/loadTree")
    public @ResponseBody
    List<TreeNode> loadTree(@RequestParam("dataProvider") String dataProvider,
                            @RequestParam(required = false, value = "nodeTypeInfo") String nodeTypeInfo,
                            TreeComponentRequestParam requestParam, HttpServletRequest request) {
        if (StringUtils.isNotBlank(nodeTypeInfo)) {
            Collection<TreeNodeConfig> configs = JsonUtils.toCollection(nodeTypeInfo, TreeNodeConfig.class);
            requestParam.setTreeNodeConfigs(new ArrayList<TreeNodeConfig>(configs));
        }
        List<TreeNode> treeNodes = this.loadTreeJson(dataProvider, requestParam, request);
        return treeNodes;
        // return convertTreeJson(requestParam, treeNodes, request);
    }

    private List<TreeNode> loadTreeJson(String dataProvider, TreeComponentRequestParam requestParam,
                                        HttpServletRequest request) {
        try {
            Enumeration<String> names = request.getParameterNames();
            Map<String, String> otherParams = new HashMap<String, String>();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                otherParams.put(name, request.getParameter(name));
            }
            TreeComponentDataProvider provider = ((TreeComponentDataProvider) ApplicationContextHolder.getBean(Class
                    .forName(dataProvider)));
            if (otherParams.containsKey("parentId") && provider instanceof TreeComponentAsyncDataProvider) {
                return ((TreeComponentAsyncDataProvider) provider).asyncLoadTreeData(requestParam);
            }
            if (otherParams.containsKey("searchText") && provider instanceof TreeComponentAsyncDataProvider) {
                return ((TreeComponentAsyncDataProvider) provider).search(requestParam);
            }
            return provider.loadTreeData(requestParam);
        } catch (ClassNotFoundException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
        return new ArrayList<TreeNode>(0);
    }

    // private List<TreeNode> convertTreeJson(TreeComponentRequestParam
    // requestParam, List<TreeNode> treeNodes,
    // HttpServletRequest request) {
    // String nodeTypeInfo = requestParam.getNodeTypeInfo();
    // JSONArray nodeTypeConfig = JSONArray.fromObject(nodeTypeInfo);
    // Map<String, JSONObject> config = new HashMap<String, JSONObject>();
    // for (int i = 0; i < nodeTypeConfig.size(); i++) {
    // JSONObject typeJson = nodeTypeConfig.getJSONObject(i);
    // config.put(typeJson.getString("type"), typeJson);
    // }
    // convertChildren(config, treeNodes);
    // return treeNodes;
    // }
    //
    // private void convertChildren(Map<String, JSONObject> nodeTypeConfig,
    // List<TreeNode> treeNodes) {
    // for (int i = 0; i < treeNodes.size(); i++) {
    // TreeNode treeNode = treeNodes.get(i);
    // String type = treeNode.getType();
    // if (nodeTypeConfig.containsKey(type)) {
    // JSONObject typeConfig = nodeTypeConfig.get(type);
    // int disableChecked = typeConfig.optInt("disableChecked", 0);
    // if (disableChecked == 1) {
    // treeNode.setNocheck(true);
    // }
    // JSONObject icon = typeConfig.optJSONObject("icon");
    // if (icon.containsKey("iconPath")) {
    // // json.put("icon", icon.get("iconPath"));
    // treeNode.setIconSkin(icon.get("iconPath") + " ");
    // }
    // if (icon.containsKey("className")) {
    // treeNode.setIconSkin(icon.get("className") + " ");
    // }
    // convertChildren(nodeTypeConfig, treeNode.getChildren());
    // }
    // }
    // }

    /**
     * 加载数据仓库树结点数据
     *
     * @param param
     * @param searchText
     * @param request
     * @return
     */
    @PostMapping(value = "/loadDataStoreTree")
    public @ResponseBody
    List<TreeNode> loadDataStoreTree(@RequestBody TreeDataStoreRequestParam param,
                                     HttpServletRequest request) {
        if (StringUtils.isNotBlank(param.getSearchText())) {
            String defaultCondition = StringUtils.replace(param.getDefaultCondition(), "&#39;", "'");
            if (StringUtils.isBlank(defaultCondition)) {
                defaultCondition = " 1 = 1 ";
            }
            List<String> conditions = Lists.newArrayList();
            conditions.add(defaultCondition);
            String displayColumnName = improvedNamingStrategy.propertyToColumnName(param.getDisplayColumn());
            conditions.add(displayColumnName + " like '%" + param.getSearchText() + "%'");
            param.setDefaultCondition(StringUtils.join(conditions, " and "));
        } else {
            param.setDefaultCondition(StringUtils.replace(param.getDefaultCondition(), "&#39;", "'"));
        }
        return ApplicationContextHolder.getBean(DatastoreTreeDataProvider.class).loadTreeData(param);
    }

    /**
     * 加载数据仓库树指定结点数据
     *
     * @param param
     * @param searchText
     * @param request
     * @return
     */
    @RequestMapping(value = "/loadDataStoreTreeNode")
    public @ResponseBody
    TreeNode loadDataStoreTreeNode(@RequestParam(required = true, value = "treeId") String treeId,
                                   TreeDataStoreRequestParam param, HttpServletRequest request) {
        String defaultCondition = StringUtils.replace(param.getDefaultCondition(), "&#39;", "'");
        if (StringUtils.isBlank(defaultCondition)) {
            defaultCondition = " 1 = 1 ";
        }
        List<String> conditions = Lists.newArrayList();
        conditions.add(defaultCondition);
        String uniqueColumnName = improvedNamingStrategy.propertyToColumnName(param.getUniqueColumn());
        conditions.add(uniqueColumnName + " = '" + treeId + "'");
        param.setDefaultCondition(StringUtils.join(conditions, " and "));
        return ApplicationContextHolder.getBean(DatastoreTreeDataProvider.class).loadTreeNodeById(treeId, param);
    }

}
