/*
 * @(#)2013-12-9 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bm.facade;

import com.wellsoft.pt.integration.response.Response;

import javax.jws.WebService;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-9.1	zhulh		2013-12-9		Create
 * </pre>
 * @date 2013-12-9
 */
@WebService
public interface SelfPublicityAsyncWebService {

    /**
     * 自主公示UUID
     *
     * @param uuid
     */
    public Response asyncAttach(String uuid);

}
