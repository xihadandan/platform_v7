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
import com.wellsoft.pt.webmail.entity.WmMailUserEntity;
import com.wellsoft.pt.webmail.service.WmMailUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Description: 外部邮箱账号展示树的数据提供
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
public class WmMailOuterMailAccountTreeDataProvider implements TreeComponentDataProvider {

    @Resource
    WmMailUserService wmMailUserService;

    @Override
    public String getName() {
        return "平台邮件_外部邮箱账号数据树";
    }

    @Override
    public List<TreeType> getNodeTypes() {
        return Lists.newArrayList(TreeType.createTreeType("MAIL_ACCOUNT", "邮箱账号"));
    }

    @Override
    public String getFilterHint() {
        return null;
    }

    @Override
    public List<TreeNode> loadTreeData(TreeComponentRequestParam arg0) {
        TreeNode treeNode = new TreeNode("0", "其他邮箱账号", null);
        treeNode.setIsParent(true);
        Map<String, Object> rootData = Maps.newHashMap();
        List<WmMailUserEntity> wmMailUserEntities = wmMailUserService.listOuterMailUser(
                SpringSecurityUtils.getCurrentUserId());


        List<TreeNode> children = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(wmMailUserEntities)) {
            for (WmMailUserEntity entity : wmMailUserEntities) {
                TreeNode child = new TreeNode(entity.getUuid(), entity.getMailAddress(),
                        null);
                child.setData(entity);
                child.setIsParent(false);
                children.add(child);
            }
        } else {
            rootData.put("hidden", true);// 如果没有外部账号，则不展示
        }
        treeNode.setData(rootData);
        treeNode.setIconSkin("iconfont icon-ptkj-zhanghaoyonghuming");
        treeNode.setChildren(children);
        return Lists.newArrayList(treeNode);
    }

}
