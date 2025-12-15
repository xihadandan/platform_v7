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
import com.wellsoft.pt.webmail.entity.WmMailTagEntity;
import com.wellsoft.pt.webmail.service.WmMailTagService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Description: 我的标签展示树的数据提供
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
public class WmMailTagTreeDataProvider implements TreeComponentDataProvider {

    @Resource
    WmMailTagService wmMailTagService;

    @Override
    public String getName() {
        return "平台邮件_我的标签数据树";
    }

    @Override
    public List<TreeType> getNodeTypes() {
        return Lists.newArrayList(TreeType.createTreeType("TAG", "标签"));
    }

    @Override
    public List<TreeNode> loadTreeData(TreeComponentRequestParam param) {
        TreeNode treeNode = new TreeNode("0", "我的标签", null);
        treeNode.setIsParent(true);
        Map<String, Object> rootData = Maps.newHashMap();
        List<WmMailTagEntity> wmMailTagEntities = wmMailTagService.queryUserMailTags(SpringSecurityUtils
                .getCurrentUserId());

        List<TreeNode> children = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(wmMailTagEntities)) {
            for (WmMailTagEntity entity : wmMailTagEntities) {
                TreeNode child = new TreeNode(entity.getSeq().toString(), entity.getTagName(), null);
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put("uuid", entity.getUuid());
                dataMap.put("tagName", entity.getTagName());
                dataMap.put("tagColor", entity.getTagColor());
                dataMap.put("icon", "glyphicon glyphicon-stop");
                dataMap.put("iconStyle", "color:" + entity.getTagColor() + ";");
                child.setData(dataMap);
                child.setIsParent(false);
                children.add(child);
            }
        } else {
            rootData.put("hidden", true);// 如果没有自定义标签，则不展示
        }
        treeNode.setData(rootData);
        treeNode.setChildren(children);
        treeNode.setIconSkin("iconfont icon-ptkj-biaoqian");
        return Lists.newArrayList(treeNode);
    }

    @Override
    public String getFilterHint() {
        return null;
    }

}
