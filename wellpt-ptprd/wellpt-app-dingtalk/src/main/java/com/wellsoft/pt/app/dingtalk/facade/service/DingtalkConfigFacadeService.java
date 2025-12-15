/*
 * @(#)4/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.app.dingtalk.dto.DingtalkConfigDto;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkConfigVo;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/16/25.1	    zhulh		4/16/25		    Create
 * </pre>
 * @date 4/16/25
 */
public interface DingtalkConfigFacadeService extends Facade {
    /**
     * @param system
     * @return
     */
    DingtalkConfigDto getDtoBySystem(String system);

    /**
     * @param dingtalkConfigDto
     */
    Long saveDto(DingtalkConfigDto dingtalkConfigDto);

    /**
     * @param clientId
     * @param clientSecret
     * @param baseUrl
     */
    void testCreateToken(String clientId, String clientSecret, String baseUrl);

    DingtalkConfigVo getVoByUuid(Long uuid);

    DingtalkConfigVo getVoBySystemAndTenant(String system, String tenant);
    
}
