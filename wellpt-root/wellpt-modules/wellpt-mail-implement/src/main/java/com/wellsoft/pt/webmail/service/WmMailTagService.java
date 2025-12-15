/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.bean.WmMailTagDto;
import com.wellsoft.pt.webmail.dao.WmMailTagDao;
import com.wellsoft.pt.webmail.entity.WmMailTagEntity;

import java.util.List;

/**
 * Description: 邮件标签服务类
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
public interface WmMailTagService extends JpaService<WmMailTagEntity, WmMailTagDao, String> {

    /**
     * 删除标签，并移除相关邮件标记关系
     *
     * @param uuid
     */
    void deleteTagAndEmailRela(String uuid);

    /**
     * 更新标签
     *
     * @param uuid
     */
    void updateTag(WmMailTagDto dto);

    /**
     * 新增标签
     *
     * @param dto
     * @return uuid
     */
    WmMailTagEntity addMailTag(WmMailTagDto dto);

    /**
     * 查询用户的所有自定义标签
     *
     * @param userId
     * @return
     */
    List<WmMailTagEntity> queryUserMailTags(String userId);
}
