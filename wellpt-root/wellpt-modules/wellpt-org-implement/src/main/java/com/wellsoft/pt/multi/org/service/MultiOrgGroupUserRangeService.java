/*
 * @(#)2022-04-16 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgGroupUserRangeDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupUserRangeEntity;

import java.util.List;

/**
 * Description: 数据库表MULTI_ORG_GROUP_USER_RANGE的service服务接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-04-16.1	zenghw		2022-04-16		Create
 * </pre>
 * @date 2022-04-16
 */
public interface MultiOrgGroupUserRangeService
        extends JpaService<MultiOrgGroupUserRangeEntity, MultiOrgGroupUserRangeDao, String> {

    /**
     * 通过群组ID删除使用者
     *
     * @param groupId 群组ID
     * @return void
     **/
    public void deleteByGroupId(String groupId);

    /**
     * 通过群组ID 获取使用者集合
     *
     * @param groupId
     **/
    public List<MultiOrgGroupUserRangeEntity> getUserRangeListBygroupId(String groupId);

    /**
     * 通过群组ID集合 获取使用者集合
     * 找不到数据返回null
     *
     * @param groupIds
     **/
    public List<MultiOrgGroupUserRangeEntity> getUserRangeListBygroupIds(List<String> groupIds);

}
