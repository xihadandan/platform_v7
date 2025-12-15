/*
 * @(#)Apr 13, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentRequestParam;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeType;
import com.wellsoft.pt.dms.entity.DmsTagEntity;
import com.wellsoft.pt.dms.facade.service.DmsTagFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
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
public class DmsMyTagTreeDataProvider implements TreeComponentDataProvider {

    @Autowired
    private DmsTagFacadeService dmsTagFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#getName()
     */
    @Override
    public String getName() {
        return "数据管理_我的标签";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#getNodeTypes()
     */
    @Override
    public List<TreeType> getNodeTypes() {
        return Lists.newArrayList(TreeType.createTreeType("TAG", "标签"));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#loadTreeData(java.util.Map)
     */
    @Override
    public List<TreeNode> loadTreeData(TreeComponentRequestParam arg0) {
        TreeNode treeNode = new TreeNode("0", "我的标签", null);
        treeNode.setIsParent(true);
        Map<String, Object> rootData = Maps.newHashMap();
        List<DmsTagEntity> wmMailTagEntities = dmsTagFacadeService
                .queryUserTags(SpringSecurityUtils.getCurrentUserId());

        List<TreeNode> children = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(wmMailTagEntities)) {
            for (DmsTagEntity entity : wmMailTagEntities) {
                TreeNode child = new TreeNode(entity.getId(), entity.getName(), null);
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put("uuid", entity.getUuid());
                dataMap.put("tagName", entity.getName());
                dataMap.put("tagColor", entity.getColor());
                dataMap.put("icon", "glyphicon glyphicon-stop");
                dataMap.put("iconStyle", "color:" + entity.getColor() + ";");
                child.setData(dataMap);
                child.setIsParent(false);
                children.add(child);
            }
        } else {
            rootData.put("hidden", true);// 如果没有自定义标签，则不展示
        }
        treeNode.setData(rootData);
        treeNode.setChildren(children);
        return Lists.newArrayList(treeNode);
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
