/*
 * @(#)2017年4月10日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mobile.facade.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.mobile.bean.MobileAppNav;
import com.wellsoft.pt.mobile.facade.service.MobileAppNavService;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*import com.wellsoft.pt.cms.bean.CmsCategory;
import com.wellsoft.pt.cms.dao.CmsCategoryDao;*/

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年4月10日.1	zhulh		2017年4月10日		Create
 * </pre>
 * @date 2017年4月10日
 */
@Service
@Transactional(readOnly = true)
public class MobileAppNavServiceImpl extends BaseServiceImpl implements MobileAppNavService {

    //@Autowired
    //private CmsCategoryDao cmsCategoryDao;
    @Autowired
    private SecurityAuditFacadeService securityAuditFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mobile.facade.service.MobileAppNavService#getCmsCategoryByParentId(java.lang.String)
     */
    @Override
    public TreeNode getCmsCategoryByParentId(String navId) {
        return null;
		/*
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rootNav", navId);
		List<MobileAppNav> appCategories = this.nativeDao.namedQuery("mobileAppNavQuery", map, MobileAppNav.class);
		CmsCategory category = cmsCategoryDao.findUniqueBy("code", navId);
		if (category == null) {
			return null;
		}

		TreeNode treeNode = new TreeNode();
		treeNode.setName(category.getTitle());
		treeNode.setId(category.getCode());

		treeNode = buildTreeNode(category.getUuid(), appCategories);

		// 空的二级导航移到新增的主导航分类下
		List<TreeNode> mainNavs = treeNode.getChildren();
		for (TreeNode mainNav : mainNavs) {
			List<TreeNode> moves = new ArrayList<TreeNode>();
			List<TreeNode> subNavs = mainNav.getChildren();
			for (TreeNode subNav : subNavs) {
				if (subNav.getChildren().isEmpty()) {
					moves.add(subNav);
				}
			}

			if (!moves.isEmpty()) {
				TreeNode newTreeNode = new TreeNode();
				newTreeNode.setName(mainNav.getName());
				newTreeNode.setId(UUID.randomUUID().toString());
				newTreeNode.getChildren().addAll(moves);

				mainNav.getChildren().add(0, newTreeNode);
				subNavs.removeAll(moves);
			}
		}
		return treeNode;
	*/
    }

    /**
     * 如何描述该方法
     *
     * @param appCategories
     * @return
     */
    private TreeNode buildTreeNode(String rootUuid, List<MobileAppNav> mobileAppNavs) {
        Map<String, List<MobileAppNav>> parentMap = new HashMap<String, List<MobileAppNav>>();
        for (MobileAppNav nav : mobileAppNavs) {
            if (rootUuid.equals(nav.getUuid())) {
                continue;
            }
            if (!securityAuditFacadeService.isGranted(nav.getResources())) {
                continue;
            }
            String parentUuid = nav.getParentUuid();
            if (!parentMap.containsKey(parentUuid)) {
                parentMap.put(parentUuid, new ArrayList<MobileAppNav>());
            }
            parentMap.get(parentUuid).add(nav);
        }

        TreeNode treeNode = new TreeNode();
        treeNode.setId(rootUuid);
        // 生成子结点
        buildChildNodes(treeNode, parentMap);
        return treeNode;
    }

    /**
     * 如何描述该方法
     *
     * @param treeNode
     * @param parentMap
     */
    private void buildChildNodes(TreeNode node, Map<String, List<MobileAppNav>> parentMap) {
        String key = node.getId();
        List<MobileAppNav> appNavs = parentMap.get(key);
        if (appNavs == null) {
            return;
        }
        for (MobileAppNav appNav : appNavs) {
            TreeNode child = new TreeNode();
            child.setId(appNav.getUuid());
            child.setName(appNav.getName());
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("uri", appNav.getUri());
            child.setData(data);
            node.getChildren().add(child);
            buildChildNodes(child, parentMap);
        }
    }

}
