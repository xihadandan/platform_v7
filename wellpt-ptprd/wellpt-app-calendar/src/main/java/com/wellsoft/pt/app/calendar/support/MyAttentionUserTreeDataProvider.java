/*
 * @(#)Apr 13, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.app.calendar.facade.service.AttentionFacade;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentRequestParam;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeType;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * Apr 13, 2018.1	zhulh		Apr 13, 2018		Create
 * </pre>
 * @date Apr 13, 2018
 */
@Component
public class MyAttentionUserTreeDataProvider implements TreeComponentDataProvider {

    @Autowired
    private AttentionFacade attentionFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#getName()
     */
    @Override
    public String getName() {
        return "日程管理_我关注的用户";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#getNodeTypes()
     */
    @Override
    public List<TreeType> getNodeTypes() {
        return Lists.newArrayList(TreeType.createTreeType("User", "用户"));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#loadTreeData(java.util.Map)
     */
    @Override
    public List<TreeNode> loadTreeData(TreeComponentRequestParam arg0) {
        List<OrgUserVo> list = attentionFacade.queryMyAttentionUserList();
        List<TreeNode> children = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(list)) {
            for (OrgUserVo entity : list) {
                TreeNode child = new TreeNode(entity.getUuid(), entity.getUserName(), null);
                child.setType("user");
                Map<String, Object> data = Maps.newHashMap();
                data.put("icon", "glyphicon glyphicon-heart pull-right my-icon-top");
                data.put("user", entity);
                data.put("type", "user");
                child.setData(data);
                children.add(child);
            }
        }
        return children;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#getFilterHint()
     */
    @Override
    public String getFilterHint() {
        // TODO Auto-generated method stub
        return null;
    }

}
