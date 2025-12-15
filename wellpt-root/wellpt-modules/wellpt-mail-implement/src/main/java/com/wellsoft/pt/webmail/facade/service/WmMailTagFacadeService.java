/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.webmail.bean.WmMailTagDto;

import java.util.List;
import java.util.Map;

/**
 * Description: 邮件标签门面服务
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
public interface WmMailTagFacadeService extends BaseService {

    /**
     * 删除文件夹
     *
     * @param uuid
     * @param isDeleteEmail 是否删除邮件
     */
    void deleteTag(String uuid);

    /**
     * 更新标签
     */
    void updateTag(WmMailTagDto dto);

    /**
     * 新建标签
     *
     * @param WmMailTagDto
     */
    String addMailTag(WmMailTagDto dto);

    /**
     * 查询当前用户的所有自定义标签
     *
     * @return
     */
    List<WmMailTagDto> queryUserMailTags();

    /**
     * 查询邮件关联的标签数据
     *
     * @param emailUuids
     * @return {'emailUuid':'123123','tags':[WmMailTagDto]}
     */
    Map<String, List<WmMailTagDto>> queryMailRelaTag(List<String> emailUuids);

    /**
     * 新建标签并标记邮件
     *
     * @param emailUuids
     */
    void addTagAndMarkEmails(List<String> emailUuids, WmMailTagDto dto);

    /**
     * 删除邮件的标记
     *
     * @param emailUuids
     * @param tagUuid
     */
    void deleteEmailRelaTag(List<String> emailUuids, String tagUuid);

    /**
     * 标记邮件
     *
     * @param tagUuid
     * @param emailUuids
     */
    void addMailRelaTag(String tagUuid, List<String> emailUuids);
}
