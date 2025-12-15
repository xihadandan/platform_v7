package com.wellsoft.pt.app.feishu.service;

import com.wellsoft.pt.app.feishu.dao.FeishuUserDao;
import com.wellsoft.pt.app.feishu.entity.FeishuUserEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FeishuUserService extends JpaService<FeishuUserEntity, FeishuUserDao, Long> {
    List<FeishuUserEntity> listByOpenIdsAndOrgVersionUuid(Set<String> openIds, Long orgVersionUuid);

    Map<String, String> listOpenIdMapByOaUserId(Set<String> oaUserIds, String appId);

    Map<String, String> listOaUserIdMapByOpenUserId(Set<String> openIds, String appId);

    Map<String, String> getUserNamesByOpenIds(Set<String> openIds, String appId);

    String getOaUserIdByOpenIdAndConfigUuid(String openId, Long configUuid);

    String getOaUserIdByOpenIdAndOrgVersionUuid(String openId, Long orgVersionUuid);

    List<String> listOaUserIdByOpenIdAndOrgVersionUuid(List<String> openIds, Long orgVersionUuid);

    FeishuUserEntity getByOpenIdAndOrgVersionUuid(String openId, Long orgVersionUuid);

    void updateStatusByOpenIdAndOrgVersionUuid(String status, String openId, Long orgVersionUuid);

}
