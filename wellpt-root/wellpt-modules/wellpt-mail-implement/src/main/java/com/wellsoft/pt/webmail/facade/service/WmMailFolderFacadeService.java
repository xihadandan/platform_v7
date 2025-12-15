/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.webmail.bean.WmMailFolderDto;

import java.util.List;

/**
 * Description: 邮件文件夹门面服务
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
public interface WmMailFolderFacadeService extends BaseService {

    /**
     * 删除文件夹
     *
     * @param uuid
     * @param isDeleteEmail 是否删除邮件
     */
    void deleteFolder(String uuid, boolean isDeleteEmail);

    /**
     * 更新文件夹名称
     *
     * @param uuid
     * @param rename
     */
    void updateFolderName(String uuid, String rename);

    /**
     * 新建文件夹
     *
     * @param wmMailFolderDto
     * @return 文件夹编码 folderCode
     */
    String addFolder(WmMailFolderDto wmMailFolderDto);

    /**
     * 查询当前用户的所有自定义文件夹
     *
     * @return
     */
    List<WmMailFolderDto> queryUserFolders();

    /**
     * 新建文件夹并移入邮件
     *
     * @param emailUuids
     */
    void addFolderAndEmailMovIn(List<String> emailUuids, WmMailFolderDto dto);
}
