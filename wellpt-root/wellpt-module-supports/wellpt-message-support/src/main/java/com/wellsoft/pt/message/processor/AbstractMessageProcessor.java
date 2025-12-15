/*
 * @(#)2013-5-10 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.processor;

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
 * 2013-5-10.1	zhulh		2013-5-10		Create
 * </pre>
 * @date 2013-5-10
 */
public abstract class AbstractMessageProcessor implements MessageProcessor {
    protected Logger logger = LoggerFactory.getLogger(getClass());
}
