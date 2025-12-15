/*
 * @(#)2015-8-13 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.core.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.cg.core.Context;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-8-13.1	zhulh		2015-8-13		Create
 * </pre>
 * @date 2015-8-13
 */
public interface ResourceGeneratorService extends BaseService {

    void generate(Context context, String tableName);

}
