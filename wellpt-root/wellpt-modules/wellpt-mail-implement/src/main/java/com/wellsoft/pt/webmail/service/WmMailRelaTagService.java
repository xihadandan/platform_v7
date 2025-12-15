/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.dao.WmMailRelaTagDao;
import com.wellsoft.pt.webmail.entity.WmMailRelaTagEntity;

import java.util.List;

/**
 * Description: 邮件关联标签接口类
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
public interface WmMailRelaTagService extends JpaService<WmMailRelaTagEntity, WmMailRelaTagDao, String> {

    /**
     * 删除指定标记的邮件关系
     *
     * @param uuid
     */
    void deleteByTagUuid(String tagUuid);

    /**
     * 标记邮件
     *
     * @param uuid
     * @param emailUuids
     */
    void addMailRelaTag(String tagUuid, List<String> emailUuids);

    /**
     * 删除邮件的标记
     *
     * @param emailUuids
     * @param tagUuid
     */
    void deleteEmailRelaTag(List<String> emailUuids, String tagUuid);

    /**
     * 查询邮件关联的标签关系
     *
     * @param emailUuids
     * @return
     */
    List<WmMailRelaTagEntity> queryMailRelaTag(List<String> emailUuids);
}
