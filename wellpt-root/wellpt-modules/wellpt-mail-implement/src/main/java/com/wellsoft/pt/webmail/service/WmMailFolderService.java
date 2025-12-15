/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.bean.WmMailFolderDto;
import com.wellsoft.pt.webmail.dao.WmMailFolderDao;
import com.wellsoft.pt.webmail.entity.WmMailFolderEntity;

import java.util.List;

/**
 * Description: 邮件文件夹服务类
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
public interface WmMailFolderService extends JpaService<WmMailFolderEntity, WmMailFolderDao, String> {

    /**
     * 重命名文件夹名称
     *
     * @param uuid
     * @param rename
     */
    void renameFolder(String uuid, String rename);

    /**
     * 新增文件夹
     *
     * @param dto
     * @return uuid
     */
    WmMailFolderEntity addFolder(WmMailFolderDto dto);

    /**
     * 查询用户的所有自定义文件夹
     *
     * @param userId
     * @return
     */
    List<WmMailFolderEntity> queryUserFolders(String userId);

    /**
     * 根据用户Id 文件夹名称查询 是否存在
     *
     * @param userId
     * @param folerName
     * @return
     */
    boolean existSameNameFolder(String userId, String folerName);
}
