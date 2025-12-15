/*
 * @(#)5/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.app.weixin.dto.WeixinConfigDto;
import com.wellsoft.pt.app.weixin.vo.WeixinConfigVo;

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
public interface WeixinConfigFacadeService extends Facade {
    WeixinConfigDto getDtoBySystem(String system);

    Long saveDto(WeixinConfigDto weixinConfigDto);

    void testCreateToken(String corpId, String corpSecret);

    WeixinConfigVo getVoByUuid(Long uuid);

    WeixinConfigVo getVoBySystem(String system);
}
