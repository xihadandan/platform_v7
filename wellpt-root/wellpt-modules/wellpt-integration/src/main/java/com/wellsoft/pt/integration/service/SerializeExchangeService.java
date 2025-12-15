/*
 * @(#)2014-7-27 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.integration.request.FilesRequest;
import com.wellsoft.pt.integration.response.Response;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-9-21.1	Administrator		2016-9-21		Create
 * </pre>
 * @date 2016-9-21
 */
public interface SerializeExchangeService extends BaseService {

    public Response onSend(String typeId, String unitId, String serializeData, String para, FilesRequest streamingDatas)
            throws Exception;

    public Response onReceive(String typeId, String unitId, String serializeData, String para,
                              FilesRequest streamingDatas) throws Exception;

}
