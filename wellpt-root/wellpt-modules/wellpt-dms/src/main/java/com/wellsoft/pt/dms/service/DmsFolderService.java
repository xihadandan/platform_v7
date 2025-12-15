/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dms.dao.DmsFolderDao;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Collection;
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
 * 2017-12-19.1	zhulh		2017-12-19		Create
 * </pre>
 * @date 2017-12-19
 */
public interface DmsFolderService extends JpaService<DmsFolderEntity, DmsFolderDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    DmsFolderEntity get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<DmsFolderEntity> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<DmsFolderEntity> findByExample(DmsFolderEntity example);

    List<QueryItem> listByParams(Map<String, Object> params, PagingInfo pagingInfo);

    /**
     * 获取根夹
     *
     * @return
     */
    List<DmsFolderEntity> getRootFolders();

    /**
     * @param system
     * @return
     */
    List<DmsFolderEntity> listLibraryBySystem(String system);

    /**
     * 保存
     *
     * @param entity
     */
    void save(DmsFolderEntity entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<DmsFolderEntity> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(DmsFolderEntity entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<DmsFolderEntity> entities);

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
     * 如何描述该方法
     *
     * @param parentUuid
     * @return
     */
    Long countByParentUuid(String parentUuid);

    /**
     * 如何描述该方法
     *
     * @param parentUuids
     * @return
     */
    Map<String, Long> countByParentUuids(List<String> parentUuids);

    /**
     * 添加用户自己夹的私有权限
     *
     * @param folderUuid
     */
    void addUserPrivateAdminPermission(String folderUuid);

    /**
     * 根据夹名称、上级夹UUID判断夹是否存在
     *
     * @param childFolderName
     * @param parentFolderUuid
     * @return
     */
    boolean isExists(String childFolderName, String parentFolderUuid);

    /**
     * 根据夹名称、上级夹UUID获取夹
     *
     * @param childFolderName
     * @param parentFolderUuid
     * @return
     */
    DmsFolderEntity getByFolderNameAndParentFolderUuid(String childFolderName,
                                                       String parentFolderUuid);

    boolean existsFileUnderFolerIncludeChildFolder(String folderUuid);

    public Map<String, Object> existsFolerChildFile(Collection<String> folderUuids);

    public void removeAllUuids(Collection<String> folderUuids);

    /**
     * 根据文件UUID获取夹
     *
     * @param fileUuid
     * @return
     */
    DmsFolderEntity getByFileUuid(String fileUuid);

    /**
     * 列出文件库
     *
     * @return
     */
    List<DmsFolderEntity> listFileLibrary();

    /**
     * 更新夹编号
     *
     * @param folderCodes
     */
    void updateFolderCodes(Map<String, String> folderCodes);

    /**
     * 根据UUID夹路径获取继承夹权限的子夹
     *
     * @param uuidPath
     * @return
     */
    List<DmsFolderEntity> listChildrenOfInheritFolderRoleByUuidPath(String uuidPath);

    /**
     * 检测夹下是否存在指定文件夹名的夹
     *
     * @param folderName
     * @param parentFolderUuid
     * @return
     */
    boolean existsFolderNameByParentFolderUuid(String folderName, String parentFolderUuid);

    /**
     * 检查同一目录下文件夹名是否重复
     *
     * @param folderName
     * @param folderUuid
     * @return
     */
    boolean existsTheSameFolderNameWidthFolderUuid(String folderName, String folderUuid);

    /**
     * @param libraryUuid
     * @return
     */
    List<String> listFolderUuidByLibraryUuid(String libraryUuid);

    /**
     * @param folderUuids
     * @return
     */
    Map<String, String> listFolderPathNameAsMap(Collection<String> folderUuids);
}
