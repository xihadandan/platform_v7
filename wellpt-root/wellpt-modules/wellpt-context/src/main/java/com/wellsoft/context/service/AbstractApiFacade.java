/*
 * @(#)2013-1-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-27.1	zhulh		2013-1-27		Create
 * </pre>
 * @date 2013-1-27
 */
public abstract class AbstractApiFacade implements Facade {
    protected Logger logger = LoggerFactory.getLogger(getClass());
}
