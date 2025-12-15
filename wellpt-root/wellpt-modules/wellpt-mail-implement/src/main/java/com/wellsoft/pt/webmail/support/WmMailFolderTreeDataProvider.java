/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentRequestParam;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeType;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.entity.WmMailFolderEntity;
import com.wellsoft.pt.webmail.service.WmMailFolderService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Description: 我的文件夹展示树的数据提供
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年2月27日.1	chenqiong		2018年2月27日		Create
 * </pre>
 * @date 2018年2月27日
 */
@Component
public class WmMailFolderTreeDataProvider implements TreeComponentDataProvider {

    @Resource
    WmMailFolderService wmMailFolderService;

    @Override
    public String getName() {
        return "平台邮件_我的文件夹数据树";
    }

    @Override
    public List<TreeType> getNodeTypes() {
        return Lists.newArrayList(TreeType.createTreeType("FOLDER", "文件夹"));
    }

    @Override
    public String getFilterHint() {
        return null;
    }

    @Override
    public List<TreeNode> loadTreeData(TreeComponentRequestParam arg0) {
        TreeNode treeNode = new TreeNode("0", "我的文件夹", null);
        treeNode.setIsParent(true);
        Map<String, Object> rootData = Maps.newHashMap();
        List<WmMailFolderEntity> wmMailFolderEntities = wmMailFolderService.queryUserFolders(SpringSecurityUtils
                .getCurrentUserId());

        List<TreeNode> children = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(wmMailFolderEntities)) {
            for (WmMailFolderEntity entity : wmMailFolderEntities) {
                TreeNode child = new TreeNode(entity.getSeq().toString(), entity.getFolderName(), null);
                child.setData(entity);
                child.setIsParent(false);
                children.add(child);
            }
        } else {
            rootData.put("hidden", true);// 如果没有自定义文件夹，则不展示
        }
        treeNode.setData(rootData);
        treeNode.setIconSkin("iconfont icon-oa-wodewenjianjia");
        treeNode.setChildren(children);
        return Lists.newArrayList(treeNode);
    }

}
