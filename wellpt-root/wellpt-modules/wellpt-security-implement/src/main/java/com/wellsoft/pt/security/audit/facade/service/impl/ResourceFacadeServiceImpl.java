/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.facade.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.security.audit.bean.ResourceBean;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.facade.service.ResourceFacadeService;
import com.wellsoft.pt.security.audit.service.ResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月12日.1	chenqiong		2018年4月12日		Create
 * </pre>
 * @date 2018年4月12日
 */
@Service
public class ResourceFacadeServiceImpl implements ResourceFacadeService {

    @Autowired
    ResourceService resourceService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.ResourceFacadeService#getBean(java.lang.String)
     */
    @Override
    public ResourceBean getBean(String uuid) {
        return resourceService.getBean(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.ResourceFacadeService#saveBean(com.wellsoft.pt.security.audit.bean.ResourceBean)
     */
    @Override
    public String saveBean(ResourceBean bean) {
        return resourceService.saveBean(bean);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.ResourceFacadeService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        resourceService.remove(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.ResourceFacadeService#getMenuAsTree(java.lang.String)
     */
    @Override
    public TreeNode getMenuAsTree(String excludeUuid) {
        return resourceService.getMenuAsTree(excludeUuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.ResourceFacadeService#getResourceByCode(java.lang.String)
     */
    @Override
    public com.wellsoft.pt.security.audit.entity.Resource getResourceByCode(String code) {
        return resourceService.getResourceByCode(code);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.ResourceFacadeService#getResourceButtonTree(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getResourceButtonTree(String treeNodeId, String excludeUuid) {
        return resourceService.getResourceButtonTree(treeNodeId, excludeUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.ResourceFacadeService#getResourceButtonTree(java.lang.String)
     */
    @Override
    public List<TreeNode> getResourceButtonTree(String excludeUuid) {
        return resourceService.getResourceButtonTree(TreeNode.ROOT_ID, excludeUuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.ResourceFacadeService#getResourceMenuTree(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getResourceMenuTree(String treeNodeId, String excludeUuid) {
        return resourceService.getResourceMenuTree(treeNodeId, excludeUuid);
    }

    @Override
    public long coutModuleMenus(String moduleId) {
        return resourceService.coutModuleMenus(moduleId);
    }

    @Override
    public List<Resource> getModuleMenuResources(String id) {
        return resourceService.listModuleMenuResources(id);
    }

    @Override
    public TreeNode getModuleMenuAsTree(String uuid, String moduleId) {

        TreeNode rootNode = new TreeNode();
        // 从根节点开始获取
        if (TreeNode.ROOT_ID.equals(uuid)) {
            rootNode.setName("资源");
            rootNode.setId(TreeNode.ROOT_ID);
            rootNode.setNocheck(true);
        } else {// 获取指定节点的数据
            com.wellsoft.pt.security.audit.entity.Resource obj = this.resourceService.getOne(uuid);
            rootNode.setName(obj.getName());
            rootNode.setId(obj.getUuid());
            rootNode.setData(obj.getCode());
            rootNode.setNocheck(true);
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put("parentUuidIsNull", true);
        if (StringUtils.isNotBlank(moduleId)) {
            params.put("moduleId", moduleId);
        }
        params.put("orderBy", " order by   t1.create_time asc");
        List<Resource> topParentDics = resourceService.queryModuleSecurityMenuResource(
                params);

        params.remove("parentUuidIsNull");
        params.put("parentUuidIsNotNull", true);
        params.remove("moduleId");
        List<Resource> all = resourceService.queryModuleSecurityMenuResource(
                params);//查询全部子数据

        // 将所有节点数据按上下级关系分组
        Multimap<String, Resource> map = Multimaps.index(all,
                new Function<Resource, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable Resource resource) {
                        return StringUtils.isNotBlank(
                                resource.getParentUuid()) ? resource.getParentUuid() : "-1";
                    }
                });
        Map<String, Collection<Resource>> groupResourceMap = map.asMap();

        for (Resource resource : topParentDics) {
            TreeNode thisNode = new TreeNode();
            thisNode.setName(resource.getName());
            thisNode.setId(resource.getUuid());
            rootNode.getChildren().add(thisNode);
            appendChildDataDicNode(thisNode, resource, moduleId, groupResourceMap);
        }
        return rootNode;
    }

    private void appendChildDataDicNode(TreeNode thisNode, Resource resource,
                                        String moduleId,
                                        Map<String, Collection<Resource>> ddMap) {
        String key = thisNode.getId();
        Collection<Resource> ddList = ddMap.get(key);
        if (ddList == null) {
            return;
        }
        for (Resource dd : ddList) {
            TreeNode child = new TreeNode();
            child.setId(dd.getUuid());
            child.setName(dd.getName());
//            child.setData(dataDictionary.getIsRef() ? true : dd.getIsRef());
            thisNode.getChildren().add(child);
            appendChildDataDicNode(child, dd, moduleId, ddMap);
        }
    }

}
