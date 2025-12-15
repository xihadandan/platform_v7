/*
 * @(#)2016年9月10日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.facade.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.app.workflow.facade.service.WorkflowNewWorkService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.common.recentuse.facade.service.RecentUseFacadeService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Description: 平台应用发起工作门面服务实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年9月10日.1	zhulh		2016年9月10日		Create
 * </pre>
 * @date 2016年9月10日
 */
@Service
@Transactional(readOnly = true)
public class WorkflowNewWorkServiceImpl extends BaseServiceImpl implements WorkflowNewWorkService {

    @Autowired
    private FlowSchemeService flowSchemeService;

    @Autowired
    private RecentUseFacadeService recentUseFacadeService;

    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    /**
     * (non-Javadoc)
     */
    @Override
    public List<TreeNode> getFlowDefinitions() {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        treeNodes.addAll(getRecentUse(false));
        treeNodes.addAll(flowSchemeService.getAllFlowAsCategoryTree());
        return treeNodes;
    }

    @Override
    public List<TreeNode> getMobileFlowDefinitions() {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        treeNodes.addAll(getRecentUse(true));
        treeNodes.addAll(flowSchemeService.getAllMobileFlowAsCategoryTree());
        return treeNodes;

    }

    @Override
    public TreeNode getUserRecentUseFlowDefinitions() {
        List<TreeNode> recentUses = getRecentUse(true);
        return CollectionUtils.isNotEmpty(recentUses) ? recentUses.get(0) : null;
    }

    @Override
    public List<TreeNode> getUserAllFlowDefinitions() {
        return flowSchemeService.getAllFlowAsCategoryTree();
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    private List<TreeNode> getRecentUse(boolean isMobile) {
        List<FlowDefinition> flowDefinitions = flowSchemeService.getRecentUseFlowDefintions(isMobile);
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        Map<String, TreeNode> nodeMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(flowDefinitions)) {
            TreeNode categoryNode = new TreeNode();
            categoryNode.setId("recent_use");
            categoryNode.setName("最近使用");
            categoryNode.setData("recent_use");
            categoryNode.setIsParent(false);
            treeNodes.add(categoryNode);
            for (FlowDefinition flow : flowDefinitions) {
                if (!flow.getEnabled()) {
                    continue;
                } else {
                    if (isMobile) {
                        if (!flow.getIsMobileShow()) {
                            // 移动端要过滤掉隐藏的
                            continue;
                        }
                    } else {
                        if (flow.getPcShowFlag() != null && !flow.getPcShowFlag()) {
                            // PC端要过滤掉隐藏的
                            continue;
                        }
                    }
                }
                TreeNode flowDefNode = new TreeNode();
                flowDefNode.setId(flow.getId());
                nodeMap.put(flow.getId(), flowDefNode);
                flowDefNode.setName(flow.getName());
                flowDefNode.setData(flow.getId());
                categoryNode.getChildren().add(flowDefNode);
            }
            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                List<AppDefElementI18nEntity> i18nEntities = appDefElementI18nService.getI18ns(nodeMap.keySet(), IexportType.FlowDefinition, "workflowName", null, LocaleContextHolder.getLocale().toString());
                if (CollectionUtils.isNotEmpty(i18nEntities)) {
                    for (AppDefElementI18nEntity i : i18nEntities) {
                        if (nodeMap.containsKey(i.getDefId()) && StringUtils.isNotBlank(i.getContent())) {
                            nodeMap.get(i.getDefId()).setName(i.getContent());
                        }
                    }
                }
            }
        }
        return treeNodes;
    }

}
