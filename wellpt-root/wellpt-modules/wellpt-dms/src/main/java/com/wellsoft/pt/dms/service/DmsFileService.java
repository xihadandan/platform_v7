/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.basicdata.printtemplate.dto.PrintDto;
import com.wellsoft.pt.basicdata.printtemplate.dto.PrintsDto;
import com.wellsoft.pt.dms.bean.DmsFileVo;
import com.wellsoft.pt.dms.dao.DmsFileDao;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.repository.entity.LogicFileInfo;

import java.util.Collection;
import java.util.Date;
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
 * 2017-12-19.1	zhulh		2017-12-19		Create
 * </pre>
 * @date 2017-12-19
 */
public interface DmsFileService extends JpaService<DmsFileEntity, DmsFileDao, String> {

    List<DmsFileEntity> getByDataUuid(String dataUuid);

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    DmsFileEntity get(String uuid);

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<DmsFileEntity> findByExample(DmsFileEntity example);

    /**
     * 根据数据UUID查询总数
     *
     * @param dataUuid
     * @return
     */
    Long countByDataUuid(String dataUuid);

    /**
     * 保存
     *
     * @param entity
     */
    void save(DmsFileEntity entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<DmsFileEntity> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(DmsFileEntity entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<DmsFileEntity> entities);

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
     * @param folderUuid
     * @param dataUuid
     * @return
     */
    DmsFileEntity getByFolderUuidAndDataUuid(String folderUuid, String dataUuid);

    /**
     * 判断指定的组织ID是否为文件的编辑者
     *
     * @param orgId
     * @param fileUuid
     * @return
     */
    boolean isFileEditor(String orgId, String fileUuid);

    /**
     * 判断指定的组织ID是否为文件夹下的数据的编辑者
     *
     * @param userId
     * @param folderUuid
     * @param dataUuid
     * @return
     */
    boolean isFileEditor(String userId, String folderUuid, String dataUuid);

    /**
     * 保存文档为草稿
     *
     * @param dyFormData
     * @param fileUuid
     * @param folderUuid
     * @return
     */
    DmsFileEntity saveDocumentAsDraft(DyFormData dyFormData, String fileUuid, String folderUuid);

    /**
     * 保存文档
     *
     * @param dyFormData
     * @param fileUuid
     * @param folderUuid
     * @return
     */
    DmsFileEntity saveDocument(DyFormData dyFormData, String fileUuid, String folderUuid);

    /**
     * 保存表单数据到指定夹
     *
     * @param dyFormData
     * @param folderUuid
     */
    String archive(DyFormData dyFormData, String folderUuid, List<String> readerIds);

    /**
     * 保存表单数据到指定夹
     *
     * @param fileName
     * @param dyFormData
     * @param folderUuid
     * @param readerIds
     */
    String archive(String fileName, DyFormData dyFormData, String folderUuid, List<String> readerIds);

    /**
     * 保存表单数据到指定夹
     *
     * @param fileName
     * @param contentType
     * @param dyFormData
     * @param folderUuid
     */
    String archive(String fileName, String contentType, DyFormData dyFormData, String folderUuid, List<String> readerIds);

    /**
     * 归档
     *
     * @param dmsFile
     * @return
     */
    String archive(DmsFile dmsFile, List<String> readerIds);

    /**
     * 套打文件
     *
     * @param printDto
     * @return com.wellsoft.pt.repository.entity.LogicFileInfo
     **/
    public LogicFileInfo print(PrintDto printDto);

    /**
     * 批量套打文件
     *
     * @param printsDto
     * @return com.wellsoft.pt.repository.entity.LogicFileInfo
     **/
    public LogicFileInfo prints(PrintsDto printsDto);

    /**
     * 根据 归档文件实体 查询
     *
     * @param fileEntity
     * @return
     */
    List<DmsFileEntity> list(DmsFileEntity fileEntity);

    /**
     * 根据 uuid集合更新创建人
     *
     * @param dmsFileVo
     */
    int updateDmsFileCreator(DmsFileVo dmsFileVo);

    /**
     * 根据文件UUID，添加文件阅读者
     *
     * @param fileUuid
     * @param readerIds
     * @return
     */
    boolean addFileReader(String fileUuid, List<String> readerIds);

    /**
     * 根据文件数据UUID，添加文件阅读者
     *
     * @param dataUuid
     * @param readerIds
     * @return
     */
    boolean addFileReaderByDataUuid(String dataUuid, List<String> readerIds);

    /**
     * 根据文件UUID，获取文件阅读者
     *
     * @param fileUuid
     * @return
     */
    List<String> getFileReaders(String fileUuid);

    /**
     * 根据文件UUID，删除文件阅读者
     *
     * @param fileUuid
     * @param readerIds
     * @return
     */
    boolean deleteFileReader(String fileUuid, List<String> readerIds);

    /**
     * 根据文件数据UUID，删除文件阅读者
     *
     * @param dataUuid
     * @param readerIds
     * @return
     */
    boolean deleteFileReaderByDataUuid(String dataUuid, List<String> readerIds);

    Long countByFolderUuid(String folderUuid);

    /**
     * 检查同一目录下文件名是否重复
     *
     * @param fileName
     * @param fileUuid
     * @return
     */
    boolean existsTheSameFileNameWidthFileUuid(String fileName, String fileUuid);

    /**
     * 检查同一目录下文件名是否重复
     *
     * @param fileName
     * @param folderUuid
     * @return
     */
    boolean existsFileNameByFolderUuid(String fileName, String folderUuid);

    /**
     * @param folderUuid
     * @return
     */
    List<DmsFileEntity> listByFolderUuid(String folderUuid);

    /**
     * @param folderUuid
     * @param fromTime
     * @return
     */
    List<DmsFileEntity> listByFolderUuidAndGTEModifyTime(String folderUuid, Date fromTime);
}
