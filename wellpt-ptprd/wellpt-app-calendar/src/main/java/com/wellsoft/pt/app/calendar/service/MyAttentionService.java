/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.service;

import com.wellsoft.pt.app.calendar.dao.MyAttentionDao;
import com.wellsoft.pt.app.calendar.entity.MyAttentionEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 公共通讯录标签服务类
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
public interface MyAttentionService extends JpaService<MyAttentionEntity, MyAttentionDao, String> {

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<MyAttentionEntity> queryMyAttentionListByType(String type);

    /**
     * 如何描述该方法
     *
     * @param userId
     * @return
     */
    boolean cancelAttentionUser(String userId);

    /**
     * 如何描述该方法
     *
     * @param groupUuid
     */
    boolean cancelAttentionGroup(String groupUuid);

}
