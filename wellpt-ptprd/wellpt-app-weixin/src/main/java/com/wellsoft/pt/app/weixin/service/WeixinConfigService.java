/*
 * @(#)5/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.service;

import com.wellsoft.pt.app.weixin.dao.WeixinConfigDao;
import com.wellsoft.pt.app.weixin.entity.WeixinConfigEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/20/25.1	    zhulh		5/20/25		    Create
 * </pre>
 * @date 5/20/25
 */
public interface WeixinConfigService extends JpaService<WeixinConfigEntity, WeixinConfigDao, Long> {
    WeixinConfigEntity getBySystem(String system);

    long countByCorpIdAndAppId(String corpId, String appId);
}
