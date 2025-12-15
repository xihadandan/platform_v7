/*
 * @(#)2017-12-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.dms.dao.DmsFolderConfigurationDao;
import com.wellsoft.pt.dms.entity.DmsFolderConfigurationEntity;
import com.wellsoft.pt.dms.model.DmsFolderConfiguration;
import com.wellsoft.pt.dms.model.DmsFolderDataViewConfiguration;
import com.wellsoft.pt.dms.model.DmsFolderDyformDefinition;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-12-21.1	zhulh		2017-12-21		Create
 * </pre>
 * @date 2017-12-21
 */
public interface DmsFolderConfigurationService extends JpaService<DmsFolderConfigurationEntity, DmsFolderConfigurationDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    DmsFolderConfigurationEntity get(String uuid);

    /**
     * 如何描述该方法
     *
     * @param folderUuid
     * @return
     */
    DmsFolderConfigurationEntity getByFolderUuid(String folderUuid);

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<DmsFolderConfigurationEntity> findByExample(DmsFolderConfigurationEntity example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(DmsFolderConfigurationEntity entity);

    /**
     * 保存夹配置
     *
     * @param folderConfiguration
     */
    void saveFolderConfiguration(DmsFolderConfiguration folderConfiguration);

    /**
     * 更新源文件夹的继承的上级夹权限引用为目标夹
     *
     * @param folderUuid
     * @param refObjectIdentityUuidOfFolderUuid
     */
    void updateInheritFolderRole(String folderUuid, String refObjectIdentityUuidOfFolderUuid);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<DmsFolderConfigurationEntity> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(DmsFolderConfigurationEntity entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<DmsFolderConfigurationEntity> entities);

    /**
     * 根据UUID删除记录
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuids
     */
    void removeAllByPk(Collection<String> uuids);

    /**
     * 根据夹UUID获取夹配置的表单定义信息，如果找不到，取上级夹
     *
     * @param folderUuid
     * @return
     */
    DmsFolderDyformDefinition getFolderDyformDefinitionByFolderUuid(String folderUuid);

    /**
     * 根据夹UUID获取夹及子夹配置的表单定义信息，如果找不到，取上级夹
     *
     * @param folderUuid
     * @return
     */
    List<DmsFolderDyformDefinition> getAllFolderDyformDefinitionsByFolderUuid(String folderUuid);

    /**
     * 根据文件UUID获取夹配置的表单定义信息，如果找不到，取上级夹
     *
     * @param fileUuid
     * @return
     */
    DmsFolderDyformDefinition getFolderDyformDefinitionByFileUuid(String fileUuid);

    /**
     * 根据文件UUID获取夹配置的视图配置信息，如果找不到，取上级夹
     *
     * @param folderUuid
     * @return
     */
    DmsFolderDataViewConfiguration getFolderDataViewConfigurationByFolderUuid(String folderUuid);

    /**
     * 根据夹UUID获取夹配置的夹数据类型
     *
     * @param folderUuid
     * @return
     */
    List<String> getFolderDataTypeByFolderUuid(String folderUuid);

    /**
     * 根据文件UUID获取夹配置的夹数据类型
     *
     * @param fileUuid
     * @return
     */
    List<String> getFolderDataTypeByFileUuid(String fileUuid);

    /**
     * 根据用户获取对应的套打模板树
     * 超管用户：可见超管和全部系统单位定义的打印模板
     * 单位管理员：可见超管和当前系统单位中创建的打印模板
     *
     * @return
     */
    List<TreeNode> getPrintTemplateTreeByUser();

    /**
     * 1、读取该文件夹的“打印模板”配置
     * 2、如果该文件夹没有配置“打印模板”，读取其父级夹，父级还没有，找父级的父级，有查到即终止
     * 3、当文档所在文件夹及其所有父级夹都未设置打印模板时，获取全部打印模板（全部打印模板：目前是超管和当前系统单位的）
     *
     * @param folderUuids
     * @return java.util.List<com.wellsoft.context.component.tree.TreeNode>
     **/
    List<TreeNode> getPrintTemplateTreeByFolderUuids(List<String> folderUuids);

    /**
     * @param fileUuid
     * @return
     */
    String getRefObjectIdentityUuidByFileUuid(String fileUuid);
}
