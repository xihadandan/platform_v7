/*
 * @(#)2013-1-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.audit.bean.ResourceBean;
import com.wellsoft.pt.security.audit.bean.ResourceDto;
import com.wellsoft.pt.security.audit.dao.ResourceDao;
import com.wellsoft.pt.security.audit.entity.Resource;
import org.springframework.security.access.ConfigAttribute;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 资源管理服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-17.1	zhulh		2013-1-17		Create
 * </pre>
 * @date 2013-1-17
 */
public interface ResourceService extends JpaService<Resource, ResourceDao, String> {

    /**
     * 根据UUID获取资源
     *
     * @param uuid
     * @return
     */
    ResourceBean getBean(String uuid);

    /**
     * 保存资源
     *
     * @param bean
     */
    String saveBean(ResourceBean bean);

    /**
     * 根据UUID删除资源
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 获取资源的树形结构
     *
     * @param excludeUuid
     * @return
     */
    TreeNode getMenuAsTree(String excludeUuid);

    /**
     * 获取资源的树形结构
     *
     * @param excludeUuid
     * @return
     */
    List<TreeNode> getResourceMenuTree(String treeNodeId, String excludeUuid);

    /**
     * 获取资源按钮树
     *
     * @param treeNodeId
     * @param excludeId
     * @return
     */
    List<TreeNode> getResourceButtonTree(String treeNodeId, String excludeUuid);

    /**
     * 根据模块资源编号，获该模块下的所有动态按钮
     *
     * @param code
     * @return
     */
    List<Resource> getDynamicButtonResourcesByCode(String code);

    Map<String, Resource> loadResourceMap();

    /**
     * 加载所有的安全配置
     *
     * @return
     */
    Map<String, Map<Object, Collection<ConfigAttribute>>> loadConfigAttribute();

    /**
     * 加载指定配置类型的安全配置
     *
     * @param configUuid
     * @param configType
     * @return
     */
    Map<String, Map<Object, Collection<ConfigAttribute>>> loadConfigAttribute(String configUuid, String configType);

    /**
     * 根据用户UUID返回用户所具有的URL资源
     *
     * @param userUuid
     * @return
     */
    List<Resource> getUrlResourcesByUserUuid(String userUuid);

    /**
     * 如何描述该方法
     *
     * @param code
     * @return
     */
    Resource getResourceByCode(String code);

    Resource get(String uuid);

    void save(Resource resource);

    /**
     * 将childCode资源更改到toParentCode下
     *
     * @param childCode
     * @param toParentCode
     */
    void updateParent(String childCode, String toParentCode);

    /**
     * 如何描述该方法
     *
     * @param code
     * @return
     */
    List<Resource> getByCode(String code);

    List<Resource> queryModuleSecurityMenuResource(Map<String, Object> params);

    long coutModuleMenus(String moduleId);

    void updateConfigAttributeByPrivilege(String privilegeUuid);

    List<ResourceDto> getModuleMenuResources(String moduleId);

    List<Resource> listModuleMenuResources(String moduleId);
}
