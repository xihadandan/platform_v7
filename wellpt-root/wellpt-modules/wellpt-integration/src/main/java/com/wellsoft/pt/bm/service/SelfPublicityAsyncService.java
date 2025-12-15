/*
 * @(#)2013-12-9 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bm.service;

import com.wellsoft.context.service.BaseService;

/**
 * Description: 自主公示附件
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
public interface SelfPublicityAsyncService extends BaseService {

    public void asyncAttach(String uuid) throws Exception;

}
