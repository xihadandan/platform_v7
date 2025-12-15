/*
 * @(#)5/28/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.dao.DmsObjectAssignActionDao;
import com.wellsoft.pt.dms.entity.DmsObjectAssignActionEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 5/28/24.1	zhulh		5/28/24		Create
 * </pre>
 * @date 5/28/24
 */
public interface DmsObjectAssignActionService extends JpaService<DmsObjectAssignActionEntity, DmsObjectAssignActionDao, Long> {

    /**
     * 保存文件阅读者、编辑者
     *
     * @param fileUuid
     * @param readerIds
     * @param editorIds
     */
    void saveFileReaderAndEditors(String fileUuid, Set<String> readerIds, Set<String> editorIds);

    /**
     * 根据对象数据标识删除数据
     *
     * @param objectIdIdentity
     */
    void removeByObjectIdIdentity(String objectIdIdentity);

    /**
     * 根据文件UUID，添加文件阅读者
     *
     * @param fileUuid
     * @param readerIds
     */
    void addFileReader(String fileUuid, List<String> readerIds);

    /**
     * 根据文件UUID，获拥有指定操作的用户
     *
     * @param fileUuid
     * @param fileActions
     * @return
     */
    List<String> listFileActionUser(String fileUuid, String... fileActions);

    /**
     * 根据文件UUID，删除文件阅读者
     *
     * @param fileUuid
     * @param readerIds
     */
    void deleteFileReader(String fileUuid, List<String> readerIds);

}
