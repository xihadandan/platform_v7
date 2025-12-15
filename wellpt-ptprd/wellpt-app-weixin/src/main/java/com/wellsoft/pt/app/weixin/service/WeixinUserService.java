/*
 * @(#)5/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.service;

import com.wellsoft.pt.app.weixin.dao.WeixinUserDao;
import com.wellsoft.pt.app.weixin.entity.WeixinUserEntity;
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
 * 5/21/25.1	    zhulh		5/21/25		    Create
 * </pre>
 * @date 5/21/25
 */
public interface WeixinUserService extends JpaService<WeixinUserEntity, WeixinUserDao, Long> {

    WeixinUserEntity getByUserIdAndCorpId(String userId, String corpId);

    WeixinUserEntity getByUserIdAndOrgVersionUuid(String userId, Long orgVersionUuid);

    List<String> listOaUserIdByUserIdAndOrgVersionUuid(List<String> userIds, Long orgVersionUuid);

    List<WeixinUserEntity> listLeaderUserByOrgVersionUuid(Long orgVersionUuid);

    List<WeixinUserEntity> listWithDirectLeaderByOrgVersionUuid(Long orgVersionUuid);

    Map<String, String> listUserIdMapByOaUserIds(List<String> oaUserIds, String corpId);

    String getUserIdByOaUserIdAndCorpId(String oaUserId, String corpId);

    String getOaUserIdByUserIdAndCorpId(String userId, String corpId);
}
