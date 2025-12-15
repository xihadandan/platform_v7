/*
 * @(#)2015-6-23 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.service;

import com.wellsoft.context.service.BaseService;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-23.1	zhulh		2015-6-23		Create
 * </pre>
 * @date 2015-6-23
 */
public interface RuleEngineService extends BaseService {

    boolean validate(String rule);

}
