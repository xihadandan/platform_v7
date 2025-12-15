/*
 * @(#)4/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.facade.service;

import com.wellsoft.pt.app.dingtalk.entity.DingtalkConfigEntity;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/21/25.1	    zhulh		4/21/25		    Create
 * </pre>
 * @date 4/21/25
 */
public interface DingtalkEventCallbackFacadeService {
    void updateWsClient(DingtalkConfigEntity entity);
}
