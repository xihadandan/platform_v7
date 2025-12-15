/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgUserLeaderDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserLeader;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
public interface MultiOrgUserLeaderService extends JpaService<MultiOrgUserLeader, MultiOrgUserLeaderDao, String> {
    /**
     * 按类型批量添加对应的管理者对象
     *
     * @param userId
     * @param ids
     * @param names
     * @param leaderType
     * @return
     */
    List<MultiOrgUserLeader> addLeaderListByType(String userId, String ids, String names, Integer leaderType);

    /**
     * 按类型获取组织元素对应的管理者列表
     *
     * @param userId
     * @param leaderType
     * @return
     */
    List<MultiOrgUserLeader> queryLeaderListByType(String userId, Integer leaderType);

    /**
     * 批量删除指定节点元素的管理者信息
     *
     * @param userId
     */
    boolean deleteLeaderList(String userId);
}
