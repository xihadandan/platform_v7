/*
 * @(#)4/18/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.DingtalkUserDao;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkUserEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/18/25.1	    zhulh		4/18/25		    Create
 * </pre>
 * @date 4/18/25
 */
public interface DingtalkUserService extends JpaService<DingtalkUserEntity, DingtalkUserDao, Long> {

    DingtalkUserEntity getByUnionIdAndOrgVersionUuid(String unionId, Long orgVersionUuid);

    List<DingtalkUserEntity> listLeaderUserByOrgVersionUuid(Long orgVersionUuid);

    List<String> listOaUserIdByUserIdAndOrgVersionUuid(List<String> userIds, Long orgVersionUuid);

    DingtalkUserEntity getByUserIdAndOrgVersionUuid(String userId, Long orgVersionUuid);

    Map<String, String> listUserIdMapByOaUserIds(List<String> oaUserIds, String appId);

    Map<String, String> listUnionIdMapByOaUserIds(List<String> oaUserIds, String appId);

    String getUserIdByOaUserId(String oaUserId, String appId);

    String getUnionIdByOaUserId(String oaUserId, String appId);

    String getOaUserIdByUnionIdAndAppId(String unionId, String appId);
}
