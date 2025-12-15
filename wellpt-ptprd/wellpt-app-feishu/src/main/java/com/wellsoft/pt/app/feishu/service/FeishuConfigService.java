package com.wellsoft.pt.app.feishu.service;

import com.wellsoft.pt.app.feishu.dao.FeishuConfigDao;
import com.wellsoft.pt.app.feishu.entity.FeishuConfigEntity;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;
import com.wellsoft.pt.jpa.service.JpaService;

public interface FeishuConfigService extends JpaService<FeishuConfigEntity, FeishuConfigDao, Long> {
    FeishuConfigVo query();

    FeishuConfigVo getBySystemAndTenant(String system, String tenant);

    Long save(FeishuConfigVo feishuConfigVo);

    void testCreateToken(String appId, String appSecret, String baseUrl);

    void syncOrg(FeishuConfigVo feishuConfigVo);

    FeishuConfigEntity getByAppId(String appId);
}
